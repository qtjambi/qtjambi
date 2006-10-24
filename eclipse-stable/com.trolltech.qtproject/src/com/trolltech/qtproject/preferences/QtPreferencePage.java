package com.trolltech.qtproject.preferences;

import java.util.Vector;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.trolltech.qtproject.QtProjectPlugin;
import com.trolltech.qtproject.utils.QtCDTUtils;

class QtVersionDialogListener extends SelectionAdapter {
	public final static int OK = 1;
	public final static int CANCEL = 2;
	public final static int BROWSE = 3;
	
	private QtVersionDialog m_dlg;
	private int m_control;
	
	public QtVersionDialogListener(QtVersionDialog dlg, int control)
	{
		m_control = control;
		m_dlg = dlg;
	}
	
	public void widgetSelected(SelectionEvent e) {
		if (m_control == OK) {
			m_dlg.accept(true);
		} else if (m_control == CANCEL) {
			m_dlg.accept(false);
		} else {
			m_dlg.browse();		
		}
	}	
};

class QtVersionDialog extends Dialog {
	private boolean m_accepted;
	private Text location;
	private Shell shell;

	public QtVersionDialog(Shell parent, int style) {
		super(parent, style);
		m_name = new String();
		m_location = new String();
		m_accepted = false;
	}
	
	public void accept(boolean accepted) {
		m_accepted = accepted;
		shell.close();
	}
	
	public void browse() {
		DirectoryDialog dirdlg = new DirectoryDialog(shell);
		dirdlg.setMessage("Select Qt Installation Directory");
		String dir = dirdlg.open();
		
		if (dir != null)
			location.setText(dir);					
	}
	
	private void createForm(Shell shell, Shell parent) {
		
		Rectangle rect = parent.getBounds();
		int x = rect.x + ((rect.width - 250) / 2);
		int y = rect.y + ((rect.height - 150) / 2);
		shell.setBounds(x, y, 250, 150);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		shell.setLayout(layout);
		
		Label label = new Label(shell, SWT.NONE);
		label.setText("Name");
		
		Text name = new Text(shell, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		
		name.setLayoutData(gridData);
		name.setText(m_name);
		name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				QtVersionDialog.m_name = ((Text)e.widget).getText();				
			}
		});

		label = new Label(shell, SWT.NONE);
		label.setText("Location");
		location = new Text(shell, SWT.BORDER);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;		
		location.setLayoutData(gridData);

		location.setText(m_location);
		location.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				QtVersionDialog.m_location = ((Text)e.widget).getText();				
			}
		});
		
		Button browse = new Button(shell, SWT.NONE);
		browse.setText("...");
		browse.addSelectionListener(new QtVersionDialogListener(this, QtVersionDialogListener.BROWSE));
		
		Composite buttongrp = new Composite(shell, SWT.NONE);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.verticalAlignment = GridData.END;
		buttongrp.setLayoutData(gridData);
		
		layout = new GridLayout();
		layout.numColumns = 2;
		buttongrp.setLayout(layout);
		
		Button cancel = new Button(buttongrp, SWT.NONE);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.minimumWidth = 80;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.END;
		cancel.setLayoutData(gridData);		
		cancel.setText("Cancel");
		cancel.addSelectionListener(new QtVersionDialogListener(this, QtVersionDialogListener.CANCEL));

		Button ok = new Button(buttongrp, SWT.NONE);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.minimumWidth = 80;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.END;
		ok.setLayoutData(gridData);		
		ok.setText("OK");
		ok.addSelectionListener(new QtVersionDialogListener(this, QtVersionDialogListener.OK));
	}
	
	public boolean open() {
		Shell parent = getParent();
		shell = new Shell(parent, SWT.RESIZE | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(getText());

		createForm(shell, parent);
		
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
	
		return m_accepted;
	}
	
	// public fields
	static public String m_name;	
	static public String m_location;
}

class QtVersionListener extends SelectionAdapter
{
	public final static int ADD = 1;
	public final static int EDIT = 2;
	public final static int REMOVE = 3;
	public final static int DEFAULT = 4;
	public final static int SELECTION = 5;
	
	private QtPreferencePage m_prefpage;
	private int m_control;
	
	public QtVersionListener(QtPreferencePage prefpage, int control)
	{
		m_control = control;
		m_prefpage = prefpage;
	}
	
	public void widgetSelected(SelectionEvent e)
	{
		QtVersionDialog dlg = new QtVersionDialog(m_prefpage.getShell(), SWT.NONE);
		if (m_control == ADD) {
			dlg.setText("Add Qt Version");
			if (dlg.open())
				m_prefpage.addItem(QtVersionDialog.m_name, QtVersionDialog.m_location);
		} else if (m_control == EDIT) {
			String[] current = m_prefpage.getCurrentItem();
			QtVersionDialog.m_name = current[0];
			QtVersionDialog.m_location = current[1];
			dlg.setText("Edit Qt Version");
			if (dlg.open())
				m_prefpage.updateItem(QtVersionDialog.m_name, QtVersionDialog.m_location);
		} else if (m_control == REMOVE) {
			m_prefpage.removeItem();
		} else if (m_control == DEFAULT) {
			m_prefpage.setCurrentDefault();
		} else if (m_control == SELECTION) {
			Table table = (Table)e.widget;
			m_prefpage.enableButtons(table.getSelectionCount() > 0);
		}
	}
}

public class QtPreferencePage extends PreferencePage
	implements IWorkbenchPreferencePage {
	
	private Table table;
	private TableColumn column;
	private Button removeButton;
	private Button editButton;
	private Button defaultButton;
	private Button autosetmkspec;
	private Button autosetmkcmd;
	
	public QtPreferencePage() {
		
	}
	
	public static boolean getAutoSetMkSpec() {
		IPreferenceStore store = QtProjectPlugin.getDefault().getPreferenceStore();
		
		if (store.contains(PreferenceConstants.QT_AUTOSETMKSPEC))
			return store.getBoolean(PreferenceConstants.QT_AUTOSETMKSPEC);
		else
			return true;
	}

	public static boolean getAutoSetMkCmd() {
		IPreferenceStore store = QtProjectPlugin.getDefault().getPreferenceStore();
		
		if (store.contains(PreferenceConstants.QT_AUTOSETMKCMD))
			return store.getBoolean(PreferenceConstants.QT_AUTOSETMKCMD);
		else
			return true;
	}
	
	public static String[] getQtVersions() {
		Vector versions = new Vector();

		IPreferenceStore store = QtProjectPlugin.getDefault().getPreferenceStore();
		if (!store.contains(PreferenceConstants.QTVERSION_COUNT))
			return null;
		
		int count = store.getInt(PreferenceConstants.QTVERSION_COUNT);
		for (int i=0; i<count; ++i) {
			String name = PreferenceConstants.QTVERSION_NAME + "." + Integer.toString(i);
			if (store.contains(name))
				versions.add(store.getString(name));
		}
		
		return (String[])versions.toArray(new String[versions.size()]);
		
	}
	
	public static String getQtVersionPath(String version) {
		IPreferenceStore store = QtProjectPlugin.getDefault().getPreferenceStore();
		if (!store.contains(PreferenceConstants.QTVERSION_COUNT))
			return null;
		
		int count = store.getInt(PreferenceConstants.QTVERSION_COUNT);
		for (int i=0; i<count; ++i) {
			String name = PreferenceConstants.QTVERSION_NAME + "." + Integer.toString(i);
			String path = PreferenceConstants.QTVERSION_PATH + "." + Integer.toString(i);
			
			if (store.contains(name))
				name = store.getString(name);
			if (store.contains(path))
				path = store.getString(path);
			
			if (name.equals(version))
				return path;
		}
		
		return store.getString(PreferenceConstants.QTVERSION_DEFAULT);
	}
	
	public String[] getCurrentItem() {
		TableItem[] items = table.getSelection();
		if (items.length == 0)
			return null;
		
		String itemtext[] = new String[2];
		itemtext[0] = items[0].getText(0);
		itemtext[1] = items[0].getText(1);
		return itemtext;
	}
	
	public void enableButtons(boolean enabled) {
		removeButton.setEnabled(enabled);
		defaultButton.setEnabled(enabled);
		editButton.setEnabled(enabled);
	}
	
	public void updateItem(String name, String location) {
		TableItem[] items = table.getSelection();
		if (items.length == 0)
			return;

		String itemtext[] = new String[2];
		itemtext[0] = name;
		itemtext[1] = location;
		items[0].setText(itemtext);
	}
	
	public void addItem(String name, String location) {
		TableItem item = new TableItem(table, SWT.NONE);
		String itemtext[] = new String[2];
		itemtext[0] = name;
		itemtext[1] = location;
		item.setText(itemtext);
	}
	
	public void removeItem() {
		if (table.getSelectionCount() > 0) {
			int removeindex = table.getSelectionIndex(); 
			int defindex = getDefaultIndex();
			table.remove(removeindex);
			
			if (removeindex == defindex)
				setDefault(0);
		}
		
		enableButtons(table.getSelectionCount() > 0);
	}
	
	public boolean performOk()
	{
		IPreferenceStore store = QtProjectPlugin.getDefault().getPreferenceStore();
		store.setValue(PreferenceConstants.QTVERSION_DEFAULT, getDefault());
		
		store.setValue(PreferenceConstants.QTVERSION_COUNT, table.getItemCount());
		for (int i=0; i<table.getItemCount(); ++i) {
			store.setValue(PreferenceConstants.QTVERSION_NAME + "." + Integer.toString(i),
					table.getItem(i).getText(0));
			store.setValue(PreferenceConstants.QTVERSION_PATH + "." + Integer.toString(i),
					table.getItem(i).getText(1));
		}
		
		store.setValue(PreferenceConstants.QT_AUTOSETMKSPEC, autosetmkspec.getSelection());
		store.setValue(PreferenceConstants.QT_AUTOSETMKCMD, autosetmkspec.getSelection());
		
		// updates all the Qt projects
		QtCDTUtils.updateQtDir();
		return true;
	}
	
	private void setDefault(int index) {
		for (int i=0; i<table.getItemCount(); ++i) {
			TableItem item = table.getItem(i); 
			Font fnt = item.getFont();
			FontData fntdata = fnt.getFontData()[0];
			
			if (i == index) {
				int style = fntdata.getStyle() | SWT.BOLD;
				fntdata.setStyle(style);
				item.setFont(new Font(fnt.getDevice(), fntdata));				
			} else if ((fntdata.getStyle() & SWT.BOLD) != 0) {
				int style = fntdata.getStyle() & ~SWT.BOLD;
				fntdata.setStyle(style);
				item.setFont(new Font(fnt.getDevice(), fntdata));
			}
		}
	}
	
	private int getDefaultIndex() {
		for (int i=0; i<table.getItemCount(); ++i) {
			TableItem item = table.getItem(i); 
			Font fnt = item.getFont();
			FontData fntdata = fnt.getFontData()[0];
			
			if ((fntdata.getStyle() & SWT.BOLD) != 0)
				return i; 
		}
		
		if (table.getItemCount() > 0)
			return 0;
		
		return -1;
	}
	
	private String getDefault() {
		int index = getDefaultIndex();
		if (index == -1)
			return null;

		return table.getItem(index).getText(1);
	}
	
	public void setCurrentDefault() {
		setDefault(table.getSelectionIndex());
	}
	
	private void updateItems()
	{
		IPreferenceStore store = QtProjectPlugin.getDefault().getPreferenceStore();
		if (!store.contains(PreferenceConstants.QTVERSION_COUNT))
			return;
		
		int defversionindex = 0;
		String defversion = store.getString(PreferenceConstants.QTVERSION_DEFAULT);
		
		int count = store.getInt(PreferenceConstants.QTVERSION_COUNT);
		for (int i=0; i<count; ++i) {
			String name = PreferenceConstants.QTVERSION_NAME + "." + Integer.toString(i);
			String path = PreferenceConstants.QTVERSION_PATH + "." + Integer.toString(i);
			
			if (store.contains(name))
				name = store.getString(name);
			if (store.contains(path))
				path = store.getString(path);
			addItem(name, path);
			
			if (path.equals(defversion))
				defversionindex = i;
		}
		
		autosetmkspec.setSelection(getAutoSetMkSpec());
		autosetmkcmd.setSelection(getAutoSetMkCmd());
		
		setDefault(defversionindex);
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		column.setWidth(table.getSize().x - (100 + (table.getBorderWidth() * 2)));		
	}

	private void addQtBuildsSection(Composite parent) {
		table = new Table(parent, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Name");
		column.setWidth(100);
		column.setResizable(true);
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Location");
		column.setResizable(true);
		column.setWidth(150);
		
		GridData gridData = new GridData();
		gridData.verticalSpan = 5;
		gridData.widthHint = 250;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		
		table.setLayoutData(gridData);
		table.addSelectionListener(new QtVersionListener(this, QtVersionListener.SELECTION));
		
		Button addButton = new Button(parent, SWT.NONE);
		addButton.addSelectionListener(new QtVersionListener(this, QtVersionListener.ADD)); 
		addButton.setText("Add...");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		addButton.setLayoutData(gridData);
		
		editButton = new Button(parent, SWT.NONE);
		editButton.addSelectionListener(new QtVersionListener(this, QtVersionListener.EDIT));
		editButton.setText("Edit...");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		editButton.setLayoutData(gridData);
		
		removeButton = new Button(parent, SWT.NONE);
		removeButton.addSelectionListener(new QtVersionListener(this, QtVersionListener.REMOVE));
		removeButton.setText("Remove");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		removeButton.setLayoutData(gridData);
		
		defaultButton = new Button(parent, SWT.NONE);
		defaultButton.addSelectionListener(new QtVersionListener(this, QtVersionListener.DEFAULT));
		defaultButton.setText("Default");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		defaultButton.setLayoutData(gridData);
		
		Composite spacer = new Composite(parent, SWT.NONE);
		gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		spacer.setLayoutData(gridData);
		
		autosetmkspec = new Button(parent, SWT.CHECK);
		autosetmkspec.setText("Auto update QMAKESPEC when applying changes.");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		autosetmkspec.setLayoutData(gridData);
		
		autosetmkcmd = new Button(parent, SWT.CHECK);
		autosetmkcmd.setText("Auto update make command when applying changes.");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		autosetmkcmd.setLayoutData(gridData);
	}

	protected Control createContents(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		addQtBuildsSection(composite);
		updateItems();
		enableButtons(table.getSelectionCount() > 0);
		
		return composite;
	}
	
	public void init(IWorkbench workbench) {
				
	}
}