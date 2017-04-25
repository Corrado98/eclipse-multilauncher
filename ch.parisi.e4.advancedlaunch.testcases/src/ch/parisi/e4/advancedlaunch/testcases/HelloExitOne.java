package ch.parisi.e4.advancedlaunch.testcases;

/**
 * The {@link HelloExitOne} implements an application that
 * simply prints "Exiting with exit code 1." to standard output
 * and exits the application with exit code 1, which by convention, indicates 
 * unusual termination. 
 */
public class HelloExitOne {

	/**
	 * Prints to standard out, exits with exit code 1.
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		System.out.println("Exiting with exit code 1.");
		System.exit(1);
	}

}
