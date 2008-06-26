
import com.trolltech.examples.generatortutorial.*;
import com.trolltech.qt.gui.*;

public class TestHelloGenerator
{

    public static void main(String args[]) throws Exception
    {
        QApplication.initialize(args);

        HelloGenerator gen = new HelloGenerator();
        gen.hello();
    
        System.err.println("Yea, the show seems all right");

        QApplication.exec();
    }
}

