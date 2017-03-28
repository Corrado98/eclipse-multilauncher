package ch.parisi.e4.advancedlaunch.strategies;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.TextConsole;

import ch.parisi.e4.advancedlaunch.strategies.console.ConsolePatternMatchListener;
import ch.parisi.e4.advancedlaunch.strategies.console.ConsoleRemoveListener;

/**
 * Reads the output of all consoles and waits for a regular expression.
 */
public class ReadConsoleTextStrategy extends AbstractLaunchStrategy {

	private final String regex;
	private volatile boolean aborted = false;

	/**
	 * Constructs a {@link ReadConsoleTextStrategy}.
	 * 
	 * @param consoleStringToWaitFor the console string to wait for
	 */
	public ReadConsoleTextStrategy(String consoleStringToWaitFor) {
		this.regex = consoleStringToWaitFor;
	}

	@Override
	protected void waitForLaunch(ILaunch launch) {
		TextConsole console = findTextConsole(launch);
		if (console != null) {
			waitForConsolePatternMatch(console, launch);
		}
	}

	private void waitForConsolePatternMatch(TextConsole console, ILaunch launch) {
		IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		ConsolePatternMatchListener consoleListener = null;
		ConsoleRemoveListener consoleRemoveListener = null;

		try {
			consoleListener = new ConsolePatternMatchListener(regex);
			console.addPatternMatchListener(consoleListener);

			consoleRemoveListener = new ConsoleRemoveListener(console);
			consoleManager.addConsoleListener(consoleRemoveListener);

			//FIXME could check aborted on method start
			while (!consoleRemoveListener.isRemoved() && !consoleListener.isConsoleStringDetected() && isLaunchRunning(launch) && !aborted) {
				sleep();
			}
		}
		finally {
			System.out.println("Finished waiting");

			if (consoleListener != null) {
				console.removePatternMatchListener(consoleListener);
			}
			if (consoleRemoveListener != null) {
				consoleManager.removeConsoleListener(consoleRemoveListener);
			}
		}
	}

	private boolean isLaunchRunning(ILaunch launch) {
		IProcess[] processes = launch.getProcesses();
		for (IProcess process : processes) {
			if (!process.isTerminated()) {
				return true;
			}
		}

		return false;
	}

	private void sleep() {
		try {
			System.out.println("Still waiting..");
			Thread.sleep(1000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void launchTerminated(int exitCode) {
		aborted = true;
	}

	private TextConsole findTextConsole(ILaunch launch) {
		IProcess[] processes = launch.getProcesses();
		for (IProcess process : processes) {
			TextConsole console = findTextConsole(process);
			if (console != null) {
				return console;
			}
		}

		return null;
	}

	private TextConsole findTextConsole(IProcess process) {
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
		IConsole[] consoles = manager.getConsoles();
		for (IConsole console : consoles) {
			if (console instanceof TextConsole) {
				TextConsole textConsole = (TextConsole) console;
				Object consoleProcess = textConsole.getAttribute(IDebugUIConstants.ATTR_CONSOLE_PROCESS);
				if (process == consoleProcess) {
					return textConsole;
				}
			}
		}

		return null;
	}
}
