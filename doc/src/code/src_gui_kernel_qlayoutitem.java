/*   Ported from: src.gui.kernel.qlayoutitem.cpp
<snip>
//! [0]
        int MyLayout::heightForWidth(int w) const
        {
            if (cache_dirty || cached_width != w) {
                // not all C++ compilers support "mutable"
                MyLayout *that = (MyLayout*)this;
                int h = calculateHeightForWidth(w);
                that->cached_hfw = h;
                return h;
            }
            return cached_hfw;
        }
//! [0]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_kernel_qlayoutitem {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        int MyLayout.heightForWidth(int w)
        {
            if (cache_dirty || cached_width != w) {
                // not all C++ compilers support "mutable"
                MyLayout hat = (MyLayout*)this;
                int h = calculateHeightForWidth(w);
                that.cached_hfw = h;
                return h;
            }
            return cached_hfw;
        }
//! [0]


    }
}
