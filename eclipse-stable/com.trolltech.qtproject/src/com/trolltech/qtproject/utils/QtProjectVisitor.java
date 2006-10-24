package com.trolltech.qtproject.utils;

import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;

public class QtProjectVisitor implements IResourceProxyVisitor {
	private Vector result;
	private String extention;
	
	public QtProjectVisitor() {
		result = new Vector();
	}

	public boolean visit(IResourceProxy proxy) throws CoreException {
		if (proxy.getName().endsWith(extention) && proxy.getType() == IResource.FILE) {
			result.add(proxy.requestResource());
			return false;
		}
		return true;
	}
	
	public Vector findFiles(IProject project, String ext) {
		extention = "." + ext;
		try {
			result.clear();
			project.accept(this, IResource.NONE);
			return result;
		} catch (Exception e) {
			return null;
		}
	}
}