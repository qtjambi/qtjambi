package com.trolltech.examples.qtconcurrent;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

import java.util.*;

public class Map {

    public static class MyMappedFunctor implements QtConcurrent.MappedFunctor<QImage, QImage> {
        public QImage map(QImage image) {
            System.err.println("Scaling image in thread: " + Thread.currentThread());
            return image.scaled(new QSize(100, 100), Qt.AspectRatioMode.IgnoreAspectRatio,
                    Qt.TransformationMode.SmoothTransformation);
        }
    }

    public static void main(String args[])
    {

        final int imageCount = 20;

        // Create a list containing imageCount images.
        List<QImage> images = new ArrayList<QImage>();
        for (int i = 0; i < imageCount; ++i)
            images.add(new QImage(1600, 1200, QImage.Format.Format_ARGB32_Premultiplied));

        MyMappedFunctor functor = new MyMappedFunctor();
        List<QImage> thumbnails = new ArrayList<QImage>();
        QTime time = new QTime();
        time.start();
        for (int i=0; i<imageCount; ++i) {
            thumbnails.add(functor.map(images.get(i)));
        }
        System.err.println("Time without QtConcurrent: " + time.elapsed() + "ms");

        // Use QtConcurrentBlocking::mapped to apply the scale function to all the
        // images in the list.
        time.restart();
        thumbnails = QtConcurrent.blockingMapped(images, functor);
        System.err.println("Time with QtConcurrent: " + time.elapsed() + "ms");

    }

}
