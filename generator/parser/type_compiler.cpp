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

/* -*- Mode: C++; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */


#include "type_compiler.h"
#include "name_compiler.h"
#include "lexer.h"
#include "symbol.h"
#include "tokens.h"
#include "binder.h"

#include <QtCore/QString>

TypeCompiler::TypeCompiler(Binder *binder)
  : _M_binder (binder), _M_token_stream(binder->tokenStream ())
{
}

void TypeCompiler::run(TypeSpecifierAST *node)
{
  _M_type.clear();
  _M_cv.clear();

  visit(node);

  if (node && node->cv)
    {
      const ListNode<std::size_t> *it = node->cv->toFront();
      const ListNode<std::size_t> *end = it;
      do
        {
          int kind = _M_token_stream->kind(it->element);
          if (! _M_cv.contains(kind))
            _M_cv.append(kind);

          it = it->next;
        }
      while (it != end);
    }
}

void TypeCompiler::visitClassSpecifier(ClassSpecifierAST *node)
{
  visit(node->name);
}

void TypeCompiler::visitEnumSpecifier(EnumSpecifierAST *node)
{
  visit(node->name);
}

void TypeCompiler::visitElaboratedTypeSpecifier(ElaboratedTypeSpecifierAST *node)
{
  visit(node->name);
}

void TypeCompiler::visitSimpleTypeSpecifier(SimpleTypeSpecifierAST *node)
{
  if (const ListNode<std::size_t> *it = node->integrals)
    {
      it = it->toFront();
      const ListNode<std::size_t> *end = it;
      QString current_item;
      do
        {
          std::size_t token = it->element;
          current_item += token_name(_M_token_stream->kind(token));
          current_item += " ";
          it = it->next;
        }
      while (it != end);
      _M_type += current_item.trimmed();
    }
  else if (node->type_of)
    {
      // ### implement me
      _M_type += QLatin1String("typeof<...>");
    }

  visit(node->name);
}

void TypeCompiler::visitName(NameAST *node)
{
  NameCompiler name_cc(_M_binder);
  name_cc.run(node);
  _M_type = name_cc.qualifiedName();
}

QStringList TypeCompiler::cvString() const
{
  QStringList lst;

  foreach (int q, cv())
    {
      if (q == Token_const)
        lst.append(QLatin1String("const"));
      else if (q == Token_volatile)
        lst.append(QLatin1String("volatile"));
    }

  return lst;
}

bool TypeCompiler::isConstant() const
{
  return _M_cv.contains(Token_const);
}

bool TypeCompiler::isVolatile() const
{
  return _M_cv.contains(Token_volatile);
}

// kate: space-indent on; indent-width 2; replace-tabs on;
