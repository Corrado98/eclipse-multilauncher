package ch.parisi.e4.advancedlaunch;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

import ch.parisi.e4.advancedlaunch.strategies.AbstractLaunchStrategy;
import ch.parisi.e4.advancedlaunch.strategies.DelayStrategy;
import ch.parisi.e4.advancedlaunch.strategies.EmptyStrategy;
import ch.parisi.e4.advancedlaunch.strategies.ReadConsoleTextStrategy;
import ch.parisi.e4.advancedlaunch.strategies.WaitForTerminationStrategy;
import ch.parisi.e4.advancedlaunch.utils.LaunchUtils;
import ch.parisi.e4.advancedlaunch.utils.PostLaunchActionUtils;

/**
 * Delegates the launch of each configuration, while respecting it`s
 * corresponding launch-strategy with possible params and the selected launch
 * mode: see {@link org.eclipse.debug.core.ILaunchManager}
 */
public class LaunchGroupConfigurationDelegate implements ILaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		/*
		 * This method iterates through all user-selected configurations and
		 * starts them.
		 * 
		 * At the beginning it adds a PseudoProcess to the ILaunch in order
		 * to set it to <terminated> in the end, when all other running processes
		 * are terminated.  
		 */		
		PseudoProcess process = new PseudoProcess(launch);
		process.setLabel(configuration.getName());
		launch.addProcess(process);

		List<LaunchConfigurationModel> launchConfigurationDataList = LaunchUtils
				.loadLaunchConfigurations(configuration);

		for (LaunchConfigurationModel model : launchConfigurationDataList) {
			//isIterating = true;
			ILaunchConfiguration launchConfiguration = LaunchUtils.findLaunchConfiguration(model.getName());
			if (launchConfiguration != null) {
				if (process.isTerminated()) {
					break;
				}
				AbstractLaunchStrategy launchAndWaitStrategy = createLaunchAndWaitStrategy(model);
				launchAndWaitStrategy.launchAndWait(launchConfiguration, model.getMode(), mode);
			}
			// launchConfiguration can never be null, since an invalid launch
			// cannot be runned.
		}

		waitForRunningProcesses();

		launch.removeProcess(process);
		process.setIsTerminated(true);
		launch.addProcess(process);
	}

	private void waitForRunningProcesses() {
		//alternative solution would have been the following: DebugPlugin.getDefault().getLaunchManager().removeLaunch(launch);
		while (!LaunchUtils.AreAllRunningProcessesTerminated()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates the waiting-strategy each configuration has to follow. The
	 * strategy that has to be created, is based on the
	 * {@code postLaunchAction}-field of the {@link LaunchConfigurationModel}.
	 * 
	 * @param launchConfigurationModel
	 *            the model which stores the postLaunchAction-attribute
	 * @return the strategy to follow
	 */
	private AbstractLaunchStrategy createLaunchAndWaitStrategy(LaunchConfigurationModel launchConfigurationModel) {
		switch (launchConfigurationModel.getPostLaunchAction()) {
		case WAIT_FOR_TERMINATION:
			return new WaitForTerminationStrategy();

		case DELAY:
			return new DelayStrategy(Integer.parseInt(launchConfigurationModel.getParam()));

		case WAIT_FOR_CONSOLESTRING:
			return new ReadConsoleTextStrategy(launchConfigurationModel.getParam());

		case NONE:
			return new EmptyStrategy();
		}

		throw new IllegalArgumentException("Unknown launch and wait strategy: "
				+ PostLaunchActionUtils.convertToName(launchConfigurationModel.getPostLaunchAction()));
	}
}
