/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $TROLLTECH_DUAL_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#ifndef JAMBI_LANGUAGE_PLUGIN_H
#define JAMBI_LANGUAGE_PLUGIN_H

#include "jambilanguageextension.h"

#include <QtDesigner/QtDesigner>
#include <QtDesigner/QExtensionFactory>
#include <QtDesigner/QDesignerResourceBrowserInterface>

class JambiLanguagePlugin: public QObject, public QDesignerFormEditorPluginInterface
{
    Q_OBJECT
    Q_INTERFACES(QDesignerFormEditorPluginInterface)

public:
    JambiLanguagePlugin();
    virtual ~JambiLanguagePlugin();

    virtual bool isInitialized() const;
    virtual void initialize(QDesignerFormEditorInterface *core);
    virtual QAction *action() const;

    virtual QDesignerFormEditorInterface *core() const;
private:
    QDesignerFormEditorInterface *m_core;
    bool m_vmLoaded;
};


class JambiExtraInfoExtension : public QObject, QDesignerExtraInfoExtension
{
    Q_OBJECT
    Q_INTERFACES(QDesignerExtraInfoExtension)
public:
    JambiExtraInfoExtension(QWidget *widget, QDesignerFormEditorInterface *core);

    virtual QDesignerFormEditorInterface *core() const { return m_core; }
    virtual QWidget *widget() const { return m_widget; }

    virtual bool saveUiExtraInfo(DomUI *ui);
    virtual bool loadUiExtraInfo(DomUI *ui);

    virtual bool saveWidgetExtraInfo(DomWidget *ui_widget);
    virtual bool loadWidgetExtraInfo(DomWidget *ui_widget);

private:
    void fiddleWith(DomProperty *property);
    void fiddleWith(DomSpacer *spacer);
    void fiddleWith(DomWidget *widget);
    void fiddleWith(DomLayoutItem *layoutItem);
    void fiddleWith(DomLayout *layout);

    QWidget *m_widget;
    QDesignerFormEditorInterface *m_core;
};


class JambiLanguage: public QObject, public JambiLanguageExtension
{
    Q_OBJECT
    Q_INTERFACES(QDesignerLanguageExtension JambiLanguageExtension)

public:
    JambiLanguage(QObject *parent = 0);
    virtual ~JambiLanguage();

    virtual QDialog *createFormWindowSettingsDialog(QDesignerFormWindowInterface *formWindow, QWidget *parentWidget);

    virtual QDesignerResourceBrowserInterface *createResourceBrowser(QWidget *parentWidget);
    virtual bool isLanguageResource(const QString &path) const;

    virtual QString classNameOf(QObject *object) const;
    virtual QString enumerator(const QString &name) const;
    virtual QString neutralEnumerator(const QString &name) const;

    virtual QString uiExtension() const { return "jui"; }

    virtual QDialog *createPromotionDialog(QDesignerFormEditorInterface *, const QString &,
                                           QString *, QWidget *);
    virtual QDialog *createPromotionDialog(QDesignerFormEditorInterface *,QWidget *);

    virtual bool signalMatchesSlot(const QString &signal, const QString &slot) const;

    virtual QString widgetBoxContents() const;

private:

};

class JambiExtensionFactory: public QExtensionFactory
{
    Q_OBJECT

public:
    JambiExtensionFactory(JambiLanguagePlugin *plugin, QExtensionManager *parent);
    virtual ~JambiExtensionFactory();

protected:
    virtual QObject *createExtension(QObject *object, const QString &iid, QObject *parent) const;

private:
    JambiLanguagePlugin *m_jambi;
    QPointer<QDesignerLanguageExtension> m_language;
};


#endif // JAMBI_LANGUAGE_PLUGIN_H
