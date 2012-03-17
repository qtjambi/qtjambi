/**
 * 
 */

package com.trolltech.unittests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.After;

import com.trolltech.qt.gui.QImageReader;
import com.trolltech.qt.core.QByteArray;
import com.trolltech.qt.core.QFile;
import com.trolltech.qt.core.QFileInfo;
import com.trolltech.qt.core.QIODevice;

import com.trolltech.autotests.Utils;

public class TestPluginImageFormats extends QApplicationTest {

	@org.junit.Before
	public void setUp() {
	}

	@org.junit.After
	public void tearDown() {
	}

	private boolean searchList(List<String> list, String s) {
		s = s.toLowerCase();
		for(String compare : list) {
			if(s.compareToIgnoreCase(compare) == 0)
				return true;
		}
		return false;
	}

	@org.junit.Test
	public void testSupportedImageFormats() {
		List<QByteArray> list = QImageReader.supportedImageFormats();
		List<String> labelList = new ArrayList<String>();
		for(QByteArray ba : list) {
                	StringBuilder sb = new StringBuilder();
			for(int i = 0; i < ba.size(); i++)
				sb.append((char)ba.at(i));
			labelList.add(sb.toString());
			Utils.println(1, "format: " + ba.toString() + " " + sb.toString());
		}

		assertTrue("support: png", searchList(labelList, "png"));
		assertTrue("support: gif", searchList(labelList, "gif"));
		assertTrue("support: jpg", searchList(labelList, "jpg"));	// aka "jpeg"
		assertTrue("support: ico", searchList(labelList, "ico"));
		assertTrue("support: bmp", searchList(labelList, "bmp"));
		assertTrue("support: mng", searchList(labelList, "mng"));
		assertTrue("support: pbm", searchList(labelList, "pbm"));
		assertTrue("support: pgm", searchList(labelList, "pgm"));
		assertTrue("support: ppm", searchList(labelList, "ppm"));
		assertTrue("support: xbm", searchList(labelList, "xbm"));
		assertTrue("support: xpm", searchList(labelList, "xpm"));
		assertTrue("support: svg", searchList(labelList, "svg"));
		assertTrue("support: tif", searchList(labelList, "tif"));	// aka "tiff"

		// Not found on any system I tested ~4.7.x
		//assertTrue("support: tga", searchList(labelList, "tga"));
	}

	// TODO: Load each kind of those supported images from a known file with colour wheel and
	//  test loading, size, meta info, a handful of pixel ARGB values.

	@org.junit.Test
	public void testFoobar() {
		ClassLoader cl = TestPluginImageFormats.class.getClassLoader();
		String res = "com/trolltech/autotests/TestClassFunctionality.jar";
		InputStream inStream = null;
		try {
			URL url = cl.getResource(res);
			Utils.println(1, "testFoobar(res=\"" + res + "\"; url=\"" + url + "\")");
			inStream = cl.getResourceAsStream(res);
			Utils.println(1, "inStream(\"" + res + "\")=" + inStream);
			byte[] bA = new byte[4096];
			int n = inStream.read(bA);
			Utils.println(1, "inStream.read()=" + n);
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(inStream != null) {
				try {
					inStream.close();
				} catch(IOException eat) {
				}
				inStream = null;
			}
		}
	}

	@org.junit.Test
	public void testFoobar2() {
		try {
			String res = "F:\\JavaDevel\\deps\\apache-ant-1.8.2\\lib\\ant-junit4.jar";
			QFileInfo fileinfo = new QFileInfo(res);
			Utils.println(1, "testFoobar2(A res=\"" + res + "\"; fileinfo=\"" + fileinfo + "\")");
		} catch(Throwable t) {
			t.printStackTrace();
		} finally {
		}

		try {
			String res = "F:/JavaDevel/deps/apache-ant-1.8.2/lib/ant-junit4.jar";
			QFileInfo fileinfo = new QFileInfo(res);
			Utils.println(1, "testFoobar2(B res=\"" + res + "\"; fileinfo=\"" + fileinfo + "\")");
		} catch(Throwable t) {
			t.printStackTrace();
		} finally {
		}

		String res = "classpath:com/trolltech/autotests/TestClassFunctionality.jar";
		try {
			QFileInfo fileinfo = new QFileInfo(res);
			Utils.println(1, "testFoobar2(res=\"" + res + "\"; fileinfo=\"" + fileinfo + "\")");
			Utils.println(1, "testFoobar2(res=\"" + res + "\"; fileinfo.size()=" + fileinfo.size());
			Utils.println(1, "testFoobar2(res=\"" + res + "\"; fileinfo.exists()=" + fileinfo.exists());
			Utils.println(1, "testFoobar2(res=\"" + res + "\"; fileinfo.fileName()=" + fileinfo.fileName());
		} catch(Throwable t) {
			t.printStackTrace();
		} finally {
		}
	}

	@org.junit.Test
	public void testFoobar3() {
		try {
			String res = "F:\\JavaDevel\\deps\\apache-ant-1.8.2\\lib\\ant-junit4.jar";
			QFile file = new QFile(res);
			Utils.println(1, "testFoobar3(A res=\"" + res + "\"; file=\"" + file + "\"); file.exists()=" + file.exists());
		} catch(Throwable t) {
			t.printStackTrace();
		} finally {
		}

		try {
			String res = "F:/JavaDevel/deps/apache-ant-1.8.2/lib/ant-junit4.jar";
			QFile file = new QFile(res);
			Utils.println(1, "testFoobar3(A res=\"" + res + "\"; file=\"" + file + "\"); file.exists()=" + file.exists());
		} catch(Throwable t) {
			t.printStackTrace();
		} finally {
		}

		String res = "classpath:com/trolltech/autotests/TestClassFunctionality.jar";
		try {
			// classpath:com/trolltech/autotests/TestClassFunctionality.jar
			// classpath:com/trolltech/examples/images/cheese.png
			QFile file = new QFile(res);

			boolean bf = file.open(QIODevice.OpenModeFlag.ReadOnly);
			Utils.println(1, "testFoobar3(res=\"" + res + "\"; file=\"" + file + "\"); file.open()="+bf);

			QByteArray ba = file.readAll();
			Utils.println(1, "testFoobar3(res=\"" + res + "\"; file=\"" + file + "\"); file.readAll()="+ba.size());
		} catch(Throwable t) {
			t.printStackTrace();
		} finally {
		}
	}

	// Need to load an animated image and then a non-animated and test
	//  ideally one of each supported format
//	@org.junit.Test
//	public void testSupportsAnimation() {
//		boolean bf = QImageReader.supportsAnimation();
//		assertTrue("QImageReader.supportsAnimation()", bf);
//	}

	// Same as above...
//	@org.junit.Test
//	public void testSupportsOption() {
//		boolean bf = QImageReader.supportsOption();
//		assertTrue("QImageReader.supportsAnimation()", bf);
//	}

}
