package com.trolltech.qtproject.pages;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import com.trolltech.qtproject.QtProjectPlugin;
import com.trolltech.qtproject.editors.ProConfiguration;
import com.trolltech.qtproject.editors.ProEditor;
import com.trolltech.qtproject.editors.ProPartitionScanner;

public class ProSourcePage extends TextEditor implements IFormPage
{
	private FormEditor formEditor;
	private int pindex;
	private boolean pactive;
	
	public ProSourcePage(ProEditor editor)
	{
		super();
		formEditor = editor;
		setSourceViewerConfiguration(new ProConfiguration());
	}
	
	public boolean canLeaveThePage() {
		return true;
	}
	
	public boolean isEditable() {
		return false;
	}

	
	public IDocument getDocument()
	{
		return getSourceViewer().getDocument();		
	}
	
    public FormEditor getEditor() {
    	return formEditor;
    }
    
    public String getId() {
    	return "com.trolltech.QtProEditor.pages.ProSourcePage";
    }
    
    public int getIndex() {
    	return pindex;
    }
    
    public IManagedForm getManagedForm() {
    	return null;
    }
    
    public Control getPartControl() {
    	if (getSourceViewer() != null) {
    		IDocumentPartitioner partitioner = 
    			new FastPartitioner(QtProjectPlugin.getDefault().getProPartitionScanner(),
    					ProPartitionScanner.PARTITION_TYPES);

    		partitioner.connect(getSourceViewer().getDocument());
    		getSourceViewer().getDocument().setDocumentPartitioner(partitioner);    		
    	}
    	
    	return getSourceViewer().getTextWidget();
    }
    
    public void initialize(FormEditor editor) {
    	formEditor = editor;
    }
    
	public boolean isActive() {
		return pactive;
	}
	
    public boolean isEditor() {
    	return true;    	
    }
    
    public boolean selectReveal(Object object) {
    	return false;
    }
    
    public void setActive(boolean active) {
    	pactive = active;
    }
    
    public void setIndex(int index) {
    	pindex = index;
    }	
}
