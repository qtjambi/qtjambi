package com.trolltech.qtstartup;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleException;

import com.trolltech.qtproject.QtProjectPlugin;

class ProjectWatcher implements IResourceChangeListener, IResourceVisitor
{
	private boolean m_loaded = false;
	private final static String QT_NATURE_ID = "com.trolltech.qtproject.QtNature";
	
	public void createWatcher() {
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		
		try {
			ws.getRoot().accept(this);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		ws.addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
	}

	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		IResourceDelta children[] = delta.getAffectedChildren(IResourceDelta.ADDED);
		for (int i=0; i<children.length; ++i) {
			IResource current = children[i].getResource();
			if (current.getType() == IResource.PROJECT) {
				IProject pro = (IProject)current;
				try {
					if (pro.hasNature(QT_NATURE_ID))
						loadPlugin();
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean visit(IResource resource) throws CoreException {
		if (resource.getType() == IResource.PROJECT) {
			IProject pro = (IProject)resource;
			try {
				if (pro.hasNature(QT_NATURE_ID))
					loadPlugin();																
			} catch (CoreException e) {
				e.printStackTrace();
			}
			return false;
		}
		
		return (resource.getType() == IResource.ROOT
				|| resource.getType() == IResource.FOLDER);
	}
	
	public void loadPlugin() {
		if (m_loaded)
			return;
		
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		ws.removeResourceChangeListener(this);
		
		m_loaded = true;

		try {
			QtProjectPlugin.getDefault().getBundle().start();
		} catch (BundleException e) {
			e.printStackTrace();
		}
	}
}

public class QtStartup implements IStartup {
	private ProjectWatcher m_watcher;
	
	public void earlyStartup() {
		m_watcher = new ProjectWatcher();
		m_watcher.createWatcher();
	}
}
