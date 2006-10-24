package com.trolltech.qtproject;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.cdt.make.core.MakeCorePlugin;
import org.eclipse.cdt.make.core.MakeProjectNature;
import org.eclipse.cdt.make.core.scannerconfig.ScannerConfigNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.trolltech.qtproject.editors.ProPartitionScanner;
import com.trolltech.qtproject.launch.QtLaunchConfig;
import com.trolltech.qtproject.utils.QtCDTUtils;

/**
 * The main plugin class to be used in the desktop.
 */
public class QtProjectPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static QtProjectPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	private ProPartitionScanner proPartScanner = null;
	private ProEditorModelManager modelManager = null;
	
	public static final String TEMPLATE_LOCATION = "/com/trolltech/qtproject/wizards/templates/";
	
	/**
	 * The constructor.
	 */
	public QtProjectPlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		new QtProjectMonitor();
		
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		QtLaunchConfig qlc = new QtLaunchConfig();
		launchManager.addLaunchConfigurationListener(qlc);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;
		proPartScanner = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static QtProjectPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = QtProjectPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	public ProEditorModelManager getModelManager() {
		if (modelManager == null)
			modelManager = new ProEditorModelManager();

		return modelManager;
	}
	
	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("com.trolltech.qtproject.QtProjectPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}
	
	public static String getUniqueIdentifier() {
		if (getDefault() == null) {
			return "com.trolltech.qtproject";
		}
		return getDefault().getBundle().getSymbolicName();
	}
	
	public ProPartitionScanner getProPartitionScanner()
	{
		if (proPartScanner == null)
			proPartScanner = new ProPartitionScanner();
		
		return proPartScanner;
	}
	
	public void convertToQtProject(IProject project, IProgressMonitor monitor)	throws CoreException
	{
		if (!project.hasNature(org.eclipse.cdt.make.core.MakeProjectNature.NATURE_ID))
			org.eclipse.cdt.core.CCorePlugin.getDefault().convertProjectToCC(project, monitor, MakeCorePlugin.MAKE_PROJECT_ID);
		
		if (!project.hasNature(MakeProjectNature.NATURE_ID))
				MakeProjectNature.addNature(project, new SubProgressMonitor(monitor, 1));
		if (!project.hasNature(ScannerConfigNature.NATURE_ID))
			ScannerConfigNature.addScannerConfigNature(project);

		addQtNature(project, monitor);
		QtCDTUtils.configureMake(project);
	}
	
	public void addQtNature(IProject project, IProgressMonitor monitor)
		throws CoreException
	{
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
	
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length + 0] = QtProConstants.QTNATURE_ID;

		description.setNatureIds(newNatures);
		project.setDescription(description, monitor);		
	}
}
