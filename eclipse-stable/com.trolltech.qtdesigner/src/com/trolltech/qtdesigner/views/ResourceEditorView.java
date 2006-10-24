package com.trolltech.qtdesigner.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.*;

public class ResourceEditorView extends ViewPart {
	private ResourceEditorW resourceeditor;

	public void createPartControl(Composite parent) {
		resourceeditor = new ResourceEditorW(parent, SWT.EMBEDDED);
	}

	public void setFocus() {
		resourceeditor.setFocus();
	}
}
