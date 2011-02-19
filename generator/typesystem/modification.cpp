
#include "modification.h"

QString Modification::accessModifierString() const {
    if (isPrivate()) return "private";
    if (isProtected()) return "protected";
    if (isPublic()) return "public";
    if (isFriendly()) return "friendly";
    return QString();
}
