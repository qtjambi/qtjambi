package com.trolltech.qtproject.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.trolltech.qtproject.QtProjectPlugin;
import com.trolltech.qtproject.wizards.pages.QtConversionWizardPage;

public class QtConversionWizard extends BasicNewResourceWizard
{
	private QtConversionWizardPage page;
		
	public void addPages()
	{
		setWindowTitle("Convert to Qt Project");
		page = new QtConversionWizardPage("com.trolltech.qtproject.wizards.QtConversionWizard");
		page.setDescription("Convert the selected project to a Qt projects.");
		addPage(page);		
	}
	
	public boolean performFinish() {
		if (page.isPageComplete()) {
			Object[] projects = page.getSelectedProjects();
			for (int i=0; i<projects.length; ++i) {
				convertProject((IProject)projects[i]);				
			}
			return true;
		}
		return false;
	}
	
	private void convertProject(IProject project)
	{
		final IProject pro = project;
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor) throws CoreException {
				monitor.beginTask("", 1000);
				try {
					QtProjectPlugin.getDefault().convertToQtProject(pro, monitor);					
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(false, true, op);
		} catch (InterruptedException e) {
			e.printStackTrace();			
		} catch (InvocationTargetException e) {
			e.printStackTrace();			
		}
	}}
