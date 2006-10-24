package com.trolltech.qtproject.wizards.pages;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

public class QtConversionWizardPage extends WizardPage
{
	private Label l;
	private Table table;
	private CheckboxTableViewer tableViewer;
	
	protected Object[] projects = null;
	protected Object[] selectedProjects = null;
	
	public QtConversionWizardPage(String pageName)
	{
		super(pageName);
	}

	public void createControl(Composite parent)
	{
		collectProjects();
		Composite mainComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		mainComposite.setLayout(layout);
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
				
		l = new Label(mainComposite, SWT.PUSH);
		l.setText("Select the project you want to convert:");
		
		table = new Table(mainComposite, SWT.CHECK | SWT.BORDER | SWT.MULTI | 
                SWT.SINGLE | SWT.H_SCROLL | 
                SWT.V_SCROLL);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
        table.setLayoutData(gd);
        table.setHeaderVisible(true);
        table.setLinesVisible(false);
        
        TableLayout tableLayout = new TableLayout();
        table.setHeaderVisible(false);
        table.setLayout(tableLayout);
				
		tableViewer = new CheckboxTableViewer(table);
		
		tableViewer.setLabelProvider(new ProjectLabelProvider());
        tableViewer.setContentProvider(new ProjectContentProvider());
		
		tableViewer.add(projects);
		tableViewer.setAllChecked(false);
		
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {            
			public void selectionChanged(SelectionChangedEvent event) {
				selectedProjects = tableViewer.getCheckedElements();
				setPageComplete(selectedProjects != null && selectedProjects.length > 0);
			}
        });

		setControl(mainComposite);
		setPageComplete(false);
	}
	
	private void collectProjects()
	{
		IWorkspaceRoot ws = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projs = ws.getProjects();
		ArrayList lst = new ArrayList();
		int j = 0;
		for (int i=0; i<projs.length; ++i) {
			try {
				IProjectDescription desc = projs[i].getDescription();
				if ((desc.hasNature("org.eclipse.cdt.core.cnature")
						|| desc.hasNature("org.eclipse.cdt.core.ccnature"))
						&& !desc.hasNature("org.eclipse.cdt.managedbuilder.core.managedBuildNature")) {
					lst.add(projs[i]);
					++j;
				}
				
			} catch (CoreException e) {}
		}
		projects = lst.toArray(new Object[j]);
	}
	
	public Object[] getSelectedProjects()
	{
		return selectedProjects;
	}
	
	public class ProjectContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object parent)
		{
			return projects;                
		}

		public void dispose()
		{			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
}

	public class ProjectLabelProvider extends LabelProvider implements ITableLabelProvider {
    public String getColumnText(Object obj, int index)
    {
        if (index == 0) {
            return ((IProject)obj).getName();
        }
        return "";
    }

	public Image getColumnImage(Object element, int columnIndex)
	{
		return null;
	}
}
}
