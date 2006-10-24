package com.trolltech.qtproject.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class QtUtils {
	public static String getMakeSpec(String qtdir) {
		try {
			Path qmakecash = new Path(qtdir + "/.qmake.cache");
			BufferedReader in = new BufferedReader(new FileReader(qmakecash.toOSString()));
			while (true) {
				String line = in.readLine();
				if (line == null)
					break;
				if (line.startsWith("QMAKESPEC")) {
					String [] args = line.split("=");
					if (args.length == 2) {
						line = args[1].trim();
						line = line.replace('\\', '/');
						int lastindex = line.lastIndexOf('/');
						if (lastindex == -1)
							return line.trim();
						return line.substring(lastindex + 1).trim();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getMakeCommand(String mkspec) {
		if (mkspec != null) {
			if (mkspec.endsWith("win32-g++")) {
				return "mingw32-make";
			} else if(mkspec.contains("win32")) {
				return "nmake";
			}
		}
		
		return "make";		
	}
	
	public static IPath removeFileNameFromIPath(IPath path) {
		if (path == null)
			return null;
		return path.removeLastSegments(1);
	}
	
	public static String getFileNameFromIPath(IPath path, boolean noextension) {
		if (path == null)
			return null;
		
		if (noextension)
			path = path.removeFileExtension();
		
		String name = path.toString().replace('\\', '/').trim();
		int index = name.lastIndexOf('/');
		if (index == -1)
			return name;
		
		return name.substring(index + 1);
	}
	
	public static boolean isFormFile(String filename) {
		return filename.toLowerCase().endsWith(".ui");				
	}
	
	public static boolean isResourceFile(String filename) {
		return filename.toLowerCase().endsWith(".qrc");				
	}
	
	public static IPath findProFile(IProject pro) {
		QtProjectVisitor visitor = new QtProjectVisitor();
		Vector result = visitor.findFiles(pro, "pro");
		if (result.size() <= 0)
			return null;
		return ((IResource)result.get(0)).getLocation();			
	}
}
