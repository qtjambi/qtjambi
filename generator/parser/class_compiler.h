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


#ifndef CLASS_COMPILER_H
#define CLASS_COMPILER_H

#include <QtCore/qglobal.h>
#include <QtCore/QStringList>

#include "default_visitor.h"
#include "name_compiler.h"
#include "type_compiler.h"

class TokenStream;
class Binder;

class ClassCompiler: protected DefaultVisitor
{
public:
  ClassCompiler(Binder *binder);
  virtual ~ClassCompiler();

  inline QString name() const { return _M_name; }
  inline QStringList baseClasses() const { return _M_base_classes; }

  void run(ClassSpecifierAST *node);

protected:
  virtual void visitClassSpecifier(ClassSpecifierAST *node);
  virtual void visitBaseSpecifier(BaseSpecifierAST *node);

private:
  Binder *_M_binder;
  TokenStream *_M_token_stream;
  QString _M_name;
  QStringList _M_base_classes;
  NameCompiler name_cc;
  TypeCompiler type_cc;
};

#endif // CLASS_COMPILER_H

// kate: space-indent on; indent-width 2; replace-tabs on;
