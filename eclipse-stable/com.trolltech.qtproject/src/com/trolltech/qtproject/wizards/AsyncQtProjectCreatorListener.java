package com.trolltech.qtproject.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

public interface AsyncQtProjectCreatorListener {
	void projectCreated(IProject project, IProgressMonitor monitor);
}
