/*******************************************************************************
 *  Copyright (c) 2009, 2016 QNX Software Systems and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *      QNX Software Systems - initial API and implementation
 *      Freescale Semiconductor
 *******************************************************************************/
package ch.parisi.e4.advancedlaunch.utils;

import ch.parisi.e4.advancedlaunch.dialog.MultiLaunchConfigurationSelectionDialog;
import ch.parisi.e4.advancedlaunch.messages.LaunchMessages;

/**
 * Utility class for the {@link MultiLaunchConfigurationSelectionDialog}.
 * It contains methods for: {@link PostLaunchAction}.
 * 
 * This class was taken from CDT and was modified by the author of this project.
 */
public class PostLaunchActionUtils {

	/**
	 * Converts the specified {@link PostLaunchAction} to the human readable name.
	 * 
	 * @param postLaunchAction the {@link PostLaunchAction} to convert
	 * @return the human readable name
	 */
	public static String convertToName(PostLaunchAction postLaunchAction) {
		switch (postLaunchAction) {
			case NONE:
				return LaunchMessages.LaunchGroupConfigurationDelegate_Action_None;
			case WAIT_FOR_TERMINATION:
				return LaunchMessages.LaunchGroupConfigurationDelegate_Action_WaitForTermination;
			case DELAY:
				return LaunchMessages.LaunchGroupConfigurationDelegate_Action_Delay;
			case WAIT_FOR_CONSOLESTRING:
				return LaunchMessages.LaunchGroupConfigurationDelegate_Action_WaitForConsoleString;
			case WAIT_FOR_DIALOG:
				return LaunchMessages.LaunchGroupConfigurationDelegate_Action_WaitForDialog;
		}

		throw new IllegalArgumentException("Unknown post launch action: "
				+ PostLaunchActionUtils.convertToName(postLaunchAction));

	}

	/**
	 * Allows us decouple the enum identifier in the code from its textual
	 * representation in the GUI
	 * 
	 * @param name the post launch action's name
	 * @return the {@code PostLaunchAction} enum. 
	 */
	public static PostLaunchAction convertToPostLaunchAction(String name) {
		if (name.equals(LaunchMessages.LaunchGroupConfigurationDelegate_Action_None)) {
			return PostLaunchAction.NONE;
		}
		else if (name.equals(LaunchMessages.LaunchGroupConfigurationDelegate_Action_WaitForTermination)) {
			return PostLaunchAction.WAIT_FOR_TERMINATION;
		}
		else if (name.equals(LaunchMessages.LaunchGroupConfigurationDelegate_Action_Delay)) {
			return PostLaunchAction.DELAY;
		}
		else if (name.equals(LaunchMessages.LaunchGroupConfigurationDelegate_Action_WaitForConsoleString)) {
			return PostLaunchAction.WAIT_FOR_CONSOLESTRING;
		}
		else if (name.equals(LaunchMessages.LaunchGroupConfigurationDelegate_Action_WaitForDialog)) {
			return PostLaunchAction.WAIT_FOR_DIALOG;
		}
		else {
			throw new IllegalArgumentException("No post launch action for specified name: " + name);
		}
	}

	/**
	 * Returns the String representations of the {@link PostLaunchAction} enum in an array.  	
	 * 
	 * @return a {@code String} array containing the textual value of each enum state.    
	 */
	public static String[] getPostLaunchActionNames() {
		PostLaunchAction[] states = PostLaunchAction.values();
		String[] names = new String[states.length];

		for (int i = 0; i < states.length; i++) {
			names[i] = convertToName(states[i]);
		}

		return names;
	}
}
