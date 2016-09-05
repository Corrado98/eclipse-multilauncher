package com.profidatagroup.e4.advancedlaunch.strategies;

import org.eclipse.debug.core.ILaunch;

public class EmptyStrategy extends AbstractLaunchStrategy {

	@Override
	protected void waitForLaunch(ILaunch launch) {
		// nothing to do
	}

}
