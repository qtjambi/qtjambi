package com.trolltech.unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.trolltech.qt.core.QAbstractFileEngine;
import com.trolltech.qt.gui.QImage;
import com.trolltech.qt.gui.QImage.Format;
import com.trolltech.qt.gui.QImageReader;

public class TestQImage {

	private QImage qimage32;
	private QImage qimage64PNG;
	private QImage sample;
	private QImage generator;
	private QImage blueAngleJPG;
	private QImage anSVGImage;
	private QImage anotherSVGImage;
	private String qimage64Path = "classpath:com/trolltech/images/qtlogo-64.png";
	private String blueAngleJPGPath = "classpath:com/trolltech/images/blue_angle_swirl.jpg";
	private String anSVGImagePath = "classpath:com/trolltech/unittests/Logo-ubuntu_cof-orange-hex.svg";
	private String anotherSVGImagePath = "classpath:com/trolltech/images/svg-cards.svg";
	
	@org.junit.BeforeClass
	public static void init() {
		QAbstractFileEngine.addSearchPathForResourceEngine(".");
		System.out.println(QImageReader.supportedImageFormats());
	}
	
	@org.junit.Before
	public void setUp() throws Exception {
		qimage32 = new QImage("classpath:com/trolltech/images/qtlogo-32.png");
		qimage64PNG = new QImage();
		sample = new QImage(3, 3, Format.Format_Indexed8);
		generator = new QImage("classpath:com/trolltech/images/generator.png");
		blueAngleJPG = new QImage();
		anSVGImage = new QImage();
		anotherSVGImage = new QImage();
	}
	
	@org.junit.After
	public void tearDown() throws Exception {
		qimage32 = null;
		qimage64PNG = null;
		sample = null;
		generator = null;
		blueAngleJPG = null;
		anSVGImage = null;
		anotherSVGImage = null;
	}
	
	@org.junit.Test
	public void testBasic() {
		assertFalse(qimage32.isNull());
		assertFalse(qimage32.isGrayscale());
		assertEquals(generator.depth(), 32);
	}
	
	@org.junit.Test()
	public void testLoadPNG() {
		assertTrue(qimage64PNG.load(qimage64Path));
	}
	
	@org.junit.Test
	public void testLoadJPG() {
		assertTrue(blueAngleJPG.load(blueAngleJPGPath));
	}
	
	@org.junit.Test
	public void testLoadSVGSmall() {
		assertTrue(anSVGImage.load(anSVGImagePath));
	}
	
	@org.junit.Ignore
	/**
	 * The following test makes the JVM crash.
	 */
	@org.junit.Test
	public void testLoadSVGBig() {
		assertTrue(anotherSVGImage.load(anotherSVGImagePath));
	}
	
	@org.junit.Test
	public void testDimensions() {
		assertEquals(qimage32.width(), 32);
		assertEquals(qimage32.width(), qimage32.height());
	}
	
	@org.junit.Test
	public void testSize() {
		int size = qimage32.height() * qimage32.bytesPerLine();
		assertEquals(size, qimage32.byteCount());
	}
	
	@org.junit.Test
	public void testConvertToFormat() {
		assertEquals(generator.format(), Format.Format_RGB32);
		generator = generator.convertToFormat(Format.Format_Mono);
		assertEquals(generator.format(), Format.Format_Mono);
	}
	
	@org.junit.Test
	public void testSetPixel() {
		List<Integer> colors = new ArrayList<Integer>();
		
		colors.add(5);
		colors.add(10);
		colors.add(15);
		
		sample.setColorTable(colors);
		
		// x, y, color index in the color table 
		sample.setPixel(0, 0, 0);
		sample.setPixel(2, 2, 2);
		
		assertEquals(sample.pixel(2, 2), 15);
		assertEquals(sample.pixel(0, 0), 5);
	}
	
}
