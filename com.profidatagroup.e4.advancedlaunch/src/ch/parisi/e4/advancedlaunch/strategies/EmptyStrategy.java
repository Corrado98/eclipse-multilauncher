package ch.parisi.e4.advancedlaunch.strategies;

import java.io.PrintStream;
import java.text.MessageFormat;

import org.eclipse.debug.core.ILaunch;

import ch.parisi.e4.advancedlaunch.messages.LaunchMessages;

/**
 * Does not wait for a launch.
 */
public class EmptyStrategy implements WaitStrategy {

	private PrintStream printStream;

	/**
	 * Constructs a {@link EmptyStrategy}.
	 * 
	 * @param printStream the print stream
	 */
	public EmptyStrategy(PrintStream printStream) {
		this.printStream = printStream;
	}

	@Override
	public boolean waitForLaunch(ILaunch launch) {
		printStream.println(MessageFormat.format(LaunchMessages.LaunchGroupConsole_EmptyNotWaiting, launch.getLaunchConfiguration().getName()));
		return true;
	}

	@Override
	public void launchTerminated(String name, int exitCode) {
		printStream.println(MessageFormat.format(LaunchMessages.LaunchGroupConsole_LaunchNameWithExitCode, name, exitCode));
	}

}
