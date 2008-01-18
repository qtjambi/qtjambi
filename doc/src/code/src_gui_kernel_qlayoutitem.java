import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


abstract class MyLayout extends QLayoutItem {

    int cached_width = 1;
    int cached_hfw = 1;
    boolean cache_dirty = true;

    private int calculateHeightForWidth(int w) { return 1; }

//! [0]
    public int heightForWidth(int w)
    {
        if (cache_dirty || cached_width != w) {
            int h = calculateHeightForWidth(w);
            cached_hfw = h;
            return h;
        }
        return cached_hfw;
    }
//! [0]
}
