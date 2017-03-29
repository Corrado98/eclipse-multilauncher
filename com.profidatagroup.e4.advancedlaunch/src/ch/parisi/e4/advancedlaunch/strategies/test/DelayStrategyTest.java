package ch.parisi.e4.advancedlaunch.strategies.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import ch.parisi.e4.advancedlaunch.strategies.DelayStrategy;
import ch.parisi.e4.advancedlaunch.strategies.WaitStrategy;

/**
 * The {@link DelayStrategyTest}.
 */
public class DelayStrategyTest {

	/**
	 * This method tests a basic delay strategy.
	 * @throws InterruptedException if any thread has interrupted the current thread.
	 */
	@Test
	public void basicDelayStrategyTest() throws InterruptedException {
		long delayInMilliseconds = 1000;
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
		delayStrategyThread.join();

		assertEquals(delayInMilliseconds, actualDelayInMilliseconds.get(), 100);
	}

	/**
	 * 
	 * 
	 * @throws InterruptedException if any thread has interrupted the current thread.
	 */
	@Test
	public void stopWaitingWhenLaunchTerminatedTest() throws InterruptedException {
		long delayInMilliseconds = 10000;
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

		delayStrategyThread.join();

		assertEquals(delayInMilliseconds, actualDelayInMilliseconds.get(), 5);

	}

}
