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

import com.trolltech.qt.QNativePointer;
import com.trolltech.qt.QPair;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;


public class QAwtWidget extends Canvas {
	private static final long serialVersionUID = 1L;
	private static boolean guard = false;	
		
	private static native QEvent makeEventSpontaneous(QEvent event, long nativeId);
	private static QEvent makeEventSpontaneous(QEvent event) {
		return makeEventSpontaneous(event, event.nativeId());
	}
	
	private static class QUpdateAwtWidgetEvent extends QEvent {
		private final static QEvent.Type UPDATE_AWT_WIDGET_EVENT = QEvent.Type.resolve(QEvent.Type.User.value() + 1);
		
		public QUpdateAwtWidgetEvent() {
			super(UPDATE_AWT_WIDGET_EVENT);
		}
	}
	
	private static class QFindUnderMouseEvent extends QEvent {
		private final static QEvent.Type FIND_UNDER_MOUSE_EVENT = QEvent.Type.resolve(QEvent.Type.User.value() + 2);
		
		public QFindUnderMouseEvent() {
			super(FIND_UNDER_MOUSE_EVENT);
		}
		
		public QFindUnderMouseEvent(QEvent.Type type) {
			super(type);
		}
		
		private QWidget topWidget = null;
		public void setTopWidget(QWidget widget) { topWidget = widget; }
		public QWidget topWidget() { return topWidget; }
		
		private QPoint relativePoint = null;
		public void setRelativePoint(QPoint point) { relativePoint = point; }
		public QPoint relativePoint() { return relativePoint; }
		
		public QWidget findHitWidget() {
			return findHitWidget(topWidget(), relativePoint(), null);
		}
		
		protected QWidget findHitWidget(QWidget parentWidget, QPoint relativePoint, QPoint outputPoint) {		
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
						found = findHitWidget(childWidget, mapped, outputPoint);
					
					if (found != null) {
						if (outputPoint != null) {
							outputPoint.setX(mapped != null ? mapped.x() : -1);
							outputPoint.setY(mapped != null ? mapped.y() : -1);
						}
						return found;
					}
				}
			}
			
			if (outputPoint != null) {
				outputPoint.setX(relativePoint != null ? relativePoint.x() : -1);
				outputPoint.setY(relativePoint != null ? relativePoint.y() : -1);
			}
			return parentWidget;
		}

		protected QWidget hitWidget = null;
		public QWidget hitWidget() {
			return hitWidget;
		}				
	}	
	
	private static class QFindChildAndPostEvent extends QFindUnderMouseEvent {
		protected final static QEvent.Type FIND_CHILD_AND_POST_EVENT = QEvent.Type.resolve(QEvent.Type.User.value() + 3);
		
		public QFindChildAndPostEvent() {
			super(FIND_CHILD_AND_POST_EVENT);
		}
		
		public QFindChildAndPostEvent(QEvent.Type type) {
			super(type);
		}
		
		public void updateWidget(QWidget widget, QAwtWidget w) {
			// intentionally empty
		}		
		
		private QEvent.Type actualType = null;
		public void setActualType(QEvent.Type type) { actualType = type; }
		public QEvent.Type actualType() { return actualType; }
		
		public QEvent event() {
			QPoint repositionedPoint = null;
			hitWidget = findHitWidget(topWidget(), relativePoint(), repositionedPoint);
			if (hitWidget() == null)
				return null;
						
			return new QEvent(actualType());			
		}
		
	}	
	
	private static class QFindChildAndPostHoverEvent extends QFindChildAndPostEvent {
		private final static QEvent.Type FIND_CHILD_AND_POST_HOVER_EVENT = QEvent.Type.resolve(FIND_CHILD_AND_POST_EVENT.value() + 1);
		
		public QFindChildAndPostHoverEvent() {
			super(FIND_CHILD_AND_POST_HOVER_EVENT);
		}
		
		private QPoint previousPosition = null;
		public void setPreviousPosition(QPoint position) {
			previousPosition = position;
		}
		public QPoint previousPosition() {
			return previousPosition;
		}
				
		public QEvent event() {
			QPoint repositionedPoint = new QPoint();
			hitWidget = findHitWidget(topWidget(), relativePoint(), repositionedPoint);
			if (hitWidget() == null)
				return null;
			
			QPoint repositionedPreviousPosition = new QPoint(previousPosition().x(), previousPosition().y());
			if (previousPosition.x() != -1)							
				findHitWidget(topWidget(), previousPosition(), repositionedPreviousPosition);
			
			return new QHoverEvent(actualType(), repositionedPoint, repositionedPreviousPosition);
		}
				
	}
	
	private static class QFindChildAndPostMouseEvent extends QFindChildAndPostEvent {
		private final static QEvent.Type FIND_CHILD_AND_POST_MOUSE_EVENT = QEvent.Type.resolve(FIND_CHILD_AND_POST_EVENT.value() + 2);
				
		public QFindChildAndPostMouseEvent() {
			super(FIND_CHILD_AND_POST_MOUSE_EVENT);
		}

		public void updateWidget(QWidget widget, QAwtWidget w) {
			if (actualType() == QEvent.Type.MouseButtonPress) {
				if ((widget.focusPolicy().value() & Qt.FocusPolicy.ClickFocus.value()) != 0) {					
					w.setFocusWidget(widget, Qt.FocusReason.MouseFocusReason);
				}
			}
		}
								
		private Qt.MouseButton mouseButton = null;
		public void setMouseButton(Qt.MouseButton mouseButton) { this.mouseButton = mouseButton; }
		public Qt.MouseButton mouseButton() { return mouseButton; } 

		private Qt.MouseButtons mouseButtons = null;
		public void setMouseButtons(Qt.MouseButtons mouseButtons) { this.mouseButtons = mouseButtons; }
		public Qt.MouseButtons mouseButtons() { return mouseButtons; }
		
		private Qt.KeyboardModifiers keyboardModifiers = null;
		public void setKeyboardModifiers(Qt.KeyboardModifiers modifiers) { keyboardModifiers = modifiers; }
		public Qt.KeyboardModifiers keyboardModifiers() { return keyboardModifiers; }

		public QEvent event() {
			QPoint repositionedPoint = new QPoint();
			hitWidget = findHitWidget(topWidget(), relativePoint(), repositionedPoint);
			
			if (hitWidget() == null)
				return null;
			
			return new QMouseEvent(actualType(), repositionedPoint, 
					mouseButton(), mouseButtons(), keyboardModifiers());
		}		
	}
		
	private QPair<QWidget, Qt.FocusReason> focus;
	private void setFocusWidget(QWidget focusWidget, Qt.FocusReason focusReason) {
		focus = new QPair<QWidget, Qt.FocusReason>(focusWidget, focusReason);		
	}
	
	private class QtEventFilter extends QObject {
		public QtEventFilter(QObject parent) {
			super(parent);
		}
				
		private boolean hasUpdateAwtEvent = false;
		private QWidget currentWidgetUnderMouse = null;
		
		@Override
		public boolean eventFilter(QObject receiver, QEvent event) {			
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
									
				if (event instanceof QFindChildAndPostEvent) {
					QEvent containedEvent = ((QFindChildAndPostEvent)event).event();
					if (containedEvent != null) {
						QWidget hitWidget = ((QFindChildAndPostEvent)event).hitWidget();
						QApplication.postEvent(hitWidget, makeEventSpontaneous(containedEvent));
						System.err.println("Posting event: " + containedEvent + ", " + containedEvent.spontaneous());
						if (!hasUpdateAwtEvent) {
							QApplication.postEvent(containedWidget, new QUpdateAwtWidgetEvent());
							hasUpdateAwtEvent = true;
						}
						
						((QFindChildAndPostEvent)event).updateWidget(hitWidget, QAwtWidget.this);						
					}
					
					return true;
				} 				
								
				if (event instanceof QFindUnderMouseEvent) {
					QFindUnderMouseEvent findUnderMouseEvent = (QFindUnderMouseEvent) event;
					QWidget hitWidget = findUnderMouseEvent.findHitWidget();					
					if (hitWidget != currentWidgetUnderMouse) {
						boolean requiresUpdate = false;
						if (currentWidgetUnderMouse != null) { 
							QApplication.postEvent(currentWidgetUnderMouse, makeEventSpontaneous(new QEvent(QEvent.Type.Leave)));
							requiresUpdate = true;
						}
						currentWidgetUnderMouse = hitWidget;
						if (currentWidgetUnderMouse != null) {
							System.err.println("Enter to " + currentWidgetUnderMouse);
							QApplication.postEvent(currentWidgetUnderMouse, makeEventSpontaneous(new QEvent(QEvent.Type.Enter)));
							requiresUpdate = true;
						}
						
						if (requiresUpdate && !hasUpdateAwtEvent) {
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
			QWidget focusWidget = focus != null ? focus.first : null;
			if (focusWidget != null) {
				System.err.println("activating");
				focusWidget.activateWindow();
				focusWidget.setFocus(focus.second);				
			}
			containedWidget.updateGeometry();
			synchronized (QAwtWidget.this) {				
				widgetAppearance.dispose();
				widgetAppearance = QPixmap.grabWidget(containedWidget);
			}
			if (focusWidget != null) {
				Container c = getParent();
				while (c != null && !(c instanceof Window)) {
					c = c.getParent();
				}
				if (c instanceof Window) {
					//((Window)c).toFront();
				}				
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
			QFindUnderMouseEvent event = new QFindUnderMouseEvent();
			event.setRelativePoint(pointToQPoint(awtEvent.getPoint()));
			event.setTopWidget(containedWidget);
			
			System.out.println("posting it : " + event.relativePoint().x() + ", " + event.relativePoint().y());
			
			QApplication.postEvent(containedWidget, event);
			
			/*QFindChildAndPostMouseEvent event = new QFindChildAndPostMouseEvent();
			event.setActualType(QEvent.Type.MouseMove);
			event.setTopWidget(containedWidget);
			event.setRelativePoint(pointToQPoint(awtEvent.getPoint()));
			event.setMouseButton(eventToMouseButton(awtEvent));
			event.setMouseButtons(eventToMouseButtons(awtEvent));
			event.setKeyboardModifiers(eventToKeyboardModifiers(awtEvent));
								
			QApplication.postEvent(containedWidget, event);*/									
		}
		
	}
		
	private class QAwtMouseListener implements MouseListener {

		public void mouseClicked(MouseEvent awtEvent) {			
		}

		public void mouseEntered(MouseEvent awtEvent) {
			/*{
				QFindChildAndPostHoverEvent event = new QFindChildAndPostHoverEvent();
				event.setActualType(QEvent.Type.HoverEnter);
				event.setTopWidget(containedWidget);
				event.setRelativePoint(pointToQPoint(awtEvent.getPoint()));
				event.setPreviousPosition(new QPoint(-1, -1));			
				QApplication.postEvent(containedWidget, event);
			}
			
			{
				QFindChildAndPostEvent event = new QFindChildAndPostEvent();
				event.setActualType(QEvent.Type.Enter);
				event.setTopWidget(containedWidget);
				QApplication.postEvent(containedWidget, event);
			}*/
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
	public synchronized void paint(Graphics g) {
		paintIt(g);
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

	@SuppressWarnings("unused")
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
		button.setText("Qt button (run gc)");
		
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
