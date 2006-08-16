/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $CPP_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

/*
  Copyright 2005 Roberto Raggi <roberto@kdevelop.org>

  Permission to use, copy, modify, distribute, and sell this software and its
  documentation for any purpose is hereby granted without fee, provided that
  the above copyright notice appear in all copies and that both that
  copyright notice and this permission notice appear in supporting
  documentation.

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
  KDEVELOP TEAM BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
  AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

#ifndef PP_H
#define PP_H

#if defined(_WIN64) || defined(WIN64) || defined(__WIN64__) \
    || defined(_WIN32) || defined(WIN32) || defined(__WIN32__)
#  define PP_OS_WIN
#endif

#include <set>
#include <map>
#include <vector>
#include <string>
#include <iterator>
#include <iostream>
#include <cassert>
#include <cctype>

#include <fcntl.h>

#ifdef HAVE_MMAP
#  include <sys/mman.h>
#endif

#include <sys/stat.h>
#include <sys/types.h>

#if defined (RPP_JAMBI)
#  include "rxx_allocator.h"
#else
#  include "rpp-allocator.h"
#endif

#include "pp-fwd.h"
#include "pp-cctype.h"
#include "pp-string.h"
#include "pp-symbol.h"
#include "pp-internal.h"
#include "pp-iterator.h"
#include "pp-macro.h"
#include "pp-environment.h"
#include "pp-scanner.h"
#include "pp-macro-expander.h"
#include "pp-engine.h"
#include "pp-engine-bits.h"

#endif // PP_H

// kate: space-indent on; indent-width 2; replace-tabs on;
