import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

import java.util.*;

public class main
{
    static void startProcess()
    {
        //! [0]
        QProcess process = new QProcess();
        List<String> env = QProcess.systemEnvironment();
        env.add("TMPDIR=C:\\MyApp\\temp"); // Add an environment variable
        for (String str : env)
            str.replaceAll("^PATH=(.*)", "PATH=\\1;C:\\Bin");
        process.setEnvironment(env);
        process.start("myapp");
        //! [0]
    }

    public static void main(String args[])
    {
        QApplication.initialize(args);

        startProcess();

        QApplication.execStatic();
        QApplication.shutdown();
    }
}
