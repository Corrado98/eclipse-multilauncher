package ch.parisi.e4.advancedlaunch.testcases;

/**
 * The {@link Printer} class implements an application that
 * prints messages to the standard output.
 */
public class Printer {

	/**
	 * Prints to standard out.
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		//match Hello
		System.out.println("Hello nice to meet you.");
		sleep();

		//match Hello...
		System.out.println("Hello... is someone there?");
		sleep();

		//match h.*o
		System.out.println("while shouting hellllllooooo one shows excitement");
		sleep();

		//match ^$
		System.out.println("");
		sleep();

		System.out.println("this is not at the begin");
		sleep();

		//match ^at the begin
		System.out.println("at the begin I was thinking on how to prove partial matching");
		sleep();

		System.out.println("at the end I will prove partial matching");
		sleep();

		//match at the end$ 
		System.out.println("we are almost at the end");
		sleep();

		//match ^start.*end$
		System.out.println("start ...this is for a full line match... end");
		sleep();

		System.out.println("goodbye");
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
