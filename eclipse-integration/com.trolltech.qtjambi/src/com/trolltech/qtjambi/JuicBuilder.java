package com.trolltech.qtjambi;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.util.ILogger;
import org.eclipse.jface.util.Policy;

public class JuicBuilder extends IncrementalProjectBuilder {
	
	private Process juicProc = null;

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		
		if (juicProc != null) {
			try {
				juicProc.exitValue();
			} catch(java.lang.IllegalThreadStateException e) {
				return null;				
			}
		}
		        
        IJavaProject jpro = JavaCore.create(getProject());
        
        IWorkspaceRoot wroot = jpro.getJavaModel().getWorkspace().getRoot();
		IClasspathEntry[] classpaths = jpro.getResolvedClasspath(true);
        String juicpath = jpro.getResource().getPersistentProperty(new QualifiedName("",
                QtJambiPropertyPage.JUICPROPERTY));                
        
		for (int i=0; i<classpaths.length; ++i) {
			IClasspathEntry classpath = classpaths[i];
			if (classpath.getEntryKind() != IClasspathEntry.CPE_SOURCE)
				continue;
			
            // Try to find resource handle of the class path entry in the project
            IResource res = null;
            IPath path = classpath.getPath();
		    try {
                res = wroot.findMember(path);
            } catch (Exception e) {
                ILogger logger = Policy.getLog();
                logger.log(new Status(
                        Status.WARNING,
                        "qtJambiPlugin",
                        Status.OK,
                        "Could not get project for " + classpath.getPath().toString(),
                        e
                ));                
                continue ;
            }
            
                        
            IPath cpath = res.getLocation();                                
			
			if (juicpath == null)
				juicpath = "juic";
			
            String excluded_directories = "";
            {
                IPath exclusions[] = classpath.getExclusionPatterns();
                for (int j=0;j<exclusions.length; ++j) {
                    if (j>0) 
                        excluded_directories += System.getProperty("path.separator");
                    excluded_directories += wroot.findMember(path.append(exclusions[j])).getLocation().toOSString();
                }
            }
            
            String included_directories = "";
            {
                IPath inclusions[] = classpath.getInclusionPatterns();
                for (int j=0;j<inclusions.length;++j) {
                    if (j>0) 
                        included_directories += System.getProperty("path.separator");
                    included_directories += wroot.findMember(path.append(inclusions[j])).getLocation().toOSString();
                }
            }
                        
            int argument_count = 3;
            argument_count += excluded_directories.length() > 0 ? 2 : 0;
            argument_count += included_directories.length() > 0 ? 2 : 0;
            
			Runtime rt = Runtime.getRuntime();
			String[] juicargs = new String[argument_count];
			juicargs[0] = juicpath;
			juicargs[1] = "-cp";			
			juicargs[2] = cpath.toOSString();
            
            int j = 3;
            if (excluded_directories.length() > 0) {
                juicargs[j++] = "-e";
                juicargs[j++] = excluded_directories;
            }
            
            if (included_directories.length() > 0) {
                juicargs[j++] = "-i";
                juicargs[j++] = included_directories;
            }
            			
			try {
				juicProc = rt.exec(juicargs);
			} catch(IOException e) {
				ILogger logger = Policy.getLog();
				logger.log(new Status(
						Status.WARNING,
						"qtJambiPlugin",
						Status.OK,
						"Could not run " + juicpath,
						e
						));		
			}
            
                        
            wroot.refreshLocal(IResource.DEPTH_INFINITE, null);
		}
		
		return null;
	}

}
