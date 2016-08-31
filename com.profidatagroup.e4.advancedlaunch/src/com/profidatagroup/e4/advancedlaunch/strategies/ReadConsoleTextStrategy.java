package com.profidatagroup.e4.advancedlaunch.strategies;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleListener;
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
	public void launchSelectedStrategy(ILaunchConfiguration iLaunchConfiguration, String mode, String param) {
		try {
			iLaunchConfiguration.launch(mode, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager(); 
//
//		// Existing consoles
//		IConsole[] consoles = manager.getConsoles();
//
//		// Listen for consoles being added/removed
//		manager.addConsoleListener(new IConsoleListener() {
//			
//			@Override
//			public void consolesRemoved(IConsole[] consoles) {
//				// TODO Auto-generated method stub
//				for (int i = 0; i < consoles.length; i++) {
//					System.out.println(consoles[i].getName() + " REMOVED");
//				}
//			}
//			
//			@Override
//			public void consolesAdded(IConsole[] consoles) {
//				for (int i = 0; i < consoles.length; i++) {
//					System.out.println(consoles[i].getName() + " ADDED");
//				}
//				
//			}
//		});
		
//		
//		ConsolePlugin plugin = ConsolePlugin.getDefault();
//		IConsoleManager conMan = plugin.getConsoleManager();
//		IConsole[] existing = conMan.getConsoles();
//		for (int i = 0; i < existing.length; i++) {
//			System.out.println(existing[i].getName());
//		}
//		// no console found, so create a new one
//		MessageConsole myConsole = new MessageConsole("Meine Konsole", null);
//		IConsole[] consoles = new IConsole[1];
//		conMan.addConsoles(consoles);
//		
//		MessageConsoleStream out = myConsole.newMessageStream();
//		out.println("Hello from Generic foo console sample action");
		

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
