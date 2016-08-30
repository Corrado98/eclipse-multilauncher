package com.profidatagroup.e4.advancedlaunch.strategies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;

import com.profidatagroup.e4.advancedlaunch.tabs.SampleTab;

public abstract class AbstractLaunchStrategy {

	protected abstract void launchSelectedStrategy(ILaunchConfiguration iLaunchConfiguration, String mode, String param);

}
