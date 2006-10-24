package com.trolltech.qtdesigner.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.*;

public class SignalSlotEditorView extends ViewPart {
	private SignalSlotEditorW signalsloteditor;

	public void createPartControl(Composite parent) {
		signalsloteditor = new SignalSlotEditorW(parent, SWT.EMBEDDED);
	}

	public void setFocus() {
		signalsloteditor.setFocus();
	}
}
