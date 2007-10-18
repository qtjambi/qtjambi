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

public class DrawText {
    
    private static class AwtWidget extends Component {
    private static final long serialVersionUID = 1L;

    @Override
    public void paint(Graphics g) {
	    g.setColor(new Color(0, 0, 0));

	    int runningTime = 5000;

	    long drawCount = 0;
	    long startTime = System.currentTimeMillis();
	    long endTime = startTime + runningTime;
	    while (System.currentTimeMillis() < endTime) {
		for (int j=0; j<100; ++j) {
		    g.drawString("hello world!!", 100, 100);
		    ++drawCount;
		}
	    }

	    long opsPrSec = (drawCount * 1000 / (endTime - startTime));
	    System.out.printf("Awt:   text drawing: ops/sec=%d\n", opsPrSec);
	    
	    setVisible(false);
	}

	@Override
    public Dimension getPreferredSize() {
	    return new Dimension(512, 512);
	}
    }


    private static class QtWidget extends QWidget {
	@Override
    protected void paintEvent(QPaintEvent e) {
	    
	    QPainter p = new QPainter();
	    p.begin(this);

	    int runningTime = 1000;

	    long drawCount = 0;
	    long startTime = System.currentTimeMillis();
	    long endTime = startTime + runningTime;
	    while (System.currentTimeMillis() < endTime) {
		for (int j=0; j<100; ++j) {
		    p.drawText(100, 100, "hello world!!");
		    ++drawCount;
		}
	    }

	    long opsPrSec = (drawCount * 1000 / (endTime - startTime));
	    System.out.printf("Qt:    text drawing: ops/sec=%d\n", opsPrSec);
	    
	    p.end();

	    hide();
	}

	@Override
    public QSize sizeHint() {
	    return new QSize(512, 512);
	}
    }

    private static class QtGLWidget extends QGLWidget {
	@Override
    protected void paintEvent(QPaintEvent e) {
	    
	    QPainter p = new QPainter();
	    p.begin(this);

	    int runningTime = 1000;

	    long drawCount = 0;
	    long startTime = System.currentTimeMillis();
	    long endTime = startTime + runningTime;
	    while (System.currentTimeMillis() < endTime) {
		for (int j=0; j<100; ++j) {
		    p.drawText(100, 100, "hello world!");
		    ++drawCount;
		}
	    }

	    long opsPrSec = (drawCount * 1000 / (endTime - startTime));
	    System.out.printf("Qt/GL: text drawing: ops/sec=%d\n", opsPrSec);
	    
	    p.end();

	    hide();
	}

	@Override
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
