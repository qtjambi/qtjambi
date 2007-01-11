
/**************************************************************************
 *
 * Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
 *
 * This file is part of $PRODUCT$.
 *
 * $CPP_LICENSE$
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 ***************************************************************************/

package com.trolltech.qt.designer;

import com.trolltech.qt.gui.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.*;

import java.lang.reflect.*;
import java.util.*;

public class QUiLoader {

    private interface PropertyReceiver {
        void setProperty(String name, Object value);
    }

    private class QObjectPropertyReceiver implements PropertyReceiver {
        QObject object;
        QObjectPropertyReceiver(QObject o) { this.object = o; }
        public void setProperty(String name, Object value) {
             QUiLoader.setProperty(object, name, value);
        }
    }

    private class SpacerPropertyReceiver implements PropertyReceiver {
        Qt.Orientation orientation;
        QSize size;
        public void setProperty(String name, Object value) {
            if (name.equals("orientation")) orientation = (Qt.Orientation) value;
            else if (name.equals("sizeHint")) size = (QSize) value;
        }

        QSpacerItem spacerItem() {
            return new QSpacerItem(
                    size.width(),
                    size.height(),
                    orientation == Qt.Orientation.Horizontal
                            ? QSizePolicy.Policy.Expanding
                            : QSizePolicy.Policy.Minimum,
                    orientation == Qt.Orientation.Horizontal
                            ? QSizePolicy.Policy.Minimum
                            : QSizePolicy.Policy.Expanding);
        }
    }

    public static QWidget load(QIODevice device) throws Exception {
        return load(device, null);
    }

    public static QWidget load(QIODevice device, QWidget parent) throws Exception {
        QUiLoader loader = new QUiLoader(device, parent);
        loader.parse();
        return loader.widget();
    }

    private QUiLoader(QIODevice device, QWidget parent) {
        this.device = device;
        this.parent = parent;
    }

    private void parse() throws Exception {
        if (!device.isOpen()) {
            if (!device.open(QIODevice.OpenModeFlag.ReadOnly)) {
                throw new QUiLoaderException("Unable to open iodevice: " + device);
            }
        }

        QDomDocument doc = new QDomDocument();
        doc.setContent(device);

        QDomElement e = doc.firstChildElement();
        parseUiRoot(e);
    }

    private void parseUiRoot(QDomElement ui) throws Exception {
        if (!ui.nodeName().equals("ui"))
            throw new QUiLoaderException("Bad root element, expected 'ui', got: " + ui.nodeName());

        String version = ui.attribute("version");
        if (!version.equals("4.0"))
            throw new QUiLoaderException("Unsupported version: " + version + ", expected 4.0");

        String cls = parseClass(ui);
        widget = parseWidget(ui.namedItem("widget").toElement(), this.parent);

        parseConnections(ui.namedItem("connections").toElement());
        parseTabOrder(ui.namedItem("tabstops").toElement());
    }

    private String parseClass(QDomElement ui) throws Exception {
        QDomNodeList list = ui.childNodes();
        for (int i=0; i<list.count(); ++i) {
            QDomNode node = list.at(i);
            if (node.nodeName().equals("class")) {
                assert !node.isNull();
                QDomNode child = node.firstChild();
                if (!child.isCharacterData())
                    throw new QUiLoaderException("Expected text data in 'class' node");
                return child.nodeValue();
            }
        }

        return null;
    }

    private QWidget parseWidget(QDomElement widgetNode, QObject parent) throws Exception {
        // Create the widget
        String cls = widgetNode.attribute("class");
        String name = widgetNode.attribute("name");
        Class<? extends QObject> cl = loadClass(cls);
        QWidget widget = (QWidget) createInstance(cl, parent);
        widget.setObjectName(name);

        if (name.equals("centralwidget") && parent instanceof QMainWindow) {
            System.out.println("reassigning to central widget");
            ((QMainWindow) parent).setCentralWidget(widget);
        }

        // Parse its properties...
        parseProperties(new QObjectPropertyReceiver(widget), widgetNode.childNodes());

        // Child widgets outside layout
        QDomNodeList list = widgetNode.childNodes();
        for (int i=0; i<list.count(); ++i) {
            QDomNode node = list.at(i);
            if (node.nodeName().equals("widget")) {
                parseWidget(node.toElement(), widget);
            }
        }

        // the layout...
        QDomNode layoutNode = widgetNode.namedItem("layout");
        if (!layoutNode.isNull())
            parseLayout(layoutNode.toElement(), widget);

        return widget;
    }

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private void parseLayout(QDomElement layoutNode, QWidget parent) throws Exception {
        Class<? extends QObject> cls = loadClass(layoutNode.attribute("class"));
        QLayout layout = (QLayout) createInstance(cls, parent);
        parseProperties(new QObjectPropertyReceiver(layout), layoutNode.childNodes());

        parent.setLayout(layout);

        // The items
        QDomNodeList list = layoutNode.childNodes();
        for (int i=0; i<list.count(); ++i) {
            QDomNode node = list.at(i);
            if (node.nodeName().equals("item")) {
                QDomElement itemNode = node.toElement();

                QSpacerItem spacer = null;
                QWidget widget = null;

                QDomNode child = itemNode.firstChild();
                if (child.nodeName().equals("widget")) {
                    widget = parseWidget(child.toElement(), layout);
                } else if (child.nodeName().equals("spacer")) {
                    spacer = parseSpacer(child.toElement(), layout);
                }

                if (layout instanceof QGridLayout) {
                    int row = parseInt(itemNode.attribute("row"), 0);
                    int col = parseInt(itemNode.attribute("column"), 0);
                    int colspan = parseInt(itemNode.attribute("colspan"), 1);
                    int rowspan = parseInt(itemNode.attribute("rowspan"), 1);

                    if (widget != null)
                        ((QGridLayout) layout).addWidget(widget, row, col, rowspan, colspan);
                    else if (spacer != null)
                        ((QGridLayout) layout).addItem(spacer, row, col, rowspan, colspan);
                } else {
                    if (widget != null)
                        layout.addWidget(widget);
                    else if (spacer != null)
                        layout.addItem(spacer);
                }

            }
        }
    }

    private QSpacerItem parseSpacer(QDomElement e, QObject parent) throws QUiLoaderException {
        SpacerPropertyReceiver spacer = new SpacerPropertyReceiver();
        parseProperties(spacer, e.childNodes());
        return spacer.spacerItem();
    }

    private void parseProperties(PropertyReceiver receiver, QDomNodeList list) throws QUiLoaderException {
        for (int i=0; i<list.count(); ++i) {
            QDomNode node = list.at(i);

            if (node.nodeName().equals("property")) {
                QDomElement e = node.toElement();

                String name = e.attribute("name");
                assert !e.isNull();
                QDomElement val = e.firstChildElement();

                PropertyHandler handler = propertyHandlers.get(name);
                if (handler == null)
                    handler = typeHandlers.get(val.nodeName());

                if (handler == null)
                    throw new QUiLoaderException("No handler for property " + name + ", of type: " + val.nodeName());

                Object value = handler.create(val);
                receiver.setProperty(name, value);
            }
        }
    }

    private QObject createInstance(Class<? extends QObject> cl, QObject parent) throws QUiLoaderException {

        try {
            if (QWidget.class.isAssignableFrom(cl)) {
                if (parent instanceof QWidget) {
                    return cl.getConstructor(QWidget.class).newInstance((QWidget) parent);
                } else {
                    return cl.getConstructor().newInstance();
                }
            } else if (QLayout.class.isAssignableFrom(cl) && parent instanceof QWidget) {
                Constructor ctor = cl.getConstructor(QWidget.class);
                return (QObject) ctor.newInstance(parent);
            } else {
                return cl.getConstructor(QObject.class).newInstance();
            }
        } catch (Exception e) {
            throw new QUiLoaderException("Failed to create widget", e);
        }
    }

    private Class<? extends QObject> loadClass(String cls) throws QUiLoaderException {
        Class<? extends QObject> cl;
        try {
            cl = Class.forName(cls).asSubclass(QObject.class);
        } catch (ClassNotFoundException e) {
            try {
                cl = Class.forName("com.trolltech.qt.gui." + cls).asSubclass(QObject.class);
            } catch (Exception ex) {
                throw new QUiLoaderException("Failed to load class", ex);
            }
        } catch (Exception e) {
            throw new QUiLoaderException("Failed to load class", e);
        }
        return cl;
    }

    private static void setProperty(QObject o, String property, Object value) {

        try {
            QtPropertyManager.Entry entry = QtPropertyManager.findPropertiesRecursive(o.getClass()).get(property);

            if (property.equals("geometry") && o.isWidgetType() && ((QWidget) o).isWindow()) {
                QWidget window = (QWidget) o;
                QRect r = (QRect) value;
                if (r.x() == 0 && r.y() == 0)
                    window.resize(r.width(), r.height());
                else
                    window.setGeometry(r);
                return;
            }

            if (value == null)
                System.out.println("Null value for: " + property + ", " + entry + ", " + value);
            if (entry == null)
                System.out.println("No property entry for: " + property + ", " + value);

            if (null != entry && value != null) {
                entry.write.invoke(o, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void parseConnections(QDomElement e) throws QUiLoaderException {
        assert !e.isNull();
        try {
        for (QDomElement el = e.firstChildElement(); !el.isNull(); el = el.nextSiblingElement()) {
            if (el.nodeName().equals("connection")) {
                String senderName = el.namedItem("sender").firstChild().nodeValue();
                String signalName = el.namedItem("signal").firstChild().nodeValue();
                String receiverName = el.namedItem("receiver").firstChild().nodeValue();
                String slotSignature = el.namedItem("slot").firstChild().nodeValue();

                QObject sender = widget.findChild(QObject.class, senderName);
                if (sender == null)
                    throw new QUiLoaderException("Unknown sender: '" + senderName + "'");

                QObject receiver = widget.findChild(QObject.class, receiverName);
                if (receiver == null)
                    throw new QUiLoaderException("Unknown sender: '" + receiverName + "'");

                int lt = signalName.indexOf('<');
                if (lt > 0)
                    signalName = signalName.substring(0, lt);

                int paren = signalName.indexOf('(');
                if (paren > 0)
                    signalName = signalName.substring(0, paren);

                // Get the signal object...
                try {
                    QObject.AbstractSignal signal = (QObject.AbstractSignal) sender.getClass().getField(signalName).get(sender);
                    signal.connect(receiver, slotSignature);
                } catch (Exception ex) {
                    throw new QUiLoaderException("Connection failed: "
                                                 + senderName + "." + signalName
                                                 + " to " + receiverName + "." + slotSignature, ex);
                }
            }
        }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void parseTabOrder(QDomElement e) {
        if (e.isNull())
            return;
        List<String> list = new ArrayList<String>();
        for (QDomElement el = e.firstChildElement(); !el.isNull(); el = el.nextSiblingElement()) {
            assert !el.isNull();
            if (el.nodeName().equals("tabstop"))
                list.add(el.firstChild().nodeValue());
        }

        for (int i=0; i<list.size() - 1; ++i) {
            QWidget w1 = (QWidget) widget.findChild(QWidget.class, list.get(i));
            QWidget w2 = (QWidget) widget.findChild(QWidget.class, list.get(i+1));
            QWidget.setTabOrder(w1, w2);
        }
    }

    private QWidget widget() {
        return widget;
    }

    private QIODevice device;
    private QWidget parent;
    private QWidget widget;

    private static HashMap<String, PropertyHandler> typeHandlers;
    private static HashMap<String, PropertyHandler> propertyHandlers;

    static {
        typeHandlers = new HashMap<String, PropertyHandler>();
        typeHandlers.put("rect", new RectPropertyHandler());
        typeHandlers.put("string", new StringPropertyHandler());
        typeHandlers.put("number", new NumberPropertyHandler());
        typeHandlers.put("sizepolicy", new SizePolicyPropertyHandler());
        typeHandlers.put("size", new SizePropertyHandler());
        typeHandlers.put("point", new PointPropertyHandler());
        typeHandlers.put("bool", new BoolPropertyHandler());
        typeHandlers.put("enum", new EnumPropertyHandler());
        typeHandlers.put("font", new FontPropertyHandler());
        typeHandlers.put("palette", new PalettePropertyHandler());
        typeHandlers.put("color", new ColorPropertyHandler());
        typeHandlers.put("pixmap", new PixmapPropertyHandler());
        typeHandlers.put("iconset", new IconsetPropertyHandler());
        typeHandlers.put("set", new SetPropertyHandler());

        propertyHandlers = new HashMap<String, PropertyHandler>();
        propertyHandlers.put("orientation", new OrientationPropertyHandler());
    }

    public static void main(String args[]) throws Exception {
        QApplication.initialize(args);

        for (String s : args) {
            QWidget w = load(new QFile(s));
            w.show();
        }

        QApplication.exec();
    }
}
