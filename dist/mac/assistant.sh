#!/bin/sh

me=$(dirname $0)
DYLD_LIBRARY_PATH=$me/lib:$DYLD_LIBRARY_PATH $me/bin/assistant.app/Contents/MacOS/assistant -profile $me/doc/html/qtjambi.adp &
