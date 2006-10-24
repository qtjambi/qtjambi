package com.trolltech.qtproject.pages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;

import com.trolltech.qtproject.ProEditorModelManager;
import com.trolltech.qtproject.QtProjectPlugin;
import com.trolltech.qtproject.editors.ProEditor;
import com.trolltech.qtproject.editors.ProEditorActionContributor;

public class ProCommonPage extends FormPage implements SelectionListener,
	DetailsViewListener
{
	private ExplorerView explorerview = null;
	private DetailsView detailsview = null;
	private ProEditor m_editor;
	private Button advancedButton;
	
	public ProCommonPage(ProEditor editor)
	{
		super(editor, "com.trolltech.QtProEditor.pages.ProCommonPage", "Settings");
		m_editor = editor;
	}

	public void setActive(boolean active)
	{
		super.setActive(active);
	}
	
	public void widgetSelected(SelectionEvent e)
	{

	}
	
	public void widgetDefaultSelected(SelectionEvent e)
	{
		
	}
	
	public boolean isDirty()
	{
		return explorerview.isDirty();		
	}
	
	public void doSave(IProgressMonitor monitor)
	{
		FileEditorInput fin = (FileEditorInput)getEditorInput();
		if (!explorerview.save()) {
    		MessageDialog.openError(getSite().getShell(),
    				"Error while saving file",
    				"Can't write to: " + fin.getFile().getLocation().toOSString()
    				+ "\nMake sure it's not write protected.");
		}
	}
	
	public DetailsView getDetailsView() {
		return detailsview;
	}
	
	protected void createFormContent(IManagedForm managedForm)
	{
		ScrolledForm form = managedForm.getForm();
		form.setText("Qt Project Settings");

		FormToolkit toolkit = managedForm.getToolkit();

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		form.getBody().setLayout(layout);

		// Explorer Part
		SectionPart part = new SectionPart(form.getBody(), 
				managedForm.getToolkit(), Section.TITLE_BAR|Section.DESCRIPTION);

		Section sec = part.getSection();
		GridData secgd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.FILL_VERTICAL);
		sec.setLayoutData(secgd);
		
		createExplorerView(toolkit, sec);		
		managedForm.addPart(part);
		
		// Value Part
		part = new SectionPart(form.getBody(), 
				managedForm.getToolkit(), Section.TITLE_BAR|Section.DESCRIPTION);

		sec = part.getSection();
		secgd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.FILL_VERTICAL);
		secgd.verticalSpan = 2;
		sec.setLayoutData(secgd);
		
		createValueView(toolkit, sec);	
		managedForm.addPart(part);
		
		// Details Part
		part = new SectionPart(form.getBody(), 
				managedForm.getToolkit(), Section.TITLE_BAR|Section.DESCRIPTION);

		sec = part.getSection();
		secgd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.FILL_VERTICAL);
		sec.setLayoutData(secgd);
		
		createDetailsView(toolkit, sec);	
		managedForm.addPart(part);
		
		FileEditorInput fin = (FileEditorInput)getEditorInput();
		String fileName = fin.getFile().getLocation().toOSString();
		
		ProEditorModelManager manager = QtProjectPlugin.getDefault().getModelManager();
		if (!manager.hasModel(fileName)) {
			int handle = explorerview.createAndShowModel(fileName);
			manager.registerModelHandle(fileName, handle);
		} else {
			explorerview.showModel(manager.getModelHandle(fileName));
		}
	}
	
	private void createExplorerView(FormToolkit toolkit, Section sec)
	{
		sec.setText("Scope Explorer");
		sec.setDescription("This section represents the scopes in you project file.");

		explorerview = new ExplorerView(sec, SWT.EMBEDDED);
		toolkit.adapt(explorerview);
		toolkit.paintBordersFor(explorerview);

		sec.setClient(explorerview);
	}
	
	private void createDetailsView(FormToolkit toolkit, Section sec)
	{
		sec.setText("Order Editor");
		sec.setDescription("In this sections you are able to rearrange the order of the settings.");
		
		Composite client = toolkit.createComposite(sec, SWT.WRAP);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		client.setLayout(layout);
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.FILL_VERTICAL);
		
		detailsview = new DetailsView(client, SWT.EMBEDDED);
		detailsview.setLayoutData(gd);
		toolkit.adapt(detailsview);

		explorerview.setDetailsViewHandle(detailsview.handle());
		
		advancedButton = toolkit.createButton(client, "Advanced Mode", SWT.CHECK);
		advancedButton.setText("Advanced Mode");
		
		toolkit.paintBordersFor(client);

		sec.setClient(client);
		
		explorerview.addExplorerViewListener(new ProFileListener(m_editor));
		advancedButton.addSelectionListener(new AdvancedListener(explorerview));
		
		detailsview.addDetailsViewListener(this);
	}
	
	private void createValueView(FormToolkit toolkit, Section sec)
	{
		sec.setText("Value Editor");
		sec.setDescription("This sections allows you to change the values of the selected setting.");

		ValueView valueview = new ValueView(sec, SWT.EMBEDDED);
		toolkit.adapt(valueview);
				
		explorerview.setValueViewHandle(valueview.handle());

		toolkit.paintBordersFor(valueview);

		sec.setClient(valueview);
	}
	
	public String contents() {
		return explorerview.contents();
	}

	public void actionChanged(int id) {
		IEditorActionBarContributor cont = getEditorSite().getActionBarContributor();                
		if (cont instanceof ProEditorActionContributor) {
			ProEditorActionContributor pecont = (ProEditorActionContributor)cont;
			pecont.updateAction(id);
		}
	}
}

class AdvancedListener implements SelectionListener {
	ExplorerView explorerview;
	
	AdvancedListener(ExplorerView explorerview) {
		this.explorerview = explorerview;		
	}
	

	public void widgetDefaultSelected(SelectionEvent e) {
		
	}

	public void widgetSelected(SelectionEvent e) {
		explorerview.enableAdvanced(((Button)e.widget).getSelection());				
	}
}

class ProFileListener implements ExplorerViewListener {
	private ProEditor m_editor;
	
	public ProFileListener(ProEditor editor) {
		m_editor = editor;
	}

	public void changed() {
		m_editor.editorDirtyStateChanged();		
	}
	
}
