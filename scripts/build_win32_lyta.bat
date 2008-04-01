call build --crt-redist "%VCINSTALLDIR%\redist\x86\Microsoft.VC80.CRT"

scp qtjambi-win32-eval*.zip gunnar@anarki.troll.no:~/public_html/packages/eval
scp qtjambi-win32-gpl*.zip gunnar@anarki.troll.no:~/public_html/packages/gpl
scp qtjambi-win32-commercial*.zip gunnar@anarki.troll.no:~/public_html/packages/commercial
scp qtjambi-win32-gpl*.jar gunnar@anarki.troll.no:~/public_html/packages/webstart
scp qtjambi-commercial-src*.zip gunnar@anarki.troll.no:~/public_html/packages/commercial
scp qtjambi-gpl-src*.zip gunnar@anarki.troll.no:~/public_html/packages/gpl
