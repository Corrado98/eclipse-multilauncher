package com.profidatagroup.e4.advancedlaunch.strategies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;

import com.profidatagroup.e4.advancedlaunch.tabs.SampleTab;

public abstract class AbstractLaunchStrategy {
	
	private static ILaunch childLaunch;
	

	public void launch() {

		if (SampleTab.launchConfigurationDataList == null) {
			System.out.println("LIST IS NULL!");
			return;
		}

		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager
				.getLaunchConfigurationType("org.eclipse.jdt.launching.localJavaApplication");
		ILaunchConfiguration[] configurations;

		useOptionalProcessTerminatedListener();
		
		// all configurations of "Java Application"
		try {
			configurations = manager.getLaunchConfigurations(type);

			for (int i = 0; i < SampleTab.launchConfigurationDataList.size(); i++) {
				for (int j = 0; j < configurations.length; j++) {
					if (SampleTab.launchConfigurationDataList.get(i).getName().equals(configurations[j].getName())) {
						childLaunch = configurations[j].launch("debug", null);
						launchSelectedStrategy();
						System.out.println(SampleTab.launchConfigurationDataList.get(i) + " was launched!");
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	protected abstract void launchSelectedStrategy();

	/*
	 * <b>ONLY</b> class <b>WaitUntilPriorConfigTerminatedStrategy</b> should override this
	 * method.
	 * 
	 * 
	 */
	protected void useOptionalProcessTerminatedListener() {
		
	}
	
	protected static ILaunch getChildLaunch() {
		return childLaunch;
	}

}
