package ch.parisi.e4.advancedlaunch.strategies;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

/**
 * Tests {@link WaitForTerminationStrategy}.
 */
public class WaitForTerminationStrategyTest {

	/**
	 * Tests a basic {@link WaitForTerminationStrategy}.
	 */
	@Test
	public void basicWaitForTerminationStrategyTest() {
		WaitForTerminationStrategy waitForTerminationStrategy = new WaitForTerminationStrategy();
		AtomicLong actualDelayInMilliseconds = new AtomicLong(0);

		Thread waitForTerminationStrategyThread = new Thread(new Runnable() {
			@Override
			public void run() {
				sleep(2000);
				waitForTerminationStrategy.launchTerminated(0);
			}
		});

		waitForTerminationStrategyThread.start();
		long startTime = System.currentTimeMillis();
		waitForTerminationStrategy.waitForLaunch(null);
		long endTime = System.currentTimeMillis();

		actualDelayInMilliseconds.set(endTime - startTime);

		assertTrue(actualDelayInMilliseconds.get() < 3000);
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		}
		catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

}
