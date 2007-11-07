#include <QtGui>

#include "xmlwriter.h"

QDomDocument *XmlWriter::toXml()
{
    QDomImplementation implementation;
    QDomDocumentType docType = implementation.createDocumentType(
        "scribe-document", "scribe", "www.trolltech.com/scribe");

    document = new QDomDocument(docType);

    // ### This processing instruction is required to ensure that any kind
    // of encoding is given when the document is written.
    QDomProcessingInstruction process = document->createProcessingInstruction(
        "xml", "version=\"1.0\" encoding=\"utf-8\"");
    document->appendChild(process);

    QDomElement documentElement = document->createElement("document");
    document->appendChild(documentElement);

    QTextBlock firstBlock = textDocument->begin();
    createItems(documentElement, firstBlock);

    return document;
}

void XmlWriter::createItems(QDomElement &parent, const QTextBlock &block)
{
    QTextBlock currentBlock = block;

    while (currentBlock.isValid()) {
        QDomElement blockElement = document->createElement("block");
        blockElement.setAttribute("length", currentBlock.length());
        parent.appendChild(blockElement);

        if (!(currentBlock.text().isNull())) {
            QDomText textNode = document->createTextNode(currentBlock.text());
            blockElement.appendChild(textNode);
        }

        currentBlock = currentBlock.next();
    }
}
