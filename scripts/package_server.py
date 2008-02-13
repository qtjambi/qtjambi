#!/usr/bin/python

import os
import shutil
import socket
import time
import threading

import pkgutil


PORT = 8184

serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print "binding to " + socket.gethostname() + ":", PORT, "..."
serversocket.bind((socket.gethostname(), PORT))
print "listening..."
serversocket.listen(5)

if pkgutil.isWindows():
    rootDir = "c:/tmp/package_server"
    task = "cmd /c task.bat > .task.log"
else:
    rootDir = "/tmp/package_server"
    task = "task.sh > .task.log"

pendingTasks = []
lock = threading.Event()

class SocketListener(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)

    def run(self):
        while 1:
            
            (clientsocket, (host, port) ) = serversocket.accept()
    print "got connection: %s on %s:%d" % (clientsocket, host, port)


    path = "%s/%d" % (rootDir, port)

    if os.path.isdir(path):
        shutil.rmtree(path)
        
    cmd = clientsocket.read(1)
    if cmd == pkgutil.CMD_RESET:
        pendingTasks = []
        
    elif cmd == pkgutil.CMD_NEWPKG:
        print "doing build in: " + path
        os.makedirs(path)


            

while 1:

        zipFileName = os.path.join(path, "tmp.zip")
        pkgutil.getDataFile(clientsocket, zipFileName)
        pkgutil.uncompress(zipFileName, path)
        os.remove(zipFileName);
        
        taskDef = (task, path, host)
        pendingTasks.append(taskDef)
        lock.set()

    
def runTask(taskDef):
    (task, path, host) = taskDef

    os.chdir(task);
    os.system(task);

    resultZipFile = path + ".zip"
    pkgutil.compress(resultZipFile, path)
    pkgutil.sendDataFile(host, resultZipFile, pkgutil.PORT_CREATOR)


serversocket.close()
    

    
    
