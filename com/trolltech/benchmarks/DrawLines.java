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

package com.trolltech.benchmarks;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.opengl.*;

import java.awt.*;
import javax.swing.*;

public class DrawLines {
    
    private static class AwtWidget extends Component {
        
    private static final long serialVersionUID = 1L;

    public void paint(Graphics g) {
	    g.setColor(new Color(255, 0, 0));

	    int runningTime = 1000;

	    for (int size=8; size<=512; size*=2) {
		
		long drawCount = 0;
		long startTime = System.currentTimeMillis();
		long endTime = startTime + runningTime;
		while (System.currentTimeMillis() < endTime) {
		    for (int j=0; j<100; ++j) {
			g.drawLine(0, 0, size, size);
			++drawCount;
		    }
		}

		long opsPrSec = (long) (drawCount * 1000 / (endTime - startTime));
		System.out.printf("Awt:   diagonal lines: size=%3d: ops/sec=%d\n", size, opsPrSec);
	    }

	    setVisible(false);
	}

	public Dimension getPreferredSize() {
	    return new Dimension(512, 512);
	}
    }


    private static class QtWidget extends QWidget {
	protected void paintEvent(QPaintEvent e) {
	    
	    QPainter p = new QPainter();
	    p.begin(this);

	    p.setPen(new QPen(QColor.red));

	    int runningTime = 1000;

	    for (int size=8; size<=512; size*=2) {		
		long drawCount = 0;
		long startTime = System.currentTimeMillis();
		long endTime = startTime + runningTime;
		while (System.currentTimeMillis() < endTime) {
		    for (int j=0; j<100; ++j) {
			p.drawLine(0, 0, size, size);
			++drawCount;
		    }
		}

		long opsPrSec = (long) (drawCount / (endTime - startTime) * 1000);
		System.out.printf("Qt:    diagonal lines: size=%3d: ops/sec=%d\n", size, opsPrSec);
	    }

	    p.end();

	    hide();
	}

	public QSize sizeHint() {
	    return new QSize(512, 512);
	}
    }

    private static class QtGLWidget extends QGLWidget {
	protected void paintEvent(QPaintEvent e) {
	    
	    QPainter p = new QPainter();
	    p.begin(this);

	    p.setPen(new QPen(QColor.red));
	    
	    int runningTime = 1000;

	    for (int size=8; size<=512; size*=2) {		
		long drawCount = 0;
		long startTime = System.currentTimeMillis();
		long endTime = startTime + runningTime;
		while (System.currentTimeMillis() < endTime) {
		    for (int j=0; j<100; ++j) {
			p.drawLine(0, 0, size, size);
			++drawCount;
		    }
		}

		long opsPrSec = (long) (drawCount / (endTime - startTime) * 1000);
		System.out.printf("Qt/GL: diagonal lines: size=%3d: ops/sec=%d\n", size, opsPrSec);
	    }

	    p.end();

	    hide();
	}

	public QSize sizeHint() {
	    return new QSize(512, 512);
	}
    }
    
    public static void main(String args[]) {
	{
	    JFrame f = new JFrame();
	    AwtWidget awt = new AwtWidget();
	    f.add(awt);

	    f.pack();

	    f.setVisible(true);
	    
	}


	try { Thread.sleep(10 * 1000); } catch (Exception e) { }

	{
	    QApplication.initialize(args);

	    QtWidget w = new QtWidget();
	    w.show();

	    QtGLWidget wgl = new QtGLWidget();
	    wgl.show();

	    QApplication.exec();
	}



    }
}
