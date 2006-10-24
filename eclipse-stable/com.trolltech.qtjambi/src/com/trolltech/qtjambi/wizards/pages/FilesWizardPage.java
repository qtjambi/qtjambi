package com.trolltech.qtjambi.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class FilesWizardPage extends WizardPage  {
    private Text classLE;
    private Combo uiType;

    class TextListener implements KeyListener {
        public void keyPressed(KeyEvent e) {
        }

        public void keyReleased(KeyEvent e) {
           setPageComplete(canFlipToNextPage());
        }
    }

    public FilesWizardPage() {
        super("com.trolltech.qtproject.FilesWizardPage");
        
        setDescription("Setup template files for the project.");
        setTitle("Files");
    }

    public String getClassName() {
        return classLE.getText();
    }

    public String getUiClassName() {
        return uiType.getText();
    }

    public void createControl(Composite parent) {
        Composite mainComposite = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        mainComposite.setLayout(layout);

        Label cl = new Label(mainComposite, SWT.PUSH);
        cl.setText("Class Name:");

        classLE = new Text(mainComposite, SWT.BORDER | SWT.SINGLE);
        classLE.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

        Label uiTypeLable = new Label(mainComposite, SWT.PUSH);
        uiTypeLable.setText("UI Type:");

        uiType = new Combo(mainComposite, SWT.READ_ONLY);
        uiType.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
        uiType.setItems(new String[] { "QWidget", "QMainWindow", "QDialog" });
        uiType.select(0);

        classLE.addKeyListener(new TextListener());

        setControl(mainComposite);
        setPageComplete(true);
    }

    public boolean isPageComplete() {
        return !classLE.getText().equals("");
    }
}
