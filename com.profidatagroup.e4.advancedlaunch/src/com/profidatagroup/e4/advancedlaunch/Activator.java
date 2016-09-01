package com.profidatagroup.e4.advancedlaunch;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.profidatagroup.e4.advancedlaunch"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
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
//					if(consoles[i] instanceof TextConsole) {				
//						System.out.println(consoles[i].getName() + " REMOVED");
//					}
//					
//				}
//			}
//
//			@Override
//			public void consolesAdded(IConsole[] consoles) {
//				for (int i = 0; i < consoles.length; i++) {
//					if(consoles[i] instanceof TextConsole) {				
//						System.out.println(consoles[i].getName() + " ADDED");
//						TextConsole textConsole = (TextConsole) consoles[i];
//						
//						
//						textConsole.addPatternMatchListener(new ConsoleListener(a));
//					}
//					
//				}
//
//			}
//		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
	 * BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
