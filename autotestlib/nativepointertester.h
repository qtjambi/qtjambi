#ifndef NATIVEPOINTERTESTER_H
#define NATIVEPOINTERTESTER_H

class NativePointerTester
{
public:
    int testInt(int &i, int to) {
        int tmp = i;
        i = to;
        return tmp;
    }

    QString testString(QString &s, const QString &to) {
        QString tmp = s;
        s = to;
        return tmp;
    }
};

#endif // NATIVEPOINTERTESTER_H
