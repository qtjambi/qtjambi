
#include <QtCore/QtCore>

#include "control.h"
#include "lexer.h"
#include "tokens.h"
#include "parser.h"
#include "default_visitor.h"

///////////////////////////////////////////////////////////////////////////////
// Node
///////////////////////////////////////////////////////////////////////////////
class Node
{
public:
  inline Node () {}

  inline Node (const QString &label):
    _M_label (label), _M_defined (false) {}

  inline QString label () const
  { return _M_label; }

  inline bool defined () const
  { return _M_defined; }

  inline void setDefined (bool defined)
  { _M_defined = defined; }

  inline QList<Node*> baseNodes () const
  { return _M_base_nodes; }

  inline void addBaseLink (Node *node)
  { _M_base_nodes.append (node); }

  inline void addForwardLink (Node *node)
  {
    Q_ASSERT (node != 0);

    _M_forward_edges.insert (node);
    node->_M_backward_edges.insert (this);
  }

  inline void addBackwardLink (Node *node)
  { node->addForwardLink (this); }

  inline QSet<Node*> forwardEdges () const
  { return _M_forward_edges; }

  inline QSet<Node*> backwardEdges () const
  { return _M_backward_edges; }

private:
  QString _M_label;
  bool _M_defined;
  QList<Node*> _M_base_nodes;
  QSet<Node*> _M_forward_edges;
  QSet<Node*> _M_backward_edges;
};



///////////////////////////////////////////////////////////////////////////////
// NodePool
///////////////////////////////////////////////////////////////////////////////
class NodePool
{
public:
  NodePool () {}

  inline Node *get (const QString &label)
  {
    if (Node *node = _M_node_map.value (label))
      return node;

    Q_ASSERT (! label.isEmpty ());

    Node *node = new Node (label);
    _M_node_map.insert (label, node);

    return node;
  }

  inline const QMap<QString, Node *> &nodes () const
  { return _M_node_map; }

private:
  QMap<QString, Node *> _M_node_map;
};


///////////////////////////////////////////////////////////////////////////////
// TextOf
///////////////////////////////////////////////////////////////////////////////
class TextOf
{
public:
  inline TextOf (TokenStream *token_stream):
    _M_token_stream (token_stream) {}

  inline QString operator () (AST *node) const
  {
    if (! node)
      return QString ();

    QByteArray text;
    text.reserve (256);

    for (std::size_t index = node->start_token; index < node->end_token; ++index)
      {
        const Token &tk = _M_token_stream->token (index);
        const char *word = &tk.text [tk.position];

        if (index != node->start_token)
          text += ' ';

        text += QByteArray (word, tk.size);
      }

    int index = text.indexOf ('<');
    if (index != -1)
      text = text.left (text.indexOf ('<'));

    text = text.trimmed ();

    return QString::fromUtf8 (text);
  }

private:
  TokenStream *_M_token_stream;
};

///////////////////////////////////////////////////////////////////////////////
// XmlForEttrichVisitor
///////////////////////////////////////////////////////////////////////////////
class XmlForEttrichVisitor:
    protected DefaultVisitor
{
public: // functors
  TextOf textOf;

public:
  XmlForEttrichVisitor (TokenStream *token_stream);

  QMap<QString, Node*> operator () (AST *node);

protected:
  virtual void visitSimpleDeclaration (SimpleDeclarationAST *ast);
  virtual void visitNamespace (NamespaceAST *ast);
  virtual void visitClassSpecifier (ClassSpecifierAST *ast);
  virtual void visitEnumSpecifier (EnumSpecifierAST *ast);
  virtual void visitElaboratedTypeSpecifier (ElaboratedTypeSpecifierAST *ast);
  virtual void visitSimpleTypeSpecifier (SimpleTypeSpecifierAST *ast);
  virtual void visitFunctionDefinition (FunctionDefinitionAST *ast);
  virtual void visitBaseClause (BaseClauseAST *ast);
  virtual void visitBaseSpecifier (BaseSpecifierAST *ast);
  virtual void visitAccessSpecifier(AccessSpecifierAST *ast);

  bool isFriendDeclaration (const ListNode<std::size_t> *storage_specifiers);

  inline Node *changeActiveNode (Node *node)
  {
    Node *was = _M_active_node;
    _M_active_node = node;
    return was;
  }

  inline std::size_t changeCurrentAcces (std::size_t access)
  {
    std::size_t was = _M_current_access;
    _M_current_access = access;
    return was;
  }

private:
  TokenStream *_M_token_stream;
  Node *_M_active_node;
  std::size_t _M_current_access;
  NodePool _M_node_pool;
};




///////////////////////////////////////////////////////////////////////////////
// XmlForEttrichVisitor -- bits
///////////////////////////////////////////////////////////////////////////////
XmlForEttrichVisitor::XmlForEttrichVisitor (TokenStream *token_stream):
  textOf (token_stream),
  _M_token_stream (token_stream),
  _M_active_node (0),
  _M_current_access (0)
{
}

QMap<QString, Node*>  XmlForEttrichVisitor::operator () (AST *ast)
{
  visit (ast);

  return _M_node_pool.nodes ();
}

bool XmlForEttrichVisitor::isFriendDeclaration (const ListNode<std::size_t> *storage_specifiers)
{
  if (const ListNode<std::size_t> *it = storage_specifiers)
    {
      it = it->toFront();
      const ListNode<std::size_t> *end = it;
      do
        {
          if (it->element == Token_friend)
            return true; // ignore the declaration

          it = it->next;
        }
      while (it != end);
    }

  return false;
}

void XmlForEttrichVisitor::visitSimpleDeclaration (SimpleDeclarationAST *ast)
{
  if (isFriendDeclaration (ast->storage_specifiers) || _M_current_access == Token_private)
    return;

  DefaultVisitor::visitSimpleDeclaration (ast);
}

void XmlForEttrichVisitor::visitAccessSpecifier(AccessSpecifierAST *ast)
{
  if (! ast->specs)
    return;

  _M_current_access = _M_token_stream->token (ast->specs->toFront ()->element).kind;
}

void XmlForEttrichVisitor::visitNamespace (NamespaceAST *ast)
{
  if (ast->namespace_name == 0) // anonymous namespaces.. we don't care!
    return;

  DefaultVisitor::visitNamespace (ast);
}

inline bool invalidName (const QString &name)
{
  return name.isEmpty () || name.contains ("::");
}


void XmlForEttrichVisitor::visitClassSpecifier (ClassSpecifierAST *ast)
{
  QString name = textOf (ast->name);

  if (invalidName (name) || name.at (0) != QLatin1Char ('Q') || _M_current_access == Token_private)
    return;

  if (_M_active_node) // skip nested classes
    return;

  Node *was = changeActiveNode (_M_node_pool.get (name));

  std::size_t access;

  switch (_M_token_stream->token (ast->class_key).kind)
    {
      case Token_class:
        access = changeCurrentAcces (Token_private);
        break;

      case Token_struct:
      case Token_union:
        access = changeCurrentAcces (Token_public);
        break;

      default:
        Q_ASSERT (0);
    }

  _M_active_node->setDefined (true);

  if (was)
    was->addForwardLink (_M_active_node);

  DefaultVisitor::visitClassSpecifier (ast);

  changeActiveNode (was);
  changeCurrentAcces (access);
}

void XmlForEttrichVisitor::visitEnumSpecifier (EnumSpecifierAST *)
{
  return; // skip enumerators
}

void XmlForEttrichVisitor::visitElaboratedTypeSpecifier (ElaboratedTypeSpecifierAST *ast)
{
  visit (ast->name);

  QString name = textOf (ast->name);

  if (! _M_active_node || invalidName (name) || name.at (0) != QLatin1Char ('Q') || _M_current_access == Token_private)
    return;

  _M_active_node->addForwardLink (_M_node_pool.get (name));
}

void XmlForEttrichVisitor::visitSimpleTypeSpecifier (SimpleTypeSpecifierAST *ast)
{
  visit (ast->name);

  QString name = textOf (ast->name);

  if (! _M_active_node || invalidName (name) || name.at (0) != QLatin1Char ('Q') || _M_current_access == Token_private)
    return;

  _M_active_node->addForwardLink (_M_node_pool.get (name));
}

void XmlForEttrichVisitor::visitFunctionDefinition (FunctionDefinitionAST *ast)
{
  if (! _M_active_node || _M_current_access == Token_private) // global function... we don't care!
    return;

  if (isFriendDeclaration (ast->storage_specifiers))
    return;

  if (ast->type_specifier)
    visit (ast->type_specifier);

  if (ast->init_declarator)
    visit (ast->init_declarator);
}

void XmlForEttrichVisitor::visitBaseClause(BaseClauseAST *ast)
{
  DefaultVisitor::visitBaseClause (ast);
}

void XmlForEttrichVisitor::visitBaseSpecifier (BaseSpecifierAST *ast)
{
  Q_ASSERT (_M_active_node != 0);

  QString name = textOf (ast->name);

  if (invalidName (name) || name.at (0) != QLatin1Char ('Q'))
    return;

  _M_active_node->addBaseLink (_M_node_pool.get (name));

  DefaultVisitor::visitBaseSpecifier (ast);
}


///////////////////////////////////////////////////////////////////////////////
// ENTRY POINT
///////////////////////////////////////////////////////////////////////////////
int main (int, char *argv[])
{
  const char *filename = argv [1];

  QFile file (filename);

  if (! file.open (QFile::ReadOnly))
    return 1;

  QByteArray contents = file.readAll ();
  file.close();

  Control control;
  Parser parser (&control);
  pool __pool;

  TranslationUnitAST *ast = parser.parse (contents, contents.size (), &__pool);
  Q_ASSERT (ast != 0);

  XmlForEttrichVisitor xmlOf (&parser.token_stream);

  QMap<QString, Node*> nodes = xmlOf (ast);

  printf ("<classgraph>\n");

  foreach (Node *node, nodes)
    {
      if (! node->defined ())
        continue;

      printf ("  <class name=\"%s\" />\n", qPrintable (node->label ()));
    }

  foreach (Node *node, nodes)
    {
      if (! node->defined ())
        continue;

      foreach (Node *base, node->baseNodes ())
        {
          if (! base->defined ())
            continue;

          printf ("  <inherits name=\"%s\" base=\"%s\"/>\n",
              qPrintable (node->label ()),
              qPrintable (base->label ()));
        }
    }

  foreach (Node *source, nodes)
    {
      if (! source->defined ())
        continue;

      foreach (Node *target, source->forwardEdges ())
        {
          Q_ASSERT (! target->label ().isEmpty ());

          if (! target->defined ())
            continue;

          printf ("  <link source=\"%s\" target=\"%s\" />\n",
              qPrintable (source->label ()),
              qPrintable (target->label ()));
        }
    }

  printf ("</classgraph>\n");
}

// kate: indent-width 2;








