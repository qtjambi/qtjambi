/****************************************************************************
**
** Copyright (C) 2004-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of an example program for Qt.
** EDITIONS: NOLIMITS
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

/*
main.cpp

Provides the main function for the RSS news reader example.
*/

#include <QtGui>

#include "rsslisting.h"

/*!
    Create an application and a main widget. Open the main widget for
    user input, and exit with an appropriate return value when it is
    closed.
*/

int main(int argc, char **argv)
{
    QApplication app(argc, argv);
    RSSListing *rsslisting = new RSSListing;
    rsslisting->show();
    return app.exec();
}
