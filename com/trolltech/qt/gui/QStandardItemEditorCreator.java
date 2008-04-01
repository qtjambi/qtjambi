package com.trolltech.qt.gui;

import java.lang.reflect.Method;

import com.trolltech.qt.QNoNativeResourcesException;
import com.trolltech.qt.QtJambiInternal;
import com.trolltech.qt.core.QByteArray;

// Not actually generated, but just to make sure not all
// virtual calls go through Java
@com.trolltech.qt.QtJambiGeneratedClass
public class QStandardItemEditorCreator extends QItemEditorCreatorBase {

	public QStandardItemEditorCreator(Class<? extends QWidget> widgetType) {
		super((QPrivateConstructor) null);

		boolean createWidgetOverride = false;
		boolean valuePropertyNameOverride = false;
		try {
			Method createWidgetMethod = getClass().getMethod("createWidget", QWidget.class);
			Method valuePropertyNameMethod = getClass().getMethod("valuePropertyName");

			createWidgetOverride = QtJambiInternal.isImplementedInJava(createWidgetMethod);
			valuePropertyNameOverride = QtJambiInternal.isImplementedInJava(valuePropertyNameMethod);
		} catch (Throwable t) {
			throw new RuntimeException("Cannot construct QItemEditorCreator", t);
		}

		__qt_QStandardItemEditorCreator(widgetType, createWidgetOverride, valuePropertyNameOverride);
	}
	private native void __qt_QStandardItemEditorCreator(Class<? extends QWidget> widgetType, boolean createWidgetOverride, boolean valuePropertyNameOverride);

	@Override
	public QWidget createWidget(QWidget parent) {
		if (nativeId() == 0)
			throw new QNoNativeResourcesException("Function call on incomplete object of type: " +getClass().getName());
		return __qt_createWidget(nativeId(), parent.nativeId());
	}
	private native QWidget __qt_createWidget(long nativeId, long parentId);

	@Override
	public QByteArray valuePropertyName() {
		if (nativeId() == 0)
			throw new QNoNativeResourcesException("Function call on incomplete object of type: " +getClass().getName());
		return valuePropertyName(nativeId());
	}
	private native QByteArray valuePropertyName(long nativeId);
}
