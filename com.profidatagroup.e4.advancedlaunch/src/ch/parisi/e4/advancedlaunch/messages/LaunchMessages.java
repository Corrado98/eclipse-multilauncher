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
 * 
 * This class holds constants which are used by the native language support (NLS)
 * to provide translations. The english translation for this constants is already provided
 * in this bundle's LaunchMessages.properties file. 
 * 
 * It is possible to provide translation files via fragment projects. 
 * Fragment projects extend this host plug-in. 
 * This approach allows that the text files can be maintained in separate plug-ins.
 * The included languages can be configured via a product configuration file.
 */
@SuppressWarnings("javadoc") // name should be self explanatory
public class LaunchMessages extends NLS {
	public static String LaunchGroupConfigurationDelegate_Loop;
	public static String LaunchGroupConfigurationDelegate_Error;
	public static String LaunchGroupConfigurationDelegate_Action_None;
	public static String LaunchGroupConfigurationDelegate_Action_WaitForTermination;
	public static String LaunchGroupConfigurationDelegate_Action_Delay;
	public static String LaunchGroupConfigurationDelegate_Action_WaitForConsoleString;
	public static String LaunchGroupConfigurationDelegate_Action_WaitForDialog;
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
	public static String LaunchGroupConfigurationSelectionDialog_SecondsLabel;
	public static String LaunchGroupConfigurationSelectionDialog_RegularExpressionLabel;
	public static String LaunchGroupConfigurationSelectionDialog_DialogTextLabel;
	public static String LaunchGroupConfigurationSelectionDialog_ConfirmLaunch_Dialog_Title;
	public static String LaunchGroupConfigurationSelectionDialog_ConfirmLaunch_Dialog_DefaultMessage;
	public static String LaunchGroupConfiguration_Up;
	public static String LaunchGroupConfiguration_Down;
	public static String LaunchGroupConfiguration_Edit;
	public static String LaunchGroupConfiguration_Add;
	public static String LaunchGroupConfiguration_Remove;
	public static String LaunchGroupConfiguration_PromptBeforeLaunch;
	public static String LaunchGroupConfiguration_PromptBeforeLaunch_Dialog_Title;
	public static String LaunchGroupConfiguration_PromptBeforeLaunch_Dialog_Question;
	public static String LaunchGroupConfiguration_PromptBeforeLaunch_Dialog_Toggle;
	public static String LaunchGroupConfiguration_Column_Name;
	public static String LaunchGroupConfiguration_Column_Mode;
	public static String LaunchGroupConfiguration_Column_Action;
	public static String LaunchGroupConfiguration_Column_Param;
	public static String LaunchGroupConfiguration_Column_Abort;
	public static String LaunchGroupConfiguration_AbortOnError;
	public static String LaunchGroupConfiguration_Name;
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
