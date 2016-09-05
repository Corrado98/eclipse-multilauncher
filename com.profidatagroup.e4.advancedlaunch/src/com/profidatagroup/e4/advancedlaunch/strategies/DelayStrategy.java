package com.profidatagroup.e4.advancedlaunch.strategies;

import org.eclipse.debug.core.ILaunch;

public class DelayStrategy extends AbstractLaunchStrategy {
	
	private int waitingTimeInSeconds;
	
	public DelayStrategy(String delayInSeconds) {
		this.waitingTimeInSeconds = Integer.parseInt(delayInSeconds);
	}

	@Override
	protected void waitForLaunch(ILaunch launch) {
		waitDelay(waitingTimeInSeconds);
	}

	private void waitDelay(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
