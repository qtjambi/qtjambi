package generator;

import com.trolltech.qt.*;
import com.trolltech.qt.opengl.*;

class QGLColormap___ extends QGLColormap {

    public final void setEntries(int colors[], int base) {
        setEntries(colors.length, QtJambiInternal.intArrayToNativePointer(colors), base);
    }

    public final void setEntries(int colors[]) {
        setEntries(colors, 0);
    }

}// class
