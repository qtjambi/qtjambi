import os
import zipfile
import socket
import platform

VERBOSE = 1
PORT = 8184

ARCH_32 = "32"
ARCH_64 = "64"
ARCH_UNIVERSAL = "universal"

PLATFORM_WINDOWS = "win"
PLATFORM_LINUX = "linux"
PLATFORM_MAC = "mac"

LICENSE_GPL = "gpl"
LICENSE_EVAL = "eval"
LICENSE_COMMERCIAL = "commercial"

# Debugs out a string if the global variable "VERBOSE" is set
#  - 0: str: The string to debug out...
def debug(str):
    if VERBOSE:
        print str



# Compresses a directory into a zipfile
#  - 0: zipFile: The name of the output file...
#  - 1: zipRoot: The directory to zip down
def compress(zipFile, zipRoot):
    def zipHelper(unused, dir, fileList):
        for file in fileList:
            absFile = (dir + "/" + file)[len(zipRoot) + 1:]
            if os.path.isfile(absFile):
                print "wrote: "  + absFile;
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
#  --> returns the socket used for the transfer
def sendDataFileToHost(hostName, dataFile):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    debug(" - sendDataFile: connecting to: %s:%d" % (hostName, PORT))
    s.connect((hostName, 8184))
    sendDataFile(s, dataFile)
    return s


# Sends the binary file in 'dataFile' to the already opened 'socket'
#  - 0: socket: An opened socket
#  - 1: dataFile: the file to transfer...
def sendDataFile(socket, dataFile):
    file = open(dataFile, "rb")
    debug(" - sendDataFile: transfering...")
    block = file.read(4096)
    while block:
        socket.send(block);
        block = file.read(4096)
    debug(" - sendDataFile: transfer complete...")



# Gets a binary file from the 'socket' and writes it to 'dataFile'
#  - 0: socket: The socket
#  - 1: dataFile: the binary file to write..
def getDataFile(socket, dataFile):
    file = open(dataFile, "wb")
    data = socket.recv(4096)
    while data:
        file.write(data);
        data = socket.recv(4096);
    file.close();




# Recursively deletes the directory specified with root
#  - 0: root: The root directory. 
def rmdirs(root):
    for (dir, dirs, names) in os.walk(root, False):
        for name in names:
            os.remove(os.path.join(dir, name))
        os.rmdir(dir)



# Returns true if the script is running on mac os x
def isMac():
    return platform.system().find("Darwin") >= 0;



# Returns true if the script is running on windows
def isWindows():
    return platform.system().find("Windows") >= 0 or platform.system().find("CYGWIN") >= 0;



# Returns true if the script is running on linux
def isLinux():
    return platform.system().find("Linux") >= 0;




        
            
