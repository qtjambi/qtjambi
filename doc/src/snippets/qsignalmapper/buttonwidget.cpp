#include <QtGui>

#include "buttonwidget.h"

//! [0]
ButtonWidget::ButtonWidget(QStringList texts, QWidget *parent)
    : QWidget(parent)
{
    signalMapper = new QSignalMapper(this);

    QGridLayout *gridLayout = new QGridLayout;
    for (int i = 0; i < texts.size(); ++i) {
	QPushButton *button = new QPushButton(texts[i]);
	connect(button, SIGNAL(clicked()), signalMapper, SLOT(map()));
//! [0] //! [1]
	signalMapper->setMapping(button, texts[i]);
	gridLayout->addWidget(button, i / 3, i % 3);
    }

    connect(signalMapper, SIGNAL(mapped(const QString &)),
//! [1] //! [2]
            this, SIGNAL(clicked(const QString &)));

    setLayout(gridLayout);
}
//! [2]
