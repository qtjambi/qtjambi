package com.trolltech.autotests;

import com.trolltech.qt.QtJambiObject;
import com.trolltech.autotests.generated.ValueType;
import org.junit.Test;

public class TestMemoryManagementValueType extends TestMemoryManagement {

    @Override
    protected QtJambiObject createInstanceInJava() {
        return new ValueType();
    }

    @Override
    protected QtJambiObject createInstanceInNative() {
        return ValueType.fromNativePointer(ValueType.newInstance());
    }

    @Override
    protected void deleteLastInstance() {
        ValueType.deleteLastInstance();
    }

    @Override
    protected void invalidateObject(QtJambiObject obj) {
        ValueType.invalidateObject(obj.nativePointer());
    }

    @Override
    protected boolean hasShellDestructor() {
        return false;
    }

    @Override
    protected boolean hasVirtualDestructor() {
        return false;
    }

    @Override
    protected String className() {
        return "ValueType";
    }

    @Override
    protected boolean supportsSplitOwnership() {
        return false;
    }

     /*
        Required for IntelliJ to realize that the method is a junit test
        since it doesn't check super classes.
    */
    @Test
    public void dummy() {}
}
