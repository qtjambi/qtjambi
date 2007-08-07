build --qt-gpl d:\tmp\qt-opensource --qt-commercial d:\tmp\qt-commercial --qt-eval d:\tmp\qt-eval --qt d:\tmp\qt-commercial %1 %2 %3 %4 %5 %6 %7 %8 %9
scp qtjambi*eval* gunnar@anarki:~/public_html/packages/eval
scp qtjambi*gpl* gunnar@anarki:~/public_html/packages/gpl
scp qtjambi*commerc* gunnar@anarki:~/public_html/packages/commercial