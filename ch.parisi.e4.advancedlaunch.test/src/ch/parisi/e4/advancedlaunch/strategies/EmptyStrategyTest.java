package ch.parisi.e4.advancedlaunch.strategies;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.junit.Test;
import org.mockito.Mockito;

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
		PrintStream inMemoryStream = new PrintStream(new ByteArrayOutputStream(), true);
		ILaunch launch = Mockito.mock(ILaunch.class);
		Mockito.when(launch.getLaunchConfiguration()).thenReturn(Mockito.mock(ILaunchConfiguration.class));
		Mockito.when(launch.getLaunchConfiguration().getName()).thenReturn("HelloJava");

		EmptyStrategy emptyStrategy = new EmptyStrategy(inMemoryStream);

		AtomicLong actualDelayInMilliseconds = new AtomicLong(0);

		Thread emptyStrategyThread = new Thread(new Runnable() {
			@Override
			public void run() {
				long startTime = System.currentTimeMillis();
				emptyStrategy.waitForLaunch(launch);
				long endTime = System.currentTimeMillis();

				actualDelayInMilliseconds.set(endTime - startTime);
			}
		});

		emptyStrategyThread.start();
		emptyStrategyThread.join();

		assertEquals(0, actualDelayInMilliseconds.get(), 1.0);

	}

}
