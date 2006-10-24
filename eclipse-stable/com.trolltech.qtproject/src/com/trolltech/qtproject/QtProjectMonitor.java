package com.trolltech.qtproject;

import java.util.Vector;

import org.eclipse.cdt.core.CConventions;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.trolltech.qtproject.dialogs.SelectScopeDialog;
import com.trolltech.qtproject.utils.QtUtils;

public class QtProjectMonitor implements IResourceChangeListener, Runnable
{
	private IProject m_pro;
	private Vector m_filesAdded;
	private Vector m_filesRemoved;

	private final static String SOURCE_FILES = "SOURCES";
	private final static String HEADER_FILES = "HEADERS";
	private final static String FORM_FILES = "FORMS";
	private final static String RESOURCE_FILES = "RESOURCES";
	
	public QtProjectMonitor() {
		m_filesAdded = new Vector();
		m_filesRemoved = new Vector();
		
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		ws.addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
	}
	
	public void resourceChanged(IResourceChangeEvent event) {
		m_filesAdded.clear();
		m_filesRemoved.clear();
		
		IResourceDelta delta = event.getDelta();
		
		if (delta.getKind() == IResourceDelta.ADDED
				|| delta.getKind() == IResourceDelta.REMOVED) {
			if (isQtProject(delta.getResource()))
				return;
		}
		
		handleResourceDeltas(delta);
		
		if (!m_filesAdded.isEmpty() || !m_filesRemoved.isEmpty()) {
			Display.getDefault().asyncExec(this);
		}
	}
	
	public boolean isQtProject(IResource res) {
		if (res.getType() == IResource.PROJECT) {
			IProject pro = (IProject)res;
			try {
				if (pro.exists() && pro.hasNature(QtNature.QT_NATURE_ID))
					return true;
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public void handleResourceDeltas(IResourceDelta delta)
	{
		IResourceDelta children[] = delta.getAffectedChildren();
		for (int i=0; i<children.length; ++i) {
			IResourceDelta child = children[i];
			IResource current = child.getResource();
			
			if (child.getKind() == IResourceDelta.ADDED) {
				if (isQtProject(current))
					return;				
				if (isValidFile(current))
					m_filesAdded.add(current);
			} else if ((child.getKind() == IResourceDelta.REMOVED)) {
				if (isQtProject(current))
					return;				
				if (isValidFile(current))
					m_filesRemoved.add(current);
			}
				
			handleResourceDeltas(child);	
		}
	}
	
	public boolean isValidFile(IResource resource)
	{
		if (resource.getType() != IResource.FILE)
			return false;
		
		m_pro = resource.getProject();
		if (isQtProject(m_pro))
			return true;

		return false;
	}
	
	protected String niceName(String vartype) {
		if (vartype == SOURCE_FILES)
			return "source";
		if (vartype == HEADER_FILES)
			return "header";
		if (vartype == FORM_FILES)
			return "form";
		
		return "resource";
	}
	
	protected void distributeFileTypes(boolean remove) {
		Vector files = m_filesAdded;
		if (remove)
			files = m_filesRemoved;
		
		Vector sources = new Vector();
		Vector headers = new Vector();
		Vector forms = new Vector();
		Vector resources = new Vector();
		
		for (int i=0; i<files.size(); ++i) {
			IFile file = (IFile)files.get(i);
			IProject pro = file.getProject();
			String name = file.getName();
			
			if (CConventions.validateSourceFileName(pro, name).isOK()) {
				if (!name.startsWith("qrc_") && !name.startsWith("moc_"))
					sources.add(file);
			} else if (CConventions.validateHeaderFileName(pro, name).isOK()) {
				if (!name.startsWith("ui_"))
					headers.add(file);
			} else if (QtUtils.isFormFile(name)) {
				forms.add(file);
			} else if (QtUtils.isResourceFile(name)) {
				resources.add(file);
			}
		}
		
		if (sources.isEmpty() && headers.isEmpty() &&
				forms.isEmpty() && resources.isEmpty())
			return;

		IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		SelectScopeDialog dlg = new SelectScopeDialog(win.getShell(), m_pro);
		
		if (remove) {
			if (!sources.isEmpty())
				dlg.removeFiles(sources, SOURCE_FILES);
			if (!headers.isEmpty())
				dlg.removeFiles(headers, HEADER_FILES);
			if (!forms.isEmpty())
				dlg.removeFiles(forms, FORM_FILES);
			if (!resources.isEmpty())
				dlg.removeFiles(resources, RESOURCE_FILES);
		} else {
			if (!sources.isEmpty())
				dlg.addFiles(sources, SOURCE_FILES);
			if (!headers.isEmpty())
				dlg.addFiles(headers, HEADER_FILES);
			if (!forms.isEmpty())
				dlg.addFiles(forms, FORM_FILES);
			if (!resources.isEmpty())
				dlg.addFiles(resources, RESOURCE_FILES);
		}
		
		dlg.open();
	}

	public void run() {
		distributeFileTypes(false);
		distributeFileTypes(true);
	}
}
