package com.profidatagroup.e4.advancedlaunch.strategies;

public abstract class AbstractLaunchStrategy {

	public void launch() {

		launchSelectedStrategy();

	}

	protected abstract void launchSelectedStrategy();

}
