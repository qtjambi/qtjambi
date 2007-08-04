import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class SignalsSlotsExample extends QWidget
{
    public SignalsSlotsExample()
    {
        System.err.println("Yes we are here");
        Counter a, b;
        a = new Counter();
        b = new Counter();

        a.valueChanged.connect(b, "setValue(int)");
        a.setValue(12);     // a.value() == 12, b.value() == 12
        b.setValue(48);     // a.value() == 12, b.value() == 48

        System.err.println("a: "+a.value()+" b: "+b.value());
    }

    class Counter extends QSignalEmitter {
        int value;

        public Signal1<Integer> valueChanged = new Signal1<Integer>();

        @QtBlockedSlot
        public int value()
        {
            return value;
        }

        public void setValue(int val)
        {
            if (value != val) {
                value = val;
                valueChanged.emit(value);
            }
        }

        public Counter()
        {
            value = 0;
        }
    }

    public static void main(String args[])
    {
        System.err.println("Enter main");
        QApplication.initialize(args);
        System.err.println("All OK");

        new SignalsSlotsExample();

        QApplication.exec();
    }
}
