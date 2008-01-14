/*   Ported from: src.3rdparty.kdelibs.phonon.objectdescriptionmodel.h
<snip>
//! [0]

     * QComboBox *cb = new QComboBox(parentWidget);
     * ObjectDescriptionModel *model = new ObjectDescriptionModel(cb);
     * model->setModelData(BackendCapabilities::availableAudioOutputDevices());
     * cb->setModel(model);
     * cb->setCurrentIndex(0); // select first entry
     * //! [0]


//! [1]

     * int cbIndex = cb->currentIndex();
     * AudioOutputDevice selectedDevice = model->modelData(cbIndex);
     * //! [1]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_3rdparty_kdelibs_phonon_objectdescriptionmodel {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]

     * QComboBox b = new QComboBox(parentWidget);
     * ObjectDescriptionModel odel = new ObjectDescriptionModel(cb);
     * model.setModelData(BackendCapabilities.availableAudioOutputDevices());
     * cb.setModel(model);
     * cb.setCurrentIndex(0); // select first entry
     * //! [0]


//! [1]

     * int cbIndex = cb.currentIndex();
     * AudioOutputDevice selectedDevice = model.modelData(cbIndex);
     * //! [1]


    }
}
