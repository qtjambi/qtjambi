package com.trolltech.qtproject.dialogs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.FileEditorInput;

import com.trolltech.qtproject.ProEditorModelManager;
import com.trolltech.qtproject.QtProjectPlugin;
import com.trolltech.qtproject.pages.ScopeList;
import com.trolltech.qtproject.utils.QtProjectVisitor;

public class SelectScopeDialog extends Dialog implements SelectionListener {
	// Controls
	private ScopeList m_scope;
	private Label m_label;
	private Combo m_combo;
	
	// Variables
	private IProject m_pro;
	private Map m_files;
	private Vector m_profiles;
	private Map m_hModels;
	private String m_desc = "";
	private boolean m_remove = false;
	
	public SelectScopeDialog(Shell parentShell, IProject pro) {
		super(parentShell);
		m_pro = pro;
		m_profiles = new Vector();
		m_hModels = new HashMap();
		m_files = new HashMap();
	}
	
	public void setDescription(String desc) {
		m_desc = desc;
	}
    
    public void addFiles(Vector files, String var) {
		m_remove = false;
		if (!m_desc.equals(""))
			m_desc = "Select where you want to insert the files in the Qt project settings.";											
				
		m_files.put(var, files);
	}

	public void removeFiles(Vector files, String var) {
    	m_remove = true;
		if (!m_desc.equals(""))
			m_desc = "The files will be removed from the following Qt project settings:";											
    	
		m_files.put(var, files);
    }
    
    public void widgetSelected(SelectionEvent e) {
		int index = m_combo.getSelectionIndex();
		setProjectFile((IFile)m_profiles.get(index));
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
		
	}
	
	public void okPressed() {
		for (int i=0; i<m_profiles.size(); ++i) {
			if (!m_hModels.containsKey(m_profiles.get(i)))
				continue;
			int hModel = ((Integer)m_hModels.get(m_profiles.get(i))).intValue();
			if (!m_scope.changed(hModel))
				continue;

			registerAndOpenModel((IFile)m_profiles.get(i), hModel);
		}
	
		if (m_remove)
			m_scope.removeFiles();
		else
			m_scope.addFiles();

		close();
	}
	
	public void cancelPressed() {
		close();
	}

    protected int getModelHandle(IFile file) {
		if (file == null)
			return 0;
		
		if (m_hModels.containsKey(file))
			return ((Integer)m_hModels.get(file)).intValue();
		
		String filename = file.getLocation().toOSString();
		ProEditorModelManager manager = QtProjectPlugin.getDefault().getModelManager();
		
		int hModel = 0;
		if (manager.hasModel(filename)) {
			hModel = manager.getModelHandle(filename);												
		} else {
			hModel = m_scope.createModel(filename);
		}
		
		m_hModels.put(file, new Integer(hModel));
		return hModel;    	
    }
	
	protected void setProjectFile(IFile file) {
		m_scope.showModel(getModelHandle(file), !m_remove);
	}
	
	protected void registerAndOpenModel(IFile file, int hModel) {
		String filename = file.getLocation().toOSString();
		ProEditorModelManager manager = QtProjectPlugin.getDefault().getModelManager();
		manager.registerModelHandle(filename, hModel);
		openProjectFile(file);
	}
	
	protected void openProjectFile(IFile file) {
		try {
			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow wbw = wb.getActiveWorkbenchWindow();
			IWorkbenchPage page = wbw.getActivePage();
			
			IEditorDescriptor desc = wb.getEditorRegistry().getDefaultEditor(file.getName());
		    page.openEditor(new FileEditorInput(file), desc.getId());
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
	}
	
	protected boolean findProjectFiles() {
		QtProjectVisitor provisitor = new QtProjectVisitor();
		m_profiles.addAll(provisitor.findFiles(m_pro, "pro"));
		m_profiles.addAll(provisitor.findFiles(m_pro, "pri"));
		
		return !m_profiles.isEmpty();
	}
	
	protected void addFiles() {
		Iterator keyit = m_files.keySet().iterator();
		while(keyit.hasNext()) {
			String var = (String)keyit.next();
			Vector files = (Vector)m_files.get(var);
			for (int i=0; i<files.size(); ++i) {
				m_scope.addFile(((IFile)files.get(i)).getLocation().toOSString(), var);
			}
		}
	}
	
	protected void createRemoveDialog(Composite composite) {
		m_combo = new Combo(composite, SWT.READ_ONLY);
		
		GridData data = new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL);
        data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        m_combo.setLayoutData(data);
        m_combo.setFont(composite.getParent().getFont());
		
		m_scope = new ScopeList(composite, SWT.EMBEDDED);
        data = new GridData(GridData.GRAB_HORIZONTAL
                | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                | GridData.VERTICAL_ALIGN_FILL);
        data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        data.heightHint = 300;
        m_scope.setLayoutData(data);
        m_scope.setFont(composite.getParent().getFont());

        addFiles();

		Vector profiles = m_profiles;
		m_profiles = new Vector();
		
		for (int i=0; i<profiles.size(); ++i) {
			IFile file = (IFile)profiles.get(i);
			int hModel = getModelHandle(file);
			if (hModel != 0) {
				if (m_scope.search(hModel)) {
					m_profiles.add(file);
					m_combo.add(file.getName());
				}
			}
		}
		
		if (!m_profiles.isEmpty()) {
			m_combo.select(0);			
			m_combo.addSelectionListener(this);
			setProjectFile((IFile)m_profiles.get(0));
		} else {
			m_label.setText("Could not find any files to remove. You must remove the files manually.");
			m_combo.setEnabled(false);
			m_scope.showModel(0, false);
		}
	}
	
	protected void createAddDialog(Composite composite) {
		// combo box			
		m_combo = new Combo(composite, SWT.READ_ONLY);
		
		for (int i=0; i<m_profiles.size(); ++i) {
			IResource res = (IResource)m_profiles.get(i);
			if (res.getType() == IResource.FILE) {
				IFile f = (IFile)res;
				m_combo.add(f.getName());
			}
		}
		
		GridData data = new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL);
        data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        m_combo.setLayoutData(data);
        m_combo.setFont(composite.getParent().getFont());
        
		// scope selection tree
        m_scope = new ScopeList(composite, SWT.EMBEDDED);
        data = new GridData(GridData.GRAB_HORIZONTAL
                | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                | GridData.VERTICAL_ALIGN_FILL);
        data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        data.heightHint = 300;
        
        m_scope.setLayoutData(data);
        m_scope.setFont(composite.getParent().getFont());
        
        addFiles();

        if (!m_profiles.isEmpty()) {        
			m_combo.select(0);			
			m_combo.addSelectionListener(this);
			setProjectFile((IFile)m_profiles.get(0));
			m_scope.selectFirstVariable();
        } else {
			m_label.setText("Could not find any Qt project files. You must edit you pro/pri files manually.");
			m_combo.setEnabled(false);
			m_scope.showModel(0, false);
        }
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		
		m_label = new Label(composite, SWT.WRAP);
		m_label.setText(m_desc);
		
        GridData data = new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL);
        data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        m_label.setLayoutData(data);
        m_label.setFont(composite.getParent().getFont());
		
        findProjectFiles();
		if (m_remove) {
			createRemoveDialog(composite);
		} else {
			createAddDialog(composite);
		}

		applyDialogFont(composite);
		return composite;
	}
	
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (m_remove)
        	shell.setText("Remove files from Qt project");
        else
        	shell.setText("Add files to Qt project");
    }	
}
