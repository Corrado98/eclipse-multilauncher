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

import com.profidatagroup.e4.advancedlaunch.ConsoleListener;

/*
 * No correct working solution atm.
 * 
 */
public class ReadConsoleTextStrategy extends AbstractLaunchStrategy {
	
	private String regEx;

	@Override
	public void launchSelectedStrategy(ILaunchConfiguration iLaunchConfiguration, String mode, String param) {

		
		regEx = param;
		
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();

		// Existing consoles
		IConsole[] consoles = manager.getConsoles();

		// Listen for consoles being added/removed
		manager.addConsoleListener(new IConsoleListener() {

			@Override
			public void consolesRemoved(IConsole[] consoles) {
				// TODO Auto-generated method stub
				for (int i = 0; i < consoles.length; i++) {
					if(consoles[i] instanceof TextConsole) {				
						System.out.println(consoles[i].getName() + " REMOVED");
					}
					
				}
			}

			@Override
			public void consolesAdded(IConsole[] consoles) {
				for (int i = 0; i < consoles.length; i++) {
					if(consoles[i] instanceof TextConsole) {				
						System.out.println(consoles[i].getName() + " ADDED");
						TextConsole textConsole = (TextConsole) consoles[i];
						textConsole.addPatternMatchListener(new ConsoleListener(regEx));
					}
					
				}

			}
		});
		
		try {
			iLaunchConfiguration.launch(mode, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		
	}
	
}
