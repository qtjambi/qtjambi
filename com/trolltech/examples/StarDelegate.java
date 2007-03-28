package com.trolltech.examples;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

class StarDelegate extends QItemDelegate
{
    public StarDelegate(QWidget parent)
    {
        super(parent);
    }

    public void paint(QPainter painter, QStyleOptionViewItem option, QModelIndex index)
    {
        Object data = index.data();

        if (data != null && data instanceof StarRating) {
            if (option.state().isSet(QStyle.StateFlag.State_Selected)) {
                painter.fillRect(option.rect(), option.palette().highlight());
            }
            ((StarRating) data).paint(painter, option.rect(), option.palette(),
                                      StarRating.ReadOnly);
        } else
            super.paint(painter, option, index);
    }

    public QSize sizeHint(QStyleOptionViewItem option, QModelIndex index)
    {
        Object data = index.data();

        if (data instanceof StarRating)
            return ((StarRating) data).sizeHint();
        else
            return super.sizeHint(option, index);
    }

    public QWidget createEditor(QWidget parent, QStyleOptionViewItem item,
                                QModelIndex index)
    {
        Object data = index.data();

        if (data instanceof StarRating)
            return new StarEditor(parent, (StarRating) data);
        else
            return super.createEditor(parent, item, index);
    }

    public void setEditorData(QWidget editor, QModelIndex index)
    {
        Object data = index.data();

        if (data instanceof StarRating)
            ((StarEditor) editor).setStarRating((StarRating) data);
        else
            super.setEditorData(editor, index);
    }

    public void setModelData(QWidget editor, QAbstractItemModel model,
                 QModelIndex index)
    {
        if (index.data() instanceof StarRating)
            model.setData(index, ((StarEditor) editor).starRating());
        else
            super.setModelData(editor, model, index);
    }
}
