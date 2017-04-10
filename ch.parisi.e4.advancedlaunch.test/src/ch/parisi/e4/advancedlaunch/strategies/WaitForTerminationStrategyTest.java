package ch.parisi.e4.advancedlaunch.strategies;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests {@link WaitForTerminationStrategy}.
 */
public class WaitForTerminationStrategyTest {

	/**
	 * Tests a basic {@link WaitForTerminationStrategy}.
	 */
	@Test
	public void basicWaitForTerminationStrategyTest() {
		PrintStream inMemoryStream = new PrintStream(new ByteArrayOutputStream(), true);
		ILaunch launch = Mockito.mock(ILaunch.class);
		Mockito.when(launch.getLaunchConfiguration()).thenReturn(Mockito.mock(ILaunchConfiguration.class));
		Mockito.when(launch.getLaunchConfiguration().getName()).thenReturn("HelloJava");

		WaitForTerminationStrategy waitForTerminationStrategy = new WaitForTerminationStrategy(inMemoryStream);
		AtomicLong actualDelayInMilliseconds = new AtomicLong(0);

		Thread waitForTerminationStrategyThread = new Thread(new Runnable() {
			@Override
			public void run() {
				sleep(2000);
				waitForTerminationStrategy.launchTerminated(null, 0);
			}
		});

		waitForTerminationStrategyThread.start();

		long startTime = System.currentTimeMillis();
		waitForTerminationStrategy.waitForLaunch(launch);
		long endTime = System.currentTimeMillis();

		actualDelayInMilliseconds.set(endTime - startTime);

		assertTrue(actualDelayInMilliseconds.get() < 3100);
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
