package com.trolltech.qtjambi;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

public class QtJambiPropertyPage extends PropertyPage {
	
	private static final String JUICLABEL = "Location of JUIC";
    private static final String DESTINATIONFOLDER_LABEL = "Place generated files in separate folder";    
	
    public  static final String JUIC_LOCATION_PROPERTY = "JuicLocationProperty";
    public  static final String JUIC_DESTINATIONFOLDER_PROPERTY = "JuicDestinationFolderProperty";
    public  static final String JUIC_USEDESTINATIONFOLDER_PROPERTY = "JuicUseDestinationFolderProperty";
	
    public Text juicPathText;
    public Text juicDestinationFolderText;
    public Button juicUseDestinationFolder;

	public QtJambiPropertyPage() {
		super();
	}

	private void addJuicSection(Composite parent) {
		IJavaProject jpro = (IJavaProject)getElement();
        
        String useDestinationFolder = null;
        String destinationFolder = null;
		String currentPath = null;
		try {
			currentPath = jpro.getResource().getPersistentProperty(
					new QualifiedName("", JUIC_LOCATION_PROPERTY));
        } catch (Exception e) {
        }
        
        try {
            destinationFolder = jpro.getResource().getPersistentProperty(
                    new QualifiedName("", JUIC_DESTINATIONFOLDER_PROPERTY));
        } catch (Exception e) {
        }
        
        try {
            useDestinationFolder = jpro.getResource().getPersistentProperty(
                    new QualifiedName("", JUIC_USEDESTINATIONFOLDER_PROPERTY));
		} catch (Exception e) {					
		}
        
        Group groupBox = new Group(parent, SWT.SHADOW_ETCHED_IN);                
        {
            groupBox.setText(JUICLABEL);
            
            GridLayout layout = new GridLayout(1, false);
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
            public void widgetDefaultSelected(SelectionEvent s) { /* not called */ }
            
            public void widgetSelected(SelectionEvent s) {
                if (juicUseDestinationFolder.getSelection())
                    juicDestinationFolderText.setEnabled(true);
                else
                    juicDestinationFolderText.setEnabled(false);
            }
        });        
	}
    
    public static String defaultValue(String propertyName) 
    {
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
		try {
			IJavaProject jpro = (IJavaProject)getElement();
			jpro.getResource().setPersistentProperty(
				new QualifiedName("", JUIC_LOCATION_PROPERTY),
				juicPathText.getText());
            
            jpro.getResource().setPersistentProperty(
                    new QualifiedName("", JUIC_DESTINATIONFOLDER_PROPERTY),
                    juicDestinationFolderText.getText());
            
            jpro.getResource().setPersistentProperty(
                    new QualifiedName("", JUIC_USEDESTINATIONFOLDER_PROPERTY),
                    new Boolean(juicUseDestinationFolder.getSelection()).toString());
            
		} catch (Exception e) {
			ErrorReporter.reportError(e, "Couldn't set properties");
            return false;
		}
        
		return true;
	}

}