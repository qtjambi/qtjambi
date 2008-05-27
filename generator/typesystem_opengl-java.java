package generator;

import com.trolltech.qt.*;
import com.trolltech.qt.opengl.*;

class QGLColormap___ extends QGLColormap {

    public final void setEntries(int colors[], int base) {
        setEntries(colors.length, com.trolltech.qt.internal.QtJambiInternal.intArrayToNativePointer(colors), base);
    }

    public final void setEntries(int colors[]) {
        setEntries(colors.length, com.trolltech.qt.internal.QtJambiInternal.intArrayToNativePointer(colors));
    }

}// class
