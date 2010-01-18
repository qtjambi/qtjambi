import com.poseidon.test.TestItem;
import com.poseidon.test.TestView;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QGraphicsScene;
import com.trolltech.qt.gui.QGraphicsView;

/**
 * @author Helge Fredriksen
 * @version 1.0 Dec 9, 2009 3:12:08 PM
 */
public class TestJava {

    public static void main(String[] args) {

        QApplication.initialize(args);
        QGraphicsScene scene = new QGraphicsScene();

        TestView view = new TestView(scene);
        TestItem item = view.getItem();
        int id = item.getId();
        System.out.println(id);

        view.show();

        QApplication.exec();

    }
}
