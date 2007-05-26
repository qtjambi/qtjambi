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

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

static const char ekey_source[512 + 12]     = "qt_qevalkey=\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";


static char ekey_result[512 + 12]     = "qt_qevalkey=\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";

int file_length(FILE *f)
{
  int pos;
  int end;

  pos = ftell (f);
  fseek (f, 0, SEEK_END);
  end = ftell (f);
  fseek (f, pos, SEEK_SET);

  return end;
}

int patchLibrary(const char *name)
{
    printf(" - patching %s...\n", name);
    
    FILE *f = fopen(name, "r");
    
    if (!f) {
      printf("failed to open file...\n");
      return 0;
    }

    int length = file_length(f);

    char *buffer = (char *) malloc(length * sizeof(char));
    int read = fread(buffer, sizeof(char), length, f);
    fclose(f);

    int success = 1;
    if (read == length) {
        int iterations = length - (512+12);
        for (int i=0; i<iterations; ++i) {
            char *c = strstr(buffer+i, ekey_source);
            if (c) {
                memcpy(c, ekey_result, 512+12);
	    }
        }

	f = fopen(name, "w");
	if (f) {
	    int wrote = fwrite(buffer, sizeof(char), length, f);
	    if (wrote != length) {
	        printf("failed to write patched binary...\n");
	        success = false;
	    }
	    fclose(f);
	} else {
	    success = 0;
	    printf(" - failed to write file...\n");
	}

    } else {
      printf(" - failed to read file...\n");
      success = 0;
    }

    free(buffer);
    return success;;
}


int main(int, char **) {

#if defined(__linux__) || defined(__linux)
    const char *core("lib/libQtCore.so.4");
    const char *gui("lib/libQtGui.so.4");
#elif defined(__APPLE__) && (defined(__GNUC__) || defined(__xlC__) || defined(__xlc__))
    const char *core("lib/libQtCore.4.dylib");
    const char *gui("lib/libQtGui.4.dylib");
#else
    const char *core("bin/QtCore4.dll");
    const char *gui("bin/QtGui4.dll");
#endif

    FILE *abort = fopen(".patched", "r");
    if (abort) {
        fclose(abort);
        return 0;
    }

    printf("This is the Qt Jambi evaluation version\n");

    char key[64];
    memset(key, 0, 64);
    char *read = key;

    do {
        printf("Insert evaluation key (XXXXX-XXX-XXX-XXX-XXXXXX-XXXXX-XXXX):\n");
        read = fgets(key, 64, stdin);
        read[strlen(read) - 1] = '\0';
    } while (read[5] != '-' || read[9] != '-' || read[13] != '-'
             || read[17] != '-' || read[23] != '-' || read[29] != '-');

    memcpy(ekey_result + 12, read, 64);

    const char *names[] = { core, gui, 0 };
    int i = 0;
    while (names[i]) {
        int ok = patchLibrary(names[i]);
        if (!ok)
            return 1;
        ++i;
    }

    FILE *f = fopen(".patched", "w");
    if (f) {
        fprintf(f, "key: %s\n", read);
        fflush(f);
        fclose(f);
    }

    return 0;
}
