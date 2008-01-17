package generator;

import com.trolltech.qt.QtBlockedSlot;
import com.trolltech.qt.xmlpatterns.QAbstractXmlNodeModel;
import com.trolltech.qt.xmlpatterns.QXmlNodeModelIndex;

class QXmlNodeModelIndex___ extends QXmlNodeModelIndex {
    @QtBlockedSlot
    public final QAbstractXmlNodeModel model() {
        return QAbstractXmlNodeModel.fromNativePointer(model_private());
    }
}// class

