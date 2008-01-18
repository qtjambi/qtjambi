/*   Ported from: src.opengl.qglpixelbuffer.cpp
<snip>
//! [0]
        QGLPixelBuffer pbuffer(...);
        ...
        pbuffer.makeCurrent();
        GLuint dynamicTexture = pbuffer.generateDynamicTexture();
        pbuffer.bindToDynamicTexture(dynamicTexture);
        ...
        pbuffer.releaseFromDynamicTexture();
//! [0]


//! [1]
        QGLPixelBuffer pbuffer(...);
        ...
        pbuffer.makeCurrent();
        GLuint dynamicTexture = pbuffer.generateDynamicTexture();
        ...
        pbuffer.updateDynamicTexture(dynamicTexture);
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


public class src_opengl_qglpixelbuffer {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QGLPixelBuffer pbuffer(...);
        ...
        pbuffer.makeCurrent();
        GLuint dynamicTexture = pbuffer.generateDynamicTexture();
        pbuffer.bindToDynamicTexture(dynamicTexture);
        ...
        pbuffer.releaseFromDynamicTexture();
//! [0]


//! [1]
        QGLPixelBuffer pbuffer(...);
        ...
        pbuffer.makeCurrent();
        GLuint dynamicTexture = pbuffer.generateDynamicTexture();
        ...
        pbuffer.updateDynamicTexture(dynamicTexture);
//! [1]


    }
}
