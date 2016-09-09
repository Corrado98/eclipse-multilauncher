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

public class LaunchGroupConfigurationDelegate implements ILaunchConfigurationDelegate {
	
	private boolean infiniteLoopDetection = true;

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		List<LaunchConfigurationBean> launchConfigurationDataList = LaunchUtils.loadLaunchConfigurations(configuration);
		
		if(isInfiniteLoop(configuration, launch, launchConfigurationDataList)) {
			if(infiniteLoopDetection) {
			showInfiniteLoopErrorMessage(configuration);
			infiniteLoopDetection = false;
			}
			return;
		}

		for (LaunchConfigurationBean bean : launchConfigurationDataList) {
			ILaunchConfiguration launchConfiguration = LaunchUtils.findLaunchConfiguration(bean.getName());
			if (launchConfiguration != null) {
				AbstractLaunchStrategy launchAndWaitStrategy = createLaunchAndWaitStrategy(bean);
				launchAndWaitStrategy.launchAndWait(launchConfiguration, bean.getMode());
			}
		}
	}

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
	
	private boolean isInfiniteLoop(ILaunchConfiguration configuration, ILaunch launch,
			List<LaunchConfigurationBean> launchConfigurationDataList) throws DebugException {
		for (LaunchConfigurationBean bean : launchConfigurationDataList) {
			if (configuration.getName().equals(bean.getName())) {	
				return true;
			}
		}
		return false;
	}

	private void showInfiniteLoopErrorMessage(ILaunchConfiguration configuration) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						LaunchMessages.LaunchUIPlugin_Error,
						NLS.bind(LaunchMessages.MultiLaunchConfigurationDelegate_Loop,
								configuration.getName()));
			}
		});
	}
}
