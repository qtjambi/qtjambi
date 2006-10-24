#ifndef %PRE_DEF%
#define %PRE_DEF%

#include <QtGui/QWidget>
#include "%UI_HDR%"

class %CLASS% : public %UI_CLASS%
{
    Q_OBJECT

public:
    %CLASS%(QWidget *parent = 0);
    ~%CLASS%();

private:
    Ui::%CLASS%Class ui;
};

#endif // %PRE_DEF%
