package com.trolltech.qtproject.wizards.pages;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.trolltech.qtproject.utils.QtUtils;

public class ImportQtWizardPage extends WizardPage implements SelectionListener, ModifyListener {
	private Table list;
	private Text profilelocation;
	private IPath path = null;
	
	public ImportQtWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setDescription("Import a Qt project from the local file system into the workspace");
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		Composite workArea = new Composite(parent, SWT.NONE);
		setControl(workArea);

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		
		workArea.setLayout(layout);
		workArea.setLayoutData(new GridData(GridData.FILL_BOTH
				| GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
		
		Label label = new Label(workArea, SWT.NONE);
		label.setText("Select .pro file: ");
		
		profilelocation = new Text(workArea, SWT.BORDER);
		profilelocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		profilelocation.addModifyListener(this);
		
		Button browse = new Button(workArea, SWT.NONE);
		browse.setText("Browse...");
		browse.addSelectionListener(this);
		
		list = new Table(workArea, SWT.CHECK | SWT.BORDER);
		list.addSelectionListener(this);
		
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 3;
		list.setLayoutData(data);
	}

	public String getProjectName() {
		return QtUtils.getFileNameFromIPath(path, true);
	}
	
	public IPath getProFileLocation() {
		return QtUtils.removeFileNameFromIPath(path);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		
	}

	public void widgetSelected(SelectionEvent e) {
		if (e.widget == list) {
			updatePage();			
		} else {
			FileDialog filedlg = new FileDialog(getShell());
			
			String[] filterext = {"*.pro"}; 
			String[] filtername = {"Qt Project File"};
			filedlg.setFilterExtensions(filterext);
			filedlg.setFilterNames(filtername);
			filedlg.setText("Select Qt Project File");
			String filename = filedlg.open();
			if (filename != null)
				profilelocation.setText(filename);
		}
	}
	
	private void updatePage() {
		boolean enabled = false;
		for (int i=0; i<list.getItemCount(); ++i) {
			if (list.getItem(i).getChecked()) {
				enabled = true;
				break;
			}
		}
		setPageComplete(enabled);
	}

	public void modifyText(ModifyEvent e) {
		if (isValidProFile()) {
			list.clearAll();
			TableItem item = new TableItem(list, SWT.NONE);
			item.setText(getProjectName());
		}
		updatePage();		
	}
	
	private boolean isValidProFile() {
		path = new Path(profilelocation.getText().trim());
		if (!path.getFileExtension().equals("pro"))
			return false;
		
		File file = path.toFile();
		if (!file.exists())
			return false;
		
		return true;
	}
}
