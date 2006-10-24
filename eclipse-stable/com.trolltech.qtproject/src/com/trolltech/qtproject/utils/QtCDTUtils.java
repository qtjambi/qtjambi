package com.trolltech.qtproject.utils;

import java.util.Map;
import java.util.Vector;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.ICDescriptor;
import org.eclipse.cdt.core.ICDescriptorOperation;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.IIncludeEntry;
import org.eclipse.cdt.core.model.IPathEntry;
import org.eclipse.cdt.core.resources.IPathEntryStore;
import org.eclipse.cdt.make.core.IMakeBuilderInfo;
import org.eclipse.cdt.make.core.IMakeTarget;
import org.eclipse.cdt.make.core.IMakeTargetManager;
import org.eclipse.cdt.make.core.MakeBuilder;
import org.eclipse.cdt.make.core.MakeCorePlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.trolltech.qtproject.QtNature;
import com.trolltech.qtproject.launch.QtLaunchConfig;
import com.trolltech.qtproject.preferences.QtPreferencePage;
import com.trolltech.qtproject.properties.QtPropertyPage;

public class QtCDTUtils {
	private static final boolean WINDOWS = java.io.File.separatorChar == '\\';
	public static final String QT_RELEASE_NAME = "Qt Release Build";
	public static final String QT_DEBUG_NAME = "Qt Debug Build";

	public static void configureMake(IProject project) {
		try {
			IMakeBuilderInfo info = MakeCorePlugin.createBuildInfo(project, MakeBuilder.BUILDER_ID);
			info.setAppendEnvironment(true);
			info.setUseDefaultBuildCmd(false);
			info.setBuildAttribute(IMakeBuilderInfo.BUILD_TARGET_INCREMENTAL, "");
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		if (Platform.getOS().equals(Platform.OS_WIN32))
			setBinaryParser(project, "org.eclipse.cdt.core.PE");

		createQtTargets(project);
		updateQtDir(project);
	}
	
	private static void setBinaryParser(IProject project, String id) {
		class TmpICDescriptorOperation implements ICDescriptorOperation {
			public String m_id;
			public void execute(ICDescriptor descriptor, IProgressMonitor monitor) throws CoreException {
				descriptor.create(CCorePlugin.BINARY_PARSER_UNIQ_ID, m_id);
			}			
		}
		
		TmpICDescriptorOperation op = new TmpICDescriptorOperation();
		op.m_id = id;
		
		try {
			CCorePlugin.getDefault().getCDescriptorManager().runDescriptorOperation(project, op, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	private static void createQtTargets(IProject project) {
		MakeCorePlugin makecore = MakeCorePlugin.getDefault();
		IMakeTargetManager targetManager = makecore.getTargetManager();
		try {
			String[] builders = targetManager.getTargetBuilders(project);
			for(int i=0; i<builders.length; ++i) {
				IMakeTarget target = targetManager.findTarget(project, QT_RELEASE_NAME);
				if (target == null) {
					target = targetManager.createTarget(project, QT_RELEASE_NAME, builders[i]);
					targetManager.addTarget(target);
				}
				initTarget(target, "release");
				
				target = targetManager.findTarget(project, QT_DEBUG_NAME);
				if (target == null) {
					target = targetManager.createTarget(project, QT_DEBUG_NAME, builders[i]);
					targetManager.addTarget(target);
				}
				initTarget(target, "debug");
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	private static void initTarget(IMakeTarget target, String config) {
		try {
			target.setBuildAttribute(IMakeTarget.BUILD_TARGET, config);
			target.setUseDefaultBuildCmd(false);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateQtDir() {
		IProject[] pros = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i=0; i<pros.length; ++i) {
			QtCDTUtils.updateQtDir(pros[i]);
		}
	}
	
	public static void updateQtDir(IProject project) {
		try {
			if (!project.hasNature(QtNature.QT_NATURE_ID))
				return;
			
			String qtdir = QtPropertyPage.getQtDir(project);
			if (qtdir == null)
				return;
			
			boolean setmkcmd = QtPreferencePage.getAutoSetMkCmd();
			
			IMakeBuilderInfo info = MakeCorePlugin.createBuildInfo(project, MakeBuilder.BUILDER_ID);
			Map env = info.getEnvironment();
			
			String oldqtdir = (String)env.get("QTDIR");
			if (oldqtdir != null && oldqtdir.equals(qtdir))
				return; // no need for update
			
			String mkspec = QtUtils.getMakeSpec(qtdir);
			if (QtPreferencePage.getAutoSetMkSpec() && mkspec != null)
				env.put("QMAKESPEC", mkspec);
			
			String mkcmd = null;
			if (setmkcmd)
				mkcmd = QtUtils.getMakeCommand(mkspec);
			
			env.put("QTDIR", qtdir);
			String path = createPath((String)env.get("PATH"), 
					(new Path(qtdir + "/bin")).toOSString(), 
					(new Path(oldqtdir + "/bin")).toOSString());
			env.put("PATH", path);
			info.setEnvironment(env);
			
			if (setmkcmd)
				info.setBuildAttribute(IMakeBuilderInfo.BUILD_COMMAND, mkcmd);											

			MakeCorePlugin makecore = MakeCorePlugin.getDefault();
			IMakeTargetManager targetManager = makecore.getTargetManager();
			String[] builders = targetManager.getTargetBuilders(project);
			for(int i=0; i<builders.length; ++i) {
				IMakeTarget target = targetManager.findTarget(project, QT_RELEASE_NAME);
				if (target != null) {
					if (setmkcmd)
						target.setBuildAttribute(IMakeTarget.BUILD_COMMAND, mkcmd);											
					target.setEnvironment(env);					
				}
				target = targetManager.findTarget(project, QT_DEBUG_NAME);
				if (target != null) {
					if (setmkcmd)
						target.setBuildAttribute(IMakeTarget.BUILD_COMMAND, mkcmd);											
					target.setEnvironment(env);
				}
			}
			
			// update the launch configs
			QtLaunchConfig.updateLaunchPaths(project, 
					(new Path(qtdir + "/bin")).toOSString(),
					(new Path(oldqtdir + "/bin")).toOSString());
			
			// update the include directories
			updateQtIncludeDir(project, qtdir, oldqtdir);
		} catch (CoreException e) {
			// ### this throws an exception if the .project file is read-only 
			e.printStackTrace();
		}
	}
	
	public static String createPath(String path, String prepend, String remove) {
		Vector result = new Vector();
		result.add(prepend);
		
		if (remove != null)
			result.add(remove);
		
		
		String splitchar = ";";
		if (!WINDOWS)
			splitchar = ":";
		
		if (path != null) {
			String[] pathlist = path.split(splitchar);
			for (int i=0; i<pathlist.length; ++i) {
				if (!result.contains(pathlist[i]))
					result.add(pathlist[i]);
			}
		}
		
		// system environment
		path = System.getenv("PATH");
		
		if (path != null) {		
			String[] pathlist = path.split(splitchar);
			for (int i=0; i<pathlist.length; ++i) {
				if (!result.contains(pathlist[i]))
					result.add(pathlist[i]);
			}
		}
		
		if (remove != null)
			result.remove(remove);
		
		path = prepend;
		for (int i=1; i<result.size(); ++i) {
			path += splitchar + (String)result.get(i);
		}
		
		return path;
	}
	
	private static void updateQtIncludeDir(IProject project, String newqtdir, String oldqtdir)
	{
		try {
			Vector v = new Vector();
			IPathEntryStore store = CoreModel.getPathEntryStore(project);
			
			IPath oldpath = null;
			if (oldqtdir != null)
				oldpath = CoreModel.newIncludeEntry(new Path(""), new Path(oldqtdir), new Path("include")).getFullIncludePath();
			IIncludeEntry newpathentry = CoreModel.newIncludeEntry(new Path(""), new Path(newqtdir), new Path("include"));
			IPath newpath = newpathentry.getFullIncludePath();
			v.add(newpathentry);
			
			IPathEntry[] paths = store.getRawPathEntries();
			for (int i=0; i<paths.length; ++i) {
				if (paths[i] instanceof IIncludeEntry) {
					IPath inc = ((IIncludeEntry)paths[i]).getFullIncludePath();
					if ((oldpath != null && inc.equals(oldpath)) || inc.equals(newpath))
						continue;
				}
				v.add(paths[i]);
			}
			
			store.setRawPathEntries((IPathEntry[])v.toArray(new IPathEntry[v.size()]));
		} catch (CoreException e) {}
	}
}
