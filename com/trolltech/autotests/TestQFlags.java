package com.trolltech.autotests;

import java.util.*;

import com.trolltech.qt.*;

import static org.junit.Assert.*;

import org.junit.*;

public class TestQFlags extends QApplicationTest {

    private enum MyEnum implements QtEnumerator {
        Zero, One, Two, Three, Four;
        public int value() {
            return ordinal();
        }
    }

    private static class Flags extends QFlags<MyEnum> {
        private static final long serialVersionUID = 1L;

        private Flags(MyEnum... flags) {
            super(flags);
        }

        private Flags(Flags other) {
            super(other);
        }
    }

    @Test
    public void run_testConstructor() {
        assertEquals(new Flags().value(), 0);

        assertEquals(new Flags(MyEnum.One, MyEnum.Two, MyEnum.Four).value(), 7);
        assertEquals(new Flags(MyEnum.Three).value(), 3);
        assertEquals(new Flags(MyEnum.One, MyEnum.Four).value(), 5);

        assertEquals(new Flags(new Flags(MyEnum.One, MyEnum.Four)).value(), 5);
        assertEquals(new Flags(new Flags(MyEnum.Four)).value(), 4);
    }

    private static Flags createFlags(Flags other) {
        Flags f = new Flags();
        f.set(other);
        return f;
    }

    private static Flags createFlags(MyEnum... args) {
        Flags f = new Flags();
        for (MyEnum e : args)
            f.set(e);
        return f;
    }

    @Test
    public void run_set() {
        assertEquals(createFlags().value(), 0);

        assertEquals(createFlags(MyEnum.One, MyEnum.Two, MyEnum.Four).value(), 7);
        assertEquals(createFlags(MyEnum.Three).value(), 3);
        assertEquals(createFlags(MyEnum.One, MyEnum.Four).value(), 5);

        assertEquals(createFlags(new Flags(MyEnum.One, MyEnum.Four)).value(), 5);
        assertEquals(createFlags(new Flags(MyEnum.Four)).value(), 4);
    }

    private class DataClear {
        public DataClear(MyEnum[] toSet, MyEnum[] toClear, int result) {
            this.toSet = toSet;
            this.toClear = toClear;
            this.result = result;
        }

        public MyEnum[] toSet;
        public MyEnum[] toClear;
        public int result;
    }

    private Collection<DataClear> data;

    @Before
    public void setUp() {
        data = new Vector<DataClear>();
        data.add(new DataClear(new MyEnum[] { MyEnum.One, MyEnum.Two, MyEnum.Four }, new MyEnum[] { MyEnum.Three }, 4));
        data.add(new DataClear(new MyEnum[] { MyEnum.One, MyEnum.Two, MyEnum.Four }, new MyEnum[] { MyEnum.Four }, 3));
        data.add(new DataClear(new MyEnum[] { MyEnum.One, MyEnum.Two, MyEnum.Four }, new MyEnum[] { MyEnum.Four, MyEnum.One }, 2));
    }

    @Test
    public void run_clear() {
        for (Iterator iter = data.iterator(); iter.hasNext();) {
            DataClear d = (DataClear) iter.next();

            Flags f = new Flags(d.toSet);
            f.clear(d.toClear);
            assertEquals(f.value(), d.result);
        }
    }

    @Test
    public void run_equals() {
        assertEquals(new Flags(MyEnum.One), new Flags(MyEnum.One));
        assertEquals(new Flags(MyEnum.Three, MyEnum.Four), new Flags(MyEnum.Three, MyEnum.Four));
        assertTrue(!new Flags(MyEnum.One).equals(new Flags(MyEnum.Two)));

        assertTrue(!new Flags(MyEnum.One).equals(new QFlags<MyEnum>(MyEnum.One) {
            private static final long serialVersionUID = 1L;
        }));
    }
}
