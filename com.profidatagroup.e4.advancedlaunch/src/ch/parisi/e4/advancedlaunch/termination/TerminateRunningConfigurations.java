package ch.parisi.e4.advancedlaunch.termination;

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

import ch.parisi.e4.advancedlaunch.PseudoProcess;


/**
 * This class terminates all running {@link IProcess}es from the back. 
 */
public class TerminateRunningConfigurations extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();

		ILaunch[] launches = manager.getLaunches();
		Collections.reverse(Arrays.asList(launches));
		
		for (ILaunch launch : launches) {
			IProcess[] processes = launch.getProcesses();
			Collections.reverse(Arrays.asList(processes));
		
				for (IProcess process : processes) {
					if (process.canTerminate() || process instanceof PseudoProcess) {
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
