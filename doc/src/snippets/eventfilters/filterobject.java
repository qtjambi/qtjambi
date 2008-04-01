import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

import java.util.*;

public class filterobject extends QObject
{
    QObject target;

    public filterobject()
    {
        target = null;
    }

    //! [0]
    public boolean eventFilter(QObject object, QEvent event)
    {
        if (object.equals(target)  && event.type().equals(QEvent.Type.KeyPress)) {
            QKeyEvent keyEvent = (QKeyEvent) event;
            if (keyEvent.key() == Qt.Key.Key_Tab.value()) {
                // Special tab handling
                return true;
            } else
                return false;
        }
        return false;
    }
    //! [0]

    public void setFilteredObject(QObject object)
    {
        if (target != null)
            target.removeEventFilter(this);

        target = object;

        if (target != null)
            target.installEventFilter(this);
    }

}
