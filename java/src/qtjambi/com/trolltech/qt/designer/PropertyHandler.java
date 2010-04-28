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

package com.trolltech.qt.designer;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.internal.RetroTranslatorHelper;
import com.trolltech.qt.xml.*;

import java.lang.reflect.*;
import java.util.*;


abstract class PropertyHandler {
    public abstract Object create(QDomElement e) throws QUiLoaderException;

    public int childIntValue(QDomElement e, String s) {
        assert !e.isNull();
        QDomNode n = namedChildContent(e, s);
        return n.isNull() ? 0 : Integer.parseInt(n.nodeValue());
    }

    public int childIntValue(QDomElement e) {
        assert !e.isNull();
        if (!e.isNull()) {
            QDomNode n = e.firstChild();
            if (!n.isNull())
                return Integer.parseInt(n.nodeValue());
        }
        return 0;
    }

    public String childStringValue(QDomElement e, String s) {
        assert !e.isNull();
        QDomNode n = namedChildContent(e, s);
        return n.isNull() ? "" : n.nodeValue();
    }

    public String childStringValue(QDomElement e) {
        assert !e.isNull();
        if (!e.isNull()) {
            QDomNode n = e.firstChild();
            if (!n.isNull())
                return n.nodeValue();
        }
        return "";
    }

    public boolean childBoolValue(QDomElement e, String s) {
        assert !e.isNull();
        QDomNode n = namedChildContent(e, s);
        return !n.isNull() && n.nodeValue().equals("true");
    }

    public boolean childBoolValue(QDomElement e) {
        assert !e.isNull();
        if (!e.isNull()) {
            QDomNode n = e.firstChild();
            if (!n.isNull())
                return n.nodeValue().equals("true");
        }
        return false;
    }

    private QDomNode namedChildContent(QDomElement e, String s) {
        assert !e.isNull();
        QDomNode n = e;
        if (!e.isNull()) n = e.namedItem(s);
        if (!n.isNull()) n = n.firstChild();
        return n;
    }

}


class BoolPropertyHandler extends PropertyHandler {
    public Object create(QDomElement e) throws QUiLoaderException {
        return childBoolValue(e);
    }
}


class ColorPropertyHandler extends PropertyHandler {
    public Object create(QDomElement e) throws QUiLoaderException {
        QColor color = new QColor(childIntValue(e, "red"), childIntValue(e, "green"), childIntValue(e, "blue"));
        String alpha = e.attribute("alpha");
        if (alpha.length() > 0) {
            color.setAlpha(Integer.parseInt(alpha));
        }
        return color;
    }
}


class EnumPropertyHandler extends PropertyHandler {

    public Object create(QDomElement e) throws QUiLoaderException {
        String name = childStringValue(e);
        return enumForValue(name);
    }

    @SuppressWarnings("unchecked")
    static Object enumForValue(String name) throws QUiLoaderException {

        Object hcValue = hardcodedValues.get(name);
        if (hcValue != null)
            return hcValue;

        int valuePos = name.lastIndexOf('.');
        int enumPos = name.lastIndexOf('.', valuePos-1);
        if (enumPos > 0 && valuePos > 0) {
            String className = name.substring(0, enumPos);
            String enumName = name.substring(enumPos+1, valuePos);
            String enumValue = name.substring(valuePos + 1);
            try {
                Class cl = Class.forName(className + '$' + enumName);
                return Enum.valueOf(cl, enumValue);
            } catch (Exception ex) {
                throw new QUiLoaderException("Converting enum '" + name + "' failed...", ex);
            }
        } else {
            throw new QUiLoaderException("Converting enum '" + name + "' failed", null);
        }
    }

    private static HashMap<String,Object> hardcodedValues = new HashMap<String,Object>();
    static {
        hardcodedValues.put("TopToolBarArea", Qt.ToolBarArea.TopToolBarArea.value());
        hardcodedValues.put("BottomToolBarArea", Qt.ToolBarArea.BottomToolBarArea.value());
        hardcodedValues.put("RightToolBarArea", Qt.ToolBarArea.RightToolBarArea.value());
        hardcodedValues.put("LeftToolBarArea", Qt.ToolBarArea.LeftToolBarArea.value());
    }

}


class FontPropertyHandler extends PropertyHandler {
    public Object create(QDomElement e) throws QUiLoaderException {
        QFont f = new QFont();
        f.setFamily(childStringValue(e, "family"));
        f.setPointSize(childIntValue(e, "pointsize"));
        f.setItalic(childBoolValue(e, "italic"));
        f.setBold(childBoolValue(e, "bold"));
        return f;
    }

}


class IconsetPropertyHandler extends PropertyHandler {

    public Object create(QDomElement e) throws QUiLoaderException {
        return new QIcon(childStringValue(e));
    }
}


class NumberPropertyHandler extends PropertyHandler {
    public Object create(QDomElement e) throws QUiLoaderException {
        return childIntValue(e);
    }
}


class OrientationPropertyHandler extends PropertyHandler {
    public Object create(QDomElement e) throws QUiLoaderException {
        String orientation = childStringValue(e);
        if (orientation.contains("Vertical")) {
            return com.trolltech.qt.core.Qt.Orientation.Vertical;
        } else {
            return com.trolltech.qt.core.Qt.Orientation.Horizontal;
        }
    }
}


class PalettePropertyHandler extends PropertyHandler {
    public Object create(QDomElement e) throws QUiLoaderException {
        QPalette p = QApplication.palette();
        parse(e.namedItem("active").toElement(), p, QPalette.ColorGroup.Active);
        parse(e.namedItem("inactive").toElement(), p, QPalette.ColorGroup.Inactive);
        parse(e.namedItem("disabled").toElement(), p, QPalette.ColorGroup.Disabled);

        return p;
    }

    private void parse(QDomElement groupElement, QPalette p, QPalette.ColorGroup group) throws QUiLoaderException {
        QDomNodeList list = groupElement.childNodes();
        for (int i=0; i<list.count(); ++i) {
            QDomNode node = list.at(i);
            if (node.nodeName().equals("colorrole")) {
                QDomElement roleElement = node.toElement();
                QPalette.ColorRole role = colorRoles.get(roleElement.attribute("role"));

                QDomElement brushElement = roleElement.namedItem("brush").toElement();

                QBrush brush;
                if (brushElement.attribute("brushstyle").equals("SolidPattern")) {
                    QColor color = (QColor) colorHandler.create(brushElement.namedItem("color").toElement());
                    brush = new QBrush(color);
                } else {
                    throw new QUiLoaderException("Unhandled brush style: " + brushElement.attribute("brushstyle"));
                }

                p.setBrush(group, role, brush);
            }
        }
    }

    private ColorPropertyHandler colorHandler = new ColorPropertyHandler();
    static HashMap<String, QPalette.ColorRole> colorRoles;
    static {
        colorRoles = new HashMap<String, QPalette.ColorRole>();
        for (QPalette.ColorRole role : QPalette.ColorRole.values()) {
            colorRoles.put(role.name(), role);
        }
    }
}

class PixmapPropertyHandler extends PropertyHandler {

    public Object create(QDomElement e) throws QUiLoaderException {
        return new QPixmap(childStringValue(e));
    }
}

class PointPropertyHandler extends PropertyHandler {
    public Object create(QDomElement e) throws QUiLoaderException {
        int x = childIntValue(e, "x");
        int y = childIntValue(e, "y");
        return new QPoint(x, y);
    }
}


class RectPropertyHandler extends PropertyHandler {
    public Object create(QDomElement e) throws QUiLoaderException {
        int x = childIntValue(e, "x");
        int y = childIntValue(e, "y");
        int width = childIntValue(e, "width");
        int height = childIntValue(e, "height");

        return new QRect(x, y, width, height);
    }
}


class SetPropertyHandler extends PropertyHandler {

    public Object create(QDomElement e) throws QUiLoaderException {
        String flagsValues[] = RetroTranslatorHelper.split(childStringValue(e), "|");

        Object enumsPreprocess[] = new Object[flagsValues.length];
        Class<?> enumClass = null;
        for (int i=0; i<enumsPreprocess.length; ++i) {
            enumsPreprocess[i] = EnumPropertyHandler.enumForValue(flagsValues[i]);
            if (enumsPreprocess[i] != null)
                enumClass = enumsPreprocess[i].getClass();
        }

        assert enumClass != null;

        if (enumsPreprocess.length > 0) {
            try {
                Object enumValues = Array.newInstance(enumClass, enumsPreprocess.length);
                for (int i=0; i<enumsPreprocess.length; ++i)
                    Array.set(enumValues, i, enumsPreprocess[i]);

                Method m = enumClass.getMethod("createQFlags", enumValues.getClass());
                com.trolltech.qt.QFlags qflags = (com.trolltech.qt.QFlags) m.invoke(enumClass, enumValues);

                return qflags.value();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}


class SizePropertyHandler extends PropertyHandler {
    public Object create(QDomElement e) throws QUiLoaderException {
        int width = childIntValue(e, "width");
        int height = childIntValue(e, "height");
        return new QSize(width, height);
    }
}


class SizePolicyPropertyHandler extends PropertyHandler {
    public Object create(QDomElement e) throws QUiLoaderException {
        String vSizeType = e.attribute("vsizetype");
        String hSizeType = e.attribute("hsizetype");
        int hStretch = childIntValue(e, "horstretch");
        int vStretch = childIntValue(e, "verstretch");

        QSizePolicy.Policy hPolicy = hSizeType.length() == 0
                ? QSizePolicy.Policy.resolve(childIntValue(e, "hsizetype"))
                : QSizePolicy.Policy.valueOf(hSizeType);

        QSizePolicy.Policy vPolicy = vSizeType.length() == 0
                ? QSizePolicy.Policy.resolve(childIntValue(e, "vsizetype"))
                : QSizePolicy.Policy.valueOf(vSizeType);

        QSizePolicy policy = new QSizePolicy(hPolicy, vPolicy);
        policy.setHorizontalStretch((byte) hStretch);
        policy.setVerticalStretch((byte) vStretch);

        return policy;
    }
}


class StringPropertyHandler extends PropertyHandler {
    public Object create(QDomElement e) throws QUiLoaderException {
        return childStringValue(e);
    }
}

