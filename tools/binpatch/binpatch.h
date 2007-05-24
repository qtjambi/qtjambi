#ifndef BINPATCH_H
#define BINPATCH_H

typedef unsigned long ulong;
typedef unsigned int uint;

class BinPatch
{
public:
    BinPatch(const char *file)
        : useLength(false), insertReplace(false)
    {
        strcpy(endTokens, "");
        strcpy(fileName, file);
    }

    void enableUseLength(bool enabled)
    { useLength = enabled; }
    void enableInsertReplace(bool enabled)
    { insertReplace = enabled; }
    void setEndTokens(const char *tokens)
    { strcpy(endTokens, tokens); }

    bool patch(const char *oldstr, const char *newstr);

private:
    long getBufferStringLength(char *data, char *end);
    bool endsWithTokens(const char *data);

    bool patchHelper(char *inbuffer, const char *oldstr, 
        const char *newstr, size_t len, long *rw);

    bool useLength;
    bool insertReplace;
    char endTokens[1024];
    char fileName[1024];
};

#endif