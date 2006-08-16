#!/bin/sh

me=$(dirname $0)
t=$(mktemp)

${CXX-g++} -E -nostdinc -nostdinc++ -I${QTDIR}/include $* > $t 2> /dev/null
${me}/classgraph $t

rm -f ${t}

