/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
** Copyright (C) 2002-2005 Roberto Raggi <roberto@kdevelop.org>
**
** This file is part of $PRODUCT$.
**
** $CPP_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/


#ifndef NAME_COMPILER_H
#define NAME_COMPILER_H

#include "default_visitor.h"
#include <QtCore/QStringList>

class TokenStream;
class Binder;

class NameCompiler: protected DefaultVisitor
{
public:
  NameCompiler(Binder *binder);

  void run(NameAST *node) { internal_run(node); }
  void run(UnqualifiedNameAST *node) { internal_run(node); }

  QString name() const { return _M_name.join("::"); }
  QStringList qualifiedName() const { return _M_name; }

protected:
  virtual void visitUnqualifiedName(UnqualifiedNameAST *node);
  virtual void visitTemplateArgument(TemplateArgumentAST *node);

  QString internal_run(AST *node);
  QString decode_operator(std::size_t index) const;

private:
  Binder *_M_binder;
  TokenStream *_M_token_stream;
  QStringList _M_name;
};

#endif // NAME_COMPILER_H

// kate: space-indent on; indent-width 2; replace-tabs on;
