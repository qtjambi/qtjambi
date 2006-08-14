package com.trolltech.qtjambi;

import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.UIPlugin;

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
            System.out.println("folder: " + folderName + ", name: " + name);
            container = container.append(folderName).addTrailingSeparator();
            
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
                            break; 
                        }
                    }
                }
                
                try {
                    folder.create(true, true, null);
                } catch (CoreException e) {}
            }
                      
            String fname = name.substring(name.lastIndexOf('/'));
            container = container.append(fname);
            IFile file = root.getFile(container);
            if (!file.exists()) {                
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                InputStream stream = cl.getResourceAsStream(pathToTemplate);
                
                try {
                    file.create(stream, true, null);     
                    IWorkbench wb = PlatformUI.getWorkbench();                    
                                        
                    IWorkbenchWindow ww = null;
                    if (wb != null)
                        ww = wb.getActiveWorkbenchWindow();
                    
                    IWorkbenchPage wp = null;
                    if (ww != null)
                        wp = ww.getActivePage();
                    
                    if (wp != null)
                        IDE.openEditor(wp, file);
                                                                
                    return true;
                } catch (CoreException e) {}

                
            }            
        } 
        return false;        
    }

}
