package ch.parisi.e4.advancedlaunch.strategies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

/**
 * Tests {@link DelayStrategy}.
 */
public class DelayStrategyTest {

	/**
	 * Tests a basic {@link DelayStrategy}.
	 * @throws InterruptedException if any thread has interrupted the current thread.
	 */
	@Test
	public void basicDelayStrategyTest() throws InterruptedException {
		assertEquals(1000, measureDelayStrategy(1000), 100);
	}

	/**
	 * Tests that a {@link DelayStrategy} stops waiting if the launch terminates normally. 
	 * @throws InterruptedException if any thread has interrupted the current thread.
	 */
	@Test
	public void stopWaitingWhenLaunchTerminatesWithoutErrorTest() throws InterruptedException {
		assertTrue(measureDelayStrategy(10000, 0) < 1100);
	}

	/**
	 * Tests that a {@link DelayStrategy} stops waiting if the launch terminates with 
	 * an exit code other than zero. 
	 * @throws InterruptedException if any thread has interrupted the current thread.
	 */
	@Test
	public void stopWaitingWhenLaunchTerminatesWithErrorTest() throws InterruptedException {
		assertTrue(measureDelayStrategy(10000, 99) < 1100);
	}

	private long measureDelayStrategy(long delayInMilliseconds) throws InterruptedException {
		return measureDelayStrategy(delayInMilliseconds, null);
	}

	private long measureDelayStrategy(long delayInMilliseconds, Integer exitCode) throws InterruptedException {
		WaitStrategy delayStrategy = new DelayStrategy((int) delayInMilliseconds / 1000);
		AtomicLong actualDelayInMilliseconds = new AtomicLong(0);

		Thread delayStrategyThread = new Thread(new Runnable() {
			@Override
			public void run() {
				long startTime = System.currentTimeMillis();
				delayStrategy.waitForLaunch(null);
				long endTime = System.currentTimeMillis();

				actualDelayInMilliseconds.set(endTime - startTime);
			}
		});
		delayStrategyThread.start();
		if (exitCode != null) {
			delayStrategy.launchTerminated(exitCode);
		}

		delayStrategyThread.join();

		return actualDelayInMilliseconds.get();
	}

}
