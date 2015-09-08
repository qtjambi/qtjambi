@echo off

set BASE_DIR=%~dp0
set BIN_PATH=%BASE_DIR%\bin
set LIB_PATH=%BASE_DIR%\lib
set PATH=%BIN_PATH%;%LIB_PATH%;%PATH%

REM Launch juic
%BIN_PATH%\juic %*

endlocal
