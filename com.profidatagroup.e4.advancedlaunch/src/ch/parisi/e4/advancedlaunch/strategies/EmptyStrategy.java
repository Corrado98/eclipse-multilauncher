package ch.parisi.e4.advancedlaunch.strategies;

import java.io.PrintStream;
import java.text.MessageFormat;

import org.eclipse.debug.core.ILaunch;

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
		printStream.println(MessageFormat.format("{0}: Not waiting, continue.", launch.getLaunchConfiguration().getName()));
		return true;
	}

	@Override
	public void launchTerminated(String name, int exitCode) {
		printStream.println(MessageFormat.format("{0}: Terminated with exit code {1}.", name, exitCode));
	}

}
