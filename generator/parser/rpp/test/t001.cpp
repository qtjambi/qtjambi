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
  ciao roberto
*/

#define A 1

#if A - 1 == 0
OK
#else
KO
#endif

#define A(a,b) a + b
#define C(a,b) A(b,a)

#define comp(a,b) #a #b
#define str(a) #a

C(1,2) comp(me, you) str(kdevelop is cool)



