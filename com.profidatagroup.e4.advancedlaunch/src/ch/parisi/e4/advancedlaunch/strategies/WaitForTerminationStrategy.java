package ch.parisi.e4.advancedlaunch.strategies;

import java.io.PrintStream;
import java.text.MessageFormat;

import org.eclipse.debug.core.ILaunch;

/**
 * Waits for launch to be terminated. 
 */
public class WaitForTerminationStrategy implements WaitStrategy {

	private volatile boolean terminated = false;
	private volatile boolean success = true;
	private PrintStream printStream;

	/**
	 * Constructs a {@link WaitForTerminationStrategy}.
	 * 
	 * @param printStream the print stream
	 */
	public WaitForTerminationStrategy(PrintStream printStream) {
		this.printStream = printStream;
	}

	@Override
	public boolean waitForLaunch(ILaunch launch) {
		if (!terminated) {
			printStream.println(MessageFormat.format("{0}: Waiting for termination...", launch.getLaunchConfiguration().getName()));
		}

		while (!terminated) {
			sleep();
		}

		return success;
	}

	private void sleep() {
		try {
			Thread.sleep(1000);
		}
		catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
			printStream.println(interruptedException.getMessage());
		}
	}

	@Override
	public void launchTerminated(String name, int exitCode) {
		if (exitCode != 0) {
			success = false;
		}

		terminated = true;
		printStream.println(MessageFormat.format("{0}: Terminated with exit code {1}.", name, exitCode));
	}

}
