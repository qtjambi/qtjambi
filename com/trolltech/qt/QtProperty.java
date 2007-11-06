package com.trolltech.qt;

/**
 * Information about a property in a QObject subclass. 
 */
public class QtProperty {
    private boolean writable;
    private boolean designable;
    private boolean resettable;
    private String name;
    
    /* friendly */ QtProperty(boolean writable, boolean designable, boolean resettable, String name) {
        this.writable = writable;
        this.designable = designable;
        this.resettable = resettable;
        this.name = name;
    }
    
    public final boolean isWritable() { return writable; }
    public final boolean isResettable() { return resettable; }
    public final boolean isDesignable() { return designable; }
    public final String name() { return name; }
    
    @Override
    public final String toString() {
        return name;
    }
}
