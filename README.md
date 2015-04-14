# Qt Jambi


* [Website](http://qtjambi.org)
* [Code repository](https://github.com/qtjambi/qtjambi)
* [Documentation](http://qtjambi.org/documentation)
* [API reference](http://doc.qt.digia.com/qtjambi-4.5.2_01/com/trolltech/qt/qtjambi-examples.html)
* [Issues](http://redmine.smar.fi/projects/qtjambi/issues)

## Description

This project provides power of Qt to Java world, for both Qt itself
and for programs and libraries built with Qt.

## Features

* Qt 4.8 compliant bindings for Linux, Mac OSX and Windows
* “Stand-alone” generator for generating bindings for miscelleanous Qt programs and libraries

### Known problems

* We don’t at the moment have stable OSX maintainer, so OSX releases are lacking behind
* Qt 5 support is still WIP

## Requirements

Users:

* Java 1.5 or greater

For compiling your own packages:

* JDK
* Ant 1.8 (or greater)
* Qt 4.X, can be either self-built or distribution package, as long as it contains all development headers and modules you want and qmake
* make

Jambi basically has all the same dependencies Qt has, but many modules of Qt are in reality optional, so you could well for example
drop QtWebkit, if you don’t need it.

## Installation

Newest releases can be found at http://qtjambi.org/downloads.

If you wish to compile Jambi from sources, there is more information at [INSTALL.md](install.md).

## Contributing

### Gerrit

Preferably, we will take contributions to our [Gerrit](http://gerrit.smar.fi/#/q/project:qtjambi-community):

1. Register to [Gerrit](http://gerrit.smar.fi/#/q/project:qtjambi-community)
2. Clone the git repository
3. (only if clone is not from Gerrit) Add gerrit as remote ssh://[YOURUSER]@gerrit.smar.fi:29418/qtjambi-community
4. Do your modifications
5. git push gerrit HEAD:refs/for/master

More information about how Gerrit works can be found at their [official documentation](https://gerrit-documentation.storage.googleapis.com/Documentation/2.11/intro-quick.html).

### Github

Alternatively, you can do pull requests at Github using the standard pattern :-)

1. Fork it
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -am 'Add some feature')
4. Push to the branch (git push origin my-new-feature)
5. Create new Pull Request
