package com.profidatagroup.e4.advancedlaunch.strategies;

import org.eclipse.debug.core.ILaunch;

public class DelayStrategy extends AbstractLaunchStrategy {

	@Override
	protected void waitForLaunch(ILaunch launch, String param) {
		waitDelay(Integer.parseInt(param));
	}

	private void waitDelay(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
