package ch.parisi.e4.advancedlaunch.strategies;

import java.io.PrintStream;
import java.text.MessageFormat;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.TextConsole;

import ch.parisi.e4.advancedlaunch.messages.LaunchMessages;
import ch.parisi.e4.advancedlaunch.strategies.console.ConsolePatternMatchListener;
import ch.parisi.e4.advancedlaunch.strategies.console.ConsoleRemoveListener;

/** 
 * Waits for line and partial regular expression matching in the launch's console-output.
 */
public class WaitForConsoleRegexStrategy implements WaitStrategy {

	private final String regex;
	private volatile boolean terminated = false;
	private volatile boolean success = true;
	private PrintStream printStream;

	/**
	 * Constructs a {@link WaitForConsoleRegexStrategy}.
	 * 
	 * @param regex the regular expression to wait for
	 * @param printStream the print stream
	 */
	public WaitForConsoleRegexStrategy(String regex, PrintStream printStream) {
		this.regex = regex;
		this.printStream = printStream;
	}

	@Override
	public boolean waitForLaunch(ILaunch launch) {
		TextConsole console = findTextConsole(launch);
		if (console != null) {
			waitForConsolePatternMatch(console, launch);
		}
		return success;
	}

	private void waitForConsolePatternMatch(TextConsole console, ILaunch launch) {
		if (terminated) {
			return;
		}
		IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		ConsolePatternMatchListener consolePatterMatchListener = null;
		ConsoleRemoveListener consoleRemoveListener = null;

		try {
			consolePatterMatchListener = new ConsolePatternMatchListener(regex);
			console.addPatternMatchListener(consolePatterMatchListener);

			consoleRemoveListener = new ConsoleRemoveListener(console);
			consoleManager.addConsoleListener(consoleRemoveListener);

			if (!consolePatterMatchListener.isConsoleStringDetected()) {
				printStream.println(MessageFormat.format(LaunchMessages.LaunchGroupConsole_RegexWaiting, launch.getLaunchConfiguration().getName(), regex));
			}

			while (!consoleRemoveListener.isRemoved() && !consolePatterMatchListener.isConsoleStringDetected() && isLaunchRunning(launch)) {
				sleep();
			}

			if (consolePatterMatchListener.isConsoleStringDetected()) {
				printStream.println(MessageFormat.format(LaunchMessages.LaunchGroupConsole_RegexStoppedWaiting, launch.getLaunchConfiguration().getName()));
			}
		}
		finally {
			if (consolePatterMatchListener != null) {
				console.removePatternMatchListener(consolePatterMatchListener);
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
			Thread.sleep(1000);
		}
		catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
			printStream.println(LaunchMessages.LaunchGroupConsole_InterruptedException);
		}
	}

	@Override
	public void launchTerminated(String name, int exitCode) {
		if (exitCode != 0) {
			success = false;
		}

		terminated = true;
		printStream.println(MessageFormat.format(LaunchMessages.LaunchGroupConsole_LaunchNameWithExitCode, name, exitCode));
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
