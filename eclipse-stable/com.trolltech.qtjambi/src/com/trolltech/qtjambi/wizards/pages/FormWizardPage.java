package com.trolltech.qtjambi.wizards.pages;

import java.io.*;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import com.trolltech.qtjambi.ErrorReporter;

public class FormWizardPage extends NewTypeWizardPage {

    private final static String PAGE_NAME = "NewQTUIFormWizardPage"; //$NON-NLS-1$

    private Tree tree;
    private Label preview;

    private static String TEMPLATE_PATH = "com/trolltech/qtjambi/templates";
    private static String TEMPLATE_LIST_FILENAME = "templates.txt";

    public FormWizardPage() {
        super(true, PAGE_NAME);

        setTitle("New Qt Designer Form");
        setDescription("Create a new Qt Designer Form");
    }

    private void setupTemplates() {
        ClassLoader loader = getClass().getClassLoader();

        InputStream stream = loader.getResourceAsStream(TEMPLATE_PATH + "/" + TEMPLATE_LIST_FILENAME);
        if (stream != null) {
            BufferedReader r = new BufferedReader(new InputStreamReader(stream));

            try {
                String s = null;
                while ((s = r.readLine()) != null) {
                    TreeItem item = new TreeItem(tree, SWT.NULL);

                    item.setText(s.replace('_', ' '));
                    item.setData(TEMPLATE_PATH + "/" + s);

                    if (s.equals("Widget")) {
                        tree.setSelection(new TreeItem[] { item });
                    }
                }
            } catch (IOException e) {
                ErrorReporter.reportError(e, "Couldn't read templates in New Form Wizard");
            }
        } else {
            ErrorReporter.reportError(null, "Can't find resource: " + TEMPLATE_PATH + "/" + TEMPLATE_LIST_FILENAME);
        }
    }

    public void init(IStructuredSelection selection) {
        IJavaElement jelem = getInitialJavaElement(selection);
        initContainerPage(jelem);
        initTypePage(jelem);
        doStatusUpdate();
    }

    private void doStatusUpdate() {
        // status of all used components
        IStatus[] status = new IStatus[] { fContainerStatus, isEnclosingTypeSelected() ? fEnclosingTypeStatus : fPackageStatus, fTypeNameStatus, fModifierStatus, fSuperClassStatus,
                fSuperInterfacesStatus };

        doUIFormTypeUpdates();

        // the mode severe status will be displayed and the OK button
        // enabled/disabled.
        updateStatus(status);
    }

    protected void handleFieldChanged(String fieldName) {
        super.handleFieldChanged(fieldName);

        doStatusUpdate();
    }

    protected void createUIFormTypeControls(Composite composite, int nColumns) {
        Composite formComposit = new Composite(composite, SWT.NONE);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = nColumns;
        formComposit.setLayoutData(gridData);

        formComposit.setLayout(new GridLayout(2, false));
        tree = new Tree(formComposit, SWT.BORDER);
        tree.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                doStatusUpdate();
            }

            public void widgetSelected(SelectionEvent e) {
                doStatusUpdate();
            }

        });
        tree.setLayoutData(new GridData(SWT.None, SWT.FILL, false, false));

        preview = new Label(formComposit, SWT.NONE);
        preview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        setupTemplates();
        doStatusUpdate();
    }

    protected void doUIFormTypeUpdates() {
        if (preview == null)
            return;

        Image image = null;
        try {
            String name = tree.getSelection()[0].getData() + ".png";
            InputStream stream = getClass().getClassLoader().getResourceAsStream(name);
            image = new Image(preview.getDisplay(), stream);
        } catch (RuntimeException e) {
        }

        preview.setImage(image);
        preview.pack();
    }

    public void createControl(Composite parent) {
        initializeDialogUnits(parent);

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());

        int nColumns = 4;

        GridLayout layout = new GridLayout();
        layout.numColumns = nColumns;
        composite.setLayout(layout);

        // pick & choose the wanted UI components

        createContainerControls(composite, nColumns);
        createPackageControls(composite, nColumns);

        createSeparator(composite, nColumns);

        createTypeNameControls(composite, nColumns);

        createSeparator(composite, nColumns);

        createUIFormTypeControls(composite, nColumns);

        setControl(composite);

        Dialog.applyDialogFont(composite);
    }

    public boolean isPageComplete() {
        return super.isPageComplete() && (tree.getSelectionCount() > 0);
    }

    public String getPathToTemplate() {
        return tree.getSelection()[0].getData() + ".ui";
    }
}
