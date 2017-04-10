package ch.parisi.e4.advancedlaunch.utils;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

/**
 * Utility class providing convenience methods for a {@link IConsole}. 
 */
public class MessageConsoleUtils {

	/**
	 * Finds a {@link MessageConsole} by name. 
	 * 
	 * @param name the name of the {@code MessageConsole}
	 * @return the {@code MessageConsole} or {@code null} if not found
	 */
	public static MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager consoleManager = plugin.getConsoleManager();
		IConsole[] existingConsoles = consoleManager.getConsoles();
		for (int console = 0; console < existingConsoles.length; console++) {
			if (name.equals(existingConsoles[console].getName())) {
				return (MessageConsole) existingConsoles[console];
			}
		}

		return null;
	}

	/**
	 * Finds or creates a {@link MessageConsole} by name. 
	 * 
	 * @param name the name of the {@code MessageConsole}
	 * @return the found {@code MessageConsole} or a newly created one.
	 */
	public static MessageConsole findOrCreateConsole(String name) {
		MessageConsole messageConsole = findConsole(name);
		if (messageConsole == null) {
			messageConsole = new MessageConsole(name, null);
			ConsolePlugin plugin = ConsolePlugin.getDefault();
			IConsoleManager consoleManager = plugin.getConsoleManager();
			consoleManager.addConsoles(new IConsole[] {
					messageConsole });
		}

		return messageConsole;
	}
}
