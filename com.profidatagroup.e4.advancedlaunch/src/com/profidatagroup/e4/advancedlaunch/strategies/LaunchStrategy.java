package com.profidatagroup.e4.advancedlaunch.strategies;

public abstract class LaunchStrategy {

	public void launch() {

		launchSelectedStrategy();

	}

	public abstract void launchSelectedStrategy();

}
