package com.trolltech.autotests;

import com.trolltech.qt.QtJambiObject;
import com.trolltech.autotests.generated.NonPolymorphicObjectType;
import org.junit.Test;

public class TestMemoryManagementNonPolymorphicObject extends TestMemoryManagement {
    protected QtJambiObject createInstanceInJava() {
        return new NonPolymorphicObjectType();
    }

    protected QtJambiObject createInstanceInNative() {
        return NonPolymorphicObjectType.newInstance();
    }

    protected void deleteLastInstance() {
        NonPolymorphicObjectType.deleteLastInstance();
    }

    protected void invalidateObject(QtJambiObject obj) {
        NonPolymorphicObjectType.invalidateObject((NonPolymorphicObjectType) obj);
    }

    protected String className() {
        return "NonPolymorphicObjectType";
    }

    protected boolean hasShellDestructor() {
        return false;
    }

    @Override
    protected boolean hasVirtualDestructor() {
        return false;
    }    

    /*
        Required for IntelliJ to realize that the method is a junit test
        since it doesn't check super classes.
    */
    @Test
    public void dummy() {}
    
}
