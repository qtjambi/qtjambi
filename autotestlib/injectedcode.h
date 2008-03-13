/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $CPP_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#ifndef INJECTEDCODE_H
#define INJECTEDCODE_H

#include <QtCore/QtCore>
#include <QtGui/QtGui>
#include <QtSql/QtSql>
#include <QtXml/QtXml>
#include <QtNetwork/QtNetwork>

class SpinBoxHandler
{
public:
    void tryValidate(QAbstractSpinBox *spinBox, const QString &input, int i)
    {
        my_returned_string = input;
        my_returned_pos = i;
        my_returned_state = spinBox->validate(my_returned_string, my_returned_pos);
    }

    void tryFixup(QAbstractSpinBox *spinBox, const QString &input)
    {
        my_returned_string = input;
        spinBox->fixup(my_returned_string);
    }

public:
    QString my_returned_string;
    int my_returned_pos;
    QValidator::State my_returned_state;
};

class SpinBoxSubclass: public QAbstractSpinBox
{
public:
    virtual QValidator::State validate(QString &input, int &pos) const
    {
        this->my_received_pos = pos;
        this->my_received_string = input;
        pos += 13;
        input = "The silence of that " + input.left(5) + "less sleep";

        return QValidator::Intermediate;
    }

    virtual void fixup(QString &input) const
    {
        this->my_received_string = input;
        input = "And " + input.left(4) + " art dead";
    }

public:
    mutable QString my_received_string;
    mutable int my_received_pos;
};

class GraphicsSceneSubclass: public QGraphicsScene
{
public:
    void drawItems(QPainter *painter, int numItems, QGraphicsItem *items[], const QStyleOptionGraphicsItem options[], QWidget *widget);

     int numItems;
     QStyleOptionGraphicsItem firstStyleOption;
     QStyleOptionGraphicsItem secondStyleOption;
     QGraphicsItem *firstItem;
     QGraphicsItem *secondItem;
     int firstStyleOptionType;
     int secondStyleOptionType;
     int firstStyleOptionVersion;
     int secondStyleOptionVersion;
     QRectF firstBoundingRect;
     QRectF secondBoundingRect;
};

class TextCodecSubclass: public QTextCodec
{


public:
    QString callToUnicode(const QByteArray &ba, ConverterState *state) {
        return convertToUnicode(ba.data(), ba.size(), state);
    }

    QByteArray callFromUnicode(const QString &str, ConverterState *state) {
        return convertFromUnicode(str.data(), str.length(), state);
    }

    mutable ConverterState *receivedState;

protected:
    QString convertToUnicode(const char *data, int size, ConverterState *state) const {
        receivedState = state;

        QString returned;
        for (int i=0; i<size; ++i)
            returned += (data[i] == 'a' ? QLatin1Char('b') : QLatin1Char('a'));

        return returned;
    }

    QByteArray convertFromUnicode(const QChar *data, int size, ConverterState *state) const {
        receivedState = state;

        QByteArray returned;
        for (int i=0; i<size; ++i)
            returned += (data[i] == 's' ? 'a' : 's');

        return returned;
    }
};

class IODeviceSubclass: public QIODevice
{
public:
    IODeviceSubclass(int buffer_length) {
        buffer = new char[buffer_length];
        this->buffer_length = buffer_length;
    }

    qint64 callReadData() {
        return readData(buffer, buffer_length);
    }

    qint64 callWriteData(const QByteArray &data) {
        return writeData(data.data(), data.size());
    }

    qint64 callReadLineData() {
        return readLineData(buffer, buffer_length);
    }

protected:
    qint64 readData(char *data, qint64 maxSize) {
        Q_UNUSED(maxSize);
        char *inp = "I am a boy";
        for (int i=0; i<int(strlen(inp)); ++i)
            data[i] = inp[i];

        return strlen(inp);
    }

    qint64 writeData(const char *data, qint64 maxSize) {
        delete buffer;
        buffer = new char[maxSize];
        for (int i=0; i<maxSize; ++i)
            buffer[i] = data[i];

        return maxSize;
    }

    qint64 readLineData(char *data, qint64 maxSize) {
        Q_UNUSED(maxSize)
        char *inp = "Confucius say: Don't go outside with wet hair";
        for (int i=0; i<int(strlen(inp)); ++i)
            data[i] = inp[i];

        return strlen(inp);
    }

public:
    char *buffer;
    int buffer_length;

};

class PictureSubclass: public QPicture
{
public:
    void callSetData(const QByteArray &byteArray) {
        setData(byteArray.data(), byteArray.size());
    }
};

class GraphicsItemSubclass: public QGraphicsItem
{
public:
    void callPaint(QPainter *painter, const QStyleOptionGraphicsItem &option, QWidget *w)
    {
        paint(painter, &option, w);
    }

    void paint(QPainter *painter, const QStyleOptionGraphicsItem *option, QWidget *widget) {
        this->widget = widget;
        this->painter = painter;
        this->option = *option;

        painter->fillRect(0, 0, 50, 50, Qt::red);
    }

    QPainter *painter;
    QStyleOptionGraphicsItem option;
    QWidget *widget;
};

class AccessibleInterfaceSubclass: public QAccessibleInterface
{
public:
    int callNavigate(RelationFlag relation, int entry) {
        return navigate(relation, entry, &this->target);
    }

    virtual int navigate(RelationFlag relation, int entry, QAccessibleInterface **target) const {
        if (relation == Self) {
            *target = const_cast<AccessibleInterfaceSubclass *>(this);
            return entry;
        } else {
            *target = 0;
            return -1;
        }
    }

    QAccessibleInterface *target;
};

class SomeQObject: public QObject
{
    Q_OBJECT
public slots:
    void getAnUrl(const QUrl &url) {
        this->url = url;
    }

    void actionTriggered() {
        emit myActionTriggered();
    }

signals:
    void myActionTriggered();

public:
    QUrl url;
};

class ValidatorSubclass: public QValidator {
public:
    ValidatorSubclass() : QValidator(0) {}

    QString callFixup(const QString &input) {
        QString blah = input;
        fixup(blah);
        return blah;
    }

    State callValidate(QString *input, int *pos) {
        return validate(*input, *pos);
    }

    void fixup(QString &input) const {
        inputString = input;

        input = "somePrefix" + input;
    }

    State validate(QString &input, int &pos) const {
        inputString = input;
        inputPos = pos;

        input += "somePostfix";
        pos = inputString.length();

        if (inputString == "intermediate")
            return Intermediate;
        else if (inputString == "acceptable")
            return Acceptable;
        else
            return Invalid;
    }

    mutable QString inputString;
    mutable int inputPos;
};

class ImageIOHandlerSubclass: public QImageIOHandler
{
public:
    bool callRead(QImage *image) {
        return read(image);
    }

    bool read(QImage *image) {
        return image != 0 ? image->load("classpath:com/trolltech/examples/images/cheese.png") : true;
    }
};

class SqlTableModelSubclass: public QSqlTableModel
{
    Q_OBJECT
public:
    void emitBeforeInsert() {
        emit beforeInsert(myRecord);
    }

    void connectBeforeInsert() {
        connect(this, SIGNAL(beforeInsert(QSqlRecord &)), this, SLOT(receiveBeforeInsert(QSqlRecord &)));
    }

    QSqlRecord myRecord;

public slots:
    void receiveBeforeInsert(QSqlRecord &rec) {
        rec.append(QSqlField("cppInt", QVariant::Int));
        rec.setValue("cppInt", 1234);
    }

};

class XmlReaderSubclass: public QXmlReader
{
public:
    bool callFeature(const QString &name) {
        return feature(name, &myOk);
    }

    bool feature(const QString &name, bool *) const
    {
        myName = name;
        return (name == "true");
    }

    bool myOk;
    mutable QString myName;
};

class AccessibleTableInterfaceSubclass: public QAccessibleTableInterface
{
public:
    virtual int selectedColumns(int maxColumns, QList<int> *columns);
    virtual int selectedRows(int maxRows, QList<int> *rows);
    virtual void cellAtIndex(int index, int *row, int *column, int *rowSpan,
                             int *columnSpan, bool *isSelected);

    static void callCellAtIndex(AccessibleTableInterfaceSubclass *obj, int index, int *row, int *col, int *rowSpan, int *columnSpan, bool *isSelected);
    static QList<int> callSelectedRows(AccessibleTableInterfaceSubclass *obj, int maxRows, QList<int> rows);
    static QList<int> callSelectedColumns(AccessibleTableInterfaceSubclass *obj, int maxColumns, QList<int> columns);
};

class AbstractSocketSubclass: public QAbstractSocket
{
    Q_OBJECT
public:
    AbstractSocketSubclass(QAbstractSocket::SocketType socketType, QObject *parent) : QAbstractSocket(socketType, parent) { }

    void connectProxyAuthenticationRequired(QAbstractSocket *socket);
    void emitProxyAuthenticationRequired(QAbstractSocket *socket, const QNetworkProxy &proxy, QAuthenticator *authenticator);

    inline void emitSignalAccessor(const QNetworkProxy &proxy, QAuthenticator *authenticator)
    {
        proxyAuthenticationRequired(proxy, authenticator);
    }

private slots:
    void aSlot(const QNetworkProxy &proxy, QAuthenticator *authenticator);
};

class SenderQObject: public QObject 
{
    Q_OBJECT
public:
    void emitSignal() {
        emit mappedSignal("foo", 0xf00);
    }

    void connect(QSignalMapper *mapper) {
        QObject::connect(this, SIGNAL(mappedSignal(const QString &, int)), mapper, SLOT(map()));
    }

signals:
    void mappedSignal(const QString &, int);
};

class StyledItemDelegateSubclass: public QStyledItemDelegate
{
public:
    void initStyleOptionInStyledDelegate(QStyleOptionViewItem *styleOption) 
    {
        initStyleOption(styleOption, QModelIndex());
    }
};

class GraphicsWidgetSubclass: public QGraphicsWidget
{
protected:
    void initStyleOption(QStyleOption *option) const
    {
        if (QStyleOptionGroupBox *box = qstyleoption_cast<QStyleOptionGroupBox *>(option)) {
            box->midLineWidth = 123;
        }
    }

public:
    static int callInitStyleOption(GraphicsWidgetSubclass *widget) 
    {
        QStyleOptionGroupBox box;
        widget->initStyleOption(&box);

        return box.lineWidth + box.midLineWidth;
    }
};

class XmlEntityResolverSubclass: public QXmlEntityResolver 
{
public:
    bool resolveEntity(const QString &publicId, const QString &systemId, QXmlInputSource *&ret)
    {
        if (publicId == "c++") {
            ret = new QXmlInputSource;
            ret->setData(QString::fromLatin1("Made in C++"));
        } 

        return (systemId != "error");
    }

    QXmlInputSource *callResolveEntity(const QString &publicId, const QString &systemId) 
    {
        QXmlInputSource *ptr = 0;
        bool error = !resolveEntity(publicId, systemId, ptr);

        if (error && ptr != 0)
            ptr->setData(ptr->data() + QString::fromLatin1(" with error"));

        return ptr;
    }
};

#endif
