package com.trolltech.autotests;

import java.util.Vector;

import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QRect;
import com.trolltech.qt.gui.*;

import static org.junit.Assert.*;
import org.junit.*;

public class TestQTransform extends QApplicationTest {

	class Data {
		public Data(String name, QTransform matrix, QRect src, QPolygon res) {
			this.name = name;
			this.matrix = matrix;
			this.src = src;
			this.res = res;
		}

		String name;
		QTransform matrix;
		QRect src;
		QPolygon res;
	}

	Vector<Data> makeData() {
		Vector<Data> data = new Vector<Data>();

		data.add(new Data("identity", new QTransform(1, 0, 0, 1, 0, 0),
				new QRect(10, 20, 30, 40), new QPolygon(new QRect(10, 20, 30,
						40))));
		// scaling
		data.add(new Data("scale 0", new QTransform(2, 0, 0, 2, 0, 0),
				new QRect(10, 20, 30, 40), new QPolygon(new QRect(20, 40, 60,
						80))));

		data.add(new Data("scale 1", new QTransform(10, 0, 0, 10, 0, 0),
				new QRect(10, 20, 30, 40), new QPolygon(new QRect(100, 200,
						300, 400))));
		// mirroring
		data.add(new Data("mirror 0", new QTransform(-1, 0, 0, 1, 0, 0),
				new QRect(10, 20, 30, 40), new QPolygon(new QRect(-40, 20, 30,
						40))));

		data.add(new Data("mirror 1", new QTransform(1, 0, 0, -1, 0, 0),
				new QRect(10, 20, 30, 40), new QPolygon(new QRect(10, -60, 30,
						40))));

		data.add(new Data("mirror 2", new QTransform(-1, 0, 0, -1, 0, 0),
				new QRect(10, 20, 30, 40), new QPolygon(new QRect(-40, -60, 30,
						40))));

		data.add(new Data("mirror 3", new QTransform(-2, 0, 0, -2, 0, 0),
				new QRect(10, 20, 30, 40), new QPolygon(new QRect(-80, -120,
						60, 80))));

		data.add(new Data("mirror 4", new QTransform(-10, 0, 0, -10, 0, 0),
				new QRect(10, 20, 30, 40), new QPolygon(new QRect(-400, -600,
						300, 400))));

		data.add(new Data("mirror 5", new QTransform(-1, 0, 0, 1, 0, 0),
				new QRect(0, 0, 30, 40),
				new QPolygon(new QRect(-30, 0, 30, 40))));

		data.add(new Data("mirror 6", new QTransform(1, 0, 0, -1, 0, 0),
				new QRect(0, 0, 30, 40),
				new QPolygon(new QRect(0, -40, 30, 40))));

		data.add(new Data("mirror 7", new QTransform(-1, 0, 0, -1, 0, 0),
				new QRect(0, 0, 30, 40), new QPolygon(new QRect(-30, -40, 30,
						40))));

		data.add(new Data("mirror 8", new QTransform(-2, 0, 0, -2, 0, 0),
				new QRect(0, 0, 30, 40), new QPolygon(new QRect(-60, -80, 60,
						80))));

		data.add(new Data("mirror 9", new QTransform(-10, 0, 0, -10, 0, 0),
				new QRect(0, 0, 30, 40), new QPolygon(new QRect(-300, -400,
						300, 400))));

		double M_PI = 3.14159265897932384626433832795f;

		// rotations
		float deg = 0;
		data.add(new Data("rot 0 a", new QTransform(
				Math.cos(M_PI * deg / 180.), -Math.sin(M_PI * deg / 180.), Math
						.sin(M_PI * deg / 180.), Math.cos(M_PI * deg / 180.),
				0, 0), new QRect(0, 0, 30, 40), new QPolygon(new QRect(0, 0,
				30, 40))));
		deg = 0.00001f;
		data.add(new Data("rot 0 b", new QTransform(
				Math.cos(M_PI * deg / 180.), -Math.sin(M_PI * deg / 180.), Math
						.sin(M_PI * deg / 180.), Math.cos(M_PI * deg / 180.),
				0, 0), new QRect(0, 0, 30, 40), new QPolygon(new QRect(0, 0,
				30, 40))));
		deg = 0;
		data.add(new Data("rot 0 c", new QTransform(
				Math.cos(M_PI * deg / 180.), -Math.sin(M_PI * deg / 180.), Math
						.sin(M_PI * deg / 180.), Math.cos(M_PI * deg / 180.),
				0, 0), new QRect(10, 20, 30, 40), new QPolygon(new QRect(10,
				20, 30, 40))));
		deg = 0.00001f;
		data.add(new Data("rot 0 d", new QTransform(
				Math.cos(M_PI * deg / 180.), -Math.sin(M_PI * deg / 180.), Math
						.sin(M_PI * deg / 180.), Math.cos(M_PI * deg / 180.),
				0, 0), new QRect(10, 20, 30, 40), new QPolygon(new QRect(10,
				20, 30, 40))));

		return data;
	}

	@Test
	public void mapRect() {
		for (Data d : makeData()) {
			assertTrue(d.name, new QPolygon(d.matrix.mapRect(d.src))
					.operator_equal(d.res.toList()));
		}
	}

	@Test
	public void assignments() {
		QTransform m = new QTransform();
		m.scale(2, 3);
		m.rotate(45);
		m.shear(4, 5);

		QTransform c1 = new QTransform(m.toAffine());

		assertEquals(m.m11(), c1.m11());
		assertEquals(m.m12(), c1.m12());
		assertEquals(m.m21(), c1.m21());
		assertEquals(m.m22(), c1.m22());
		assertEquals(m.dx(), c1.dx());
		assertEquals(m.dy(), c1.dy());

		QTransform c2 = new QTransform().operator_assign(m);
		assertEquals(m.m11(), c2.m11());
		assertEquals(m.m12(), c2.m12());
		assertEquals(m.m21(), c2.m21());
		assertEquals(m.m22(), c2.m22());
		assertEquals(m.dx(), c2.dx());
		assertEquals(m.dy(), c2.dy());
	}

	@Test
	public void mapToPolygon() {
		for (Data d : makeData()) {
			assertTrue(d.name, d.matrix.mapToPolygon(d.src).operator_equal(
					d.res.toList()));
		}
	}

	@Test
	public void translate() {
		QTransform m = new QTransform(1, 2, 3, 4, 5, 6);
		QTransform res2 = new QTransform(m.toAffine());
		QTransform res = new QTransform(1, 2, 3, 4, 75, 106);
		m.translate(10, 20);

		assertTrue(m.operator_equal(res));

		m.translate(-10, -20);
		assertTrue(m.operator_equal(res2));
	}

	@Test
	public void scale() {
		QTransform m = new QTransform(1, 2, 3, 4, 5, 6);
		QTransform res2 = new QTransform(m.toAffine());
		QTransform res = new QTransform(10, 20, 60, 80, 5, 6);
		m.scale(10, 20);
		assertTrue(m.operator_equal(res));
		m.scale(1. / 10., 1. / 20.);
		assertTrue(m.operator_equal(res2));
	}

	@Test
	public void matrix() {
		QMatrix mat1 = new QMatrix();
		mat1.scale(0.3, 0.7);
		mat1.translate(53.3, 94.4);
		mat1.rotate(45);

		QMatrix mat2 = new QMatrix();
		mat2.rotate(33);
		mat2.scale(0.6, 0.6);
		mat2.translate(13.333, 7.777);

		QTransform tran1 = new QTransform(mat1);
		QTransform tran2 = new QTransform(mat2);
		QTransform dummy = new QTransform();
		dummy.setMatrix(mat1.m11(), mat1.m12(), 0, mat1.m21(), mat1.m22(), 0,
				mat1.dx(), mat1.dy(), 1);

		assertTrue(tran1.operator_equal(dummy));
		assertTrue(tran1.inverted().operator_equal(dummy.inverted()));
		assertTrue(tran1.inverted().operator_equal(
				new QTransform(mat1.inverted())));
		assertTrue(tran2.inverted().operator_equal(
				new QTransform(mat2.inverted())));

		QMatrix mat3 = mat1.operator_multiply(mat2);
		QTransform tran3 = tran1.operator_multiply(tran2);
		assertTrue(new QTransform(mat3).operator_equal(tran3));
		assertTrue(mat3.operator_equal(tran3.toAffine()));

		QTransform tranInv = tran1.inverted();
		QMatrix matInv = mat1.inverted();

		QRect rect = new QRect(43, 70, 200, 200);
		QPoint pt = new QPoint(43, 66);

		// FIXME missing equal operator
		assertEquals(tranInv.map(pt).x(), matInv.map(pt).x());
		assertEquals(tranInv.map(pt).x(), matInv.map(pt).x());

		QPainterPath path = new QPainterPath();
		path.moveTo(55, 60);
		path.lineTo(110, 110);
		path.quadTo(220, 50, 10, 20);
		path.closeSubpath();
		assertTrue(tranInv.map(path).operator_equal(matInv.map(path)));
	}

}
