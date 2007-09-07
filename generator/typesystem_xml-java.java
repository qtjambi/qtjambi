package generator;

import com.trolltech.qt.*;
import com.trolltech.qt.xml.*;

class QDomDocument___ extends QDomDocument {

    public class Result {
        private Result(boolean success, QNativePointer errorMessage, QNativePointer errorLine, QNativePointer errorColumn) {
            this.success = success;
            this.errorMessage = errorMessage.stringValue();
            this.errorLine = errorLine.intValue();
            this.errorColumn = errorColumn.intValue();
        }

        public boolean success;
        public String errorMessage;
        public int errorLine;
        public int errorColumn;

    }

}// class

class QXmlNamespaceSupport___ extends QXmlNamespaceSupport {

    public static class ProcessedName {
        public ProcessedName(String nsuri, String localName) {
            this.nsuri = nsuri;
            this.localName = localName;
        }

        public String nsuri;
        public String localName;
    }

    public final ProcessedName processName(String qname, boolean isAttribute) {
        QNativePointer nsUri = new QNativePointer(QNativePointer.Type.String);
        QNativePointer localName = new QNativePointer(QNativePointer.Type.String);
        processName(qname, isAttribute, nsUri, localName);

        return new ProcessedName(nsUri.stringValue(), localName.stringValue());
    }

    public static class SplitName {
        public SplitName(String prefix, String localname) {
            this.prefix = prefix;
            this.localname = localname;
        }

        public String prefix;
        public String localname;
    }

    public final SplitName splitName(String qname) {
        QNativePointer prefix = new QNativePointer(QNativePointer.Type.String);
        QNativePointer localName = new QNativePointer(QNativePointer.Type.String);
        splitName(qname, prefix, localName);

        return new SplitName(prefix.stringValue(), localName.stringValue());
    }

}// class

class QXmlStreamWriter___ extends QXmlStreamWriter {

    public QXmlStreamWriter(com.trolltech.qt.core.QByteArray array) {
        this(array.nativePointer());
        __rcDevice = array;
    }

    public final void setCodec(String codecName) {
        setCodec(QNativePointer.createCharPointer(codecName));
        __rcCodec = null;
    }

}// class
