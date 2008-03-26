package com.trolltech.manualtests;


import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.phonon.*;

public class VideoInGraphicsView extends ZoomableGraphicsView {
	
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
    
	public static void main(String args[]) {
		QApplication.initialize(args);
		
		VideoInGraphicsView view = new VideoInGraphicsView();
		view.show();
		
		QApplication.exec();
	}
	
}
