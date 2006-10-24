package com.trolltech.qtproject.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.trolltech.qtproject.QtProConstants;

public class QtModulesWizardPage extends WizardPage {	
	private Button qtCoreBtn;
	private Button qtGuiBtn;
	private Button qtSqlBtn;
	private Button qtXmlBtn;
	private Button qtNetworkBtn;
	private Button qtSvgBtn;	
	private Button qtOpenGLBtn;
	private Button qt3SupportBtn;
	
	private boolean hasInitialized;
	private int selectedModules;
	private int requiredModules;
	
	public QtModulesWizardPage(String pageName) {
		super(pageName);
		setDescription("Select the Qt modules for the project.");
		setTitle("Qt Modules");
		selectedModules = 0;
		requiredModules = 0;
		hasInitialized = false;
	}
	
	public void setSelectedModules(int mods)
	{
		selectedModules = mods;
		refreshSelectedModules();
	}
	
	public void setRequiredModules(int mods)
	{
		requiredModules = mods;
		refreshSelectedModules();
	}
	
	public int getSelectedModules()
	{
		selectedModules = 0;
		if (qtCoreBtn.getSelection()) selectedModules |= QtProConstants.QtCore;
		if (qtGuiBtn.getSelection()) selectedModules |= QtProConstants.QtGui;
		if (qtSqlBtn.getSelection()) selectedModules |= QtProConstants.QtSql;
		if (qtXmlBtn.getSelection()) selectedModules |= QtProConstants.QtXml;
		if (qtNetworkBtn.getSelection()) selectedModules |= QtProConstants.QtNetwork;
		if (qtSvgBtn.getSelection()) selectedModules |= QtProConstants.QtSvg;		
		if (qtOpenGLBtn.getSelection()) selectedModules |= QtProConstants.QtOpenGL;
		if (qt3SupportBtn.getSelection()) selectedModules |= QtProConstants.Qt3Support;
		return selectedModules;
	}
	
	public String getModules()
	{
		String modules = "";
		if (qtCoreBtn.getSelection()) modules += "core ";
		if (qtGuiBtn.getSelection()) modules += "gui ";
		if (qtSqlBtn.getSelection()) modules += "sql ";
		if (qtXmlBtn.getSelection()) modules += "xml ";
		if (qtNetworkBtn.getSelection()) modules += "network ";
		if (qtSvgBtn.getSelection()) modules += "svg ";
		if (qtOpenGLBtn.getSelection()) modules += "opengl ";
		if (qt3SupportBtn.getSelection()) modules += "qt3support ";
		
		return modules;
	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		mainComposite.setLayout(layout);
		
		Group moduleGroup = new Group(mainComposite, SWT.PUSH);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
	    gridData.grabExcessHorizontalSpace = true;
	    gridData.horizontalAlignment = GridData.FILL;
	    gridData.verticalAlignment = GridData.BEGINNING;
	    moduleGroup.setLayoutData(gridData);		
	    moduleGroup.setLayout(new RowLayout(SWT.VERTICAL));				
	    moduleGroup.setText("Qt Modules");
		
		qtCoreBtn = new Button(moduleGroup, SWT.CHECK);
		qtCoreBtn.setText("Core");
		qtCoreBtn.setSelection(false);
		
		qtGuiBtn = new Button(moduleGroup, SWT.CHECK);
		qtGuiBtn.setText("Gui");
		qtGuiBtn.setSelection(false);
		
		qtSqlBtn = new Button(moduleGroup, SWT.CHECK);
		qtSqlBtn.setText("SQL");
		qtSqlBtn.setSelection(false);
		
		qtXmlBtn = new Button(moduleGroup, SWT.CHECK);
		qtXmlBtn.setText("XML");
		qtXmlBtn.setSelection(false);
		
		qtNetworkBtn = new Button(moduleGroup, SWT.CHECK);
		qtNetworkBtn.setText("Network");
		qtNetworkBtn.setSelection(false);

		qtSvgBtn = new Button(moduleGroup, SWT.CHECK);
		qtSvgBtn.setText("SVG");
		qtSvgBtn.setSelection(false);
		
		qtOpenGLBtn = new Button(moduleGroup, SWT.CHECK);
		qtOpenGLBtn.setText("OpenGL");
		qtOpenGLBtn.setSelection(false);
		
		qt3SupportBtn = new Button(moduleGroup, SWT.CHECK);
		qt3SupportBtn.setText("Qt3 Support");
		qt3SupportBtn.setSelection(false);
		
		hasInitialized = true;
		refreshSelectedModules();
		enableModules(requiredModules, false);
		
		setControl(mainComposite);
		setPageComplete(true);		
	}
	
	private void refreshSelectedModules()
	{
		if (!hasInitialized)
			return;
		int modules = selectedModules | requiredModules;
		if ((modules & QtProConstants.QtCore) != 0)
			qtCoreBtn.setSelection(true);
		if ((modules & QtProConstants.QtGui) != 0)
			qtGuiBtn.setSelection(true);
		if ((modules & QtProConstants.QtSql) != 0)
			qtSqlBtn.setSelection(true);
		if ((modules & QtProConstants.QtXml) != 0)
			qtXmlBtn.setSelection(true);
		if ((modules & QtProConstants.QtNetwork) != 0)
			qtNetworkBtn.setSelection(true);
		if ((modules & QtProConstants.QtSvg) != 0)
			qtSvgBtn.setSelection(true);
		if ((modules & QtProConstants.QtOpenGL) != 0)
			qtOpenGLBtn.setSelection(true);
		if ((modules & QtProConstants.Qt3Support) != 0)
			qt3SupportBtn.setSelection(true);
	}
	
	private void enableModules(int modules, boolean enabled)
	{
		if (!hasInitialized)
			return;
		if ((modules & QtProConstants.QtCore) != 0)
			qtCoreBtn.setEnabled(enabled);
		if ((modules & QtProConstants.QtGui) != 0)
			qtGuiBtn.setEnabled(enabled);
		if ((modules & QtProConstants.QtSql) != 0)
			qtSqlBtn.setEnabled(enabled);
		if ((modules & QtProConstants.QtXml) != 0)
			qtXmlBtn.setEnabled(enabled);
		if ((modules & QtProConstants.QtNetwork) != 0)
			qtNetworkBtn.setEnabled(enabled);
		if ((modules & QtProConstants.QtSvg) != 0)
			qtSvgBtn.setEnabled(enabled);
		if ((modules & QtProConstants.QtOpenGL) != 0)
			qtOpenGLBtn.setEnabled(enabled);
		if ((modules & QtProConstants.Qt3Support) != 0)
			qt3SupportBtn.setEnabled(enabled);
	}
}