package com.profidatagroup.e4.advancedlaunch.strategies;

import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.TextConsole;

import com.profidatagroup.e4.advancedlaunch.strategies.console.ConsolePatternMatchListener;
import com.profidatagroup.e4.advancedlaunch.strategies.console.ConsoleRemoveListener;

/**
 * Launches a {@link ILaunchConfiguration} and waits for a regular expression in the console.
 */
public class ReadConsoleTextStrategy extends AbstractLaunchStrategy {
	
	private String regEx;
	private boolean terminationDetection = false;
	
	public ReadConsoleTextStrategy(String userConsoleStringToWaitFor) {
		this.regEx = userConsoleStringToWaitFor;
	}

	@Override
	protected void waitForLaunch(ILaunch launch) {
		TextConsole console = findTextConsole(launch);
		if (console != null) {
			waitForConsolePatternMatch(console, regEx, launch);
		}
	}

	private void waitForConsolePatternMatch(TextConsole console, String regEx, ILaunch launch) {
		IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		ConsolePatternMatchListener consoleListener = null;
		ConsoleRemoveListener consoleRemoveListener = null;
		
		try {
			consoleListener = new ConsolePatternMatchListener(regEx);
			console.addPatternMatchListener(consoleListener);
			
			consoleRemoveListener = new ConsoleRemoveListener(console);
			consoleManager.addConsoleListener(consoleRemoveListener);
			
			//if a condition turns true, breaks out of method.
			while (!consoleRemoveListener.isRemoved() && !consoleListener.getConsoleStringDetected() && !terminationDetection) {
				sleep();
				detectProcessTermination(launch);
			}
		} finally {
			System.out.println("Finished waiting");
			
			if (consoleListener != null) {
				console.removePatternMatchListener(consoleListener);
			}
			if (consoleRemoveListener != null) {
				consoleManager.removeConsoleListener(consoleRemoveListener);
			}
		}
	}

	private void detectProcessTermination(ILaunch launch) {
		IProcess[] processes = launch.getProcesses();
		for (IProcess process : processes) {
			if(process.isTerminated()) {
				terminationDetection = true;
			}
		}
	}

	private void sleep() {
		try {
			System.out.println("Still waiting..");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
