#include <QtGui>

void myProcessing(const QString &)
{
}

int main()
{
    QWidget myWidget;
    {
        // RECORD
//! [0]
        QPicture picture;
        QPainter painter;
        painter.begin(&picture);           // paint in picture
        painter.drawEllipse(10,20, 80,70); // draw an ellipse
        painter.end();                     // painting done
        picture.save("drawing.pic");       // save picture
//! [0]
    }

    {
        // REPLAY
//! [1]
        QPicture picture;
        picture.load("drawing.pic");           // load picture
        QPainter painter;
        painter.begin(&myWidget);              // paint in myWidget
        painter.drawPicture(0, 0, picture);    // draw the picture at (0,0)
        painter.end();                         // painting done
//! [1]
    }

    QPicture myPicture;
    {
        // FORMATS
//! [2]
        QStringList list = QPicture::inputFormatList();
        foreach (QString string, list)
            myProcessing(string);
//! [2]
    }

    {
        // OUTPUT
//! [3]
        QStringList list = QPicture::outputFormatList();
        foreach (QString string, list)
            myProcessing(string);
//! [3]
    }

    {
        // PIC READ
//! [4]
        QPictureIO iio;
        QPixmap  pixmap;
        iio.setFileName("vegeburger.pic");
        if (iio.read()) {        // OK
            QPicture picture = iio.picture();
            QPainter painter(&pixmap);
            painter.drawPicture(0, 0, picture);
        }
//! [4]
    }

    {
        QPixmap pixmap;
        // PIC WRITE
//! [5]
        QPictureIO iio;
        QPicture   picture;
        QPainter painter(&picture);
        painter.drawPixmap(0, 0, pixmap);
        iio.setPicture(picture);
        iio.setFileName("vegeburger.pic");
        iio.setFormat("PIC");
        if (iio.write())
            return true; // returned true if written successfully
//! [5]
    }

}

// SVG READ
//! [6]
void readSVG(QPictureIO *picture)
{
    // read the picture using the picture->ioDevice()
}
//! [6]

// SVG WRITE
//! [7]
void writeSVG(QPictureIO *picture)
{
    // write the picture using the picture->ioDevice()
}
//! [7]

// USE SVG
void foo() {

//! [8]
    // add the SVG picture handler
    // ...
//! [8]
    QPictureIO::defineIOHandler("SVG", 0, 0, readSVG, writeSVG);
    // ...

}
