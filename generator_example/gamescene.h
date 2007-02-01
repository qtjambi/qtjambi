#ifndef GAMESCENE_H
#define GAMESCENE_H

#include "gamegrammar.h"

#include <QtGui/QGraphicsView>

class QPainter ;
class QGraphicsItem ;
class QStyleOptionGraphicsItem ;
class GameObject ;

class GameScene: public QGraphicsView
{
    Q_OBJECT
public:
    GameScene(QWidget *parent = 0);
    ~GameScene();

    void addGameObject(AbstractGameObject *gameObject);
    void setEgoObject(AbstractGameObject *ego_object);    
    bool inProximityOfEgo(const AbstractGameObject *o) const;    
    void addToEgoInventory(AbstractGameObject *o);
    void lookAround();
    virtual void message(const QString &msg); 

    inline QList<AbstractGameObject *> egoInventory() const { return m_ego_inventory; }
    inline void removeFromEgoInventory(AbstractGameObject *o) { m_ego_inventory.removeAll(o); }
    inline bool egoHasInInventory(AbstractGameObject *o) { return m_ego_inventory.contains(o); }        
    inline QString description() const { return m_description; }
    inline QImage background() const { return m_current_background; }   
    inline bool blocked() const { return !m_message.isEmpty(); }
    inline qreal closestZ() const { return m_closest_z; }
    inline qreal farthestZ() const { return m_farthest_z; }
    inline qreal horizon() const { return m_horizon; }
    inline GameGrammar *grammar() const { return m_grammar; }

    inline void setDescription(const QString &description) { m_description = description; }
    inline void setBackground(const QImage &image) { m_current_background = image; }
    inline void setFarthestZ(qreal z) { m_farthest_z = z; }
    inline void setClosestZ(qreal z) { m_closest_z = z; }
    inline void setHorizon(qreal y) { m_horizon = y; }

    inline void addNameToGameObject(AbstractGameObject *o, const QString &other_name)
    {
        m_grammar->addNameToGameObject(o, other_name);
    }
   
protected:
    virtual void keyPressEvent(QKeyEvent *e);
    virtual void drawForeground(QPainter *painter, const QRectF &);
    virtual void drawBackground(QPainter *painter, const QRectF &);

private:
    GameGrammar *m_grammar;
    QString m_description;
    AbstractGameObject *m_ego_object;
    QList<AbstractGameObject *> m_ego_inventory;     
    QStringList m_message;
    QString m_current_input;
    QImage m_current_background;
    qreal m_horizon;
    qreal m_farthest_z;
    qreal m_closest_z;
};

#endif
