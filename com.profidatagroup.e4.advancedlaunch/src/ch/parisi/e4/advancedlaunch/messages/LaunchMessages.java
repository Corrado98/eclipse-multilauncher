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
	public static String LaunchGroupConfigurationDelegate_Action_WaitForConsoleRegex;
	public static String LaunchGroupConfigurationDelegate_Action_WaitForConsoleText;
	public static String LaunchGroupConfigurationDelegate_Action_WaitForDialog;
	public static String LaunchGroupConfigurationSelectionDialog_LaunchMode;
	public static String LaunchGroupConfigurationSelectionDialog_PostLaunchAction;
	public static String LaunchGroupConfigurationSelectionDialog_SelectOnlyOneConfiguration;
	public static String LaunchGroupConfigurationSelectionDialog_AddConfiguration;
	public static String LaunchGroupConfigurationSelectionDialog_EditConfiguration;
	public static String LaunchGroupConfigurationSelectionDialog_AddConfigurationTitle;
	public static String LaunchGroupConfigurationSelectionDialog_EditConfigurationTitle;
	public static String LaunchGroupConfigurationSelectionDialog_SecondsLabel;
	public static String LaunchGroupConfigurationSelectionDialog_RegularExpressionLabel;
	public static String LaunchGroupConfigurationSelectionDialog_TextLabel;
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
	public static String LaunchGroupConfiguration_InvalidNumberOfSeconds;
	public static String LaunchGroupConfiguration_EmptyRegularExpression;
	public static String LaunchGroupConfiguration_EmptyText;
	public static String LaunchGroupConfiguration_LaunchDelegationError;
	public static String LaunchGroupConfiguration_CannotLaunch;
	public static String LaunchGroupConsole_Name;
	public static String LaunchGroupConsole_Abort;
	public static String LaunchGroupConsole_Scheduled;
	public static String LaunchGroupConsole_LaunchInformation;
	public static String LaunchGroupConsole_WaitingSuccessful;
	public static String LaunchGroupConsole_WaitingNotSuccessful;
	public static String LaunchGroupConsole_ScheduledSequenceAborted;
	public static String LaunchGroupConsole_TerminatedAllLaunchConfigurations;
	public static String LaunchGroupConsole_Complete;
	public static String LaunchGroupConsole_InterruptedException;
	public static String LaunchGroupConsole_LaunchNameWithExitCode;
	public static String LaunchGroupConsole_DelayWaiting;
	public static String LaunchGroupConsole_DelayStoppedWaiting;
	public static String LaunchGroupConsole_EmptyNotWaiting;
	public static String LaunchGroupConsole_RegexWaiting;
	public static String LaunchGroupConsole_RegexStoppedWaiting;
	public static String LaunchGroupConsole_DialogConfirmed;
	public static String LaunchGroupConsole_DialogCancelled;
	public static String LaunchGroupConsole_TerminationWaiting;

	private LaunchMessages() {
	}

	static {
		// Load message values from bundle file
		NLS.initializeMessages(LaunchMessages.class.getCanonicalName(), LaunchMessages.class);
	}
}
