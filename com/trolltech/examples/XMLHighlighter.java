/****************************************************************************
**
** Copyright (C) 2008-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $JAVA_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/


package com.trolltech.examples;

import java.util.Vector;

import com.trolltech.qt.core.QRegExp;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.*;

public class XMLHighlighter extends QSyntaxHighlighter {

    public class HighlightingRule {
        public QRegExp pattern;
        public QTextCharFormat format;

        public HighlightingRule(QRegExp pattern, QTextCharFormat format) {
            this.pattern = pattern;
            this.format = format;
        }
    }

    Vector<HighlightingRule> highlightingRules = new Vector<HighlightingRule>();

    QRegExp commentStartExpression;
    QRegExp commentEndExpression;

    QTextCharFormat tagNameFormat = new QTextCharFormat();
    QTextCharFormat commentFormat = new QTextCharFormat();
    QTextCharFormat quotationFormat = new QTextCharFormat();
    QTextCharFormat attributeFormat = new QTextCharFormat();

    public XMLHighlighter(QTextDocument parent) {

        super(parent);

        HighlightingRule rule;
        QBrush brush;
        QRegExp pattern;

        // Tagname
        brush = new QBrush(QColor.darkMagenta);
        tagNameFormat.setForeground(brush);
        tagNameFormat.setFontWeight(QFont.Weight.Bold.value());
        pattern = new QRegExp("<[A-Za-z//-]+");
        rule = new HighlightingRule(pattern, tagNameFormat);
        highlightingRules.add(rule);
        pattern = new QRegExp(">");
        rule = new HighlightingRule(pattern, tagNameFormat);
        highlightingRules.add(rule);

        // Attribute 
        brush = new QBrush(QColor.darkGreen);
        attributeFormat.setForeground(brush);
        attributeFormat.setFontWeight(QFont.Weight.Bold.value());
        pattern = new QRegExp("[A-Za-z//-]+=");
        rule = new HighlightingRule(pattern, attributeFormat);
        highlightingRules.add(rule);
        
        
        // String
        brush = new QBrush(QColor.blue, Qt.BrushStyle.SolidPattern);
        pattern = new QRegExp("\".*\"");
        pattern.setMinimal(true);
        quotationFormat.setForeground(brush);
        rule = new HighlightingRule(pattern, quotationFormat);
        highlightingRules.add(rule);
        
        // Block comment
        brush = new QBrush(QColor.gray, Qt.BrushStyle.SolidPattern);
        commentFormat.setForeground(brush);
        
        commentStartExpression = new QRegExp("<!--");
        commentEndExpression = new QRegExp("-->");
    }

    public void highlightBlock(String text) {

        for (HighlightingRule rule : highlightingRules) {
            QRegExp expression = rule.pattern;
            int index = expression.indexIn(text);
            while (index >= 0) {
                int length = expression.matchedLength();
                setFormat(index, length, rule.format);
                index = expression.indexIn(text, index + length);
            }
        }
        setCurrentBlockState(0);

        int startIndex = 0;
        if (previousBlockState() != 1)
            startIndex = commentStartExpression.indexIn(text);

        while (startIndex >= 0) {
            int endIndex = commentEndExpression.indexIn(text, startIndex);
            int commentLength;
            if (endIndex == -1) {
                setCurrentBlockState(1);
                commentLength = text.length() - startIndex;
            } else {
                commentLength = endIndex - startIndex + commentEndExpression.matchedLength();
            }
            setFormat(startIndex, commentLength, commentFormat);
            startIndex = commentStartExpression.indexIn(text, startIndex + commentLength);
        }
    }
}
