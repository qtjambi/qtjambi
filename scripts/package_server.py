#!/usr/bin/python

import os
import shutil
import socket
import time
import threading

import pkgutil

serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print "binding to " + socket.gethostname() + ":", pkgutil.PORT_SERVER, "..."
serversocket.bind(('localhost', pkgutil.PORT_SERVER))
print "listening..."
serversocket.listen(5)

if pkgutil.isWindows():
    rootDir = "c:/tmp/package_server"
    task = "cmd /c task.bat > .task.log 2>&1"
elif pkgutil.isLinux():
    rootDir = "/tmp/package_server"
    task = "sh task.sh > .task.log"
else:
    rootDir = "/tmp/package_server"
    task = ". task.sh > .task.log"

pendingTasks = []
waitCondition = threading.Condition()
startDir = os.getcwd()



class SocketListener(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)
        self.setDaemon(True)

    def run(self):
        while 1:
            print "listener: waiting for socket accept..."
            (clientsocket, (host, port) ) = serversocket.accept()
            print "listener: got connection: %s on %s:%d" % (clientsocket, host, port)
            
            path = "%s/%d" % (rootDir, port)

            if os.path.isdir(path):
                shutil.rmtree(path)

            os.makedirs(path)
        
            zipFileName = os.path.join(path, "tmp.zip")
            pkgutil.getDataFile(clientsocket, zipFileName)
            print "listener: uncompressing %s from %s" % (path, zipFileName)
            pkgutil.uncompress(zipFileName, path)
            os.remove(zipFileName)
            
            taskDef = (task, path, host)
            print "listener: aquiring lock for task push"
            waitCondition.acquire()
            pendingTasks.append(taskDef)
            print "listener: aquiring lock for notify/release after task push"
            waitCondition.notify()
            waitCondition.release()



def runTask(taskDef):
    (task, path, host) = taskDef
    
    print "runTask:\n - command='%s'\n - directory='%s'\n - host='%s'" % taskDef

    os.chdir(path)
    
    exitCode = os.system(task)
    
    if exitCode:
        fh = open(os.path.join(path, "FATAL.ERROR"), "w")
        fh.write("Exit code: %d\n" % exitCode)
        fh.close()

    resultZipFile = path + ".zip"
    pkgutil.compress(resultZipFile, path)
    pkgutil.sendDataFileToHost(host, pkgutil.PORT_CREATOR, resultZipFile)

    try:
        os.chdir(startDir)
        os.remove(resultZipFile)
        shutil.rmtree(path)
    except OSError:
        print " - runTask: failed to clean up, cause='%s'" % OSError
        
    
    
if __name__ == "__main__":
    socketListener = SocketListener()
    socketListener.start()

    # some initial cleanup...
    if os.path.isdir(rootDir):
        shutil.rmtree(rootDir)
    
    while 1:
        print "mt: aquired lock..."
        waitCondition.acquire()
        if len(pendingTasks) == 0:
            print "mt: waiting..."
            waitCondition.wait()
            print "mt: woke up..."

        if len(pendingTasks) == 0:
            print "mt: no pending tasks after all this time..."

        todo = pendingTasks[0]
        del pendingTasks[0]
        waitCondition.release()
        runTask(todo)

    
    
