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


#ifndef SMALLOBJECT_H
#define SMALLOBJECT_H

#include "rxx_allocator.h"
#include <cstring>

class pool
{
  rxx_allocator<char> __alloc;

public:
  inline void *allocate(std::size_t __size);
};

inline void *pool::allocate(std::size_t __size)
{
  return __alloc.allocate(__size);
}

#endif

// kate: space-indent on; indent-width 2; replace-tabs on;
