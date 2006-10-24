package com.trolltech.qtproject.wizards;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.IWizardPage;

import com.trolltech.common.QtWizardUtils;
import com.trolltech.qtproject.QtProConstants;
import com.trolltech.qtproject.QtProjectPlugin;
import com.trolltech.qtproject.wizards.pages.FilesWizardPage;
import com.trolltech.qtproject.wizards.pages.QtModulesWizardPage;

public class QtGuiProjectWizard extends QtProjectWizard {
	private FilesWizardPage filesPage;

	private QtModulesWizardPage modulesPage;

	public void addPages() {
		String title = "Qt Gui Project";
		mainPage.setTitle(title);
		mainPage.setDescription("Create a new Qt Gui Application Project.");
		addPage(mainPage);

		filesPage = new FilesWizardPage("com.trolltech.qtproject.FilesWizardPage");
		filesPage.setTitle(title);
		filesPage.setDescription("Setup the class and file names.");
		addPage(filesPage);

		modulesPage = new QtModulesWizardPage(
				"com.trolltech.qtproject.QtModulesPage");
		modulesPage.setSelectedModules(QtProConstants.QtCore
				| QtProConstants.QtGui);
		modulesPage.setRequiredModules(QtProConstants.QtCore
				| QtProConstants.QtGui);
		modulesPage.setTitle(title);
		modulesPage.setDescription("Select the Qt modules.");
		addPage(modulesPage);
	}

	public IWizardPage getNextPage(IWizardPage page) {
		if (mainPage.isPageComplete()
				&& getContainer().getCurrentPage() == mainPage) {
			filesPage.setClassName(mainPage.getProjectHandle().getName());
		}
		return super.getNextPage(page);
	}

	public void projectCreated(IProject project, IProgressMonitor monitor) {
		addFiles(project, monitor);
	}

	private void addFiles(IProject pro, IProgressMonitor monitor) {
		HashMap replaceMap = new HashMap();

		String hdr = filesPage.getHeaderFileName();
		if (hdr.endsWith(".h"))
			hdr = hdr.substring(0, hdr.length() - 2);
		String preDef = hdr.toUpperCase() + "_H";

		String uiHdr = filesPage.getUIFileName();
		if (uiHdr.endsWith(".ui"))
			uiHdr = uiHdr.substring(0, uiHdr.length() - 3);
		uiHdr = "ui_" + uiHdr + ".h";
		
		String headerFile = filesPage.getHeaderFileName();
		String srcFile = filesPage.getSourceFileName();
		String uiFile = filesPage.getUIFileName();

		replaceMap.put("%MODULES%", modulesPage.getModules());
		
		replaceMap.put("%INCLUDE%", headerFile);
		replaceMap.put("%CLASS%", filesPage.getClassName());

		replaceMap.put("%PRE_DEF%", preDef);
		replaceMap.put("%UI_HDR%", uiHdr);

		String uiClass = filesPage.getUiClassName();
		replaceMap.put("%UI_CLASS%", uiClass);
		
		String projectName = pro.getName();
		replaceMap.put("%PROJECT%", projectName);
		
		replaceMap.put("%HEADER_FILE%", headerFile);
		replaceMap.put("%SOURCE_FILE%", srcFile);
		replaceMap.put("%UI_FILE%", uiFile);

		InputStream src = getClass().getResourceAsStream(QtProjectPlugin.TEMPLATE_LOCATION + "QtGui/Gui.pro");
		File dest = new File(pro.getLocation().toOSString() + "/" + projectName + ".pro");
		QtWizardUtils.addTemplateFile(src, dest, replaceMap);

		src = getClass().getResourceAsStream(QtProjectPlugin.TEMPLATE_LOCATION + "QtGui/Main.cpp");
		dest = new File(pro.getLocation().toOSString() + "/Main.cpp");
		QtWizardUtils.addTemplateFile(src, dest, replaceMap);

		src = getClass().getResourceAsStream(QtProjectPlugin.TEMPLATE_LOCATION + "QtGui/Gui.cpp");
		dest = new File(pro.getLocation().toOSString() + "/" + srcFile);
		QtWizardUtils.addTemplateFile(src, dest, replaceMap);

		src = getClass().getResourceAsStream(QtProjectPlugin.TEMPLATE_LOCATION + "QtGui/Gui.h");
		dest = new File(pro.getLocation().toOSString() + "/" + headerFile);
		QtWizardUtils.addTemplateFile(src, dest, replaceMap);

		src = getClass().getResourceAsStream(QtProjectPlugin.TEMPLATE_LOCATION + "QtGui/" + uiClass + ".ui");
		dest = new File(pro.getLocation().toOSString() + "/" + uiFile);
		QtWizardUtils.addTemplateFile(src, dest, replaceMap);
	}
}
