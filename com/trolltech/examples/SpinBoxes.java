/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $JAVA_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.examples;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class SpinBoxes extends QWidget
{
    private QDateTimeEdit meetingEdit;
    private QDoubleSpinBox doubleSpinBox;
    private QDoubleSpinBox priceSpinBox;
    private QDoubleSpinBox scaleSpinBox;
    private QGroupBox spinBoxesGroup;
    private QGroupBox editsGroup;
    private QGroupBox doubleSpinBoxesGroup;
    private QLabel meetingLabel;

    public SpinBoxes() {
	createSpinBoxes();
	createDateTimeEdits();
	createDoubleSpinBoxes();

	QHBoxLayout layout = new QHBoxLayout();
	layout.addWidget(spinBoxesGroup);
	layout.addWidget(editsGroup);
	layout.addWidget(doubleSpinBoxesGroup);
	setLayout(layout);

	setWindowTitle(tr("Spin Boxes"));
    
    setWindowIcon(new QIcon("classpath:com/trolltech/images/logo_32.png"));
    }

    public void createSpinBoxes() {
	spinBoxesGroup = new QGroupBox(tr("Spinboxes"));

	QLabel integerLabel = 
	    new QLabel(String.format(tr("Enter a value between %1$d and %2$d:"), -20, 20));
	QSpinBox integerSpinBox = new QSpinBox();
	integerSpinBox.setRange(-20, 20);
	integerSpinBox.setSingleStep(1);
	integerSpinBox.setValue(0);

	QLabel zoomLabel = 
	    new QLabel(String.format(tr("Enter a zoom value between %1$d and %2$d:"), 0, 1000));
	QSpinBox zoomSpinBox = new QSpinBox();
	zoomSpinBox.setRange(0, 1000);
	zoomSpinBox.setSingleStep(10);
	zoomSpinBox.setSuffix("%");
	zoomSpinBox.setSpecialValueText(tr("Automatic"));
	zoomSpinBox.setValue(100);

	QLabel priceLabel = 
	    new QLabel(String.format(tr("Enter a price between %1$d and %2$d:"), 0, 999));
	QSpinBox priceSpinBox = new QSpinBox();
	priceSpinBox.setRange(0, 999);
	priceSpinBox.setSingleStep(1);
	priceSpinBox.setPrefix("$");
	priceSpinBox.setValue(99);

	QVBoxLayout spinBoxLayout = new QVBoxLayout();
	spinBoxLayout.addWidget(integerLabel);
	spinBoxLayout.addWidget(integerSpinBox);
	spinBoxLayout.addWidget(zoomLabel);
	spinBoxLayout.addWidget(zoomSpinBox);
	spinBoxLayout.addWidget(priceLabel);
	spinBoxLayout.addWidget(priceSpinBox);
	spinBoxesGroup.setLayout(spinBoxLayout);
    }

    public void createDateTimeEdits() {
	editsGroup = new QGroupBox(tr("Date and time spin boxes"));

	QLabel dateLabel = new QLabel();
	QDateTimeEdit dateEdit = new QDateTimeEdit(QDate.currentDate());
	dateEdit.setDateRange(new QDate(2005, 1, 1), new QDate(2010, 12, 31));
	dateLabel.setText(String.format(tr("Appointment date (between %0$s and %1$s):"),
					dateEdit.minimumDate().toString(Qt.ISODate),
					dateEdit.maximumDate().toString(Qt.ISODate)));

	QLabel timeLabel = new QLabel();
	QDateTimeEdit timeEdit = new QDateTimeEdit(QTime.currentTime());
	timeEdit.setTimeRange(new QTime(9, 0, 0, 0), new QTime(16, 30, 0, 0));
	timeLabel.setText(String.format(tr("Appointment time (between %0$s and %1$s):"),
					timeEdit.minimumTime().toString(Qt.ISODate),
					timeEdit.maximumTime().toString(Qt.ISODate)));

	meetingLabel = new QLabel();
	meetingEdit = new QDateTimeEdit(QDateTime.currentDateTime());

	QLabel formatLabel = new QLabel(tr("Format string for the meeting date and time:"));
	QComboBox formatComboBox = new QComboBox();
	formatComboBox.addItem("yyyy-MM-dd hh:mm:ss (zzz 'ms')");
	formatComboBox.addItem("hh:mm:ss MM/dd/yyyy");
	formatComboBox.addItem("hh:mm:ss dd/MM/yyyy");
	formatComboBox.addItem("hh:mm:ss");
	formatComboBox.addItem("hh:mm ap");

	formatComboBox.activated.connect(this, "setFormatString(String)");

	setFormatString(formatComboBox.currentText());

	QVBoxLayout editsLayout = new QVBoxLayout();
	editsLayout.addWidget(dateLabel);
	editsLayout.addWidget(dateEdit);
	editsLayout.addWidget(timeLabel);
	editsLayout.addWidget(timeEdit);
	editsLayout.addWidget(meetingLabel);
	editsLayout.addWidget(meetingEdit);
	editsLayout.addWidget(formatLabel);
	editsLayout.addWidget(formatComboBox);
	editsGroup.setLayout(editsLayout);
    }

    public void setFormatString(String formatString) {
	meetingEdit.setDisplayFormat(formatString);
	if ((meetingEdit.displayedSections() & QDateTimeEdit.DateSections_Mask) != 0) {
	    meetingEdit.setDateRange(new QDate(2004, 11, 1), new QDate(2005, 11, 30));
	    meetingLabel.setText(String.format(tr("Meeting date (between %0$s and %1$s):"),
						meetingEdit.minimumDate().toString(Qt.ISODate),
						meetingEdit.maximumDate().toString(Qt.ISODate)));
	} else {
	    meetingEdit.setTimeRange(new QTime(0, 7, 20, 0), new QTime(21, 0, 0, 0));
	    meetingLabel.setText(String.format(tr("Meeting time (between %0$s and %1$s):"),
					       meetingEdit.minimumTime().toString(Qt.ISODate),
					       meetingEdit.maximumTime().toString(Qt.ISODate)));
	}
    }

    public void createDoubleSpinBoxes() {
	doubleSpinBoxesGroup = new QGroupBox(tr("Double precision spinboxes"));

	QLabel precisionLabel = new QLabel(tr("Number of decimal places to show:"));
	QSpinBox precisionSpinBox = new QSpinBox();
	precisionSpinBox.setRange(0, 13);
	precisionSpinBox.setValue(2);

	QLabel doubleLabel = 
	    new QLabel(String.format(tr("Enter a value between %1$d and %2$d:"), -20, 20));
	doubleSpinBox = new QDoubleSpinBox();
	doubleSpinBox.setRange(-20.0, 20.0);
	doubleSpinBox.setSingleStep(1.0);
	doubleSpinBox.setValue(0.0);

	QLabel scaleLabel = 
	    new QLabel(String.format(tr("Enter a scale factor between %1$d and %2$d:"), 0, 1000));
	scaleSpinBox = new QDoubleSpinBox();
	scaleSpinBox.setRange(0.0, 1000.0);
	scaleSpinBox.setSingleStep(10.0);
	scaleSpinBox.setSuffix("%");
	scaleSpinBox.setSpecialValueText(tr("No scaling"));
	scaleSpinBox.setValue(100.0);

	QLabel priceLabel = 
	    new QLabel(String.format(tr("Enter a price between %1$d and %2$d:"), 0, 1000));
	priceSpinBox = new QDoubleSpinBox();
	priceSpinBox.setRange(0.0, 1000.0);
	priceSpinBox.setSingleStep(1.0);
	priceSpinBox.setPrefix("$");
	priceSpinBox.setValue(99.99);

	precisionSpinBox.valueChanged.connect(this, "changePrecision(int)");

	QVBoxLayout spinBoxLayout = new QVBoxLayout();
	spinBoxLayout.addWidget(precisionLabel);
	spinBoxLayout.addWidget(precisionSpinBox);
	spinBoxLayout.addWidget(doubleLabel);
	spinBoxLayout.addWidget(doubleSpinBox);
	spinBoxLayout.addWidget(scaleLabel);
	spinBoxLayout.addWidget(scaleSpinBox);
	spinBoxLayout.addWidget(priceLabel);
	spinBoxLayout.addWidget(priceSpinBox);
	doubleSpinBoxesGroup.setLayout(spinBoxLayout);
    }

    public void changePrecision(int decimals) {
	doubleSpinBox.setDecimals(decimals);
	scaleSpinBox.setDecimals(decimals);
	priceSpinBox.setDecimals(decimals);
    }

    public static void main(String args[]) {  
        QApplication.initialize(args);
	
        SpinBoxes spinBoxes = new SpinBoxes();
        spinBoxes.show();

        QApplication.exec();
    }	
}
