package com.profidatagroup.e4.advancedlaunch;

import java.util.Arrays;
import java.util.Collections;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class TerminateConfigurations extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();

		ILaunch[] launches = manager.getLaunches();
		Collections.reverse(Arrays.asList(launches));
		
		for (ILaunch launch : launches) {
			IProcess[] processes = launch.getProcesses();
			Collections.reverse(Arrays.asList(processes));
		
				for (IProcess process : processes) {
					if (process.canTerminate()) {
						try {
							process.terminate();
						} catch (DebugException e) {
							e.printStackTrace();
						}
					}
				}
		}
		return null;
	}
}
