nsidpackage com.trolltech.demos;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.phonon.*;

public class MediaPlayer extends QWidget {
    
    private QPushButton pauseButton = null;
    private QPushButton playButton = null;
    private QPushButton rewindButton = null;
    private AudioOutput audioOutput = new AudioOutput(Phonon.Category.VideoCategory);
    private MediaObject mediaObject = new MediaObject();
    private QTextEdit info = null;
    private long duration;
    private QMenu fileMenu = null;    
    private SeekSlider slider = null;
    private QSlider volume = null;
    private QSlider hueSlider = null;
    private QSlider satSlider = null;
    private QSlider contSlider = null;
    
    private QWidget videoWindow = new QWidget();
    private VideoWidget videoWidget = new VideoWidget();
    private Path audioOutputPath = new Path();

    
    
    public MediaPlayer(String filePath) {
        setWindowTitle("Media Player");
        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));
        setAttribute(Qt.WidgetAttribute.WA_MacBrushedMetal);
    
        setContextMenuPolicy(Qt.ContextMenuPolicy.CustomContextMenu);
        
        QSize buttonSize = new QSize(34, 28);
    
        QPushButton openButton = new QPushButton(this);
        openButton.setMinimumSize(buttonSize);
        openButton.setIcon(style().standardIcon(QStyle.StandardPixmap.SP_DialogOpenButton));
    
        rewindButton = new QPushButton(this);
        rewindButton.setMinimumSize(buttonSize);
        rewindButton.setIcon(new QIcon(new QPixmap("classpath:com/trolltech/examples/images/rewind.png")));
    
        playButton = new QPushButton(this);
        playButton.setMinimumSize(buttonSize);
        playButton.setIcon(new QIcon(new QPixmap("classpath:com/trolltech/examples/images/play.png")));
    
        pauseButton = new QPushButton(this);
        pauseButton.setMinimumSize(buttonSize);
        pauseButton.setIcon(new QIcon(new QPixmap("classpath:com/trolltech/examples/images/pause.png")));
    
        slider = new SeekSlider(this);
        slider.setMediaObject(mediaObject);
        volume = new QSlider(Qt.Orientation.Horizontal, this);
    
        QVBoxLayout vLayout = new QVBoxLayout(this);
        QHBoxLayout layout = new QHBoxLayout();
    
        info = new QTextEdit(this);
        info.setMaximumHeight(60);
        info.setReadOnly(true);
        info.setAcceptDrops(false);
        info.setVerticalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff);
        info.setTextInteractionFlags(Qt.TextInteractionFlag.NoTextInteraction);
     
        if (System.getProperty("os.name").equals("Mac OS X")) {
            QLinearGradient bgBrush = new QLinearGradient(new QPointF(0, 0), new QPointF(0, 50));
            bgBrush.setColorAt(0, QColor.fromRgb(40, 50, 60));
            bgBrush.setColorAt(1, QColor.fromRgb(120, 130, 140));            
            QPalette palette = new QPalette();
            palette.setBrush(QPalette.ColorRole.Base, new QBrush(bgBrush));
            info.setPalette(palette);
        } else {
            info.setStyleSheet("background-color:qlinearGradient(x1:0, y1:0, x2:0, y2:1, stop:0 #335577, " +
                               "stop:1 #6688AA); color: #eeeeff");
        }
       
        info.setMinimumWidth(300);
        volume.setRange(0, 100);
        volume.setValue(100);
        volume.setMinimumWidth(40);
    
        layout.addWidget(openButton);
        layout.addWidget(rewindButton);
        layout.addWidget(playButton);
        layout.addWidget(pauseButton);
    
        QLabel volumeLabel = new QLabel(this);
        volumeLabel.setPixmap(new QPixmap("classpath:com/trolltech/examples/images/volume.png"));
        layout.addWidget(volumeLabel);
        layout.addWidget(volume);
    
        vLayout.addWidget(info);
        vLayout.addLayout(layout);
        vLayout.addWidget(slider);
    
        QHBoxLayout labelLayout = new QHBoxLayout();
    
        vLayout.addLayout(labelLayout);
        setLayout(vLayout);
    
        // Create menu bar:
        QMenuBar menubar = new QMenuBar();
        fileMenu = menubar.addMenu(tr("&File"));
        QAction settingsAction = fileMenu.addAction(tr("&Settings"));
     
        // Setup signal connections:
        rewindButton.clicked.connect(this, "rewind()");
        openButton.clicked.connect(this, "openFile()");
        pauseButton.clicked.connect(this, "pause()");
        playButton.clicked.connect(this, "play()");
        volume.valueChanged.connect(this, "setVolume(int)");
        settingsAction.triggered.connect(this, "showSettingsDialog()");

        customContextMenuRequested.connect(this, "showContextMenu(QPoint)");
        mediaObject.metaDataChanged.connect(this, "updateInfo()");
        mediaObject.totalTimeChanged.connect(this, "updateInfo()");
        mediaObject.hasVideoChanged.connect(this, "handleVideoChanged(boolean)");
        mediaObject.tick.connect(this, "updateInfo()");
        mediaObject.finished.connect(this, "finished()");
        mediaObject.stateChanged.connect(this, "stateChanged(Phonon.State, Phonon.State)");
    
        rewindButton.setEnabled(false);
        pauseButton.setEnabled(false);
        playButton.setEnabled(false);
        setAcceptDrops(true);
    
        setFixedSize(sizeHint());
        initVideoWindow();
    
        mediaObject.setTickInterval(50);
        audioOutputPath = Phonon.createPath(mediaObject, audioOutput);
        Phonon.createPath(mediaObject, videoWidget);
        if (!filePath.isEmpty())
            setFile(filePath);
    }
    
    @SuppressWarnings("unused")
    private void stateChanged(Phonon.State newstate, Phonon.State oldstate)
    {
        switch (newstate) {
            case ErrorState:
                QMessageBox.warning(this, "Phonon Mediaplayer", "Error : " + mediaObject.errorString(), QMessageBox.StandardButton.Close, QMessageBox.StandardButton.NoButton);
                if (mediaObject.errorType() == Phonon.ErrorType.FatalError) {
                    pauseButton.setEnabled(false);
                    playButton.setEnabled(false);
                    rewindButton.setEnabled(false);
                } else {
                    mediaObject.stop();
                }
                break;
            case PausedState:
            case StoppedState:
                if (mediaObject.currentSource().type() != MediaSource.Type.Invalid){
                    pauseButton.setEnabled(false);
                    playButton.setEnabled(true);
                    rewindButton.setEnabled(true);
                } else {
                    pauseButton.setEnabled(false);
                    playButton.setEnabled(false);
                    rewindButton.setEnabled(false);
                }
                break;
            case PlayingState:
            case BufferingState:
                pauseButton.setEnabled(true);
                playButton.setEnabled(false);
                rewindButton.setEnabled(true);
                break;
            case LoadingState:
                pauseButton.setEnabled(true);
                playButton.setEnabled(true);
                rewindButton.setEnabled(false);
                break;
        }
    
    }
    
    public void showSettingsDialog() {
        Ui_Dialog ui;
        QDialog dialog;
        ui.setupUi(&dialog);
        
        ui.crossFadeSlider.setValue((int)(2 * m_MediaObject.transitionTime() / 1000.0f));
        
        // Insert audio devices:
        QList<Phonon::AudioOutputDevice> devices = Phonon::BackendCapabilities::availableAudioOutputDevices();
        for (int i=0; i<devices.size(); i++){
            ui.deviceCombo.addItem(devices[i].name() + " (" + devices[i].description() + ')');
            if (devices[i] == m_AudioOutput.outputDevice())
                ui.deviceCombo.setCurrentIndex(i);
        }
    
        // Insert audio effects:
        ui.audioEffectsCombo.addItem("<no effect>");
        QList<Phonon::Effect *> currEffects = m_audioOutputPath.effects();
        Phonon::Effect *currEffect = currEffects.size() ? currEffects[0] : 0;
        QList<Phonon::EffectDescription> availableEffects = Phonon::BackendCapabilities::availableAudioEffects();
        for (int i=0; i<availableEffects.size(); i++){
            ui.audioEffectsCombo.addItem(availableEffects[i].name());
            if (currEffect && availableEffects[i] == currEffect.description())
                ui.audioEffectsCombo.setCurrentIndex(i+1);
        }
        
        dialog.exec();
        
        if (dialog.result() == QDialog::Accepted){
            m_MediaObject.setTransitionTime((int)(1000 * float(ui.crossFadeSlider.value()) / 2.0f));
            m_AudioOutput.setOutputDevice(devices[ui.deviceCombo.currentIndex()]);
        }
        
        if (ui.audioEffectsCombo.currentIndex() > 0){
            Phonon::EffectDescription chosenEffect = availableEffects[ui.audioEffectsCombo.currentIndex() - 1];
            if (!currEffect || currEffect.description() != chosenEffect){
                foreach(Phonon::Effect *effect, currEffects){
                    m_audioOutputPath.removeEffect(effect);
    //              delete effect;
                }
                m_audioOutputPath.insertEffect(chosenEffect);
            }
        } else {
            foreach(Phonon::Effect *effect, currEffects){
                m_audioOutputPath.removeEffect(effect);
    //          delete effect;
            }
        }
    }
    
    void MediaPlayer::initVideoWindow()
    {
        QVBoxLayout *videoLayout = new QVBoxLayout();
        QHBoxLayout *sliderLayout = new QHBoxLayout();
    
        QSlider *brightnessSlider = new QSlider(Qt::Horizontal);
        brightnessSlider.setRange(-SLIDER_RANGE, SLIDER_RANGE);
        brightnessSlider.setValue(0);
        connect(brightnessSlider, SIGNAL(valueChanged(int)), this, SLOT(setBrightness(int)));
    
        QSlider *hueSlider = new QSlider(Qt::Horizontal);
        hueSlider.setRange(-SLIDER_RANGE, SLIDER_RANGE);
        hueSlider.setValue(0);
        connect(hueSlider, SIGNAL(valueChanged(int)), this, SLOT(setHue(int)));
    
        QSlider *saturationSlider = new QSlider(Qt::Horizontal);
        saturationSlider.setRange(-SLIDER_RANGE, SLIDER_RANGE);
        saturationSlider.setValue(0);
        connect(saturationSlider, SIGNAL(valueChanged(int)), this, SLOT(setSaturation(int)));
    
        QSlider *contrastSlider = new QSlider(Qt::Horizontal);
        contrastSlider.setRange(-SLIDER_RANGE, SLIDER_RANGE);
        contrastSlider.setValue(0);
        connect(contrastSlider , SIGNAL(valueChanged(int)), this, SLOT(setContrast(int)));
    
        sliderLayout.addWidget(new QLabel("bright"));
        sliderLayout.addWidget(brightnessSlider);
        sliderLayout.addWidget(new QLabel("col"));
        sliderLayout.addWidget(hueSlider);
        sliderLayout.addWidget(new QLabel("sat"));
        sliderLayout.addWidget(saturationSlider);
        sliderLayout.addWidget(new QLabel("cont"));
        sliderLayout.addWidget(contrastSlider);
    
        videoLayout.addWidget(&m_videoWidget);
        videoLayout.addLayout(sliderLayout);
        m_videoWindow.setLayout(videoLayout);
        m_videoWindow.setWindowTitle("Video");
        m_videoWindow.setAttribute(Qt::WA_QuitOnClose, false);
        m_videoWindow.setAttribute(Qt::WA_MacBrushedMetal);
        m_videoWindow.setMinimumSize(100, 100);
    
    }
    
    void MediaPlayer::handleVideoChanged(bool hasVideo)
    {
        if (hasVideo){
            QDesktopWidget desktop;
            QRect videoHintRect = QRect(QPoint(0, 0), m_videoWindow.sizeHint());
            QRect newVideoRect = desktop.screenGeometry().intersected(videoHintRect);
            m_videoWindow.resize(newVideoRect.size());
        }
        m_videoWindow.setVisible(hasVideo);
    }
    
    void MediaPlayer::pause()
    {
        m_MediaObject.pause();
    }
    
    void MediaPlayer::dropEvent(QDropEvent *e)
    {
        if (e.mimeData().hasUrls())
            e.acceptProposedAction();
    
        QList<QUrl> urls = e.mimeData().urls();
    
        if (e.keyboardModifiers() & Qt::ShiftModifier){
            // Just add to the que:
            for (int i=0; i<urls.size(); i++)
                m_MediaObject.enqueue(Phonon::MediaSource(urls[i].toLocalFile()));
        } else {
            // Create new que:
            m_MediaObject.clearQueue();
            QString fileName = urls[0].toLocalFile();
            setFile(fileName);
            for (int i=1; i<urls.size(); i++)
                m_MediaObject.enqueue(Phonon::MediaSource(urls[i].toLocalFile()));
        }
        play();
    }
    
    void MediaPlayer::dragEnterEvent(QDragEnterEvent *e)
    {
        if (e.mimeData().hasUrls())
            e.acceptProposedAction();
    }
    
    void MediaPlayer::play()
    {
        m_MediaObject.play();
    }
    
    void MediaPlayer::setVolume(int volume)
    {
        Q_UNUSED(volume);
        m_AudioOutput.setVolume(volume/100.0f);
    }
    
    void MediaPlayer::setFile(const QString &fileName)
    {
        if (fileName.contains("://"))
            m_MediaObject.setCurrentSource(Phonon::MediaSource(QUrl(fileName)));
        else
            m_MediaObject.setCurrentSource(Phonon::MediaSource(fileName));
    }
    
    void MediaPlayer::openFile()
    {
        QString fileName = QFileDialog::getOpenFileName();
        if (!fileName.isEmpty())
            setFile(fileName);
    }
    
    void MediaPlayer::setSaturation(int val)
    {
        m_videoWidget.setSaturation(val / float(SLIDER_RANGE));
    }
    
    void MediaPlayer::setHue(int val)
    {
        m_videoWidget.setHue(val / float(SLIDER_RANGE));
    }
    
    void MediaPlayer::setBrightness(int val)
    {
        m_videoWidget.setBrightness(val / float(SLIDER_RANGE));
    }
    
    void MediaPlayer::setContrast(int val)
    {
        m_videoWidget.setContrast(val / float(SLIDER_RANGE));
    }
    
    void MediaPlayer::updateInfo()
    {
        long len = m_MediaObject.totalTime();
        long pos = m_MediaObject.currentTime();
    
        QString font = "<font color=#ffffd0>";
        QString fontmono = "<font family=\"monospace,courier new\" color=#ffffd0>";
    
        QMap <QString, QString> metaData = m_MediaObject.metaData();
        QString trackArtist = metaData.value("ARTIST");
        QString trackTitle = metaData.value("TITLE");
        QString timeString, time;
        if (pos || len)
        {
            int sec = pos/1000;
            int min = sec/60;
            int hour = min/60;
            int msec = pos;
    
            QTime playTime(hour%60, min%60, sec%60, msec%1000);
            sec = len / 1000;
            min = sec / 60;
            hour = min / 60;
            msec = len;
    
            QTime stopTime(hour%60, min%60, sec%60, msec%1000);
            timeString = playTime.toString("hh:mm:ss:zzz") + "</font>";
            if (len)
                timeString += "&nbsp; Duration: " + fontmono + stopTime.toString("hh:mm:ss:zzz") + "</font>";
            time =   "Time: " + font + timeString + "</font>";
        }
    
        QString fileName = m_MediaObject.currentSource().fileName();
        fileName = fileName.right(fileName.length() - fileName.lastIndexOf('/') - 1);
    
        QString title;    
        if (!trackTitle.isEmpty())
            title = "Title: " + font + trackTitle + "<br></font>";
        else if (!fileName.isEmpty())
            title = "File: " + font + fileName + "<br></font>";
            
        QString artist;
        if (!trackArtist.isEmpty())
            artist = "Artist:  " + font + trackArtist + "<br></font>";
        info.setHtml(title + artist + time);
    
    }
    
    void MediaPlayer::rewind()
    {
        m_MediaObject.setTickInterval(50);
        m_MediaObject.seek(0);
        updateInfo();
    }
    
    void MediaPlayer::finished()
    {
        updateInfo();
    }
    
    void MediaPlayer::showContextMenu(const QPoint &p)
    {
        fileMenu.popup(mapToGlobal(p));
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        QApplication.initialize(args);
        
        QApplication.setApplicationName("Media Player");
        QApplication.setQuitOnLastWindowClosed(true);
        
        String fileString = QApplication.arguments().value(1);
        
        MediaPlayer player = new MediaPlayer(fileString);
        player.show();

        return QApplication.exec();

    }

}
