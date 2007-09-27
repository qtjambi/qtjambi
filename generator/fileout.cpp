/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $CPP_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#include "fileout.h"
#include "reporthandler.h"

#include <QFileInfo>
#include <QDir>

bool FileOut::dummy = false;
bool FileOut::diff = false;

FileOut::FileOut(QString n):
    name(n),
    stream(&tmp),
    isDone(false)
{}

static int* lcsLength(QList<QByteArray> a, QList<QByteArray> b) {
    const int height = a.size() + 1;
    const int width = b.size() + 1;

    int *res = new int[width * height];

    for (int row=0; row<height; row++) {
        res[width * row] = 0;
    }
    for (int col=0; col<width; col++) {
        res[col] = 0;
    }

    for (int row=1; row<height; row++) {
        for (int col=1; col<width; col++) {
            
            if (a[row-1] == b[col-1])
                res[width * row + col] = res[width * (row-1) + col-1] + 1;
            else
                res[width * row + col] = qMax(res[width * row     + col-1],
                                              res[width * (row-1) + col]);
        }
    }
    return res;
}

enum Type {Add, Delete, Unchanged};

struct Unit
{
    Unit(Type type, int pos) :
        type(type),
        start(pos),
        end(pos)
    {}

    Type type;
    int start;
    int end;

    void print(QList<QByteArray> a, QList<QByteArray> b){
        {
            if (type == Unchanged) {
                if ((end - start) > 9) {
                    for (int i = start; i <= start+2; i++)
                        printf("  %s\n", a[i].data());
                    printf("=\n= %d more lines\n=\n", end - start - 6);
                    for (int i = end-2; i <= end; i++) 
                        printf("  %s\n", a[i].data());
                }
                else 
                    for (int i = start; i <= end; i++) 
                        printf("  %s\n", a[i].data());
            }
            else if(type == Add) {
                for (int i = start; i <= end; i++){
#ifdef Q_OS_LINUX
                    printf("\033[32m+ %s\033[0m\n", b[i].data());
#else
                    printf("+ %s\n", b[i].data());
#endif
                }
            } 
            else if (type == Delete) {
                for (int i = start; i <= end; i++) {
#ifdef Q_OS_LINUX
                    printf("\033[31m- %s\033[0m\n", a[i].data());
#else
                    printf("- %s\n", b[i].data());
#endif
                }
            }    
        }
    }
};

static QList<Unit*> *unitAppend(QList<Unit*> *res, Type type, int pos)
{
    if (res == 0) {
        res = new QList<Unit*>;
        res->append(new Unit(type, pos));
        return res;
    }

    Unit *last = res->last();
    if (last->type == type) {
        last->end = pos;
    } else {
        res->append(new Unit(type, pos));
    }
    return res;
}

static QList<Unit*> *diffHelper(int *lcs, QList<QByteArray> a, QList<QByteArray> b, int row, int col) {
    if (row>0 && col>0 && (a[row-1] == b[col-1])) {
        return unitAppend(diffHelper(lcs, a, b, row-1, col-1), Unchanged, row-1);
    }
    else {
        int width = b.size()+1;
        if ((col > 0) && ((row==0) || 
                          lcs[width * row + col-1] >= lcs[width * (row-1) + col]))
            {
                return unitAppend(diffHelper(lcs, a, b, row, col-1), Add, col-1);
            }
        else if((row > 0) && ((col==0) ||
                              lcs[width * row + col-1] < lcs[width * (row-1) + col])){ 
            return unitAppend(diffHelper(lcs, a, b, row-1, col), Delete, row-1);;
        }
    }
    return 0;
}

static void diff(QList<QByteArray> a, QList<QByteArray> b) {
    QList<Unit*> *res = diffHelper(lcsLength(a, b), a, b, a.size(), b.size());
    for (int i=0; i < res->size(); i++) {
        Unit *unit = res->at(i);
        unit->print(a, b);
        delete(unit);
    }
    delete(res);
}


bool FileOut::done() {
    Q_ASSERT( !isDone );
    isDone = true;
    bool fileEqual = false;
    QFile fileRead(name);
    QFileInfo info(fileRead);
    stream.flush();
    QByteArray original;
    if (info.exists() && (diff || (info.size() == tmp.size()))) {
        if ( !fileRead.open(QIODevice::ReadOnly) ) {
            ReportHandler::warning(QString("failed to open file '%1' for reading")
                                   .arg(fileRead.fileName()));
            return false;
        }
        
        original = fileRead.readAll();
        fileRead.close();
        fileEqual = (original == tmp);
    }
    
    if( !fileEqual ) {
        if( !FileOut::dummy ) {
            QDir dir(info.absolutePath());
            dir.mkpath(dir.absolutePath());

            QFile fileWrite(name);
            if (!fileWrite.open(QIODevice::WriteOnly)) {
                ReportHandler::warning(QString("failed to open file '%1' for writing")
                                       .arg(fileWrite.fileName()));
                return false;
            }
            stream.setDevice(&fileWrite);
            stream << tmp;
        }
        if (diff) {
            printf("File: %s\n", qPrintable(name));
         
            ::diff(original.split('\n'), tmp.split('\n'));
            
            printf("\n");
        }
        return true;
    }
    return false;
}
