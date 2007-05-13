package com.trolltech.extensions.awt;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class QAwtHost extends QWidget {

    public QAwtHost(QWidget parent) {
        super(parent);
    }

    public QAwtHost() {
        this(null);
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Component component() {
        return component;
    }

    public QSize sizeHint() {
        Dimension d = component.getPreferredSize();
        return new QSize((int) d.getWidth(), (int) d.getHeight());
    }

    public void resizeEvent(QResizeEvent e) {
        if (component == null)
            return;

        buffer = null;

        if (qbuffer != null)
            qbuffer.dispose();
        qbuffer = null;
        component.resize(e.size().width(), e.size().height());
    }



    protected void paintEvent(QPaintEvent e) {

        if (component == null)
            return;

        if (buffer == null) {
            buffer = new BufferedImage(width(), height(), BufferedImage.TYPE_INT_ARGB);
        }

        if (qbuffer == null) {
            qbuffer = new QImage(width(), height(), QImage.Format.Format_RGB32);
        }

        Graphics2D g = (Graphics2D) buffer.getGraphics();
        g.setColor(Color.red);
        g.fillRect(0, 0, width(), height());
        component.paint(g);

        for (int y=0; y<height(); ++y) {
            for (int x=0; x<width(); ++x) {
                qbuffer.setPixel(x, y, buffer.getRGB(x, y));
            }
        }

        QPainter p = new QPainter(this);
        p.drawImage(0, 0, qbuffer);
    }

    private Component component;
    private BufferedImage buffer;
    private QImage qbuffer;

    public static void main(String args[]) {
        QApplication.initialize(args);

        QWidget root = new QWidget();

        QGridLayout layout = new QGridLayout(root);

        for (int i=0; i<9; ++i) {
            if (i == 4) {
                QAwtHost host = new QAwtHost();
                layout.addWidget(host, 1, 1);

                JButton button = new JButton();
                button.setText("I'm a JButton");
                button.setVisible(true);
                host.setComponent(button);

            } else {
                QWidget widget = new QWidget() {
                        protected void paintEvent(QPaintEvent e) {
                            QPainter p = new QPainter(this);
                            p.setBrush(new QBrush(new QLinearGradient(-100, -100, 400, 400)));
                            p.drawRect(0, 0, width() - 1, height() - 1);
                        }

                        public QSize sizeHint() { return new QSize(100, 100); }
                    };
                layout.addWidget(widget, i%3, i/3);
            }
        }

        root.show();

        QApplication.exec();
    }
}
