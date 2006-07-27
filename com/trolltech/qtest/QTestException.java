/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $JAVA_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.qtest;

public class QTestException extends RuntimeException
{
    public String m_className;
    public String m_fileName;
    public String m_methodName;
    public int m_lineNumber;
    public String m_message;
    public String m_testDataName = "";
    
    public QTestException(String className, String fileName, String methodName, 
                          int lineNumber, String message)
    {
        m_className = className;
        m_fileName = fileName;
        m_methodName = methodName;
        m_lineNumber = lineNumber;
        m_message = message;        
    }        
    
    public String toString() { 
        return m_fileName + ":" + m_lineNumber + ": " + m_message;        
    }
}

class QTestPassException extends QTestException
{
    public QTestPassException(String methodName)
    {
        super("", "", methodName, 0, "");
    }
}