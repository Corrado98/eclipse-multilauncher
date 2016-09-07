package com.profidatagroup.e4.advancedlaunch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.launch.internal.ui.MultiLaunchConfigurationSelectionDialog;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.DebugUITools;

import com.profidatagroup.e4.advancedlaunch.strategies.AbstractLaunchStrategy;
import com.profidatagroup.e4.advancedlaunch.strategies.DelayStrategy;
import com.profidatagroup.e4.advancedlaunch.strategies.EmptyStrategy;
import com.profidatagroup.e4.advancedlaunch.strategies.ReadConsoleTextStrategy;
import com.profidatagroup.e4.advancedlaunch.strategies.WaitForTerminationStrategy;
import com.profidatagroup.e4.advancedlaunch.tabs.SampleTab;

public class LaunchGroupConfigurationDelegate implements ILaunchConfigurationDelegate {

	private ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
	// private ILaunchConfigurationType type = manager
	// .getLaunchConfigurationType("org.eclipse.jdt.launching.localJavaApplication");
	private ILaunchConfigurationType[] allTypes = manager.getLaunchConfigurationTypes();

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		if (SampleTab.launchConfigurationDataList == null || SampleTab.launchConfigurationDataList.isEmpty()) {
			System.out.println("LIST IS NULL OR EMPTY!");
			return;
		}

		for (ILaunchConfigurationType type : allTypes) {
			ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);

			for (LaunchConfigurationBean bean : SampleTab.launchConfigurationDataList) {
				for (ILaunchConfiguration iLaunchConfiguration : configurations) {

					if (bean.getName().equals(iLaunchConfiguration.getName())) {
						AbstractLaunchStrategy launchAndWaitStrategy = createLaunchAndWaitStrategy(bean);
						launchAndWaitStrategy.launchAndWait(iLaunchConfiguration, bean.getMode());
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
