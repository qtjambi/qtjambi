package com.trolltech.qt.designer;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;

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
        return null;
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
        QSizePolicy policy = new QSizePolicy(QSizePolicy.Policy.valueOf(hSizeType),
                                             QSizePolicy.Policy.valueOf(vSizeType));
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

