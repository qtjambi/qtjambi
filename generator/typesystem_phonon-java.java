class ObjectDescriptionSubclass___ {

    /**
     * Returns a named property.
     *
     * If the property is not set an invalid value is returned. 
     * 
     * @param name The name of the property to return
     * @return The property corresponding to the name
     */                                                      
    public final java.lang.Object property(String name) {
        return property(QNativePointer.createCharPointer(name));
    }
 
}// class
