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
package ch.parisi.e4.advancedlaunch;

import ch.parisi.e4.advancedlaunch.dialog.MultiLaunchConfigurationSelectionDialog;
import ch.parisi.e4.advancedlaunch.messages.LaunchMessages;

/**
 * This is a helper class for the
 * {@link MultiLaunchConfigurationSelectionDialog}. It contains the enum with
 * the postLaunchActions: {@link PostLaunchAction}.
 * 
 * This class was taken from CDT and was modified by the author of this project.
 */
public class EnumController {

	/**
	 * The strategies each launch has to wait for, to start the next one.
	 */
	public static enum PostLaunchAction {
		NONE, WAIT_FOR_TERMINATION, DELAY, WAIT_FOR_CONSOLESTRING
	};

	/**
	 * Allows us decouple the enum identifier in the code from its textual
	 * representation in the GUI
	 */
	public static String actionEnumToStr(PostLaunchAction action) {
		switch (action) {
		case NONE:
			return LaunchMessages.LaunchGroupConfigurationDelegate_Action_None;
		case WAIT_FOR_TERMINATION:
			return LaunchMessages.LaunchGroupConfigurationDelegate_Action_WaitUntilTerminated;
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
	public static PostLaunchAction strToActionEnum(String str) {
		if (str.equals(LaunchMessages.LaunchGroupConfigurationDelegate_Action_None)) {
			return PostLaunchAction.NONE;
		} else if (str.equals(LaunchMessages.LaunchGroupConfigurationDelegate_Action_WaitUntilTerminated)) {
			return PostLaunchAction.WAIT_FOR_TERMINATION;
		} else if (str.equals(LaunchMessages.LaunchGroupConfigurationDelegate_Action_Delay)) {
			return PostLaunchAction.DELAY;
		} else if (str.equals(LaunchMessages.LaunchGroupConfigurationDelegate_Action_WaitForConsoleString)) {
			return PostLaunchAction.WAIT_FOR_CONSOLESTRING;
		} else {
			assert false : "new post launch action type is missing logic"; //$NON-NLS-1$
			return PostLaunchAction.NONE;
		}
	}
}
