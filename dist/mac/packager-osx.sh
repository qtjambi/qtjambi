#/bin/bash

if [ "${QTDIR}" == "" ]; then
    echo "Error: QTDIR is not set!"
    exit 1
fi

OS=macosx
VERSION=$(ls qtjambi-4*.jar)
VERSION=${VERSION:8:5}
RELEASE=qtjambi-$OS-community-$VERSION
COMPILER=gcc
JAMBISTUFF=build/platform-output

mkdir $RELEASE
mkdir $RELEASE/bin
mkdir $RELEASE/lib
mkdir $RELEASE/plugins

cp version.properties $RELEASE
cp qtjambi-$VERSION.jar $RELEASE
cp qtjambi-designer-$VERSION.jar $RELEASE
cp qtjambi-examples-$VERSION.jar $RELEASE
cp jars/qtjambi-examples-src.jar $RELEASE
cp jars/qtjambi-util.jar $RELEASE
cp build/qtjambi-native-$OS-$COMPILER-$VERSION.jar $RELEASE

cp dist/mac/qtjambi.sh $RELEASE
cp dist/mac/designer.sh $RELEASE
cp set_qtjambi_env.sh $RELEASE

cp dist/changes-$VERSION $RELEASE
cp dist/install.html $RELEASE
cp dist/LGPL_EXCEPTION.TXT $RELEASE
cp dist/LICENSE.GPL3 $RELEASE
cp dist/LICENSE.LGPL $RELEASE
cp dist/readme.txt $RELEASE

cp build/qmake-juic/juic $RELEASE/bin
cp -R $QTDIR/bin/Designer.app $RELEASE/bin
cp -R $QTDIR/bin/Linguist.app $RELEASE/bin
cp $QTDIR/bin/lrelease $RELEASE/bin
cp $QTDIR/bin/lupdate $RELEASE/bin

cp $JAMBISTUFF/lib/libcom_trolltech_qt_core.1.jnilib $RELEASE/lib
cp $JAMBISTUFF/lib/libcom_trolltech_qt_declarative.1.jnilib $RELEASE/lib
cp $JAMBISTUFF/lib/libcom_trolltech_qt_gui.1.jnilib $RELEASE/lib
cp $JAMBISTUFF/lib/libcom_trolltech_qt_help.1.jnilib $RELEASE/lib
cp $JAMBISTUFF/lib/libcom_trolltech_qt_multimedia.1.jnilib $RELEASE/lib
cp $JAMBISTUFF/lib/libcom_trolltech_qt_network.1.jnilib $RELEASE/lib
cp $JAMBISTUFF/lib/libcom_trolltech_qt_opengl.1.jnilib $RELEASE/lib
cp $JAMBISTUFF/lib/libcom_trolltech_qt_phonon.1.jnilib $RELEASE/lib
cp $JAMBISTUFF/lib/libcom_trolltech_qt_script.1.jnilib $RELEASE/lib
cp $JAMBISTUFF/lib/libcom_trolltech_qt_scripttools.1.jnilib $RELEASE/lib
cp $JAMBISTUFF/lib/libcom_trolltech_qt_sql.1.jnilib $RELEASE/lib
cp $JAMBISTUFF/lib/libcom_trolltech_qt_svg.1.jnilib $RELEASE/lib
cp $JAMBISTUFF/lib/libcom_trolltech_qt_test.1.jnilib $RELEASE/lib
cp $JAMBISTUFF/lib/libcom_trolltech_qt_webkit.1.jnilib $RELEASE/lib
cp $JAMBISTUFF/lib/libcom_trolltech_qt_xml.1.jnilib $RELEASE/lib
cp $JAMBISTUFF/lib/libcom_trolltech_qt_xmlpatterns.1.jnilib $RELEASE/lib
cp $JAMBISTUFF/lib/libcom_trolltech_tools_designer.1.jnilib $RELEASE/lib

cp $JAMBISTUFF/lib/libphonon.4.dylib $RELEASE/lib
cp $JAMBISTUFF/lib/libQtCLucene.4.dylib $RELEASE/lib
cp $JAMBISTUFF/lib/libQtCore.4.dylib $RELEASE/lib
cp $JAMBISTUFF/lib/libQtDeclarative.4.dylib $RELEASE/lib
cp $JAMBISTUFF/lib/libQtDesigner.4.dylib $RELEASE/lib
cp $JAMBISTUFF/lib/libQtDesignerComponents.4.dylib $RELEASE/lib
cp $JAMBISTUFF/lib/libQtGui.4.dylib $RELEASE/lib
cp $JAMBISTUFF/lib/libQtHelp.4.dylib $RELEASE/lib
cp $JAMBISTUFF/lib/libqtjambi.1.jnilib $RELEASE/lib
cp $JAMBISTUFF/lib/libQtMultimedia.4.dylib $RELEASE/lib
cp $JAMBISTUFF/lib/libQtNetwork.4.dylib $RELEASE/lib
cp $JAMBISTUFF/lib/libQtOpenGL.4.dylib $RELEASE/lib
cp $JAMBISTUFF/lib/libQtScript.4.dylib $RELEASE/lib
cp $JAMBISTUFF/lib/libQtScriptTools.4.dylib $RELEASE/lib
cp $JAMBISTUFF/lib/libQtSql.4.dylib $RELEASE/lib
cp $JAMBISTUFF/lib/libQtSvg.4.dylib $RELEASE/lib
cp $JAMBISTUFF/lib/libQtTest.4.dylib $RELEASE/lib
cp $JAMBISTUFF/lib/libQtWebKit.4.dylib $RELEASE/lib
cp $JAMBISTUFF/lib/libQtXml.4.dylib $RELEASE/lib
cp $JAMBISTUFF/lib/libQtXmlPatterns.4.dylib $RELEASE/lib

cp -R $JAMBISTUFF/lib/Resources $RELEASE/lib

cd $JAMBISTUFF/plugins/
# creates Qt plugin folders
find . -type d -execdir mkdir -p ../../../$RELEASE/plugins/{} \;

# copies the Qt plugins
find . -name "*.dylib" -exec cp {} ../../../$RELEASE/plugins/{} \;

cd ../plugins-designer
# creates plugin folders
find . -type d -execdir mkdir -p ../../../$RELEASE/plugins/{} \;

# copies the Jambi plugins
find . -name "*.dylib" -exec cp {} ../../../$RELEASE/plugins/{} \;
find . -name "*.xml" -exec cp {} ../../../$RELEASE/plugins/{} \;

cd ../../../

tar czf $RELEASE.tar.gz $RELEASE

rm -Rf $RELEASE
