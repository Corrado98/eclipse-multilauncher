package ch.parisi.e4.advancedlaunch.strategies.console;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link ConsolePatternMatchListener}.
 */
public class ConsolePatternMatchListenerTest {

	/**
	 * Tests that {@link ConsolePatternMatchListener#getPattern()} returns the specified regex.
	 */
	@Test
	public void getPatternTest() {
		String regex = ".*hello.*";
		ConsolePatternMatchListener consolePatternMatchListener = new ConsolePatternMatchListener(regex);
		assertEquals(regex, consolePatternMatchListener.getPattern());
	}

	/**
	 * Tests that the {@code consoleStringDetected} flag is set to {@code false} 
	 * at instantiation of {@link ConsolePatternMatchListener}.
	 */
	@Test
	public void instantiateAndCheckFlagTest() {
		ConsolePatternMatchListener consolePatternMatchListener = new ConsolePatternMatchListener(null);
		assertFalse(consolePatternMatchListener.isConsoleStringDetected());
	}

	/**
	 * Tests that {@link ConsolePatternMatchListener#matchFound(org.eclipse.ui.console.PatternMatchEvent)}
	 * sets the {@code consoleStringDetected} flag correctly.
	 */
	@Test
	public void matchFoundTest() {
		ConsolePatternMatchListener consolePatternMatchListener = new ConsolePatternMatchListener(null);
		assertFalse(consolePatternMatchListener.isConsoleStringDetected());
		consolePatternMatchListener.matchFound(null);
		assertTrue(consolePatternMatchListener.isConsoleStringDetected());
	}

	/**
	 * Tests that {@link ConsolePatternMatchListener#connect(org.eclipse.ui.console.TextConsole)}
	 * sets the {@code consoleStringDetected} flag to {@code false}.
	 */
	@Test
	public void connectConsoleTest() {
		ConsolePatternMatchListener consolePatternMatchListener = new ConsolePatternMatchListener(null);
		assertFalse(consolePatternMatchListener.isConsoleStringDetected());
		consolePatternMatchListener.connect(null);
		assertFalse(consolePatternMatchListener.isConsoleStringDetected());
	}

}
