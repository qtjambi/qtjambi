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


#ifndef DUMPTREE_H
#define DUMPTREE_H

#include "default_visitor.h"

class DumpTree: protected DefaultVisitor
{
public:
  DumpTree();

  void dump(AST *node) { visit(node); }

protected:
  virtual void visit(AST *node);
};

#endif // DUMPTREE_H

// kate: space-indent on; indent-width 2; replace-tabs on;
