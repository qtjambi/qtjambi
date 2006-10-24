package com.trolltech.qtjambi;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class JuicBuilder extends IncrementalProjectBuilder {
	
	private Process juicProc = null;
    
    private IClasspathEntry[] newSourceEntry(IClasspathEntry[] raw_classpath, IPath path)
    {        
        IClasspathEntry[] new_classpath = new IClasspathEntry[raw_classpath.length + 1];
        
        // Check for and remove nesting in new classpath
        for (int i=0; i<raw_classpath.length; ++i) {
            new_classpath[i] = raw_classpath[i];
            
            if (raw_classpath[i].getEntryKind() == IClasspathEntry.CPE_SOURCE && raw_classpath[i].getPath().isPrefixOf(path)) {
                IPath[] exclusionPatterns = raw_classpath[i].getExclusionPatterns();                
                IPath relative_path = 
                    path.removeFirstSegments(raw_classpath[i].getPath().segmentCount()).addTrailingSeparator();
                                
                // Don't add it twice
                boolean found = false;
                for (int j=0; j<exclusionPatterns.length; ++j) {
                    IPath exclusion = exclusionPatterns[j];
                    if (exclusion.equals(relative_path))
                        found = true;                    
                }
                
                if (!found) {
                    IPath[] new_exclusions = new IPath[exclusionPatterns.length + 1];
                    System.arraycopy(exclusionPatterns, 0, new_exclusions, 0, exclusionPatterns.length);
                    new_exclusions[exclusionPatterns.length] = relative_path; 
                        
                    new_classpath[i] = JavaCore.newSourceEntry(raw_classpath[i].getPath(), 
                            raw_classpath[i].getInclusionPatterns(), new_exclusions, 
                            raw_classpath[i].getOutputLocation(), 
                            raw_classpath[i].getExtraAttributes());                    
                } 
            }
        }

        new_classpath[raw_classpath.length] = JavaCore.newSourceEntry(path);        
        return new_classpath;
    }
    
    private boolean uiFilesInContainer(IContainer container)
    {
        IResource[] resources = null;
        try {
            resources = container.members();
        } catch (CoreException e) {
            return false;
        }
        
        for (int i=0; i<resources.length; ++i) {
            IResource resource = resources[i];
            if (resource.getType() == IResource.FILE && resource.getName().endsWith(".ui")) {
                return true;
            } else if (resource instanceof IContainer) {
                if (uiFilesInContainer((IContainer)resource))
                    return true;
            }
        }
        
        return false;
    }

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
                QtJambiPropertyPage.JUIC_LOCATION_PROPERTY));
        String destinationFolder = jpro.getResource().getPersistentProperty(new QualifiedName("",
                QtJambiPropertyPage.JUIC_DESTINATIONFOLDER_PROPERTY));
        String useDestinationFolder = jpro.getResource().getPersistentProperty(new QualifiedName("",
                QtJambiPropertyPage.JUIC_USEDESTINATIONFOLDER_PROPERTY));
        
        if (juicpath == null)
            juicpath = QtJambiPropertyPage.defaultValue(QtJambiPropertyPage.JUIC_LOCATION_PROPERTY);
        if (destinationFolder == null)
            destinationFolder = QtJambiPropertyPage.defaultValue(QtJambiPropertyPage.JUIC_DESTINATIONFOLDER_PROPERTY);
        if (useDestinationFolder == null)
            useDestinationFolder = QtJambiPropertyPage.defaultValue(QtJambiPropertyPage.JUIC_USEDESTINATIONFOLDER_PROPERTY);
        
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
                ErrorReporter.reportError(e, "Could not get project for " + classpath.getPath().toString());
                continue ;
            }
            
                        
            IPath cpath = res.getLocation();                                
						
            String excluded_directories = "";
            {
                IPath exclusions[] = classpath.getExclusionPatterns();
                for (int j=0;j<exclusions.length; ++j) {
                    IResource member = wroot.findMember(path.append(exclusions[j]));                    
                    if (j>0) 
                        excluded_directories += System.getProperty("path.separator");
                    
                    if (member != null) {
                        excluded_directories += member.getLocation().toOSString();
                    } else {
                        member = wroot.findMember(path);
                        if (member != null) {
                            excluded_directories += member
                                                        .getLocation()
                                                        .addTrailingSeparator()
                                                        .append(exclusions[j])
                                                        .toOSString();
                        }                                                
                    }
                }
            }

            String included_directories = "";
            {
                IPath inclusions[] = classpath.getInclusionPatterns();
                for (int j=0;j<inclusions.length;++j) {
                    IResource member = wroot.findMember(path.append(inclusions[j]));
                    if (j>0) 
                        included_directories += System.getProperty("path.separator");
                    
                    if (member != null) {
                        included_directories += member.getLocation().toOSString();
                    } else {
                        member = wroot.findMember(path);
                        if (member != null) {
                            included_directories += member
                                                    .getLocation()
                                                    .addTrailingSeparator()
                                                    .append(inclusions[j])
                                                    .toOSString();
                        }
                    }
                }
            }
            
            IProject current_project = res.getProject();
            IFolder projuiced_files_in = null;
            
            if (Boolean.parseBoolean(useDestinationFolder) && destinationFolder.length() > 0) {                                
                projuiced_files_in = current_project.getFolder(destinationFolder);
                if (!projuiced_files_in.exists() && uiFilesInContainer(jpro.getProject())) {
                    try {
                        projuiced_files_in.create(true, true, monitor);
                        
                        ResourceAttributes attributes = projuiced_files_in.getResourceAttributes();
                        projuiced_files_in.setResourceAttributes(attributes);
                        
                        IClasspathEntry[] new_classpath = newSourceEntry(jpro.getRawClasspath(), projuiced_files_in.getFullPath());                          
                        jpro.setRawClasspath(new_classpath, monitor);
                    } catch (CoreException e) {
                        ErrorReporter.reportError(e, "Could not create directory for generated JUIC files: " 
                                                      + projuiced_files_in.getLocation().toOSString());
                    }
                }
            }
                                    
            int argument_count = 3;
            argument_count += projuiced_files_in != null ? 2 : 0;
            argument_count += excluded_directories.length() > 0 ? 2 : 0;
            argument_count += included_directories.length() > 0 ? 2 : 0;
                        
			Runtime rt = Runtime.getRuntime();
			String[] juicargs = new String[argument_count];
			juicargs[0] = juicpath;
			juicargs[1] = "-cp";			
			juicargs[2] = cpath.toOSString();
            
            int j = 3;
            if (projuiced_files_in != null) {
                juicargs[j++] = "-d";
                juicargs[j++] = projuiced_files_in.getLocation().toOSString();              
            }
            
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
                juicProc.waitFor();
                
			} catch(IOException e) {
                ErrorReporter.reportError(e, "Could not run " + juicpath);
			} catch(InterruptedException e) {
                ErrorReporter.reportError(e, "Interrupted: " + juicpath);
            }
            
            wroot.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		
		return null;
	}

}
