package ch.parisi.e4.advancedlaunch;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsole;

import ch.parisi.e4.advancedlaunch.messages.LaunchMessages;
import ch.parisi.e4.advancedlaunch.strategies.DelayStrategy;
import ch.parisi.e4.advancedlaunch.strategies.EmptyStrategy;
import ch.parisi.e4.advancedlaunch.strategies.LaunchAndWait;
import ch.parisi.e4.advancedlaunch.strategies.WaitForConsoleRegexStrategy;
import ch.parisi.e4.advancedlaunch.strategies.WaitForDialogStrategy;
import ch.parisi.e4.advancedlaunch.strategies.WaitForTerminationStrategy;
import ch.parisi.e4.advancedlaunch.strategies.WaitStrategy;
import ch.parisi.e4.advancedlaunch.utils.LaunchUtils;
import ch.parisi.e4.advancedlaunch.utils.MessageConsoleUtils;
import ch.parisi.e4.advancedlaunch.utils.MultilauncherConfigurationAttributes;
import ch.parisi.e4.advancedlaunch.utils.PostLaunchAction;
import ch.parisi.e4.advancedlaunch.utils.PostLaunchActionUtils;

/**
 * Delegates the launch of each configuration, while respecting it`s
 * corresponding launch-strategy with possible params and the selected launch
 * mode: see {@link org.eclipse.debug.core.ILaunchManager}
 */
public class LaunchGroupConfigurationDelegate implements ILaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		/*
		 * This method iterates through all user-selected launchconfigurations and
		 * starts them.
		 * 
		 * Depending on the multilaunchconfiguration for 'Prompt before launch',
		 * the user will be prompted whether he really wants 
		 * to start the multilaunch. 
		 * 
		 * A PseudoProcess is added to the ILaunch in order
		 * to set a name (label) to the multilaunch. After all wait strategies have finished, the multilaunch is removed 
		 * from the LaunchManager.
		 */
		String multilauncherConsoleName = LaunchMessages.LaunchGroupConsole_Name;
		MessageConsole multilauncherConsole = MessageConsoleUtils.findOrCreateConsole(multilauncherConsoleName);

		try (PrintStream printStream = new PrintStream(multilauncherConsole.newMessageStream())) {
			printStream.println("********************************");

			if (!confirmMultilaunch(configuration, printStream)) {
				printStream.println(MessageFormat.format("{0}: aborted.", launch.getLaunchConfiguration().getName()));

				removeLaunchesFromLaunchManager();
				return;
			}

			PseudoProcess process = new PseudoProcess(launch);
			process.setLabel(configuration.getName());
			launch.addProcess(process);

			try {

				List<LaunchConfigurationModel> activeLaunchConfigurationModels = LaunchUtils
						.getActiveLaunchConfigurationModels(LaunchUtils.loadLaunchConfigurations(configuration));

				for (LaunchConfigurationModel launchConfigurationModel : activeLaunchConfigurationModels) {
					printStream.println(MessageFormat.format("{0}: Scheduled {1}.", launch.getLaunchConfiguration().getName(), launchConfigurationModel.getName()));
				}

				for (LaunchConfigurationModel launchConfigurationModel : activeLaunchConfigurationModels) {
					ILaunchConfiguration launchConfiguration = LaunchUtils.findLaunchConfiguration(launchConfigurationModel.getName());
					if (launchConfiguration != null) {
						if (process.isTerminated()) {
							break;
						}

						LaunchAndWait launchAndWaitStrategy = new LaunchAndWait(createWaitStrategy(launchConfigurationModel, printStream));
						printStream.println(MessageFormat.format(
								"{0}: Launching in launch mode ''{1}'' with waiting strategy ''{2}'', abort launch on error ''{3}'' and parameter ''{4}''",
								launchConfigurationModel.getName(),
								launchConfigurationModel.getMode(),
								launchConfigurationModel.getPostLaunchAction(),
								launchConfigurationModel.isAbortLaunchOnError(),
								launchConfigurationModel.getParam()));
						boolean success = launchAndWaitStrategy.launchAndWait(launchConfiguration, launchConfigurationModel.getMode());
						printStream.println(MessageFormat.format("{0}: Waiting {1}.", launchConfigurationModel.getName(), success ? "successful" : "not successful"));

						if (!success) {
							if (launchConfigurationModel.isAbortLaunchOnError()) {
								printStream.println(MessageFormat.format("{0}: Scheduled sequence aborted.", launch.getLaunchConfiguration().getName()));
								break;
							}

							if (launchConfigurationModel.getPostLaunchAction() == PostLaunchAction.WAIT_FOR_DIALOG) {
								LaunchUtils.terminateRunningConfigurations();
								printStream.println(MessageFormat.format("{0}: Terminated all launch configurations.", launch.getLaunchConfiguration().getName()));
								break;
							}
						}
					}
				}
				DebugPlugin.getDefault().getLaunchManager().removeLaunch(launch);
				printStream.println(MessageFormat.format("{0}: Multilaunch completed.", launch.getLaunchConfiguration().getName()));

			}
			catch (CoreException coreException) {
				printStream.println(coreException.getMessage());
				removeLaunchesFromLaunchManager();
				throw coreException;
			}
		}
	}

	/**
	 * This method opens and handles the user-interaction of the multilaunch-start-confirmation-dialog.
	 * 
	 * @param configuration the multilaunch configuration
	 * @param printStream the print stream
	 * @return whether the dialog was confirmed by the user.
	 * @throws CoreException if an exception occurs while retrieving the attribute from underlying storage.
	 */
	private boolean confirmMultilaunch(ILaunchConfiguration configuration, PrintStream printStream) throws CoreException {
		boolean promptBeforeLaunch = configuration.getAttribute(MultilauncherConfigurationAttributes.PROMPT_BEFORE_LAUNCH_ATTRIBUTE, false);
		if (!promptBeforeLaunch) {
			return true;
		}
		AtomicBoolean isConfirmed = new AtomicBoolean();
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialogWithToggle startMultilaunchDialog = MessageDialogWithToggle
						.openOkCancelConfirm(
								Display.getDefault().getActiveShell(),
								LaunchMessages.LaunchGroupConfiguration_PromptBeforeLaunch_Dialog_Title,
								MessageFormat.format(LaunchMessages.LaunchGroupConfiguration_PromptBeforeLaunch_Dialog_Question, configuration.getName()),
								LaunchMessages.LaunchGroupConfiguration_PromptBeforeLaunch_Dialog_Toggle,
								false,
								null,
								null);
				isConfirmed.set(confirmDialog(startMultilaunchDialog, configuration, printStream));
			}
		});
		return isConfirmed.get();
	}

	private boolean confirmDialog(MessageDialogWithToggle startMultilaunchDialog, ILaunchConfiguration configuration, PrintStream printStream) {
		if (startMultilaunchDialog.getReturnCode() == IDialogConstants.OK_ID) {
			try {
				ILaunchConfigurationWorkingCopy configurationCopy = configuration.getWorkingCopy();
				configurationCopy.setAttribute(MultilauncherConfigurationAttributes.PROMPT_BEFORE_LAUNCH_ATTRIBUTE, !startMultilaunchDialog.getToggleState());
				configurationCopy.doSave();
				return true;
			}
			catch (CoreException e) {
				e.printStackTrace();
				printStream.println(e.getMessage());
			}
		}
		return false;
	}

	private void removeLaunchesFromLaunchManager() {
		ILaunch[] launches = DebugPlugin.getDefault().getLaunchManager().getLaunches();
		for (int i = 0; i < launches.length; i++) {
			DebugPlugin.getDefault().getLaunchManager().removeLaunch(launches[i]);
		}
	}

	/**
	 * Creates the waiting-strategy each configuration has to follow. The
	 * strategy that has to be created, is based on the
	 * {@code postLaunchAction}-field of the {@link LaunchConfigurationModel}.
	 * 
	 * @param launchConfigurationModel
	 *            the model which stores the postLaunchAction-attribute
	 * @return the strategy to follow
	 */
	private WaitStrategy createWaitStrategy(LaunchConfigurationModel launchConfigurationModel, PrintStream printStream) {
		switch (launchConfigurationModel.getPostLaunchAction()) {
			case WAIT_FOR_TERMINATION:
				return new WaitForTerminationStrategy(printStream);

			case DELAY:
				return new DelayStrategy(Integer.parseInt(launchConfigurationModel.getParam()), printStream);

			case WAIT_FOR_CONSOLE_REGEX:
				return new WaitForConsoleRegexStrategy(launchConfigurationModel.getParam(), printStream);

			case WAIT_FOR_CONSOLE_TEXT:
				return new WaitForConsoleRegexStrategy(Pattern.quote(launchConfigurationModel.getParam()), printStream);

			case NONE:
				return new EmptyStrategy(printStream);

			case WAIT_FOR_DIALOG:
				return new WaitForDialogStrategy(getShowDialogFunction(), launchConfigurationModel.getParam(), printStream);
		}

		throw new IllegalArgumentException("Unknown launch and wait strategy: "
				+ PostLaunchActionUtils.convertToName(launchConfigurationModel.getPostLaunchAction()));
	}

	private Function<String, Boolean> getShowDialogFunction() {
		return new Function<String, Boolean>() {
			@Override
			public Boolean apply(String dialogText) {
				AtomicBoolean confirmed = new AtomicBoolean(false);

				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						boolean okPressed = MessageDialog.openConfirm(
								null,
								LaunchMessages.LaunchGroupConfigurationSelectionDialog_ConfirmLaunch_Dialog_Title,
								dialogText);

						if (okPressed) {
							confirmed.set(true);
						}
					}
				});

				return confirmed.get();
			}
		};

	}

}
