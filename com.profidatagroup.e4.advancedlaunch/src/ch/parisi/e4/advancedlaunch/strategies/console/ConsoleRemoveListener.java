package ch.parisi.e4.advancedlaunch.strategies.console;

import java.util.Arrays;

import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleListener;

/**
 * The {@link ConsoleRemoveListener}.
 */
public class ConsoleRemoveListener implements IConsoleListener {

	private final IConsole console;

	private volatile boolean removed;

	/**
	 * Constructs a {@link ConsoleRemoveListener}.
	 * 
	 * @param console the console to stop listening to
	 */
	public ConsoleRemoveListener(IConsole console) {
		this.console = console;
	}

	@Override
	public void consolesAdded(IConsole[] consoles) {
		if (Arrays.asList(consoles).contains(console)) {
			removed = false;
		}
	}

	@Override
	public void consolesRemoved(IConsole[] consoles) {
		if (Arrays.asList(consoles).contains(console)) {
			removed = true;
		}
	}

	/**
	 * Returns whether a console is removed from the {@link ConsoleRemoveListener}.
	 * 
	 * @return {@code true} if a console is removed from the {@link ConsoleRemoveListener}, otherwise {@code false}.
	 */
	public boolean isRemoved() {
		return removed;
	}

}
