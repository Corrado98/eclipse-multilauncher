/*******************************************************************************
 * Copyright (c) 2004, 2015 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - initial API and implementation
 *     Sergey Prigogin (Google)
 *     Marc Khouzam (Ericsson) - New strings for CMainTab2
 *******************************************************************************/
package ch.parisi.e4.advancedlaunch.messages;

import org.eclipse.osgi.util.NLS;

/**
 * This class was taken from CDT and was modified by the author of this project.
 */
public class LaunchMessages extends NLS {
	public static String LaunchGroupConfigurationDelegate_Loop;
	public static String LaunchGroupConfigurationDelegate_Error;
	public static String LaunchGroupConfigurationDelegate_Action_None;
	public static String LaunchGroupConfigurationDelegate_Action_WaitForTermination;
	public static String LaunchGroupConfigurationDelegate_Action_Delay;
	public static String LaunchGroupConfigurationDelegate_Action_WaitForConsoleString;
	public static String LaunchGroupConfigurationSelectionDialog_4;
	public static String LaunchGroupConfigurationSelectionDialog_8;
	public static String LaunchGroupConfigurationSelectionDialog_9;
	public static String LaunchGroupConfigurationSelectionDialog_9_2;
	public static String LaunchGroupConfigurationSelectionDialog_10;
	public static String LaunchGroupConfigurationSelectionDialog_10_2;
	public static String LaunchGroupConfigurationSelectionDialog_11;
	public static String LaunchGroupConfigurationSelectionDialog_12;
	public static String LaunchGroupConfigurationSelectionDialog_13;
	public static String LaunchGroupConfigurationSelectionDialog_14;
	public static String LaunchGroupConfigurationSelectionDialog_15;
	public static String LaunchGroupConfiguration_Up;
	public static String LaunchGroupConfiguration_Down;
	public static String LaunchGroupConfiguration_Edit;
	public static String LaunchGroupConfiguration_Add;
	public static String LaunchGroupConfiguration_Remove;
	public static String LaunchGroupConfiguration_Name;
	public static String LaunchGroupConfiguration_Mode;
	public static String LaunchGroupConfiguration_Launches;
	public static String LaunchGroupConfiguration_NotFound;
	public static String LaunchGroupConfiguration_RecursiveNotFound;
	public static String LaunchGroupConfiguration_NotALaunchConfiguration;
	public static String LaunchGroupConfiguration_LaunchDelegationError;
	public static String LaunchGroupConfiguration_CannotLaunch;
	private LaunchMessages() {
	}

	static {
        // Load message values from bundle file
        NLS.initializeMessages(LaunchMessages.class.getCanonicalName(), LaunchMessages.class);
    }
}
