; Installer script for qt jambi for windows
;

CRCCheck force
XPStyle on
SetCompressor /FINAL /SOLID lzma
SetCompressorDictSize 64


; The name of the installer
Name "Qt Jambi Installer"

; The file to write
OutFile "setup-qt-jambi-4.6.0.exe"

; The default installation directory
InstallDir C:\qt-jambi

; Request application privileges for Windows Vista
RequestExecutionLevel user

;--------------------------------

; Pages

Page directory
Page instfiles

;--------------------------------

; The stuff to install
Section "" ;No components page, name is not important

  ; Set output path to the installation directory.
  SetOutPath $INSTDIR
  
  ; Put file there
  File QtJambi.exe
  File qtjambi-4.6.0.jar
  File qtjambi-designer-4.6.0.jar
  File qtjambi-examples-4.6.0.jar
  File qtjambi-util-4.6.0.jar
  File qtjambi-win32-msvc2008-4.6.0.jar
  File set_qtjambi_env.bat
  File installer\changes-4.6.0_01
  File installer\designer.bat
  File installer\install.html
  File installer\LGPL_EXCEPTION.TXT
  File installer\LICENSE.GPL3
  File installer\LICENSE.LGPL
  File installer\readme.html
  
  File /r java\src\qtjambi-examples\com
  
  SetOutPath $INSTDIR\bin
  File bin\com_trolltech_qt_core.dll
  File bin\com_trolltech_qt_gui.dll
  File bin\com_trolltech_qt_network.dll
  File bin\com_trolltech_qt_opengl.dll
  File bin\com_trolltech_qt_phonon.dll
  File bin\com_trolltech_qt_sql.dll
  File bin\com_trolltech_qt_svg.dll
  File bin\com_trolltech_qt_webkit.dll
  File bin\com_trolltech_qt_xml.dll
  File bin\com_trolltech_qt_xmlpatterns.dll
  File bin\com_trolltech_tools_designer.dll
  File bin\juic.exe
  File bin\qtjambi.dll
  File ..\Qt\4.6.0\bin\designer.exe
  File ..\Qt\4.6.0\bin\linguist.exe
  File ..\Qt\4.6.0\bin\lrelease.exe
  File ..\Qt\4.6.0\bin\lupdate.exe
  File ..\Qt\4.6.0\bin\phonon4.dll
  File ..\Qt\4.6.0\bin\QtCore4.dll
  File ..\Qt\4.6.0\bin\QtGui4.dll
  File ..\Qt\4.6.0\bin\QtNetwork4.dll
  File ..\Qt\4.6.0\bin\QtOpenGL4.dll
  File ..\Qt\4.6.0\bin\QtSql4.dll
  File ..\Qt\4.6.0\bin\QtScript4.dll
  File ..\Qt\4.6.0\bin\QtSvg4.dll
  File ..\Qt\4.6.0\bin\QtWebKit4.dll
  File ..\Qt\4.6.0\bin\QtXml4.dll
  File ..\Qt\4.6.0\bin\QtXmlPatterns4.dll
  File ..\Qt\4.6.0\bin\QtDesignerComponents4.dll
  File ..\Qt\4.6.0\bin\QtDesigner4.dll
  
  File /r "..\Program Files (x86)\Microsoft Visual Studio 9.0\VC\redist\x86\Microsoft.VC90.CRT"
  
  
  SetOutPath $INSTDIR\lib
  File bin\com_trolltech_qt_core.dll
  File bin\com_trolltech_qt_gui.dll
  File bin\com_trolltech_qt_network.dll
  File bin\com_trolltech_qt_opengl.dll
  File bin\com_trolltech_qt_phonon.dll
  File bin\com_trolltech_qt_sql.dll
  File bin\com_trolltech_qt_svg.dll
  File bin\com_trolltech_qt_webkit.dll
  File bin\com_trolltech_qt_xml.dll
  File bin\com_trolltech_qt_xmlpatterns.dll
  File bin\com_trolltech_tools_designer.dll
  File bin\qtjambi.dll
  
  
  
  SetOutPath $INSTDIR\plugins
  File /r ..\Qt\4.6.0\plugins\*.dll
  
SectionEnd ; end the section
