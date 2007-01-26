package com.trolltech.autotests;

import java.util.List;

import org.junit.Test;

import com.trolltech.autotests.generated.AccessibleInterfaceSubclass;
import com.trolltech.autotests.generated.GraphicsItemSubclass;
import com.trolltech.autotests.generated.IODeviceSubclass;
import com.trolltech.autotests.generated.ImageIOHandlerSubclass;
import com.trolltech.autotests.generated.PictureSubclass;
import com.trolltech.autotests.generated.SomeQObject;
import com.trolltech.autotests.generated.SqlTableModelSubclass;
import com.trolltech.autotests.generated.TextCodecSubclass;
import com.trolltech.autotests.generated.ValidatorSubclass;
import com.trolltech.autotests.generated.XmlReaderSubclass;
import com.trolltech.qt.QNativePointer;
import com.trolltech.qt.QVariant;
import com.trolltech.qt.core.QBuffer;
import com.trolltech.qt.core.QByteArray;
import com.trolltech.qt.core.QDataStream;
import com.trolltech.qt.core.QDate;
import com.trolltech.qt.core.QDateTime;
import com.trolltech.qt.core.QDir;
import com.trolltech.qt.core.QFile;
import com.trolltech.qt.core.QIODevice;
import com.trolltech.qt.core.QLocale;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.QSize;
import com.trolltech.qt.core.QTemporaryFile;
import com.trolltech.qt.core.QTextCodec;
import com.trolltech.qt.core.QTextCodec_ConverterState;
import com.trolltech.qt.core.QTime;
import com.trolltech.qt.core.QUrl;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QAccessible;
import com.trolltech.qt.gui.QAccessibleInterface;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QBitmap;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QClipboard;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QFontMetrics;
import com.trolltech.qt.gui.QGradient;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QImage;
import com.trolltech.qt.gui.QKeySequence;
import com.trolltech.qt.gui.QLineF;
import com.trolltech.qt.gui.QLinearGradient;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMimeData;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPicture;
import com.trolltech.qt.gui.QPictureIO;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QPixmapCache;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QRadialGradient;
import com.trolltech.qt.gui.QRegion;
import com.trolltech.qt.gui.QShortcut;
import com.trolltech.qt.gui.QStyleOptionButton;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QTableArea;
import com.trolltech.qt.gui.QTextBlock;
import com.trolltech.qt.gui.QTextCursor;
import com.trolltech.qt.gui.QTextDocument;
import com.trolltech.qt.gui.QValidator;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.network.QHttp;
import com.trolltech.qt.network.QTcpServer;
import com.trolltech.qt.opengl.QGLColormap;
import com.trolltech.qt.sql.QSqlDatabase;
import com.trolltech.qt.sql.QSqlField;
import com.trolltech.qt.sql.QSqlRecord;
import com.trolltech.qt.xml.QDomDocument;
import com.trolltech.qt.xml.QDomElement;
import com.trolltech.qt.xml.QXmlContentHandlerInterface;
import com.trolltech.qt.xml.QXmlDTDHandlerInterface;
import com.trolltech.qt.xml.QXmlDeclHandlerInterface;
import com.trolltech.qt.xml.QXmlEntityResolverInterface;
import com.trolltech.qt.xml.QXmlErrorHandlerInterface;
import com.trolltech.qt.xml.QXmlInputSource;
import com.trolltech.qt.xml.QXmlLexicalHandlerInterface;

import static org.junit.Assert.*;

public class TestInjectedCode extends QApplicationTest {
    
    static class IODeviceSubclassSubclass extends IODeviceSubclass {
        public byte buffer[];
        public int inputBufferSize;
        
        public IODeviceSubclassSubclass(int bufferLength) {
            super(bufferLength);                       
        }

        @Override
        protected int readData(byte[] data) {
            inputBufferSize = data.length;
            
            int size = super.readData(data);            
            buffer = new byte[size];
            for (int i=0; i<size; ++i)
                buffer[i] = data[i];
            
            return size;
        }

        @Override
        protected int writeData(byte[] data) {
            inputBufferSize = data.length;
            buffer = new byte[data.length];
            
            for (int i=0; i<data.length; ++i)
                buffer[i] = data[i];
            
            return super.writeData(data);
        }
        
        @Override
        protected int readLineData(byte[] data) {
            inputBufferSize = data.length;
            
            int size = super.readLineData(data);
            buffer = new byte[size];
            for (int i=0; i<size; ++i)
                buffer[i] = data[i];
            
            return size;
        }
    }
    
    static class TextCodecSubclassSubclass extends TextCodecSubclass {
        char receivedChar[];
        byte receivedByte[];
        QTextCodec_ConverterState receivedState;

        @Override
        protected QByteArray convertFromUnicode(char[] data, int size, QTextCodec_ConverterState state) {
            receivedChar = data;
            receivedState = state;
            return super.convertFromUnicode(data, size, state);
        }

        @Override
        protected String convertToUnicode(byte[] data, int size, QTextCodec_ConverterState state) {
            receivedByte = data;
            receivedState = state;
            return super.convertToUnicode(data, size, state);
        }

        @Override
        public int mibEnum() {
            return 0;
        }

        @Override
        public QByteArray name() {
            return null;
        }        
    }
    
    static class OtherIODeviceSubclass extends QIODevice {
        public byte[] bytes = {(byte) 'h', (byte) 'a', (byte) 'l' };
        public int i = 0;
        public QByteArray ba = new QByteArray();

        @Override
        protected int readData(byte[] data) {
            if (i == bytes.length) return -1;
            data[0] = bytes[i++];
            return 1;
        }

        @Override
        protected int writeData(byte[] data) {
            if (data.length == 0) return -1;
            ba.append(data[0]);
            return 1;
        }                        
    }
    
    static class PictureSubclassSubclass extends PictureSubclass {
        public byte[] data;

        @Override
        public void setData(byte[] data) {
            this.data = data;
            super.setData(data);
        }
        
    }
    
    static class GraphicsItemSubclassSubclass extends GraphicsItemSubclass {

        public QPainter painter;
        public QStyleOptionGraphicsItem option;
        public QWidget widget;
        
        @Override
        public QRectF boundingRect() {
            return null;
        }

        @Override
        public void paint(QPainter painter, QStyleOptionGraphicsItem option, QWidget widget) {
            this.painter = painter;
            this.option = option;
            this.widget = widget;
            painter.fillRect(new QRect(51, 0, 50, 50), new QBrush(QColor.green));
            super.paint(painter, option, widget);
        }        
    }
    
    static class AccessibleInterfaceSubclassSubclass extends AccessibleInterfaceSubclass {
        public RelationFlag relation;
        public Target target;
        @Override
        public Target navigate(RelationFlag relation, int entry) {
            this.relation  = relation;
            target = super.navigate(relation, entry);
            return target;
        }

        @Override
        public String actionText(int action, Text t, int child) {
            return null;
        }

        @Override
        public int childAt(int x, int y) {
            return 0;
        }

        @Override
        public int childCount() {
            return 0;
        }

        @Override
        public boolean doAction(int action, int child, List<Object> params) {
            return false;
        }

        @Override
        public int indexOfChild(QAccessibleInterface arg__1) {
            return 0;
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public QObject object() {
            return null;
        }

        @Override
        public QRect rect(int child) {
            return null;
        }

        @Override
        public Relation relationTo(int child, QAccessibleInterface other, int otherChild) {
            return null;
        }

        @Override
        public Role role(int child) {
            return null;
        }

        @Override
        public void setText(Text t, int child, String text) {
            
        }

        @Override
        public State state(int child) {
            return null;
        }

        @Override
        public String text(Text t, int child) {
            return null;
        }

        @Override
        public int userActionCount(int child) {
            return 0;
        }
        
    }
    
    static class AnyClass extends QWidget {
        public QUrl url;
        
        @SuppressWarnings("unused")
        private void myUrlHandler(QUrl url) {
            this.url = url;            
        }
        
        public void actionTriggered() {
            myActionTriggered.emit();
        }
        
        public void somethingElse() {
        	// nothing here
        	System.out.println("here");
        }
        
        public Signal0 myActionTriggered = new Signal0();
    }
    
    static class ValidatorSubclassSubclass extends ValidatorSubclass {

    	public String inputString;
    	public int inputPos;
    	
		@Override
		public String fixup(String input) {
			inputString = input;
			return super.fixup(input) + "javaPostfix";
		}

		@Override
		public QValidator.State validate(QValidator.QValidationData input) {
			inputString = input.string;
			inputPos = input.position;
			QValidator.State state = super.validate(input);
			input.string = "javaPrefix" + input.string;
			
			return state;
		}
    	
    }
    
    static class ImageIOHandlerSubclassSubclass extends ImageIOHandlerSubclass {
        public QImage image;

        @Override
        public boolean read(QImage image) {
            this.image = new QImage();
            
            super.read(null); // don't crash
            boolean result = super.read(this.image);            
            return result && (image != null ? image.load("classpath:com/trolltech/examples/images/bg1.png") : true);
        }

        @Override
        public boolean canRead() {
            return false;
        }
        
    }
    
    static class XmlReaderSubclassSubclass extends XmlReaderSubclass {
        
        @Override
        public boolean feature(String name) {
            
            return super.feature(name) || name.equals("javaTrue");
        }

        @Override
        public QXmlContentHandlerInterface contentHandler() {
            return null;
        }

        @Override
        public QXmlDeclHandlerInterface declHandler() {
            return null;
        }

        @Override
        public QXmlDTDHandlerInterface DTDHandler() {
            return null;
        }

        @Override
        public QXmlEntityResolverInterface entityResolver() {
            return null;
        }

        @Override
        public QXmlErrorHandlerInterface errorHandler() {
            return null;
        }

        @Override
        public boolean hasFeature(String name) {
            return name.equals("javaTrue");
        }

        @Override
        public boolean hasProperty(String name) {
            return false;
        }

        @Override
        public QXmlLexicalHandlerInterface lexicalHandler() {
            return null;
        }

        @Override
        public boolean parse(QXmlInputSource input) {
            return false;
        }

        @Override
        public void setContentHandler(QXmlContentHandlerInterface handler) {
            
        }

        @Override
        public void setDeclHandler(QXmlDeclHandlerInterface handler) {
            
        }

        @Override
        public void setDTDHandler(QXmlDTDHandlerInterface handler) {
            
        }

        @Override
        public void setEntityResolver(QXmlEntityResolverInterface handler) {
            
        }

        @Override
        public void setErrorHandler(QXmlErrorHandlerInterface handler) {
            
        }

        @Override
        public void setFeature(String name, boolean value) {
            
        }

        @Override
        public void setLexicalHandler(QXmlLexicalHandlerInterface handler) {
            
        }

    }
    
    private static QTemporaryFile setUpPicture() {
        QTemporaryFile file = new QTemporaryFile();
        
        assertTrue(file.open(QIODevice.OpenModeFlag.WriteOnly));        
        
        {
            QPicture picture = new QPicture();
            QPainter painter = new QPainter();
            painter.begin(picture);
            painter.fillRect(new QRect(0, 0, 10, 10), new QBrush(QColor.green));
            painter.fillRect(new QRect(11, 0, 10, 10), new QBrush(QColor.red));
            painter.end();
            
            assertTrue(picture.save(file));
        }        
        file.close();

        return file;
    }
    
    private static void verifyQPicture(QPicture picture) {
        QImage img = new QImage(100, 100, QImage.Format.Format_ARGB32_Premultiplied);
        QPainter painter = new QPainter();
        painter.begin(img);
        painter.drawPicture(new QPoint(0, 0), picture);
        painter.end();
        
        assertEquals(QColor.green.rgba(), img.pixel(4, 4));
        assertEquals(QColor.red.rgba(), img.pixel(12, 4));
    }    
    
    @Test
    public void testQXmlReaderFeature() {
        XmlReaderSubclassSubclass xrss = new XmlReaderSubclassSubclass();
        
        assertFalse(xrss.callFeature("javaFalse"));
        assertEquals("javaFalse", xrss.myName());
        assertFalse(xrss.myOk());
        
        assertTrue(xrss.callFeature("javaTrue"));
        assertEquals("javaTrue", xrss.myName());
        assertTrue(xrss.myOk());
        
        assertTrue(xrss.callFeature("true"));
        assertEquals("true", xrss.myName());
        assertFalse(xrss.myOk());
    }
    
    @Test
    public void testQDomElementAttributeNS() {
        QDomDocument document = new QDomDocument();
        QDomElement element = document.createElement("tag");
        element.setAttributeNS("something", "foo", "bar");
        assertEquals("bar", element.attributeNS("something", "foo"));
    }
            
    @SuppressWarnings("unused")
    private void receiveBeforeInsert(QSqlRecord record) {
        record.append(new QSqlField("javaInt", QVariant.Int));
        record.setValue("javaInt", 3456);               
    }
    
    @Test
    public void testQTcpServerWaitForConnection() {
        QTcpServer server = new QTcpServer();
        
        QTcpServer.Result result = server.waitForNewConnection(1);
        assertEquals(QTcpServer.Result.TimedOut, result);
    }
    
    @Test
    public void testQHttpRead() {
        QHttp http = new QHttp("www.trolltech.com");        
        http.get("/index.html");
        
        byte bytes[] = new byte[32];
        while (http.bytesAvailable() < 32) QApplication.processEvents();
        
        http.read(bytes);
        
        assertEquals("<!DOCTYPE", new QByteArray(bytes).left(9).toString()); 
    }
    
    @Test
    public void testQGLColorMapSetEntries() {
        int firstColors[] = { QColor.red.rgba(), QColor.green.rgba() };
        int secondColors[] = { QColor.blue.rgba(), QColor.yellow.rgba() };
                
        QGLColormap map = new QGLColormap();
        map.setEntries(firstColors);        
        assertEquals(QColor.red.rgba(), map.entryRgb(0));
        assertEquals(QColor.green.rgba(), map.entryRgb(1));
        
        map.setEntries(secondColors, 1);
        assertEquals(QColor.red.rgba(), map.entryRgb(0));
        assertEquals(QColor.yellow.rgba(), map.entryRgb(1));        
    }
    
    @Test
    public void testSqlTableModelBeforeInsertJava() {
        SqlTableModelSubclass stms = new SqlTableModelSubclass();
        
        stms.beforeInsert.connect(this, "receiveBeforeInsert(QSqlRecord)");
        stms.emitBeforeInsert();
        
        QSqlRecord record = stms.myRecord();
        assertEquals(3456, record.value("javaInt"));
        assertEquals(3456, QVariant.toInt(record.value("javaInt")));        
    }

    @Test
    public void testSqlTableModelBeforeInsertCpp() {
        SqlTableModelSubclass stms = new SqlTableModelSubclass();
        
        stms.connectBeforeInsert();
        
        QSqlRecord record = new QSqlRecord();
        stms.beforeInsert.emit(record);
        
        assertEquals(1234, QVariant.toInt(record.value("cppInt")));
    }
    
    @Test
    public void testSqlDatabaseDefaultConnection() {
        String defaultConnection = QSqlDatabase.defaultConnection();
        assertEquals("qt_sql_default_connection", defaultConnection);
    }
    
    @Test
    public void testQTextDocumentUndoRedo() {
        QTextDocument textDocument = new QTextDocument();
        textDocument.setPlainText("i have plain text");
        
        QTextCursor cursor = new QTextCursor(textDocument);
        cursor.movePosition(QTextCursor.MoveOperation.Start);
        
        assertTrue(cursor.atStart());
        cursor.insertText("A");        
        assertEquals("Ai have plain text", textDocument.toPlainText());
        assertFalse(cursor.atStart());
                
        textDocument.undo(cursor);
        assertEquals("i have plain text", textDocument.toPlainText());
        assertTrue(cursor.atStart());
        
        textDocument.redo(cursor);
        assertEquals("Ai have plain text", textDocument.toPlainText());
        assertFalse(cursor.atStart());
    }
    
    @Test
    public void testQClipboardTextSpecificSubtype() {
        QMimeData data = new QMimeData();
        data.setHtml("some text"); // text/html
        
        QClipboard clipboard = QApplication.clipboard();
        clipboard.clear();
        
        QClipboard.Text text = clipboard.text("html");
        assertEquals("html", text.subtype);
        assertEquals("", text.text);

        text = clipboard.text("plain");
        assertEquals("plain", text.subtype);
        assertEquals("", text.text);
        
        clipboard.setMimeData(data);
        
        text = clipboard.text("plain");
        assertEquals("plain", text.subtype);
        assertEquals("", text.text);

        text = clipboard.text("html");
        assertEquals("html", text.subtype);
        assertEquals("some text", text.text);
        
        data = new QMimeData();
        data.setText("some plain text");
        clipboard.setMimeData(data);
        
        text = clipboard.text("html");
        assertEquals("html", text.subtype);
        assertEquals("", text.text);
        
        text = clipboard.text("plain");
        assertEquals("plain", text.subtype);        
        assertEquals("some plain text", text.text);
    }
    
    @Test
    public void testQClipboardTextAnySubtype() {
        QMimeData data = new QMimeData();
        data.setHtml("some text"); // text/html
        
        QClipboard clipboard = QApplication.clipboard();
        clipboard.clear();
        
        QClipboard.Text text = clipboard.text("");
        assertEquals("", text.subtype);
        assertEquals("", text.text);

        text = clipboard.text((String) null);
        assertEquals("", text.subtype);
        assertEquals("", text.text);
        
        clipboard.setMimeData(data);
        
        text = clipboard.text("");
        assertEquals("html", text.subtype);
        assertEquals("some text", text.text);
        
        data = new QMimeData();
        data.setText("some plain text");
        clipboard.setMimeData(data);
        
        text = clipboard.text((String) null);
        assertEquals("plain", text.subtype);
        assertEquals("some plain text", text.text);        
    }
    
    @Test
    public void testQImageIOHandlerRead() {
        QImage image = new QImage();
        ImageIOHandlerSubclassSubclass iihss = new ImageIOHandlerSubclassSubclass();
        
        assertTrue(iihss.callRead(image.nativePointer()));
        
        QImage ref1 = new QImage("classpath:com/trolltech/examples/images/cheese.png");
        QImage ref2 = new QImage("classpath:com/trolltech/examples/images/bg1.png");
        
        assertEquals(ref2.width(), image.width());
        assertEquals(ref2.height(), image.height());
        assertTrue(ref2.operator_equal(image));
        
        assertEquals(ref1.width(), iihss.image.width());
        assertEquals(ref1.height(), iihss.image.height());
        assertTrue(ref1.operator_equal(iihss.image));
        
    }
    
    @Test
    public void testQWidgetGetContentsMargins() {
        QWidget w = new QWidget();
        
        w.setContentsMargins(10, 11, 12, 13);
        
        QWidget.ContentsMargins cm = w.getContentsMargins();
        assertEquals(cm.left, 10);
        assertEquals(cm.top, 11);
        assertEquals(cm.right, 12);
        assertEquals(cm.bottom, 13);
    }
    
    @Test
    public void testQGridLayoutGetItemPosition() {
        QGridLayout layout = new QGridLayout();
        
        QWidget w1 = new QWidget();
        QWidget w2 = new QWidget();
        
        layout.addWidget(w1, 2, 2);
        layout.addWidget(w2, 3, 4, 5, 6);
        
        {
            QTableArea ip = layout.getItemPosition(0);
            assertEquals(2, ip.row);            
            assertEquals(2, ip.column);
            assertEquals(1, ip.rowCount);            
            assertEquals(1, ip.columnCount);
        }
        
        {
            QTableArea ip = layout.getItemPosition(1);
            assertEquals(3, ip.row);
            assertEquals(4, ip.column);
            assertEquals(5, ip.rowCount);
            assertEquals(6, ip.columnCount);
        }
    }
    
    @Test
    public void testValidatorFixup() {
    	ValidatorSubclassSubclass vss = new ValidatorSubclassSubclass();
    	
    	QNativePointer input = new QNativePointer(QNativePointer.Type.String);
    	input.setStringValue("acceptable");
    	QNativePointer pos = new QNativePointer(QNativePointer.Type.Int);
    	pos.setIntValue(13);
    	assertEquals(QValidator.State.Acceptable, vss.callValidate(input, pos));
    	assertEquals("javaPrefixacceptablesomePostfix", input.stringValue());
    	assertEquals("acceptable".length(), pos.intValue());
    	assertEquals("acceptable", vss.inputString);
    	assertEquals(13, vss.inputPos);
    	assertEquals("acceptable", vss.inputString());
    	assertEquals(13, vss.inputPos());
    	
    	input.setStringValue("intermediate");
    	pos.setIntValue(14);
    	assertEquals(QValidator.State.Intermediate, vss.callValidate(input, pos));
    	assertEquals("javaPrefixintermediatesomePostfix", input.stringValue());
    	assertEquals("intermediate".length(), pos.intValue());
    	assertEquals("intermediate", vss.inputString);
    	assertEquals(14, vss.inputPos);
    	assertEquals("intermediate", vss.inputString());
    	assertEquals(14, vss.inputPos());
    	
    }
    
    @Test
    public void testQShortcutConstructor() {
    	QShortcut shortcut = new QShortcut(new QKeySequence("Ctrl+F1"), new QWidget());
    	assertEquals("Ctrl+F1", shortcut.key().toString());
    	assertEquals(Qt.ShortcutContext.WindowShortcut, shortcut.context());
    	
    	shortcut = new QShortcut(new QKeySequence("Ctrl+F2"), new QWidget(), Qt.ShortcutContext.ApplicationShortcut);
    	assertEquals("Ctrl+F2", shortcut.key().toString());
    	assertEquals(Qt.ShortcutContext.ApplicationShortcut, shortcut.context());
    	
    }
    
    @Test
    public void testQPixmapCacheFind() {    	
    	{
    		QPixmap pm = new QPixmap("classpath:com/trolltech/examples/images/cheese.png");
    		QPixmapCache.insert("myPixmap", pm);
    	}
    	
    	{
    		QPixmap pm = new QPixmap();
    		assertTrue(QPixmapCache.find("myPixmap", pm));
    		assertEquals(94, pm.width());
    		
    		assertFalse(QPixmapCache.find("noSuchPixmap", pm));
    	}
    }
    
    @Test
    public void testQPictureParameters() {
    	QPictureIO pictureIO = new QPictureIO("someFile", "PNG");
    	
    	pictureIO.setParameters("my parameters");
    	assertEquals("my parameters", pictureIO.parameters());
    }
    
    @Test
    public void testQPictureConstructor() {
    	QPictureIO pictureIO = new QPictureIO(new QTemporaryFile(), "JPEG");
    	assertEquals("JPEG", pictureIO.format());
    	assertEquals("", pictureIO.fileName());
    	
    	pictureIO = new QPictureIO("someFile", "PNG");
    	assertEquals("PNG", pictureIO.format());
    	assertEquals("someFile", pictureIO.fileName());
    }
    
    static boolean called = false;
    void myReceiver() { 
        called = true;
    }
    
    @Test
    public void testQMenuAddActionJavaSignal() {
        AnyClass ac = new AnyClass();
        ac.myActionTriggered.connect(this, "myReceiver()");
        
        QMenu menu = new QMenu();
        QAction action = menu.addAction(null, "blah", ac.myActionTriggered);
                
        called = false;
        action.activate(QAction.ActionEvent.Trigger);
        assertTrue(called);        
    }
    
    @Test
    public void testQMenuAddActionJavaSlot() {
        AnyClass ac = new AnyClass();
        ac.myActionTriggered.connect(this, "myReceiver()");
        
        QMenu menu = new QMenu();
        QAction action = menu.addAction(null, "blah", ac, "actionTriggered()");
                
        called = false;
        action.activate(QAction.ActionEvent.Trigger);
        assertTrue(called);
    }
    
    @Test 
    public void testQMenuAddActionCpp() {
        SomeQObject sqo = new SomeQObject();        
        QMenu menu = new QMenu();
        QAction action = menu.addAction(null, "blah", sqo, "actionTriggered()");
        sqo.myActionTriggered.connect(this, "myReceiver()");
        
        called = false;
        action.activate(QAction.ActionEvent.Trigger);
        assertTrue(called);
    }
            
    static class PushButtonAccessor extends QPushButton {
        public void callInitStyleOption(QStyleOptionButton option) {
            initStyleOption(option);
        }
    }
    
    @Test
    public void testInitStyleOption() {               
        QStyleOptionButton option = new QStyleOptionButton();
        PushButtonAccessor accessor = new PushButtonAccessor();
        
        accessor.setFlat(true);        
        accessor.callInitStyleOption(option);        
        assertTrue(option.features().isSet(QStyleOptionButton.ButtonFeature.Flat));
        
        accessor.setFlat(false);
        accessor.callInitStyleOption(option);
        assertFalse(option.features().isSet(QStyleOptionButton.ButtonFeature.Flat));
    }
        
    @Test
    public void testQAccessibleInterfaceNavigate() {
        AccessibleInterfaceSubclassSubclass aiss = new AccessibleInterfaceSubclassSubclass();
        
        int i = aiss.callNavigate(QAccessible.RelationFlag.Self, 33);
        assertEquals(QAccessible.RelationFlag.Self, aiss.relation);
        assertEquals(33, i);
        assertEquals(33, aiss.target.childIndex);
        assertTrue(aiss == aiss.target());
        assertTrue(aiss == aiss.target.target);
        
        i = aiss.callNavigate(QAccessible.RelationFlag.Ancestor, 103);
        assertEquals(-1, i);
        assertEquals(-1, aiss.target.childIndex);
        assertTrue(null == aiss.target());
        assertTrue(null == aiss.target.target);
        
    }
    
    @Test
    public void testQGraphicsItemPaint() {
        GraphicsItemSubclassSubclass giss = new GraphicsItemSubclassSubclass();
        
        QImage image = new QImage(100, 100, QImage.Format.Format_ARGB32_Premultiplied);
        QPainter painter = new QPainter();
        painter.begin(image);
        
        QStyleOptionGraphicsItem item = new QStyleOptionGraphicsItem();
        item.setExposedRect(new QRectF(0, 1, 2, 3));        
        QWidget w = new QPushButton();
        
        giss.callPaint(painter, item, w);        
        painter.end();
        
        assertEquals(QColor.red.rgba(), image.pixel(2, 2));
        assertEquals(QColor.green.rgba(), image.pixel(52, 2));
        assertTrue(w == giss.widget);
        assertTrue(w == giss.widget());
        assertTrue(giss.widget instanceof QPushButton);
        assertTrue(giss.widget() instanceof QPushButton);
        assertTrue(painter == giss.painter);
        assertTrue(painter == giss.painter());
        assertEquals(0.0, giss.option.exposedRect().x());        
        assertEquals(1.0, giss.option.exposedRect().y());
        assertEquals(2.0, giss.option.exposedRect().width());
        assertEquals(3.0, giss.option.exposedRect().height());
        assertEquals(0.0, giss.option().exposedRect().x());        
        assertEquals(1.0, giss.option().exposedRect().y());
        assertEquals(2.0, giss.option().exposedRect().width());
        assertEquals(3.0, giss.option().exposedRect().height());       
    }
    
    @Test
    public void testQFontMetricsBoundingRect() {
        QFontMetrics metrics = new QFontMetrics(QApplication.font());
        
        QRect wideRect = new QRect(0, 0, 1000, 1000);
        QRect brect = metrics.boundingRect(wideRect.x(), wideRect.y(), wideRect.width(), wideRect.height(), Qt.AlignmentFlag.AlignLeft.value(), "Some text");
        assertTrue(brect.isValid());
        assertTrue(wideRect.contains(brect));
        
        int[] tabArray = new int[5];
        for (int i=0; i<tabArray.length; ++i)
            tabArray[i] = i + 1;        
        brect = metrics.boundingRect(wideRect.x(), wideRect.y(), wideRect.width(), wideRect.height(), 
                Qt.AlignmentFlag.AlignLeft.value() + Qt.TextFlag.TextExpandTabs.value(), "Some text", 0,
                tabArray);
        assertTrue(brect.isValid());
        assertTrue(wideRect.contains(brect));
    }
    
    @Test
    public void testQBrushGradient() {
        QBrush brush = new QBrush(QColor.red);        
        assertEquals(null, brush.gradient());
        
        QGradient gradient = new QLinearGradient(0, 0, 11, 12);
        brush = new QBrush(gradient);
        
        assertTrue(brush.gradient() != null);
        assertEquals(QGradient.Type.LinearGradient, brush.gradient().type());
        assertTrue(brush.gradient() instanceof QLinearGradient);
        QLinearGradient lg = (QLinearGradient) brush.gradient();
        assertEquals(0.0, lg.start().x());
        assertEquals(0.0, lg.start().y());
        assertEquals(11.0, lg.finalStop().x());
        assertEquals(12.0, lg.finalStop().y());
        
        gradient = new QRadialGradient(0, 0, 1);
        brush = new QBrush(gradient);
        
        assertTrue(brush.gradient() != null);
        assertEquals(QGradient.Type.RadialGradient, brush.gradient().type());
        assertTrue(brush.gradient() instanceof QRadialGradient);
        
    }
    
    @Test
    public void testQImageLoadFromData() {
        QImage img = new QImage();
        QFile file = new QFile("classpath:com/trolltech/examples/images/cheese.png");
        
        assertTrue(file.open(QIODevice.OpenModeFlag.ReadOnly));
                
        img.load(file, "JPEG");
        assertTrue(img.isNull());
        
        img = new QImage();
        file.reset();
        img.load(file);
        assertFalse(img.isNull());
        assertEquals(94, img.width());   
        
        img = new QImage();
        file.reset();
        img.loadFromData(file.readAll().toByteArray(), "JPEG");
        assertTrue(img.isNull());
        
        img = new QImage();
        file.reset();
        img.loadFromData(file.readAll().toByteArray());
        assertFalse(img.isNull());
        assertEquals(94, img.width());
    }
    
    @Test
    public void testQCursorBitmap() {
        QCursor cursor = new QCursor(Qt.CursorShape.CrossCursor);
        assertEquals(null, cursor.bitmap());
        
        QBitmap bm = new QBitmap("classpath:com/trolltech/examples/images/cheese.png");
        cursor = new QCursor(bm,
                             new QBitmap("classpath:com/trolltech/examples/images/cheese.png"));
        
        assertEquals(bm.width(), cursor.bitmap().width());
        assertEquals(bm.height(), cursor.bitmap().height());
        
        QImage bmImage = bm.toImage();
        QImage otherImage = cursor.bitmap().toImage();
        
        assertTrue(bmImage.operator_equal(otherImage));
    }
    
    @Test
    public void testQImageConstructedFromStringAndStuff() {
        QImage img = new QImage("classpath:com/trolltech/examples/images/cheese.png", "JPEG");
        assertTrue(img.isNull());
        
        img = new QImage("classpath:com/trolltech/examples/images/cheese.png", "PNG");
        assertFalse(img.isNull());
        assertEquals(94, img.width());        
    }
    
    @Test
    public void testQImageConstructedFromByteArray() {
        QImage img = new QImage("classpath:com/trolltech/examples/images/cheese.png");
        
        byte bytes[] = img.copyOfBytes();        
        QImage img2 = new QImage(bytes, img.width(), img.height(), img.format());
                
        assertTrue(img.operator_equal(img2));
    }
    
    @Test
    public void testQTextCursorSelectedTableCells() {
        QTextDocument document = new QTextDocument();        
        QTextBlock block = document.begin();
        QTextCursor cursor = new QTextCursor(block);

        cursor.movePosition(QTextCursor.MoveOperation.Start);
        cursor.insertTable(15, 20);
                       
        cursor.select(QTextCursor.SelectionType.Document);
        //cursor.movePosition(QTextCursor.MoveOperation.End, QTextCursor.MoveMode.KeepAnchor);
        
        assertTrue(cursor.hasSelection());
        
        QTableArea cells = cursor.selectedTableCells();        
        assertEquals(15, cells.columnCount);
        assertEquals(20, cells.rowCount);        
        assertEquals(0, cells.column);
        assertEquals(0, cells.row);
    }
    
    @Test
    public void testQPixmapSaveLoadIODevice() {
        QBuffer buffer = new QBuffer();
        buffer.open(QIODevice.OpenModeFlag.WriteOnly);
                
        QPixmap pmSave = new QPixmap("classpath:com/trolltech/examples/images/cheese.png");
        assertFalse(pmSave.isNull());
        assertEquals(94, pmSave.width());
        assertTrue(pmSave.save(buffer, "PNG"));
        
        buffer.close();
        buffer.open(QIODevice.OpenModeFlag.ReadOnly);
        
        QPixmap pmLoad = new QPixmap();
        pmLoad.loadFromData(buffer.buffer(), "JPEG");
        assertTrue(pmLoad.isNull());
        
        pmLoad.loadFromData(buffer.buffer(), "PNG");
        buffer.close();
        
        assertFalse(pmLoad.isNull());
        assertEquals(94, pmLoad.width());
        
        QImage imgSave = pmSave.toImage();
        QImage imgLoad = pmLoad.toImage();
        
        assertTrue(imgSave.operator_equal(imgLoad));

        buffer = new QBuffer();
        buffer.open(QIODevice.OpenModeFlag.WriteOnly);
        
        pmSave.save(buffer, "JPEG");
        pmLoad = new QPixmap();
        pmLoad.loadFromData(buffer.buffer(), "PNG");
        assertTrue(pmLoad.isNull());
        
        pmLoad.loadFromData(buffer.buffer(), "JPEG");
        buffer.close();
        
        assertFalse(pmLoad.isNull());
        assertEquals(94, pmLoad.width());
    }
    
    @Test
    public void testQRegionSetRects() {
        QRegion region = new QRegion();
        
        {
            QRect rects[] = new QRect[2];
            rects[0] = new QRect(0, 0, 10, 10);
            rects[1] = new QRect(5, 11, 10, 10);
            
            region.setRects(rects);
        }
        
        {
            List<QRect> rects = region.rects();
            
            assertEquals(2, rects.size());
            
            assertEquals(0, rects.get(0).x());
            assertEquals(0, rects.get(0).y());
            assertEquals(10, rects.get(0).width());
            assertEquals(10, rects.get(0).height());

            assertEquals(5, rects.get(1).x());
            assertEquals(11, rects.get(1).y());
            assertEquals(10, rects.get(1).width());
            assertEquals(10, rects.get(1).height());
                      
        }        
    }
    
    @Test
    public void testQPictureData() {
        QTemporaryFile file = setUpPicture();        
        assertTrue(file.open(QIODevice.OpenModeFlag.ReadOnly));
        
        QPicture picture = new QPicture();
        picture.load(file);        
        file.close();
        
        QPicture otherPicture = new QPicture();
        otherPicture.setData(picture.data());
        
        verifyQPicture(otherPicture);        
    }
    
    @Test
    public void testQPictureSetDataVirtualCall() {
        QTemporaryFile file = setUpPicture();
        assertTrue(file.open(QIODevice.OpenModeFlag.ReadOnly));
        
        QByteArray ba = file.readAll();
        
        PictureSubclassSubclass pss = new PictureSubclassSubclass();
        pss.callSetData(ba);
        
        assertEquals(ba.size(), pss.data.length);
        
        verifyQPicture(pss);
    }
    
    @Test
    public void testQPictureSetDataNormalCall() {
        QTemporaryFile file = setUpPicture();
        
        assertTrue(file.open(QIODevice.OpenModeFlag.ReadOnly));
        byte bytes[] = file.readAll().toByteArray();
        file.close();
        
        QPicture picture = new QPicture();
        picture.setData(bytes);
        
        verifyQPicture(picture);
    }
    
    @Test
    public void testQPictureLoadFromIODevice() {
        QTemporaryFile file = setUpPicture();
    
        assertTrue(file.open(QIODevice.OpenModeFlag.ReadOnly));
        {
            QPicture picture = new QPicture();
            assertTrue(picture.load(file));
                        
            verifyQPicture(picture);
            file.close();
        }
    }
    
    @Test
    public void testQLineFintersection() {
        QLineF line1 = new QLineF(10, 0, 10, 20);
        QLineF line2 = new QLineF(0, 10, 20, 10);
        
        QPointF intersectionPoint = new QPointF();
        
        assertEquals(QLineF.IntersectType.BoundedIntersection, line1.intersect(line2, intersectionPoint));
        assertEquals(10.0, intersectionPoint.x());
        assertEquals(10.0, intersectionPoint.y());
        
        line2 = new QLineF(0, 30, 20, 30);
        assertEquals(QLineF.IntersectType.UnboundedIntersection, line1.intersect(line2, intersectionPoint));
        assertEquals(10.0, intersectionPoint.x());
        assertEquals(30.0, intersectionPoint.y());        
        
        line2 = new QLineF(11, 0, 11, 20);
        assertEquals(QLineF.IntersectType.NoIntersection, line1.intersect(line2, null));
    }
    
    @Test
    public void testQDataStreamReadWriteBytes() {
        QByteArray ba = new QByteArray();
                
        {
            QDataStream stream = new QDataStream(ba, QIODevice.OpenModeFlag.WriteOnly);                
            byte bytes[] = new QByteArray("abra ka dabra").toByteArray();
            stream.writeBytes(bytes, bytes.length);
        }
        
        {
            QDataStream stream = new QDataStream(ba);            
            byte bytes[] = stream.readBytes();
            assertEquals("abra ka dabra".length(), bytes.length);
            assertEquals("abra ka dabra", new QByteArray(bytes).toString());
        }
        
        {
            boolean caught = false;           
            QDataStream stream = new QDataStream(ba, QIODevice.OpenModeFlag.WriteOnly);
            
            try {
                stream.writeBytes(null, 0);
            } catch (Exception e) {
                caught = true;
            }
            
            assertTrue(caught);
        }
        
        {
            QByteArray empty = new QByteArray();
            
            QDataStream stream = new QDataStream(empty);            
            byte bytes[] = stream.readBytes();
            assertEquals(0, bytes.length);
        }       
    }
    
    
    @Test
    public void testQDataStreamByteArrayConstruction() {
        {
            QByteArray ba = new QByteArray();
            
            QDataStream stream = new QDataStream(ba, QIODevice.OpenModeFlag.WriteOnly);
            stream.operator_shift_left(1.2);
            stream.operator_shift_left(3.2);
            stream.dispose();
            
            stream = new QDataStream(ba);
            
            double f[] = new double[2];
            stream.operator_shift_right(f);
            
            assertEquals(1.2, f[0]);
            assertEquals(3.2, f[1]);
        }

        for (int i=0; i<100; ++i) {
            QDataStream stream = new QDataStream(new QByteArray(), QIODevice.OpenModeFlag.WriteOnly);                                                
            System.gc();
            
            stream.operator_shift_left(1.2);
            stream.operator_shift_left(3.2);            
        }
        
    }
    
    @Test
    public void testQBitmapFromData() {
        byte bits[] = { 0x01, 0x02, 0x4, 0x8, 0x10, 0x20, 0x40, (byte) 0x80 };
        
        QBitmap bm = QBitmap.fromData(new QSize(8, 8), bits);
        assertFalse(bm.isNull());
        assertEquals(8, bm.width());
        assertEquals(8, bm.height());
        
        QImage img = bm.toImage();                
        assertEquals(1, img.pixelIndex(0, 0));
        assertEquals(0, img.pixelIndex(1, 0));
        assertEquals(1, img.pixelIndex(1, 1));
        
              
    }
    
    @Test
    public void testQBitmapStringStringConstructor() {
        {
            QBitmap bm = new QBitmap("classpath:com/trolltech/examples/images/cheese.png", "PNG");
            assertFalse(bm.isNull());
            assertEquals(94, bm.width());
        }
        
        {
            QBitmap bm = new QBitmap("classpath:com/trolltech/examples/images/cheese.png");
            assertFalse(bm.isNull());
            assertEquals(94, bm.width());
        }
        
        {
            QBitmap bm = new QBitmap("classpath:com/trolltech/examples/images/cheesemisspelling.png");
            assertTrue(bm.isNull());
        }
    }
    
    @Test
    public void testQDataStreamWriteRawData() {
        OtherIODeviceSubclass device = new OtherIODeviceSubclass();
        assertTrue(device.open(QIODevice.OpenModeFlag.WriteOnly));
        QDataStream stream = new QDataStream(device);

        QByteArray ba = new QByteArray("onetwothree");        
        while (ba.size() > 0)
            ba.remove(0, stream.writeRawData(ba.toByteArray()));
        
        assertEquals("onetwothree", device.ba.toString());
    }
    
    @Test
    public void testQDataStreamReadRawData() {
        
        OtherIODeviceSubclass device = new OtherIODeviceSubclass();
        assertTrue(device.open(QIODevice.OpenModeFlag.ReadOnly));
        QDataStream stream = new QDataStream(device);
        
        int i = 1;
        byte bytes[] = new byte[128];
        QByteArray ba = new QByteArray();
        while (i > 0) {
            i = stream.readRawData(bytes);
            for (int j=0; j<i; ++j)
                ba.append(bytes[j]);                
        }
        assertEquals(3, ba.size());
        
        assertEquals(ba.toString(), new QByteArray(device.bytes).toString());
        
    }

    @Test
    public void testQBufferRetainBuffer() {
        QBuffer buffer;        
        {
            QByteArray ba = new QByteArray("ABC");
            buffer = new QBuffer(ba, null);
        }        
        System.gc();        
        assertEquals("ABC", buffer.buffer().toString());        
        System.gc();        
        buffer.setData( new byte[] {(byte) 'a', (byte) 'b', (byte) 'c'} );        
        assertEquals("abc", buffer.buffer().toString());
        
        {
            QByteArray ba2 = new QByteArray("HIJ");
            buffer.setBuffer(ba2);
        }        
        System.gc();        
        assertEquals("HIJ", buffer.buffer().toString());
        
        buffer.setData(new QByteArray("KLM"));
        assertEquals("KLM", buffer.buffer().toString());
    }
    
    @Test
    public void testQBufferUseBuffer() {
        QByteArray ba = new QByteArray("CDE");
        QBuffer buffer = new QBuffer(ba);       
        assertEquals("CDE", buffer.buffer().toString());
        
        ba.append("fgh");        
        assertEquals("CDEfgh", buffer.buffer().toString());
        
        buffer.setData(new QByteArray("cdeFGH"));        
        assertEquals("cdeFGH", ba.toString());
        
        QByteArray ba2 = new QByteArray("HIJ");
        buffer.setBuffer(ba2);
        
        assertEquals("HIJ", buffer.buffer().toString());
        
        ba2.append("KLM");
        assertEquals("HIJKLM", buffer.buffer().toString());
    }
    
    @Test
    public void testQTextCodecForNameString() {
        QTextCodec codec = QTextCodec.codecForName("UTF-8");
        
        assertTrue(codec != null);
        assertEquals("UTF-8", codec.name().toString());
        
        codec = QTextCodec.codecForName("Magic Text Codec Which Successfully Improves What You've Written");
        assertTrue(codec == null);
    }    
    
/* These will crash the JVM currently, so they're out
  
      
    @Test
    public void testTextCodecConvertToUnicode() {
        TextCodecSubclassSubclass tcss = new TextCodecSubclassSubclass();
        
        QTextCodec_ConverterState state = new QTextCodec_ConverterState();        
        
        assertEquals("abba", tcss.callToUnicode(new QByteArray("baab"), state));
        assertTrue(state == tcss.receivedState);
        assertTrue(state == tcss.receivedState());
        assertEquals("baab", new QByteArray(tcss.receivedByte).toString());        
    }
    
    @Test
    public void testTextCodecConvertFromUnicode() {
        TextCodecSubclassSubclass tcss = new TextCodecSubclassSubclass();
        QTextCodec_ConverterState state = new QTextCodec_ConverterState();
        
        assertEquals("sas", tcss.callFromUnicode("asa", state).toString());
        assertTrue(state == tcss.receivedState);
        assertTrue(state == tcss.receivedState());
        assertEquals("asa", new String(tcss.receivedChar));
    }
    
*/       
    @Test 
    public void testCrashReminder() {
        assertEquals("The JVM will crash when you instantiate a QTextCodec subclass", 
                     "because it's a global static which will cause jambi to attach the vm to a thread as" +
                     " it's shutting down");
    }
    
    public static void main(String args[]) {
        QApplication.initialize(args);
        
        TestInjectedCode test = new TestInjectedCode();
        
        test.testOperatorAssignOtherTypeTemplate();
        //test.testQMenuAddActionJavaSlot();
        //test.testTextCodecConvertFromUnicode();
    }
    
    @Test
    public void testIODeviceWriteData() {
        IODeviceSubclassSubclass iodss = new IODeviceSubclassSubclass(128);
        
        QByteArray ba = new QByteArray("Evil draws men together");
        assertEquals(23, (int) iodss.callWriteData(ba));
        assertEquals(23, iodss.inputBufferSize);
        assertEquals("Evil draws men together", new QByteArray(iodss.buffer).toString());
        QNativePointer np = iodss.buffer();
        byte data[] = new byte[23];
        for (int i=0; i<data.length; ++i)
            data[i] = np.byteAt(i);
        assertEquals("Evil draws men together", new QByteArray(data).toString());                 
    }
    
    @Test
    public void testIODeviceReadLineData() {
        IODeviceSubclassSubclass iodss = new IODeviceSubclassSubclass(128);
        
        assertEquals(45, (int) iodss.callReadLineData());
        assertEquals(128, iodss.inputBufferSize);
        assertEquals(45, iodss.buffer.length);
        assertEquals("Confucius say: Don't go outside with wet hair", new QByteArray(iodss.buffer).toString());

        QNativePointer np = iodss.buffer();
        byte data[] = new byte[45];
        for (int i=0; i<data.length; ++i)
            data[i] = np.byteAt(i);
        assertEquals("Confucius say: Don't go outside with wet hair", new QByteArray(data).toString());         
    }
    
    @Test
    public void testIODeviceReadData() {
        IODeviceSubclassSubclass iodss = new IODeviceSubclassSubclass(128);
                              
        assertEquals(10, (int) iodss.callReadData());
        assertEquals(128, iodss.inputBufferSize);
        assertEquals(10, iodss.buffer.length);
        assertEquals("I am a boy", new QByteArray(iodss.buffer).toString());
        
        QNativePointer np = iodss.buffer();
        byte data[] = new byte[10];
        for (int i=0; i<data.length; ++i)
            data[i] = np.byteAt(i);
        assertEquals("I am a boy", new QByteArray(data).toString()); 
    }
    
    @Test
    public void testIODeviceWrite() {
        QTemporaryFile file = new QTemporaryFile();
        file.setAutoRemove(true);
        
        assertTrue(file.open(QIODevice.OpenModeFlag.WriteOnly));        
        byte data[] = new QByteArray("I am a boy").toByteArray();        
        assertEquals(10, file.write(data));        
        file.close();
        
        assertTrue(file.open(QIODevice.OpenModeFlag.ReadOnly));
        QByteArray all = file.readAll();        
        assertEquals("I am a boy", all.toString());
        
        file.dispose();
    }

    @Test
    public void testIODevicePeek() {
        QIODevice file = new QFile("classpath:com/trolltech/autotests/TestInjectedCode.java");
        
        assertTrue(file.open(QIODevice.OpenModeFlag.ReadOnly));

        byte bytes[] = new byte[7];
        assertEquals(7, file.peek(bytes));
        assertEquals((byte) 'p', bytes[0]);
        assertEquals((byte) 'a', bytes[1]);
        assertEquals((byte) 'c', bytes[2]);
        assertEquals((byte) 'k', bytes[3]);
        assertEquals((byte) 'a', bytes[4]);
        assertEquals((byte) 'g', bytes[5]);
        assertEquals((byte) 'e', bytes[6]);
        
        file.close();
    }
    
    @Test
    public void testIODeviceGetByteFail() {
        QIODevice file = new QFile();
        
        int b = file.getByte();
        assertEquals(-1, b);
    }
    
    @Test
    public void testIODeviceGetByteSuccess() {
        QIODevice file = new QFile("classpath:com/trolltech/autotests/TestInjectedCode.java");
        
        assertTrue(file.open(QIODevice.OpenModeFlag.ReadOnly));
        
        byte b = (byte) file.getByte();
        assertEquals((byte) 'p', b);
        
        b = (byte) file.getByte();
        assertEquals((byte) 'a', b);
        
        file.close();
    }
        
    @Test
    public void testByteArrayStartsWithString() {
        QByteArray ba = new QByteArray("hello");
        
        assertTrue(ba.startsWith("hell"));
        assertFalse(ba.startsWith("heaven"));
    }
    
    @Test
    public void testByteArraySetNumOverloads() {
        QByteArray ba = new QByteArray();
        
        QByteArray ba2 = ba.setNum(11);
        assertEquals("11", ba.toString());
        assertEquals("11", ba2.toString());
        
        QByteArray ba3 = ba.setNum(1.333333333);
        assertEquals("1.33333", ba.toString());
        assertEquals("1.33333", ba2.toString());
        assertEquals("1.33333", ba3.toString());
        
        ba3.setNum(13.3333333333, 'e');
        assertEquals("1.333333e+01", ba.toString());
        assertEquals("1.333333e+01", ba2.toString());
        assertEquals("1.333333e+01", ba3.toString());        
    }
    
    @Test
    public void testByteArrayReplaceString() {
        QByteArray ba = new QByteArray("hello");
        
        QByteArray ba2 = ba.replace(new QByteArray("e"), "a");
        
        assertEquals("hallo", ba.toString());
        assertEquals("hallo", ba2.toString());
    }
    
    @Test
    public void testByteArrayPushBackString() {
        QByteArray ba = new QByteArray("hello");
        
        ba.push_back("h nice");
        assertEquals("helloh nice", ba.toString());                
    }
    
    @Test
    public void testByteArrayPushFrontString() {
        QByteArray ba = new QByteArray("hello");
        
        ba.push_front("c");
        assertEquals("chello", ba.toString());        
    }

    @Test
    public void testByteArrayPrependString() {
        QByteArray ba = new QByteArray("hello");
        
        QByteArray ba2 = ba.prepend("c");
        assertEquals("chello", ba.toString());
        assertEquals("chello", ba2.toString());
    }
    
    @Test
    public void testByteArrayInsert() {
        QByteArray ba = new QByteArray("hello");
        
        QByteArray ba2 = ba.insert(3, new QByteArray("gefyl"));
        assertEquals("helgefyllo", ba2.toString());
        assertEquals("helgefyllo", ba.toString());
    }
    
    @Test
    public void testByteArrayFill() {
        QByteArray ba = new QByteArray("hello");
        
        QByteArray ba2 = ba.fill((byte) 'a');
        assertEquals(ba.toString(), "aaaaa");
        assertEquals(ba2.toString(), "aaaaa");
                
        QByteArray ba3 = ba.fill((byte) 'b', 3);
        assertEquals(ba.toString(), "bbb");
        assertEquals(ba2.toString(), "bbb");
        assertEquals(ba3.toString(), "bbb");
    }
    
    @Test
    public void testByteArrayEndsWithString() {
        QByteArray ba = new QByteArray("hello");
        
        assertTrue(ba.endsWith("lo"));
        assertFalse(ba.endsWith("lol"));
    }
    
    @Test
    public void testByteArrayCountString() {
        QByteArray ba = new QByteArray("hello");
        
        assertEquals(0, ba.count("heaven"));
        assertEquals(1, ba.count("hell"));
        assertEquals(2, ba.count("l"));        
    }
    
    @Test
    public void testByteArrayContainsString() {
        QByteArray ba = new QByteArray("hello");
        
        assertFalse(ba.contains("heaven"));
        assertTrue(ba.contains("hell"));
    }
    
    @Test
    public void testByteArrayFrombytearray() {
        byte hello[] = new byte[5];               
        hello[0] = (byte) 'h';
        hello[1] = (byte) 'e';
        hello[2] = (byte) 'l';
        hello[3] = (byte) 'l';
        hello[4] = (byte) 'o';
        
        QByteArray ba = new QByteArray(hello);
        assertEquals(5, ba.size());
        assertEquals((byte) 'h', ba.at(0));
        assertEquals((byte) 'e', ba.at(1));
        assertEquals((byte) 'l', ba.at(2));
        assertEquals((byte) 'l', ba.at(3));
        assertEquals((byte) 'o', ba.at(4));
    }

    @Test
    public void testQLocaleToDouble() {
        {
            QLocale locale = new QLocale(QLocale.Language.C);
            
            boolean caughtException = false;
            double d = 0.0;
            try {
                d = locale.toDouble("1234,56");
            } catch (NumberFormatException e) {
                caughtException = true;
            }
            
            assertTrue(caughtException);
            assertEquals(0.0, d);
            d = 0.0;
            
            caughtException = false;
            try {
                d = locale.toDouble("1234.56");
            } catch (NumberFormatException e) {
                caughtException = true;
            }
            
            assertFalse(caughtException);
            assertEquals(1234.56, d);
        }
        
        {
            QLocale locale = new QLocale(QLocale.Language.Norwegian);
            
            boolean caughtException = false;
            double d = 0.0;
            try {
                d = locale.toDouble("1234,56");
            } catch (NumberFormatException e) {
                caughtException = true;
            }
            
            assertFalse(caughtException);
            assertEquals(1234.56, d);
            d = 0.0;
            
            
            caughtException = false;
            try {
                d = locale.toDouble("1234.56");
            } catch (NumberFormatException e) {
                caughtException = true;
            }
            
            assertTrue(caughtException);
            assertEquals(0.0, d);
        }
        
    }
    
    @Test 
    public void testQLocaleToInt() {
        QLocale locale = new QLocale(QLocale.Language.Norwegian, QLocale.Country.Norway);
        
        assertEquals(16, locale.toInt("0x10"));
        assertEquals(16, locale.toInt("10", 16));
        assertEquals(10, locale.toInt("10"));
        assertEquals(10, locale.toInt("10", 10));
    }
    
    @Test
    public void testNestedOperators() {
        QPoint p = new QPoint(2, 3);
        assertEquals(2, p.x());
        assertEquals(3, p.y());

        QPoint self = p.operator_multiply_assign(3).operator_add_assign(new QPoint(3, 6)).operator_divide_assign(3).operator_subtract_assign(new QPoint(2, 1));
        assertEquals(1, self.x());
        assertEquals(4, self.y());
        assertEquals(1, p.x());
        assertEquals(4, p.y());        
    }
    
    @Test
    public void testUnarySelfType() {
        QPoint p = new QPoint(2, 3);
        assertEquals(2, p.x());
        assertEquals(3, p.y());
        
        QPoint self = p.operator_add_assign(new QPoint(5, 6));
        assertEquals(7, self.x());
        assertEquals(9, self.y());
        assertEquals(7, p.x());
        assertEquals(9, p.y());        
    }
    
    @Test
    public void testUnaryOtherType() {
        QPoint p = new QPoint(2, 3);
        assertEquals(2, p.x());
        assertEquals(3, p.y());
        
        QPoint self = p.operator_multiply_assign(3);
        assertEquals(6, self.x());
        assertEquals(9, self.y());
        assertEquals(6, p.x());
        assertEquals(9, p.y());        
    }
    
    @Test
    public void testWeekNumber() {
        QDate date = new QDate(2000, 1, 1);
        
        int weekNumber = date.weekNumber();
        assertEquals(52, weekNumber);
        
        int yearNumber = date.yearOfWeekNumber();
        assertEquals(1999, yearNumber);
        
        date.setDate(2002, 12, 31);
        weekNumber = date.weekNumber();
        assertEquals(1, weekNumber);
        
        yearNumber = date.yearOfWeekNumber();
        assertEquals(2003, yearNumber);        
    }
    
    @Test 
    public void testOperatorAssignOtherTypeTemplate() {
        QDir in = new QDir("classpath:com/trolltech/");
        QDir other = new QDir("classpath:com/trolltech/examples/");
        assertFalse(other.operator_equal(in));
        
        String out = "classpath:com/trolltech/examples/";
        QDir self = in.operator_assign(out);        
        assertTrue(self.operator_equal(in));        
        assertEquals(self.absolutePath(), in.absolutePath());
                        
        assertTrue(other.operator_equal(in));
        assertEquals(other.count(), in.count());
        assertEquals(other.absolutePath(), in.absolutePath());
    }
    
    @Test
    public void testOperatorAssignSelfTypeTemplate() {
        QDate date = new QDate(2006, 11, 30);
        QTime time = new QTime(8, 19);
        
        QDateTime in = new QDateTime(date, time);
        assertEquals(30, in.date().day());
        assertEquals(11, in.date().month());
        assertEquals(2006, in.date().year());
        assertEquals(8, in.time().hour());
        assertEquals(19, in.time().minute());
        
        QDate date_out = new QDate(1963, 11, 22);
        QTime time_out = new QTime(12, 30);
        QDateTime out = new QDateTime(date_out, time_out);
        assertEquals(22, out.date().day());
        assertEquals(11, out.date().month());
        assertEquals(1963, out.date().year());
        assertEquals(12, out.time().hour());
        assertEquals(30, out.time().minute());
                
        QDateTime self = out.operator_assign(in);
        assertTrue(self.operator_equal(out));
        assertTrue(self.operator_equal(in));
        assertTrue(in.operator_equal(out));

        assertEquals(30, out.date().day());
        assertEquals(11, out.date().month());
        assertEquals(2006, out.date().year());
        assertEquals(8, out.time().hour());
        assertEquals(19, out.time().minute());

        assertEquals(30, self.date().day());
        assertEquals(11, self.date().month());
        assertEquals(2006, self.date().year());
        assertEquals(8, self.time().hour());
        assertEquals(19, self.time().minute());                
    }
    
}
