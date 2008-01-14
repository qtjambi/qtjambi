/*   Ported from: src.gui.dialogs.qfiledialog.cpp
<snip>
//! [0]
  fileName = QFileDialog::getOpenFileName(this,
      tr("Open Image"), "/home/jana", tr("Image Files (*.png *.jpg *.bmp)"));
//! [0]


//! [1]
  "Images (*.png *.xpm *.jpg);;Text files (*.txt);;XML files (*.xml)"
//! [1]


//! [2]
  QFileDialog dialog(this);
  dialog.setFileMode(QFileDialog::AnyFile);
//! [2]


//! [3]
  dialog.setNameFilter(tr("Images (*.png *.xpm *.jpg)"));
//! [3]


//! [4]
  dialog.setViewMode(QFileDialog::Detail);
//! [4]


//! [5]
  QStringList fileNames;
  if (dialog.exec())
      fileNames = dialog.selectedFiles();
//! [5]


//! [6]
       dialog.setNameFilter("All C++ files (*.cpp *.cc *.C *.cxx *.c++)");
       dialog.setNameFilter("*.cpp;*.cc;*.C;*.cxx;*.c++");
//! [6]


//! [7]
      QStringList filters;
      filters << "Image files (*.png *.xpm *.jpg)"
              << "Text files (*.txt)"
              << "Any files (*)";

      QFileDialog dialog(this);
      dialog.setNameFilters(filters);
      dialog.exec();
//! [7]


//! [8]
    QString fileName = QFileDialog::getOpenFileName(this, tr("Open File"),
                                                    "/home",
                                                    tr("Images (*.png *.xpm *.jpg)"));
//! [8]


//! [9]
    QStringList files = QFileDialog::getOpenFileNames(
                            this,
                            "Select one or more files to open",
                            "/home",
                            "Images (*.png *.xpm *.jpg)");
//! [9]


//! [10]
    QStringList list = files;
    QStringList::Iterator it = list.begin();
    while(it != list.end()) {
        myProcessing(*it);
        ++it;
    }
//! [10]


//! [11]
    QString fileName = QFileDialog::getSaveFileName(this, tr("Save File"),
                               "/home/jana/untitled.png",
                               tr("Images (*.png *.xpm *.jpg)"));
//! [11]


//! [12]
    QString dir = QFileDialog::getExistingDirectory(this, tr("Open Directory"),
                                                    "/home",
                                                    QFileDialog::ShowDirsOnly
                                                    | QFileDialog::DontResolveSymlinks);
//! [12]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_gui_dialogs_qfiledialog {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
  fileName = QFileDialog.getOpenFileName(this,
      tr("Open Image"), "/home/jana", tr("Image Files (*.png *.jpg *.bmp)"));
//! [0]


//! [1]
  "Images (*.png *.xpm *.jpg);;Text files (*.txt);;XML files (*.xml)"
//! [1]


//! [2]
  QFileDialog dialog(this);
  dialog.setFileMode(QFileDialog.AnyFile);
//! [2]


//! [3]
  dialog.setNameFilter(tr("Images (*.png *.xpm *.jpg)"));
//! [3]


//! [4]
  dialog.setViewMode(QFileDialog.Detail);
//! [4]


//! [5]
  List<String> fileNames;
  if (dialog.exec())
      fileNames = dialog.selectedFiles();
//! [5]


//! [6]
       dialog.setNameFilter("All C++ files (*.cpp *.cc *.C *.cxx *.c++)");
       dialog.setNameFilter("*.cpp;*.cc;*.C;*.cxx;*.c++");
//! [6]


//! [7]
      List<String> filters;
      filters << "Image files (*.png *.xpm *.jpg)"
              << "Text files (*.txt)"
              << "Any files (*)";

      QFileDialog dialog(this);
      dialog.setNameFilters(filters);
      dialog.exec();
//! [7]


//! [8]
    StringsfileName = QFileDialog.getOpenFileName(this, tr("Open File"),
                                                    "/home",
                                                    tr("Images (*.png *.xpm *.jpg)"));
//! [8]


//! [9]
    List<String> files = QFileDialog.getOpenFileNames(
                            this,
                            "Select one or more files to open",
                            "/home",
                            "Images (*.png *.xpm *.jpg)");
//! [9]


//! [10]
    List<String> list = files;
    List<String>.Iterator it = list.begin();
    while(it != list.end()) {
        myProcessing(t);
        ++it;
    }
//! [10]


//! [11]
    StringsfileName = QFileDialog.getSaveFileName(this, tr("Save File"),
                               "/home/jana/untitled.png",
                               tr("Images (*.png *.xpm *.jpg)"));
//! [11]


//! [12]
    Stringsdir = QFileDialog.getExistingDirectory(this, tr("Open Directory"),
                                                    "/home",
                                                    QFileDialog.ShowDirsOnly
                                                    | QFileDialog.DontResolveSymlinks);
//! [12]


    }
}
