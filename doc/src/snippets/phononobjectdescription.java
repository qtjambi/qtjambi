
import com.trolltech.qt.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.phonon.*;

import java.util.*;

public class phononobjectdescription
{

public static void main(String args[])
{
    QApplication.initialize(args);
    
//![0]
    List<EffectDescription> effectDescriptions =
            BackendCapabilities.availableAudioEffects();

//![1]
    List<AudioOutputDevice> audioOutputDevices =
            BackendCapabilities.availableAudioOutputDevices();

//![1]
    for (EffectDescription effectDescription : effectDescriptions) {
        Effect effect = new Effect(effectDescription);

        // ... Do something with the effect, like insert it into a media graph
    }

    AudioOutput audioOutput = new AudioOutput();

    audioOutput.setOutputDevice(audioOutputDevices.get(0));
//![0]

    QApplication.execStatic();
    QApplication.shutdown();

}

}
