package ch.parisi.e4.advancedlaunch.strategies.console;

import java.util.Arrays;

import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleListener;

public class ConsoleRemoveListener implements IConsoleListener {

	private final IConsole console;
	
	private volatile boolean removed;

	public ConsoleRemoveListener(IConsole console) {
		this.console = console;
	}
	
	@Override
	public void consolesAdded(IConsole[] consoles) {
	}

	@Override
	public void consolesRemoved(IConsole[] consoles) {
		if (Arrays.asList(consoles).contains(console)) {
			removed = true;
		}
	}
	
	public boolean isRemoved() {
		return removed;
	}

}
