#include <QtGui>

class CustomScene : public QGraphicsScene
{
public:
    CustomScene()
        { addItem(new QGraphicsEllipseItem(QRect(10, 10, 30, 30))); }

    void drawItems(QPainter *painter, int numItems, QGraphicsItem *items[],
                   const QStyleOptionGraphicsItem options[],
                   QWidget *widget = 0);
};

//! [0]
void CustomScene::drawItems(QPainter *painter, int numItems,
                            QGraphicsItem *items[],
                            const QStyleOptionGraphicsItem options[],
                            QWidget *widget)
{
    for (int i = 0; i < numItems; ++i) {
         // Draw the item
         painter->save();
         painter->setMatrix(items[i]->sceneMatrix(), true);
         items[i]->paint(painter, &options[i], widget);
         painter->restore();
     }
}
//! [0]

int main(int argv, char **args)
{
    QApplication app(argv, args);

    CustomScene scene;
    QGraphicsView view(&scene);

    view.show();

    return app.exec();
}
