package ch.parisi.e4.advancedlaunch.strategies.console;

import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

public class ConsolePatternMatchListener implements IPatternMatchListener {
	private final String checkRegex;
	private volatile boolean consoleStringDetected = false;
	
	public ConsolePatternMatchListener(String checkRegex) {
		this.checkRegex = checkRegex;
	}
	
	@Override
	public void matchFound(PatternMatchEvent event) {
		consoleStringDetected = true;
	}

	@Override
	public String getPattern() {
		// e.g:  .*successfully.*
		return checkRegex; 
	}
	
	public boolean getConsoleStringDetected() {
		return consoleStringDetected;
	}
	
	@Override
	public void connect(TextConsole console) {
	}
	@Override
	public void disconnect() {
	}
	@Override
	public int getCompilerFlags() {
		return 0;
	}
	@Override
	public String getLineQualifier() {
		return null;
	}
}
