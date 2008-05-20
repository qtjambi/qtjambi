package com.trolltech.autotests;

import com.trolltech.autotests.generated.PolymorphicObjectType;
import com.trolltech.qt.QtJambiObject;
import org.junit.Test;

public class TestMemoryManagementPolymorphicObject extends TestMemoryManagement {


    protected QtJambiObject createInstanceInJava() {
        return new PolymorphicObjectType();
    }

    protected QtJambiObject createInstanceInNative() {
        return PolymorphicObjectType.newInstance();
    }

    protected void deleteLastInstance() {
        PolymorphicObjectType.deleteLastInstance();
    }

    protected void invalidateObject(QtJambiObject obj) {
        PolymorphicObjectType.invalidateObject((PolymorphicObjectType) obj);
    }

    protected String className() {
        return "PolymorphicObjectType";
    }

    protected boolean hasShellDestructor() {
        return true;
    }

    @Override
    protected boolean hasVirtualDestructor() {
        return true;
    }

    /*
        Required for IntelliJ to realize that the method is a junit test
        since it doesn't check super classes.
    */
    @Test
    public void dummy() {}
}
