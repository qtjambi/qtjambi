/****************************************************************************
**
** Copyright (C) 2007-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $TROLLTECH_DUAL_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.phonon.*;

import java.util.*;

public class phonon extends QWidget
{

public phonon()
{
    {
//![0]
    MediaObject music =
        Phonon.createPlayer(Phonon.Category.MusicCategory,
                            new MediaSource("/path/mysong.wav"));
    music.play();
//![0]
    }

    {
    QWidget parentWidget = new QWidget();
    QUrl url = new QUrl("Myfancymusic");
//![1]
    VideoPlayer player =
        new VideoPlayer(Phonon.Category.VideoCategory, parentWidget);
    player.play(new MediaSource(url));
//![1]
    }

    {
//![2]
    MediaObject mediaObject = new MediaObject(this);
    mediaObject.setCurrentSource(new MediaSource("/mymusic/barbiegirl.wav"));
    AudioOutput audioOutput =
        new AudioOutput(Phonon.Category.MusicCategory, this);
    Path path = Phonon.createPath(mediaObject, audioOutput);
//![2]
    
//![3]
    Effect effect =
        new Effect(
            BackendCapabilities.availableAudioEffects().get(0), this);
    path.insertEffect(effect);
//![3]    
    }

    {
//![4]
    MediaObject mediaObject = new MediaObject(this);

    VideoWidget videoWidget = new VideoWidget(this);
    Phonon.createPath(mediaObject, videoWidget);

    AudioOutput audioOutput =
        new AudioOutput(Phonon.Category.VideoCategory, this);
    Phonon.createPath(mediaObject, audioOutput);
//![4]
//![5]
    mediaObject.play();
//![5]
    }
}

}
