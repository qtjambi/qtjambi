#include "qswt.h"

#include <QMetaMethod>
#include <QTextStream>
#include <QFile>

#define COMMONFILE "../common_src"

static bool ensureDir(const QFile &file) {
    QDir dir = QFileInfo(file).dir();
    if (!dir.exists() && !dir.mkpath(dir.absolutePath())) {
        qWarning("failed to create directory: %s\n",
                 qPrintable(dir.absolutePath()));
        return false;
    }
    return true;
}


bool typeNeedsConvertion(const QByteArray &qtype)
{
    if (qtype.contains('*'))
        return true;
    else if(qtype == "QString")
        return true;
    return false;
}

QString toJavaNativeType(const QByteArray &qtype)
{
    if (qtype.isEmpty())
        return "void";
    else if(qtype == "int")
        return "jint";
    else if(qtype == "bool")
        return "jboolean";
    else if(qtype == "QString")
        return "jstring";

    return "unknown";
}

QString toJavaType(const QByteArray &qtype)
{
    if (qtype.isEmpty())
        return "void";
    else if(qtype == "int")
        return "int";
    else if(qtype == "bool")
        return "boolean";
    else if(qtype == "QString")
        return "String";

    return "unknown";
}

void convertTypes(QTextStream &stream, const QList<QByteArray> &parTypes, const QList<QByteArray> &parNames)
{
    for (int npar=0; npar<parTypes.count(); ++npar)
    {
        if (typeNeedsConvertion(parTypes.at(npar)))
        {
            // c cast :(
            if (parTypes.at(npar).contains('*')) {
                stream << "    " << parTypes.at(npar)
                    << parNames.at(npar) << " = (" << parTypes.at(npar)
                    << ")jni_" << parNames.at(npar) << ";\n";
            }
            else if (parTypes.at(npar) == "QString") {
                stream << "    const char *utf_" << parNames.at(npar) << " = env->GetStringUTFChars(jni_"
                    << parNames.at(npar) << ", 0);\n";
                stream << "    QString " << parNames.at(npar) << " = QString::fromUtf8(utf_"
                    << parNames.at(npar) << ");\n";
                stream << "    env->ReleaseStringUTFChars(jni_" << parNames.at(npar)
                    << ", utf_" << parNames.at(npar) << ");\n";
            }
        }
    }
}

QString toMemberName(const char *signature)
{
    QString memberName(signature);
    return memberName.left(memberName.indexOf('(')+1);
}

void writeStaticData(QTextStream &source, QTextStream &target, QString token)
{
    bool tokenFound = false;
    source.device()->seek(0);
    while(!source.atEnd())
    {
        QString sline = source.readLine();
        if (tokenFound)
        {
            if(sline == "//[END]") break;
            target << sline << "\n";
        }
        else if (sline == token)
        {
            tokenFound = true;
        }
    }
}

int nrOfSignals(const QMetaObject *metaobj)
{
    int count = 0;
    QMetaMethod mmember;
    for (int nmem=metaobj->methodOffset(); nmem<metaobj->methodCount(); ++nmem)
    {
        mmember = metaobj->method(nmem);
        if (mmember.methodType() == QMetaMethod::Signal)
            count++;
    }
    return count;
}


void convertReturnType(QTextStream &stream, const QByteArray &typeName)
{
    QString tn(typeName);
    if (tn.isEmpty()) {
        stream << "    Q_UNUSED(env);\n";
        return;
    }

    if (tn == "QString") {
        stream << "    return env->NewStringUTF(res.toUtf8());\n";
    } else {
        stream << "    Q_UNUSED(env);\n";
        stream << "    return (" << toJavaNativeType(typeName) << ")res;\n";
    }
}

// from jbindings
QString cppToJNISig(const QString param)
{
    if (param == "void") return "V";
    if (param == "bool") return "Z";
    if (param == "byte") return "B";
    if (param == "char") return "C";
    if (param == "short") return "S";
    if (param == "int") return "I";
    if (param == "long") return "J";
    if (param == "float") return "F";
    if (param == "double") return "D";
    return "";
}

QString getJavaSignature(QMetaMethod mmember)
{
    QString result = "(I";
    QList<QByteArray> parTypes = mmember.parameterTypes();

    for (int npar = 0; npar<parTypes.count(); ++npar) {
        result += cppToJNISig(parTypes.at(npar));
    }

    return (result + ")V");
}

void writeJavaListenerFile(const QObject *obj, QString libName, QString package)
{
    const QMetaObject *metaobj;
    QMetaMethod mmember;

    metaobj = obj->metaObject();
    if (nrOfSignals(metaobj) == 0)
        return;

    QFile lfile(libName + "/java/" + metaobj->className() + "Listener" + ".java");
    if (!ensureDir(lfile))
        return;

    if (!lfile.open(QIODevice::WriteOnly)) {
        qWarning("failed to write file: %s\n", qPrintable(QFileInfo(lfile).absoluteFilePath()));
        return;
    }
    QTextStream stream(&lfile);



    if (!package.isEmpty())
        stream << "package " << package << ";\n\n";

    stream << "public interface " << metaobj->className() << "Listener\n";
    stream << "{\n";

    for (int nmem=metaobj->methodOffset(); nmem<metaobj->methodCount(); ++nmem)
    {
        mmember = metaobj->method(nmem);
        QList<QByteArray> parTypes = mmember.parameterTypes();
        QList<QByteArray> parNames = mmember.parameterNames();

        if (mmember.methodType() == QMetaMethod::Signal)
        {
            stream << "    public void " << toMemberName(mmember.signature());
            for (int npar=0; npar<parTypes.count(); ++npar) {
                if (npar != 0) stream << ", ";
                stream << toJavaType(parTypes.at(npar)) << " " << parNames.at(npar);
            }
            stream << ");\n";
        }
    }

    stream << "}\n";

    lfile.close();
}

void writeJavaFile(const QObject *obj, QString libName, QString package)
{
    const QMetaObject *metaobj;
    QMetaMethod mmember;

    metaobj = obj->metaObject();
    bool hasSig = (nrOfSignals(metaobj) != 0);

    QFile jfile(libName + "/java/" + metaobj->className() + ".java");
    if (!ensureDir(jfile))
        return;
    if (!jfile.open(QIODevice::WriteOnly)) return;
    QTextStream stream(&jfile);

    QFile cfile(COMMONFILE);
    if (!ensureDir(cfile))
        return;
    if (!cfile.open(QIODevice::ReadOnly)) return;
    QTextStream scommon(&cfile);

    if (!package.isEmpty())
        stream << "package " << package << ";\n\n";

    writeStaticData(scommon, stream, "//[COMMON_JAVA_IMPORT]");
    stream << "public class " << metaobj->className() << " extends Composite\n{\n";

    stream << "    int handleWidget;\n";
    if (hasSig)
        stream << "    ArrayList listeners;\n";
    stream << "    static Hashtable table = new Hashtable();\n";
    stream << "    static {\n";
    stream << "        System.loadLibrary (\"" << libName << "\");\n";
    stream << "    }\n\n";
    stream << "    public " << metaobj->className() << "(Composite parent, int style)\n    {\n";

    writeStaticData(scommon, stream, "//[COMMON_JAVA_CONSTRUCTOR]");

    if (hasSig)
        stream << "        listeners = new ArrayList();\n\n";
    stream << "        addDisposeListener(new DisposeListener() {\n";
    stream << "            public void widgetDisposed(DisposeEvent e) {\n";
    stream << "                " << metaobj->className() << ".this.widgetDisposed(e);\n";
    stream << "            }\n";
    stream << "        });\n\n";

    stream << "        addControlListener(new ControlAdapter() {\n";
    stream << "            public void controlResized(ControlEvent e) {\n";
    stream << "                " << metaobj->className() << ".this.controlResized(e);\n";
    stream << "            }\n";
    stream << "        });\n\n";

    stream << "    }\n\n";
    writeStaticData(scommon, stream, "//[COMMON_JAVA_FUNCTIONS]");

    for (int nmem=metaobj->methodOffset(); nmem<metaobj->methodCount(); ++nmem)
    {
        mmember = metaobj->method(nmem);
        QList<QByteArray> parTypes = mmember.parameterTypes();
        QList<QByteArray> parNames = mmember.parameterNames();

        // public slots
        if ((mmember.access() == QMetaMethod::Public)
            && (mmember.methodType() == QMetaMethod::Slot))
        {
            stream << "    public " << toJavaType(mmember.typeName()) << " " << toMemberName(mmember.signature());
            for (int npar=0; npar<parTypes.count(); ++npar) {
                if (npar != 0) stream << ", ";
                stream << toJavaType(parTypes.at(npar)) << " " << parNames.at(npar);
            }
            stream << ")\n    {\n        checkWidget();\n        ";

            if (!QString(mmember.typeName()).isEmpty())
                stream << "return ";

            stream << toMemberName(mmember.signature()) << "handleWidget";
            for (int npar=0; npar<parNames.count(); ++npar) {
                stream << ", " << parNames.at(npar);
            }
            stream << ");\n    }\n\n";
        }
    }

    if(hasSig)
    {
        stream << "    public void add" << metaobj->className()
            << "Listener(" << metaobj->className() << "Listener lstnr)\n";
        stream << "    {\n";
        stream << "        listeners.add(lstnr);\n";
        stream << "    }\n\n";

        stream << "    public void remove" << metaobj->className()
            << "Listener(" << metaobj->className() << "Listener lstnr)\n";
        stream << "    {\n";
        stream << "        listeners.remove(listeners.indexOf(lstnr));\n";
        stream << "    }\n\n";
        stream << "    // native callback functions\n";

        for (int nmem=metaobj->methodOffset(); nmem<metaobj->methodCount(); ++nmem)
        {
            mmember = metaobj->method(nmem);
            QList<QByteArray> parTypes = mmember.parameterTypes();
            QList<QByteArray> parNames = mmember.parameterNames();
            if (mmember.methodType() == QMetaMethod::Signal)
            {
                stream << "    static void " << toMemberName(mmember.signature());
                stream << "int handle";
                for (int npar=0; npar<parTypes.count(); ++npar) {
                    stream << ", " << toJavaType(parTypes.at(npar)) << " " << parNames.at(npar);
                }
                stream << ")\n    {\n";
                stream << "        " << metaobj->className() << " obj = ("
                    << metaobj->className() << ") table.get(new Integer (handle));\n";
                stream << "        if (obj == null) return;\n";
                stream << "        for (int i=0; i<obj.listeners.size(); i++)\n";
                stream << "        {\n";
                stream << "            ((" << metaobj->className() << "Listener)obj.listeners.get(i))."
                    << toMemberName(mmember.signature());
                for (int npar=0; npar<parTypes.count(); ++npar) {
                    if (npar != 0) stream << ", ";
                    stream << parNames.at(npar);
                }
                stream << ");\n";
                stream << "        }\n";
                stream << "    }\n\n";
            }
        }
    }

    writeStaticData(scommon, stream, "//[COMMON_JAVA_NATIVE]");

    // declare native functions
    for (int nmem=metaobj->methodOffset(); nmem<metaobj->methodCount(); ++nmem)
    {
        mmember = metaobj->method(nmem);
        QList<QByteArray> parTypes = mmember.parameterTypes();
        QList<QByteArray> parNames = mmember.parameterNames();

        // public slots
        if ((mmember.access() == QMetaMethod::Public)
            && (mmember.methodType() == QMetaMethod::Slot))
        {
            stream << "    static final native " << toJavaType(mmember.typeName()) << " " << toMemberName(mmember.signature());
            stream << "int handle";
            for (int npar=0; npar<parTypes.count(); ++npar) {
                stream << ", " << toJavaType(parTypes.at(npar)) << " " << parNames.at(npar);
            }
            stream << ");\n";
        }
    }

    stream << "}\n";

    jfile.close();
    cfile.close();
}

void QSWT::writeJavaFiles(const QList<QObject *> &lstObj, QString libName, QString package)
{
    for (int nobj=0; nobj<lstObj.count(); ++nobj) {
        writeJavaFile(lstObj.at(nobj), libName, package);
        writeJavaListenerFile(lstObj.at(nobj), libName, package);
    }
}

void QSWT::writeNativeHeaderFile(const QList<QObject *> &lstObj, QString libName, QString package)
{
    const QMetaObject *metaobj;
    QMetaMethod mmember;
    QString uname = libName.toUpper();

    QString sigpack;
    if (!package.isEmpty())
        sigpack = package.replace('.', '_') + "_";

    QFile hfile(libName + "/" + libName + ".h");
    if (!ensureDir(hfile))
        return;
    if (!hfile.open(QIODevice::WriteOnly)) return;
    QTextStream stream(&hfile);

    stream << "#include <QObject>\n";
    stream << "#include <jni.h>\n\n";
    stream << "#ifndef " << uname << "_H\n";
    stream << "#define " << uname << "_H\n\n";

    stream << "extern \"C\" {";

    for (int nobj=0; nobj<lstObj.count(); ++nobj)
    {
        metaobj = lstObj.at(nobj)->metaObject();
        stream << "\n// ------ " << metaobj->className() << " ------\n";

        stream << "JNIEXPORT jint JNICALL Java_" << sigpack << metaobj->className()
            << "_createControl(JNIEnv *, jclass, jint, jint);\n";
        stream << "JNIEXPORT void JNICALL Java_" << sigpack << metaobj->className()
            << "_computeSize(JNIEnv *, jclass, jint, jintArray);\n";
        stream << "JNIEXPORT void JNICALL Java_" << sigpack << metaobj->className()
            << "_resizeControl(JNIEnv *, jclass, jint, jint, jint, jint, jint);\n";
        stream << "JNIEXPORT void JNICALL Java_" << sigpack << metaobj->className()
            << "_disposeControl(JNIEnv *, jclass, jint);\n";
        stream << "JNIEXPORT void JNICALL Java_" << sigpack << metaobj->className()
            << "_setFont(JNIEnv *, jclass, jint, jstring, jint);\n";

        for (int nmem=metaobj->methodOffset(); nmem<metaobj->methodCount(); ++nmem)
        {
            mmember = metaobj->method(nmem);
            QList<QByteArray> parTypes = mmember.parameterTypes();
            QList<QByteArray> parNames = mmember.parameterNames();

            // public slots
            if ((mmember.access() == QMetaMethod::Public)
                && (mmember.methodType() == QMetaMethod::Slot))
            {
                stream << "JNIEXPORT " << toJavaNativeType(mmember.typeName())
                    << " JNICALL Java_" << sigpack << metaobj->className() << "_" << toMemberName(mmember.signature())
                    << "JNIEnv *, jclass, jint";

                for (int npar=0; npar<parTypes.count(); ++npar)
                {
                    stream << ", " + toJavaNativeType(parTypes.at(npar));
                }
                stream << ");\n";
            }
        }
    }

    stream << "}\n";

    for (int nobj=0; nobj<lstObj.count(); ++nobj)
    {
        metaobj = lstObj.at(nobj)->metaObject();
        int nrSig = nrOfSignals(metaobj);

        if (nrSig != 0)
        {
            stream << "\n// ------ " << metaobj->className() << " ------\n";

            //create a fake listening class
            stream << "class " << metaobj->className() << "Listener : public QObject\n";
            stream << "{\n";
            stream << "    Q_OBJECT\n";
            stream << "public:\n";
            stream << "    " << metaobj->className() << "Listener(QObject *parent, pthread_key_t *key, jclass that)\n";
            stream << "        : QObject(parent)\n";
            stream << "    {\n";
            stream << "        envKey = key;\n";
            stream << "        JNIEnv *env = (JNIEnv *)pthread_getspecific(*envKey);\n";

            // get the java method id's
            stream << "        javaClass = (jclass) env->NewGlobalRef((jobject)that);\n";

            int mid = 0;
            for (int nmem=metaobj->methodOffset(); nmem<metaobj->methodCount(); ++nmem)
            {
                mmember = metaobj->method(nmem);
                if (mmember.methodType() == QMetaMethod::Signal)
                {
                    QString memberName(mmember.signature());
                    memberName = memberName.left(memberName.indexOf('('));
                    stream << "        jmId[" << mid
                        << "] = env->GetStaticMethodID(that, \""
                        << memberName << "\", \"" << getJavaSignature(mmember) << "\");\n";
                    mid++;
                }
            }

            stream << "    }\n\n";
            stream << "    ~" << metaobj->className() << "Listener() { }\n\n";
            stream << "public slots:\n";

            mid = 0;
            for (int nmem=metaobj->methodOffset(); nmem<metaobj->methodCount(); ++nmem)
            {
                mmember = metaobj->method(nmem);
                QList<QByteArray> parTypes = mmember.parameterTypes();
                QList<QByteArray> parNames = mmember.parameterNames();

                if (mmember.methodType() == QMetaMethod::Signal)
                {
                    stream << "    void slot_" <<
                        toMemberName(mmember.signature());
                    for (int npar=0; npar<parTypes.count(); ++npar) {
                        if (npar != 0) stream << ", ";
                        stream << parTypes.at(npar) << " " << parNames.at(npar);
                    }
                    stream << ")\n    {\n";
                    stream << "        JNIEnv *env = (JNIEnv *)pthread_getspecific(*envKey);\n";
                    stream << "        if (env == NULL) return;\n";
                    stream << "        env->CallStaticVoidMethod(javaClass, jmId[" << mid << "], (jint)parent()";
                    for (int npar=0; npar<parTypes.count(); ++npar) {
                        stream << ", " << parNames.at(npar);
                    }
                    stream << ");\n";
                    mid++;
                    stream << "    }\n\n";
                }
            }

            stream << "private:\n";
            stream << "    pthread_key_t *envKey;\n";

            // define all the signals
            stream << "    jclass javaClass;\n";
            stream << "    jmethodID jmId[" << nrSig << "];\n";

            stream << "};\n\n";
        }
    }

    stream << "#endif //" << uname << "_H\n";

    hfile.close();
}

void QSWT::writeProjectFile(const QList<QString> &lstSources, const QList<QString> &lstHeaders, QString libName)
{
    QFile pfile(libName + "/" + libName + "_inc.pri");
    if (!ensureDir(pfile))
        return;
    if (!pfile.open(QIODevice::WriteOnly)) return;
    QTextStream stream(&pfile);

    QFile cfile(COMMONFILE);
    if (!ensureDir(cfile))
        return;
    if (!cfile.open(QIODevice::ReadOnly)) return;
    QTextStream scommon(&cfile);

    writeStaticData(scommon, stream, "//[COMMON_PROJECT_HEADER]");

    stream << "HEADERS += " << libName << ".h";
    for (int nheader=0; nheader<lstHeaders.count(); ++nheader) {
        stream << "\\\n    ../" << lstHeaders.at(nheader);
    }
    stream << "\n\n";
    stream << "SOURCES += " << libName << ".cpp";
    for (int nsrc=0; nsrc<lstSources.count(); ++nsrc) {
        stream << "\\\n    ../" << lstSources.at(nsrc);
    }

    pfile.close();
    cfile.close();
}

void QSWT::writeNativeSourceFile(const QList<QObject *> &lstObj, const QList<QString> &lstHeaders, QString libName, QString package)
{
    const QMetaObject *metaobj;
    QMetaMethod mmember;
    QString lname = libName.toLower();

    QString sigpack;
    if (!package.isEmpty())
        sigpack = package.replace('.', '_') + "_";

    QFile sfile(libName + "/" + libName + ".cpp");
    if (!ensureDir(sfile))
        return;
    if (!sfile.open(QIODevice::WriteOnly)) return;
    QTextStream stream(&sfile);

    QFile cfile(COMMONFILE);
    if (!ensureDir(cfile))
        return;
    if (!cfile.open(QIODevice::ReadOnly)) return;
    QTextStream scommon(&cfile);

    for (int nheader=0; nheader<lstHeaders.count(); ++nheader) {
        stream << "#include \"" << lstHeaders.at(nheader) << "\"\n";
    }

    writeStaticData(scommon, stream, "//[COMMON_NATIVE_HEADER]");
    stream << "#include \"" << libName << ".h\"\n";

    writeStaticData(scommon, stream, "//[COMMON_NATIVE_FUNCTIONS]");

    for (int nobj=0; nobj<lstObj.count(); ++nobj)
    {
        metaobj = lstObj.at(nobj)->metaObject();
        int nrSig = nrOfSignals(metaobj);

        stream << "// ------ " << metaobj->className() << " ------\n";

        stream << "JNIEXPORT jint JNICALL Java_" << sigpack << metaobj->className()
            << "_createControl(JNIEnv *env, jclass that, jint parent, jint socketWin)\n{\n";
        writeStaticData(scommon, stream, "//[COMMON_NATIVE_CREATEQAPP]");
        stream << "    " << metaobj->className() << " *obj = new " << metaobj->className() << "();\n";
        writeStaticData(scommon, stream, "//[COMMON_NATIVE_CREATECONTROL]");

        // initialize all the signals
        if (nrSig != 0)
        {
            // connect the signals to a fake listener object
            stream << "    " << metaobj->className() << "Listener *lstnr = new "
                << metaobj->className() << "Listener(obj, envKey, that);\n";
            for (int nmem=metaobj->methodOffset(); nmem<metaobj->methodCount(); ++nmem)
            {
                mmember = metaobj->method(nmem);
                QList<QByteArray> parTypes = mmember.parameterTypes();
                QList<QByteArray> parNames = mmember.parameterNames();

                if (mmember.methodType() == QMetaMethod::Signal)
                {
                    stream << "    QObject::connect(obj, SIGNAL("
                        << toMemberName(mmember.signature());
                    for (int npar=0; npar<parTypes.count(); ++npar) {
                        if (npar != 0) stream << ", ";
                        stream << parTypes.at(npar);
                    }
                    stream << ")), lstnr, SLOT(slot_"
                        << toMemberName(mmember.signature());
                    for (int npar=0; npar<parTypes.count(); ++npar) {
                        if (npar != 0) stream << ", ";
                        stream << parTypes.at(npar);
                    }
                    stream << ")));\n";
                }
            }
        }
        else
        {
            stream << "    Q_UNUSED(that);\n\n";
        }

        stream << "    vblayout->addWidget(obj);\n\n";
        stream << "    xeclient->embedInto(socketWin);\n";
        stream << "    xeclient->show();\n";
        stream << "    return (jint)obj;\n";
        stream << "}\n\n";

        stream << "JNIEXPORT void JNICALL Java_" << sigpack <<  metaobj->className()
            << "_computeSize(JNIEnv *env, jclass that, jint handle, jintArray result)\n{\n";
        stream << "    " << metaobj->className() << " *obj = (" << metaobj->className() << "*)handle;\n";
        writeStaticData(scommon, stream, "//[COMMON_NATIVE_COMPUTESIZE]");
        stream << "}\n\n";

        stream << "JNIEXPORT void JNICALL Java_" << sigpack << metaobj->className()
            << "_resizeControl(JNIEnv *env, jclass that, jint handle, jint x, jint y, jint width, jint height)\n{\n";
        stream << "    " << metaobj->className() << " *obj = (" << metaobj->className() << "*)handle;\n";
        writeStaticData(scommon, stream, "//[COMMON_NATIVE_RESIZECONTROL]");
        stream << "}\n\n";

        stream << "JNIEXPORT void JNICALL Java_" << sigpack << metaobj->className()
            << "_disposeControl(JNIEnv *env, jclass that, jint handle)\n{\n";
        stream << "    " << metaobj->className() << " *obj = (" << metaobj->className() << "*)handle;\n";
        stream << "    QWidget *xembed = obj->topLevelWidget();\n";
        stream << "    delete xembed;\n";
        stream << "    Q_UNUSED(env);\n";
        stream << "    Q_UNUSED(that);\n";
        stream << "}\n\n";

        stream << "JNIEXPORT void JNICALL Java_" << sigpack << metaobj->className()
            << "_setFont(JNIEnv *env, jclass that, jint handle, jstring jni_family, jint size)\n{\n";
        stream << "    " << metaobj->className() << " *obj = (" << metaobj->className() << "*)handle;\n";
        writeStaticData(scommon, stream, "//[COMMON_NATIVE_SETFONT]");
        stream << "}\n\n";

        for (int nmem=metaobj->methodOffset(); nmem<metaobj->methodCount(); ++nmem)
        {
            mmember = metaobj->method(nmem);
            QList<QByteArray> parTypes = mmember.parameterTypes();
            QList<QByteArray> parNames = mmember.parameterNames();

            // public slots
            if ((mmember.access() == QMetaMethod::Public)
                && (mmember.methodType() == QMetaMethod::Slot))
            {
                stream << "JNIEXPORT " << toJavaNativeType(mmember.typeName())
                    << " JNICALL Java_" << sigpack << metaobj->className() << "_" << toMemberName(mmember.signature())
                    << "JNIEnv *env, jclass that, jint handle";

                for (int npar=0; npar<parTypes.count(); ++npar) {
                    stream << ", " << toJavaNativeType(parTypes.at(npar)) << " ";
                    if (typeNeedsConvertion(parTypes.at(npar)))
                        stream << "jni_";
                    stream << parNames.at(npar);
                }
                stream << ")\n{\n    " << metaobj->className() << " *obj = ("
                    << metaobj->className() << "*)handle;\n";

                stream << "    Q_UNUSED(that);\n";

                convertTypes(stream, parTypes, parNames);

                stream << "    ";
                if (!QString(mmember.typeName()).isEmpty())
                    stream << mmember.typeName() << " res = ";
                stream << "obj->" << toMemberName(mmember.signature());
                for (int npar=0; npar<parTypes.count(); ++npar) {
                    if (npar != 0) stream << ", ";
                    stream << parNames.at(npar);
                }
                stream << ");\n";
                convertReturnType(stream, mmember.typeName());
                stream << "}\n\n";
            }
        }
    }

    cfile.close();
    sfile.close();
}

// ************* WINDOWS *****************

QString variantToJava(QString qtype)
{
    if(qtype == "int")
        return "getInt()";
    else if(qtype == "bool")
        return "getBoolean()";
    else if(qtype == "QString")
        return "getString()";

    return "##error##";
}

void writeWinJavaFile(const QObject *obj, QString libName, QString package)
{
    const QMetaObject *metaobj;
    QMetaMethod mmember;

    metaobj = obj->metaObject();
    bool hasSig = (nrOfSignals(metaobj) != 0);

    QFile jfile(libName + "/java/" + metaobj->className() + ".java");
    if (!ensureDir(jfile))
        return;
    if (!jfile.open(QIODevice::WriteOnly)) return;
    QTextStream stream(&jfile);

    QFile cfile(COMMONFILE);
    if (!ensureDir(cfile))
        return;
    if (!cfile.open(QIODevice::ReadOnly)) return;
    QTextStream scommon(&cfile);

    if (!package.isEmpty())
        stream << "package " << package << ";\n\n";

    writeStaticData(scommon, stream, "//[COMMON_WIN_JAVA_IMPORT]");
    stream << "public class " << metaobj->className() << " extends Composite\n{\n";

    // signals
    if (hasSig)
        stream << "    ArrayList listeners;\n";

    stream << "    OleFrame frame;\n";
    stream << "    OleControlSite site;\n";
    stream << "    OleAutomation automation;\n";
    stream << "    ArrayList dispIds;\n\n";

    stream << "    public " << metaobj->className() << "(Composite parent, int style)\n    {\n";

    writeStaticData(scommon, stream, "//[COMMON_WIN_JAVA_CONSTRUCTOR]");

    stream << "        site = new OleControlSite(frame, SWT.NONE, \"" << libName << "."
        << metaobj->className() << "\");\n";

    stream << "        automation = new OleAutomation(site);\n";
    stream << "        site.doVerb(OLE.OLEIVERB_SHOW);\n\n";

    int tmpId = 5; //start Id is (usually) 4
    if (hasSig)
    {
        stream << "        listeners = new ArrayList();\n";
        for (int nmem=metaobj->methodOffset(); nmem<metaobj->methodCount(); ++nmem)
        {
            mmember = metaobj->method(nmem);
            QList<QByteArray> parTypes = mmember.parameterTypes();
            QList<QByteArray> parNames = mmember.parameterNames();
            if (mmember.methodType() == QMetaMethod::Signal)
            {
	        stream << "        site.addEventListener(" << tmpId << ", new OleListener() {\n";
		stream << "            public void handleEvent(OleEvent event) {\n";

                for (int npar=0; npar<parTypes.count(); ++npar) {
                    stream << "                Variant " << parNames.at(npar)
                        << " = event.arguments[" << npar << "];\n";
                }

                stream << "                for (int i=0; i<listeners.size(); i++)\n";
                stream << "                {\n";
                stream << "                    ((" << metaobj->className()
                    << "Listener)listeners.get(i))." << toMemberName(mmember.signature());

                for (int npar=0; npar<parTypes.count(); ++npar) {
                    if (npar != 0) stream << ", ";
                    stream << parNames.at(npar) << "." << variantToJava(parTypes.at(npar));
                }
                stream << ");\n";
                stream << "                }\n";
		stream << "            }\n";
	        stream << "        });\n\n";
                ++tmpId;
            }
        }
    }

    tmpId = 0;
    for (int nmem=metaobj->methodOffset(); nmem<metaobj->methodCount(); ++nmem)
    {
        mmember = metaobj->method(nmem);

        if ((mmember.access() == QMetaMethod::Public)
            && (mmember.methodType() == QMetaMethod::Slot))
        {
            QString memberName = toMemberName(mmember.signature());
            memberName.chop(1);

            stream << "        tmpIds = automation.getIDsOfNames(new String[]{\""
                    << memberName << "\"});\n";
            stream << "        dispIds.add(new Integer(tmpIds[0]));\n\n";
            ++tmpId;
        }
    }

    stream << "    }\n\n";
    writeStaticData(scommon, stream, "//[COMMON_WIN_JAVA_FUNCTIONS]");

    tmpId = 0;
    for (int nmem=metaobj->methodOffset(); nmem<metaobj->methodCount(); ++nmem)
    {
        mmember = metaobj->method(nmem);
        QList<QByteArray> parTypes = mmember.parameterTypes();
        QList<QByteArray> parNames = mmember.parameterNames();

        // public slots
        if ((mmember.access() == QMetaMethod::Public)
            && (mmember.methodType() == QMetaMethod::Slot))
        {
            stream << "    public " << toJavaType(mmember.typeName()) << " " << toMemberName(mmember.signature());
            for (int npar=0; npar<parTypes.count(); ++npar) {
                if (npar != 0) stream << ", ";
                stream << toJavaType(parTypes.at(npar)) << " " << parNames.at(npar);
            }
            stream << ")\n    {\n";

            if (parTypes.count() > 0)
                stream << "        Variant[] par = new Variant[" << parTypes.count() << "];\n";

            for (int npar=0; npar<parTypes.count(); ++npar) {
                stream << "        par[" << npar << "] = new Variant(" << parNames.at(npar) << ");\n";
            }

            stream << "        int dispId = ((Integer)dispIds.get(" << tmpId << ")).intValue();\n";
            if (!QString(mmember.typeName()).isEmpty())
            {
                stream << "        Variant varResult = automation.invoke(dispId";
                if (parTypes.count() > 0)
                    stream << ", par";
                stream << ");\n";
                stream << "        return varResult." << variantToJava(mmember.typeName()) << ";\n";
            }
            else
            {
                stream << "        automation.invoke(dispId";
                if (parTypes.count() > 0)
                    stream << ", par";
                stream << ");\n";
            }

            stream << "    }\n\n";
            ++tmpId;
        }
    }

    if(hasSig)
    {
        stream << "    public void add" << metaobj->className()
            << "Listener(" << metaobj->className() << "Listener lstnr)\n";
        stream << "    {\n";
        stream << "        listeners.add(lstnr);\n";
        stream << "    }\n\n";

        stream << "    public void remove" << metaobj->className()
            << "Listener(" << metaobj->className() << "Listener lstnr)\n";
        stream << "    {\n";
        stream << "        listeners.remove(listeners.indexOf(lstnr));\n";
        stream << "    }\n\n";
    }

    stream << "}\n";

    jfile.close();
    cfile.close();
}

void QSWT::writeWinJavaFiles(const QList<QObject *> &lstObj, QString libName, QString package)
{
    for (int nobj=0; nobj<lstObj.count(); ++nobj) {
        writeWinJavaFile(lstObj.at(nobj), libName, package);
        writeJavaListenerFile(lstObj.at(nobj), libName, package);
    }
}

void QSWT::writeWinProjectFile(const QList<QString> &lstSources, const QList<QString> &lstHeaders, QString libName)
{
    QFile pfile(libName + "/" + libName + "_inc.pri");
    if (!ensureDir(pfile))
        return;
    if (!pfile.open(QIODevice::WriteOnly)) return;
    QTextStream stream(&pfile);

    QFile cfile(COMMONFILE);
    if (!ensureDir(cfile))
        return;
    if (!cfile.open(QIODevice::ReadOnly)) return;
    QTextStream scommon(&cfile);

    writeStaticData(scommon, stream, "//[COMMON_WIN_PROJECT_HEADER]");

    stream << "HEADERS += ";
    for (int nheader=0; nheader<lstHeaders.count(); ++nheader) {
        stream << "\\\n    ../" << lstHeaders.at(nheader);
    }
    stream << "\n\n";
    stream << "SOURCES += ";
    for (int nsrc=0; nsrc<lstSources.count(); ++nsrc) {
        stream << "\\\n    ../" << lstSources.at(nsrc);
    }

    pfile.close();
    cfile.close();
}

