/*   Ported from: src.gui.util.qcompleter.cpp
<snip>
//! [0]
        QStringList wordList;
        wordList << "alpha" << "omega" << "omicron" << "zeta";

        QLineEdit *lineEdit = new QLineEdit(this);

        QCompleter *completer = new QCompleter(wordList, this);
        completer->setCaseSensitivity(Qt::CaseInsensitive);
        lineEdit->setCompleter(completer);
//! [0]


//! [1]
        QCompleter *completer = new QCompleter(this);
        completer->setModel(new QDirModel(completer));
        lineEdit->setCompleter(completer);
//! [1]


//! [2]
        for (int i = 0; completer->setCurrentRow(i); i++)
            qDebug() << completer->currentCompletion() << " is match number " << i;
//! [2]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_util_qcompleter {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        List<String> wordList;
        wordList << "alpha" << "omega" << "omicron" << "zeta";

        QLineEdit ineEdit = new QLineEdit(this);

        QCompleter ompleter = new QCompleter(wordList, this);
        completer.setCaseSensitivity(Qt.CaseInsensitive);
        lineEdit.setCompleter(completer);
//! [0]


//! [1]
        QCompleter ompleter = new QCompleter(this);
        completer.setModel(new QDirModel(completer));
        lineEdit.setCompleter(completer);
//! [1]


//! [2]
        for (int i = 0; completer.setCurrentRow(i); i++)
            qDebug() << completer.currentCompletion() << " is match number " << i;
//! [2]


    }
}
