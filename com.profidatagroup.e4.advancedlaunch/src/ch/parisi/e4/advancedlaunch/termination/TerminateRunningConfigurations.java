package ch.parisi.e4.advancedlaunch.termination;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.debug.core.model.IProcess;

import ch.parisi.e4.advancedlaunch.utils.LaunchUtils;

/**
 * This class terminates all running {@link IProcess}es from the back. 
 */
public class TerminateRunningConfigurations extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		LaunchUtils.terminateRunningConfigurations();
		return null;
	}

}
