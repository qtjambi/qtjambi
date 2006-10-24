package com.trolltech.qtproject.editors;

import org.eclipse.ui.forms.editor.*;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.core.runtime.IProgressMonitor;

import com.trolltech.qtproject.ProEditorModelManager;
import com.trolltech.qtproject.QtProjectPlugin;
import com.trolltech.qtproject.pages.*;

public class ProEditor extends FormEditor {
	private ProSourcePage srcPage = null;
	private ProCommonPage comPage = null;
	
	public ProEditor() {
		super();
	}
	
	public void doSave(IProgressMonitor monitor)
	{
		comPage.doSave(monitor);
	}
	
	public void doSaveAs()
	{
	
	}
	
	public boolean isSaveAsAllowed()
	{
		return false;
	}
	
	public DetailsView getDetailsView() {
		return comPage.getDetailsView();
	}
	
	protected void addPages()
	{
		try {
			comPage = new ProCommonPage(this);
			addPage(comPage);
			srcPage = new ProSourcePage(this);
			addPage(srcPage, getEditorInput());
		} catch(Exception e) {
			//### show warning
		}
		
		setPartName(getEditorInput().getName());
	}
	
	public boolean isDirty() {
		return comPage.isDirty();
	}
	
	public void dispose()
	{
		FileEditorInput fin = (FileEditorInput)getEditorInput();
		String fileName = fin.getFile().getLocation().toOSString();
		
		ProEditorModelManager manager = QtProjectPlugin.getDefault().getModelManager();
		manager.unregisterModelHandle(fileName);
		
		super.dispose();
	}
	
	protected void pageChange(int newPageIndex) {
		if (newPageIndex == 1) {
			srcPage.getDocument().set(comPage.contents());
		}
		
		super.pageChange(newPageIndex);
	}
}
