/*   Ported from: src.gui.image.qpixmapcache.cpp
<snip>
//! [0]
        QPixmap* pp;
        QPixmap p;
        if ((pp=QPixmapCache::find("my_big_image", pm))) {
            p = *pp;
        } else {
            p.load("bigimage.png");
            QPixmapCache::insert("my_big_image", new QPixmap(p));
        }
        painter->drawPixmap(0, 0, p);
//! [0]


//! [1]
        QPixmap pm;
        if (!QPixmapCache::find("my_big_image", pm)) {
            pm.load("bigimage.png");
            QPixmapCache::insert("my_big_image", pm);
        }
        painter->drawPixmap(0, 0, pm);
//! [1]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_image_qpixmapcache {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QPixmap* pp;
        QPixmap p;
        if ((pp=QPixmapCache.find("my_big_image", pm))) {
            p = p;
        } else {
            p.load("bigimage.png");
            QPixmapCache.insert("my_big_image", new QPixmap(p));
        }
        painter.drawPixmap(0, 0, p);
//! [0]


//! [1]
        QPixmap pm;
        if (!QPixmapCache.find("my_big_image", pm)) {
            pm.load("bigimage.png");
            QPixmapCache.insert("my_big_image", pm);
        }
        painter.drawPixmap(0, 0, pm);
//! [1]


    }
}
