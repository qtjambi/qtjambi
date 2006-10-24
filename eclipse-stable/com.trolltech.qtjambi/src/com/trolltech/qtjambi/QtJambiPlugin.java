package com.trolltech.qtjambi;

import org.eclipse.ui.plugin.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ElementChangedEvent;


public class QtJambiPlugin extends AbstractUIPlugin implements org.eclipse.ui.IStartup {

	private static QtJambiPlugin plugin;
	
	public QtJambiPlugin() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	public static QtJambiPlugin getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("QtJambi", path);
	}

	public void earlyStartup() {
		ProjectListener proListener = new ProjectListener();
		
		JavaCore.addElementChangedListener(proListener, 
				ElementChangedEvent.POST_CHANGE);
		
		proListener.addBuilderToAllProjects();
	}
}
