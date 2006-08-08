package com.trolltech.qtjambi;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

public class QtJambiPropertyPage extends PropertyPage {
	
	private static final String JUICLABEL = "Juic Location:";
	public static final String JUICPROPERTY = "JuicLocationProperty";
	
    public Text juicPathText;

	public QtJambiPropertyPage() {
		super();
	}

	private void addJuicSection(Composite parent) {
		IJavaProject jpro = (IJavaProject)getElement();
		String currentPath = null;
		try {
			currentPath = jpro.getResource().getPersistentProperty(
					new QualifiedName("", JUICPROPERTY));
		} catch (Exception e) {
						
		}
		
		Label pathLabel = new Label(parent, SWT.NONE);
		pathLabel.setText(JUICLABEL);
		juicPathText = new Text(parent, SWT.SINGLE|SWT.BORDER);
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		juicPathText.setLayoutData(gridData);
		
		if (currentPath == null)
			performDefaults();
		else
			juicPathText.setText(currentPath);
	}

	protected Control createContents(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		addJuicSection(composite);
		return composite;
	}

	protected void performDefaults() {
		juicPathText.setText("juic");		
	}
	
	public boolean performOk() {
		try {
			IJavaProject jpro = (IJavaProject)getElement();
			jpro.getResource().setPersistentProperty(
				new QualifiedName("", JUICPROPERTY),
				juicPathText.getText());
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}