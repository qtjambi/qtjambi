package com.trolltech.qtdesigner.editors;

import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.*;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IEditorSite;
import com.trolltech.qtdesigner.views.*;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.eclipse.ui.IEditorActionBarContributor;

public class UiEditor extends EditorPart {
	private FormWindowW formwindow = null;
	private String uifile;
    private IResource resource_handle = null;
	
	public void doSave(IProgressMonitor monitor)
	{
		if (formwindow == null)
			return;

		formwindow.save();
        
        if (resource_handle != null) {
            try {
                resource_handle.touch(null);
            } catch (CoreException e) {
                throw new RuntimeException("Failed to touch resource", e); 
            }
        }
		
		firePropertyChange(PROP_DIRTY);
	}
	
	public void doSaveAs()
	{
		//### implement me later
	}
	
	public String getTitleToolTip()
	{
		return uifile;
	}

	public void init(IEditorSite site, IEditorInput input) 
		throws PartInitException
	{
		Class cpath, cloc;
		try {
			cpath = Class.forName("org.eclipse.ui.IPathEditorInput");
			cloc = Class.forName("org.eclipse.ui.editors.text.ILocationProvider");
		} catch (Exception e) {
			throw new PartInitException(e.toString());		
		}
		
		//### more checking
		IPath path;
		
		if (cpath.isAssignableFrom(input.getClass()))
			path = ((IPathEditorInput)input).getPath();
		else if (cloc.isAssignableFrom(input.getClass()))
			path = ((ILocationProvider)input).getPath(input);
		else 
			throw new PartInitException("not a .ui file");
		
		uifile = path.toOSString();
        
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        resource_handle = root.getFileForLocation(path); 
		
		setContentDescription("Qt Designer Editor");
		setPartName(input.getName());
		
		setSite(site);
		setInput(input);
	}
	
	public void createPartControl(Composite parent)
	{
		formwindow = new FormWindowW(parent, SWT.EMBEDDED);
		
		formwindow.addFormWindowWListener(new FormWindowWListener() {
			public void actionChanged(int actId)
			{
				IEditorActionBarContributor cont = UiEditor.this.getEditorSite().getActionBarContributor();                
				if (cont instanceof DesignerActionBarContributor) {
					DesignerActionBarContributor dcont = (DesignerActionBarContributor)cont;
					dcont.updateAction(actId);
				}
			}
			
			public void checkActiveWindow()
			{
				try {				
					UiEditor.this.getEditorSite().getPage().activate(UiEditor.this);
				} catch (Exception e) {
					
				}
			}

			public void resourceFilesChanged()
			{
				try {
					IEditorInput ein = UiEditor.this.getEditorInput();
					if (ein instanceof FileEditorInput) {
						FileEditorInput fin = (FileEditorInput)ein;
						if (fin.getFile().getProject() != null)
							fin.getFile().getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
					}
				} catch(Exception e) {
					//### do nothing...
				}
			}

			public void updateDirtyFlag() {
				UiEditor.this.firePropertyChange(PROP_DIRTY);							
			}
		});
		
		formwindow.open(uifile);
	}
	
	public void setFocus()
	{
		if (formwindow == null)
			return;
		
		formwindow.setFocus();
	}
	
	public boolean isDirty()
	{
		if (formwindow == null)
			return false;

		return formwindow.isDirty();
	}
	
	public boolean isSaveAsAllowed()
	{
		return false;
	}
	
	public void dispose()
	{
		if (formwindow != null)
			formwindow.close();
			
		super.dispose();		
	}
	
	public FormWindowW formWindow()
	{
		return formwindow;
	}
}
