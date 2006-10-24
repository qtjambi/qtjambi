package com.trolltech.qtdesigner.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class ActionEditorView extends ViewPart {
	private ActionEditorW actioneditor;

	public void createPartControl(Composite parent) {
		actioneditor = new ActionEditorW(parent, SWT.EMBEDDED);
	}

	public void setFocus() {
		actioneditor.setFocus();
	}
}
