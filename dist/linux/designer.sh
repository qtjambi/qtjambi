if [ -e binpatch ];
then
    ./binpatch
fi

me=$(dirname $0)

if ! java -version 2>&1 | grep -q "1\.[5-9]"
then
    echo "Qt Jambi requires Java version 1.5.0 or higher to be preinstalled"
    echo "to work. If Java is installed then make sure that the 'java' executable"
    echo "is available in the PATH environment."
else
    VERSION=$(ls qtjambi-4*.jar)
    VERSION=`echo $VERSION | awk '{ print substr($1, 9, 8); }'`
    CP=$me/qtjambi-$VERSION.jar:$me/qtjambi-examples-$VERSION.jar:$me/qtjambi-designer-$VERSION.jar
    LD_LIBRARY_PATH=$me/lib QT_PLUGIN_PATH=$me/plugins CLASSPATH=$CP $me/bin/designer
fi
