package com.trolltech.autotests;

import com.trolltech.autotests.generated.PolymorphicObjectType;
import com.trolltech.autotests.generated.InvalidatorPolymorphicObjectType;
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

    QtJambiObject temporaryObject = null;
    protected QtJambiObject invalidateObject(QtJambiObject obj, final boolean returnReference) {

        new InvalidatorPolymorphicObjectType() {
            @Override
            public void overrideMe(PolymorphicObjectType t) {
                if (returnReference) {
                    temporaryObject = t;
                    temporaryObject.setJavaOwnership();
                }
            }
        }.invalidateObject((PolymorphicObjectType) obj);

        QtJambiObject tmp = temporaryObject;
        temporaryObject = null;
        return tmp;
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
