package com.trolltech.qtproject.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.trolltech.qtproject.QtProConstants;
import com.trolltech.qtproject.wizards.pages.QtModulesWizardPage;

public class QtEmptyProjectWizard extends QtProjectWizard
{
		private QtModulesWizardPage modulesPage;
		
		public void addPages()
		{
			String title = "Empty Qt Project";
			mainPage.setTitle(title);
			mainPage.setDescription("Create a new empty Qt Project.");
			addPage(mainPage);
		    
			modulesPage = new QtModulesWizardPage("com.trolltech.qtproject.QtModulesPage");
			modulesPage.setSelectedModules(QtProConstants.QtCore);
			modulesPage.setRequiredModules(QtProConstants.QtCore);
			modulesPage.setTitle(title);
			modulesPage.setDescription("Select the Qt modules.");
		    addPage(modulesPage);		
		}
		
		public void projectCreated(IProject project, IProgressMonitor monitor)
		{
//			ProFile proFile = ProSourceWriter.getProFile(project);
//			proFile.getMap().put(QtProConstants.KEY_TEMPLATE, "app");
//			proFile.getMap().put(QtProConstants.KEY_MODULES, new Integer(modulesPage.getSelectedModules()).toString());
//			proFile.commitSettings();
		}		
}
