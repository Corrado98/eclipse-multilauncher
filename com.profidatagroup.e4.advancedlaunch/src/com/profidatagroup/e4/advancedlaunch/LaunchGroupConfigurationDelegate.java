package com.profidatagroup.e4.advancedlaunch;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

import com.profidatagroup.e4.advancedlaunch.strategies.AbstractLaunchStrategy;
import com.profidatagroup.e4.advancedlaunch.strategies.DelayStrategy;
import com.profidatagroup.e4.advancedlaunch.strategies.EmptyStrategy;
import com.profidatagroup.e4.advancedlaunch.strategies.ReadConsoleTextStrategy;
import com.profidatagroup.e4.advancedlaunch.strategies.WaitForTerminationStrategy;
import com.profidatagroup.e4.advancedlaunch.tabs.SampleTab;

public class LaunchGroupConfigurationDelegate implements ILaunchConfigurationDelegate {

	private ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
	private ILaunchConfigurationType[] allTypes = manager.getLaunchConfigurationTypes();

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		List<LaunchConfigurationBean> launchConfigurationDataList = SampleTab.loadLaunchConfigurations(configuration);

		for (ILaunchConfigurationType type : allTypes) {
			ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);

			for (LaunchConfigurationBean bean : launchConfigurationDataList) {
				for (ILaunchConfiguration launchConfiguration : configurations) {
					if (bean.getName().equals(launchConfiguration.getName())) {
						AbstractLaunchStrategy launchAndWaitStrategy = createLaunchAndWaitStrategy(bean);
						launchAndWaitStrategy.launchAndWait(launchConfiguration, bean.getMode());
					}
				}
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
}
