package ch.parisi.e4.advancedlaunch.strategies;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

/**
 * Tests {@link EmptyStrategy}.
 */
public class EmptyStrategyTest {

	/**
	 * Tests a basic {@link EmptyStrategy}.
	 * @throws InterruptedException if any thread has interrupted the current thread.
	 */
	@Test
	public void emptyStrategyTest() throws InterruptedException {
		EmptyStrategy emptyStrategy = new EmptyStrategy();

		AtomicLong actualDelayInMilliseconds = new AtomicLong(0);

		Thread emptyStrategyThread = new Thread(new Runnable() {
			@Override
			public void run() {
				long startTime = System.currentTimeMillis();
				emptyStrategy.waitForLaunch(null);
				long endTime = System.currentTimeMillis();

				actualDelayInMilliseconds.set(endTime - startTime);
			}
		});

		emptyStrategyThread.start();
		emptyStrategyThread.join();

		assertEquals(0, actualDelayInMilliseconds.get());

	}

}
