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


#ifndef TYPE_COMPILER_H
#define TYPE_COMPILER_H

#include "default_visitor.h"

#include <QtCore/QString>
#include <QtCore/QStringList>
#include <QtCore/QList>

class TokenStream;
class Binder;

class TypeCompiler: protected DefaultVisitor
{
public:
  TypeCompiler(Binder *binder);

  inline QStringList qualifiedName() const { return _M_type; }
  inline QList<int> cv() const { return _M_cv; }

  bool isConstant() const;
  bool isVolatile() const;

  QStringList cvString() const;

  void run(TypeSpecifierAST *node);

protected:
  virtual void visitClassSpecifier(ClassSpecifierAST *node);
  virtual void visitEnumSpecifier(EnumSpecifierAST *node);
  virtual void visitElaboratedTypeSpecifier(ElaboratedTypeSpecifierAST *node);
  virtual void visitSimpleTypeSpecifier(SimpleTypeSpecifierAST *node);

  virtual void visitName(NameAST *node);

private:
  Binder *_M_binder;
  TokenStream *_M_token_stream;
  QStringList _M_type;
  QList<int> _M_cv;
};

#endif // TYPE_COMPILER_H

// kate: space-indent on; indent-width 2; replace-tabs on;
