package com.trolltech.qtjambi;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class ProjectListener implements IElementChangedListener {
	public void elementChanged(ElementChangedEvent event) {
		IJavaElementDelta delta = event.getDelta();

		for(int i=0; i<delta.getAddedChildren().length; ++i) {
			IJavaElement element = delta.getAddedChildren()[i].getElement();
			if (element.getElementType() == IJavaElement.JAVA_PROJECT)
				addJuicBuilder(element.getJavaProject());
		}
	}

	public void addBuilderToAllProjects() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IJavaModel jmodel = JavaCore.create(root);
		
		try {
			IJavaProject[] projects = jmodel.getJavaProjects(); 
			for(int i=0; i<projects.length; ++i) {
				addJuicBuilder(projects[i]);				
			}
		} catch (Exception e) {
			e.printStackTrace();		
		}
	}
	
	
	protected IJavaProject m_jpro;
	
	private void addJuicBuilder(IJavaProject jpro) {
		final IWorkbench workbench = PlatformUI.getWorkbench(); 
		workbench.getDisplay().asyncExec(new BuilderAdder(jpro.getProject())); 
	}
	
}

class BuilderAdder implements Runnable {
	private IProject m_project;
	
	BuilderAdder(IProject project) {
		m_project = project;
	}

	public void run() {
		final String BUILDER_ID = "com.trolltech.qtjambi.juicBuilder";
		try {
			IProjectDescription desc = m_project.getProject().getDescription();
			ICommand[] commands = desc.getBuildSpec();
			boolean found = false;
	
			for (int i = 0; i < commands.length; ++i) {
				if (commands[i].getBuilderName().equals(BUILDER_ID)) {
					found = true;
					break;
				}
			}
			if (!found) {
				//add builder to project
				ICommand command = desc.newCommand();
				command.setBuilderName(BUILDER_ID);
				ICommand[] newCommands = new ICommand[commands.length + 1];
	
				// Add it before other builders.
				System.arraycopy(commands, 0, newCommands, 1, commands.length);
				newCommands[0] = command;
				desc.setBuildSpec(newCommands);
                try {
                    m_project.getProject().setDescription(desc, null);
                } catch (Exception e) {
                    ErrorReporter.reportError(e, "Unable to modify description of project");
                }

			}
		} catch (CoreException e) {
            ErrorReporter.reportError(e, "Exception when adding builder");
		}						
	}
}
