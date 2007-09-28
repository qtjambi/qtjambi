call build --no-gpl --qt-commercial c:\tmp\qt-commercial --qt-eval c:\tmp\qt-eval --qt c:\tmp\qt-commercial %1 %2 %3 %4 %5 %6 %7 %8 %9

scp qtjambi*eval* gunnar@anarki:~/public_html/packages/eval
scp qtjambi*gpl* gunnar@anarki:~/public_html/packages/gpl
scp qtjambi*commerc* gunnar@anarki:~/public_html/packages/commercial
