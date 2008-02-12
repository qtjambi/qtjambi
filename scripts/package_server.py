#!/usr/bin/python

import os
import shutil
import socket

import pkgutil


PORT = 8184

serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print "binding to " + socket.gethostname() + ":", PORT, "..."
serversocket.bind((socket.gethostname(), PORT))
print "listening..."
serversocket.listen(5)

if pkgutil.isWindows():
    rootDir = "c:/tmp/package_server"
    task = "cmd /c task.bat"
else:
    rootDir = "/tmp/package_server"
    task = "task.sh"



while 1:
    (clientsocket, (host, port) ) = serversocket.accept()
    print "got connection: %s on %s:%d" % (clientsocket, host, port)


    path = "%s/%d" % (rootDir, port)

    if os.path.isdir(path):
        shutil.rmtree(path)
        
    print "doing build in: " + path
    os.makedirs(path)
    os.chdir(path)

    zipFileName = os.path.join(path, "tmp.zip")
    pkgutil.getDataFile(clientsocket, zipFileName)
    
    pkgutil.uncompress(zipFileName, path)
    os.remove(zipFileName);
    os.system(task);

   
    resultZipFile = path + ".zip"
    pkgutil.compress(resultZipFile, path)
    pkgutil.sendDataFile(host, resultZipFile)


serversocket.close()
    

    
    
