package com.trolltech.qtproject.wizards;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.trolltech.common.QtWizardUtils;
import com.trolltech.qtproject.QtProjectPlugin;

public class QtGuiExampleProjectWizard extends QtProjectWizard {

	public void addPages() {
		String title = "Qt Gui Example Project";
		mainPage.setTitle(title);
		mainPage
				.setDescription("Create a new Qt Gui Example Application Project.");
		addPage(mainPage);
	}

	public void projectCreated(IProject project, IProgressMonitor monitor) {
		addFiles(project, monitor);
	}

	private void addFiles(IProject project, IProgressMonitor monitor) {
		String[] files = {"Main.cpp", "MainWindow.cpp", "MainWindow.h",
				"MainWindow.ui" ,"Example1.htm" ,"Example2.htm" ,"Example3.htm", "ExampleResourceFile.qrc"};
		
		for (int i = 0; i < files.length; i++) {
			String s = files[i];
			InputStream src = getClass().getResourceAsStream(QtProjectPlugin.TEMPLATE_LOCATION + "QtAppExample/" + s);
			File dest = new File(project.getLocation().toOSString() + "/" + s);
			QtWizardUtils.addTemplateFile(src, dest, null);
		}
		
		HashMap replaceMap = new HashMap();

		String projectName = project.getName();
		replaceMap.put("%PROJECT%", projectName);

		InputStream src = getClass().getResourceAsStream(QtProjectPlugin.TEMPLATE_LOCATION + "QtAppExample/Example.pro");
		File dest = new File(project.getLocation().toOSString() + "/" + projectName + ".pro");
		QtWizardUtils.addTemplateFile(src, dest, replaceMap);
	}
}
