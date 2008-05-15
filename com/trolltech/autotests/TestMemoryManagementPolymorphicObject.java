package com.trolltech.autotests;

import com.trolltech.autotests.generated.PolymorphicObjectType;
import com.trolltech.qt.QtJambiObject;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: eblomfel
 * Date: 15.mai.2008
 * Time: 09:43:08
 * To change this template use File | Settings | File Templates.
 */
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

    /*
        Required for IntelliJ to realize that the method is a junit test
        since it doesn't check super classes.
    */
    @Test
    public void dummy() {}
}
