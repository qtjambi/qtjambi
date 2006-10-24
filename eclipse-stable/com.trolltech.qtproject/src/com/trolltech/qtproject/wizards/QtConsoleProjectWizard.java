package com.trolltech.qtproject.wizards;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.trolltech.common.QtWizardUtils;
import com.trolltech.qtproject.QtProConstants;
import com.trolltech.qtproject.QtProjectPlugin;
import com.trolltech.qtproject.wizards.pages.QtModulesWizardPage;

public class QtConsoleProjectWizard extends QtProjectWizard {

	private QtModulesWizardPage modulesPage;

	public void addPages() {
		String title = "Qt Console Project";
		mainPage.setTitle(title);
		mainPage.setDescription("Create a new Qt Console Application Project.");
		addPage(mainPage);

		modulesPage = new QtModulesWizardPage(
				"com.trolltech.qtproject.QtModulesPage");
		modulesPage.setSelectedModules(QtProConstants.QtCore);
		modulesPage.setRequiredModules(QtProConstants.QtCore);
		
		modulesPage.setTitle(title);
		modulesPage.setDescription("Select the Qt modules.");
		addPage(modulesPage);
	}

	public void projectCreated(IProject project, IProgressMonitor monitor) {
		addFiles(project, monitor);
	}

	private void addFiles(IProject project, IProgressMonitor monitor) {
		HashMap replaceMap = new HashMap();

		String projectName = project.getName();
		replaceMap.put("%PROJECT%", projectName);
		replaceMap.put("%MODULES%", modulesPage.getModules());

		InputStream src = getClass().getResourceAsStream(QtProjectPlugin.TEMPLATE_LOCATION + "QtConsole/Console.pro");
		File dest = new File(project.getLocation().toOSString() + "/" + projectName + ".pro");
		QtWizardUtils.addTemplateFile(src, dest, replaceMap);
		
		src = getClass().getResourceAsStream(QtProjectPlugin.TEMPLATE_LOCATION + "QtConsole/Main.cpp");
		dest = new File(project.getLocation().toOSString() + "/Main.cpp");
		QtWizardUtils.addTemplateFile(src, dest, null);
		
		
	}
}
