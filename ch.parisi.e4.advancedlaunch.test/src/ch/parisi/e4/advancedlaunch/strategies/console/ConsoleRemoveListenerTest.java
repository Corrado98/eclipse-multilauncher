package ch.parisi.e4.advancedlaunch.strategies.console;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.ui.console.IConsole;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests {@link ConsoleRemoveListener}.
 */
public class ConsoleRemoveListenerTest {

	/**
	 * Tests initial state of {@link ConsoleRemoveListener#isRemoved()}.
	 */
	@Test
	public void initialStateTest() {
		IConsole console = Mockito.mock(IConsole.class);

		ConsoleRemoveListener consoleRemoveListener = new ConsoleRemoveListener(console);
		assertFalse(consoleRemoveListener.isRemoved());
	}

	/**
	 * Tests {@link ConsoleRemoveListener#isRemoved()} after removing a console.
	 */
	@Test
	public void removeConsoleTest() {
		IConsole console = Mockito.mock(IConsole.class);

		ConsoleRemoveListener consoleRemoveListener = new ConsoleRemoveListener(console);
		consoleRemoveListener.consolesRemoved(asArray(console));
		assertTrue(consoleRemoveListener.isRemoved());
	}

	/**
	 * Tests {@link ConsoleRemoveListener#isRemoved()} after removing another console.
	 */
	@Test
	public void removeAnotherConsoleTest() {
		IConsole console = Mockito.mock(IConsole.class);
		IConsole anotherConsole = Mockito.mock(IConsole.class);

		ConsoleRemoveListener consoleRemoveListener = new ConsoleRemoveListener(console);
		consoleRemoveListener.consolesRemoved(asArray(anotherConsole));
		assertFalse(consoleRemoveListener.isRemoved());
	}

	/**
	 * Tests {@link ConsoleRemoveListener#consolesAdded(IConsole[])} after removing and adding a console.
	 */
	@Test
	public void addRemoveConsoleTest() {
		IConsole console = Mockito.mock(IConsole.class);

		ConsoleRemoveListener consoleRemoveListener = new ConsoleRemoveListener(console);
		consoleRemoveListener.consolesRemoved(asArray(console));
		assertTrue(consoleRemoveListener.isRemoved());
		consoleRemoveListener.consolesAdded(asArray(console));
		assertFalse(consoleRemoveListener.isRemoved());
	}

	/**
	 * Tests {@link ConsoleRemoveListener#consolesAdded(IConsole[])} after remove 
	 * the watched console and adding another console.
	 */
	@Test
	public void addRemoveAnotherConsoleTest() {
		IConsole console = Mockito.mock(IConsole.class);
		IConsole anotherConsole = Mockito.mock(IConsole.class);

		ConsoleRemoveListener consoleRemoveListener = new ConsoleRemoveListener(console);
		consoleRemoveListener.consolesRemoved(asArray(console));
		assertTrue(consoleRemoveListener.isRemoved());
		consoleRemoveListener.consolesAdded(asArray(anotherConsole));
		assertTrue(consoleRemoveListener.isRemoved());
	}

	@SafeVarargs // no heap pollution since only used in this specific case
	private static <T> T[] asArray(T... elements) {
		return elements;
	}

}
