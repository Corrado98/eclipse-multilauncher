package ch.parisi.e4.advancedlaunch.strategies;

import java.util.function.Function;

import org.eclipse.debug.core.DebugException;
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

	/**
	 * Constructs a {@link WaitForDialogStrategy}.
	 * 
	 * @param showDialogFunction the show dialog function
	 * @param dialogText the dialog text or {@code null} if default text should be used
	 */
	public WaitForDialogStrategy(Function<String, Boolean> showDialogFunction, String dialogText) {
		this.showDialogFunction = showDialogFunction;
		this.dialogText = dialogText;
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

		if (!confirmed && launch.canTerminate()) {
			try {
				launch.terminate();
			}
			catch (DebugException debugException) {
				return false;
			}
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
	public void launchTerminated(int exitCode) {
		// ignore termination
	}

}
