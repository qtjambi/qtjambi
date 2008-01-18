/*   Ported from: src.gui.itemviews.qdatawidgetmapper.cpp
<snip>
//! [0]
    QDataWidgetMapper *mapper = new QDataWidgetMapper;
    mapper->setModel(model);
    mapper->addMapping(mySpinBox, 0);
    mapper->addMapping(myLineEdit, 1);
    mapper->addMapping(myCountryChooser, 2);
    mapper->toFirst();
//! [0]


//! [1]
    QDataWidgetMapper *mapper = new QDataWidgetMapper();
    mapper->setModel(myModel);
    mapper->addMapping(nameLineEdit, 0);
    mapper->addMapping(ageSpinBox, 1);
//! [1]


//! [2]
    QDataWidgetMapper *mapper = new QDataWidgetMapper(); 
    connect(myTableView->selectionModel(), SIGNAL(currentRowChanged(QModelIndex,QModelIndex)),
            mapper, SLOT(setCurrentModelIndex(QModelIndex)));
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


public class src_gui_itemviews_qdatawidgetmapper {
    public static void main(String args[]) {
        QApplication.initialize(args);
    }
    public void snippet0(QAbstractItemModel model, QWidget mySpinBox, QWidget myLineEdit, QWidget myCountryChooser) {
//! [0]
    QDataWidgetMapper mapper = new QDataWidgetMapper();
    mapper.setModel(model);
    mapper.addMapping(mySpinBox, 0);
    mapper.addMapping(myLineEdit, 1);
    mapper.addMapping(myCountryChooser, 2);
    mapper.toFirst();
//! [0]
    }

    public void snippet1(QAbstractItemModel myModel, QWidget nameLineEdit, QWidget ageSpinBox) {
//! [1]
    QDataWidgetMapper mapper = new QDataWidgetMapper();
    mapper.setModel(myModel);
    mapper.addMapping(nameLineEdit, 0);
    mapper.addMapping(ageSpinBox, 1);
//! [1]
    }

    public void snippet2(QTableView myTableView)
    {
//! [2]
    QDataWidgetMapper mapper = new QDataWidgetMapper(); 
    myTableView.selectionModel().currentRowChanged.connect(mapper, "setCurrentModelIndex(QModelIndex)");
//! [2]
    }
}
