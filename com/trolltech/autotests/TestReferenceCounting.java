package com.trolltech.autotests;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import com.trolltech.qt.core.QEvent;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.gui.*;

public class TestReferenceCounting extends QApplicationTest {
	private static final int COUNT = 200;
	
	private int deleted = 0;
	/*@Test public void testQWidgetAddAction() {
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
	
	@Test public void testQAbstractProxyModelSetSourceModel() {
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

	@Test public void testQAbstractProxyModelSetSourceModelNull() {
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
	}*/
	
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


}
