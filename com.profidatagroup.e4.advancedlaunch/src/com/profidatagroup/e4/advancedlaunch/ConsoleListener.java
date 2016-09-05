package com.profidatagroup.e4.advancedlaunch;

import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

public class ConsoleListener implements IPatternMatchListener {
	private String checkRegex;
	
	public ConsoleListener(String checkRegex) {
		this.checkRegex = checkRegex;
	}

	@Override
	public void connect(TextConsole console) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void matchFound(PatternMatchEvent event) {
	System.out.println("NEXT CONFIGURATION STARTED!");

	}

	@Override
	public String getPattern() {
		return checkRegex; /*".*successfully.*"*/
	}

	@Override
	public int getCompilerFlags() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getLineQualifier() {
		// TODO Auto-generated method stub
		return null;
	}

}
