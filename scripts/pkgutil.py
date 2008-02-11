import os
import zipfile
import socket

verbose = 1
PORT = 8184


def debug(str):
    if verbose:
        print str




# Compresses a directory into a zipfile
#  0: zipName: The name of the output file...
#  1: zipRoot: The directory to zip down
def compress(zipFile, zipRoot):
    def zipHelper(unused, dir, fileList):
        for file in fileList:
            absFile = (dir + "/" + file)[len(zipRoot) + 1:]
            if os.path.isfile(absFile):
                zip.write(absFile);
    os.chdir(zipRoot);
    zip = zipfile.ZipFile(zipFile, "w");
    os.path.walk(zipRoot, zipHelper, 'somenull')



# Decompresses a zipfile to a certain directory
#  - 0: zipFile: The name of the zipfile to compress
#  - 1: rootDir: The directory in which to stuff the output..
def uncompress(zipFile, rootDir):

    if os.path.isfile(rootDir):
        raise "uncompress: rootdir " + rootDir + " exists and is a file!"
    elif not os.path.isdir(rootDir):
        os.makedirs(rootDir, 0777)
        print "uncompres: directory didn't exist, created..."
        
    file = zipfile.ZipFile(zipFile);
    for name in file.namelist():
        absPath = os.path.join(rootDir, "/".join(os.path.split(name)[: -1]))
        if not os.path.isdir(absPath):
            os.makedirs(absPath)
        
        outfile = open(os.path.join(rootDir, name), 'wb')
        outfile.write(file.read(name))
        outfile.close()



# Opens a connection to hostName and sends the file specified by
# dataFile to that machine... The port number used is 8184 (ascii dec
# codes for 'Q', 'T')
#  - 0: hostName: The host name of the target machine.
#  - 1: dataFile: The file to send...
def sendDataFile(hostName, dataFile):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    debug(" - sendDataFile: connecting to: %s:%d" % (hostName, PORT))
    s.connect((hostName, 8184))
    file = open(dataFile, "rb")
    debug(" - sendDataFile: transfering...")
    block = file.read(4096)
    while block:
        s.send(block);
        block = file.read(4096)
    debug(" - sendDataFile: transfer complete...")



