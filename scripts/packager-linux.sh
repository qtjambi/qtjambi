#/bin/bash

OS=linux32
VERSION=`cat version.properties | cut -d= -f2 | tr -d ' '`
RELEASE=qtjambi-$OS-community-$VERSION
COMPILER=gcc

mkdir $RELEASE
mkdir $RELEASE/bin
mkdir $RELEASE/lib

cp qtjambi-$VERSION.jar $RELEASE
cp qtjambi-designer-$VERSION.jar $RELEASE
cp qtjambi-examples-$VERSION.jar $RELEASE
cp qtjambi-util-$VERSION.jar $RELEASE
cp qtjambi-$OS-$COMPILER-$VERSION.jar $RELEASE
    
cp dist/linux/qtjambi.sh $RELEASE
cp dist/linux/designer.sh $RELEASE
cp set_qtjambi_env.sh $RELEASE

cp dist/changes-$VERSION $RELEASE 
cp dist/install.html $RELEASE
cp dist/LICENSE.GPL3 $RELEASE
cp dist/LICENSE.LGPL $RELEASE
cp dist/readme.html $RELEASE
  
cp -R java/src/qtjambi-examples/com $RELEASE
  
cp bin/juic $RELEASE/bin
cp $QTDIR/bin/lrelease $RELEASE/bin
cp $QTDIR/bin/lupdate $RELEASE/bin
cp $QTDIR/bin/designer $RELEASE/bin

cp lib/libqtjambi.so $RELEASE/lib
cp lib/libqtjambi.so.1 $RELEASE/lib
cp $QTDIR/lib/libphonon.so.4 $RELEASE/lib
cp $QTDIR/lib/libQtCore.so.4 $RELEASE/lib
cp $QTDIR/lib/libQtGui.so.4 $RELEASE/lib
cp $QTDIR/lib/libQtNetwork.so.4 $RELEASE/lib
cp $QTDIR/lib/libQtOpenGL.so.4 $RELEASE/lib
cp $QTDIR/lib/libQtSql.so.4 $RELEASE/lib
cp $QTDIR/lib/libQtScript.so.4 $RELEASE/lib
cp $QTDIR/lib/libQtSvg.so.4 $RELEASE/lib
cp $QTDIR/lib/libQtWebKit.so.4 $RELEASE/lib
cp $QTDIR/lib/libQtXml.so.4 $RELEASE/lib
cp $QTDIR/lib/libQtXmlPatterns.so.4 $RELEASE/lib
cp $QTDIR/lib/libQtDesignerComponents.so.4 $RELEASE/lib
cp $QTDIR/lib/libQtDesigner.so.4 $RELEASE/lib
  
cp lib/libcom_trolltech_qt_core.so $RELEASE/lib
cp lib/libcom_trolltech_qt_gui.so $RELEASE/lib
cp lib/libcom_trolltech_qt_network.so $RELEASE/lib
cp lib/libcom_trolltech_qt_opengl.so $RELEASE/lib
cp lib/libcom_trolltech_qt_phonon.so $RELEASE/lib
cp lib/libcom_trolltech_qt_sql.so $RELEASE/lib
cp lib/libcom_trolltech_qt_svg.so $RELEASE/lib
cp lib/libcom_trolltech_qt_webkit.so $RELEASE/lib
cp lib/libcom_trolltech_qt_xml.so $RELEASE/lib
cp lib/libcom_trolltech_qt_xmlpatterns.so $RELEASE/lib
cp lib/libcom_trolltech_tools_designer.so $RELEASE/lib

  
cp -R $QTDIR/plugins $RELEASE
cp -R /plugin/designer/ $RELEASE/plugins
  

tar czf $RELEASE.tar.gz $RELEASE

rm -Rf $RELEASE
