/*   Ported from: src.gui.kernel.qformlayout.cpp
<snip>
//! [0]
        QFormLayout *formLayout = new QFormLayout;
        formLayout->addRow(tr("&Name:"), nameLineEdit);
        formLayout->addRow(tr("&Email:"), emailLineEdit);
        formLayout->addRow(tr("&Age:"), ageSpinBox);
        setLayout(formLayout);
//! [0]


//! [1]
        nameLabel = new QLabel(tr("&Name:"));
        nameLabel->setBuddy(nameLineEdit);

        emailLabel = new QLabel(tr("&Name:"));
        emailLabel->setBuddy(emailLineEdit);

        ageLabel = new QLabel(tr("&Name:"));
        ageLabel->setBuddy(ageSpinBox);

        QGridLayout *gridLayout = new QGridLayout;
        gridLayout->addWidget(nameLabel, 0, 0);
        gridLayout->addWidget(nameLineEdit, 0, 1);
        gridLayout->addWidget(emailLabel, 1, 0);
        gridLayout->addWidget(emailLineEdit, 1, 1);
        gridLayout->addWidget(ageLabel, 2, 0);
        gridLayout->addWidget(ageSpinBox, 2, 1);
        setLayout(gridLayout);
//! [1]


//! [2]
        formLayout->setFormStyle(QFormLayout::MacStyle);
        formLayout->setLabelAlignment(Qt::AlignLeft);
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


public class src_gui_kernel_qformlayout {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QFormLayout ormLayout = new QFormLayout;
        formLayout.addRow(tr("ame:"), nameLineEdit);
        formLayout.addRow(tr("mail:"), emailLineEdit);
        formLayout.addRow(tr("ge:"), ageSpinBox);
        setLayout(formLayout);
//! [0]


//! [1]
        nameLabel = new QLabel(tr("ame:"));
        nameLabel.setBuddy(nameLineEdit);

        emailLabel = new QLabel(tr("ame:"));
        emailLabel.setBuddy(emailLineEdit);

        ageLabel = new QLabel(tr("ame:"));
        ageLabel.setBuddy(ageSpinBox);

        QGridLayout ridLayout = new QGridLayout;
        gridLayout.addWidget(nameLabel, 0, 0);
        gridLayout.addWidget(nameLineEdit, 0, 1);
        gridLayout.addWidget(emailLabel, 1, 0);
        gridLayout.addWidget(emailLineEdit, 1, 1);
        gridLayout.addWidget(ageLabel, 2, 0);
        gridLayout.addWidget(ageSpinBox, 2, 1);
        setLayout(gridLayout);
//! [1]


//! [2]
        formLayout.setFormStyle(QFormLayout.MacStyle);
        formLayout.setLabelAlignment(Qt.AlignLeft);
//! [2]


    }
}
