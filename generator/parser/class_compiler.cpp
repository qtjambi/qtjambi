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


#include "class_compiler.h"
#include "lexer.h"
#include "binder.h"

ClassCompiler::ClassCompiler(Binder *binder)
  : _M_binder (binder),
     _M_token_stream(binder->tokenStream ()),
     name_cc(_M_binder),
     type_cc(_M_binder)
{
}

ClassCompiler::~ClassCompiler()
{
}

void ClassCompiler::run(ClassSpecifierAST *node)
{
  name_cc.run(node->name);
  _M_name = name_cc.name();
  _M_base_classes.clear();

  visit(node);
}

void ClassCompiler::visitClassSpecifier(ClassSpecifierAST *node)
{
  visit(node->base_clause);
}

void ClassCompiler::visitBaseSpecifier(BaseSpecifierAST *node)
{
  name_cc.run(node->name);
  QString name = name_cc.name();

  if (! name.isEmpty())
    _M_base_classes.append(name);
}


// kate: space-indent on; indent-width 2; replace-tabs on;
