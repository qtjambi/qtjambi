package com.trolltech.qtproject.qmake;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CommandLauncher;
import org.eclipse.cdt.core.resources.IConsole;
import org.eclipse.cdt.make.core.IMakeBuilderInfo;
import org.eclipse.cdt.make.core.MakeBuilder;
import org.eclipse.cdt.make.core.MakeCorePlugin;
import org.eclipse.cdt.utils.spawner.EnvironmentReader;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;

import com.trolltech.qtproject.properties.QtPropertyPage;
import com.trolltech.qtproject.utils.QtUtils;

public class QtMakefileGenerator extends IncrementalProjectBuilder
{
	private static final boolean WINDOWS = java.io.File.separatorChar == '\\';
	private String errMsg;
	
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException
	{
		if (kind != AUTO_BUILD) {		
			if (!runQMake(monitor))
				monitor.setCanceled(true);
		}
		
		return null;
	}
	
	protected void clean(IProgressMonitor monitor) {
		// do nothing for now...
	}
		
	public boolean runQMake(IProgressMonitor monitor)
	{
		checkCancel(monitor);
		IProject currentProject = getProject();
		
		IPath proFile = QtUtils.findProFile(currentProject);
		
		if(proFile == null)
			return false;
		
		if (monitor == null)
			monitor = new NullProgressMonitor();
				
		monitor.subTask("Qt Makefile Generator - Collecting Data" + currentProject.getName());
		
		CommandLauncher launcher = new CommandLauncher();
		launcher.showCommand(true);
		
		String qtdir = QtPropertyPage.getQtDir(currentProject);
		
		IPath buildCommand = new Path(qtdir + "/bin/qmake");
		
		Vector buildArguments = new Vector();
		
		if (!WINDOWS)
			buildArguments.add("CONFIG+=debug_and_release");			
		
		buildArguments.add(QtUtils.getFileNameFromIPath(proFile, false));

		IPath workingDir = QtUtils.removeFileNameFromIPath(proFile);
		
		Properties envProps = EnvironmentReader.getEnvVars();
		IMakeBuilderInfo info;
		try {
			info = MakeCorePlugin.createBuildInfo(currentProject, MakeBuilder.BUILDER_ID);
			Map buildenv = info.getEnvironment();
			envProps.setProperty("CWD",workingDir.toOSString());
			envProps.setProperty("PWD",workingDir.toOSString());
			String mkspec = (String)buildenv.get("QMAKESPEC");
			if (mkspec != null)
				envProps.setProperty("QMAKESPEC", mkspec);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		
		String[] env = createEnvStringList(envProps);
		
/*		System.out.println("qmake command: " + buildCommand.toOSString());
		System.out.println("profile: " + (String)buildArguments.get(0));
		System.out.println("workingdir: " + workingDir.toOSString()); */		
		
		errMsg = null;
		boolean result = true;
		try {
			IConsole console = CCorePlugin.getDefault().getConsole();
			console.start(currentProject);
			OutputStream consoleOut = console.getOutputStream();
			OutputStream consoleErr = console.getErrorStream();
			
			Process p = launcher.execute(buildCommand, 
					(String[])buildArguments.toArray(new String[buildArguments.size()]),
					env, workingDir);
			if (p != null) {
				try {
					p.getOutputStream().close();
				} catch (IOException e) {}
				
				monitor.subTask("Qt Makefile Generator - Starting QMake" + launcher.getCommandLine());
				
				if (launcher.waitAndRead(consoleOut, consoleErr, new SubProgressMonitor(monitor, 0))
					!= CommandLauncher.OK)
					errMsg = launcher.getErrorMessage();
				
				result = p.exitValue() == 0;
				monitor.subTask("Qt Makefile Generator - QMake Finished");

				try {
					getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
				} catch (CoreException e) {
				}
			} else {
				errMsg = launcher.getErrorMessage();
			}
			if (errMsg != null) {
				StringBuffer buf = new StringBuffer(buildCommand.toString() + " ");
				for (int i = 0; i < buildArguments.size(); i++) {
					buf.append(buildArguments.get(i));
					buf.append(' ');
				}
				String errorDesc = "Qt Makefile Generator - QMake Error: " + System.getProperty("line.separator", "\n")
					+ buf.toString();
				buf = new StringBuffer(errorDesc);
				buf.append(System.getProperty("line.separator", "\n"));
				buf.append("(").append(errMsg).append(")");
				consoleErr.write(buf.toString().getBytes());
				consoleErr.flush();
				result = false;				
			}
			consoleOut.close();
			consoleErr.close();			    
		} catch (Exception e) {
			result = false;
			CCorePlugin.log(e);			
		}
		
		return result;
	}
			
	public void checkCancel(IProgressMonitor monitor) {
		if (monitor != null && monitor.isCanceled())
			throw new OperationCanceledException();
	}
	
	public String[] createEnvStringList(Properties envProps)
	{
		String[] env = null;
		ArrayList envList = new ArrayList();
		Enumeration names = envProps.propertyNames();
		if (names != null) {
			while (names.hasMoreElements()) {
				String key = (String) names.nextElement();
				envList.add(key + "=" + envProps.getProperty(key));
			}
			env = (String[]) envList.toArray(new String[envList.size()]);
		}
		return env;
	}
}
