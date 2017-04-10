package ch.parisi.e4.advancedlaunch.strategies;

import java.io.PrintStream;
import java.text.MessageFormat;

import org.eclipse.debug.core.ILaunch;

/**
 * Waits for a specified amount of time before launching the next launch.
 */
public class DelayStrategy implements WaitStrategy {

	private int waitingTimeInSeconds;

	private volatile boolean terminated = false;
	private volatile boolean success = true;
	private PrintStream printStream;

	/**
	 * Constructs a {@link DelayStrategy}.
	 * 
	 * @param delayInSeconds the delay in seconds
	 * @param printStream the print stream
	 */
	public DelayStrategy(int delayInSeconds, PrintStream printStream) {
		this.waitingTimeInSeconds = delayInSeconds;
		this.printStream = printStream;
	}

	@Override
	public boolean waitForLaunch(ILaunch launch) {
		for (int second = 0; second < waitingTimeInSeconds; second++) {
			if (terminated) {
				printStream.println(MessageFormat.format("{0}: Stopped waiting since terminated.", launch.getLaunchConfiguration().getName()));
				return success;
			}
			waitDelay(1);
			printStream.println(MessageFormat.format("{0}: Waiting for {1} seconds.", launch.getLaunchConfiguration().getName(), ((waitingTimeInSeconds - 1) - second)));
		}
		return success;
	}

	private void waitDelay(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		}
		catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
			printStream.println(MessageFormat.format("InterruptedException: {0}", interruptedException.getMessage()));
		}
	}

	@Override
	public void launchTerminated(String name, int exitCode) {
		if (exitCode != 0) {
			success = false;
		}

		terminated = true;
		printStream.println(MessageFormat.format("{0}: terminated with exit code: {1}", name, exitCode));
	}

}
