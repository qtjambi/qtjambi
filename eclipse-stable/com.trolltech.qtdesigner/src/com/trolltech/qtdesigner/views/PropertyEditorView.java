package com.trolltech.qtdesigner.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class PropertyEditorView extends ViewPart {
	private PropertyEditorW propertyeditor;

	public void createPartControl(Composite parent) {
		propertyeditor = new PropertyEditorW(parent, SWT.EMBEDDED);
	}
	
	public void setFocus() {
		propertyeditor.setFocus();
	}
}
