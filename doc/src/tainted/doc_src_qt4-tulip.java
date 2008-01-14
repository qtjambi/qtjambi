/*   Ported from: doc.src.qt4-tulip.qdoc
<snip>
//! [0]
        foreach (variable, container)
            statement;
//! [0]


//! [1]
        QList<QString> list;
        ...
        foreach (QString str, list)
            cout << str.ascii() << endl;
//! [1]


//! [2]
        QString str;
        foreach (str, list)
            cout << str.ascii() << endl;
//! [2]


//! [3]
        // forward                                  // backward
        QList<QString> list;                        QList<QString> list;
        ...                                         ...
        QListIterator<QString> i(list);             QListIterator<QString> i(list);        
        while (i.hasNext())                         i.toBack();                            
            cout << i.next().ascii() << endl;       while (i.hasPrev())                    
                                                        cout << i.prev().ascii() << endl;
//! [3]


//! [4]
        // forward                                  // backward
        QMutableListIterator<int> i(list);          QMutableListIterator<int> i(list);  
        while (i.hasNext())                         i.toBack();                         
            if (i.next() > 128)                     while (i.hasPrev())                 
                i.setValue(128);                        if (i.prev() > 128)             
                                                            i.setValue(128);           
//! [4]


//! [5]
        // forward                                  // backward
        QMutableListIterator<int> i(list);          QMutableListIterator<int> i(list);                 
        while (i.hasNext())                         i.toBack();                         
            if (i.next() % 2 != 0)                  while (i.hasPrev())                        
                i.remove();                             if (i.prev() % 2 != 0)          
                                                            i.remove();                 
//! [5]


//! [6]
        // STL-style                                // Java-style
        QMap<int, QWidget *>::const_iterator i;     QMapIterator<int, QWidget *> i(map);
        for (i = map.begin(); i != map.end(); ++i)  while (i.findNext(widget))
            if (i.value() == widget)                    cout << "Found widget " << widget
                cout << "Found widget " << widget            << " under key "
                     << " under key "                        << i.key() << endl;
                     << i.key() << endl;
//! [6]


//! [7]
        // STL-style                                // Java-style
        QList<int>::iterator i = list.begin();      QMutableListIterator<int> i(list);
        while (i != list.end()) {                   while (i.hasNext()) {
            if (*i == 0) {                              int val = i.next();
                i = list.erase(i);                      if (val < 0)
            } else {                                        i.setValue(-val);
                if (*i < 0)                             else if (val == 0)
                    *i = -*i;                               i.remove();
                ++i;                                }
            }
        }
//! [7]


//! [8]
        QList<double> list;
        ...
        for (int i = 0; i < list.size(); ++i) {
            if (list[i] < 0.0)
                list[i] = 0.0;
        }
//! [8]


//! [9]
        QMap<QString, int> map;
        ...
        map.value("TIMEOUT", 30);  // returns 30 if "TIMEOUT" isn't in the map
//! [9]


//! [10]
        QMultiMap<QString, int> map;
        ...
        QList<int> values = map.values("TIMEOUT");
//! [10]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class doc_src_qt4-tulip {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        for (variable, container)
            statement;
//! [0]


//! [1]
        QList<QString> list;
        ...
        for (Stringsstr, list)
            cout << str.ascii() << endl;
//! [1]


//! [2]
        Stringsstr;
        for (str, list)
            cout << str.ascii() << endl;
//! [2]


//! [3]
        // forward                                  // backward
        QList<QString> list;                        QList<QString> list;
        ...                                         ...
        QListIterator<QString> i(list);             QListIterator<QString> i(list);        
        while (i.hasNext())                         i.toBack();                            
            cout << i.next().ascii() << endl;       while (i.hasPrev())                    
                                                        cout << i.prev().ascii() << endl;
//! [3]


//! [4]
        // forward                                  // backward
        QMutableListIterator<int> i(list);          QMutableListIterator<int> i(list);  
        while (i.hasNext())                         i.toBack();                         
            if (i.next() > 128)                     while (i.hasPrev())                 
                i.setValue(128);                        if (i.prev() > 128)             
                                                            i.setValue(128);           
//! [4]


//! [5]
        // forward                                  // backward
        QMutableListIterator<int> i(list);          QMutableListIterator<int> i(list);                 
        while (i.hasNext())                         i.toBack();                         
            if (i.next() % 2 != 0)                  while (i.hasPrev())                        
                i.remove();                             if (i.prev() % 2 != 0)          
                                                            i.remove();                 
//! [5]


//! [6]
        // STL-style                                // Java-style
        QMap<int, QWidget *>.const_iterator i;     QMapIterator<int, QWidget *> i(map);
        for (i = map.begin(); i != map.end(); ++i)  while (i.findNext(widget))
            if (i.value() == widget)                    cout << "Found widget " << widget
                cout << "Found widget " << widget            << " under key "
                     << " under key "                        << i.key() << endl;
                     << i.key() << endl;
//! [6]


//! [7]
        // STL-style                                // Java-style
        QList<int>.iterator i = list.begin();      QMutableListIterator<int> i(list);
        while (i != list.end()) {                   while (i.hasNext()) {
            if ( == 0) {                              int val = i.next();
                i = list.erase(i);                      if (val < 0)
            } else {                                        i.setValue(-val);
                if ( < 0)                             else if (val == 0)
                     = -;                               i.remove();
                ++i;                                }
            }
        }
//! [7]


//! [8]
        QList<double> list;
        ...
        for (int i = 0; i < list.size(); ++i) {
            if (list[i] < 0.0)
                list[i] = 0.0;
        }
//! [8]


//! [9]
        QMap<QString, int> map;
        ...
        map.value("TIMEOUT", 30);  // returns 30 if "TIMEOUT" isn't in the map
//! [9]


//! [10]
        QMultiMap<QString, int> map;
        ...
        QList<int> values = map.values("TIMEOUT");
//! [10]


    }
}
