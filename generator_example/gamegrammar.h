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

#ifndef GAMEGRAMMAR_H
#define GAMEGRAMMAR_H

#include <QtCore/QString>
#include <QtCore/QHash>
#include <QtCore/QStringList>

class GameScene ;
class GameAction ;
class AbstractGameObject ;

class GameGrammar
{
public:
    GameGrammar(GameScene *scene);
    virtual ~GameGrammar();

    void registerGameObject(AbstractGameObject *gameObject);
    void addNameToGameObject(AbstractGameObject *gameObject, const QString &other_name);
    void addVerb(const QString &verb, GameAction *action)
    {
        m_actions[verb] = action;
    }

    virtual void parse(const QString &command);

protected:
    virtual GameAction *command();
    virtual GameAction *verb();
    virtual AbstractGameObject *object();
    
    virtual bool filler();
    virtual bool and_token();
    virtual QString currentToken(int token_count = 1) const;
    virtual void nextToken();

    QHash<QString, AbstractGameObject *> m_objects;
    QHash<QString, GameAction *> m_actions;
    GameScene *m_scene;
    QStringList m_current_command;
};

#endif
