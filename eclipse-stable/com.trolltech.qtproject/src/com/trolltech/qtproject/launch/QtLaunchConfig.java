package com.trolltech.qtproject.launch;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.launch.AbstractCLaunchDelegate;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

import com.trolltech.qtproject.properties.QtPropertyPage;
import com.trolltech.qtproject.utils.QtCDTUtils;

class LaunchConfigResult {
	public ILaunchConfiguration config;
	public IProject project;
}

public class QtLaunchConfig implements ILaunchConfigurationListener
{
	private static ILaunchConfiguration[] getQtLaunchConfigurations(IProject pro) {
		Vector result = new Vector();
		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations();
			for (int i=0; i<configs.length; ++i) {
				String projectName = AbstractCLaunchDelegate.getProjectName(configs[i]);
				if (projectName != null) {
					projectName = projectName.trim();
					if (projectName.length() > 0) {
						IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
						if (project == pro)
							result.add(configs[i]);
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return (ILaunchConfiguration[])result.toArray(new ILaunchConfiguration[result.size()]);
	}
	
	public static void updateLaunchPaths(IProject pro, String prepend, String remove) {
		ILaunchConfiguration[] configs = getQtLaunchConfigurations(pro);
		for (int i=0; i<configs.length; ++i) {
			updateConfigPath(configs[i], prepend, remove);
		}
	}

	private static void updateConfigPath(ILaunchConfiguration configuration, String prepend, String remove) {
		try {
			ILaunchConfigurationWorkingCopy config = configuration.getWorkingCopy();
			Map m = config.getAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, new HashMap());
			String path = QtCDTUtils.createPath((String)m.get("PATH"), prepend, remove);
			m.put("PATH", path);
			config.setAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, m);
			config.doSave();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public void launchConfigurationAdded(ILaunchConfiguration configuration) {
		try {
			ICProject pro = AbstractCLaunchDelegate.getCProject(configuration);
			String qtdir = QtPropertyPage.getQtDir(pro.getProject());
			Path path = new Path(qtdir + "/bin");
			updateConfigPath(configuration, path.toOSString(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void launchConfigurationChanged(ILaunchConfiguration configuration) {
		
	}

	public void launchConfigurationRemoved(ILaunchConfiguration configuration) {
		
	}
}
