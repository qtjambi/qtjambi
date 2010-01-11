/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** $BEGIN_LICENSE$
** GNU Lesser General Public License Usage
** This file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
**
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
** $END_LICENSE$

**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.autotests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.trolltech.qt.core.QBitArray;
import com.trolltech.qt.core.QByteArray;
import com.trolltech.qt.core.QByteArrayMatcher;
import com.trolltech.qt.core.QDateTime;
import com.trolltech.qt.core.QDir;
import com.trolltech.qt.core.QFileInfo;
import com.trolltech.qt.core.QLocale;
import com.trolltech.qt.core.QPersistentModelIndex;
import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.QRegExp;
import com.trolltech.qt.core.QStringMatcher;
import com.trolltech.qt.core.QUrl;
import com.trolltech.qt.core.QXmlStreamAttribute;
import com.trolltech.qt.core.QXmlStreamEntityDeclaration;
import com.trolltech.qt.core.QXmlStreamNamespaceDeclaration;
import com.trolltech.qt.core.QXmlStreamNotationDeclaration;
import com.trolltech.qt.core.Qt.AlignmentFlag;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QColormap;
import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QFontInfo;
import com.trolltech.qt.gui.QFontMetrics;
import com.trolltech.qt.gui.QFontMetricsF;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QImage;
import com.trolltech.qt.gui.QKeySequence;
import com.trolltech.qt.gui.QMatrix;
import com.trolltech.qt.gui.QPainterPath;
import com.trolltech.qt.gui.QPalette;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QPicture;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QRegion;
import com.trolltech.qt.gui.QStyleOption;
import com.trolltech.qt.gui.QTextBlock;
import com.trolltech.qt.gui.QTextCursor;
import com.trolltech.qt.gui.QTextDocumentFragment;
import com.trolltech.qt.gui.QTextFormat;
import com.trolltech.qt.gui.QTextFragment;
import com.trolltech.qt.gui.QTextFrame_iterator;
import com.trolltech.qt.gui.QTextOption;
import com.trolltech.qt.gui.QTextTableCell;
import com.trolltech.qt.gui.QTransform;
import com.trolltech.qt.gui.QTreeWidget;
import com.trolltech.qt.gui.QTreeWidgetItemIterator;
import com.trolltech.qt.network.QAuthenticator;
import com.trolltech.qt.network.QHostAddress;
import com.trolltech.qt.network.QHostInfo;
import com.trolltech.qt.network.QHttpRequestHeader;
import com.trolltech.qt.network.QHttpResponseHeader;
import com.trolltech.qt.network.QNetworkAddressEntry;
import com.trolltech.qt.network.QNetworkCookie;
import com.trolltech.qt.network.QNetworkInterface;
import com.trolltech.qt.network.QNetworkProxy;
import com.trolltech.qt.network.QNetworkRequest;
import com.trolltech.qt.network.QUrlInfo;
import com.trolltech.qt.xml.QDomAttr;
import com.trolltech.qt.xml.QDomCDATASection;
import com.trolltech.qt.xml.QDomCharacterData;
import com.trolltech.qt.xml.QDomComment;
import com.trolltech.qt.xml.QDomDocument;
import com.trolltech.qt.xml.QDomDocumentFragment;
import com.trolltech.qt.xml.QDomDocumentType;
import com.trolltech.qt.xml.QDomElement;
import com.trolltech.qt.xml.QDomEntity;
import com.trolltech.qt.xml.QDomEntityReference;
import com.trolltech.qt.xml.QDomImplementation;
import com.trolltech.qt.xml.QDomNamedNodeMap;
import com.trolltech.qt.xml.QDomNode;
import com.trolltech.qt.xml.QDomNodeList;
import com.trolltech.qt.xml.QDomNotation;
import com.trolltech.qt.xml.QDomProcessingInstruction;
import com.trolltech.qt.xml.QDomText;

public class TestCloneable extends QApplicationTest {

    @Test
    public void run_clone_QAuthenticator() {
        QAuthenticator

        org = new QAuthenticator();
        QAuthenticator clone = org.clone();
        org.dispose();
        QAuthenticator clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QBitArray() {
        QBitArray org = new QBitArray();
        QBitArray clone = org.clone();
        org.dispose();
        QBitArray clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QBrush() {
        QBrush org = new QBrush();
        QBrush clone = org.clone();
        org.dispose();
        QBrush clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QByteArray() {
        QByteArray org = new QByteArray();
        QByteArray clone = org.clone();
        org.dispose();
        QByteArray clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QByteArrayMatcher() {
        QByteArrayMatcher org = new QByteArrayMatcher(new QByteArray("1234"));
        QByteArrayMatcher clone = org.clone();
        org.dispose();
        QByteArrayMatcher clone2 = clone.clone();
        assertEquals(clone.pattern(), clone2.pattern());
    }

    @Test
    public void run_clone_QColor() {
        QColor org = new QColor();
        QColor clone = org.clone();
        org.dispose();
        QColor clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QColormap() {
        QColormap org = QColormap.instance(0);
        QColormap clone = org.clone();
        org.dispose();
        QColormap clone2 = clone.clone();
        assertEquals(clone.depth(), clone2.depth());
    }

    @Test
    public void run_clone_QCursor() {
        QCursor org = new QCursor();
        QCursor clone = org.clone();
        org.dispose();
        QCursor clone2 = clone.clone();
        assertEquals(clone.hotSpot(), clone2.hotSpot());
    }

    @Test
    public void run_clone_QDateTime() {
        QDateTime org = new QDateTime();
        QDateTime clone = org.clone();
        org.dispose();
        QDateTime clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDir() {
        QDir org = new QDir();
        QDir clone = org.clone();
        org.dispose();
        QDir clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomAttr() {
        QDomAttr org = new QDomAttr();
        QDomAttr clone = org.clone();
        org.dispose();
        QDomAttr clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomCDATASection() {
        QDomCDATASection org = new QDomCDATASection();
        QDomCDATASection clone = org.clone();
        org.dispose();
        QDomCDATASection clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomCharacterData() {
        QDomCharacterData org = new QDomCharacterData();
        QDomCharacterData clone = org.clone();
        org.dispose();
        QDomCharacterData clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomComment() {
        QDomComment org = new QDomComment();
        QDomComment clone = org.clone();
        org.dispose();
        QDomComment clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomDocument() {
        QDomDocument org = new QDomDocument();
        QDomDocument clone = org.clone();
        org.dispose();
        QDomDocument clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomDocumentFragment() {
        QDomDocumentFragment org = new QDomDocumentFragment();
        QDomDocumentFragment clone = org.clone();
        org.dispose();
        QDomDocumentFragment clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomDocumentType() {
        QDomDocumentType org = new QDomDocumentType();
        QDomDocumentType clone = org.clone();
        org.dispose();
        QDomDocumentType clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomElement() {
        QDomElement org = new QDomElement();
        QDomElement clone = org.clone();
        org.dispose();
        QDomElement clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomEntity() {
        QDomEntity org = new QDomEntity();
        QDomEntity clone = org.clone();
        org.dispose();
        QDomEntity clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomEntityReference() {
        QDomEntityReference org = new QDomEntityReference();
        QDomEntityReference clone = org.clone();
        org.dispose();
        QDomEntityReference clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomImplementation() {
        QDomImplementation org = new QDomImplementation();
        QDomImplementation clone = org.clone();
        org.dispose();
        QDomImplementation clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomNamedNodeMap() {
        QDomNamedNodeMap org = new QDomNamedNodeMap();
        QDomNamedNodeMap clone = org.clone();
        org.dispose();
        QDomNamedNodeMap clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomNode() {
        QDomNode org = new QDomNode();
        QDomNode clone = org.clone();
        org.dispose();
        QDomNode clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomNodeList() {
        QDomNodeList org = new QDomNodeList();
        QDomNodeList clone = org.clone();
        org.dispose();
        QDomNodeList clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomNotation() {
        QDomNotation org = new QDomNotation();
        QDomNotation clone = org.clone();
        org.dispose();
        QDomNotation clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomProcessingInstruction() {
        QDomProcessingInstruction org = new QDomProcessingInstruction();
        QDomProcessingInstruction clone = org.clone();
        org.dispose();
        QDomProcessingInstruction clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomText() {
        QDomText org = new QDomText();
        QDomText clone = org.clone();
        org.dispose();
        QDomText clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QFileInfo() {
        QFileInfo org = new QFileInfo();
        QFileInfo clone = org.clone();
        org.dispose();
        QFileInfo clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QFont() {
        QFont org = new QFont();
        QFont clone = org.clone();
        org.dispose();
        QFont clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QFontInfo() {
        QFontInfo org = new QFontInfo(new QFont("frodo"));
        QFontInfo clone = org.clone();
        org.dispose();
        QFontInfo clone2 = clone.clone();
        assertEquals(clone.family(), clone2.family());
    }

    @Test
    public void run_clone_QFontMetrics() {
        QFontMetrics org = new QFontMetrics(new QFont());
        QFontMetrics clone = org.clone();
        org.dispose();
        QFontMetrics clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QFontMetricsF() {
        QFontMetricsF org = new QFontMetricsF(new QFont());
        QFontMetricsF clone = org.clone();
        org.dispose();
        QFontMetricsF clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QHostAddress() {
        QHostAddress org = new QHostAddress();
        QHostAddress clone = org.clone();
        org.dispose();
        QHostAddress clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QHostInfo() {
        QHostInfo org = new QHostInfo();
        QHostInfo clone = org.clone();
        org.dispose();
        QHostInfo clone2 = clone.clone();
        assertEquals(clone.hostName(), clone2.hostName());
    }

    @Test
    public void run_clone_QHttpRequestHeader() {
        QHttpRequestHeader org = new QHttpRequestHeader();
        QHttpRequestHeader clone = org.clone();
        org.dispose();
        QHttpRequestHeader clone2 = clone.clone();
        assertEquals(clone.toString(), clone2.toString());
    }

    @Test
    public void run_clone_QHttpResponseHeader() {
        QHttpResponseHeader org = new QHttpResponseHeader();
        QHttpResponseHeader clone = org.clone();
        org.dispose();
        QHttpResponseHeader clone2 = clone.clone();
        assertEquals(clone.toString(), clone2.toString());
    }

    @Test
    public void run_clone_QIcon() {
        QIcon org = new QIcon("file");
        QIcon clone = org.clone();
        org.dispose();
        QIcon clone2 = clone.clone();
        assertEquals(clone.isNull(), clone2.isNull());
    }

    @Test
    public void run_clone_QImage() {
        QImage org = new QImage();
        QImage clone = org.clone();
        org.dispose();
        QImage clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QKeySequence() {
        QKeySequence org = new QKeySequence();
        QKeySequence clone = org.clone();
        org.dispose();
        QKeySequence clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QLocale() {
        QLocale org = new QLocale();
        QLocale clone = org.clone();
        org.dispose();
        QLocale clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QMatrix() {
        QMatrix org = new QMatrix();
        QMatrix clone = org.clone();
        org.dispose();
        QMatrix clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QNetworkAddressEntry() {
        QNetworkAddressEntry org = new QNetworkAddressEntry();
        QNetworkAddressEntry clone = org.clone();
        org.dispose();
        QNetworkAddressEntry clone2 = clone.clone();
        assertEquals(clone.broadcast(), clone2.broadcast());
    }

    @Test
    public void run_clone_QNetworkCookie() {
        QNetworkCookie org = new QNetworkCookie();
        QNetworkCookie clone = org.clone();
        org.dispose();
        QNetworkCookie clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QNetworkInterface() {
        QNetworkInterface org = new QNetworkInterface();
        QNetworkInterface clone = org.clone();
        org.dispose();
        QNetworkInterface clone2 = clone.clone();
        assertEquals(clone.toString(), clone2.toString());
    }

    @Test
    public void run_clone_QNetworkProxy() {
        QNetworkProxy org = new QNetworkProxy();
        QNetworkProxy clone = org.clone();
        org.dispose();
        QNetworkProxy clone2 = clone.clone();
        assertEquals(clone.hostName(), clone2.hostName());
    }

    @Test
    public void run_clone_QNetworkRequest() {
        QNetworkRequest org = new QNetworkRequest();
        QNetworkRequest clone = org.clone();
        org.dispose();
        QNetworkRequest clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QPainterPath() {
        QPainterPath org = new QPainterPath();
        QPainterPath clone = org.clone();
        org.dispose();
        QPainterPath clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QPalette() {
        QPalette org = new QPalette();
        QPalette clone = org.clone();
        org.dispose();
        QPalette clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QPen() {
        QPen org = new QPen();
        QPen clone = org.clone();
        org.dispose();
        QPen clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QPersistentModelIndex() {
        QPersistentModelIndex org = new QPersistentModelIndex();
        QPersistentModelIndex clone = org.clone();
        org.dispose();
        QPersistentModelIndex clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QPicture() {
        QPicture org = new QPicture();
        QPicture clone = org.clone();
        org.dispose();
        QPicture clone2 = clone.clone();
        assertEquals(clone.size(), clone2.size());
    }

    @Test
    public void run_clone_QPixmap() {
        QPixmap org = new QPixmap(4, 5);
        QPixmap clone = org.clone();
        org.dispose();
        QPixmap clone2 = clone.clone();
        assertEquals(clone.rect(), clone2.rect());
    }

    @Test
    public void run_clone_QRegExp() {
        QRegExp org = new QRegExp();
        QRegExp clone = org.clone();
        org.dispose();
        QRegExp clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QRegion() {
        QRegion org = new QRegion();
        QRegion clone = org.clone();
        org.dispose();
        QRegion clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QStringMatcher() {
        QStringMatcher org = new QStringMatcher("pattern");
        QStringMatcher clone = org.clone();
        org.dispose();
        QStringMatcher clone2 = clone.clone();
        assertEquals(clone.pattern(), clone2.pattern());
    }

    @Test
    public void run_clone_QStyleOption() {
        QStyleOption org = new QStyleOption();
        org.setRect(new QRect(5, 5, 1, 1));
        QStyleOption clone = org.clone();
        org.dispose();
        QStyleOption clone2 = clone.clone();
        assertEquals(clone.rect(), clone2.rect());
    }

    @Test
    public void run_clone_QTextBlock() {
        QTextBlock org = new QTextBlock();
        QTextBlock clone = org.clone();
        org.dispose();
        QTextBlock clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QTextCursor() {
        QTextCursor org = new QTextCursor();
        QTextCursor clone = org.clone();
        org.dispose();
        QTextCursor clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QTextDocumentFragment() {
        QTextDocumentFragment org = new QTextDocumentFragment();
        QTextDocumentFragment clone = org.clone();
        org.dispose();
        QTextDocumentFragment clone2 = clone.clone();
        assertEquals(clone.toPlainText(), clone2.toPlainText());
    }

    @Test
    public void run_clone_QTextFormat() {
        QTextFormat org = new QTextFormat();
        QTextFormat clone = org.clone();
        org.dispose();
        QTextFormat clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QTextFragment() {
        QTextFragment org = new QTextFragment();
        QTextFragment clone = org.clone();
        org.dispose();
        QTextFragment clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QTextFrame_iterator() {
        QTextFrame_iterator org = new QTextFrame_iterator();
        QTextFrame_iterator clone = org.clone();
        org.dispose();
        QTextFrame_iterator clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QTextOption() {
        QTextOption org = new QTextOption();
        org.setAlignment(AlignmentFlag.AlignHCenter);
        QTextOption clone = org.clone();
        org.dispose();
        QTextOption clone2 = clone.clone();
        assertEquals(clone.alignment(), clone2.alignment());
    }

    @Test
    public void run_clone_QTextTableCell() {
        QTextTableCell org = new QTextTableCell();
        QTextTableCell clone = org.clone();
        org.dispose();
        QTextTableCell clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QTransform() {
        QTransform org = new QTransform();
        QTransform clone = org.clone();
        org.dispose();
        QTransform clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QTreeWidgetItemIterator() {
        QTreeWidgetItemIterator org = new QTreeWidgetItemIterator(new QTreeWidget());
        QTreeWidgetItemIterator clone = org.clone();
        org.dispose();
        QTreeWidgetItemIterator clone2 = clone.clone();
        assertEquals(clone.current(), clone2.current());
    }

    @Test
    public void run_clone_QUrl() {
        QUrl org = new QUrl();
        QUrl clone = org.clone();
        org.dispose();
        QUrl clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QUrlInfo() {
        QUrlInfo org = new QUrlInfo();
        QUrlInfo clone = org.clone();
        org.dispose();
        QUrlInfo clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QXmlStreamAttribute() {
        QXmlStreamAttribute org = new QXmlStreamAttribute();
        QXmlStreamAttribute clone = org.clone();
        org.dispose();
        QXmlStreamAttribute clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QXmlStreamEntityDeclaration() {
        QXmlStreamEntityDeclaration org = new QXmlStreamEntityDeclaration();
        QXmlStreamEntityDeclaration clone = org.clone();
        org.dispose();
        QXmlStreamEntityDeclaration clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QXmlStreamNamespaceDeclaration() {
        QXmlStreamNamespaceDeclaration org = new QXmlStreamNamespaceDeclaration();
        QXmlStreamNamespaceDeclaration clone = org.clone();
        org.dispose();
        QXmlStreamNamespaceDeclaration clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QXmlStreamNotationDeclaration() {
        QXmlStreamNotationDeclaration org = new QXmlStreamNotationDeclaration();
        QXmlStreamNotationDeclaration clone = org.clone();
        org.dispose();
        QXmlStreamNotationDeclaration clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    public static void main(String args[]) {
        org.junit.runner.JUnitCore.main(TestCloneable.class.getName());
    }
}
