package com.trolltech.qtproject.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public abstract class QtProjectWizard extends Wizard implements INewWizard, AsyncQtProjectCreatorListener 
{
	protected WizardNewProjectCreationPage mainPage;

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("New Qt Gui Application Project");
		mainPage = new WizardNewProjectCreationPage("com.trolltech.qtproject.MainWizardPage");

	}

	public void addPages() {
		addPage(mainPage);
	}

	public boolean performFinish() {
		if (mainPage.isPageComplete()) {
			createNewProject();
			return true;
		}
		return false;
	}

	public void createNewProject() {
		AsyncQtProjectCreator procreator = new AsyncQtProjectCreator(mainPage.getProjectHandle(), getContainer());
		procreator.setLocationPath(mainPage.getLocationPath());
		procreator.addAsyncQtProjectCreatorListener(this);
		procreator.create();
	}
}