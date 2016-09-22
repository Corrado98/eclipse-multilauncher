package ch.parisi.e4.advancedlaunch.enums;

import org.eclipse.debug.core.ILaunchManager;

public enum LaunchMode {
	/**
	 * 	{@link ILaunchManager#RUN_MODE}
	 */
	RUN,
	
	/**
	 * {@link ILaunchManager#DEBUG_MODE}
	 */
	DEBUG,
	
	/**
	 * {@link ILaunchManager#PROFILE_MODE}
	 */
	PROFILE,
	
	/**
	 * A Launch that can either be in {@link ILaunchManager#RUN_MODE} 
	 * or {@link ILaunchManager#DEBUG_MODE}, depending on the user being in the 
	 * 'Run Configurations' or 'Debug Configurations'
	 */
	INHERIT
}
