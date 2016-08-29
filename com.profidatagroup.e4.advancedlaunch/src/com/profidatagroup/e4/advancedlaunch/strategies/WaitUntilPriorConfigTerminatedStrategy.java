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

public class WaitUntilPriorConfigTerminatedStrategy extends AbstractLaunchStrategy {

	private List<Set<IProcess>> processesToWait = Collections.synchronizedList(new ArrayList<>());

	@Override
	public void launchSelectedStrategy() {
		IProcess[] childLaunchProcesses = getChildLaunch().getProcesses();
		waitForProcessesToTerminate(childLaunchProcesses);
	}
	
	@Override
	protected void useOptionalProcessTerminatedListener() {
		addProcessesTerminateListener();
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
