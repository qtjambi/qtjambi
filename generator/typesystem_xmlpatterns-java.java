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

class QXmlName___ extends QXmlName {

      /**
       * Constructs a <code>QXmlName</code> instance that inserts <code>localName</code>,
       * <code>namespaceURI</code> and <code>prefix</code> into <code>namePool</code> if they aren't
       * already there. The accessor functions <code>namespaceUri()</code>, <code>prefix()</code>,
       * <code>localName()</code>, and <code>toClarkName()</code> must be passed the <code>namePool</code>
       * used here, so the <code>namePool</code> must remain in scope while the
       * accessor functions might be used. However, two instances can
       * be compared with <code>==</code> or <code>!=</code> and copied without the
       * <code>namePool</code>.
       *
       * The user guarantees that the string components are valid for a
       * <code>QName</code>. In particular, the local name, and the prefix (if present),
       * must be valid {@link <a href="http://www.w3.org/TR/REC-xml-names/#NT-NCName">NCNames</a>}
       * The function <code>isNCName()</code> can be used to test validity
       * of these names. The namespace URI should be an absolute URI.
       * <code>QUrl.isRelative()</code> can be used to test whether the namespace URI
       * is relative or absolute. Finally, providing a prefix is not valid
       * when no namespace URI is provided.
       *
       * <code>namePool</code> is not copied. Nor is the reference to it retained
       * in this instance. This constructor inserts the three strings
       * into <code>namePool</code>.
       */
      public QXmlName(QXmlNamePool namePool, String localName, String namespaceURI, String prefix) {
          this(namePool.nativePointer(), localName, namespaceURI, prefix);
      }

      /**
       * Equivalent to calling QXmlName(namePool, localName, namespaceURI, null);
       */
      public QXmlName(QXmlNamePool namePool, String localName, String namespaceURI) {
          this(namePool, localName, namespaceURI, null);
      }

      /**
       * Equivalent to calling QXmlName(namePool, localName, null, null)
       */
      public QXmlName(QXmlNamePool namePool, String localName) {
          this(namePool, localName, null);
      }


}// class

class QSimpleXmlNodeModel___ extends QSimpleXmlNodeModel {

    /**
     * Returns the name pool that is associated with this model. The implementation of <code>name()</code>
     * would use this to create names.
     */
    public final QXmlNamePool namePool() {
        return QXmlNamePool.fromNativePointer(namePool_private());
    }
}// class
