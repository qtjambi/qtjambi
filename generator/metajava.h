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

#ifndef METAJAVA_H
#define METAJAVA_H

#include "abstractmetalang.h"

class MetaJavaClass;
class MetaJavaField;
class MetaJavaFunction;
class MetaJavaType;
class MetaJavaVariable;
class MetaJavaArgument;
class MetaJavaEnumValue;
class MetaJavaEnum;



class MetaJavaType : public AbstractMetaType
{};

class MetaJavaArgument : public AbstractMetaArgument
{};

class MetaJavaField : public AbstractMetaField
{};

class MetaJavaFunction : public AbstractMetaFunction
{};

class MetaJavaEnumValue : public AbstractMetaEnumValue
{};

class MetaJavaEnum : public AbstractMetaEnum
{};

class MetaJavaClass : public AbstractMetaClass
{};

#endif // METAJAVA_H
