#include <QtGui>
#include <iostream>
using namespace std;

class Widget : public QWidget
{
public:
    Widget(QWidget *parent = 0);
};

Widget::Widget(QWidget *parent)
    : QWidget(parent)
{
//! [0]
    QStringList fonts;
    fonts << "Arial" << "Helvetica" << "Times" << "Courier";
//! [0]

//! [1]
    for (int i = 0; i < fonts.size(); ++i)
         cout << fonts.at(i).toLocal8Bit().constData() << endl;
//! [1]

//! [2]
    QStringListIterator javaStyleIterator(fonts);
    while (javaStyleIterator.hasNext())
         cout << javaStyleIterator.next().toLocal8Bit().constData() << endl;
//! [2]

//! [3]
    QStringList::const_iterator constIterator;
    for (constIterator = fonts.constBegin(); constIterator != fonts.constEnd();
           ++constIterator)
        cout << (*constIterator).toLocal8Bit().constData() << endl;
//! [3]

//! [4]
    QString str = fonts.join(",");
     // str == "Arial,Helvetica,Times,Courier"
//! [4]

//! [5] //! [6]
    QStringList list;
//! [5]
    list = str.split(",");
     // list: ["Arial", "Helvetica", "Times", "Courier"]
//! [6]

//! [7]
    QStringList monospacedFonts = fonts.filter(QRegExp("Courier|Fixed"));
//! [7]

//! [8]
    QStringList files;
    files << "$QTDIR/src/moc/moc.y"
          << "$QTDIR/src/moc/moc.l"
          << "$QTDIR/include/qconfig.h";

    files.replaceInStrings("$QTDIR", "/usr/lib/qt");
    // files: [ "/usr/lib/qt/src/moc/moc.y", ...]
//! [8]

    QString str1, str2, str3;
//! [9]
    QStringList longerList = (QStringList() << str1 << str2 << str3);
//! [9]

    list.clear();
//! [10]
    list << "Bill Murray" << "John Doe" << "Bill Clinton";

//! [11]
    QStringList result;
//! [11]
    result = list.filter("Bill");
    // result: ["Bill Murray", "Bill Clinton"]
//! [10]

    result.clear();
//! [12]
    foreach (QString str, list) {
        if (str.contains("Bill"))
            result += str;
    }
//! [12]

    list.clear();
//! [13]
    list << "alpha" << "beta" << "gamma" << "epsilon";
    list.replaceInStrings("a", "o");
    // list == ["olpho", "beto", "gommo", "epsilon"]
//! [13]

    list.clear();
//! [14]
    list << "alpha" << "beta" << "gamma" << "epsilon";
    list.replaceInStrings(QRegExp("^a"), "o");
    // list == ["olpha", "beta", "gamma", "epsilon"]
//! [14]

    list.clear();
//! [15]
    list << "Bill Clinton" << "Murray, Bill";
    list.replaceInStrings(QRegExp("^(.*), (.*)$"), "\\2 \\1");
    // list == ["Bill Clinton", "Bill Murray"]
//! [15]
}

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    Widget widget;
    widget.show();
    return app.exec();
}
