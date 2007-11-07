#include <QTimer>

class Foo : public QObject
{
public:
    Foo();
};

Foo::Foo()
{
//! [0]
    QTimer *timer = new QTimer(this);
//! [0] //! [1]
    connect(timer, SIGNAL(timeout()), this, SLOT(updateCaption()));
//! [1] //! [2]
    timer->start(1000);
//! [2]

//! [3]
    QTimer::singleShot(200, this, SLOT(updateCaption()));
//! [3]

    {
    // ZERO-CASE
//! [4]
    QTimer *timer = new QTimer(this);
//! [4] //! [5]
    connect(timer, SIGNAL(timeout()), this, SLOT(processOneThing()));
//! [5] //! [6]
    timer->start();
//! [6]
    }
}

int main()
{
    
}
