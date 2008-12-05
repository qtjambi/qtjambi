package com.trolltech.examples;

import com.trolltech.qt.*;
import com.trolltech.qt.designer.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.core.*;

import java.util.*;


//@QtJambiExample(name = "Pixmap Filters")
public class PixmapFilters extends QWidget
{
    public static final QSize resultSize = new QSize(150, 150);

    public PixmapFilters()
    {
        controlWidget = new QWidget();
        controls.setupUi(controlWidget);

        sourceLabel = new QLabel();
        sourceLabel.setMinimumWidth(resultSize.width());

        filterComboBox = new QComboBox();
        filterComboBox.setFocusPolicy(Qt.FocusPolicy.NoFocus);

        filterComboBox.addItem(tr("Convolution Filter"), QPixmapFilter.FilterType.ConvolutionFilter);
        filterComboBox.addItem(tr("Colorize Filter"), QPixmapFilter.FilterType.ColorizeFilter);
        filterComboBox.addItem(tr("Drop Shadow Filter"), QPixmapFilter.FilterType.DropShadowFilter);

        backgroundPixmap = new QPixmap("classpath:images/checker.png");
        sourcePixmap = new QPixmap("classpath:images/qt-logo.png");

        QImage fixedImage = new QImage(resultSize, QImage.Format.Format_ARGB32_Premultiplied);
        QPainter painter = new QPainter(fixedImage);
        painter.drawPixmap(pixmapPos(backgroundPixmap), backgroundPixmap);
        painter.drawPixmap(pixmapPos(sourcePixmap), sourcePixmap);
        painter.end();
        sourceLabel.setPixmap(QPixmap.fromImage(fixedImage));

        resultWidget = new FilterWidget(backgroundPixmap, sourcePixmap, controls);
        resultWidget.setMinimumWidth(resultSize.width());
        resultWidget.setMinimumHeight(resultSize.height());

        filterComboBox.activatedIndex.connect(resultWidget, "setType(int)");

        controls.colorizeRedSlider.valueChanged.connect(resultWidget, "update()");
        controls.colorizeGreenSlider.valueChanged.connect(resultWidget, "update()");
        controls.colorizeBlueSlider.valueChanged.connect(resultWidget, "update()");        
        controls.dropShadowRedSlider.valueChanged.connect(resultWidget, "update()");
        controls.dropShadowGreenSlider.valueChanged.connect(resultWidget, "update()");
        controls.dropShadowBlueSlider.valueChanged.connect(resultWidget, "update()");
        controls.dropShadowAlphaSlider.valueChanged.connect(resultWidget, "update()");
        controls.dropShadowXSlider.valueChanged.connect(resultWidget, "update()");
        controls.dropShadowYSlider.valueChanged.connect(resultWidget, "update()");
        controls.dropShadowRadiusSlider.valueChanged.connect(resultWidget, "update()");

        controls.kernel_1x1.textChanged.connect(resultWidget, "update()");
        controls.kernel_2x1.textChanged.connect(resultWidget, "update()");
        controls.kernel_3x1.textChanged.connect(resultWidget, "update()");
        controls.kernel_1x2.textChanged.connect(resultWidget, "update()");
        controls.kernel_2x2.textChanged.connect(resultWidget, "update()");
        controls.kernel_3x2.textChanged.connect(resultWidget, "update()");
        controls.kernel_1x3.textChanged.connect(resultWidget, "update()");
        controls.kernel_2x3.textChanged.connect(resultWidget, "update()");
        controls.kernel_3x3.textChanged.connect(resultWidget, "update()");

        controls.kernel_1x1.setValidator(new QDoubleValidator(this));
        controls.kernel_2x1.setValidator(new QDoubleValidator(this));
        controls.kernel_3x1.setValidator(new QDoubleValidator(this));
        controls.kernel_1x2.setValidator(new QDoubleValidator(this));
        controls.kernel_2x2.setValidator(new QDoubleValidator(this));
        controls.kernel_3x2.setValidator(new QDoubleValidator(this));
        controls.kernel_1x3.setValidator(new QDoubleValidator(this));
        controls.kernel_2x3.setValidator(new QDoubleValidator(this));
        controls.kernel_3x3.setValidator(new QDoubleValidator(this));

        QGridLayout mainLayout = new QGridLayout();
        mainLayout.addWidget(sourceLabel, 0, 0, 3, 1);
        mainLayout.addWidget(filterComboBox, 1, 1);
        mainLayout.addWidget(resultWidget, 0, 2, 3, 1);
        mainLayout.addWidget(controlWidget, 3, 0, 1, 3);
        mainLayout.setSizeConstraint(QLayout.SizeConstraint.SetFixedSize);
        setLayout(mainLayout);

        setWindowTitle(tr("Pixmap Filters"));
    }

    public static QPointF pixmapPos(QPixmap pixmap)
    {
        return new QPointF((resultSize.width() - pixmap.width()) / 2,
                          (resultSize.height() - pixmap.height()) / 2);
    }

    private static class FilterWidget extends QWidget
    {

        public FilterWidget(QPixmap background, QPixmap icon, Ui_ValueControls control)
        {
            backgroundPixmap = background;
            sourcePixmap = icon;
            controls =  control;
            type = QPixmapFilter.FilterType.ConvolutionFilter;

        }

        public void paintEvent(QPaintEvent event)
        {
            QPainter painter = new QPainter(this);
            painter.setClipRect(new QRect(0, 0, PixmapFilters.resultSize.width(), PixmapFilters.resultSize.height()));
            painter.drawPixmap(0, 0, backgroundPixmap);
            QPixmapFilter currentFilter = setupFilter(type);
            currentFilter.draw(painter, pixmapPos(sourcePixmap), sourcePixmap, new QRectF());
            painter.end();
        }

        public void setType(QPixmapFilter.FilterType filterType)
        {
            type = filterType;
            update();
        }

        private void setType(int filterType)
        {
            setType(QPixmapFilter.FilterType.resolve(filterType));
        }

        private double parseDouble(String d)
        {
            if (d.equals(""))
                return 0.0;

            return Double.parseDouble(d);
        }

        private QPixmapFilter setupFilter(QPixmapFilter.FilterType type)
        {
            controls.ConvolutionControlWidget.hide();
            controls.ColorizeControlWidget.hide();
            controls.DropShadowControlWidget.hide();

            QPixmapFilter filter = null;
            double kernel[] = {
                parseDouble(controls.kernel_1x1.text()),
                parseDouble(controls.kernel_2x1.text()),
                parseDouble(controls.kernel_3x1.text()),
                parseDouble(controls.kernel_1x2.text()),
                parseDouble(controls.kernel_2x2.text()),
                parseDouble(controls.kernel_3x2.text()),
                parseDouble(controls.kernel_1x3.text()),
                parseDouble(controls.kernel_2x3.text()),
                parseDouble(controls.kernel_3x3.text())
            };
            QColor color = new QColor();

            switch(type) {
            case ConvolutionFilter:
                controls.ConvolutionControlWidget.show();
                filter = new QPixmapConvolutionFilter();
                ((QPixmapConvolutionFilter)filter).setConvolutionKernel(kernel, 3,3);
                break;

            case ColorizeFilter:
                controls.ColorizeControlWidget.show();
                filter = new QPixmapColorizeFilter();
                color = new QColor(controls.colorizeRedSlider.value(),
                                   controls.colorizeGreenSlider.value(),
                                   controls.colorizeBlueSlider.value());
                ((QPixmapColorizeFilter)filter).setColor(color);
                break;

            case DropShadowFilter:
                controls.DropShadowControlWidget.show();
                filter = new QPixmapDropShadowFilter();
                color = new QColor(controls.dropShadowRedSlider.value(),
                               controls.dropShadowGreenSlider.value(),
                               controls.dropShadowBlueSlider.value(),
                               controls.dropShadowAlphaSlider.value());
                ((QPixmapDropShadowFilter)filter).setColor(color);
                ((QPixmapDropShadowFilter)filter).setBlurRadius(((double) controls.dropShadowRadiusSlider.value())/20.0);
                ((QPixmapDropShadowFilter)filter).setOffset(
                    new QPointF(((double)controls.dropShadowXSlider.value()-500)/10.0,
                                ((double)controls.dropShadowYSlider.value()-500)/10.0
                    ));
                break;
            default:
                break;
            }
            return filter;
        }

        private QPixmap sourcePixmap;
        private QPixmap backgroundPixmap;
        private Ui_ValueControls controls;
        private QPixmapFilter.FilterType type;
    }

    private QLabel sourceLabel;
    private QComboBox filterComboBox;
    private FilterWidget resultWidget;
    private QWidget controlWidget;

    private QPixmap sourcePixmap;
    private QPixmap backgroundPixmap;

    Ui_ValueControls controls = new Ui_ValueControls();

    public static void main(String args[])
    {
        QApplication.initialize(args);

        new PixmapFilters().show();

        QApplication.exec();
    }
}

