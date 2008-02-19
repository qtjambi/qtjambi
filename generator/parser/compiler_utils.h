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


#ifndef COMPILER_UTILS_H
#define COMPILER_UTILS_H

#include <utility>

#include "codemodel.h"

class QString;
class QStringList;
struct TypeSpecifierAST;
struct DeclaratorAST;
class TokenStream;
class Binder;

namespace CompilerUtils
{

TypeInfo typeDescription(TypeSpecifierAST *type_specifier, DeclaratorAST *declarator, Binder *binder);

} // namespace CompilerUtils

#endif // COMPILER_UTILS_H

// kate: space-indent on; indent-width 2; replace-tabs on;
