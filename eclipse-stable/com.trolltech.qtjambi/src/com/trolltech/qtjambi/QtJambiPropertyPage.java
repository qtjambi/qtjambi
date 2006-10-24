package com.trolltech.qtjambi;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.PropertyPage;

public class QtJambiPropertyPage extends PropertyPage {

    private static final String JUICLABEL = "Location of JUIC";
    private static final String DESTINATIONFOLDER_LABEL = "Place generated files in separate folder";

    public static final String JUIC_LOCATION_PROPERTY = "JuicLocationProperty";
    public static final String JUIC_DESTINATIONFOLDER_PROPERTY = "JuicDestinationFolderProperty";
    public static final String JUIC_USEDESTINATIONFOLDER_PROPERTY = "JuicUseDestinationFolderProperty";

    public Text juicPathText;
    public Text juicDestinationFolderText;
    public Button juicUseDestinationFolder;

    public QtJambiPropertyPage() {
        super();
    }

    private IResource getResource(IAdaptable iAdaptable) {
        IResource resource = null;
        if (getElement() instanceof IJavaProject) {
            resource = ((IJavaProject) getElement()).getResource();
        } else if (getElement() instanceof IProject) {
            resource = (IProject) getElement();
        }
        return resource;
    }

    private void addJuicSection(Composite parent) {
        IResource resource = getResource(getElement());

        String useDestinationFolder = null;
        String destinationFolder = null;
        String currentPath = null;
        try {
            currentPath = resource.getPersistentProperty(new QualifiedName("", JUIC_LOCATION_PROPERTY));
        } catch (Exception e) {
        }

        try {
            destinationFolder = resource.getPersistentProperty(new QualifiedName("", JUIC_DESTINATIONFOLDER_PROPERTY));
        } catch (Exception e) {
        }

        try {
            useDestinationFolder = resource.getPersistentProperty(new QualifiedName("", JUIC_USEDESTINATIONFOLDER_PROPERTY));
        } catch (Exception e) {
        }

        Group groupBox = new Group(parent, SWT.SHADOW_ETCHED_IN);
        {
            groupBox.setText(JUICLABEL);

            GridLayout layout = new GridLayout(2, false);
            groupBox.setLayout(layout);

            GridData gridData = new GridData();
            gridData.grabExcessHorizontalSpace = true;
            gridData.grabExcessVerticalSpace = true;
            gridData.horizontalAlignment = SWT.FILL;
            gridData.verticalAlignment = SWT.FILL;
            groupBox.setLayoutData(gridData);

        }

        {
            juicPathText = new Text(groupBox, SWT.SINGLE | SWT.BORDER);

            GridData gridData = new GridData();
            gridData.grabExcessHorizontalSpace = true;
            gridData.horizontalAlignment = GridData.FILL;
            gridData.verticalAlignment = GridData.CENTER;
            juicPathText.setLayoutData(gridData);

            Button browsButton = new Button(groupBox, SWT.PUSH);
            browsButton.setText("Browse...");
            gridData = new GridData();
            gridData.grabExcessHorizontalSpace = false;
            gridData.horizontalAlignment = GridData.FILL;
            gridData.verticalAlignment = SWT.TOP;
            browsButton.setLayoutData(gridData);
            browsButton.addSelectionListener(new SelectionListener() {
                public void widgetDefaultSelected(SelectionEvent e) {
                    chooseJuicFolder();
                }

                public void widgetSelected(SelectionEvent e) {
                    chooseJuicFolder();
                }
            });

            new Composite(groupBox, SWT.NONE);

            Button validateButton = new Button(groupBox, SWT.PUSH);
            validateButton.setText("Validate...");
            validateButton.setLayoutData(gridData);
            validateButton.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent e) {
                    validateJuicFolder();
                }

                public void widgetSelected(SelectionEvent e) {
                    validateJuicFolder();
                }
            });
        }

        groupBox = new Group(parent, SWT.SHADOW_ETCHED_IN);
        {
            groupBox.setText(DESTINATIONFOLDER_LABEL);

            GridLayout layout = new GridLayout(2, false);
            groupBox.setLayout(layout);

            GridData gridData = new GridData();
            gridData.grabExcessHorizontalSpace = true;
            gridData.grabExcessVerticalSpace = true;
            gridData.horizontalAlignment = SWT.FILL;
            gridData.verticalAlignment = SWT.FILL;
            groupBox.setLayoutData(gridData);
        }

        {
            juicUseDestinationFolder = new Button(groupBox, SWT.CHECK);
            GridData gridData = new GridData();
            gridData.verticalAlignment = GridData.CENTER;
            juicUseDestinationFolder.setLayoutData(gridData);

            juicDestinationFolderText = new Text(groupBox, SWT.SINGLE | SWT.BORDER);
            gridData = new GridData();
            gridData.grabExcessHorizontalSpace = true;
            gridData.horizontalAlignment = GridData.FILL;
            gridData.verticalAlignment = GridData.CENTER;
            juicDestinationFolderText.setLayoutData(gridData);
        }

        if (currentPath == null)
            currentPath = defaultValue(JUIC_LOCATION_PROPERTY);
        if (destinationFolder == null)
            destinationFolder = defaultValue(JUIC_DESTINATIONFOLDER_PROPERTY);
        if (useDestinationFolder == null)
            useDestinationFolder = defaultValue(JUIC_USEDESTINATIONFOLDER_PROPERTY);

        juicPathText.setText(currentPath);
        juicDestinationFolderText.setText(destinationFolder);
        juicUseDestinationFolder.setSelection(Boolean.parseBoolean(useDestinationFolder));

        juicDestinationFolderText.setEnabled(juicUseDestinationFolder.getSelection());

        juicUseDestinationFolder.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent s) { /*
                                                                     * not
                                                                     * called
                                                                     */
            }

            public void widgetSelected(SelectionEvent s) {
                if (juicUseDestinationFolder.getSelection())
                    juicDestinationFolderText.setEnabled(true);
                else
                    juicDestinationFolderText.setEnabled(false);
            }
        });
    }

    public static String defaultValue(String propertyName) {
        if (propertyName.equals(JUIC_LOCATION_PROPERTY))
            return "juic";
        else if (propertyName.equals(JUIC_DESTINATIONFOLDER_PROPERTY))
            return "Generated JUIC files";
        else if (propertyName.equals(JUIC_USEDESTINATIONFOLDER_PROPERTY))
            return "true";

        throw new IllegalArgumentException("Attempted to fetch default value for unknown property '" + propertyName + "'");
    }

    protected Control createContents(Composite parent) {

        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(1, false);
        composite.setLayout(layout);

        addJuicSection(composite);
        return composite;
    }

    protected void performDefaults() {
        juicPathText.setText("juic");
    }

    public boolean performOk() {
        IResource resource = getResource(getElement());

        try {
            resource.setPersistentProperty(new QualifiedName("", JUIC_LOCATION_PROPERTY), juicPathText.getText());

            resource.setPersistentProperty(new QualifiedName("", JUIC_DESTINATIONFOLDER_PROPERTY), juicDestinationFolderText.getText());

            resource.setPersistentProperty(new QualifiedName("", JUIC_USEDESTINATIONFOLDER_PROPERTY), new Boolean(juicUseDestinationFolder.getSelection()).toString());

        } catch (Exception e) {
            ErrorReporter.reportError(e, "Couldn't set properties");
            return false;
        }

        return true;
    }

    private void chooseJuicFolder() {
        String initPath = juicPathText.getText(); //$NON-NLS-1$

        File initFile = new File(initPath);

        FileDialog dialog = new FileDialog(this.getShell());
        dialog.setText("Select Juic executable");
        dialog.setFilterPath(initFile.getParent());
        dialog.setFileName(initFile.getName());
        String result = dialog.open();
        if (result != null) {
            juicPathText.setText(result);
        }
    }

    private void validateJuicFolder() {
        Runtime rt = Runtime.getRuntime();
        String[] juicargs = new String[2];
        juicargs[0] = juicPathText.getText().trim();
        juicargs[1] = "-v";

        String res = "";

        Process juicProc = null;
        try {
            juicProc = rt.exec(juicargs);
            juicProc.waitFor();

            BufferedReader stdError = new BufferedReader(new InputStreamReader(juicProc.getErrorStream()));

            String tmp;
            while ((tmp = stdError.readLine()) != null) {
                res += tmp;
            }

            if (res.startsWith("Qt Jambi"))
                res = "SUCSESS!\n\nHere is the output of the command:\n" + res;
            else if (res != "")
                res = "FAILED \n\nHere is the output of the command:\n" + res;
            else
                res = "FAILED";

        } catch (IOException e) {
            res = "Could not run juic: " + juicargs[0];
        } catch (InterruptedException e) {
            res = "Could not run juic " + juicargs[0];
        }

        MessageDialog.openInformation(this.getShell(), "Validating", res);
    }
}