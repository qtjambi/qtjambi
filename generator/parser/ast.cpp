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

#include "ast.h"
#include "lexer.h"

// kate: space-indent on; indent-width 2; replace-tabs on;

QString AST::toString(TokenStream *stream) const
{
    const Token &tk = stream->token((int) start_token);
    const Token &end_tk = stream->token ((int) end_token);
    return QString::fromLatin1(tk.text + tk.position, end_tk.position - tk.position);
}
