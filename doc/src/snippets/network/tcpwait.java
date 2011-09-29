import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.network.*;


public class tcpwait
{

    public static void main(String args[])
    {
        QApplication.initialize(args);

        QTcpSocket socket = new QTcpSocket();
        socket.connectToHost("localhost", 1025,
            new QIODevice.OpenMode(QIODevice.OpenModeFlag.ReadOnly));

    //! [0]
        int numRead = 0, numReadTotal = 0;
        byte buffer[] = new byte[50];

        while (true) {
            numRead  = socket.read(buffer);

            // do whatever with array

            numReadTotal += numRead;
            if (numRead == 0 && !socket.waitForReadyRead(1000))
                break;
        }
    //! [0]

        QApplication.execStatic();
        QApplication.shutdown();
    }
}
