#include <QtGui>

int main()
{
    int x, y;
    {
        // BIT ACCESS
        QImage image;
        // sets bit at (x, y) to 1
        if (image.format() == QImage::Format_MonoLSB)
            image.scanLine(y)[x >> 3] |= 1 << (x & 7);
        else
            image.scanLine(y)[x >> 3] |= 1 << (7 - (x & 7));
    }

    {
        // 8-BIT ACCESS
        QImage image;
        // set entry 19 in the color table to yellow
        image.setColor(19, qRgb(255, 255, 0));

        // set 8 bit pixel at (x,y) to value yellow (in color table)
        image.scanLine(y)[x] = 19;
    }

    {
        // 32-BIT
        QImage image;
        // sets 32 bit pixel at (x,y) to yellow.
        uint *ptr = reinterpret_cast<uint *>(image.scanLine(y)) + x;
        *ptr = qRgb(255, 255, 0);
    }

    {
        // SAVE
//! [0]
        QImage image;
        QByteArray ba;
        QBuffer buffer(&ba);
        buffer.open(QIODevice::WriteOnly);
        image.save(&buffer, "PNG"); // writes image into ba in PNG format
//! [0]
    }

    {
        // PIX SAVE
//! [1]
        QPixmap pixmap;
        QByteArray bytes;
        QBuffer buffer(&bytes);
        buffer.open(QIODevice::WriteOnly);
        pixmap.save(&buffer, "PNG"); // writes pixmap into bytes in PNG format
//! [1]
    }

    {
        // MASK
//! [2]
        QPixmap alpha("image-with-alpha.png");
        QPixmap alphacopy = alpha;
        alphacopy.setMask(alphacopy.mask());
//! [2]
    }
}
