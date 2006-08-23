package com.trolltech.qtjambi;

import java.io.InputStream;
import java.lang.reflect.Method;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;


public class FormWizard extends Wizard implements INewWizard 
{
    private FormWizardPage mainPage;
    private IStructuredSelection selection;
    
    public FormWizard()
    {
        super();        
    }
    
    public void addPages()
    {
        super.addPages();          
        
        mainPage = new FormWizardPage("newFilePage1");
        mainPage.setTitle("New Qt Designer Form");
        mainPage.setDescription("Create a new Qt Designer Form");
        mainPage.setSelection(selection);
        
        addPage(mainPage);
    }
    
    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        setWindowTitle("New Qt Designer Form");     
        this.selection = selection;
    }

    public boolean performFinish() {
        IPath container = mainPage.getSelected();
        String name = mainPage.getSelectedFileName();
        String pathToTemplate = mainPage.getPathToTemplate();
        
        if (container != null && name.length() > 0 && pathToTemplate.length() > 0) {
            container = container.addTrailingSeparator();
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot(); 
                        
            String folderName = name.substring(0, name.lastIndexOf('/'));
            container = container.append(folderName).addTrailingSeparator();
            
            if (container.segmentCount() > 1) {
                IFolder folder = root.getFolder(container);
                if (folder != null && !folder.exists()) {
                    IPath path = null;
                    
                    // path == project
                    path = container.removeLastSegments(container.segmentCount() - 1);
                    
                    // Try to make entire path
                    for (int i=1; i<container.segmentCount(); ++i) {
                        path = path.append(container.segment(i)).addTrailingSeparator();
                        
                        folder = root.getFolder(path);
                        if (!folder.exists()) {
                            try {
                                folder.create(true, true, null);                            
                            } catch (CoreException e) {
                                ErrorReporter.reportError(e, "Couldn't create parent folder for form: " 
                                                             + folder.getLocation().toOSString());
                                break; 
                            }
                        }
                    }
                    
                    try {
                        folder.create(true, true, null);
                    } catch (CoreException e) {
                        ErrorReporter.reportError(e, "Couldn't create folder for form: " + folder.getLocation().toOSString());
                    }
                }
            }
                      
            String fname = name.substring(name.lastIndexOf('/') + 1);
            container = container.append(fname);
            IFile file = root.getFile(container);
            if (!file.exists()) {                
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                InputStream stream = cl.getResourceAsStream(pathToTemplate);
                
                try {                  
                    IWorkspaceDescription wsd = ResourcesPlugin.getWorkspace().getDescription();
                    boolean wasAutoBuilding = wsd.isAutoBuilding();
                    wsd.setAutoBuilding(false);
                    ResourcesPlugin.getWorkspace().setDescription(wsd);
                    file.create(stream, true, null);     
                    IWorkbench wb = PlatformUI.getWorkbench();
                    
                                        
                    IWorkbenchWindow ww = null;
                    if (wb != null)
                        ww = wb.getActiveWorkbenchWindow();
                    
                    IWorkbenchPage wp = null;
                    if (ww != null)
                        wp = ww.getActivePage();
                    
                    if (wp != null) {
                        IEditorPart part = IDE.openEditor(wp, file);
                        
                        Object window = null;                        
                        
                        try {
                            if (part.getClass().getName() == "com.trolltech.qtdesigner.editors.UiEditor") {
                                Method m = part.getClass().getMethod("formWindow", null);                            
                                window = m.invoke(part, null);                                                        
                            }
                            
                            if (window != null) {
                                String objectName = mainPage.getClassName(); 
                                if (objectName.length() == 0)                                
                                    objectName = fname.substring(0, fname.lastIndexOf("."));
                                
                                
                                Method m = window.getClass().getMethod("setObjectName", new Class[] { String.class });
                                m.invoke(window, new Object[] { objectName });
                                
                                m = window.getClass().getMethod("save", null);
                                m.invoke(window, null);
                            }
                        } catch (Exception e) {
                            ErrorReporter.reportError(e, "Couldn't introspect editor: " + part.getClass().getName());
                        }
                            
                    }

                    wsd = ResourcesPlugin.getWorkspace().getDescription();
                    wsd.setAutoBuilding(wasAutoBuilding);
                    ResourcesPlugin.getWorkspace().setDescription(wsd);
                                                                
                    return true;
                } catch (CoreException e) {
                    ErrorReporter.reportError(e, "Error creating form: " + name);
                }

                
            }            
        } 
        return false;        
    }

}
