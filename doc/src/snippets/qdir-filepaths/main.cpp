#include <QDir>
#include <iostream>

int main(int argc, char *argv[])
{
    QDir directory("Documents/Letters");
    QString path = directory.filePath("contents.txt");
    QString absolutePath = directory.absoluteFilePath("contents.txt");

    std::cout << qPrintable(directory.dirName()) << std::endl;
    std::cout << qPrintable(path) << std::endl;
    std::cout << qPrintable(absolutePath) << std::endl;
    return 0;
}
