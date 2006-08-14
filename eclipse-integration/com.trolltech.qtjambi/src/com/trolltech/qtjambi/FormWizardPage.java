package com.trolltech.qtjambi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.ide.IDE.SharedImages;

public class FormWizardPage extends WizardPage 
{
    private static String TEMPLATE_PATH = "com/trolltech/qtjambi/templates";
    private static String TEMPLATE_LIST_FILENAME = "templates.txt";
    
    private List templates;
    private Text sourceFolder;
    private org.eclipse.swt.widgets.List templateList;
    private IStructuredSelection selection;
    private Text fileName;
    private Text packageName;
    
    private class ContainerProvider implements ITreeContentProvider, ILabelProvider
    {
        public Object[] getChildren(Object parentElement) {
            Object[] returned = null;                        
            
            try {
                if (parentElement instanceof IContainer)                    
                    returned = ((IContainer) parentElement).members();
                else if (parentElement instanceof IWorkspaceRoot)
                    returned = ((IWorkspaceRoot) parentElement).getProjects();                                                                                       
            } catch (CoreException e) {
                setErrorMessage("Can't read workspace");
            }
            
            // Don't return null here, or Eclipse will make an obscure complaint
            return returned == null ? new Object[0] : returned;
        }

        public Object getParent(Object element) {
            return null;
        }
        
        public boolean hasChildren(Object element) 
        {
            return (getChildren(element).length > 0);
        }

        public Object[] getElements(Object inputElement) 
        {
            return getChildren(inputElement);
        }

        public void dispose() 
        {
            // who cares
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) 
        {
            // won't happen
        }

        public void addListener(ILabelProviderListener listener) 
        {
            // whatever
        }

        public boolean isLabelProperty(Object element, String property) 
        {
            return false;
        }

        public void removeListener(ILabelProviderListener listener) 
        {            
        }

        public Image getImage(Object element) 
        {       
            if (element instanceof IProject) {
                ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
                if (((IProject) element).isOpen())                    
                    return images.getImage(SharedImages.IMG_OBJ_PROJECT);
                else
                    return images.getImage(SharedImages.IMG_OBJ_PROJECT_CLOSED);                                
            } else {
                boolean isSourceFolder = false;
                if (element instanceof IFolder) {
                    IProject project = ((IFolder) element).getProject();
                    IJavaProject jproject = JavaCore.create(project);
                    if (jproject != null)
                        isSourceFolder = (jproject.isOnClasspath((IFolder) element));                                       
                }
                
                if (isSourceFolder) {
                    org.eclipse.jdt.ui.ISharedImages images = JavaUI.getSharedImages();
                    return images.getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKFRAG_ROOT);
                } else {
                    ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
                    return images.getImage(ISharedImages.IMG_OBJ_FOLDER);
                }
                    
            }
        }

        public String getText(Object element) {
            if (element instanceof IResource)
                return ((IResource) element).getName();
             else
                throw new IllegalArgumentException("Can only fetch label for resources and class path entries");                         
        }
        
    }
        
    public FormWizardPage(String pageName)
    {
        super(pageName);
        setPageComplete(false);
        
        setupTemplates();
    }
    
    private static int SELECTED_SOURCEFOLDER = 1;
    private static int SELECTED_TEMPLATE = 2;
    private static int SELECTED_FILENAME = 4;
    private void checkIfPageIsComplete()
    {
        int selected = 0;
        
        if (sourceFolder != null) {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();            
            IResource resource = root.findMember(sourceFolder.getText());
            
            IContainer folder = null;
            if (resource instanceof IContainer)
                folder = (IContainer) resource;
                                   
            if (folder != null                     
                && folder.exists() 
                && !folder.getResourceAttributes().isReadOnly()) {
                selected |= SELECTED_SOURCEFOLDER;            
            }
        }
        
        if (templateList != null) {
            String[] template = templateList.getSelection();
            if (template != null && template.length == 1)
                selected |= SELECTED_TEMPLATE;
        }
        
        if (fileName != null) {
            if (fileName.getText().length() > 0)
                selected |= SELECTED_FILENAME;
        }
        
        setPageComplete(selected 
                - (SELECTED_SOURCEFOLDER + SELECTED_TEMPLATE + SELECTED_FILENAME) == 0);
    }
    
    private void setupTemplates()
    {                
        ClassLoader loader = getClass().getClassLoader();
        
        InputStream stream = 
            loader.getResourceAsStream(TEMPLATE_PATH + "/" + TEMPLATE_LIST_FILENAME);
        if (stream != null) {
            BufferedReader r = new BufferedReader(new InputStreamReader(stream));
                        
            templates = new ArrayList();            
            try {
                String s = null;
                while ((s = r.readLine()) != null)
                    templates.add(s);                
            } catch (IOException e) {
                setErrorMessage("IOException caught when reading templates");
            }
        } else {
            setErrorMessage("Can't find resource: " + TEMPLATE_PATH + "/" + TEMPLATE_LIST_FILENAME);            
        }
    }
    
    
    public IPath getSelected()
    {
        IPath container = null;
        IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(sourceFolder.getText());
        
        if (res != null && res instanceof IContainer)
            container = ((IContainer) res).getFullPath();
        
        return container;        
    }
    
    public String getSelectedFileName()
    {
        if (fileName != null && packageName != null) {
            String fileName = packageName.getText().replaceAll("\\.", "/") + "/" + this.fileName.getText();
            
            if (!fileName.endsWith(".ui"))
                fileName += ".ui";
            
            return fileName;
        }
        return ""; 
    }
    
    public String getPathToTemplate()
    {
        if (templateList != null) {
            String path[] = templateList.getSelection();
            if (path != null) 
                return "com/trolltech/qtjambi/templates/" + path[0].replace(' ', '_') + ".ui";
        }
        
        return "";
    }
    
    public void setSelection(IStructuredSelection selection)
    {
        this.selection = selection;
    }
    
    private IResource findSuitableParent(IResource resource)
    {
        while (resource != null
                && !(resource instanceof IContainer)) {
             resource = resource.getParent();
         }
        
        return resource;
    }
    
    private String findSourceFolder(String defaultValue)
    {
        ContainerProvider cp = new ContainerProvider();
        ElementTreeSelectionDialog dialog = 
            new ElementTreeSelectionDialog(this.getShell(), cp, cp);

        dialog.addFilter(new ViewerFilter() {

            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (element instanceof IWorkspaceRoot) {
                    return true;
                } else if (element instanceof IContainer) {
                    return true; 
                } else if (element instanceof IFolder) {
                    return true; 
                }
                
                return false;
            }                
        });
        
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot(); 
        dialog.setInput(root);
                
        // Set initial element
        {            
            IResource resource = findSuitableParent(root.findMember(defaultValue));                        
            if (resource != null)
                dialog.setInitialSelection(resource);
        }
                
        dialog.setBlockOnOpen(true);
        dialog.setAllowMultiple(false);
        dialog.setDoubleClickSelects(true);
        
        String returned = defaultValue;
        if (dialog.open() == ElementTreeSelectionDialog.OK) {
            Object[] selection = dialog.getResult();
            if (selection != null && selection.length > 0) {
                Object selectedObject = selection[0];
                if (selectedObject instanceof IContainer) {
                    returned = ((IContainer) selectedObject).getFullPath().toString();
                }
            }
        }
        
        return returned;
    }
        
    public void createControl(Composite parent) {
        Composite topLevel = new Composite(parent, SWT.NONE);
        topLevel.setLayout(new GridLayout(3, false));
        topLevel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                               | GridData.HORIZONTAL_ALIGN_FILL));
        topLevel.setFont(parent.getFont());        
        
        {
            Label label = new Label(topLevel, SWT.NONE);
            label.setText("Source folder:");
                    
            sourceFolder = new Text(topLevel, SWT.BORDER);
            sourceFolder.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    checkIfPageIsComplete();                    
                }
                
            });
            
            // Set default value of source folder
            Object firstElement = selection.getFirstElement();
            
            IResource resource = null;
            if (firstElement instanceof IResource)
                resource = (IResource) firstElement;
            else if (firstElement instanceof IJavaElement) {
                try { resource = ((IJavaElement) firstElement).getCorrespondingResource(); }
                catch (JavaModelException e) {}                 
            }
            
            resource = findSuitableParent(resource);
            if (resource != null) 
                sourceFolder.setText(resource.getFullPath().toString());
            
            GridData gridData = new GridData();
            gridData.grabExcessHorizontalSpace = true;
            gridData.grabExcessVerticalSpace = true;
            gridData.horizontalAlignment = GridData.FILL;
            sourceFolder.setLayoutData(gridData);

            Button browseButton = new Button(topLevel, SWT.PUSH);
            browseButton.setText("Browse");
            
            browseButton.addSelectionListener(new SelectionListener() {
                public void widgetDefaultSelected(SelectionEvent e) { }

                public void widgetSelected(SelectionEvent e) 
                {
                    sourceFolder.setText(findSourceFolder(sourceFolder.getText()));
                }
                
            });
        }
        
        {
            Label label = new Label(topLevel, SWT.NONE);
            label.setText("Package name:");
            
            packageName = new Text(topLevel, SWT.BORDER);
            GridData gridData = new GridData();
            gridData.grabExcessHorizontalSpace = true;
            gridData.grabExcessVerticalSpace = true;
            gridData.horizontalAlignment = GridData.FILL;
            gridData.horizontalSpan = 2;
            packageName.setLayoutData(gridData);                                    
        }
        
        {
            Label label = new Label(topLevel, SWT.NONE);
            label.setText("File name:");
            
            fileName = new Text(topLevel, SWT.BORDER);
            fileName.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent e) {
                    checkIfPageIsComplete();                    
                }                
            });            
            
            GridData gridData = new GridData();
            gridData.grabExcessHorizontalSpace = true;
            gridData.grabExcessVerticalSpace = true;
            gridData.horizontalAlignment = GridData.FILL;
            gridData.horizontalSpan = 2;
            fileName.setLayoutData(gridData);            
        }
                                
        {
            Label label = new Label(topLevel, SWT.NONE);
            label.setText("Select a template for the new form");
            
            GridData gridData = new GridData();
            gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_FILL;
            gridData.horizontalSpan = 3;
            label.setLayoutData(gridData);                        
        }
        
        templateList = 
            new org.eclipse.swt.widgets.List(topLevel, SWT.SINGLE | SWT.BORDER);        
        templateList.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) { }
            
            public void widgetSelected(SelectionEvent e) {
                checkIfPageIsComplete();
            }
        });
        
        {
            GridData gridData = new GridData();
            gridData.grabExcessHorizontalSpace = true;
            gridData.grabExcessVerticalSpace = true;
            gridData.horizontalAlignment = GridData.FILL;
            gridData.verticalAlignment = GridData.FILL;
            gridData.horizontalSpan = 3;
            templateList.setLayoutData(gridData);
        }
        
        if (templates != null) {
            for (int i=0; i<templates.size(); ++i)
                templateList.add( ((String)templates.get(i)).replace('_', ' ') );
        }

        setErrorMessage(null);
        setMessage(null);
        setControl(topLevel);
    }
}
