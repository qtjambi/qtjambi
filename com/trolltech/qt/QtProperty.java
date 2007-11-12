package com.trolltech.qt;

/**
 * Information about a property in a QObject subclass. 
 */
public class QtProperty {
    private final boolean writable;
    private final boolean designable;
    private final boolean resettable;
    private final boolean user;
    private final String name;
    
    /* friendly */ QtProperty(boolean writable, boolean designable, boolean resettable, boolean user, String name) {
        this.writable = writable;
        this.designable = designable;
        this.resettable = resettable;
        this.user = user;
        this.name = name;
    }
    
    public final boolean isWritable() { return writable; }
    public final boolean isResettable() { return resettable; }
    public final boolean isDesignable() { return designable; }
    public final boolean isUser() { return user; }
    public final String name() { return name; }
    
    @Override
    public final String toString() {
        return name;
    }
}
