package ch.parisi.e4.advancedlaunch.strategies.console;

import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

/**
 * The {@link ConsolePatternMatchListener}.
 */
public class ConsolePatternMatchListener implements IPatternMatchListener {
	private final String regex;
	private volatile boolean consoleStringDetected = false;

	/**
	 * Constructs a {@link ConsolePatternMatchListener}.
	 * 
	 * @param regex the regex to listen for
	 */
	public ConsolePatternMatchListener(String regex) {
		this.regex = regex;
	}

	@Override
	public void matchFound(PatternMatchEvent event) {
		consoleStringDetected = true;
	}

	@Override
	public String getPattern() {
		// e.g:  .*successfully.*
		return regex;
	}

	/**
	 * Returns whether the console string was detected.
	 * 
	 * @return {@code true} if console string detected, otherwise {@code false}.
	 */
	public boolean isConsoleStringDetected() {
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
