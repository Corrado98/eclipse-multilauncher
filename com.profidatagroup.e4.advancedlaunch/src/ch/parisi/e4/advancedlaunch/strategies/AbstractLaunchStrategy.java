package ch.parisi.e4.advancedlaunch.strategies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;

import ch.parisi.e4.advancedlaunch.LaunchUtils;

public abstract class AbstractLaunchStrategy {

	public static List<IProcess[]> launchProcesses = new ArrayList<>();

	/**
	 * Launches the specified {@link ILaunchConfiguration} in the given <code>mode</code> and waits until the conditions of this strategy are met.
	 * 
	 * This method will only return when the conditions of this strategy are met.
	 * 
	 * @param launchConfiguration the {@link ILaunchConfiguration}
	 * @param mode the launch mode, see {@link org.eclipse.debug.core.ILaunchManager}
	 * @param param the runtime parameter for this strategy (e.g. delay time)
	 */
	public final void launchAndWait(ILaunchConfiguration launchConfiguration, String mode, String parentMode) {
		try {
			if (mode.equals(LaunchUtils.INHERIT_MODE)) mode = parentMode;
			ILaunch launch = launchConfiguration.launch(mode, null);
			launchProcesses.add(launch.getProcesses());
			waitForLaunch(launch);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Waits until the conditions of this strategy are met.
	 * 
	 * This method will only return when the conditions of this strategy are met.
	 * 
	 * @param launch the {@link ILaunch} to wait for
	 * @param param the runtime parameter for this strategy (e.g. delay time)
	 */
	protected abstract void waitForLaunch(ILaunch launch);

}
