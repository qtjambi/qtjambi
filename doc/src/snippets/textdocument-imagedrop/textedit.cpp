#include "textedit.h"
#include <QtGui>

TextEdit::TextEdit(QWidget *parent)
	: QTextEdit(parent)
{
}

//! [0]
bool TextEdit::canInsertFromMimeData( const QMimeData *source ) const
{
    if (source->hasImage())
        return true;
    else
        return QTextEdit::canInsertFromMimeData(source);
}
//! [0]

//! [1]
void TextEdit::insertFromMimeData( const QMimeData *source )
{
    if (source->hasImage())
    {
        QImage image = qvariant_cast<QImage>(source->imageData());
        QTextCursor cursor = this->textCursor();
        QTextDocument *document = this->document();
        document->addResource(QTextDocument::ImageResource, QUrl("image"), image);
        cursor.insertImage("image");
    }
}
//! [1]
