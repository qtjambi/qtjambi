import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

import java.util.*;

public class main
{

//! [0]

This Code is not applicable to Qt Jambi.
public static void main(String args[])
{
    QApplication.initialize(args);

    if (args.length != 2) {
        System.err.println("Error - expected executable path as argument");
        System.exit(1);
    }

    QX11EmbedContainer container = new QX11EmbedContainer();
    container.show();

    QProcess process = new QProcess(container);
    String executable = QApplication.arguments().get(1);
    List<String> arguments = new Vector<String>();
    arguments.add(String.valueOf(container.winId()));
    process.start(executable, arguments);

    QApplication.execStatic();
    QApplication.shutdown();

    process.close();
}
//! [0]

}
