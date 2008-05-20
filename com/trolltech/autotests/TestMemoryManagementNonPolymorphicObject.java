package com.trolltech.autotests;

import com.trolltech.qt.QtJambiObject;
import com.trolltech.autotests.generated.NonPolymorphicObjectType;
import com.trolltech.autotests.generated.InvalidatorNonPolymorphicObjectType;
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

    QtJambiObject temporaryObject = null;
    protected QtJambiObject invalidateObject(QtJambiObject obj, final boolean returnReference) {
        new InvalidatorNonPolymorphicObjectType() {
            @Override
            public void overrideMe(NonPolymorphicObjectType t) {
                if (returnReference) {
                    temporaryObject = t;
                    temporaryObject.setJavaOwnership();
                }
            }
        }.invalidateObject((NonPolymorphicObjectType) obj);

         QtJambiObject tmp = temporaryObject;
        temporaryObject = null;
        return tmp;
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
