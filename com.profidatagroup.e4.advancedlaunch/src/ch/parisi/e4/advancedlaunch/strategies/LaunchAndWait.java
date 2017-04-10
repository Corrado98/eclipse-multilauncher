package ch.parisi.e4.advancedlaunch.strategies;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;

/**
 * This class handles the default behavior of all waiting strategies.
 */
public class LaunchAndWait {

	private WaitStrategy waitStrategy;

	/**
	 * Constructs a {@link LaunchAndWait}.
	 * 
	 * @param waitStrategy the wait strategy
	 */
	public LaunchAndWait(WaitStrategy waitStrategy) {
		this.waitStrategy = waitStrategy;
	}

	/**
	 * Launches the specified {@link ILaunchConfiguration} in the given <code>mode</code> and waits until the conditions of this strategy are met.
	 * 
	 * This method will only return when the conditions of this strategy are met.
	 * It provides functionality for its subclasses to react to launch terminations.
	 * 
	 * @param launchConfiguration the {@link ILaunchConfiguration}
	 * @param mode the launch mode, see {@link org.eclipse.debug.core.ILaunchManager}
	 * 
	 * @return whether the launch was successful (or aborted).  
	 * 
	 * @throws CoreException if the specified launchConfiguration's launch method fails. Reasons include:<ul>
	 * <li>unable to instantiate the underlying launch configuration delegate</li>
	 * <li>the launch fails (in the delegate)</li>
	 * </ul>
	 */
	public final boolean launchAndWait(ILaunchConfiguration launchConfiguration, String mode) throws CoreException {
		AtomicBoolean success = new AtomicBoolean(true);
		IDebugEventSetListener debugEventSetListener = null;
		try {
			ILaunch launch = launchConfiguration.launch(mode, null);
			debugEventSetListener = registerTerminationListener(exitCode -> {
				waitStrategy.launchTerminated(launchConfiguration.getName(), exitCode);
			});
			success.set(waitStrategy.waitForLaunch(launch));
		}
		finally {
			if (debugEventSetListener != null) {
				unregisterTerminationListener(debugEventSetListener);
			}
		}
		return success.get();
	}

	/**
	 * Registers a {@link IDebugEventSetListener}. 
	 * 
	 * If the event's source is an instance of {@link IProcess}
	 * and it's kind equal to {@code DebugEvent.TERMINATE} the specified
	 * exitCodeHandler is consumed with the {@code IProcess}'s exitCode. 
	 * 
	 * @param exitCodeHandler the exit code handler
	 * @return the {@link IDebugEventSetListener}.
	 */
	private final IDebugEventSetListener registerTerminationListener(Consumer<Integer> exitCodeHandler) {
		DebugPlugin debugPlugin = DebugPlugin.getDefault();
		IDebugEventSetListener debugEventSetListener = new IDebugEventSetListener() {
			@Override
			public void handleDebugEvents(DebugEvent[] events) {
				for (DebugEvent event : events) {
					Object source = event.getSource();
					if (source instanceof IProcess && event.getKind() == DebugEvent.TERMINATE) {
						// check if the process terminating is one i'm
						// interested in
						try {
							int exitCode = ((IProcess) source).getExitValue();
							exitCodeHandler.accept(exitCode);
						}
						catch (DebugException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};

		debugPlugin.addDebugEventListener(debugEventSetListener);
		return debugEventSetListener;
	}

	private void unregisterTerminationListener(IDebugEventSetListener debugEventSetListener) {
		DebugPlugin.getDefault().removeDebugEventListener(debugEventSetListener);
	}

}
