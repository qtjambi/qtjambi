package com.trolltech.qtdesigner.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.*;

public class ObjectInspectorView extends ViewPart {
	private ObjectInspectorW objectinspector;

	public void createPartControl(Composite parent) {
		objectinspector = new ObjectInspectorW(parent, SWT.EMBEDDED);
	}

	public void setFocus() {
		objectinspector.setFocus();
	}
}
