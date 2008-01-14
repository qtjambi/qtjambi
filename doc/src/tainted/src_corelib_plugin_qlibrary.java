/*   Ported from: src.corelib.plugin.qlibrary.cpp
<snip>
//! [0]
        QLibrary myLib("mylib");
        typedef void (*MyPrototype)();
        MyPrototype myFunction = (MyPrototype) myLib.resolve("mysymbol");
        if (myFunction)
            myFunction();
//! [0]


//! [1]
        typedef void (*MyPrototype)();
        MyPrototype myFunction =
                (MyPrototype) QLibrary::resolve("mylib", "mysymbol");
        if (myFunction)
            myFunction();
//! [1]


//! [2]
        typedef int (*AvgFunction)(int, int);

        AvgFunction avg = (AvgFunction) library->resolve("avg");
        if (avg)
            return avg(5, 8);
        else
            return -1;
//! [2]


//! [3]
        extern "C" MY_EXPORT int avg(int a, int b)
        {
            return (a + b) / 2;
        }
//! [3]


//! [4]
        #ifdef Q_WS_WIN
        #define MY_EXPORT __declspec(dllexport)
        #else
        #define MY_EXPORT
        #endif
//! [4]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_corelib_plugin_qlibrary {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QLibrary myLib("mylib");
        typedef void (yPrototype)();
        MyPrototype myFunction = (MyPrototype) myLib.resolve("mysymbol");
        if (myFunction)
            myFunction();
//! [0]


//! [1]
        typedef void (yPrototype)();
        MyPrototype myFunction =
                (MyPrototype) QLibrary.resolve("mylib", "mysymbol");
        if (myFunction)
            myFunction();
//! [1]


//! [2]
        typedef int (vgFunction)(int, int);

        AvgFunction avg = (AvgFunction) library.resolve("avg");
        if (avg)
            return avg(5, 8);
        else
            return -1;
//! [2]


//! [3]
        extern "C" MY_EXPORT int avg(int a, int b)
        {
            return (a + b) / 2;
        }
//! [3]


//! [4]
        #ifdef Q_WS_WIN
        #define MY_EXPORT __declspec(dllexport)
        #else
        #define MY_EXPORT
        #endif
//! [4]


    }
}
