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

public class DelayStrategy extends AbstractLaunchStrategy {

	//HARDCODED ATM
	private int seconds = 4; 

	@Override
	public void launchSelectedStrategy() {

		// DO A HARDCODED SIMULATION
		List<String> myList = new ArrayList<>();

		// My simulated sequence of selected launch configurations.
		myList.add("Primes");
		myList.add("Numbers");

		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager
				.getLaunchConfigurationType("org.eclipse.jdt.launching.localJavaApplication");
		ILaunchConfiguration[] configurations;

		// all configurations of "Java Application"
		try {
			configurations = manager.getLaunchConfigurations(type);

			for (int i = 0; i < myList.size(); i++) {
				for (int j = 0; j < configurations.length; j++) {
					if (myList.get(i).equals(configurations[j].getName())) {
						configurations[j].launch("debug", null);
						waitDelay(seconds);
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	private void waitDelay(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
