package com.trolltech.qtproject.wizards;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.cdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public abstract class AbstractQtClassWizard extends Wizard  implements INewWizard {
	protected IStructuredSelection m_selection;
	
	protected StringBuffer getStringBuffer(IFile hFile) throws CoreException {
		InputStream in = hFile.getContents();
		InputStreamReader reader = new InputStreamReader(in);
		
		StringBuffer s = new StringBuffer();
		
		char[] a = new char[1024];
		
		while(true) {
			int size;
			try {
				size = reader.read(a);
			} catch (IOException e) {
				e.printStackTrace();
				return s;
			}
			if (size == -1)
				break;
			s.append(a, 0, size);
		}
		
		return s;
	}
	
	protected void addMember(StringBuffer s, String member) {
		int csindex = s.indexOf("class");
		int mindex = s.indexOf("{", csindex);
		
		if (csindex == -1 || mindex == -1)
			return;
		
		s.insert(mindex + 1, "\n" + member + "\n");		
	}
	
	protected void addHeaderFile(StringBuffer s, String headerFile) {
		int dsindex = s.indexOf("#define");
		int hindex = s.indexOf("\n", dsindex);
		
		if (dsindex == -1 || hindex == -1)
			return;
				
		s.insert(hindex + 1, "\n#include " + headerFile + "\n");
	}
	
	protected void addBaseClass(StringBuffer s, String baseClass) {
		int csindex = s.indexOf("class");
		int ceindex = s.indexOf("{", csindex);
		
		if (csindex == -1 || ceindex == -1)
			return;
		
		int i = s.indexOf(":", csindex);
		if (i != -1 && i < ceindex) {
			s.insert(i+1, " public " + baseClass + ", ");			
		} else {
			ceindex = s.indexOf("\n", csindex);
			s.insert(ceindex, " : public " + baseClass);
		}
	}
	
	protected void replaceConstructor(StringBuffer s, String className, String constructor) {
		String replace = className + "()";
		int sindex = s.indexOf(replace);
		if (sindex == -1)
			return;
		
		if (s.charAt(sindex - 1) == '~')
			return;
		
		int eindex = s.indexOf("{", sindex);
		if (eindex == -1)
			eindex = Integer.MAX_VALUE;
		int tmp = s.indexOf(";", sindex);
		if (tmp == -1)
			tmp = Integer.MAX_VALUE;
		
		if (tmp < eindex)
			eindex = tmp;
		
		if (eindex == Integer.MAX_VALUE)
			return;
		
		s.replace(sindex, eindex + 1, constructor);
	}

	public boolean performFinish() {
		IWorkspaceRunnable op = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
				try {
					createFiles(monitor);
				} catch (InterruptedException e) {
					throw new OperationCanceledException(e.getMessage());
				}
			}
		};
		
		try {
			getContainer().run(false, false, new WorkbenchRunnableAdapter(op));
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		m_selection = selection;	
	}
	
	protected abstract void createFiles(IProgressMonitor monitor) throws InterruptedException, CoreException;
}
