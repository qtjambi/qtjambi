package com.trolltech.qtproject;

import java.util.Vector;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class QtNature implements IProjectNature {
	private IProject project;
	public static final String QT_NATURE_ID = "com.trolltech.qtproject.QtNature";
	
	public void configure() throws CoreException {
		IProjectDescription desc = project.getDescription();
		ICommand[] commands = desc.getBuildSpec();
		
		Vector builders = new Vector();
		
		ICommand qmake = desc.newCommand();
		qmake.setBuilderName(QtProConstants.QTBUILDER_ID);
		
		builders.add(qmake);
		
		for (int i=0; i<commands.length; ++i) {
			builders.add(commands[i]);						
		}
		
		desc.setBuildSpec((ICommand[])builders.toArray(new ICommand[builders.size()]));		
		project.setDescription(desc, null);
	}

	public void deconfigure() throws CoreException {
		IProjectDescription desc = getProject().getDescription();
		ICommand[] commands = desc.getBuildSpec();
		
		Vector builders = new Vector();
		for (int i = 0; i < commands.length; ++i) {
			if (!commands[i].getBuilderName().equals(QtProConstants.QTBUILDER_ID))
				builders.add(commands[i]);
		}
		
		desc.setBuildSpec((ICommand[])builders.toArray(new ICommand[builders.size()]));
		project.setDescription(desc, null);		
	}

	public IProject getProject() {		
		return project;
	}

	public void setProject(IProject project)
	{
		this.project = project;
	}
}

