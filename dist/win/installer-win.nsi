; Installer script for qt jambi for windows
;

CRCCheck force
XPStyle on
;SetCompressor /FINAL /SOLID lzma
;SetCompressorDictSize 64

!define v 4.7.1
!define QtDir C:\Qt\qt-everywhere-opensource-src-4.7.1
!define comp msvc2008

; The default installation directory
InstallDir c:\qtjambi-${v}

; The name of the installer
Name "Qt Jambi Installer"

; The file to write
OutFile "setup-qt-jambi-${v}-win32.exe"


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
  File qtjambi-${v}.jar
  File qtjambi-designer-${v}.jar
  File qtjambi-examples-${v}.jar
  File qtjambi-win32-${comp}-${v}.jar
  File dist\changes-${v}
  File dist\win\designer.bat
  File dist\win\qtjambi.bat
  File dist\install.html
  File dist\LICENSE.GPL3
  File dist\LICENSE.LGPL
  File dist\readme.html
  
  File /r build\java\out\qtjambi-examples\com
  
  SetOutPath $INSTDIR\bin
  File build\src\cpp\bin\com_trolltech_qt_core.dll
  File build\src\cpp\bin\com_trolltech_qt_gui.dll
  File build\src\cpp\bin\com_trolltech_qt_help.dll
  File build\src\cpp\bin\com_trolltech_qt_multimedia.dll
  File build\src\cpp\bin\com_trolltech_qt_network.dll
  File build\src\cpp\bin\com_trolltech_qt_opengl.dll
  File build\src\cpp\bin\com_trolltech_qt_phonon.dll
  File build\src\cpp\bin\com_trolltech_qt_script.dll
  File build\src\cpp\bin\com_trolltech_qt_scripttools.dll
  File build\src\cpp\bin\com_trolltech_qt_sql.dll
  File build\src\cpp\bin\com_trolltech_qt_svg.dll
  File build\src\cpp\bin\com_trolltech_qt_webkit.dll
  File build\src\cpp\bin\com_trolltech_qt_xml.dll
  File build\src\cpp\bin\com_trolltech_qt_xmlpatterns.dll
  File build\src\cpp\bin\com_trolltech_tools_designer.dll
  File bin\juic.exe
  File build\src\cpp\bin\qtjambi.dll
  File ${QtDir}\bin\designer.exe
  File ${QtDir}\bin\linguist.exe
  File ${QtDir}\bin\lrelease.exe
  File ${QtDir}\bin\lupdate.exe
  File ${QtDir}\bin\phonon4.dll
  File ${QtDir}\bin\QtCore4.dll
  File ${QtDir}\bin\QtGui4.dll
  File ${QtDir}\bin\QtHelp4.dll
  File ${QtDir}\bin\QtMultimedia4.dll
  File ${QtDir}\bin\QtNetwork4.dll
  File ${QtDir}\bin\QtOpenGL4.dll
  File ${QtDir}\bin\QtSql4.dll
  File ${QtDir}\bin\QtScript4.dll
  File ${QtDir}\bin\QtScriptTools4.dll
  File ${QtDir}\bin\QtSvg4.dll
  File ${QtDir}\bin\QtWebKit4.dll
  File ${QtDir}\bin\QtXml4.dll
  File ${QtDir}\bin\QtXmlPatterns4.dll
  File ${QtDir}\bin\QtDesignerComponents4.dll
  File ${QtDir}\bin\QtDesigner4.dll
  
  File /r "c:\Program Files\Microsoft Visual Studio 9.0\VC\redist\x86\Microsoft.VC90.CRT"
  
  SetOutPath $INSTDIR\lib
  File build\src\cpp\bin\com_trolltech_qt_core.dll
  File build\src\cpp\bin\com_trolltech_qt_gui.dll
  File build\src\cpp\bin\com_trolltech_qt_multimedia.dll
  File build\src\cpp\bin\com_trolltech_qt_network.dll
  File build\src\cpp\bin\com_trolltech_qt_opengl.dll
  File build\src\cpp\bin\com_trolltech_qt_phonon.dll
  File build\src\cpp\bin\com_trolltech_qt_sql.dll
  File build\src\cpp\bin\com_trolltech_qt_svg.dll
  File build\src\cpp\bin\com_trolltech_qt_script.dll
  File build\src\cpp\bin\com_trolltech_qt_scripttools.dll
  File build\src\cpp\bin\com_trolltech_qt_webkit.dll
  File build\src\cpp\bin\com_trolltech_qt_xml.dll
  File build\src\cpp\bin\com_trolltech_qt_xmlpatterns.dll
  File build\src\cpp\bin\com_trolltech_tools_designer.dll
  File build\src\cpp\bin\qtjambi.dll
  
  SetOutPath $INSTDIR\plugins
  File /r ${QtDir}\plugins\*.dll
  File /r ${QtDir}\plugins\*.dll
  File /r plugins\qtjambi
  
  SetOutPath $INSTDIR\plugins\designer
  File build\src\cpp\plugins\designer\JambiLanguage.dll
  File build\src\cpp\plugins\designer\JambiCustomWidget.dll

  
SectionEnd ; end the section
