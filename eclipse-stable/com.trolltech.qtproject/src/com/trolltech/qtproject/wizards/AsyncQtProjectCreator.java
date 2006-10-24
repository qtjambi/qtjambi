package com.trolltech.qtproject.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import com.trolltech.qtproject.QtProjectPlugin;

public class AsyncQtProjectCreator {
	private IProject hProject;

	private IWizardContainer container;

	private IPath locationPath = null;

	private AsyncQtProjectCreatorListener listener = null;

	public AsyncQtProjectCreator(IProject hProject, IWizardContainer container) {
		this.hProject = hProject;
		this.container = container;
	}

	public void addAsyncQtProjectCreatorListener(
			AsyncQtProjectCreatorListener listener) {
		this.listener = listener;
	}

	public void setLocationPath(IPath locationPath) {
		this.locationPath = locationPath;
	}

	public void create() {
		IPath defaultPath = Platform.getLocation();
		if (defaultPath.equals(locationPath))
			locationPath = null;

		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription description = workspace
				.newProjectDescription(hProject.getName());
		final IProject newProject = hProject;
		description.setLocation(locationPath);

		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				monitor.subTask("Configuring Qt Project...");
				newProject.create(description, monitor);

				if (monitor.isCanceled())
					throw new OperationCanceledException();

				newProject.open(monitor);

				monitor.subTask("Setting up project builders.");

				if (monitor.isCanceled())
					throw new OperationCanceledException();

				if (listener != null)
					listener.projectCreated(newProject, monitor);

				newProject.refreshLocal(1, monitor);

				QtProjectPlugin.getDefault().convertToQtProject(newProject,
						monitor);

				monitor.done();
			}
		};
		try {
			container.run(false, true, op);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
