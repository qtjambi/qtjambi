#!/bin/sh
nm -DS $1 | grep " B " | awk '{print "libqt-mt "$4" "$2}' | sed 's/ 00*/ /' >missing
nm -DS $1 | grep " D " | awk '{print "libqt-mt "$4" "$2}' | sed 's/ 00*/ /' >>missing
nm -DS $1 | grep " W _ZThn" | awk '{print "libqt-mt "$4" "$2}' | sed 's/ 00*/ /' >>missing
nm -DS $1 | grep " V \(_ZTV\|_ZTI\)" | awk '{print "libqt-mt "$4" "$2}' | sed 's/ 00*/ /' >>missing
