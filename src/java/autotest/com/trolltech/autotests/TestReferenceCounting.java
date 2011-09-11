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

package com.trolltech.autotests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.junit.Test;

import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.QSignalMapper;
import com.trolltech.qt.core.QSize;
import com.trolltech.qt.core.Qt.KeyboardModifiers;
import com.trolltech.qt.gui.QAbstractItemDelegate;
import com.trolltech.qt.gui.QAbstractItemView;
import com.trolltech.qt.gui.QAbstractProxyModel;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QButtonGroup;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QCompleter;
import com.trolltech.qt.gui.QDirModel;
import com.trolltech.qt.gui.QGuiSignalMapper;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QIcon.Mode;
import com.trolltech.qt.gui.QIconEngineV2;
import com.trolltech.qt.gui.QItemSelection;
import com.trolltech.qt.gui.QItemSelectionModel;
import com.trolltech.qt.gui.QItemSelectionModel.SelectionFlags;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QListWidget;
import com.trolltech.qt.gui.QListWidgetItem;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QRegion;
import com.trolltech.qt.gui.QSortFilterProxyModel;
import com.trolltech.qt.gui.QStackedLayout;
import com.trolltech.qt.gui.QStandardItemModel;
import com.trolltech.qt.gui.QStyleOptionViewItem;
import com.trolltech.qt.gui.QTableView;
import com.trolltech.qt.gui.QValidator;
import com.trolltech.qt.gui.QWidget;

public class TestReferenceCounting extends QApplicationTest {
    public class AssertEquals {
        protected boolean equals() {
            return false;
        }

        public void test() {
            test(10000);
        }
        public void test(int timeout) {
            long stop = System.currentTimeMillis() + timeout;
            Vector<Long> garbage = new Vector<Long>();
            while(stop > System.currentTimeMillis() && !equals()) {

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

    @Test
    public void setItemDelegateForRowToNull() {
        QAbstractItemView view = new QTableView();

        boolean caughtException = false;
        try {
            view.setItemDelegateForRow(0, null);
        } catch (NullPointerException e) {
            caughtException = true;
        }

        assertFalse(caughtException);
    }

    @Test
    public void setItemDelegateForColumnToNull() {
        QAbstractItemView view = new QTableView();

        boolean caughtException = false;
        try {
            view.setItemDelegateForColumn(0, null);
        } catch (NullPointerException e) {
            caughtException = true;
        }

        assertFalse(caughtException);
    }

    @Test
    public void setWidgetMappingToNull() {
        QGuiSignalMapper mapper = new QGuiSignalMapper();

        boolean caughtException = false;
        try {
            mapper.setMapping(new QObject(), (QWidget) null);
        } catch (NullPointerException e) {
            caughtException = true;
        }

        assertFalse(caughtException);
    }

    @Test
    public void setObjectMappingToNull() {
        QSignalMapper mapper = new QSignalMapper();

        boolean caughtException = false;
        try {
            mapper.setMapping(new QObject(), (QObject) null);
        } catch (NullPointerException e) {
            caughtException = true;
        }

        assertFalse(caughtException);
    }

}
