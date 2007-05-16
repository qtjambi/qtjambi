#ifndef INJECTEDCODE_H
#define INJECTEDCODE_H

#include <QtCore/QtCore>
#include <QtGui/QtGui>
#include <QtSql/QtSql>
#include <QtXml/QtXml>

class SpinBoxHandler
{
public:
    void tryValidate(QAbstractSpinBox *spinBox, const QString &input, int i)
    {
        my_returned_string = input;
        my_returned_pos = i;
        my_returned_state = spinBox->validate(my_returned_string, my_returned_pos);
    }

    void tryFixup(QAbstractSpinBox *spinBox, const QString &input)
    {
        my_returned_string = input;
        spinBox->fixup(my_returned_string);
    }

public:
    QString my_returned_string;
    int my_returned_pos;
    QValidator::State my_returned_state;
};

class SpinBoxSubclass: public QAbstractSpinBox
{
public:
    virtual QValidator::State validate(QString &input, int &pos) const
    {
        this->my_received_pos = pos;
        this->my_received_string = input;
        pos += 13;
        input = "The silence of that " + input.left(5) + "less sleep";

        return QValidator::Intermediate;
    }

    virtual void fixup(QString &input) const
    {
        this->my_received_string = input;
        input = "And " + input.left(4) + " art dead";
    }

public:
    mutable QString my_received_string;
    mutable int my_received_pos;
};

class GraphicsSceneSubclass: public QGraphicsScene
{
public:
    void drawItems(QPainter *painter, int numItems, QGraphicsItem *items[], const QStyleOptionGraphicsItem options[], QWidget *widget);

     int numItems;
     QStyleOptionGraphicsItem firstStyleOption;
     QStyleOptionGraphicsItem secondStyleOption;
     QGraphicsItem *firstItem;
     QGraphicsItem *secondItem;
     int firstStyleOptionType;
     int secondStyleOptionType;
     int firstStyleOptionVersion;
     int secondStyleOptionVersion;
     QRectF firstBoundingRect;
     QRectF secondBoundingRect;
};

class TextCodecSubclass: public QTextCodec
{


public:
    QString callToUnicode(const QByteArray &ba, ConverterState *state) {
        return convertToUnicode(ba.data(), ba.size(), state);
    }

    QByteArray callFromUnicode(const QString &str, ConverterState *state) {
        return convertFromUnicode(str.data(), str.length(), state);
    }

    mutable ConverterState *receivedState;

protected:
    QString convertToUnicode(const char *data, int size, ConverterState *state) const {
        receivedState = state;

        QString returned;
        for (int i=0; i<size; ++i)
            returned += (data[i] == 'a' ? QLatin1Char('b') : QLatin1Char('a'));

        return returned;
    }

    QByteArray convertFromUnicode(const QChar *data, int size, ConverterState *state) const {
        receivedState = state;

        QByteArray returned;
        for (int i=0; i<size; ++i)
            returned += (data[i] == 's' ? 'a' : 's');

        return returned;
    }
};

class IODeviceSubclass: public QIODevice
{
public:
    IODeviceSubclass(int buffer_length) {
        buffer = new char[buffer_length];
        this->buffer_length = buffer_length;
    }

    qint64 callReadData() {
        return readData(buffer, buffer_length);
    }

    qint64 callWriteData(const QByteArray &data) {
        return writeData(data.data(), data.size());
    }

    qint64 callReadLineData() {
        return readLineData(buffer, buffer_length);
    }

protected:
    qint64 readData(char *data, qint64 maxSize) {
        Q_UNUSED(maxSize);
        char *inp = "I am a boy";
        for (int i=0; i<int(strlen(inp)); ++i)
            data[i] = inp[i];

        return strlen(inp);
    }

    qint64 writeData(const char *data, qint64 maxSize) {
        delete buffer;
        buffer = new char[maxSize];
        for (int i=0; i<maxSize; ++i)
            buffer[i] = data[i];

        return maxSize;
    }

    qint64 readLineData(char *data, qint64 maxSize) {
        Q_UNUSED(maxSize)
        char *inp = "Confucius say: Don't go outside with wet hair";
        for (int i=0; i<int(strlen(inp)); ++i)
            data[i] = inp[i];

        return strlen(inp);
    }

public:
    char *buffer;
    int buffer_length;

};

class PictureSubclass: public QPicture
{
public:
    void callSetData(const QByteArray &byteArray) {
        setData(byteArray.data(), byteArray.size());
    }
};

class GraphicsItemSubclass: public QGraphicsItem
{
public:
    void callPaint(QPainter *painter, const QStyleOptionGraphicsItem &option, QWidget *w)
    {
        paint(painter, &option, w);
    }

    void paint(QPainter *painter, const QStyleOptionGraphicsItem *option, QWidget *widget) {
        this->widget = widget;
        this->painter = painter;
        this->option = *option;

        painter->fillRect(0, 0, 50, 50, Qt::red);
    }

    QPainter *painter;
    QStyleOptionGraphicsItem option;
    QWidget *widget;
};

class AccessibleInterfaceSubclass: public QAccessibleInterface
{
public:
    int callNavigate(RelationFlag relation, int entry) {
        return navigate(relation, entry, &this->target);
    }

    virtual int navigate(RelationFlag relation, int entry, QAccessibleInterface **target) const {
        if (relation == Self) {
            *target = const_cast<AccessibleInterfaceSubclass *>(this);
            return entry;
        } else {
            *target = 0;
            return -1;
        }
    }

    QAccessibleInterface *target;
};

class SomeQObject: public QObject
{
    Q_OBJECT
public slots:
    void getAnUrl(const QUrl &url) {
        this->url = url;
    }

    void actionTriggered() {
        emit myActionTriggered();
    }

signals:
    void myActionTriggered();

public:
    QUrl url;
};

class ValidatorSubclass: public QValidator {
public:
    ValidatorSubclass() : QValidator(0) {}

    QString callFixup(const QString &input) {
        QString blah = input;
        fixup(blah);
        return blah;
    }

    State callValidate(QString *input, int *pos) {
        return validate(*input, *pos);
    }

    void fixup(QString &input) const {
        inputString = input;

        input = "somePrefix" + input;
    }

    State validate(QString &input, int &pos) const {
        inputString = input;
        inputPos = pos;

        input += "somePostfix";
        pos = inputString.length();

        if (inputString == "intermediate")
            return Intermediate;
        else if (inputString == "acceptable")
            return Acceptable;
        else
            return Invalid;
    }

    mutable QString inputString;
    mutable int inputPos;
};

class ImageIOHandlerSubclass: public QImageIOHandler
{
public:
    bool callRead(QImage *image) {
        return read(image);
    }

    bool read(QImage *image) {
        return image != 0 ? image->load("classpath:com/trolltech/examples/images/cheese.png") : true;
    }
};

class SqlTableModelSubclass: public QSqlTableModel
{
    Q_OBJECT
public:
    void emitBeforeInsert() {
        emit beforeInsert(myRecord);
    }

    void connectBeforeInsert() {
        connect(this, SIGNAL(beforeInsert(QSqlRecord &)), this, SLOT(receiveBeforeInsert(QSqlRecord &)));
    }

    QSqlRecord myRecord;

public slots:
    void receiveBeforeInsert(QSqlRecord &rec) {
        rec.append(QSqlField("cppInt", QVariant::Int));
        rec.setValue("cppInt", 1234);
    }

};

class XmlReaderSubclass: public QXmlReader
{
public:
    bool callFeature(const QString &name) {
        return feature(name, &myOk);
    }

    bool feature(const QString &name, bool *) const
    {
        myName = name;
        return (name == "true");
    }

    bool myOk;
    mutable QString myName;
};

class AccessibleTableInterfaceSubclass: public QAccessibleTableInterface
{
public:
    virtual void cellAtIndex(int index, int *row, int *column, int *rowSpan,
                             int *columnSpan, bool *isSelected);

    static void callCellAtIndex(AccessibleTableInterfaceSubclass *obj, int index, int *row, int *col, int *rowSpan, int *columnSpan, bool *isSelected);
};

#endif
