package com.trolltech.autotests;

import com.trolltech.qt.core.QObject;

class Accessor extends QObject
{    
    @SuppressWarnings("unchecked") 
    public static void emit_signal(AbstractSignal signal, Object ... args)
    {        
        if (signal instanceof Signal0)
            ((Signal0) signal).emit();
        else if (signal instanceof Signal1)
            ((Signal1) signal).emit(args[0]);
        else
            throw new RuntimeException("Implement more classes");
    }
}
