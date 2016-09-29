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

import java.util.Arrays;

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
	 * @param action the {@link PostLaunchAction} to convert
	 * @return the human readable name
	 */
	public static String convertToName(PostLaunchAction action) {
		switch (action) {
		case NONE:
			return LaunchMessages.LaunchGroupConfigurationDelegate_Action_None;
		case WAIT_FOR_TERMINATION:
			return LaunchMessages.LaunchGroupConfigurationDelegate_Action_WaitForTermination;
		case DELAY:
			return LaunchMessages.LaunchGroupConfigurationDelegate_Action_Delay;
		case WAIT_FOR_CONSOLESTRING:
			return LaunchMessages.LaunchGroupConfigurationDelegate_Action_WaitForConsoleString;
		default:
			assert false : "new post launch action type is missing logic"; //$NON-NLS-1$
			return LaunchMessages.LaunchGroupConfigurationDelegate_Action_None;
		}
	}

	/**
	 * Allows us decouple the enum identifier in the code from its textual
	 * representation in the GUI
	 */
	public static PostLaunchAction convertToPostLaunchAction(String name) {
		if (name.equals(LaunchMessages.LaunchGroupConfigurationDelegate_Action_None)) {
			return PostLaunchAction.NONE;
		} else if (name.equals(LaunchMessages.LaunchGroupConfigurationDelegate_Action_WaitForTermination)) {
			return PostLaunchAction.WAIT_FOR_TERMINATION;
		} else if (name.equals(LaunchMessages.LaunchGroupConfigurationDelegate_Action_Delay)) {
			return PostLaunchAction.DELAY;
		} else if (name.equals(LaunchMessages.LaunchGroupConfigurationDelegate_Action_WaitForConsoleString)) {
			return PostLaunchAction.WAIT_FOR_CONSOLESTRING;
		} else {
			assert false : "new post launch action type is missing logic"; //$NON-NLS-1$
			return PostLaunchAction.NONE;
		}
	}
	
	//TODO add javadoc
	public static String[] getPostLaunchActionNames(Class<? extends Enum<?>> e) {
	    return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}
	
	//TODO add javadoc	
	public static String[] getPostLaunchActionNames() {
	    PostLaunchAction[] states = PostLaunchAction.values();
	    String[] names = new String[states.length];

	    for (int i = 0; i < states.length; i++) {
	        names[i] = convertToName(states[i]);
	    }

	    return names;
	}	
}
