package ch.parisi.e4.advancedlaunch.strategies.console;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

import ch.parisi.e4.advancedlaunch.messages.LaunchMessages;
import ch.parisi.e4.advancedlaunch.utils.MessageConsoleUtils;

/**
 * This class removes the multilauncher console from the {@link IConsoleManager} if it exists. 
 */
public class RemoveMultilauncherConsole extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String name = LaunchMessages.LaunchGroupConsole_Name;
		MessageConsole messageConsole = MessageConsoleUtils.findConsole(name);

		if (messageConsole != null) {
			ConsolePlugin plugin = ConsolePlugin.getDefault();
			IConsoleManager conMan = plugin.getConsoleManager();
			conMan.removeConsoles(new IConsole[] {
					messageConsole });
		}

		return null;
	}

}
