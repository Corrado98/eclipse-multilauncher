package ch.parisi.e4.advancedlaunch.testcases;

/**
 * The {@link Server} class implements a simple simulation 
 * of a starting and running server with a given name by printing
 * to standard out.
 */
public class Server {

	/**
	 * Simulates the start and run of a server with a specified name.
	 * 
	 * @param args the server name 
	 */
	public static void main(String[] args) {
		simulateServer(args);
	}

	private static void simulateServer(String[] args) {
		for (int i = 0; i < 3; i++) {
			System.out.println(String.format("Starting Server %s ...", args[0]));
			sleep();
		}

		System.out.println(String.format("Server %s started.", args[0]));
		sleep();

		while (true) {
			System.out.println(String.format("Server %s running ...", args[0]));
			sleep();
		}
	}

	private static void sleep() {
		try {
			Thread.sleep(2000);
		}
		catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}
	}

}
