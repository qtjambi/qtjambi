#!/bin/bash

source env_releases

bash set_version.sh

# mavenize qtjambi libraries
bash mavenize_qtjambi.sh

# release qtjambi maven plugin
bash import_binaries.sh
mvn ${MAVEN_ACTION}
bash clean_binaries.sh

# prepare maven repo for upload
rm -rf tmp
mkdir -p tmp/maven2/net/sf/qtjambi/
cp -a ~/.m2/repository/net/sf/qtjambi/* tmp/maven2/net/sf/qtjambi/
echo "Options +Indexes" > tmp/maven2/.htaccess
cd tmp

# upload maven repo to sf.net
read -p "sf.net username: " uname
stty -echo
read -p "password: " passw; echo 
stty echo

scp -r maven2 ${uname},qtjambi@web.sourceforge.net://home/groups/q/qt/qtjambi/htdocs

# repo is at http://qtjambi.sourceforge.net/maven2/
