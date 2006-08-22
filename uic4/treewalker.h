/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ Trolltech AS. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#ifndef TREEWALKER_H
#define TREEWALKER_H

class DomUI;
class DomLayoutDefault;
class DomLayoutFunction;
class DomTabStops;
class DomLayout;
class DomLayoutItem;
class DomWidget;
class DomSpacer;
class DomColor;
class DomColorGroup;
class DomPalette;
class DomFont;
class DomPoint;
class DomRect;
class DomSizePolicy;
class DomSize;
class DomDate;
class DomTime;
class DomDateTime;
class DomProperty;
class DomCustomWidgets;
class DomCustomWidget;
class DomAction;
class DomActionGroup;
class DomActionRef;
class DomImages;
class DomImage;
class DomItem;
class DomIncludes;
class DomInclude;
class DomString;
class DomResourcePixmap;
class DomResources;
class DomResource;
class DomConnections;
class DomConnection;
class DomConnectionHints;
class DomConnectionHint;

struct TreeWalker
{
    inline virtual ~TreeWalker() {}

    virtual void acceptUI(DomUI *ui);
    virtual void acceptLayoutDefault(DomLayoutDefault *layoutDefault);
    virtual void acceptLayoutFunction(DomLayoutFunction *layoutFunction);
    virtual void acceptTabStops(DomTabStops *tabStops);
    virtual void acceptCustomWidgets(DomCustomWidgets *customWidgets);
    virtual void acceptCustomWidget(DomCustomWidget *customWidget);
    virtual void acceptLayout(DomLayout *layout);
    virtual void acceptLayoutItem(DomLayoutItem *layoutItem);
    virtual void acceptWidget(DomWidget *widget);
    virtual void acceptSpacer(DomSpacer *spacer);
    virtual void acceptColor(DomColor *color);
    virtual void acceptColorGroup(DomColorGroup *colorGroup);
    virtual void acceptPalette(DomPalette *palette);
    virtual void acceptFont(DomFont *font);
    virtual void acceptPoint(DomPoint *point);
    virtual void acceptRect(DomRect *rect);
    virtual void acceptSizePolicy(DomSizePolicy *sizePolicy);
    virtual void acceptSize(DomSize *size);
    virtual void acceptDate(DomDate *date);
    virtual void acceptTime(DomTime *time);
    virtual void acceptDateTime(DomDateTime *dateTime);
    virtual void acceptProperty(DomProperty *property);
    virtual void acceptImages(DomImages *images);
    virtual void acceptImage(DomImage *image);
    virtual void acceptIncludes(DomIncludes *includes);
    virtual void acceptInclude(DomInclude *incl);
    virtual void acceptAction(DomAction *action);
    virtual void acceptActionGroup(DomActionGroup *actionGroup);
    virtual void acceptActionRef(DomActionRef *actionRef);
    virtual void acceptConnections(DomConnections *connections);
    virtual void acceptConnection(DomConnection *connection);
    virtual void acceptConnectionHints(DomConnectionHints *connectionHints);
    virtual void acceptConnectionHint(DomConnectionHint *connectionHint);
};

#endif // TREEWALKER_H
