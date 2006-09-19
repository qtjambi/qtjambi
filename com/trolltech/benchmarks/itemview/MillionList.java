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

package com.trolltech.benchmarks.itemview;

import javax.swing.*;

import com.trolltech.qt.gui.*;
import com.trolltech.qt.core.*;

public class MillionList {
	
	public static final int ITEM_COUNT = 100000;
	
	public static class QtList extends QListView {
		public long populateTime;
		public long setModelTime;
		
		private QStandardItemModel model = new QStandardItemModel(ITEM_COUNT, 1);
		// private QStringListModel model = new QStringListModel();
		
		public QtList() {
			populate();
		}
		
		private void populate() {
			long t1 = System.currentTimeMillis();
			
//			List<String> strings = new LinkedList<String>();
			
			for (int i=0; i<ITEM_COUNT; ++i) {
				// strings.add(String.valueOf(i));
				QModelIndex index = model.index(i, 0);
                
//                System.out.printf("row=%d, col=%d\n", index.row(), index.column());
//                
//                
//                
//                System.out.println(" - internalId=" + index.internalId());
//                System.out.println(" - model=" + index.model());
                
				model.setData(index, i);
			}
			
			
			populateTime = System.currentTimeMillis() - t1;
			
			System.out.println("Population took: " + populateTime);
			
			setModel(model);
			
			setModelTime = System.currentTimeMillis() - populateTime - t1;
			System.out.println("Setting the model: " + setModelTime);
		}
	}
	
    @SuppressWarnings("unused")
	private static class FakeModelIndex {
		int row, col;
		Object data;
		
		public FakeModelIndex(int row, int col) {
			this.row = row;
			this.col = col;
		}
	}
	
	public static class SwingList extends JList {
        private static final long serialVersionUID = 1L;
        public long populateTime;
		public long setModelTime;
		
		private DefaultListModel model = new DefaultListModel();
		
		public SwingList() {
			populate();
		}
		
		private void populate() {
			long t1 = System.currentTimeMillis();
			
			
			for (int i=0; i<ITEM_COUNT; ++i) {
//				FakeModelIndex index = new FakeModelIndex(i, 0);
				model.add(i, i);
			}
			
			populateTime = System.currentTimeMillis() - t1;
			
			System.out.println("Population took: " + populateTime);
			
			setModel(model);
			
			setModelTime = System.currentTimeMillis() - populateTime - t1;
			
			System.out.println("Setting model took: " + setModelTime);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		QApplication.initialize(args);
		
		if (args.length >= 1 && (args[0].contains("java") || args[0].contains("swing"))) {
			// run swing test
			SwingList l = new SwingList();
			JFrame f = new JFrame();
			f.getContentPane().add(l);
			f.pack();
			f.setVisible(true);
		} else {
			QtList l = new QtList();
			l.show();			
		}
		
		QApplication.exec();
	}

}
