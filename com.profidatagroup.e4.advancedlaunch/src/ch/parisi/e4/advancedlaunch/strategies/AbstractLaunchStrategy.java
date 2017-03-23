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

public abstract class AbstractLaunchStrategy {

	/**
	 * Launches the specified {@link ILaunchConfiguration} in the given <code>mode</code> and waits until the conditions of this strategy are met.
	 * 
	 * This method will only return when the conditions of this strategy are met.
	 * 
	 * @param launchConfiguration the {@link ILaunchConfiguration}
	 * @param mode the launch mode, see {@link org.eclipse.debug.core.ILaunchManager}
	 * @throws CoreException 
	 */
	public final boolean launchAndWait(ILaunchConfiguration launchConfiguration, String mode) throws CoreException {
		AtomicBoolean success = new AtomicBoolean(true);
		IDebugEventSetListener debugEventSetListener = null;
		try {
			ILaunch launch = launchConfiguration.launch(mode, null);
			debugEventSetListener = registerTerminationListener(exitCode -> {
				success.set(exitCode == 0);
				if (!success.get()) {
					launchTerminated(exitCode);
				}
			});
			waitForLaunch(launch);
		}
		finally {
			if (debugEventSetListener != null) {
				unregisterTerminationListener(debugEventSetListener);
			}
		}
		return success.get(); // TODO true = success, false = abort
	}

	/**
	 * Waits until the conditions of this strategy are met.
	 * 
	 * This method will only return when the conditions of this strategy are met.
	 * 
	 * @param launch the {@link ILaunch} to wait for
	 */
	protected abstract void waitForLaunch(ILaunch launch);

	protected void launchTerminated(int exitCode) {
		System.out.println("Launch terminated " + exitCode);
	}

	/**
	 * Javadoc
	 * TODO Auto-generated javadoc registerTerminationListener
	 * 
	 * @return
	 */
	public final IDebugEventSetListener registerTerminationListener(Consumer<Integer> exitCodeHandler) {
		DebugPlugin debugPlugin = DebugPlugin.getDefault();
		IDebugEventSetListener debugEventSetListener = new IDebugEventSetListener() {
			@Override
			public void handleDebugEvents(DebugEvent[] events) {
				for (DebugEvent event : events) {
					Object source = event.getSource();
					if (source instanceof IProcess && event.getKind() == DebugEvent.TERMINATE) {
						// check if the process terminating is one i'm
						// interested in
						System.out.println("Terminated " + source);
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
