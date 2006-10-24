package com.trolltech.qtproject.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;

import com.trolltech.qtproject.preferences.QtPreferencePage;
import com.trolltech.qtproject.utils.QtCDTUtils;

public class QtPropertyPage extends PropertyPage {

	private static final String QTVERSION = "com.trolltech.qtproject.properties.qtversion";
	private Combo versioncombo;

	public QtPropertyPage() {
		super();
	}
	
	public static String getQtDir(IProject pro) {
		try {
			String version = pro.getPersistentProperty(
					new QualifiedName("", QTVERSION));
			return QtPreferencePage.getQtVersionPath(version);
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void addControls(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		composite.setLayoutData(data);
		
		Label label = new Label(composite, SWT.NONE);
		label.setText("Use Qt Version: ");
		versioncombo = new Combo(composite, SWT.READ_ONLY);
		
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		versioncombo.setLayoutData(data);
	}

	private void loadPersistentSettings() {
		versioncombo.add("<Default>");
		
		String[] versions = QtPreferencePage.getQtVersions();
		
		String currentVersion = null;
		try {
			currentVersion = ((IProject) getElement()).getPersistentProperty(
					new QualifiedName("", QTVERSION));
		} catch (CoreException e) {
			e.printStackTrace();
			return;
		}
		
		versioncombo.select(0);
		if (versions == null)
			return;
		
		for (int i=0; i<versions.length; ++i) {
			versioncombo.add(versions[i]);
			if (versions[i].equals(currentVersion))
				versioncombo.select(i + 1);				
		}
	}
	
	private boolean savePersistentSettings() {
		try {
			((IProject) getElement()).setPersistentProperty(
					new QualifiedName("", QTVERSION),
					versioncombo.getItem(versioncombo.getSelectionIndex()));
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		addControls(composite);
		
		loadPersistentSettings();
		
		return composite;
	}

	protected void performDefaults() {
		versioncombo.select(0);
	}
	
	public boolean performOk() {
		if (savePersistentSettings()) {
			QtCDTUtils.updateQtDir((IProject)getElement());
			return true;
		}
		
		return false;
	}

}