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

#ifndef TYPEPARSER_H
#define TYPEPARSER_H

#include <QList>
#include <QString>
#include <QStringList>
#include <QStack>

class Scanner;
class TypeParser {

    public:
        struct Info {
            Info() : is_reference(false), is_constant(false), is_busted(false), indirections(0) { }

            QStringList qualified_name;
            /**
             * List of different parts of “array”, or [foo]
             */
            QStringList arrays;
            QList<Info> template_instantiations;
        uint is_reference : 1;
        uint is_constant : 1;
            /**
             * If the type is not supported / valid
             */
        uint is_busted : 1;
        uint indirections : 5;

            QString toString() const;
            QString instantiationName() const; //private?
        };

        /**
         * Loops through the text token by token and returns the data
         */
        static Info parse(const QString &str);

    private:
        static void parseIdentifier(Scanner &scanner, QStack<Info *> &stack, QString &array, bool in_array, bool &colon_prefix);
};

#endif // TYPEPARSER_H
