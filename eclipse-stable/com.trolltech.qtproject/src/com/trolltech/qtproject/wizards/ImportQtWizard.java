package com.trolltech.qtproject.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.trolltech.qtproject.wizards.pages.ImportQtWizardPage;

public class ImportQtWizard extends Wizard implements IImportWizard {
	
	ImportQtWizardPage mainPage;

	public ImportQtWizard() {
		super();
	}

	public boolean performFinish() {
		IProject hProject = ResourcesPlugin.getWorkspace().getRoot().getProject(mainPage.getProjectName());
		AsyncQtProjectCreator procreator = new AsyncQtProjectCreator(hProject, getContainer());
		procreator.setLocationPath(mainPage.getProFileLocation());
		procreator.create();
		
        return true;
	}
	 
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("Qt Import Wizard");
		setNeedsProgressMonitor(true);
		mainPage = new ImportQtWizardPage("Import Qt Project");
	}
	
    public void addPages() {
        super.addPages(); 
        addPage(mainPage);        
    }

}
