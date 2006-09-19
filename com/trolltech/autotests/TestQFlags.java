package com.trolltech.autotests;


import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qtest.*;

public class TestQFlags extends QTestCase {
    
    private enum MyEnum implements QtEnumerator {
        Zero,
        One,
        Two,
        Three,
        Four;        
        public int value() { return ordinal(); }
    }
    
    private static class Flags extends QFlags<MyEnum> {
        private static final long serialVersionUID = 1L;
        private Flags(MyEnum ... flags) { super(flags); }
        private Flags(Flags other) { super(other); }
    }
    
    public void run_testConstructor() {
        QCOMPARE(new Flags().value(), 0);
        
        QCOMPARE(new Flags(MyEnum.One, MyEnum.Two, MyEnum.Four).value(), 7);
        QCOMPARE(new Flags(MyEnum.Three).value(), 3);
        QCOMPARE(new Flags(MyEnum.One, MyEnum.Four).value(), 5);
        
        QCOMPARE(new Flags(new Flags(MyEnum.One, MyEnum.Four)).value(), 5);
        QCOMPARE(new Flags(new Flags(MyEnum.Four)).value(), 4);
    }
    
    private static Flags createFlags(Flags other) {
        Flags f = new Flags();
        f.set(other);
        return f;
    }
    
    private static Flags createFlags(MyEnum ... args) {
        Flags f = new Flags();
        for (MyEnum e : args)
            f.set(e);
        return f;
    }
    
    public void run_set() {
        QCOMPARE(createFlags().value(), 0);
        
        QCOMPARE(createFlags(MyEnum.One, MyEnum.Two, MyEnum.Four).value(), 7);
        QCOMPARE(createFlags(MyEnum.Three).value(), 3);
        QCOMPARE(createFlags(MyEnum.One, MyEnum.Four).value(), 5);
        
        QCOMPARE(createFlags(new Flags(MyEnum.One, MyEnum.Four)).value(), 5);
        QCOMPARE(createFlags(new Flags(MyEnum.Four)).value(), 4);
    }
    
    public void data_clear() {
        defineDataStructure(Object.class, "toSet",
                            Object.class, "toClear", 
                            Integer.class, "result");
        
        
        addDataSet("1", 
                new MyEnum[] { MyEnum.One, MyEnum.Two, MyEnum.Four },
                new MyEnum[] { MyEnum.Three },
                4);
        
        addDataSet("2", 
                new MyEnum[] { MyEnum.One, MyEnum.Two, MyEnum.Four },
                new MyEnum[] { MyEnum.Four },
                3);                
        
        addDataSet("3", 
                new MyEnum[] { MyEnum.One, MyEnum.Two, MyEnum.Four },
                new MyEnum[] { MyEnum.Four, MyEnum.One },
                2);                
    }
    
    public void run_clear() {
        MyEnum toSet[] = (MyEnum[]) getParameter("toSet");
        MyEnum toClear[] = (MyEnum[]) getParameter("toClear");
        int i = (Integer) getParameter("result");
        Flags f = new Flags(toSet);
        f.clear(toClear);        
        QCOMPARE(f.value(), i);
    }
    
    public void run_equals() {
        QCOMPARE(new Flags(MyEnum.One), new Flags(MyEnum.One));
        QCOMPARE(new Flags(MyEnum.Three, MyEnum.Four), new Flags(MyEnum.Three, MyEnum.Four));
        QVERIFY(!new Flags(MyEnum.One).equals(new Flags(MyEnum.Two)));
        
        QVERIFY(!new Flags(MyEnum.One).equals(new QFlags<MyEnum>(MyEnum.One) { private static final long serialVersionUID = 1L; }));            
    }
    

    public static void main(String[] args) {
        QCoreApplication.initialize(args);
        
        runTest(new TestQFlags());
    }
}
