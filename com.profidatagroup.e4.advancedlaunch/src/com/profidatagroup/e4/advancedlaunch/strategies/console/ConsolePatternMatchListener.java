package com.profidatagroup.e4.advancedlaunch.strategies.console;

import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

public class ConsolePatternMatchListener implements IPatternMatchListener {
	private String checkRegex;
	private volatile boolean consoleStringDetected = false;
	
	public ConsolePatternMatchListener(String checkRegex) {
		this.checkRegex = checkRegex;
	}

	@Override
	public void connect(TextConsole console) {
	}

	@Override
	public void disconnect() {
	}

	@Override
	public void matchFound(PatternMatchEvent event) {
		consoleStringDetected = true;
		System.out.println("NEXT CONFIGURATION STARTED!");
	}

	@Override
	public String getPattern() {
		return checkRegex; /*".*successfully.*"*/
	}

	@Override
	public int getCompilerFlags() {
		return 0;
	}

	@Override
	public String getLineQualifier() {
		return null;
	}
	
	public boolean getConsoleStringDetected() {
		return consoleStringDetected;
	}

}
