package ch.parisi.e4.advancedlaunch;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

import ch.parisi.e4.advancedlaunch.messages.LaunchMessages;
import ch.parisi.e4.advancedlaunch.strategies.AbstractLaunchStrategy;
import ch.parisi.e4.advancedlaunch.strategies.DelayStrategy;
import ch.parisi.e4.advancedlaunch.strategies.EmptyStrategy;
import ch.parisi.e4.advancedlaunch.strategies.ReadConsoleTextStrategy;
import ch.parisi.e4.advancedlaunch.strategies.WaitForTerminationStrategy;

/**
 * Class which handles the launch of each configuration, while respecting it`s
 * corresponding launch-strategy with possible params and the selected launch
 * mode: see {@link org.eclipse.debug.core.ILaunchManager}
 * 
 * @author PaCo
 *
 */
public class LaunchGroupConfigurationDelegate implements ILaunchConfigurationDelegate {

	/**
	 * This method iterates through all configurations in a user-created one
	 * (LaunchGroup) and starts them.
	 */
	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		List<LaunchConfigurationBean> launchConfigurationDataList = LaunchUtils.loadLaunchConfigurations(configuration);

		for (LaunchConfigurationBean bean : launchConfigurationDataList) {
			ILaunchConfiguration launchConfiguration = LaunchUtils.findLaunchConfiguration(bean.getName());
			if (launchConfiguration != null) {
				AbstractLaunchStrategy launchAndWaitStrategy = createLaunchAndWaitStrategy(bean);
				launchAndWaitStrategy.launchAndWait(launchConfiguration, bean.getMode());
			}
		}
	}

	/**
	 * This method determines which strategy each configuration has to follow.
	 * 
	 * @param launchConfigurationBean
	 *            the configuration which stores the postLaunchAction-attribute
	 *            to determine the strategy to follow.
	 * @return the strategy to follow.
	 */
	private AbstractLaunchStrategy createLaunchAndWaitStrategy(LaunchConfigurationBean launchConfigurationBean) {
		switch (launchConfigurationBean.getPostLaunchAction()) {
		case "Wait until terminated":
			return new WaitForTerminationStrategy();

		case "Delay":
			return new DelayStrategy(Integer.parseInt(launchConfigurationBean.getParam()));

		case "Wait for Console-String":
			return new ReadConsoleTextStrategy(launchConfigurationBean.getParam());

		case "None":
			return new EmptyStrategy();
		}

		throw new IllegalArgumentException(
				"Unknown launch and wait strategy: " + launchConfigurationBean.getPostLaunchAction());
	}
}
