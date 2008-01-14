/*   Ported from: src.corelib.io.qabstractfileengine.cpp
<snip>
//! [0]
        class ZipEngineHandler : public QAbstractFileEngineHandler
        {
        public:
            QAbstractFileEngine *create(const QString &fileName) const;
        };

        QAbstractFileEngine *ZipEngineHandler::create(const QString &fileName) const
        {
            // ZipEngineHandler returns a ZipEngine for all .zip files
            return fileName.toLower().endsWith(".zip") ? new ZipEngine(fileName) : 0;
        }

        int main(int argc, char **argv)
        {
            QApplication app(argc, argv);

            ZipEngineHandler engine;

            MainWindow window;
            window.show();

            return app.exec();
        }
//! [0]


//! [1]
        QAbstractSocketEngine *ZipEngineHandler::create(const QString &fileName) const
        {
            // ZipEngineHandler returns a ZipEngine for all .zip files
            return fileName.toLower().endsWith(".zip") ? new ZipEngine(fileName) : 0;
        }
//! [1]


//! [2]
    QAbstractFileEngineIterator *
    CustomFileEngine::beginEntryList(QDir::Filters filters, const QStringList &filterNames)
    {
        return new CustomFileEngineIterator(filters, filterNames);
    }
//! [2]


//! [3]
    class CustomIterator : public QAbstractFileEngineIterator
    {
    public:
        CustomIterator(const QStringList &nameFilters, QDir::Filters filters)
            : QAbstractFileEngineIterator(nameFilters, filters), index(0)
        {
            // In a real iterator, these entries are fetched from the
            // file system based on the value of path().
            entries << "entry1" << "entry2" << "entry3";
        }

        bool hasNext() const
        {
            return index < entries.size() - 1;
        }

        QString next()
        {
           if (!hasNext())
               return QString();
           ++index;
           return currentFilePath();
        }

        QString currentFilePath()
        {
            return entries.at(index);
        }

    private:
        QStringList entries;
        int index;
    };
//! [3]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_corelib_io_qabstractfileengine {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        class ZipEngineHandler : public QAbstractFileEngineHandler
        {
        public:
            QAbstractFileEngine reate(StringsileName);
        };

        QAbstractFileEngine ipEngineHandler.create(StringsileName)
        {
            // ZipEngineHandler returns a ZipEngine for all .zip files
            return fileName.toLower().endsWith(".zip") ? new ZipEngine(fileName) : 0;
        }

        int main(int argc, char *rgv)
        {
            QApplication app(argc, argv);

            ZipEngineHandler engine;

            MainWindow window;
            window.show();

            return app.exec();
        }
//! [0]


//! [1]
        QAbstractSocketEngine ipEngineHandler.create(StringsileName)
        {
            // ZipEngineHandler returns a ZipEngine for all .zip files
            return fileName.toLower().endsWith(".zip") ? new ZipEngine(fileName) : 0;
        }
//! [1]


//! [2]
    QAbstractFileEngineIterator *
    CustomFileEngine.beginEntryList(QDir.Filters filters, List<String> ilterNames)
    {
        return new CustomFileEngineIterator(filters, filterNames);
    }
//! [2]


//! [3]
    class CustomIterator : public QAbstractFileEngineIterator
    {
    public:
        CustomIterator(List<String> ameFilters, QDir.Filters filters)
            : QAbstractFileEngineIterator(nameFilters, filters), index(0)
        {
            // In a real iterator, these entries are fetched from the
            // file system based on the value of path().
            entries << "entry1" << "entry2" << "entry3";
        }

        booleanshasNext()
        {
            return index < entries.size() - 1;
        }

        Stringsnext()
        {
           if (!hasNext())
               return QString();
           ++index;
           return currentFilePath();
        }

        StringscurrentFilePath()
        {
            return entries.at(index);
        }

    private:
        List<String> entries;
        int index;
    };
//! [3]


    }
}
