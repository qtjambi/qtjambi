#!/bin/sh

me=$(dirname $0)
LD_LIBRARY_PATH=$me/lib:$LD_LIBRARY_PATH $me/bin/assistant -profile $me/doc/html/qtjambi.adp &
