package com.trolltech.awt;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;


public class QAwtWidget extends Canvas {
	private static boolean guard = false;
	
	private class QtEventFilter extends QObject {
		public QtEventFilter(QObject parent) {
			super(parent);
		}

		
		@Override
		public boolean eventFilter(QObject receiver, QEvent event) {
			System.err.println("Event: " + event);
			
			switch (event.type()) {
			case MouseButtonPress:
				button.setText(button.text() + "!");
				break;
			}
						
			if (!guard) {
				guard = true;
				
				if (event instanceof QResizeEvent) {
					sizeHint = containedWidget.sizeHint();
					minimumSizeHint = containedWidget.minimumSizeHint();
					Rectangle rectangle = getBounds();
					
					setBounds(new Rectangle(rectangle.x, rectangle.y, 
							sizeHint.width() > rectangle.width || true ? sizeHint.width() : rectangle.width, 
						    sizeHint.height() > rectangle.height || true ? sizeHint.height() : rectangle.height));
					getParent().doLayout();					
				}
				
				containedWidget.updateGeometry();
				widgetAppearance = QPixmap.grabWidget(containedWidget);
				repaint();
				guard = false;
			} 
			
			return false;
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
			QMouseEvent mouseEvent = new QMouseEvent(QEvent.Type.MouseButtonPress,
					pointToQPoint(awtEvent.getPoint()),
					eventToMouseButton(awtEvent),
					eventToMouseButtons(awtEvent),
					eventToKeyboardModifiers(awtEvent));
					
			QApplication.postEvent(containedWidget, mouseEvent);
		}

		public void mouseReleased(MouseEvent awtEvent) {
			QMouseEvent mouseEvent = new QMouseEvent(QEvent.Type.MouseButtonRelease,
					pointToQPoint(awtEvent.getPoint()),
					eventToMouseButton(awtEvent),
					eventToMouseButtons(awtEvent),
					eventToKeyboardModifiers(awtEvent));
					
			QApplication.postEvent(containedWidget, mouseEvent);			
		}
		
	}

	

	static {
		System.out.println("qtjambi_awt loading");
		com.trolltech.qt.Utilities.loadQtLibrary("QtCore");
	    com.trolltech.qt.Utilities.loadQtLibrary("QtGui");
		com.trolltech.qt.Utilities.loadJambiLibrary("qtjambi");
		com.trolltech.qt.Utilities.loadJambiLibrary("qtjambi_jawt");
	}
		
	private QWidget containedWidget;
	private QSize sizeHint;
	private QSize minimumSizeHint;	
	private QPixmap widgetAppearance;
	public QAwtWidget(QWidget containedWidget) {
		this.containedWidget = containedWidget;
		sizeHint = containedWidget.sizeHint();
		minimumSizeHint = containedWidget.minimumSizeHint();
		setVisible(true);
		
		addMouseListener(new QAwtMouseListener());
		widgetAppearance = QPixmap.grabWidget(containedWidget);
		containedWidget.installEventFilter(new QtEventFilter(containedWidget));
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
		System.out.println("rect: " + rect.width + " " + rect.height);
		return rect;
	}

	
	private QPoint pointToQPoint(Point p) {
		System.out.println("P: " + p.x + ", " + p.y);
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
	public native void paint(Graphics g);
	
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
		System.out.println("BBB");
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
		QLineEdit lineEdit = new QLineEdit();
		button = new QPushButton();
		button.setText("Qt button");
		
		QHBoxLayout layout = new QHBoxLayout(w);
		layout.addWidget(label);
		layout.addWidget(lineEdit);
		layout.addWidget(button);
		w.setGeometry(w.x(), w.y(), w.sizeHint().width(), w.sizeHint().height());
		
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
		
		f.pack();
		
		QApplication.exec();
	}

}
