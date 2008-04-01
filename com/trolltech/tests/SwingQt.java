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

package com.trolltech.tests;

import com.trolltech.qt.gui.*;
import java.awt.event.*;
import javax.swing.*;


class SwingWindow extends JFrame implements ActionListener
{
    private static final long serialVersionUID = 1L;

    public SwingWindow()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);

        JButton button = new JButton("Hit me");
        button.setActionCommand("hit");
        button.addActionListener(this);
        add(button);

        pack();

    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("hit")) {
            QtWindow w = new QtWindow();
            w.show();
        }
    }
}

class QtWindow extends QPushButton
{
    public QtWindow()
    {
        super("Hit me");

        clicked.connect(this, "openSwingWindow()");
    }

    public void openSwingWindow()
    {
        SwingWindow w = new SwingWindow();
        w.setVisible(true);
    }
}

public class SwingQt extends QPushButton{



    /**
     * @param args
     */
    public static void main(String[] args) {
        new QApplication(args);
        QtWindow w = new QtWindow();
        w.show();

        QApplication.exec();
    }

}
