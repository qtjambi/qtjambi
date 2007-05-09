package com.trolltech.launcher;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

class SplashScreen extends QSplashScreen {

    private static final QSize SIZE = new QSize(400, 200);

    public SplashScreen() {
        QRect r = QApplication.desktop().rect();
        QPixmap desktopBg = QPixmap.grabWindow(0,
		                                   r.width() / 2 - SIZE.width() / 2,
		                                   r.height() / 2 - SIZE.height() / 2,
		                                   SIZE.width(), SIZE.height());

        QPixmap target = new QPixmap(SIZE);
        QPixmap logo = new QPixmap("classpath:com/trolltech/images/logo.png");
        QRectF tr = new QRectF(0, 0, SIZE.width(), SIZE.height());

        int round = 40;
        int round2 = round * 2;

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
        p.fillRect(tr, new QBrush(QColor.white));
        p.restore();
        
        // The border
        for (int i=30; i>=0; i-=3) {
            int intensity = i * 255 / 30;
            QColor c = new QColor(intensity, intensity, intensity, (255 - intensity) / 3);
            p.setPen(new QPen(c, i));
            p.drawPath(path);
        }

        p.drawPixmap(0, 0, logo);

        p.end();

        setPixmap(target);
    }
}
