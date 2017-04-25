package ch.parisi.e4.advancedlaunch.testcases;

/**
 * The {@link HelloWorldLoop} class implements an application that
 * simply prints "Hello World!" in an infinite loop to standard output.
 */
public class HelloWorldLoop {

	/**
	 * Prints to standard out.
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		while (true) {
			System.out.println("Hello World!");
			sleep();
		}
	}

	private static void sleep() {
		try {
			Thread.sleep(4000);
		}
		catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}
	}
}
