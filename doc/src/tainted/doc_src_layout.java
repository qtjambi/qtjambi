/*   Ported from: doc.src.layout.qdoc
<snip>
//! [0]
        #ifndef CARD_H
        #define CARD_H

        #include <QLayout>
        #include <QList>

        class CardLayout : public QLayout
        {
        public:
            CardLayout(QWidget *parent, int dist)
                : QLayout(parent, 0, dist) {}
            CardLayout(QLayout *parent, int dist)
                : QLayout(parent, dist) {}
            CardLayout(int dist)
                : QLayout(dist) {}
            ~CardLayout();

            void addItem(QLayoutItem *item);
            QSize sizeHint() const;
            QSize minimumSize() const;
            QLayoutItem *itemAt(int) const;
            QLayoutItem *takeAt(int);
            void setGeometry(const QRect &rect);

        private:
            QList<QLayoutItem*> list;
        };
        #endif
//! [0]


//! [1]
        #include "card.h"
//! [1]


//! [2]
        QLayoutItem *CardLayout::itemAt(int idx) const
        {
            // QList::value() performs index checking, and returns 0 if we are
            // outside the valid range
            return list.value(idx);
        }

        QLayoutItem *CardLayout::takeAt(int idx)
        {
            // QList::take does not do index checking
            return idx >= 0 && idx < list.size() ? list.takeAt(idx) : 0;
        }
//! [2]


//! [3]
        void CardLayout::addItem(QLayoutItem *item)
        {
            list.append(item);
        }
//! [3]


//! [4]
        CardLayout::~CardLayout()
        {
            deleteAllItems();
        }
//! [4]


//! [5]
        void CardLayout::setGeometry(const QRect &r)
        {
            QLayout::setGeometry(r);

            if (list.size() == 0)
                return;

            int w = r.width() - (list.count() - 1) * spacing();
            int h = r.height() - (list.count() - 1) * spacing();
            int i = 0;
            while (i < list.size()) {
                QLayoutItem *o = list.at(i);
                QRect geom(r.x() + i * spacing(), r.y() + i * spacing(), w, h);
                o->setGeometry(geom);
                ++i;
            }
        }
//! [5]


//! [6]
        QSize CardLayout::sizeHint() const
        {
            QSize s(0,0);
            int n = list.count();
            if (n > 0)
                s = QSize(100,70); //start with a nice default size
            int i = 0;
            while (i < n) {
                QLayoutItem *o = list.at(i);
                s = s.expandedTo(o->sizeHint());
                ++i;
            }
            return s + n*QSize(spacing(), spacing());
        }

        QSize CardLayout::minimumSize() const
        {
            QSize s(0,0);
            int n = list.count();
            int i = 0;
            while (i < n) {
                QLayoutItem *o = list.at(i);
                s = s.expandedTo(o->minimumSize());
                ++i;
            }
            return s + n*QSize(spacing(), spacing());
        }
//! [6]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class doc_src_layout {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        #ifndef CARD_H
        #define CARD_H

        #include <QLayout>
        #include <QList>

        class CardLayout : public QLayout
        {
        public:
            CardLayout(QWidget arent, int dist)
                : QLayout(parent, 0, dist) {}
            CardLayout(QLayout arent, int dist)
                : QLayout(parent, dist) {}
            CardLayout(int dist)
                : QLayout(dist) {}
            ~CardLayout();

            void addItem(QLayoutItem tem);
            QSize sizeHint();
            QSize minimumSize();
            QLayoutItem temAt(int);
            QLayoutItem akeAt(int);
            void setGeometry(QRect ect);

        private:
            QList<QLayoutItem*> list;
        };
        #endif
//! [0]


//! [1]
        #include "card.h"
//! [1]


//! [2]
        QLayoutItem ardLayout.itemAt(int idx)
        {
            // QList.value() performs index checking, and returns 0 if we are
            // outside the valid range
            return list.value(idx);
        }

        QLayoutItem ardLayout.takeAt(int idx)
        {
            // QList.take does not do index checking
            return idx >= 0 && idx < list.size() ? list.takeAt(idx) : 0;
        }
//! [2]


//! [3]
        void CardLayout.addItem(QLayoutItem tem)
        {
            list.append(item);
        }
//! [3]


//! [4]
        CardLayout.~CardLayout()
        {
            deleteAllItems();
        }
//! [4]


//! [5]
        void CardLayout.setGeometry(QRect )
        {
            QLayout.setGeometry(r);

            if (list.size() == 0)
                return;

            int w = r.width() - (list.count() - 1) * spacing();
            int h = r.height() - (list.count() - 1) * spacing();
            int i = 0;
            while (i < list.size()) {
                QLayoutItem  = list.at(i);
                QRect geom(r.x() + i * spacing(), r.y() + i * spacing(), w, h);
                o.setGeometry(geom);
                ++i;
            }
        }
//! [5]


//! [6]
        QSize CardLayout.sizeHint()
        {
            QSize s(0,0);
            int n = list.count();
            if (n > 0)
                s = QSize(100,70); //start with a nice default size
            int i = 0;
            while (i < n) {
                QLayoutItem  = list.at(i);
                s = s.expandedTo(o.sizeHint());
                ++i;
            }
            return s + nSize(spacing(), spacing());
        }

        QSize CardLayout.minimumSize()
        {
            QSize s(0,0);
            int n = list.count();
            int i = 0;
            while (i < n) {
                QLayoutItem  = list.at(i);
                s = s.expandedTo(o.minimumSize());
                ++i;
            }
            return s + nSize(spacing(), spacing());
        }
//! [6]


    }
}
