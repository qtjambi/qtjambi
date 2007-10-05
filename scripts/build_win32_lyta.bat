call build --crt-redist "%VCINSTALLDIR%\redist\x86\Microsoft.VC80.CRT" --qt-commercial c:\tmp\qt-commercial --qt-gpl c:\tmp\qt-opensource --qt-eval c:\tmp\qt-eval --no-source

scp qtjambi*eval* gunnar@anarki.troll.no:~/public_html/packages/eval
scp qtjambi*gpl* gunnar@anarki.troll.no:~/public_html/packages/gpl
scp qtjambi*commerc* gunnar@anarki.troll.no:~/public_html/packages/commercial
