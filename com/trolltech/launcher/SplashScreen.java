package com.trolltech.launcher;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

class SplashScreen extends QSplashScreen {

    private static final QSize SIZE = new QSize(600, 300);

    public SplashScreen() {
        QRect r = QApplication.desktop().rect();
        QPixmap desktopBg = QPixmap.grabWindow(0,
		                                   r.width() / 2 - SIZE.width() / 2,
		                                   r.height() / 2 - SIZE.height() / 2,
		                                   SIZE.width(), SIZE.height());

        QImage target = new QImage(SIZE, QImage.Format.Format_ARGB32_Premultiplied);
        QPixmap logo = new QPixmap("classpath:com/trolltech/images/logo.png");
        
        final int margin = 20;
        final int shadow = 50;        
        QRectF tr = new QRectF(0, 0, SIZE.width() - margin - shadow -1 , SIZE.height() - margin - shadow -1);

        final int round = 40;
        final int round2 = round * 2;
        
        QPainterPath path = new QPainterPath();
        path.moveTo(tr.left() + round, tr.top());
        path.lineTo(tr.right() - round, tr.top());
        path.arcTo(tr.right() - round2, tr.top(), round2, round2, 90, -90);
        path.lineTo(tr.right(), tr.bottom() - round);
        path.arcTo(tr.right() - round2, tr.bottom() - round2, round2, round2, 0, -90);
        path.lineTo(tr.left() - round, tr.bottom());
        path.arcTo(tr.left(), tr.bottom() - round2, round2, round2, 270, -90);
        path.lineTo(tr.left(), tr.top() - round);
        path.arcTo(tr.left(), tr.top(), round2, round2, 180, -90);

        QPainter p = new QPainter(target);
        p.setRenderHint(QPainter.RenderHint.Antialiasing);

        p.drawPixmap(0, 0, desktopBg);

        p.setClipPath(path);

        // The background blurring...
        p.save();
        p.setOpacity(0.3);
        p.drawPixmap(-1, -1, desktopBg);
        p.drawPixmap(1, 1, desktopBg);
        p.setOpacity(0.2);
        p.drawPixmap(1, -1, desktopBg);
        p.drawPixmap(-1, 1, desktopBg);
        p.setOpacity(0.5);
        p.fillRect(target.rect(), new QBrush(QColor.white));
        p.restore();
        
        final QRectF rectRight = new QRectF(tr.left() - margin + tr.width() + 1, 
				   tr.top() + shadow, 
				   shadow, tr.height() - shadow + 1 - margin);
        final QRectF rectBottom = new QRectF(tr.left() + shadow, 
        		tr.top() - margin + tr.height() + 1, tr.width() - shadow + 1 - margin, shadow);
        final QRectF rectBottomRight = new QRectF(tr.left() - margin + tr.width() + 1, 
        		tr.top() - margin + tr.height() + 1, shadow, shadow);
        final QRectF rectTopRight = new QRectF(tr.left() - margin + tr.width() + 1, 
        		tr.top(), shadow, shadow);
        final QRectF rectBottomLeft = new QRectF(tr.left(), 
        		tr.top() - margin + tr.height() + 1, shadow, shadow);
        
        QPainterPath clipPath = new QPainterPath();
        clipPath.addRect(new QRectF(0, 0, SIZE.width(), SIZE.height()));        
        clipPath.addPath(path);
        
        p.setClipPath(clipPath);
        
        final QColor dark = QColor.darkGray;
        final QColor light = QColor.transparent;               
       
        // Drop shadow: right shadow
        {
            QLinearGradient lg = new QLinearGradient(rectRight.topLeft(), 
            		                                 rectRight.topRight());
            lg.setColorAt(0, dark);
            lg.setColorAt(1, light);
            p.fillRect(rectRight, new QBrush(lg));
        }

        // bottom shadow
        {
            QLinearGradient lg = new QLinearGradient(rectBottom.topLeft(), rectBottom.bottomLeft());
            lg.setColorAt(0, dark);
            lg.setColorAt(1, light);
            p.fillRect(rectBottom, new QBrush(lg));
        }

        // bottom/right corner shadow
        {
            QRadialGradient g = new QRadialGradient(rectBottomRight.topLeft(), shadow);
            g.setColorAt(0, dark);
            g.setColorAt(1, light);
            p.fillRect(rectBottomRight, new QBrush(g));
        }

        // top/right corner
        {
            QRadialGradient g = new QRadialGradient(rectTopRight.bottomLeft(), shadow);
            g.setColorAt(0, dark);
            g.setColorAt(1, light);
            p.fillRect(rectTopRight, new QBrush(g));
        }

        // bottom/left corner
        {
            QRadialGradient g = new QRadialGradient(rectBottomLeft.topRight(), shadow);
            g.setColorAt(0, dark);
            g.setColorAt(1, light);
            p.fillRect(rectBottomLeft, new QBrush(g));
        }

        p.setClipPath(path);

        // The border
        for (int i=30; i>=0; i-=3) {
            int intensity = i * 255 / 30;
            QColor c = new QColor(intensity, intensity, intensity, (255 - intensity) / 3);
            p.setPen(new QPen(c, i));
            p.drawPath(path);
        }

        p.drawPixmap(0, 0, logo);

        p.end();

        setPixmap(QPixmap.fromImage(target));
    }
}
