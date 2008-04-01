/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $JAVA_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.autotests;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.QSize;
import com.trolltech.qt.core.Qt.KeyboardModifiers;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.gui.QIcon.Mode;
import com.trolltech.qt.gui.QItemSelectionModel.SelectionFlags;

public class TestReferenceCounting extends QApplicationTest {

    public class AssertEquals {

        protected boolean equals(){
            return false;
        }

        public void test(){
            test(10000);
        }
        public void test(int timeout){
            long stop = System.currentTimeMillis() + timeout;
            Vector<Long> garbage = new Vector<Long>();
            while(stop > System.currentTimeMillis() && !equals()){

                garbage.add(System.currentTimeMillis());

                System.gc();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
            }
            assertTrue(equals());
        }
    }

    private static final int COUNT = 200;

    private int deleted = 0;
    @Test public void testQWidgetAddAction() {
        QWidget w = new QWidget();

        deleted = 0;
        for (int i=0; i<COUNT; ++i) {
            w.addAction(new QAction("action" + i, null) {

                @Override
                public void disposed() {
                    deleted++;
                }


            });
            System.gc();
        }

        assertEquals(0, deleted);
        assertEquals(COUNT, w.actions().size());
        for (int i=0; i<COUNT; ++i) {
            assertTrue(w.actions().get(i) != null);
            assertEquals("action" + i, w.actions().get(i).text());
        }
    }

    @Test public void testQWidgetInsertAction() {
        QWidget w = new QWidget();

        deleted = 0;
        for (int i=0; i<COUNT; ++i) {
            w.insertAction(null, new QAction("action" + i, null) {

                @Override
                public void disposed() {
                    deleted++;
                }


            });
            System.gc();
        }

        assertEquals(0, deleted);
        assertEquals(COUNT, w.actions().size());
        for (int i=0; i<COUNT; ++i) {
            assertTrue(w.actions().get(i) != null);
            assertEquals("action" + i, w.actions().get(i).text());
        }
    }



    @Test public void testQWidgetAddActionDuplicate() {
        QWidget w = new QWidget();

        deleted = 0;

        {
            QAction act = new QAction("action", null) {
                @Override
                public void disposed() {
                    deleted++;
                }
            };

            w.addAction(act);
            w.addAction(act);

            assertEquals(1, w.actions().size());

            w.removeAction(act);
            act = null;
            assertEquals(0, w.actions().size());
        }
        new AssertEquals() {
            @Override
            protected boolean equals() {
                return 1 == deleted;
            }
        }.test();
    }

    @Test public void testQWidgetActionsNull() {
        QWidget w = new QWidget();
        w.addAction(null);
        w.addActions(null);
        w.removeAction(null);
        assertEquals(0, w.actions().size());
    }

    @Test public void testQWidgetAddActions() {
        QWidget w = new QWidget();

        deleted = 0;
        for (int i=0; i<COUNT; ++i) {
            {
                List<QAction> actions = new LinkedList<QAction>();
                actions.add(new QAction("action" + i, null) {
                    @Override
                    public void disposed() {
                        deleted++;
                    }
                });
                w.addActions(actions);
            }
            System.gc();
        }

        assertEquals(0, deleted);
        assertEquals(COUNT, w.actions().size());
        for (int i=0; i<COUNT; ++i) {
            assertTrue(w.actions().get(i) != null);
            assertEquals("action" + i, w.actions().get(i).text());
        }
    }

    @Test public void testQWidgetInsertActions() {
        QWidget w = new QWidget();

        deleted = 0;
        for (int i=0; i<COUNT; ++i) {
            {
                List<QAction> actions = new LinkedList<QAction>();
                actions.add(new QAction("action" + i, null) {
                    @Override
                    public void disposed() {
                        deleted++;
                    }
                });
                w.insertActions(null, actions);
            }
            System.gc();
        }

        assertEquals(0, deleted);
        assertEquals(COUNT, w.actions().size());
        for (int i=0; i<COUNT; ++i) {
            assertTrue(w.actions().get(i) != null);
            assertEquals("action" + i, w.actions().get(i).text());
        }
    }

    @Test public void testQWidgetRemoveAction() {
        final QWidget w = new QWidget();

        deleted = 0;
        for (int i=0; i<COUNT; ++i) {
            {
                QAction act = new QAction("action" + i, null) {
                    @Override
                    public void disposed() {
                        deleted++;
                    }
                };
                w.addAction(act);
                w.removeAction(act);
            }
            System.gc();
        }
        new AssertEquals() {
            @Override
            protected boolean equals() {
                return COUNT == deleted;
            }
        }.test();
        new AssertEquals() {
            @Override
            protected boolean equals() {
                return 0 == w.actions().size();
            }
        }.test();
    }

    @Test public void testQAbstractProxyModelSetSourceModel() {
        deleted = 0;

        QAbstractProxyModel proxy = new QAbstractProxyModel() {

            @Override
            public QModelIndex mapFromSource(QModelIndex sourceIndex) {

                return null;
            }

            @Override
            public QModelIndex mapToSource(QModelIndex proxyIndex) {

                return null;
            }

            @Override
            public int columnCount(QModelIndex parent) {

                return 0;
            }

            @Override
            public QModelIndex index(int row, int column, QModelIndex parent) {

                return null;
            }

            @Override
            public QModelIndex parent(QModelIndex child) {

                return null;
            }

            @Override
            public int rowCount(QModelIndex parent) {

                return 0;
            }

        };

        {
            QStandardItemModel model = new QStandardItemModel() {
                @Override
                public void disposed() {
                    deleted++;
                }
            };

            model.setObjectName("source model");

            proxy.setSourceModel(model);
        }

        long millis = System.currentTimeMillis();
        while (System.currentTimeMillis() - millis < 1000)
            System.gc();

        assertEquals(0, deleted);
        assertEquals("source model", proxy.sourceModel().objectName());
    }

    @Test public void testQAbstractProxyModelSetSourceModelNull() {
        deleted = 0;

        final QAbstractProxyModel proxy = new QAbstractProxyModel() {

            @Override
            public QModelIndex mapFromSource(QModelIndex sourceIndex) {

                return null;
            }

            @Override
            public QModelIndex mapToSource(QModelIndex proxyIndex) {

                return null;
            }

            @Override
            public int columnCount(QModelIndex parent) {

                return 0;
            }

            @Override
            public QModelIndex index(int row, int column, QModelIndex parent) {

                return null;
            }

            @Override
            public QModelIndex parent(QModelIndex child) {

                return null;
            }

            @Override
            public int rowCount(QModelIndex parent) {

                return 0;
            }

        };

        {
            QStandardItemModel model = new QStandardItemModel() {
                @Override
                public void disposed() {
                    deleted++;
                }
            };


            model.setObjectName("source model");

            proxy.setSourceModel(model);
            proxy.setSourceModel(null);

            model = null;
        }

        new AssertEquals() {
            @Override
            protected boolean equals() {
                return 1 == deleted;
            }
        }.test();

        new AssertEquals() {
            @Override
            protected boolean equals() {
                return null == proxy.sourceModel();
            }
        }.test();
    }

    @Test public void testQSortFilterProxyModelSetSourceModel() {
        deleted = 0;

        QSortFilterProxyModel proxy = new QSortFilterProxyModel() {

            @Override
            public QModelIndex mapFromSource(QModelIndex sourceIndex) {

                return null;
            }

            @Override
            public QModelIndex mapToSource(QModelIndex proxyIndex) {

                return null;
            }

            @Override
            public int columnCount(QModelIndex parent) {

                return 0;
            }

            @Override
            public QModelIndex index(int row, int column, QModelIndex parent) {

                return null;
            }

            @Override
            public QModelIndex parent(QModelIndex child) {

                return null;
            }

            @Override
            public int rowCount(QModelIndex parent) {

                return 0;
            }

        };

        {
            QStandardItemModel model = new QStandardItemModel() {
                @Override
                public void disposed() {
                    deleted++;
                }
            };

            model.setObjectName("source model");

            proxy.setSourceModel(model);
        }

        long millis = System.currentTimeMillis();
        while (System.currentTimeMillis() - millis < 1000)
            System.gc();

        assertEquals(0, deleted);
        assertEquals("source model", proxy.sourceModel().objectName());
    }

    @Test public void testQSortFilterProxyModelSetSourceModelNull() {
        deleted = 0;

        final QSortFilterProxyModel proxy = new QSortFilterProxyModel() {

            @Override
            public QModelIndex mapFromSource(QModelIndex sourceIndex) {

                return null;
            }

            @Override
            public QModelIndex mapToSource(QModelIndex proxyIndex) {

                return null;
            }

            @Override
            public int columnCount(QModelIndex parent) {

                return 0;
            }

            @Override
            public QModelIndex index(int row, int column, QModelIndex parent) {

                return null;
            }

            @Override
            public QModelIndex parent(QModelIndex child) {

                return null;
            }

            @Override
            public int rowCount(QModelIndex parent) {

                return 0;
            }

        };

        {
            QStandardItemModel model = new QStandardItemModel() {
                @Override
                public void disposed() {
                    deleted++;
                }
            };

            model.setObjectName("source model");

            proxy.setSourceModel(model);
            proxy.setSourceModel(null);

            model = null;
        }

        new AssertEquals() {
            @Override
            protected boolean equals() {
                return 1 == deleted;
            }
        }.test();

        new AssertEquals() {
            @Override
            protected boolean equals() {
                return null == proxy.sourceModel();
            }
        }.test();
    }

    @Test public void testQStackedLayoutAddStackedWidget() {
        QStackedLayout layout = new QStackedLayout();

        deleted = 0;
        for (int i=0; i<COUNT; ++i) {
            QWidget widget = new QWidget() {
                @Override
                public void disposed() {
                    deleted++;
                }
            };
            layout.addStackedWidget(widget);

            System.gc();
        }

        assertEquals(COUNT, layout.count());
        assertEquals(0, deleted);
    }

    @Test public void testQStackLayoutRemoveWidget() {
        final QStackedLayout layout = new QStackedLayout();

        deleted = 0;
        for (int i=0; i<COUNT; ++i) {
            QWidget widget = new QWidget() {
                @Override
                public void disposed() {
                    deleted++;
                }
            };
            layout.addStackedWidget(widget);
            layout.removeWidget(widget);

            System.gc();
        }

        new AssertEquals() {
            @Override
            protected boolean equals() {
                return 0 == layout.count();
            }
        }.test();
        new AssertEquals() {
            @Override
            protected boolean equals() {
                return COUNT == deleted;
            }
        }.test();
    }

    @Test public void testQStackLayoutAddWidget() {
        QStackedLayout layout = new QStackedLayout();

        deleted = 0;
        for (int i=0; i<COUNT; ++i) {
            QWidget widget = new QWidget() {
                @Override
                public void disposed() {
                    deleted++;
                }
            };
            layout.addWidget(widget);

            System.gc();
        }

        long millis = System.currentTimeMillis();
        while (System.currentTimeMillis() - millis < 1000)
            System.gc();

        assertEquals(COUNT, layout.count());
        assertEquals(0, deleted);
    }

    @Test public void testQStackLayoutAddRemoveWidget() {
        final QStackedLayout layout = new QStackedLayout();

        deleted = 0;
        for (int i=0; i<COUNT; ++i) {
            QWidget widget = new QWidget() {
                @Override
                public void disposed() {
                    deleted++;
                }
            };
            layout.addWidget(widget);
            layout.removeWidget(widget);

            System.gc();
        }

        new AssertEquals() {
            @Override
            protected boolean equals() {
                return 0 == layout.count();
            }
        }.test();
        new AssertEquals() {
            @Override
            protected boolean equals() {
                return COUNT == deleted;
            }
        }.test();
    }

    @Test public void testQComboBoxSetCompleter() {
        QComboBox box = new QComboBox();
        QLineEdit lineEdit = new QLineEdit();
        box.setLineEdit(lineEdit);

        deleted = 0;
        {
            QCompleter completer = new QCompleter() {
                @Override
                public void disposed() {
                    deleted++;
                }
            };
            box.setCompleter(completer);
        }

        long millis = System.currentTimeMillis();
        while (System.currentTimeMillis() - millis < 1000)
            System.gc();

        assertTrue(box.completer() != null);
        assertEquals(0, deleted);
    }

    @Test public void testQComboBoxEditableSetCompleter() {
        QComboBox box = new QComboBox();
        box.setEditable(true);

        deleted = 0;
        {
            QCompleter completer = new QCompleter() {
                @Override
                public void disposed() {
                    deleted++;
                }
            };
            box.setCompleter(completer);
        }

        long millis = System.currentTimeMillis();
        while (System.currentTimeMillis() - millis < 1000)
            System.gc();

        assertTrue(box.completer() != null);
        assertEquals(0, deleted);
    }


    @Test public void testQComboBoxSetCompleterNull() {
        final QComboBox box = new QComboBox();
        QLineEdit lineEdit = new QLineEdit();
        box.setLineEdit(lineEdit);

        deleted = 0;
        {
            QCompleter completer = new QCompleter() {
                @Override
                public void disposed() {
                    deleted++;
                }
            };
            box.setCompleter(completer);
            box.setCompleter(null);
            completer = null;
        }

        new AssertEquals() {
            @Override
            protected boolean equals() {
                return box.completer() == null;
            }
        }.test();
        new AssertEquals() {
            @Override
            protected boolean equals() {
                return 1 == deleted;
            }
        }.test();
    }

    @Test public void testQComboBoxSetCompleterNoLineEdit() {
        final QComboBox box = new QComboBox();

        deleted = 0;
        {
            QCompleter completer = new QCompleter() {
                @Override
                public void disposed() {
                    deleted++;
                }
            };
            box.setCompleter(completer);
            completer = null;
        }

            long millis = System.currentTimeMillis();
            while (System.currentTimeMillis() - millis < 1000)
                System.gc();

            assertEquals(box.completer(), null); // the completer wil newer be set because of missing line edit.
            assertEquals(0, deleted); // and not deleted, since it has been set.
    }

    @Test public void testQLineEditSetCompleter() {
        QLineEdit lineEdit = new QLineEdit();

        deleted = 0;
        {
            QCompleter completer = new QCompleter() {
                @Override
                public void disposed() {
                    deleted++;
                }
            };
            lineEdit.setCompleter(completer);
        }

        long millis = System.currentTimeMillis();
        while (System.currentTimeMillis() - millis < 1000)
            System.gc();

        assertTrue(lineEdit.completer() != null);
        assertEquals(0, deleted);
    }

    @Test public void testQLineEditSetCompleterNull() {
        final QLineEdit lineEdit = new QLineEdit();

        deleted = 0;
        {
            QCompleter completer = new QCompleter() {
                @Override
                public void disposed() {
                    deleted++;
                }
            };
            lineEdit.setCompleter(completer);
            lineEdit.setCompleter(null);

            completer = null;
        }

        new AssertEquals() {
            @Override
            protected boolean equals() {
                return lineEdit.completer() == null;
            }
        }.test();
        new AssertEquals() {
            @Override
            protected boolean equals() {
                return 1 == deleted;
            }
        }.test();
    }

    @Test public void testQComboBoxLineEditSetCompleterNull() {
        QComboBox box = new QComboBox();
        final QLineEdit lineEdit = new QLineEdit();

        box.setLineEdit(lineEdit);

        deleted = 0;
        {
            QCompleter completer = new QCompleter() {
                @Override
                public void disposed() {
                    deleted++;
                }
            };
            lineEdit.setCompleter(completer);
            box.lineEdit().setCompleter(null);

            completer = null;
        }

        new AssertEquals() {
            @Override
            protected boolean equals() {
                return 1 == deleted;
            }
        }.test();

        new AssertEquals() {
            @Override
            protected boolean equals() {
                return lineEdit.completer() == null;
            }
        }.test();
    }

    @Test public void testQLineEditComboBoxSetCompleter() {
        QComboBox box = new QComboBox();
        QLineEdit lineEdit = new QLineEdit();

        box.setLineEdit(lineEdit);

        deleted = 0;
        {
            QCompleter completer = new QCompleter() {
                @Override
                public void disposed() {
                    deleted++;
                }
            };
            box.lineEdit().setCompleter(completer);
        }

        long millis = System.currentTimeMillis();
        while (System.currentTimeMillis() - millis < 1000)
            System.gc();

        assertTrue(lineEdit.completer() != null);
        assertEquals(0, deleted);
    }

    @Test public void testQLineEditComboBoxSetCompleterNull() {
        QComboBox box = new QComboBox();
        final QLineEdit lineEdit = new QLineEdit();

        box.setLineEdit(lineEdit);

        deleted = 0;
        {
            QCompleter completer = new QCompleter() {
                @Override
                public void disposed() {
                    deleted++;
                }
            };
            box.lineEdit().setCompleter(completer);
            lineEdit.setCompleter(null);
            completer = null;
        }

        new AssertEquals() {
            @Override
            protected boolean equals() {
                return lineEdit.completer() == null;
            }
        }.test();
        new AssertEquals() {
            @Override
            protected boolean equals() {
                return 1 == deleted;
            }
        }.test();
    }


    @Test public void testQComboBoxSetItemDelegate() {
        QComboBox box = new QComboBox();

        deleted = 0;
        {
            QAbstractItemDelegate delegate = new QAbstractItemDelegate() {

                @Override
                public void paint(QPainter painter, QStyleOptionViewItem option, QModelIndex index) {
                }

                @Override
                public QSize sizeHint(QStyleOptionViewItem option, QModelIndex index) {
                    return null;
                }

                @Override
                public void disposed() {
                    deleted++;
                }

            };

            box.setItemDelegate(delegate);
        }

        long millis = System.currentTimeMillis();
        while (System.currentTimeMillis() - millis < 1000)
            System.gc();

        assertTrue(box.itemDelegate() != null);
        assertEquals(0, deleted);
    }

    @Test public void testQComboBoxSetItemDelegateNull() {
        QComboBox box = new QComboBox();

        deleted = 0;
        {
            QAbstractItemDelegate delegate = new QAbstractItemDelegate() {

                @Override
                public void paint(QPainter painter, QStyleOptionViewItem option, QModelIndex index) {
                }

                @Override
                public QSize sizeHint(QStyleOptionViewItem option, QModelIndex index) {
                    return null;
                }

                @Override
                public void disposed() {
                    deleted++;
                }

            };
            QAbstractItemDelegate delegate2 = new QAbstractItemDelegate() {

                @Override
                public void paint(QPainter painter, QStyleOptionViewItem option, QModelIndex index) {
                }

                @Override
                public QSize sizeHint(QStyleOptionViewItem option, QModelIndex index) {
                    return null;
                }

                @Override
                public void disposed() {
                    deleted++;
                }

            };


            box.setItemDelegate(delegate);
            box.setItemDelegate(delegate2);
        }

        long millis = System.currentTimeMillis();
        while (System.currentTimeMillis() - millis < 1000)
            System.gc();

        assertTrue(box.itemDelegate() != null);
        assertEquals(1, deleted);
    }

    @Test public void testQComboBoxSetModel() {
        QComboBox box = new QComboBox();

        deleted = 0;
        {
            QStandardItemModel model = new QStandardItemModel() {
                @Override
                public void disposed() {
                    deleted++;
                }
            };
            box.setModel(model);
        }

        long millis = System.currentTimeMillis();
        while (System.currentTimeMillis() - millis < 1000)
            System.gc();

        assertTrue(box.model() != null);
        assertEquals(0, deleted);

    }

    @Test public void testQComboBoxSetValidator() {
        QComboBox box = new QComboBox();
        box.setEditable(true);

        deleted = 0;
        {
            QValidator validator = new QValidator((QObject)null) {
                @Override
                public void disposed() {
                    deleted++;
                }

                @Override
                public State validate(QValidationData arg__1) {
                    // TODO Auto-generated method stub
                    return null;
                }
            };

            box.setValidator(validator);
        }

        long millis = System.currentTimeMillis();
        while (System.currentTimeMillis() - millis < 1000)
            System.gc();

        assertTrue(box.validator() != null);
        assertEquals(0, deleted);
    }

    @Test public void testQComboBoxSetValidatorNull() {
        final QComboBox box = new QComboBox();
        box.setEditable(true);

        deleted = 0;
        {
            QValidator validator = new QValidator((QObject)null) {
                @Override
                public void disposed() {
                    deleted++;
                }

                @Override
                public State validate(QValidationData arg__1) {
                    // TODO Auto-generated method stub
                    return null;
                }
            };

            box.setValidator(validator);
            box.setValidator(null);

            validator = null;
        }

        new AssertEquals() {
            @Override
            protected boolean equals() {
                return box.validator() == null;
            }
        }.test();
        new AssertEquals() {
            @Override
            protected boolean equals() {
                return 1 == deleted;
            }
        }.test();
    }

    @Test public void testQComboBoxLineEditSetValidator() {
        final QComboBox box = new QComboBox();
        box.setEditable(true);

        deleted = 0;
        {
            QValidator validator = new QValidator((QObject)null) {
                @Override
                public void disposed() {
                    deleted++;
                }

                @Override
                public State validate(QValidationData arg__1) {
                    // TODO Auto-generated method stub
                    return null;
                }
            };

            box.setValidator(validator);
            box.lineEdit().setValidator(null);

            validator = null;
        }

        new AssertEquals() {
            @Override
            protected boolean equals() {
                return box.validator() == null;
            }
        }.test();
        new AssertEquals() {
            @Override
            protected boolean equals() {
              //  return 1 == deleted;  // this wan't work because box will probably remember the validator.
                return 0 == deleted;
            }
        }.test();
    }

    @Test public void testQButtonGroupAddButton() {
        QButtonGroup group = new QButtonGroup();

        deleted = 0;
        for (int i=0; i<COUNT; ++i) {
            group.addButton(new QPushButton("button" + i) {
                @Override
                public void disposed() {
                    deleted++;
                }
            });
            System.gc();
        }

        assertEquals(COUNT, group.buttons().size());
        assertEquals(0, deleted);
        assertEquals("button" + COUNT / 2, group.buttons().get(COUNT / 2).text());

    }

    @Test public void testQButtonGroupAddButtonId() {
        QButtonGroup group = new QButtonGroup();

        deleted = 0;
        for (int i=0; i<COUNT; ++i) {
            group.addButton(new QPushButton("button" + i) {
                @Override
                public void disposed() {
                    deleted++;
                }
            }, i);
            System.gc();
        }

        assertEquals(COUNT, group.buttons().size());
        assertEquals(0, deleted);
        assertEquals(COUNT / 2, group.id(group.buttons().get(COUNT / 2)));
        assertEquals("button" + COUNT / 2, group.buttons().get(COUNT / 2).text());
    }

    @Test public void testQButtonGroupRemoveButton() {
        final QButtonGroup group = new QButtonGroup();

        deleted = 0;
        for (int i=0; i<COUNT; ++i) {
            QPushButton button = new QPushButton("button" + i) {
                @Override
                public void disposed() {
                    deleted++;
                }
            };
            group.addButton(button);
            group.removeButton(button);
            System.gc();
        }

        new AssertEquals() {
            @Override
            protected boolean equals() {
                return group.buttons().size() == 0;
            }
        }.test();
        new AssertEquals() {
            @Override
            protected boolean equals() {
                return COUNT == deleted;
            }
        }.test();
    }

    @Test public void testQAbstractItemViewSetItemDelegate() {
        QAbstractItemView view = new QAbstractItemView() {

            @Override
            protected int horizontalOffset() {
                return 0;
            }

            @Override
            public QModelIndex indexAt(QPoint point) {
                return null;
            }

            @Override
            protected boolean isIndexHidden(QModelIndex index) {
                return false;
            }

            @Override
            protected QModelIndex moveCursor(CursorAction cursorAction, KeyboardModifiers modifiers) {
                return null;
            }

            @Override
            public void scrollTo(QModelIndex index, ScrollHint hint) {
            }

            @Override
            protected void setSelection(QRect rect, SelectionFlags command) {
            }

            @Override
            protected int verticalOffset() {
                return 0;
            }

            @Override
            public QRect visualRect(QModelIndex index) {
                return null;
            }

            @Override
            protected QRegion visualRegionForSelection(QItemSelection selection) {
                return null;
            }

        };

        deleted = 0;
        {
            QAbstractItemDelegate delegate = new QAbstractItemDelegate() {

                @Override
                public void paint(QPainter painter, QStyleOptionViewItem option, QModelIndex index) {
                    // TODO Auto-generated method stub

                }

                @Override
                public QSize sizeHint(QStyleOptionViewItem option, QModelIndex index) {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public void disposed() {
                    deleted++;
                }

            };

            view.setItemDelegate(delegate);
        }

        long millis = System.currentTimeMillis();
        while (System.currentTimeMillis() - millis < 1000)
            System.gc();

        assertTrue(view.itemDelegate() != null);
        assertEquals(0, deleted);

    }

    @Test public void testQAbstractItemViewSetItemDelegateNull() {
        final QAbstractItemView view = new QAbstractItemView() {

            @Override
            protected int horizontalOffset() {
                return 0;
            }

            @Override
            public QModelIndex indexAt(QPoint point) {
                return null;
            }

            @Override
            protected boolean isIndexHidden(QModelIndex index) {
                return false;
            }

            @Override
            protected QModelIndex moveCursor(CursorAction cursorAction, KeyboardModifiers modifiers) {
                return null;
            }

            @Override
            public void scrollTo(QModelIndex index, ScrollHint hint) {
            }

            @Override
            protected void setSelection(QRect rect, SelectionFlags command) {
            }

            @Override
            protected int verticalOffset() {
                return 0;
            }

            @Override
            public QRect visualRect(QModelIndex index) {
                return null;
            }

            @Override
            protected QRegion visualRegionForSelection(QItemSelection selection) {
                return null;
            }

        };

        deleted = 0;
        {
            QAbstractItemDelegate delegate = new QAbstractItemDelegate() {

                @Override
                public void paint(QPainter painter, QStyleOptionViewItem option, QModelIndex index) {
                    // TODO Auto-generated method stub

                }

                @Override
                public QSize sizeHint(QStyleOptionViewItem option, QModelIndex index) {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public void disposed() {
                    deleted++;
                }

            };

            view.setItemDelegate(delegate);
            view.setItemDelegate(null);

            delegate = null;
        }

        new AssertEquals() {
            @Override
            protected boolean equals() {
                return view.itemDelegate() == null;
            }
        }.test();
        new AssertEquals() {
            @Override
            protected boolean equals() {
                return 1 == deleted;
            }
        }.test();
    }

    @Test public void testQAbstractItemViewSetModel() {
        QAbstractItemView view = new QAbstractItemView() {

            @Override
            protected int horizontalOffset() {
                return 0;
            }

            @Override
            public QModelIndex indexAt(QPoint point) {
                return null;
            }

            @Override
            protected boolean isIndexHidden(QModelIndex index) {
                return false;
            }

            @Override
            protected QModelIndex moveCursor(CursorAction cursorAction, KeyboardModifiers modifiers) {
                return null;
            }

            @Override
            public void scrollTo(QModelIndex index, ScrollHint hint) {
            }

            @Override
            protected void setSelection(QRect rect, SelectionFlags command) {
            }

            @Override
            protected int verticalOffset() {
                return 0;
            }

            @Override
            public QRect visualRect(QModelIndex index) {
                return null;
            }

            @Override
            protected QRegion visualRegionForSelection(QItemSelection selection) {
                return null;
            }

        };

        deleted = 0;
        {
            QStandardItemModel model = new QStandardItemModel() {
                @Override
                public void disposed() {
                    deleted++;
                }
            };

            view.setModel(model);
        }

        long millis = System.currentTimeMillis();
        while (System.currentTimeMillis() - millis < 1000)
            System.gc();

        assertTrue(view.model() != null);
        assertEquals(0, deleted);
    }

    @Test public void testQAbstractItemViewSetSelectionModelThenSetModel() {
        final QAbstractItemView view = new QAbstractItemView() {

            @Override
            protected int horizontalOffset() {
                return 0;
            }

            @Override
            public QModelIndex indexAt(QPoint point) {
                return null;
            }

            @Override
            protected boolean isIndexHidden(QModelIndex index) {
                return false;
            }

            @Override
            protected QModelIndex moveCursor(CursorAction cursorAction, KeyboardModifiers modifiers) {
                return null;
            }

            @Override
            public void scrollTo(QModelIndex index, ScrollHint hint) {
            }

            @Override
            protected void setSelection(QRect rect, SelectionFlags command) {
            }

            @Override
            protected int verticalOffset() {
                return 0;
            }

            @Override
            public QRect visualRect(QModelIndex index) {
                return null;
            }

            @Override
            protected QRegion visualRegionForSelection(QItemSelection selection) {
                return null;
            }

        };

        deleted = 0;
        {
            QItemSelectionModel model = new QItemSelectionModel(view.model()) {
                @Override
                public void disposed() {
                    deleted++;
                }
            };

            view.setSelectionModel(model);
            view.setModel(new QStandardItemModel());

            model = null;
        }

        new AssertEquals() {
            @Override
            protected boolean equals() {
                return view.selectionModel() != null;
            }
        }.test();
        new AssertEquals() {
            @Override
            protected boolean equals() {
                return 1 == deleted;
            }
        }.test();

    }

    @Test public void testQAbstractItemViewSetSelectionModel() {
        QAbstractItemView view = new QAbstractItemView() {

            @Override
            protected int horizontalOffset() {
                return 0;
            }

            @Override
            public QModelIndex indexAt(QPoint point) {
                return null;
            }

            @Override
            protected boolean isIndexHidden(QModelIndex index) {
                return false;
            }

            @Override
            protected QModelIndex moveCursor(CursorAction cursorAction, KeyboardModifiers modifiers) {
                return null;
            }

            @Override
            public void scrollTo(QModelIndex index, ScrollHint hint) {
            }

            @Override
            protected void setSelection(QRect rect, SelectionFlags command) {
            }

            @Override
            protected int verticalOffset() {
                return 0;
            }

            @Override
            public QRect visualRect(QModelIndex index) {
                return null;
            }

            @Override
            protected QRegion visualRegionForSelection(QItemSelection selection) {
                return null;
            }

        };

        deleted = 0;
        {
            view.setModel(new QDirModel());

            QItemSelectionModel model = new QItemSelectionModel(view.model()) {
                @Override
                public void disposed() {
                    deleted++;
                }
            };

            view.setSelectionModel(model);

            model = null;
        }

        long millis = System.currentTimeMillis();
        while (System.currentTimeMillis() - millis < 1000)
            System.gc();

        assertTrue(view.selectionModel() != null);
        assertEquals(0, deleted);


    }

    private static int deletedEngines = 0;
    private static class MyIconEngine extends QIconEngineV2 {
        private int i;
        public MyIconEngine(int i) {
            this.i = i;
        }

        @Override
        public void paint(QPainter painter, QRect rect, Mode mode, com.trolltech.qt.gui.QIcon.State state) {

        }

        @Override
        public QIconEngineV2 clone() {
            return new MyIconEngine(i);
        }

        @Override
        protected void disposed() {
            deletedEngines++;
        }

    }

    private static class MyIcon extends QIcon {

        public MyIcon(int i) {
            super(new MyIconEngine(i));
        }
    }

    @Test public void testIconEngine() {
        QListWidget w = new QListWidget();

        deletedEngines = 0;

        for (int i=0; i<100; ++i) {
            w.addItem(new QListWidgetItem(new MyIcon(i), "" + i));
            System.gc();
        }

        // Don't crash
        w.show();
        assertEquals(0, deletedEngines);
    }
}
