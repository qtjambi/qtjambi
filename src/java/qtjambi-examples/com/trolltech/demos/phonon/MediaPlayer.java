/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** ** $BEGIN_LICENSE$
** Commercial Usage
** Licensees holding valid Qt Commercial licenses may use this file in
** accordance with the Qt Commercial License Agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Nokia.
** 
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
** 
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
** 
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
** 
** If you are unsure which license is appropriate for your use, please
** contact the sales department at qt-sales@nokia.com.
** $END_LICENSE$

**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.demos.phonon;

import java.util.*;

import com.trolltech.demos.phonon.mediaplayer.*;
import com.trolltech.examples.QtJambiExample;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.phonon.*;

@QtJambiExample(name="Media Player")
public class MediaPlayer extends QWidget {

    private static final int SLIDER_RANGE = 5;

    private QPushButton pauseButton = null;
    private QPushButton playButton = null;
    private QPushButton rewindButton = null;
    private AudioOutput audioOutput = new AudioOutput(Phonon.Category.VideoCategory);
    private MediaObject mediaObject = new MediaObject();
    private QTextEdit info = null;
    private QMenu fileMenu = null;
    private SeekSlider slider = null;
    private QSlider volume = null;

    private QWidget videoWindow = new QWidget();
    private VideoWidget videoWidget = new VideoWidget();
    private Path audioOutputPath = new Path();

    public MediaPlayer() {
        this("");
    }

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
        mediaObject.stateChanged.connect(this, "stateChanged(Phonon$State, Phonon$State)");

        rewindButton.setEnabled(false);
        pauseButton.setEnabled(false);
        playButton.setEnabled(false);
        setAcceptDrops(true);

        setFixedSize(sizeHint());
        initVideoWindow();

        mediaObject.setTickInterval(50);
        audioOutputPath = Phonon.createPath(mediaObject, audioOutput);
        Phonon.createPath(mediaObject, videoWidget);
        if (filePath.length() > 0)
            setFile(filePath);
    }

    protected void closeEvent(QCloseEvent e) {
        videoWindow.close();
        mediaObject.stop();
    }


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

    private void showSettingsDialog() {
        Ui_Dialog ui = new Ui_Dialog();
        QDialog dialog = new QDialog();
        ui.setupUi(dialog);

        ui.crossFadeSlider.setValue((int)(2 * mediaObject.transitionTime() / 1000.0f));

        // Insert audio devices:
        List<AudioOutputDevice> devices = BackendCapabilities.availableAudioOutputDevices();
        for (int i=0; i<devices.size(); i++){
            ui.deviceCombo.addItem(devices.get(i).name() + " (" + devices.get(i).description() + ')');
            if (devices.get(i) == audioOutput.outputDevice())
                ui.deviceCombo.setCurrentIndex(i);
        }

        // Insert audio effects:
        ui.audioEffectsCombo.addItem("<no effect>");
        List<Effect> currEffects = audioOutputPath.effects();
        Effect currEffect = currEffects.size() > 0 ? currEffects.get(0) : null;
        List<EffectDescription> availableEffects = BackendCapabilities.availableAudioEffects();
        for (int i=0; i<availableEffects.size(); i++){
            ui.audioEffectsCombo.addItem(availableEffects.get(i).name());
            if (currEffect != null && availableEffects.get(i).equals(currEffect.description()))
                ui.audioEffectsCombo.setCurrentIndex(i+1);
        }

        dialog.exec();

        if (dialog.result() == QDialog.DialogCode.Accepted.value()){
            mediaObject.setTransitionTime((int)(1000 * (float)ui.crossFadeSlider.value() / 2.0f));
            audioOutput.setOutputDevice(devices.get(ui.deviceCombo.currentIndex()));
        }

        if (ui.audioEffectsCombo.currentIndex() > 0){
            EffectDescription chosenEffect = availableEffects.get(ui.audioEffectsCombo.currentIndex() - 1);
            if (currEffect == null || !currEffect.description().equals(chosenEffect)){
                for (Effect effect : currEffects)
                    audioOutputPath.removeEffect(effect);
                audioOutputPath.insertEffect(chosenEffect);
            }
        } else {
            for (Effect effect : currEffects)
                audioOutputPath.removeEffect(effect);
        }
    }

    private void initVideoWindow() {
        QVBoxLayout videoLayout = new QVBoxLayout();
        QHBoxLayout sliderLayout = new QHBoxLayout();

        QSlider brightnessSlider = new QSlider(Qt.Orientation.Horizontal);
        brightnessSlider.setRange(-SLIDER_RANGE, SLIDER_RANGE);
        brightnessSlider.setValue(0);
        brightnessSlider.valueChanged.connect(this, "setBrightness(int)");

        QSlider hueSlider = new QSlider(Qt.Orientation.Horizontal);
        hueSlider.setRange(-SLIDER_RANGE, SLIDER_RANGE);
        hueSlider.setValue(0);
        hueSlider.valueChanged.connect(this, "setHue(int)");

        QSlider saturationSlider = new QSlider(Qt.Orientation.Horizontal);
        saturationSlider.setRange(-SLIDER_RANGE, SLIDER_RANGE);
        saturationSlider.setValue(0);
        saturationSlider.valueChanged.connect(this, "setSaturation(int)");

        QSlider contrastSlider = new QSlider(Qt.Orientation.Horizontal);
        contrastSlider.setRange(-SLIDER_RANGE, SLIDER_RANGE);
        contrastSlider.setValue(0);
        contrastSlider.valueChanged.connect(this, "setContrast(int)");

        sliderLayout.addWidget(new QLabel("bright"));
        sliderLayout.addWidget(brightnessSlider);
        sliderLayout.addWidget(new QLabel("col"));
        sliderLayout.addWidget(hueSlider);
        sliderLayout.addWidget(new QLabel("sat"));
        sliderLayout.addWidget(saturationSlider);
        sliderLayout.addWidget(new QLabel("cont"));
        sliderLayout.addWidget(contrastSlider);

        videoLayout.addWidget(videoWidget);
        videoLayout.addLayout(sliderLayout);
        videoWindow.setLayout(videoLayout);
        videoWindow.setWindowTitle("Video");
        videoWindow.setAttribute(Qt.WidgetAttribute.WA_QuitOnClose, false);
        videoWindow.setAttribute(Qt.WidgetAttribute.WA_MacBrushedMetal);
        videoWindow.setMinimumSize(100, 100);

    }

    private void handleVideoChanged(boolean hasVideo) {
        if (hasVideo){
            QDesktopWidget desktop = new QDesktopWidget();
            QRect videoHintRect = new QRect(new QPoint(0, 0), videoWindow.sizeHint());
            QRect newVideoRect = desktop.screenGeometry().intersected(videoHintRect);
            videoWindow.resize(newVideoRect.size());
        }
        videoWindow.setVisible(hasVideo);
    }

    private void pause() {
        mediaObject.pause();
    }

    @Override
    protected void dropEvent(QDropEvent e) {
        if (e.mimeData().hasUrls())
            e.acceptProposedAction();

        List<QUrl> urls = e.mimeData().urls();

        if (e.keyboardModifiers().isSet(Qt.KeyboardModifier.ShiftModifier)){
            // Just add to the que:
            for (int i=0; i<urls.size(); i++)
                mediaObject.enqueue(new MediaSource(urls.get(i).toLocalFile()));
        } else {
            // Create new que:
            mediaObject.clearQueue();
            String fileName = urls.get(0).toLocalFile();
            setFile(fileName);
            for (int i=1; i<urls.size(); i++)
                mediaObject.enqueue(new MediaSource(urls.get(i).toLocalFile()));
        }

        play();
    }

    @Override
    protected void dragEnterEvent(QDragEnterEvent e) {
        if (e.mimeData().hasUrls())
            e.acceptProposedAction();
    }

    private void play() {
        mediaObject.play();
    }

    private void setVolume(int volume) {
        audioOutput.setVolume(volume/100.0f);
    }

    private void setFile(String fileName) {
        if (fileName.contains("://"))
            mediaObject.setCurrentSource(new MediaSource(new QUrl(fileName)));
        else
            mediaObject.setCurrentSource(new MediaSource(fileName));
    }

    private void openFile() {
        String fileName = QFileDialog.getOpenFileName();
        if (fileName.length() > 0)
            setFile(fileName);
    }

    private void setSaturation(int val) {
        videoWidget.setSaturation(val / (float)SLIDER_RANGE);
    }

    private void setHue(int val) {
        videoWidget.setHue(val / (float)SLIDER_RANGE);
    }

    private void setBrightness(int val) {
        videoWidget.setBrightness(val / (float)SLIDER_RANGE);
    }

    private void setContrast(int val) {
        videoWidget.setContrast(val / (float)SLIDER_RANGE);
    }

    private void updateInfo()
    {
        long len = mediaObject.totalTime();
        long pos = mediaObject.currentTime();

        String font = "<font color=#ffffd0>";
        String fontmono = "<font family=\"monospace,courier new\" color=#ffffd0>";

        Map <String, List<String>> metaData = mediaObject.metaData();

        List<String> trackArtists = metaData.get("ARTIST");
        List<String> trackTitles = metaData.get("TITLE");

        String trackArtist = trackArtists != null ? trackArtists.get(0) : "";
        String trackTitle = trackTitles != null ? trackTitles.get(0) : "";
        String timeString = "", time = "";
        if (pos != 0 || len != 0)
        {
            long sec = pos/1000;
            long min = sec/60;
            long hour = min/60;
            long msec = pos;

            QTime playTime = new QTime((int) hour%60, (int) min%60, (int) sec%60, (int) msec%1000);
            sec = len / 1000;
            min = sec / 60;
            hour = min / 60;
            msec = len;

            QTime stopTime = new QTime((int) hour%60, (int) min%60, (int) sec%60, (int) msec%1000);
            timeString = playTime.toString("hh:mm:ss:zzz") + "</font>";
            if (len != 0)
                timeString += "&nbsp; Duration: " + fontmono + stopTime.toString("hh:mm:ss:zzz") + "</font>";
            time =   "Time: " + font + timeString + "</font>";
        }

        String fileName = mediaObject.currentSource().fileName();
        fileName = fileName.substring(fileName.length() - fileName.lastIndexOf('/') - 1);

        String title = "";
        if (trackTitle.length() > 0)
            title = "Title: " + font + trackTitle + "<br></font>";
        else if (fileName.length() > 0)
            title = "File: " + font + fileName + "<br></font>";

        String artist = "";
        if (trackArtist.length() > 0)
            artist = "Artist:  " + font + trackArtist + "<br></font>";
        info.setHtml(title + artist + time);
    }

    private void rewind() {
        mediaObject.setTickInterval(50);
        mediaObject.seek(0);
        updateInfo();
    }

    private void finished() {
        updateInfo();
    }

    private void showContextMenu(QPoint p) {
        fileMenu.popup(mapToGlobal(p));
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        QApplication.initialize(args);

        QApplication.setApplicationName("Media Player");
        QApplication.setQuitOnLastWindowClosed(true);

        String fileString = QApplication.arguments().size() > 1 ? QApplication.arguments().get(1) : "";

        MediaPlayer player = new MediaPlayer(fileString);
        player.show();

        QApplication.execStatic();
        QApplication.shutdown();
    }

}
