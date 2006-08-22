/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ Trolltech AS. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#include "cppwriteicondata.h"
#include "driver.h"
#include "ui4.h"
#include "uic.h"

#include <QTextStream>

namespace CPP {

static QByteArray unzipXPM(QString data, ulong& length)
{
#ifndef QT_NO_COMPRESS
    const int lengthOffset = 4;
    int baSize = data.length() / 2 + lengthOffset;
    uchar *ba = new uchar[baSize];
    for (int i = lengthOffset; i < baSize; ++i) {
        char h = data[2 * (i-lengthOffset)].toLatin1();
        char l = data[2 * (i-lengthOffset) + 1].toLatin1();
        uchar r = 0;
        if (h <= '9')
            r += h - '0';
        else
            r += h - 'a' + 10;
        r = r << 4;
        if (l <= '9')
            r += l - '0';
        else
            r += l - 'a' + 10;
        ba[i] = r;
    }
    // qUncompress() expects the first 4 bytes to be the expected length of the
    // uncompressed data
    ba[0] = (length & 0xff000000) >> 24;
    ba[1] = (length & 0x00ff0000) >> 16;
    ba[2] = (length & 0x0000ff00) >> 8;
    ba[3] = (length & 0x000000ff);
    QByteArray baunzip = qUncompress(ba, baSize);
    delete[] ba;
    return baunzip;
#else
    Q_UNUSED(data);
    Q_UNUSED(length);
    return QByteArray();
#endif
}

WriteIconData::WriteIconData(Uic *uic)
    : driver(uic->driver()), output(uic->output()), option(uic->option())
{
}

void WriteIconData::acceptUI(DomUI *node)
{
    TreeWalker::acceptUI(node);
}

void WriteIconData::acceptImages(DomImages *images)
{
    TreeWalker::acceptImages(images);
}

void WriteIconData::acceptImage(DomImage *image)
{
    QString img = image->attributeName() + QLatin1String("_data");
    QString data = image->elementData()->text();
    QString fmt = image->elementData()->attributeFormat();
    int size = image->elementData()->attributeLength();

    if (fmt == QLatin1String("XPM.GZ")) {
        ulong length = size;
        QByteArray baunzip = unzipXPM(data, length);
        length = baunzip.size();
        // shouldn't we test the initial 'length' against the
        // resulting 'length' to catch corrupt UIC files?
        int a = 0;
        int column = 0;
        bool inQuote = false;
        output << option.indent << "static const char* const " << img << "[] = { \n";
        while (baunzip[a] != '\"')
            a++;
        for (; a < (int) length; a++) {
            output << baunzip[a];
            if (baunzip[a] == '\n') {
                column = 0;
            } else if (baunzip[a] == '"') {
                inQuote = !inQuote;
            }

            if (column++ >= 511 && inQuote) {
                output << "\"\n\""; // be nice with MSVC & Co.
                column = 1;
            }
        }

        if (! baunzip.trimmed ().endsWith ("};"))
            output << "};";

        output << "\n\n";
    } else {
        output << option.indent << "static const unsigned char " << img << "[] = { \n";
        output << option.indent;
        int a ;
        for (a = 0; a < (int) (data.length()/2)-1; a++) {
            output << "0x" << QString(data[2*a]) << QString(data[2*a+1]) << ",";
            if (a % 12 == 11)
                output << "\n" << option.indent;
            else
                output << " ";
        }
        output << "0x" << QString(data[2*a]) << QString(data[2*a+1]) << "\n";
        output << "};\n\n";
    }
}

} // namespace CPP
