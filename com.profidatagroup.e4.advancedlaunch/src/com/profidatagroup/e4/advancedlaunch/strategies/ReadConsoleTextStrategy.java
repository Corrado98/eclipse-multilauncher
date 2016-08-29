package com.profidatagroup.e4.advancedlaunch.strategies;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

/*
 * No correct working solution atm.
 * 
 */
public class ReadConsoleTextStrategy extends AbstractLaunchStrategy implements IPatternMatchListenerDelegate {

	@Override
	public void launchSelectedStrategy() {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++) {
			System.out.println(existing[i].getName());
		}
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole("Meine Konsole", null);
		IConsole[] consoles = new IConsole[1];
		conMan.addConsoles(consoles);
		
		MessageConsoleStream out = myConsole.newMessageStream();
		out.println("Hello from Generic foo console sample action");
		

	}

	@Override
	public void connect(TextConsole console) {
		
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void matchFound(PatternMatchEvent event) {
		System.out.println("MATCH FOUND");
	}

}
