package com.trolltech.autotests;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.core.QAbstractItemModel;
import com.trolltech.qt.core.QEvent;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.QSize;
import com.trolltech.qt.core.Qt.KeyboardModifiers;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.gui.QItemSelectionModel.SelectionFlags;

public class TestReferenceCounting extends QApplicationTest {
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
			assertEquals(0, w.actions().size());
		}

		long millis = System.currentTimeMillis();
		while (System.currentTimeMillis() - millis < 1000)
			System.gc();

		assertEquals(1, deleted);
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
		QWidget w = new QWidget();
		
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
		
		long millis = System.currentTimeMillis();
		while (System.currentTimeMillis() - millis < 1000)
			System.gc();
		
		assertEquals(COUNT, deleted);
		assertEquals(0, w.actions().size());
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
			proxy.setSourceModel(null);
		}
		
		long millis = System.currentTimeMillis();
		while (System.currentTimeMillis() - millis < 1000)
			System.gc();
		
		assertEquals(1, deleted);
		assertEquals(null, proxy.sourceModel());
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
			proxy.setSourceModel(null);
		}
		
		long millis = System.currentTimeMillis();
		while (System.currentTimeMillis() - millis < 1000)
			System.gc();
		
		assertEquals(1, deleted);
		assertEquals(null, proxy.sourceModel());
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
			layout.removeWidget(widget);
			
			System.gc();
		}

		long millis = System.currentTimeMillis();
		while (System.currentTimeMillis() - millis < 1000)
			System.gc();
		
		assertEquals(0, layout.count());
		assertEquals(COUNT, deleted);
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
			layout.removeWidget(widget);
			
			System.gc();
		}

		long millis = System.currentTimeMillis();
		while (System.currentTimeMillis() - millis < 1000)
			System.gc();
		
		assertEquals(0, layout.count());
		assertEquals(COUNT, deleted);
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
			box.setCompleter(null);
		}
		
		long millis = System.currentTimeMillis();
		while (System.currentTimeMillis() - millis < 1000)
			System.gc();
		
		assertTrue(box.completer() == null);
		assertEquals(1, deleted);
		
	}
	
	@Test public void testQComboBoxSetCompleterNoLineEdit() {
		QComboBox box = new QComboBox();
		
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
		
		assertTrue(box.completer() == null);
		assertEquals(1, deleted);		
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
			lineEdit.setCompleter(null);
		}
		
		long millis = System.currentTimeMillis();
		while (System.currentTimeMillis() - millis < 1000)
			System.gc();
		
		assertTrue(lineEdit.completer() == null);
		assertEquals(1, deleted);		
	}
	
	@Test public void testQComboBoxLineEditSetCompleterNull() {
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
			lineEdit.setCompleter(completer);
			box.lineEdit().setCompleter(null);
		}
		
		long millis = System.currentTimeMillis();
		while (System.currentTimeMillis() - millis < 1000)
			System.gc();
		
		assertTrue(lineEdit.completer() == null);
		assertEquals(1, deleted);		
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
			lineEdit.setCompleter(null);
		}
		
		long millis = System.currentTimeMillis();
		while (System.currentTimeMillis() - millis < 1000)
			System.gc();
		
		assertTrue(lineEdit.completer() == null);
		assertEquals(1, deleted);		
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
			box.setValidator(null);
		}
		
		long millis = System.currentTimeMillis();
		while (System.currentTimeMillis() - millis < 1000)
			System.gc();
		
		assertTrue(box.validator() == null);
		assertEquals(1, deleted);
	}
	
	@Test public void testQComboBoxLineEditSetValidator() {
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
			box.lineEdit().setValidator(null);
		}

		long millis = System.currentTimeMillis();
		while (System.currentTimeMillis() - millis < 1000)
			System.gc();
				
		assertTrue(box.validator() == null);
		assertEquals(1, deleted);
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
		assertEquals("button10", group.buttons().get(10).text());
		
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
		assertEquals(10, group.id(group.buttons().get(10)));
		assertEquals("button10", group.buttons().get(10).text());		
	}
	
	@Test public void testQButtonGroupRemoveButton() {
		QButtonGroup group = new QButtonGroup();
		
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
				
		long millis = System.currentTimeMillis();
		while (System.currentTimeMillis() - millis < 1000)
			System.gc();

		assertEquals(0, group.buttons().size());
		assertEquals(COUNT, deleted);
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
			view.setItemDelegate(null);
		}
		
		long millis = System.currentTimeMillis();
		while (System.currentTimeMillis() - millis < 1000)
			System.gc();
		
		assertTrue(view.itemDelegate() == null);
		assertEquals(1, deleted);
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
			QItemSelectionModel model = new QItemSelectionModel(view.model()) {
				@Override
				public void disposed() {
					deleted++;
				}
			};
			
			view.setSelectionModel(model);
			view.setModel(new QStandardItemModel());
		}
		
		long millis = System.currentTimeMillis();
		while (System.currentTimeMillis() - millis < 1000)
			System.gc();
		
		assertTrue(view.selectionModel() != null);
		assertEquals(1, deleted);
		
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
			QItemSelectionModel model = new QItemSelectionModel(view.model()) {
				@Override
				public void disposed() {
					deleted++;
				}
			};
			
			view.setSelectionModel(model);
		}
		
		long millis = System.currentTimeMillis();
		while (System.currentTimeMillis() - millis < 1000)
			System.gc();
		
		assertTrue(view.selectionModel() != null);
		assertEquals(0, deleted);
		
	}

}
