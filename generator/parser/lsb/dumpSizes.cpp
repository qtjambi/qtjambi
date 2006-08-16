#include <stdio.h>

typedef unsigned short ushort;
typedef unsigned int uint;

struct QMetaObject
{
    struct {
        const QMetaObject *superdata;
        const char *stringdata;
        const uint *data;
        const QMetaObject **extradata;
    } d;
};

struct QBasicAtomic
{
    volatile int i;
};

struct N10QByteArray4DataE {
    QBasicAtomic ref;
    int alloc, size;
    char *data;
    char array[1];
};

struct N7QString4NullE
{
};

struct N7QString4DataE {
    QBasicAtomic ref;
    int alloc, size;
    ushort *data;
    ushort clean : 1;
    ushort simpletext : 1;
    ushort righttoleft : 1;
    ushort asciiCache : 1;
    ushort reserved : 12;
    ushort array[1];
};

struct N9QListData4DataE {
    QBasicAtomic ref;
    int alloc, begin, end;
    uint sharable : 1;
    void *array[1];
};

struct QHashData
{
    struct Node {
        Node *next;
        uint h;
    };

    Node *fakeNext;
    Node **buckets;
    QBasicAtomic ref;
    int size;
    int nodeSize;
    short userNumBits;
    short numBits;
    int numBuckets;
    uint sharable : 1;
};

struct QMapData
{
    struct Node {
        Node *backward;
        Node *forward[1];
    };

    enum { LastLevel = 11, Sparseness = 3 };

    Node *backward;
    Node *forward[QMapData::LastLevel + 1];
    QBasicAtomic ref;
    int topLevel;
    int size;
    uint randomBits;
    uint insertInOrder : 1;
    uint sharable : 1;
};

struct QVectorData
{
    QBasicAtomic ref;
    int alloc;
    int size;
    uint sharable : 1;
};

struct QLinkedListData
{
    QLinkedListData *n, *p;
    QBasicAtomic ref;
    int size;
    uint sharable : 1;
};

static char *archId = 0;

template<typename T>
void dumpSize(const char *className)
{
    printf("INSERT INTO ArchType (ATaid, ATtid, ATsize) values "
             "(%s, (SELECT Tid FROM Type WHERE Tname = '%s'), %d);\n",
            archId, className, int(sizeof(T)));
}

int main(int argc, char *argv[])
{
    if (argc != 2) {
        fprintf(stderr, "Usage: %s architectureId\n"
                "    architectureId: A valid Aid from the LSB Architecture table\n", argv[0]);
        return 1;
    }

    archId = argv[1];

    dumpSize<QMetaObject>("QMetaObject");
    dumpSize<N10QByteArray4DataE>("N10QByteArray4DataE");
    dumpSize<N7QString4NullE>("N7QString4NullE");
    dumpSize<N7QString4DataE>("N7QString4DataE");
    dumpSize<N9QListData4DataE>("N9QListData4DataE");
    dumpSize<QHashData>("QHashData");
    dumpSize<QMapData>("QMapData");
    dumpSize<QVectorData>("QVectorData");
    dumpSize<QLinkedListData>("QLinkedListData");

    return 0;
}

