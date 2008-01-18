/*   Ported from: src.gui.util.qcompleter.cpp
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;
import java.util.*;


public class src_gui_util_qcompleter {
    static void main(String args[]) {
        QApplication.initialize(args);
        //! [0]
        List<String> wordList = new Vector<String>();
        wordList.add("alpha");
        wordList.add("omega");
        wordList.add("omicron");
        wordList.add("zeta");

        QLineEdit lineEdit = new QLineEdit();

        QCompleter completer = new QCompleter(wordList);
        completer.setCaseSensitivity(Qt.CaseSensitivity.CaseInsensitive);
        lineEdit.setCompleter(completer);
        //! [0]

        //! [1]
        QCompleter completer2 = new QCompleter();
        completer2.setModel(new QDirModel(completer2));
        lineEdit.setCompleter(completer2);
        //! [1]

        //! [2]
            for (int i = 0; completer.setCurrentRow(i); i++)
                System.out.println(completer.currentCompletion() + " is match number " + i);
        //! [2]

    }
}
