#include "qtreemodel.h"

#include <QtCore/QVector>
#include <QtCore/QStack>
#include <QtGui/QIcon>

#include <qtjambi_core.h>

class Node {
public:
    enum State {
        ChildCountQueried =  0x0001,
        ChildrenQueried =    0x0002,
    };

    Node() : parent(0), value(0), state(0) { }
    ~Node() {
        if (nodes.size()) {
            JNIEnv *env = qtjambi_current_environment();
            release(env);
        }
    }

    void release(JNIEnv *env) {
        // Release myself
        env->DeleteGlobalRef(value);
        QTJAMBI_EXCEPTION_CHECK(env);

        // All my children
        for (int i=0; i<nodes.size(); ++i) {
            Node *n = nodes.at(i);
            if (n) {
                n->release(env);
                delete n;
            }
        }

        // The free memory..
        nodes = QVector<Node *>();

        state = 0;
    }

    void setState(State s) { state |= s; }
    void clearState(State s) { state &= ~s; }
    bool checkState(State s) const { return state & s; }

    bool isChildCountQueried() const { return checkState(ChildCountQueried); }
    bool isChildrenQueried() const { return checkState(ChildrenQueried); }

    Node *parent;
    QVector<Node *> nodes;
    jobject value;

    uint state;
};

/*!
    \class QTreeModel

    \brief The QTreeModel class provides a convenience base class for
    hierarchical item models in the Qt Itemview Framework.

    \ingroup model-view

    QTreeModel extends the more generic QAbstractItemModel class with
    an API that is both simpler and more suitable for Java. The API
    differs from the rest of the Qt Itemviews API in that it is
    primarily using node objects directly rather than QModelIndex
    objects. Note, for example, that the related selection and filter
    models use QModelIndex objects. QTreeModel provides the
    indexToValue() function to translate the model indexes into node
    objects. QTreeModel also encapsulates and hides the parent()
    concept of QAbstractItemModel.

    When deriving from the QTreeModel class, make sure that the pure
    virtual childCount(), child() and text() functions are implemented.

    Using the standard QTreeView class to view the content's of the
    model, the text is rendered using QTreeView's fonts. This behavior
    can be altered by reimplementing QTreeModel's data() function,
    providing access to all the various item roles (i.e., background
    color, font, size hint, etc.). Reimplement the icon() function to
    get graphical nodes. It is also possible to implement a custom
    QItemDelegate class for the view and reimplement the
    QItemDelegate.paint() function.

    Internally, the QTreeModel class caches the datastructures of
    nodes that have been expanded. Call the releaseChildren() function
    to release parts of this memory pool when they are no longer
    needed, for example when the view collapses a subtree:

    \code
        view.collapsed.connect(model, "releaseChildren(QModelIndex)");
    \endcode

    Note that if the model is shared between multiple views, calling
    the model's releaseChildren() function affects all.
*/

QTreeModel::QTreeModel(QObject *parent)
    : QAbstractItemModel(parent),
      m_root(new Node())
{
    connect(this, SIGNAL(modelReset()), this, SLOT(wasReset()));
    connect(this, SIGNAL(layoutChanged()), this, SLOT(wasChanged()));
    m_invalidation = false;
}

Node *QTreeModel::node(const QModelIndex &index) const
{
    Node *n = index.isValid() && index.internalPointer() != 0
              ? (Node *) index.internalPointer()
              : (Node *) m_root;
    if (!n->isChildCountQueried())
        initializeNode(n, index);
    return n;
}


Node *QTreeModel::node(jobject object) const
{
    JNIEnv *env = qtjambi_current_environment();
    StaticCache *sc = StaticCache::instance(env);
    sc->resolveObject();

    JObject_key key = {
        object,
        env->CallIntMethod(object, sc->Object.hashCode)
    };

    return m_nodes.value(key, 0);
}

/*!
    \internal
*/
int QTreeModel::rowCount(const QModelIndex &parent) const
{
    return node(parent)->nodes.size();
}


/*!
    \internal
*/

int QTreeModel::columnCount(const QModelIndex &) const
{
    return 1;
}


/*!
    \internal
*/

QModelIndex QTreeModel::index(int row, int, const QModelIndex &parent) const
{
    Node *parentNode = node(parent);
    QTJAMBI_EXCEPTION_CHECK(qtjambi_current_environment());
    if (!parentNode->isChildrenQueried())
        queryChildren(parentNode);
    QTJAMBI_EXCEPTION_CHECK(qtjambi_current_environment());
    Q_ASSERT_X(row < parentNode->nodes.size(),
               "QTreeModel::index()",
               qPrintable(QString::fromLatin1("index %1 of %2, node=%3")
                          .arg(row)
                          .arg(parentNode->nodes.size())
                          .arg(text(parentNode->value))));
    Q_ASSERT_X(parentNode->nodes.at(row),
               "QTreeModel::indeX()",
               "all child nodes must be queried ahead of time...");

    return createIndex(row, 0, parentNode->nodes.at(row));
}


/*!
    \internal
*/
QModelIndex QTreeModel::parent(const QModelIndex &index) const
{
    Node *child = node(index);
    Node *parent = child->parent;

    if (parent == m_root)
        return QModelIndex();

    Node *grandParent = parent->parent;

    for (int i=0; i<grandParent->nodes.size(); ++i)
        if (grandParent->nodes.at(i) == parent)
            return createIndex(i, 0, parent);

    return QModelIndex();
}


/*!
    \internal
*/
QVariant QTreeModel::data(const QModelIndex &index, int role) const
{
    return data(node(index)->value, role);
}


/*!
    Translates the given index to a value node and returns the node.
*/
jobject QTreeModel::indexToValue(const QModelIndex &index) const
{
    return node(index)->value;
}


/*!
    Returns the model index for \a object.
*/
QModelIndex QTreeModel::valueToIndex(jobject object) const
{
    Node *n = node(object);
    if (n) {
        Node *parent = n->parent;
        for (int i=0; i<parent->nodes.size(); ++i) {
            if (parent->nodes.at(i) == n)
                return createIndex(i, 0, n);
        }
    }
    return QModelIndex();
}


/*!
    \internal
*/
void QTreeModel::wasReset()
{
    delete m_root;
    m_root = new Node();
}


/*!
    \internal
*/
void QTreeModel::wasChanged()
{
    QStack<Node*> stk;
    if (m_root)
        stk.push(m_root);
    while (!stk.isEmpty()) {
        Node *node = stk.pop();
        node->state = 0;
        for (int i = 0; i < node->nodes.count(); ++i){
            if (Node *tmp = node->nodes.at(i))
                stk.push(tmp);
        }
    }
}



/*!
    \fn void QTreeModel::childrenRemoved(const QModelIndex &parent, int first, int last)

    Removes the nodes from \a first to \a last from the given \a
    parent, including the node specified by \a last. Note that this
    function must be called when nodes have been removed from the
    model.

    \sa beginDeleteRows()
*/
void QTreeModel::childrenRemoved(const QModelIndex &parentIndex, int first, int last)
{
    Node *n = node(parentIndex);

    if (first < 0 || last >= n->nodes.size() || first > last) {
        printf("QTreeModel::childrenRemoved(), bad input, first=%d, last=%d, childCount=%d\n",
               first, last, n->nodes.size());
        return;
    }

    beginRemoveRows(parentIndex, first, last);
    for (int i=first; i<=last; ++i)
        delete n->nodes.at(i);
    n->nodes.remove(first, last - first + 1);
    endRemoveRows();
}

/*!
    \fn void QTreeModel::childrenInserted(const QModelIndex &parent, int first, int last)

    Inserts \a first - \a last + 1 nodes into the given \a parent,
    before the node specified by \a first. Note that this function
    must be called when nodes have been inserted into the model.

    \sa beginInsertRows()
*/

void QTreeModel::childrenInserted(const QModelIndex &parentIndex, int first, int last)
{
    Node *parentNode = node(parentIndex);

    if (first < 0 || first > last) {
        printf("QTreeModel::childrenInserted(), bad input, first=%d, last=%d, childCount=%d\n",
               first, last, parentNode->nodes.size());
        return;
    }

    int increase = last - first + 1;
    int oldSize = parentNode->nodes.size();

    int newSize = childCount(parentNode->value);
    if (increase != newSize - oldSize) {
        printf("QTreeModel::childrenInserted(), inconsistent childCount=%d vs oldCount=%d,"
               " first=%d, last=%d\n",
               newSize, oldSize, first, last);
        return;
    }

    beginInsertRows(parentIndex, first, last);
    parentNode->nodes.insert(first, increase, 0);

    // Need to keep the stuff in sync...
    if (parentNode->isChildrenQueried())
        queryChildren(parentNode, first, increase);

    endInsertRows();
}

/*!
    \internal
*/
void QTreeModel::initializeNode(Node *node, const QModelIndex &) const
{
    Q_ASSERT(!node->isChildCountQueried());
    int count = childCount(node->value);
    QTJAMBI_EXCEPTION_CHECK(qtjambi_current_environment());
    node->nodes.resize(count);
    node->setState(Node::ChildCountQueried);
}

/*!
    \internal
*/

void QTreeModel::queryChildren(Node *parentNode, int start, int length) const
{
    if (start < 0) start = 0;
    if (length < 0) length = parentNode->nodes.size();

    Q_ASSERT(start + length <= parentNode->nodes.size());

    JNIEnv *env = qtjambi_current_environment();
    StaticCache *sc = StaticCache::instance(env);
    sc->resolveObject();


    int top = start + length;

    for (int i=start; i<top; ++i) {
        Node *childNode = new Node();

        childNode->value = child(parentNode->value, i);
        QTJAMBI_EXCEPTION_CHECK(env);

        childNode->parent = parentNode;
        parentNode->nodes[i] = childNode;

        JObject_key key = {
            childNode->value,
            env->CallIntMethod(childNode->value, sc->Object.hashCode)
        };

        const_cast<QTreeModel *>(this)->m_nodes.insert(key, childNode);
    }

    parentNode->setState(Node::ChildrenQueried);
}

/*!
    Releases datastructures that are no longer needed, from the memory
    pool.

    Internally, the QTreeModel class caches the datastructures of
    nodes that have been expanded. Call the releaseChildren() function
    to release parts of this memory pool when they are no longer
    needed, for example when the view collapses a subtree.

    Note that if the model is shared between multiple views, calling
    the model's releaseChildren() function affects all.
*/
void QTreeModel::releaseChildren(const QModelIndex &index)
{
    Node *n = node(index);
    JNIEnv *env = qtjambi_current_environment();
    StaticCache *sc = StaticCache::instance(env);
    sc->resolveObject();

    int count = n->nodes.size();

    // Need to emit the proper signals here so that the QTreeView will
    // update its cache properly.
    beginRemoveRows(index, 0, count-1);
    for (int i=0; i<count; ++i) {
        Node *childNode = n->nodes.at(i);
        if (childNode) {
            JObject_key key = {
                childNode->value,
                env->CallIntMethod(childNode->value, sc->Object.hashCode)
            };
            m_nodes.remove(key);
            childNode->release(env);
            delete childNode;
            n->nodes.replace(i, 0);
        }
    }
    endRemoveRows();

    // Reinsert the rows we "took" out
    beginInsertRows(index, 0, count-1);
    endInsertRows();

    n->clearState(Node::ChildrenQueried);
}


/*!
    Using the standard QTreeView class to view the content's of the
    model, the text is rendered using QTreeView's fonts. Reimplement
    this function to alter the view's rendering behavior.

    The default implementation is calling text() and icon() function
    for the given \a value and \a role.
*/
QVariant QTreeModel::data(jobject value, int role) const
{
    switch (role) {
    case Qt::DisplayRole: return text(value);
    case Qt::DecorationRole: return icon(value);
    default:
        return QVariant();
    }
}

/*!
    Reimplement this function to get graphical nodes. The default
    implementation returns an empty icon.
*/
QIcon QTreeModel::icon(jobject) const
{
    return QIcon();
}

/*!
    \fn int QTreeModel::childCount(jobject node) const = 0

    Returns the given \a node's number of children, or 0 if the \a
    node is a leaf node.
*/

/*!
    \fn jobject QTreeModel::child(jobject node, int index) const = 0

    Returns the child specified by \a index, of the given \a node.
*/

/*!
    \fn virtual QString text(jobject value) const = 0

    Returns a string representation of the given \a value.
*/


bool operator==(const JObject_key &a, const JObject_key &b)
{
    if (a.hashCode == b.hashCode) {
        JNIEnv *env = qtjambi_current_environment();
        return env->IsSameObject(a.obj, b.obj);
    }
    return false;
}

uint qHash(const JObject_key &key) {
    return key.hashCode;
}
