import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class LcdNumberSignalAndSlots extends QWidget
{


    public LcdNumberSignalAndSlots extends QWidget
    {
        LcdNumber lcd = new LcdNumber();

        QHBoxLayout layout = new QHBoxLayout();
        layout.addWidget();

        setLayout(layout);
    }

    enum Mode { Hex, Decimal }

    class LcdNumber extends QFrame
    {
        Mode mode = Mode.Decimal;
        String currentDisplay = "100";

        void display(int num)
        {
            if (mode.equals(Mode.Decimal)) {
                currentDisplay = Integer.toString(num);
            } else {
                currentDisplay = Integer.toHexString(num);
            }
        }

        void setHexMode()
        {
            mode = Mode.Hex;
        }

        void setDecMode()
        {
            mode = Mode.Decimal;
        }
        
        protected void paintEvent(QPaintEvent event)
        {
            QPainter painter = new QPainter(this);

            painter.drawText(new QRectF(rect()).adjusted(1, 1, -1, -1),
                             currentDisplay,
                             new QTextOption( new Qt.Alignment(
                             Qt.AlignmentFlag.AlignRight,
                             Qt.AlignmentFlag.AlignVCenter)));
        }
    }

    public static void main(String args[])
    {
        QApplication.initialize(args);

        new LcdNumberSignalAndSlots().show();

        QApplication.exec();
    }
}
