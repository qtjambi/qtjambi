package com.trolltech.qtjambi.wizards;

import java.io.InputStream;
import java.lang.reflect.Method;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;

import com.trolltech.qtjambi.ErrorReporter;
import com.trolltech.qtjambi.wizards.pages.FormWizardPage;

public class FormWizard extends Wizard implements INewWizard {
    private FormWizardPage mainPage;

    public FormWizard() {
        super();
        setWindowTitle("New Qt Designer Form");
    }

    public void addPages() {
        super.addPages();

        addPage(mainPage);
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        mainPage = new FormWizardPage();
        mainPage.init(selection);
    }

    public boolean performFinish() {
        IPackageFragmentRoot packageRoot = mainPage.getPackageFragmentRoot();
        IPackageFragment pack = mainPage.getPackageFragment();
        if (pack == null) {
            pack = packageRoot.getPackageFragment(""); //$NON-NLS-1$
        }

        if (!pack.exists()) {
            String packName = pack.getElementName();

            try {
                pack = packageRoot.createPackageFragment(packName, true, null);
            } catch (JavaModelException e) {
                ErrorReporter.reportError(e, "Error creating package: " + packName);
            }
        }

        IPath container = mainPage.getPackageFragment().getPath();
        String name = mainPage.getTypeName() + ".ui";
        String pathToTemplate = mainPage.getPathToTemplate();

        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(container.append(name));
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
                            String objectName = mainPage.getTypeName();

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

        return false;
    }
}