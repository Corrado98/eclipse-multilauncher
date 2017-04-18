package ch.parisi.e4.advancedlaunch.strategies;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.function.Function;

import org.eclipse.debug.core.ILaunch;

import ch.parisi.e4.advancedlaunch.messages.LaunchMessages;

/**
 * Waits for a dialog to be confirmed before launching the next launch. 
 * 
 * If the dialog is cancelled launching will abort. 
 */
public class WaitForDialogStrategy implements WaitStrategy {

	private Function<String, Boolean> showDialogFunction;
	private String dialogText;
	private PrintStream printStream;

	/**
	 * Constructs a {@link WaitForDialogStrategy}.
	 * 
	 * @param showDialogFunction the show dialog function
	 * @param dialogText the dialog text or {@code null} if default text should be used
	 * @param printStream the print stream
	 */
	public WaitForDialogStrategy(Function<String, Boolean> showDialogFunction, String dialogText, PrintStream printStream) {
		this.showDialogFunction = showDialogFunction;
		this.dialogText = dialogText;
		this.printStream = printStream;
		setDialogTextToDefaultIfNullOrEmpty();
	}

	private void setDialogTextToDefaultIfNullOrEmpty() {
		if (dialogText == null || dialogText.trim().isEmpty()) {
			dialogText = LaunchMessages.LaunchGroupConfigurationSelectionDialog_ConfirmLaunch_Dialog_DefaultMessage;
		}
	}

	@Override
	public boolean waitForLaunch(ILaunch launch) {
		boolean confirmed = showDialogFunction.apply(dialogText);

		if (confirmed) {
			printStream.println(MessageFormat.format(LaunchMessages.LaunchGroupConsole_DialogConfirmed, launch.getLaunchConfiguration().getName()));
		}
		else {
			printStream.println(MessageFormat.format(LaunchMessages.LaunchGroupConsole_DialogCancelled, launch.getLaunchConfiguration().getName()));
		}

		return confirmed;
	}

	/**
	 * Gets the dialog text.
	 * 
	 * @return the dialog text
	 */
	public String getDialogText() {
		return dialogText;
	}

	@Override
	public void launchTerminated(String name, int exitCode) {
		printStream.println(MessageFormat.format(LaunchMessages.LaunchGroupConsole_LaunchNameWithExitCode, name, exitCode));
	}

}
