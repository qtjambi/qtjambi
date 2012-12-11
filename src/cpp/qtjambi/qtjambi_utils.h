/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** ** $BEGIN_LICENSE$
** Commercial Usage
** Licensees holding valid Qt Commercial licenses may use this file in
** accordance with the Qt Commercial License Agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Nokia.
**
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
**
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
**
** If you are unsure which license is appropriate for your use, please
** contact the sales department at qt-sales@nokia.com.
** $END_LICENSE$

**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#ifndef QTJAMBI_UTILS_H
#define QTJAMBI_UTILS_H

struct ClassData {
    jclass *cl;
    const char *name;
};

struct MethodData {
    jclass *cl;
    jmethodID *id;
    const char *name;
    const char *signature;
};

struct FieldData {
    jclass *cl;
    jfieldID *id;
    const char *name;
    const char *signature;
};

QTJAMBI_EXPORT bool qtjambi_resolve_classes(JNIEnv *env, ClassData *data);
QTJAMBI_EXPORT void qtjambi_unresolve_classes(JNIEnv *env, ClassData *data);
QTJAMBI_EXPORT bool qtjambi_resolve_fields(JNIEnv *env, bool bf_static, FieldData *data);
QTJAMBI_EXPORT void qtjambi_unresolve_fields(JNIEnv *env, FieldData *data);
QTJAMBI_EXPORT bool qtjambi_resolve_methods(JNIEnv *env, bool bf_static, MethodData *data);
QTJAMBI_EXPORT void qtjambi_unresolve_methods(JNIEnv *env, MethodData *data);

#endif // QTJAMBI_UTILS_H
