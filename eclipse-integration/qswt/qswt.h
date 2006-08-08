#ifndef QSWT_H
#define QSWT_H

#ifdef USE_QSWT

#include <QApplication>
#include <QObject>
#include <QList>
#include <QString>
#include <QByteArray>
#include <QDir>

// avoid name clashes
namespace QSWT
{
    void writeWinJavaFiles(const QList<QObject *> &lstObj, QString libName, QString package);
    void writeWinProjectFile(const QList<QString> &lstSources, const QList<QString> &lstHeaders, QString libName);

    void writeJavaFiles(const QList<QObject *> &lstObj, QString libName, QString package);
    void writeNativeHeaderFile(const QList<QObject *> &lstObj, QString libName, QString package);
    void writeNativeSourceFile(const QList<QObject *> &lstObj, const QList<QString> &lstHeaders, QString libName, QString package);
    void writeProjectFile(const QList<QString> &lstSources, const QList<QString> &lstHeaders, QString libName);
};

#define QSWT_MAIN_BEGIN(LibName, Package, axLibId, axAppId) \
int main(int argc, char *argv[]) \
{ \
    QString libName(LibName); \
    QString package(Package); \
    QApplication *qapp; \
    qapp = new QApplication(argc, argv); \
    QList<QObject *> objList; \
    QList<QString> headerList; \
    QList<QString> srcList; \
    QDir outPath; \
    outPath.mkdir(libName);
        
#define QSWT_CLASS(Class, Header, Sources) \
    objList.append(new Class()); \
    headerList.append(QString(Header)); \
    srcList.append(QString(Sources));

#ifdef Q_OS_WIN
#define QSWT_MAIN_END() \
    QSWT::writeWinJavaFiles(objList, libName, package); \
    QSWT::writeWinProjectFile(srcList, headerList, libName); \
    delete qapp; \
    return 0; \
}
#else
#define QSWT_MAIN_END() \
    QSWT::writeNativeHeaderFile(objList, libName, package); \
    QSWT::writeNativeSourceFile(objList, headerList, libName, package); \
    QSWT::writeJavaFiles(objList, libName, package); \
    QSWT::writeProjectFile(srcList, headerList, libName); \
    delete qapp; \
    return 0; \
}
#endif

#else //USE_QSWT

#ifdef Q_OS_WIN
#include <QAxFactory>
#define QSWT_MAIN_BEGIN(LibName, Package, axLibId, axAppId) \
    QAXFACTORY_BEGIN(axLibId, axAppId)
#define QSWT_CLASS(Class, Header, Sources) \
    QAXCLASS(Class)
#define QSWT_MAIN_END() \
    QAXFACTORY_END()
#else //Q_OS_WIN
#define QSWT_MAIN_BEGIN(LibName, Package, axLibId, axAppId)
#define QSWT_CLASS(Class, Header, Sources)
#define QSWT_MAIN_END()
#endif //Q_OS_WIN

#endif //USE_QSWT

#endif //QSWT_H
