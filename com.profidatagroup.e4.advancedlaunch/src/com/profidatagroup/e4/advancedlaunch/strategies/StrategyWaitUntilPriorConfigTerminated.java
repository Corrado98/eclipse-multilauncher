package com.profidatagroup.e4.advancedlaunch.strategies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;

import com.profidatagroup.e4.advancedlaunch.tabs.SampleTab;

public class StrategyWaitUntilPriorConfigTerminated extends LaunchStrategy {

	private List<Set<IProcess>> processesToWait = Collections.synchronizedList(new ArrayList<>());

	@Override
	public void launchSelectedStrategy() {

		List<String> myList = SampleTab.list;
		if(myList == null) return;

		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager
				.getLaunchConfigurationType("org.eclipse.jdt.launching.localJavaApplication");
		ILaunchConfiguration[] configurations;
		
		addProcessesTerminateListener();

		// all configurations of "Java Application"
		try {
			configurations = manager.getLaunchConfigurations(type);

			for (int i = 0; i < myList.size(); i++) {
				for (int j = 0; j < configurations.length; j++) {
					if (myList.get(i).equals(configurations[j].getName())) {

						ILaunch childLaunch = configurations[j].launch("debug", null);
						IProcess[] childLaunchProcesses = childLaunch.getProcesses();
						waitForProcessesToTerminate(childLaunchProcesses);
						System.out.println(myList.get(i) + " was launched!");
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}
	
	private void addProcessesTerminateListener() {
		DebugPlugin.getDefault().addDebugEventListener(new IDebugEventSetListener() {

			@Override
			public void handleDebugEvents(DebugEvent[] events) {
				for (DebugEvent event : events) {
					Object source = event.getSource();
					if (source instanceof IProcess && event.getKind() == DebugEvent.TERMINATE) {
						// TODO check if the process terminating is one you are
						// interested in
						System.out.println("Terminated " + source);
						for (Set<IProcess> processSet : processesToWait) {
							processSet.remove(source);
						}
					}
				}
			}
		});	
	}

	// Adds the processes I launched in a List (Set)
	private void waitForProcessesToTerminate(IProcess[] processes) {
		Set<IProcess> processSet = Collections.synchronizedSet(new HashSet<>(Arrays.asList(processes)));
		System.out.println("Waiting for processes: " + processSet);
		processesToWait.add(processSet);

		while (!processSet.isEmpty()) {
			System.out.println("Still waiting for processes: " + processSet);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Finished waiting for processes: " + Arrays.toString(processes));
	}
}
