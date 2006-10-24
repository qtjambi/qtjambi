package com.trolltech.common;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.dialogs.ContainerGenerator;

public class QtWizardUtils {
	public static boolean addTemplateFile(InputStream src, File dest, Map replace) {
		try {
			FileOutputStream out = new FileOutputStream(dest);

			String outstr = patchTemplateFile(src, replace);
			out.write(outstr.getBytes());
			out.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static String patchTemplateFile(InputStream src, Map replace) {
		InputStreamReader fr = new InputStreamReader(src);
		BufferedReader br = new BufferedReader(fr);

		StringBuffer result = new StringBuffer();
		String line;
		try {
			while ((line = br.readLine()) != null) {
				if (replace != null) {
					for (Iterator iter = replace.keySet().iterator(); iter.hasNext();) {
						String key = (String) iter.next();
						line = line.replaceAll(key, (String) replace.get(key));
					}
				}
				result.append(line + "\n");
			}
			
			br.close();
			fr.close();
		} catch (IOException e) {
			return "";
		}
		
		return result.toString();
	}
	
	public static IFile createFile(IPath fpath, IProgressMonitor monitor) throws CoreException {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IFile file = root.getFileForLocation(fpath);
        
        if (file == null) {
        	file = root.getFile(fpath);
        }
        
        if (file == null || file.exists()) {
            monitor.done();
            return file;
        }

        // create container if not existing
        if (fpath.segmentCount() > 1) {
	        IPath cpath = fpath.removeLastSegments(1);
	        if (root.getContainerForLocation(cpath) == null) {
	            ContainerGenerator generator = new ContainerGenerator(cpath);
	            generator.generateContainer(monitor);
	        }
        }
                
        return file;
	}	
}
