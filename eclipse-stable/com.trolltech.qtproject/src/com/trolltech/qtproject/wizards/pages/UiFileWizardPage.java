package com.trolltech.qtproject.wizards.pages;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.trolltech.common.QtWizardUtils;
import com.trolltech.qtproject.QtProjectPlugin;
import com.trolltech.qtproject.wizards.NewQtClassWizard;

public class UiFileWizardPage extends WizardPage implements KeyListener,
		SelectionListener {
	private NewQtClassWizard wizard;

	private boolean uichanged = false;

	// controls
	private Group uigroup;

	private Button uicheck;

	private Text uiLE;

	private Combo uiType;

	private Label uil;

	private Label uiTypeLable;

	public UiFileWizardPage(NewQtClassWizard wizard, String pageName) {
		super(pageName);
		this.wizard = wizard;
		setTitle("Create User Interface");
		setDescription("Creates a user interface connected to the created class.");
	}

	public String getUIFileName() {
		return uiLE.getText();
	}

	public String getUIHeaderFileName() {
		IPath uipath = new Path(getUIFileName());

		String fileName = uipath.lastSegment();
		String fileExt = "." + uipath.getFileExtension();
		if (fileExt != null)
			fileName = fileName.replaceAll(fileExt, "");

		fileName = "ui_" + fileName + ".h";

		return uipath.removeLastSegments(1).append(fileName).toOSString();
	}

	public String getUIClassName() {
		return uiType.getText();
	}

	public boolean isGenerateUIChecked() {
		return uicheck.getSelection();
	}

	protected IPath getUIFilePath() {
		IPath uipath = new Path(getUIFileName());
		if (!uipath.isAbsolute()) {
			IPath srcPath = wizard.getSourceFolder();
			if (srcPath != null)
				uipath = srcPath.append(uipath);
		}
		return uipath;
	}

	public void createUIFile(IProgressMonitor monitor) throws CoreException {

		String uiClass = getUIClassName();

		HashMap replaceMap = new HashMap();
		replaceMap.put("%UI_CLASS%", uiClass);
		replaceMap.put("%CLASS%", wizard.getClassName());

		InputStream template = getClass().getResourceAsStream(
				QtProjectPlugin.TEMPLATE_LOCATION +	"QtGui/" + uiClass + ".ui");

		IFile uifile = QtWizardUtils.createFile(getUIFilePath(), monitor);
		
		String source = "";
		if (template != null) 
			source = QtWizardUtils.patchTemplateFile(template, replaceMap);
		
		uifile.create(new ByteArrayInputStream(source.getBytes()), true, monitor);
	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		mainComposite.setLayout(layout);

		uicheck = new Button(mainComposite, SWT.CHECK);
		uicheck.setSelection(false);
		uicheck.setText("Create User Interface");

		uicheck.addSelectionListener(this);

		uigroup = new Group(mainComposite, SWT.CHECK);
		uigroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
		uigroup.setText("UI File Information");

		layout = new GridLayout();
		layout.numColumns = 2;
		uigroup.setLayout(layout);

		uil = new Label(uigroup, SWT.PUSH);
		uil.setText("UI File:");

		uiLE = new Text(uigroup, SWT.BORDER | SWT.SINGLE);
		uiLE.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
		uiLE.addKeyListener(this);

		uiTypeLable = new Label(uigroup, SWT.PUSH);
		uiTypeLable.setText("UI Type:");

		uiType = new Combo(uigroup, SWT.READ_ONLY);
		uiType.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
		uiType.setItems(new String[] { "QWidget", "QMainWindow", "QDialog" });
		uiType.select(0);

		setControl(mainComposite);
		setPageComplete(true);

		widgetSelected(null);
	}

	public void keyPressed(KeyEvent e) {

	}

	public void keyReleased(KeyEvent e) {
		uichanged = true;
		checkValidPage();
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible && !uichanged) {
			String file = wizard.getCurrentHeaderFileName();
			uiLE.setText(file.replaceAll("\\.h", ".ui"));
			checkValidPage();
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {

	}

	public void widgetSelected(SelectionEvent e) {
		boolean enabled = uicheck.getSelection();
		uigroup.setEnabled(enabled);
		uiLE.setEnabled(enabled);
		uiType.setEnabled(enabled);
		uil.setEnabled(enabled);
		uiTypeLable.setEnabled(enabled);
	}

	private void checkValidPage() {
		if (uigroup.isEnabled()) {
			String string = uiLE.getText().trim();
			if (string.length() > 0 && string.contains(" \\/")) {
				setPageComplete(false);
				return;
			}
		}
		setPageComplete(true);
	}
}
