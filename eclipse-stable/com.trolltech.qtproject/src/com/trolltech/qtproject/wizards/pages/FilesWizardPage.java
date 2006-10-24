package com.trolltech.qtproject.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FilesWizardPage extends WizardPage {
	private Text classLE;

	private Text cppLE;

	private Text hLE;

	private Text uiLE;

	private Combo uiType;

	class TextListener implements KeyListener {
		public TextListener(WizardPage page) {
			super();
		}

		public void keyPressed(KeyEvent e) {
		}

		public void keyReleased(KeyEvent e) {
			checkValidPage();
		}
	}

	public FilesWizardPage(String pageName) {
		super(pageName);
		setDescription("Setup template files for the project.");
		setTitle("Files");
	}

	public void setClassName(String name) {
		if (classLE != null) {
			classLE.setText(name);
			updateLEs(name);
		}
	}

	public String getClassName() {
		return classLE.getText();
	}

	public String getSourceFileName() {
		return cppLE.getText();
	}

	public String getHeaderFileName() {
		return hLE.getText();
	}

	public String getUIFileName() {
		return uiLE.getText();
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
		classLE.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));

		Label cppl = new Label(mainComposite, SWT.PUSH);
		cppl.setText("Source Filename:");

		cppLE = new Text(mainComposite, SWT.BORDER | SWT.SINGLE);
		cppLE.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
		cppLE.addKeyListener(new TextListener(this));

		Label hl = new Label(mainComposite, SWT.PUSH);
		hl.setText("Header Filename:");

		hLE = new Text(mainComposite, SWT.BORDER | SWT.SINGLE);
		hLE.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
		hLE.addKeyListener(new TextListener(this));

		Label uil = new Label(mainComposite, SWT.PUSH);
		uil.setText("UI Filename:");

		uiLE = new Text(mainComposite, SWT.BORDER | SWT.SINGLE);
		uiLE.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
		uiLE.addKeyListener(new TextListener(this));

		Label uiTypeLable = new Label(mainComposite, SWT.PUSH);
		uiTypeLable.setText("UI Type:");

		uiType = new Combo(mainComposite, SWT.READ_ONLY);
		uiType.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
		uiType.setItems(new String[] { "QWidget", "QMainWindow", "QDialog" });
		uiType.select(0);

		classLE.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				updateLEs(classLE.getText());
				checkValidPage();
			}
		});

		setControl(mainComposite);
		setPageComplete(true);
	}

	private void updateLEs(String text) {
		cppLE.setText(text + ".cpp");
		hLE.setText(text + ".h");
		uiLE.setText(text + ".ui");
	}

	private void checkValidPage() {
		Text[] text = { classLE, cppLE, hLE, uiLE };
		for (int i = 0; i < text.length; i++) {
			String string = text[i].getText().trim();
			if (string.length() > 0 && string.contains(" \\/")) { 
				setPageComplete(false);
				return;
			}
		}
		setPageComplete(true);
	}
}
