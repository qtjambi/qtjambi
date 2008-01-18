/*   Ported from: src.gui.styles.qstyleoption.cpp
<snip>
//! [0]
        int MyStyle::styleHint(StyleHint stylehint, const QStyleOption *opt,
                               const QWidget *widget, QStyleHintReturn* returnData) const;
        {
            if (stylehint == SH_RubberBand_Mask) {
                const QStyleHintReturnMask *maskReturn =
                        qstyleoption_cast<const QStyleHintReturnMask *>(hint);
                if (maskReturn) {
                    ...
                }
            }
            ...
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


public class src_gui_styles_qstyleoption {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        int MyStyle.styleHint(StyleHint stylehint, QStyleOption pt,
                               QWidget idget, QStyleHintReturn* returnData);
        {
            if (stylehint == SH_RubberBand_Mask) {
                QStyleHintReturnMask askReturn =
                        qstyleoption_cast<QStyleHintReturnMask *>(hint);
                if (maskReturn) {
                    ...
                }
            }
            ...
        }
//! [0]


    }
}
