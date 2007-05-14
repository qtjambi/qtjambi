package com.trolltech.tests;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class GlassPane extends QWidget {

    public static class Overlay extends QWidget {
        public enum Transition {
            Fade,
            ScrollLeft,
            ScrollRight,
            ScrollUp,
            ScrollDown,
            SplitVertical,
            SplitHorizontal
        };

        public Overlay(QPixmap start, QPixmap end) {
            this.start = start;
            this.end = end;
            this.timeline = new QTimeLine();
            transition = Transition.Fade;

            setAttribute(Qt.WidgetAttribute.WA_OpaquePaintEvent);

            timeline.finished.connect(this, "disposeLater()");
            timeline.finished.connect(this, "hide()");
            timeline.valueChanged.connect(this, "update(double)");
            timeline.start();
        }

        public void update(double d) {
            level = d;
            update();
        }

        public void setTransition(Transition type) {
            transition = type == null ? Transition.Fade : type;
        }

        protected void paintEvent(QPaintEvent e) {
            QPainter p = new QPainter(this);

            switch (transition) {
            case Fade:
                p.drawPixmap(0, 0, start);
                p.setOpacity(level);
                p.drawPixmap(0, 0, end);
                break;
            case ScrollLeft:
                p.drawPixmap(0, 0, end);
                p.drawPixmap((int) (level * width()), 0, start);
                break;
            case ScrollRight:
                p.drawPixmap(0, 0, end);
                p.drawPixmap((int) (-level * width()), 0, start);
                break;
            case ScrollUp:
                p.drawPixmap(0, 0, end);
                p.drawPixmap(0, (int) (-level * height()), start);
                break;
            case ScrollDown:
                p.drawPixmap(0, 0, end);
                p.drawPixmap(0, (int) (level * height()), start);
                break;
            case SplitHorizontal:
                if (level < 0.2) {
                    level *= 5;
                    p.drawLine(0, height() / 2, (int)(level * width()), height() / 2);
                } else {
                    level = (level - 0.2) / 0.8;
                    int h2 = start.height() / 2;
                    p.drawPixmap(0, 0, end);
                    p.drawPixmap(0, (int) (-level * height()), start.width(), h2,
                                 start,
                                 0, 0, start.width(), h2);
                    p.drawPixmap(0, (int) (level * height()) + h2, start.width(), h2,
                                 start,
                                 0, h2, start.width(), h2);
                }
                break;
            case SplitVertical:
                if (level < 0.2) {
                    level *= 5;
                    p.drawLine(width() / 2, 0, width() / 2, (int) (level * height()));
                } else {
                    level = (level - 0.2) / 0.8;
                    int w2 = start.width() / 2;
                    p.drawPixmap(0, 0, end);
                    p.drawPixmap((int) (level * width()) + w2, 0, w2, start.height(),
                                 start,
                                 w2, 0, w2, start.height());
                    p.drawPixmap((int) (-level * height()), 0, w2, start.height(),
                                 start,
                                 0, 0, w2, start.height());
                }
                break;
            }
        }

        private Transition transition;
        private double level;
        private QTimeLine timeline;
        private QPixmap start;
        private QPixmap end;
    }

    public static QWidget createPanel(String baseTitle) {
        QWidget widget = new QGroupBox(baseTitle);

        QGridLayout layout = new QGridLayout(widget);
        for (int i=0; i<6; ++i) {
            QLabel label = new QLabel(baseTitle + ": " + (i+1));
            QLineEdit edit = new QLineEdit();
            QToolButton button = new QToolButton();

            layout.addWidget(label, i, 0);
            layout.addWidget(edit, i, 1);
            layout.addWidget(button, i, 2);
        }
        return widget;
    }


    public GlassPane() {
        // Initialization
        panels = new QStackedWidget();
        toggle = new QPushButton("&Toggle");
        frontPanel = createPanel("Front Panel");
        backPanel = createPanel("Back Panel");
        selector = new QComboBox();

        // Extended setup
        for (Overlay.Transition t : Overlay.Transition.values()) {
            selector.addItem(t.name());
        }
        // Layout
        QVBoxLayout layout = new QVBoxLayout(this);
        layout.addWidget(panels);
        layout.addWidget(toggle);
        layout.addWidget(selector);
        panels.addWidget(frontPanel);
        panels.addWidget(backPanel);

        // connections
        toggle.clicked.connect(this, "toggle()");
        selector.activated.connect(this, "changeTransition(String)");
    }

    private void toggle() {
        QWidget current = panels.currentWidget();
        QWidget next = panels.widget((panels.currentIndex() + 1) % 2);

        // make sure the layout is up to date...
        next.setGeometry(current.geometry());

        QPixmap startpm = QPixmap.grabWindow(current.winId());
        QPixmap endpm = QPixmap.grabWidget(next);

        Overlay overlay = new Overlay(startpm, endpm);
        overlay.setTransition(transition);
        overlay.setParent(this);
        overlay.setGeometry(panels.geometry());
        overlay.show();

        panels.setCurrentIndex((panels.currentIndex() + 1) % 2);
    }

    private void changeTransition(String type) {
        for (Overlay.Transition t : Overlay.Transition.values()) {
            if (type.equals(t.name())) {
                this.transition = t;
                break;
            }
        }
    }

    private Overlay.Transition transition;
    private QWidget frontPanel;
    private QWidget backPanel;
    private QStackedWidget panels;
    private QPushButton toggle;
    private QComboBox selector;

    public static void main(String args[]) {
        QApplication.initialize(args);

        GlassPane pane = new GlassPane();
        pane.show();

        QApplication.exec();
    }
}
