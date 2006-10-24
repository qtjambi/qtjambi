package com.trolltech.qtdesigner.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.*;

public class WidgetBoxView extends ViewPart {
	private WidgetBoxW widgetbox;

	public void createPartControl(Composite parent) {
		widgetbox = new WidgetBoxW(parent, SWT.EMBEDDED);
	}

	public void setFocus() {
		widgetbox.setFocus();
	}
}
