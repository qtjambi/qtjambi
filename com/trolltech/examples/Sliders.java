package com.trolltech.examples;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

@QtJambiExample(name = "Sliders")
//! [0]
public class Sliders extends QWidget
{
    private SlidersGroup horizontalSliders;
    private SlidersGroup verticalSliders;
    private QStackedWidget stackedWidget;

    private QGroupBox controlsGroup;
    private QLabel minimumLabel;
    private QLabel maximumLabel;
    private QLabel valueLabel;
    private QCheckBox invertedAppearance;
    private QCheckBox invertedKeyBindings;
    private QSpinBox minimumSpinBox;
    private QSpinBox maximumSpinBox;
    private QSpinBox valueSpinBox;
    private QComboBox orientationCombo;
//! [0]


//! [1]
    public Sliders()
    {
        horizontalSliders = new SlidersGroup(Qt.Orientation.Horizontal, tr("Horizontal"));
        verticalSliders = new SlidersGroup(Qt.Orientation.Vertical, tr("Vertical"));

        stackedWidget = new QStackedWidget();
        stackedWidget.addWidget(horizontalSliders);
        stackedWidget.addWidget(verticalSliders);

        createControls(tr("Controls"));
//! [1] //! [2]

        horizontalSliders.valueChanged.connect(verticalSliders, "setValue(int)");
        verticalSliders.valueChanged.connect(valueSpinBox, "setValue(int)");
        valueSpinBox.valueChanged.connect(horizontalSliders, "setValue(int)");

        QHBoxLayout layout = new QHBoxLayout();
        layout.addWidget(controlsGroup);
        layout.addWidget(stackedWidget);
        setLayout(layout);

        minimumSpinBox.setValue(0);
        maximumSpinBox.setValue(20);
        valueSpinBox.setValue(5);

        setWindowTitle(tr("Sliders"));
    }
//! [2]

//! [3]
    private void createControls(String title)
    {
        controlsGroup = new QGroupBox(title);

        minimumLabel = new QLabel(tr("Minimum value:"));
        maximumLabel = new QLabel(tr("Maximum value:"));
        valueLabel = new QLabel(tr("Current value:"));

        invertedAppearance = new QCheckBox(tr("Inverted appearance"));
        invertedKeyBindings = new QCheckBox(tr("Inverted key bindings"));

//! [3] //! [4]
        minimumSpinBox = new QSpinBox();
//! [4] //! [5]
        minimumSpinBox.setRange(-100, 100);
        minimumSpinBox.setSingleStep(1);

        maximumSpinBox = new QSpinBox();
        maximumSpinBox.setRange(-100, 100);
        maximumSpinBox.setSingleStep(1);

        valueSpinBox = new QSpinBox();
        valueSpinBox.setRange(-100, 100);
        valueSpinBox.setSingleStep(1);

        orientationCombo = new QComboBox();
        orientationCombo.addItem(tr("Horizontal slider-like widgets"));
        orientationCombo.addItem(tr("Vertical slider-like widgets"));

//! [5] //! [6]
        orientationCombo.activatedIndex.connect(stackedWidget, "setCurrentIndex(int)");
//! [6] //! [7]
        minimumSpinBox.valueChanged.connect(horizontalSliders, "setMinimum(int)");
        minimumSpinBox.valueChanged.connect(verticalSliders, "setMinimum(int)");
        maximumSpinBox.valueChanged.connect(horizontalSliders, "setMaximum(int)");
        maximumSpinBox.valueChanged.connect(verticalSliders, "setMaximum(int)");
        invertedAppearance.toggled.connect(horizontalSliders, "invertAppearance(boolean)");
        invertedAppearance.toggled.connect(verticalSliders, "invertAppearance(boolean)");
        invertedKeyBindings.toggled.connect(horizontalSliders, "invertedKeyBindings(boolean)");
        invertedKeyBindings.toggled.connect(verticalSliders, "invertedKeyBindings(boolean)");

        QGridLayout controlsLayout = new QGridLayout();
        controlsLayout.addWidget(minimumLabel, 0, 0);
        controlsLayout.addWidget(maximumLabel, 1, 0);
        controlsLayout.addWidget(valueLabel, 2, 0);
        controlsLayout.addWidget(minimumSpinBox, 0, 1);
        controlsLayout.addWidget(maximumSpinBox, 1, 1);
        controlsLayout.addWidget(valueSpinBox, 2, 1);
        controlsLayout.addWidget(invertedAppearance, 0, 2);
        controlsLayout.addWidget(invertedKeyBindings, 1, 2);
        controlsLayout.addWidget(orientationCombo, 3, 0, 1, 3);
        controlsGroup.setLayout(controlsLayout);
    }
//! [7]

    public static void main(String args[])
    {
        QApplication.initialize(args);

        new Sliders().show();

        QApplication.exec();
    }
}

//! [8]
class SlidersGroup extends QGroupBox
{
    private QSlider slider;
    private QScrollBar scrollBar;
    private QDial dial;

    public Signal1<Integer> valueChanged = new Signal1<Integer>();
//! [8]

//! [9]
    public SlidersGroup(Qt.Orientation orientation, String title)
    {
        slider = new QSlider(orientation);
        slider.setFocusPolicy(Qt.FocusPolicy.StrongFocus);
        slider.setTickPosition(QSlider.TickPosition.TicksBothSides);
        slider.setTickInterval(10);
        slider.setSingleStep(1);

        scrollBar = new QScrollBar(orientation);
        scrollBar.setFocusPolicy(Qt.FocusPolicy.StrongFocus);

        dial = new QDial();
        dial.setFocusPolicy(Qt.FocusPolicy.StrongFocus);

        slider.valueChanged.connect(scrollBar, "setValue(int)");
        scrollBar.valueChanged.connect(dial, "setValue(int)");
//! [9] //! [10]
        dial.valueChanged.connect(slider, "setValue(int)");
//! [10] //! [11]
        dial.valueChanged.connect(valueChanged);

//! [11] //! [12]
        QBoxLayout.Direction direction;
//! [12] //! [13]

        if (orientation == Qt.Orientation.Horizontal)
            direction = QBoxLayout.Direction.TopToBottom;
        else
            direction = QBoxLayout.Direction.LeftToRight;

        QBoxLayout slidersLayout = new QBoxLayout(direction);
        slidersLayout.addWidget(slider);
        slidersLayout.addWidget(scrollBar);
        slidersLayout.addWidget(dial);
        setLayout(slidersLayout);
    }
//! [13]

//! [14]
    public void setValue(int value)
    {
        slider.setValue(value);
    }
//! [14]

//! [15]
    public void setMinimum(int value)
    {
        slider.setMinimum(value);
        scrollBar.setMinimum(value);
        dial.setMinimum(value);
    }
//! [15]

//! [16]
    public void setMaximum(int value)
    {
        slider.setMaximum(value);
        scrollBar.setMaximum(value);
        dial.setMaximum(value);
    }
//! [16]

//! [17]
    public void invertAppearance(boolean invert)
    {
        slider.setInvertedAppearance(invert);
        scrollBar.setInvertedAppearance(invert);
        dial.setInvertedAppearance(invert);
    }
//! [17]

//! [18]
    public void invertedKeyBindings(boolean invert)
    {
        slider.setInvertedControls(invert);
        scrollBar.setInvertedControls(invert);
        dial.setInvertedControls(invert);
    }
//! [18]
}
