package com.trolltech.manualtests;


import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.phonon.*;

public class VideoInGraphicsView extends QGraphicsView {
	
	public VideoInGraphicsView() {		
		setRenderHint(QPainter.RenderHint.Antialiasing);

		// Video widget
		VideoWidget videoWidget = new VideoWidget();

		// Set up video player
		MediaObject mediaObject = new MediaObject(this);
		Phonon.createPath(mediaObject, videoWidget);
		mediaObject.setCurrentSource(new MediaSource("c:/documents and settings/eblomfel/my documents/my videos/johnrambo_320x144.mpg"));								
		mediaObject.play();
		
		QGraphicsProxyWidget w = new QGraphicsProxyWidget();
		w.setWidget(videoWidget);
		w.setVisible(true);
		
		// Put ellipse on top
		w.setZValue(-1);
		
		// Set up a graphics scene
		QGraphicsScene scene = new QGraphicsScene();
		
		scene.addEllipse(10, 10, 100, 100, new QPen(QColor.black), new QBrush(QColor.red)).setFlag(QGraphicsItem.GraphicsItemFlag.ItemIsMovable, true);
		scene.addItem(w);		
		setScene(scene);
		
		// Rotate the scene 45 degrees
		rotate(45);								
		
	}
	
	// Zoom on wheel events
    protected void wheelEvent(QWheelEvent event) {
        double scaleFactor = Math.pow(2, -event.delta() / 240.0); 
        QMatrix m = matrix();
        m.scale(scaleFactor, scaleFactor);
        double factor = m.mapRect(new QRectF(0, 0, 1, 1)).width();
        if (factor < 0.07 || factor > 100)
            return;

        scale(scaleFactor, scaleFactor);
        
    }    
    
	public static void main(String args[]) {
		QApplication.initialize(args);
		
		VideoInGraphicsView view = new VideoInGraphicsView();
		view.show();
		
		QApplication.exec();
	}
	
}
