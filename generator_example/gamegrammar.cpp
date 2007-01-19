#include "gamegrammar.h"

#include "useaction.h"
#include "lookaction.h"
#include "pickupaction.h"
#include "gamescene.h"
#include "abstractgameobject.h"

GameGrammar::GameGrammar(GameScene *scene) : m_scene(scene)
{
    addVerb("use", new UseAction());
    addVerb("look", new LookAction());
    addVerb("take", new PickUpAction());
    addVerb("pick", new PickUpAction());
}

GameGrammar::~GameGrammar()
{
}

void GameGrammar::parse(const QString &c) 
{
    m_current_command = c.toLower().split(' ');

    GameAction *action = command();
    if (action == 0) {
        m_scene->message("You can't do that");
    } else {
        action->perform(m_scene);
    }
}

void GameGrammar::registerGameObject(AbstractGameObject *gameObject)
{
    if (gameObject != 0) {
        m_objects[gameObject->name()] = gameObject;
        
        QStringList names = gameObject->otherNames();
        foreach (QString name, names) {
            m_objects[name] = gameObject;
        }
    } else {
        qWarning("GameGrammar::registerGameObject: Tried to add null object to grammar");
    }
}

void GameGrammar::addNameToGameObject(AbstractGameObject *gameObject, const QString &other_name)
{
    if (gameObject != 0) {
        m_objects[other_name] = gameObject;
    } else {
        qWarning("GameGrammar::addNameToGameObject: Tried to add null object to grammar");
    }
}

QString GameGrammar::currentToken(int i) const 
{
    QString returned;
    if (m_current_command.size() < i) 
        return QString();

    for (int j=0; j<i; ++j) {
        if (j > 0) returned += " ";
        returned += m_current_command.at(j);
    }

    return returned;
}

void GameGrammar::nextToken()
{
    m_current_command.pop_front();
}

GameAction *GameGrammar::command() 
{
    GameAction *action = verb();
    if (action == 0) 
        return 0;

    while (filler()) ;
    
    while (AbstractGameObject *s = object()) action->addSubject(s);

    while (filler()) ;

    while (AbstractGameObject *o = object()) action->addObject(o);

    return m_current_command.isEmpty() ? action : 0;
}

GameAction *GameGrammar::verb() 
{    
    GameAction *a = m_actions.value(currentToken(), 0);
    if (a != 0) {
        nextToken();
        return a->clone();
    } else {
        return 0;
    }
}

AbstractGameObject *GameGrammar::object() 
{    
    AbstractGameObject *gameObject = 0;    
    int i=1;
    while (gameObject == 0 && !currentToken(i).isEmpty()) 
    {
        gameObject = m_objects.value(currentToken(i++), 0);
    }

    if (gameObject != 0 && gameObject->isVisible() 
        && (m_scene->egoHasInInventory(gameObject) || m_scene->inProximityOfEgo(gameObject))) {
        while (--i) nextToken();
        while (and()) ;
        return gameObject;
    } else {
        return 0;
    }
}

bool GameGrammar::and() 
{
    if (currentToken() == "and"
        || currentToken() == ",") {
        nextToken();
        return true;
    } else {
        return false;
    }
}

bool GameGrammar::filler() 
{
    if (currentToken() == "to" 
        || currentToken() == "with"
        || currentToken() == "in"
        || currentToken() == "at"
        || currentToken() == "up") {
        nextToken();
        return true;
    } else {
        return false;
    }
}