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

package com.trolltech.extensions.awt;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.util.List;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;


public class QAwtWidget extends Canvas {
	private static boolean guard = false;
	
	private static class QUpdateAwtWidgetEvent extends QEvent {
		private final static QEvent.Type UPDATE_AWT_WIDGET_EVENT = QEvent.Type.resolve(QEvent.Type.User.value() + 2);
		
		public QUpdateAwtWidgetEvent() {
			super(UPDATE_AWT_WIDGET_EVENT);
		}
	}
	
	private static class QFindChildAndPostMouseEvent extends QEvent {
		private final static QEvent.Type FIND_CHILD_AND_POST_MOUSE_EVENT = QEvent.Type.resolve(QEvent.Type.User.value() + 1);
				
		public QFindChildAndPostMouseEvent() {
			super(FIND_CHILD_AND_POST_MOUSE_EVENT);
		}
		
		private QEvent.Type actualType = null;
		public void setActualType(QEvent.Type type) { actualType = type; }
		public QEvent.Type actualType() { return actualType; }
		
		private QPoint relativePoint = null;
		public void setRelativePoint(QPoint point) { relativePoint = point; }
		public QPoint relativePoint() { return relativePoint; }
		
		private QWidget topWidget = null;
		public void setTopWidget(QWidget widget) { topWidget = widget; }
		public QWidget topWidget() { return topWidget; }
		
		private Qt.MouseButton mouseButton = null;
		public void setMouseButton(Qt.MouseButton mouseButton) { this.mouseButton = mouseButton; }
		public Qt.MouseButton mouseButton() { return mouseButton; } 

		private Qt.MouseButtons mouseButtons = null;
		public void setMouseButtons(Qt.MouseButtons mouseButtons) { this.mouseButtons = mouseButtons; }
		public Qt.MouseButtons mouseButtons() { return mouseButtons; }
		
		private Qt.KeyboardModifiers keyboardModifiers = null;
		public void setKeyboardModifiers(Qt.KeyboardModifiers modifiers) { keyboardModifiers = modifiers; }
		public Qt.KeyboardModifiers keyboardModifiers() { return keyboardModifiers; }
		
		private QWidget getHitWidget(QWidget parentWidget, QPoint relativePoint, QPoint outputPoint) {		
			if (!parentWidget.rect().contains(relativePoint))
				return null;
			
			List<QObject> childrens = parentWidget.children();
			
			for (QObject child : childrens) {
				if (child instanceof QWidget) {
					QWidget childWidget = (QWidget) child;
					QPoint mapped = childWidget.mapFromParent(relativePoint);
					
					// ### Allow overlapping widgets with z vals
					QWidget found = null;
					if (childWidget.rect().contains(mapped))
						found = getHitWidget(childWidget, mapped, outputPoint);
					
					if (found != null) {
						outputPoint.setX(mapped.x());
						outputPoint.setY(mapped.y());
						return found;
					}
				}
			}
			
			outputPoint.setX(relativePoint.x());
			outputPoint.setY(relativePoint.y());
			return parentWidget;
		}		

		private QWidget hitWidget = null;
		public QWidget hitWidget() {
			return hitWidget;
		}
		
		public QMouseEvent mouseEvent() {
			QPoint repositionedPoint = new QPoint();
			hitWidget = getHitWidget(topWidget(), relativePoint(), repositionedPoint);
			
			if (hitWidget == null)
				return null;
			
			return new QMouseEvent(actualType(), repositionedPoint, 
					mouseButton(), mouseButtons(), keyboardModifiers());
		}
		
	}
	
	private class QtEventFilter extends QObject {
		public QtEventFilter(QObject parent) {
			super(parent);
		}

		
		
		private boolean hasUpdateAwtEvent = false;
		
		@Override
		public boolean eventFilter(QObject receiver, QEvent event) {
			
			System.out.println("instance thread: " + thread() + " application thread: " + QCoreApplication.instance().thread());
			
			boolean returned = false;
			if (!(receiver instanceof QWidget)) 
				return false;
								
			if (!guard) try {
				guard = true;

				if (event instanceof QUpdateAwtWidgetEvent) {
					updateAwtWidget();
					hasUpdateAwtEvent = false;
					return true;
				}
				
				
				if (event instanceof QChildEvent) {
					QObject child = ((QChildEvent) event).child();
					switch (event.type()) {
					case ChildAdded:
						child.installEventFilter(this);
						break;
					case ChildRemoved:
						child.removeEventFilter(this);
						break;
					default:
						// Don't care
					}
					updateAwtWidget();
				}
				
				if (event instanceof QFindChildAndPostMouseEvent) {
					QMouseEvent mouseEvent = ((QFindChildAndPostMouseEvent) event).mouseEvent();
					if (mouseEvent != null) {
						QApplication.postEvent(((QFindChildAndPostMouseEvent)event).hitWidget(), mouseEvent);
						if (!hasUpdateAwtEvent) {
							QApplication.postEvent(containedWidget, new QUpdateAwtWidgetEvent());
							hasUpdateAwtEvent = true;
						}
					}
										
					return true;			
				}
								
				if (event instanceof QResizeEvent) {
					sizeHint = containedWidget.sizeHint();
					minimumSizeHint = containedWidget.minimumSizeHint();
					Rectangle rectangle = getBounds();
					
					setBounds(new Rectangle(rectangle.x, rectangle.y, 
							sizeHint.width() > rectangle.width || true ? sizeHint.width() : rectangle.width, 
						    sizeHint.height() > rectangle.height || true ? sizeHint.height() : rectangle.height));
					getParent().doLayout();
					//updateAwtWidget();
				}
				
				if (event instanceof QPaintEvent) {
					//
				}
							
						
				updateAwtWidget();
			} finally {
				guard = false;
			}
			
			return returned;
		}

		private void updateAwtWidget() {
			containedWidget.updateGeometry();
			synchronized (widgetAppearance) {
				widgetAppearance.dispose();
				widgetAppearance = QPixmap.grabWidget(containedWidget);
			}
			repaint();
		}

		
	}
	
	private class QAwtMouseMotionListener implements MouseMotionListener {

		public void mouseDragged(MouseEvent awtEvent) {
			QFindChildAndPostMouseEvent event = new QFindChildAndPostMouseEvent();
			event.setActualType(QEvent.Type.MouseMove);
			event.setTopWidget(containedWidget);
			event.setRelativePoint(pointToQPoint(awtEvent.getPoint()));
			event.setMouseButton(eventToMouseButton(awtEvent));
			event.setMouseButtons(eventToMouseButtons(awtEvent));
			event.setKeyboardModifiers(eventToKeyboardModifiers(awtEvent));
								
			QApplication.postEvent(containedWidget, event);						
		}

		public void mouseMoved(MouseEvent awtEvent) {
			QFindChildAndPostMouseEvent event = new QFindChildAndPostMouseEvent();
			event.setActualType(QEvent.Type.MouseMove);
			event.setTopWidget(containedWidget);
			event.setRelativePoint(pointToQPoint(awtEvent.getPoint()));
			event.setMouseButton(eventToMouseButton(awtEvent));
			event.setMouseButtons(eventToMouseButtons(awtEvent));
			event.setKeyboardModifiers(eventToKeyboardModifiers(awtEvent));
								
			QApplication.postEvent(containedWidget, event);									
		}
		
	}
		
	private class QAwtMouseListener implements MouseListener {

		public void mouseClicked(MouseEvent awtEvent) {			
		}

		public void mouseEntered(MouseEvent arg0) {			
		}

		public void mouseExited(MouseEvent arg0) {
		}

		public void mousePressed(MouseEvent awtEvent) {
			QFindChildAndPostMouseEvent event = new QFindChildAndPostMouseEvent();
			event.setActualType(QEvent.Type.MouseButtonPress);
			event.setTopWidget(containedWidget);
			event.setRelativePoint(pointToQPoint(awtEvent.getPoint()));
			event.setMouseButton(eventToMouseButton(awtEvent));
			event.setMouseButtons(eventToMouseButtons(awtEvent));
			event.setKeyboardModifiers(eventToKeyboardModifiers(awtEvent));
								
			QApplication.postEvent(containedWidget, event);
		}

		public void mouseReleased(MouseEvent awtEvent) {
			QFindChildAndPostMouseEvent event = new QFindChildAndPostMouseEvent();
			event.setActualType(QEvent.Type.MouseButtonRelease);
			event.setTopWidget(containedWidget);
			event.setRelativePoint(pointToQPoint(awtEvent.getPoint()));
			event.setMouseButton(eventToMouseButton(awtEvent));
			event.setMouseButtons(eventToMouseButtons(awtEvent));
			event.setKeyboardModifiers(eventToKeyboardModifiers(awtEvent));
								
			QApplication.postEvent(containedWidget, event);			
		}
		
	}

	

	static {
		com.trolltech.qt.Utilities.loadQtLibrary("QtCore");
	    com.trolltech.qt.Utilities.loadQtLibrary("QtGui");
		com.trolltech.qt.Utilities.loadJambiLibrary("qtjambi");
		com.trolltech.qt.Utilities.loadJambiLibrary("qtjambi_jawt");
	}
		
	private QWidget containedWidget;
	private QSize sizeHint;
	private QSize minimumSizeHint;
	@SuppressWarnings("unused") private QPixmap widgetAppearance;
	private QtEventFilter eventFilter;
	public QAwtWidget(QWidget containedWidget) {
		this.containedWidget = containedWidget;
		sizeHint = containedWidget.sizeHint();
		minimumSizeHint = containedWidget.minimumSizeHint();
		setVisible(true);
		
		addMouseListener(new QAwtMouseListener());
		addMouseMotionListener(new QAwtMouseMotionListener());
		widgetAppearance = QPixmap.grabWidget(containedWidget);
		eventFilter = new QtEventFilter(containedWidget);
		
		installEventFilter(eventFilter, containedWidget);
	}
	
	private void installEventFilter(QtEventFilter eventFilter, QObject filtered) {
		filtered.installEventFilter(eventFilter);				
		List<QObject> childrens = filtered.children();
		for (QObject child : childrens) {
			
			installEventFilter(eventFilter, child);			
		}		
	}

	@SuppressWarnings("unused")
	private void printStackTrace() {
		try {
			throw new RuntimeException("abc");
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Rectangle getBounds() {
		Rectangle rect = super.getBounds();
		return rect;
	}

	
	private QPoint pointToQPoint(Point p) {
		return new QPoint(p.x, p.y);
	}
	
	private Qt.MouseButton eventToMouseButton(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1) 
			return Qt.MouseButton.LeftButton;
		else if (event.getButton() == MouseEvent.BUTTON2)
			return Qt.MouseButton.RightButton;
		else
			return Qt.MouseButton.NoButton;
	}
	
	private Qt.MouseButtons eventToMouseButtons(InputEvent event) {
		Qt.MouseButtons flags = new Qt.MouseButtons(0);
		if ((event.getModifiers() & MouseEvent.BUTTON1) != 0)
			flags.set(Qt.MouseButton.LeftButton);
		if ((event.getModifiers() & MouseEvent.BUTTON2) != 0)
			flags.set(Qt.MouseButton.RightButton);
		
		return flags;
	}
	
	private Qt.KeyboardModifiers eventToKeyboardModifiers(InputEvent event) {
		Qt.KeyboardModifiers modifiers = new Qt.KeyboardModifiers(0);		
		if (event.isAltDown())
			modifiers.set(Qt.KeyboardModifier.AltModifier);
		if (event.isControlDown())
			modifiers.set(Qt.KeyboardModifier.ControlModifier);
		
		return modifiers;
	}
	
	@Override
	public Dimension getPreferredSize() {
		Dimension awtDimension = new Dimension(sizeHint.width(), sizeHint.height());
		return awtDimension;
	}
	
	@Override
	public Dimension getMinimumSize() {
		Dimension awtDimension = new Dimension(minimumSizeHint.width(), minimumSizeHint.height());
		return awtDimension;
	}
	
	@Override
	public Dimension getMaximumSize() {
		Dimension awtDimension = new Dimension(10000, 10000);
		return awtDimension;
	}
	

	@Override
	public void paint(Graphics g) {
		synchronized (widgetAppearance) {
			paintIt(g);
		}
	}
	private native void paintIt(Graphics g);
	
	@Override
	public void setBounds(int x, int y, final int width, final int height) {
		QApplication.invokeLater(new Runnable() {
			public void run() {	
				containedWidget.resize(width, height);
				QPaintEvent event = new QPaintEvent(containedWidget.rect());
				QApplication.postEvent(containedWidget, event);				
			}
		});
				
		super.setBounds(x, y, width, height);
	}
	
	private void setButtonText() {
		button.setText(button.text() + "?");
	}
		
	private static QPushButton button;
	public static void main(String args[]) {
		QApplication.initialize(args);
		
		JFrame f = new JFrame();
		
		GridLayout flayout = new GridLayout(2,2);
		f.setLayout(flayout);
		
		f.setBounds(0, 0, 500, 100);
		
		QWidget w = new QWidget();
		QLabel label = new QLabel("Directory:");
		label.setTextInteractionFlags(Qt.TextInteractionFlag.TextSelectableByMouse);
		
		QLineEdit lineEdit = new QLineEdit();
		button = new QPushButton(w);
		button.setText("Qt button");
		
		QHBoxLayout layout = new QHBoxLayout(w);
		layout.addWidget(label);
		layout.addWidget(lineEdit);
		layout.addWidget(button);
		
		JButton swingButton = new JButton();
		swingButton.setText("Swing button");
		
		Button awtButton = new Button();
		awtButton.setLabel("Awt button");				
		QAwtWidget awtWidget = new QAwtWidget(w);
		button.clicked.connect(awtWidget, "setButtonText()");
		
		f.add(swingButton);
		f.add(awtWidget);
		f.add(awtButton);
		f.setVisible(true);
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		f.pack();
				
		QApplication.exec();
	}

}
