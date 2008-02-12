#!/usr/bin/python

import os
import shutil
import socket
import sys
import time
from threading import Thread

import pkgutil


# initialize the socket callback interface so we have it
# available..
serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serversocket.bind((socket.gethostname(), 8184))
serversocket.listen(16)



class Options:
    def __init__(self):
        self.qtVersion = None
        self.packageRoot = None
        self.qtJambiVersion = "4.4.0_01"
        self.p4User = "qt";
        self.p4Client = "qt-builder";
options = Options()



class Package:
    def __init__(self, platform, arch, license):
        self.license = license
        self.platform = platform
        self.arch = arch
        self.binary = False
        self.removeDirs = [
            "autotestlib",
            "com/trolltech/autotests",
            "com/trolltech/benchmarks",
            "com/trolltech/extensions",
            "com/trolltech/manualtests",
            "com/trolltech/tests",
            "cpp",
            "dist",
            "doc/config",
            "doc/src",
            "eclipse-integration",
            "eclipse-stable",
            "jawt",
            "launcher_launcher",
            "libbenchmark",
            "scripts",
            "tools",
            "whitepaper"
            ]
        self.removeFiles = [
            ]
        self.mkdirs = [
            "include"
            ]
        self.copyFiles = [
            # Include files...
            ["qtjambi/qtjambi_core.h", "include"],
            ["qtjambi/qtjambi_cache.h", "include"],
            ["qtjambi/qtjambi_global.h", "include"],
            ["qtjambi/qtjambilink.h", "include"],
            ["qtjambi/qtjambifunctiontable.h", "include"],

            # text files for main directory...
            "dist/readme.html",
            "dist/install.html",
            "dist/changes-" + options.qtJambiVersion
            ]

    def setBinary(self):
        self.binary = True

    def setMacBinary(self):
        self.setBinary()
        self.copyFiles.append("dist/mac/qtjambi.sh")
        self.copyFiles.append("dist/mac/designer.sh")

    def setWinBinary(self):
        self.setBinary()
        self.copyFiles.append("dist/win/designer.bat")
        if self.arch == pkgutil.ARCH_64:
            self.copyFiles.append(["dist/win/qtjambi64.exe", "qtjambi.exe"])
        else:
            self.copyFiles.append("dist/win/qtjambi.exe")
            
        if self.license == pkgutil.LICENSE_GPL:
            self.compiler = "mingw"
        else:
            self.compiler = "msvc2005"
        

    def setLinuxBinary(self):
        self.setBinary()
        self.copyFiles.append("dist/linux/designer.sh")
        self.copyFiles.append("dist/linux/qtjambi.sh")

    def name(self):
        return "qtjambi-" + self.platform + self.arch + "-" + self.license + "-" + options.qtJambiVersion;
packages = []



# Sets up all the various packages to be built into the global
# variable "packages"
def setupPackages():
    win64 = Package(pkgutil.PLATFORM_WINDOWS, pkgutil.ARCH_64, pkgutil.LICENSE_COMMERCIAL)
    win64.setWinBinary()
    win64.buildServer = "aeryn.troll.no";
    packages.append(win64);



# Sets up the client spec and performs a complete checkout of the
# tree...
def prepareSourceTree():

    # remove and recreat dir and cd into it...
    shutil.rmtree(options.packageRoot)
    os.makedirs(options.packageRoot)
    os.chdir(options.packageRoot)

    # set up the perforce client...
    tmpFile = open("p4spec.tmp", "w")
    tmpFile.write("Root: %s\n" % (options.packageRoot))
    tmpFile.write("Owner: %s\n" % options.p4User)
    tmpFile.write("Client: %s\n" % options.p4Client)
    tmpFile.write("View:\n")
    tmpFile.write("        //depot/qtjambi/%s/...  //qt-builder/qtjambi/...\n" % options.qtJambiVersion)
    tmpFile.close()
    os.system("p4 -u %s -c %s client -i < p4spec.tmp" % (options.p4User, options.p4Client) );
    os.remove("p4spec.tmp")

    # sync p4 client spec into subdirectory...
    pkgutil.debug(" - syncing p4...")
    os.system("p4 -u %s -c %s sync -f //%s/... > .p4sync.buildlog" % (options.p4User, options.p4Client, options.p4Client))

    # unjar docs into doc directory...
#    pkgutil.debug(" - doing docs...")
#    os.makedirs("qtjambi/doc/html")
#    os.chdir("qtjambi/doc/html")
#    os.system("jar -xf %s/qtjambi-javadoc-%s.jar" % (options.startDir, options.qtJambiVersion) )



# Creates the build script (.bat or .sh), zips up the file and sends it off to the
# build server
def packageAndSend(package):
    pkgutil.debug("packaging and sending: %s..." % package.name())
    
    os.chdir(options.packageRoot)

    shutil.copytree("qtjambi", "tmptree");

    pkgutil.debug(" - creating task script")
    if package.platform == pkgutil.PLATFORM_WINDOWS:
        buildFile = open("tmptree/task.bat", "w")
        buildFile.write("call pkg_set_compiler " + package.compiler + "\n")
        buildFile.write("call pkg_set_qt " + options.qtVersion + "\n")
        buildFile.write("call ant generator.xmlmerge\n")
    else:
        buildfile = open("tmptree/task.sh", "w")
        buildFile.write("pkg_set_compiler " + package.compiler + "\n")
        buildFile.write("pkg_set_qt " + options.qtVersion + "\n")
        buildFile.write("ant generator\n")
    buildFile.close()

    pkgutil.debug(" - compressing...")
    pkgutil.compress(os.path.join(options.packageRoot, "tmp.zip"), os.path.join(options.packageRoot, "tmptree"))

    pkgutil.debug(" - sending %s to host: %s.." % (package.name(), package.buildServer))
    socket = pkgutil.sendDataFileToHost(package.buildServer, os.path.join(options.packageRoot, "tmp.zip"))
    socket.close()



# performs the post-compilation processing of the package
def postProcessPackage(package):
    print "Post process package " + package.name()



def waitForResponse():
    packagesRemaining = len(packages)
    print "Waiting for build server responses..."
    
    while packagesRemaining:
        (sock, (host, port)) = serversocket.accept()
        print " - got response from %s:%d" % (host, port)
        match = False
        for pkg in packages:
            print "   - matching %s vs %s... " % (pkg.buildServer, host)
            if socket.gethostbyname(pkg.buildServer) == host:
                pkg.dataFile = options.packageRoot + "/" + pkg.name() + ".zip"
                pkgutil.getDataFile(sock, pkg.dataFile)
                postProcessPackage(pkg)
                match = True
        if match:
            packagesRemaining = packagesRemaining - 1

        


# The main function, parses cmd line arguments and starts the pacakge
# building process...
def main():

    
    for i in range(0, len(sys.argv)):
        arg = sys.argv[i];
        if arg == "--qt-version":
            options.qtVersion = sys.argv[i+1]
        elif arg == "--package-root":
            options.packageRoot = sys.argv[i+1]
        elif arg == "--qt-jambi-version":
            options.qtJambiVersion = sys.argv[i+1]
        elif arg == "--verbose":
            pkgutil.VERBOSE = 1

    options.startDir = os.getcwd()

    print "Options:"
    print "  - Qt Version: " + options.qtVersion
    print "  - Package Root: " + options.packageRoot
    print "  - Qt Jambi Version: " + options.qtJambiVersion
    print "  - P4 User: " + options.p4User
    print "  - P4 Client: " + options.p4Client

    pkgutil.debug("preparing source tree...")
    prepareSourceTree()

    pkgutil.debug("configuring packages...");
    setupPackages()

    for package in packages:
        packageAndSend(package)

    waitForResponse()

    

if __name__ == "__main__":
    main()
