#ifndef INTERFACES_H
#define INTERFACES_H

#include <QtGui/QtGui>

class SetupLayout
{
public:
    static void setupLayout(QLayout *layout) 
    {
        QPushButton *button1 = new QPushButton("Test", layout->parentWidget());
        QPushButton *button2 = new QPushButton("Test2", layout->parentWidget());
        QSpacerItem *spacer = new QSpacerItem(10, 10);
        
        layout->addWidget(button1);
        layout->addItem(spacer);
        layout->addWidget(button2);        

        {
            QHBoxLayout *other_layout = new QHBoxLayout;
            QPushButton *button3 = new QPushButton("Test3", layout->parentWidget());
            QSpacerItem *spacer2 = new QSpacerItem(5, 5);
            QPushButton *button4 = new QPushButton("Test4", layout->parentWidget());
            other_layout->addWidget(button3);
            other_layout->addItem(spacer2);
            other_layout->addWidget(button4);

            layout->addItem(other_layout);
        }

    }
};

#endif

