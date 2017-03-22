package ch.parisi.e4.advancedlaunch;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Display;

import ch.parisi.e4.advancedlaunch.messages.LaunchMessages;
import ch.parisi.e4.advancedlaunch.strategies.AbstractLaunchStrategy;
import ch.parisi.e4.advancedlaunch.strategies.DelayStrategy;
import ch.parisi.e4.advancedlaunch.strategies.EmptyStrategy;
import ch.parisi.e4.advancedlaunch.strategies.ReadConsoleTextStrategy;
import ch.parisi.e4.advancedlaunch.strategies.WaitForTerminationStrategy;
import ch.parisi.e4.advancedlaunch.utils.LaunchUtils;
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
		 * A PseudoProcess to the ILaunch in order
		 * to set a name (label) to the multilaunch. After all wait strategies have finished, the multilaunch is removed 
		 * from the LaunchManager.
		 */

		if (!confirmMultilaunch(configuration)) {
			removeLaunchesFromLaunchManager();
			return;
		}

		PseudoProcess process = new PseudoProcess(launch);
		process.setLabel(configuration.getName());
		launch.addProcess(process);

		try {
			List<LaunchConfigurationModel> launchConfigurationDataList = LaunchUtils
					.loadLaunchConfigurations(configuration);

			for (LaunchConfigurationModel model : launchConfigurationDataList) {
				ILaunchConfiguration launchConfiguration = LaunchUtils.findLaunchConfiguration(model.getName());
				if (launchConfiguration != null) {
					if (process.isTerminated()) {
						break;
					}
					AbstractLaunchStrategy launchAndWaitStrategy = createLaunchAndWaitStrategy(model);
					launchAndWaitStrategy.launchAndWait(launchConfiguration, model.getMode());
				}
			}
		}
		catch (CoreException e) {
			removeLaunchesFromLaunchManager();
			throw e;
		}

		DebugPlugin.getDefault().getLaunchManager().removeLaunch(launch);
	}

	/**
	 * This method opens and handles the user-interaction of the multilaunch-start-confirmation-dialog.
	 * 
	 * @param configuration the multilaunch configuration
	 * @return whether the dialog was confirmed by the user.
	 * @throws CoreException if an exception occurs while retrieving the attribute from underlying storage.
	 */
	private boolean confirmMultilaunch(ILaunchConfiguration configuration) throws CoreException {
		boolean promptBeforeLaunch = configuration.getAttribute("promptBeforeLaunch", false);
		if(!promptBeforeLaunch) {
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
								LaunchMessages.LaunchGroupConfiguration_PromptBeforeLaunch_Dialog_Question,
								LaunchMessages.LaunchGroupConfiguration_PromptBeforeLaunch_Dialog_Toggle,
								false,
								null,
								null);
				isConfirmed.set(confirmDialog(startMultilaunchDialog, configuration));
			}
		});
		return isConfirmed.get();
	}

	private boolean confirmDialog(MessageDialogWithToggle startMultilaunchDialog, ILaunchConfiguration configuration) {
		if (startMultilaunchDialog.getReturnCode() == IDialogConstants.OK_ID) {
			try {
				ILaunchConfigurationWorkingCopy configurationCopy = configuration.getWorkingCopy();
				configurationCopy.setAttribute("promptBeforeLaunch", !startMultilaunchDialog.getToggleState());
				configurationCopy.doSave();
				return true;
			}
			catch (CoreException e) {
				e.printStackTrace();
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
	private AbstractLaunchStrategy createLaunchAndWaitStrategy(LaunchConfigurationModel launchConfigurationModel) {
		switch (launchConfigurationModel.getPostLaunchAction()) {
			case WAIT_FOR_TERMINATION:
				return new WaitForTerminationStrategy();

			case DELAY:
				return new DelayStrategy(Integer.parseInt(launchConfigurationModel.getParam()));

			case WAIT_FOR_CONSOLESTRING:
				return new ReadConsoleTextStrategy(launchConfigurationModel.getParam());

			case NONE:
				return new EmptyStrategy();
		}

		throw new IllegalArgumentException("Unknown launch and wait strategy: "
				+ PostLaunchActionUtils.convertToName(launchConfigurationModel.getPostLaunchAction()));
	}
}
