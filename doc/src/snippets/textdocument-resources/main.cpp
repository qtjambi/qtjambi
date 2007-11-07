#include <QtGui>
#include <QtNetwork>

#include "httpresource.h"

QString tr(const char *text)
{
    return QApplication::translate(text, text);
}

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    QTextEdit *editor = new QTextEdit;

    QTextDocument *document = new QTextDocument(editor);
    QTextCursor cursor(document);

    HttpResource http(QUrl("http://www.trolltech.com/images/logos/newlogo.gif"));
    QByteArray imageData = http.getData();
    
    document->addResource(QTextDocument::ImageResource,
        QUrl("http://www.trolltech.com/images/logos/newlogo.gif"),
        QVariant(imageData));

    QTextImageFormat imageFormat;
    imageFormat.setName("http://www.trolltech.com/images/logos/newlogo.gif");
    cursor.insertImage(imageFormat);

    cursor.insertBlock();
    cursor.insertText("Code less. Create more.");

    editor->setDocument(document);
    editor->setWindowTitle(tr("Text Document Images"));
    editor->resize(320, 480);
    editor->show();
    return app.exec();
}
