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

package com.trolltech.examples;

import com.trolltech.qt.QtJambiUtils;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

import java.util.*;


class ResourceItem extends QTreeWidgetItem
{
    private QFileInfo m_info = null;
    private static QFileIconProvider m_iconProvider = new QFileIconProvider();
    private boolean m_is_populated = false;
    private QTreeWidgetItem m_dummy_node = null;

    public ResourceItem(QTreeWidgetItem parent, QFileInfo info, boolean recurse)
    {
        super(parent);
        setInfo(info, recurse);
    }

    public ResourceItem(QTreeWidget parent, QFileInfo info, boolean recurse)
    {
        super(parent);
        setInfo(info, recurse);
    }

    public QFileInfo getInfo()
    {
        return m_info;
    }

    boolean shouldInsertFile(QFileInfo i)
    {
        return ((i.isDir() && !i.fileName().equals(".") && !i.fileName().equals(".."))
                || i.suffix().equals("png")
                || i.suffix().equals("jpg")
                || i.suffix().equals("jpeg"));
    }

    private void populate()
    {
        QDir dir = new QDir(m_info.absoluteFilePath());

        List<QFileInfo> entryList = dir.entryInfoList();

        for (QFileInfo i : entryList) {
            if (shouldInsertFile(i))
                new ResourceItem(this, i, false);
        }

        m_is_populated = true;
    }

    public void expand()
    {
        if (!m_is_populated) {
            if (m_dummy_node != null) {
                takeChildren();
                m_dummy_node.dispose();
                m_dummy_node = null;
            }

            populate();
        }
    }

    private void setInfo(QFileInfo info, boolean recurse)
    {
        m_info = info;

        setText(0, m_info.fileName());
        setIcon(0, m_iconProvider.icon(info));

        if (m_info.isDir() && recurse) {
            populate();
        } else if (m_info.isDir()) {
            m_dummy_node = new QTreeWidgetItem(this);
        }

    }
}

public class ResourceSystem extends QWidget
{
    private QLabel m_currentImage = null;
    private QTreeWidget m_selection = null;
    private boolean m_browse_class_path = true;
    private boolean m_shown = false;
    private String m_jar_name = null;

    public ResourceSystem()
    {
        setupUI();
    }

    private void setupUI()
    {
        m_currentImage = new QLabel();
        m_selection = new QTreeWidget();
        m_selection.setColumnCount(1);
        m_selection.setSizePolicy(QSizePolicy.Expanding, QSizePolicy.Minimum);
        m_selection.currentItemChanged.connect(this, "itemChanged(QTreeWidgetItem, QTreeWidgetItem)");
        m_selection.itemExpanded.connect(this, "expandItem(QTreeWidgetItem)");

        List<String> labels = new LinkedList<String>();
        labels.add("Name");
        m_selection.setHeaderLabels(labels);

        QRadioButton bt1 = new QRadioButton("Browse class path + ResourceSystem.jar");
        bt1.setChecked(true);
        QRadioButton bt2 = new QRadioButton("Browse just ResourceSystem.jar");

        QButtonGroup group = new QButtonGroup();
        group.addButton(bt1);
        group.addButton(bt2);
        group.setId(bt1, 0);
        group.setId(bt2, 1);
        group.buttonIdClicked.connect(this, "modeChanged(int)");

        QHBoxLayout layout2 = new QHBoxLayout();
        layout2.addWidget(bt1);
        layout2.addWidget(bt2);

        QVBoxLayout layout = new QVBoxLayout();
        layout.addWidget(m_selection);
        layout.addLayout(layout2);
        layout.addWidget(m_currentImage);

        setLayout(layout);

        setWindowIcon(new QIcon("classpath:com/trolltech/images/logo_32.png"));

        setWindowTitle("Resource System");
    }

    protected void expandItem(QTreeWidgetItem item)
    {
        if (item instanceof ResourceItem)
            ((ResourceItem) item).expand();
    }

    protected void modeChanged(int id)
    {
        if (id == 0)
            m_browse_class_path = true;
        else
            m_browse_class_path = false;

        setupSelection();
    }

    protected void itemChanged(QTreeWidgetItem cur, QTreeWidgetItem prev)
    {
        if (cur == null || !(cur instanceof ResourceItem))
            return ;

        ResourceItem selected_item = (ResourceItem) cur;
        QFileInfo info = selected_item.getInfo();

        if (info.exists() && !info.isDir()) {
            QPixmap pm = new QPixmap(info.absoluteFilePath());
            m_currentImage.setPixmap(pm);
        }

    }

    private void setupSelection()
    {
        m_selection.clear();

        // Find the jar file (bundled with the class) and add it to the class path if requested by the user
        String jar_file_name = "classpath:com/trolltech/examples/ResourceSystem.jar";
        QFileInfo jarInfo = new QFileInfo(jar_file_name);
        if (!jarInfo.exists()) {
            QMessageBox.warning(this, "File not found", "Can't find the resource jar file");
            return ;
        }

        String searchPath = null;
        if (m_browse_class_path) {   
            if (m_jar_name == null) {
                QtJambiUtils.addSearchPathForResourceEngine(jarInfo.canonicalFilePath());
                m_jar_name = jarInfo.canonicalFilePath();
            }
            searchPath = "classpath:/";
        } else { // Otherwise just browse the root of the jar file
            searchPath = "classpath:" + jarInfo.canonicalFilePath() + "#/";
        }

        QFileInfo info = new QFileInfo(searchPath);
        if (!info.exists() || !info.isDir()) {
            QMessageBox.warning(this, "Couldn't open root dir", "Problem reading from class path");
            return ;
        }

        new ResourceItem(m_selection, info, true);
    }

    protected void finalize()
    {
        if (m_jar_name != null)
            QtJambiUtils.removeSearchPathForResourceEngine(m_jar_name);
        super.finalize();
    }

    protected void showEvent(QShowEvent e) {
        if (!m_shown) {
            QTimer.singleShot(0, this, "setupSelection()");
            m_shown = true;
        }
    }

    public static void main(String[] args)
    {
        QApplication.initialize(args);
        ResourceSystem w = new ResourceSystem();
        w.show();

        QApplication.exec();
    }

}
