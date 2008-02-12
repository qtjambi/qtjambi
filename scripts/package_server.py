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
    (clientsocket, address) = serversocket.accept()
    print "got connection: ", address, ", ", clientsocket

    print address[0], address[1]

    path = "%s/%d" % (rootDir, address[1])
    if os.path.isdir(path):
        shutil.rmtree(path)
        
    print "doing build in: " + path
    os.makedirs(path)
    os.chdir(path)

    zipFileName = os.path.join(path, "tmp.zip")
    pkgutil.getDataFile(clientsocket, zipFileName)

    pkgutil.uncompress(zipFileName, path)
    os.system(task);

    pkgutil.compress(zipFileName, path)
    

    
    
