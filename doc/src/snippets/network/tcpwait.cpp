#include <QtGui>
#include <QTcpSocket>

#include "server.h"

int main(int argv, char **args)
{
    QCoreApplication app(argv, args);

    QTcpSocket socket;
    socket.connectToHost("localhost", 1025);
    
//! [0]
    int numRead = 0, numReadTotal = 0;
    char buffer[50];

    forever {
	numRead  = socket.read(buffer, 50);

	// do whatever with array
	
	numReadTotal += numRead;
	if (numRead == 0 && !socket.waitForReadyRead()) 
	    break;
    }
//! [0]
    
    return app.exec();
}
