package com.trolltech.autotests;

import org.junit.Test;

import com.trolltech.autotests.generated.OrdinaryDestroyed;

import static org.junit.Assert.*;

class MyOrdinaryDestroyed extends OrdinaryDestroyed
{

    static int disposedCount = 0;
    
    @Override
    public OrdinaryDestroyed virtualGetObjectCppOwnership() {
        return new MyOrdinaryDestroyed();
    }

    @Override
    public OrdinaryDestroyed virtualGetObjectJavaOwnership() {
        return new MyOrdinaryDestroyed();
    }

    @Override
    public void virtualSetDefaultOwnership(OrdinaryDestroyed arg__1) {
        // nothing
    }

    @Override
    protected void disposed() {
        disposedCount++;
        
        super.disposed();
    }
    
}


public class TestDestruction extends QApplicationTest {
    @Test
    public void testJavaCreationJavaOwnership() 
    {
        MyOrdinaryDestroyed.disposedCount = 0;
        MyOrdinaryDestroyed.setDestroyedCount(0);
        
        {
            MyOrdinaryDestroyed d = new MyOrdinaryDestroyed();
        }
        
        try {
            Thread.currentThread().wait(5000);
        } catch (Exception e) {
            // exceptions are an idiotic concept
        }
        System.gc();
        
        assertEquals(1, MyOrdinaryDestroyed.disposedCount);
        assertEquals(1, MyOrdinaryDestroyed.destroyedCount());
    }
    
    @Test
    public void testJavaCreationCppOwnership() 
    {
        
    }
}
