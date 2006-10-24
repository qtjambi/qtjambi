package com.trolltech.qtproject.wizards;

import java.io.ByteArrayInputStream;

import org.eclipse.cdt.ui.wizards.NewClassCreationWizardPage;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.trolltech.qtproject.wizards.pages.UiFileWizardPage;


public class NewQtClassWizard extends AbstractQtClassWizard {
	private NewClassCreationWizardPage m_classpage;
	private UiFileWizardPage m_uipage;

	public NewQtClassWizard() {
		
	}

    public void addPages() {
        super.addPages();
        
        m_classpage = new NewClassCreationWizardPage();
        addPage(m_classpage);
        m_classpage.init(m_selection);
        m_classpage.setTitle("Welcome to the Qt4 Class Wizard");
        m_classpage.setDescription("This wizard will add a new Qt4 class to your project.");
        
        m_uipage = new UiFileWizardPage(this, "");
        addPage(m_uipage);
    }

	protected void createFiles(IProgressMonitor monitor) throws InterruptedException, CoreException {
		m_classpage.createClass(monitor);
		
		String clsName = m_classpage.getClassName();
		
		IFile hFile = m_classpage.getCreatedHeaderFile();
		IFile sFile = m_classpage.getCreatedSourceFile();
		
		StringBuffer hsb = getStringBuffer(hFile);
		StringBuffer ssb = getStringBuffer(sFile);
		addMember(hsb, "\tQ_OBJECT");

		if (m_uipage.isGenerateUIChecked()) {
			m_uipage.createUIFile(monitor);
			String baseClass = m_uipage.getUIClassName();
			addHeaderFile(hsb, "\"" + m_uipage.getUIHeaderFileName() + "\"");
			addBaseClass(hsb, "Ui::" + clsName + "Class");
			addBaseClass(hsb, baseClass);
			replaceConstructor(hsb, clsName, clsName + "(QWidget *parent = 0);");
			replaceConstructor(ssb, clsName, clsName + "(QWidget *parent)\n\t: " + baseClass + "(parent)\n{\n\tsetupUi(this);");
		} else {
			addBaseClass(hsb, "QObject");
			addHeaderFile(hsb, "<QtCore/QObject>");
			replaceConstructor(hsb, clsName, clsName + "(QObject *parent = 0);");
			replaceConstructor(ssb, clsName, clsName + "(QObject *parent)\n\t: QObject(parent)\n{");
		}
		
		hFile.setContents(new ByteArrayInputStream(hsb.toString().getBytes()), true, false, monitor);		
		sFile.setContents(new ByteArrayInputStream(ssb.toString().getBytes()), true, false, monitor);
	}
	
	public String getCurrentHeaderFileName() {
		return m_classpage.getHeaderFileText();
	}
	
	public IPath getSourceFolder() {
        String text = m_classpage.getSourceFolderText();
        if (text.length() > 0)
            return new Path(text).makeAbsolute();
        return null;
	}
	
	public String getClassName() {
		return m_classpage.getClassName();
	}
}
