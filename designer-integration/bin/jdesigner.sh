#/bin/sh

me=$(dirname $0)
CLASSPATH=$me/../..:$CLASSPATH QT_PLUGIN_PATH=$me/../plugins LD_LIBRARY_PATH=$me/../../lib designer $*
