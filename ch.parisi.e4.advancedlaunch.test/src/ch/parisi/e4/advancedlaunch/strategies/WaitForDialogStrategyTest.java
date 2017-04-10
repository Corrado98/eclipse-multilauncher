package ch.parisi.e4.advancedlaunch.strategies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.Function;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import ch.parisi.e4.advancedlaunch.messages.LaunchMessages;

/**
 * Tests {@link WaitForDialogStrategy}.
 */
public class WaitForDialogStrategyTest {

	private PrintStream inMemoryStream;
	private ILaunch launch;

	/**
	 * Initializes the required mocks and sets up an in-memory print stream. 
	 */
	@Before
	public void initializeMocks() {
		inMemoryStream = new PrintStream(new ByteArrayOutputStream(), true);
		launch = Mockito.mock(ILaunch.class);
		Mockito.when(launch.getLaunchConfiguration()).thenReturn(Mockito.mock(ILaunchConfiguration.class));
		Mockito.when(launch.getLaunchConfiguration().getName()).thenReturn("HelloJava");
	}

	/**
	 * Tests that {@link WaitForDialogStrategy#waitForLaunch(ILaunch)} returns {@code true} when ok-pressed. 
	 */
	@Test
	public void waitForDialogStrategyOkPressedTest() {
		Function<String, Boolean> okPressedFunction = dialogText -> true;
		WaitForDialogStrategy waitForDialogStrategy = new WaitForDialogStrategy(okPressedFunction, null, inMemoryStream);
		assertTrue(waitForDialogStrategy.waitForLaunch(launch));
	}

	/**
	 * Tests that {@link WaitForDialogStrategy#waitForLaunch(ILaunch)} returns {@code false} when cancel-pressed. 
	 */
	@Test
	public void waitForDialogStrategyCancelPressedTest() {
		Function<String, Boolean> cancelPressedFunction = dialogText -> false;
		WaitForDialogStrategy waitForDialogStrategy = new WaitForDialogStrategy(cancelPressedFunction, null, inMemoryStream);
		assertFalse(waitForDialogStrategy.waitForLaunch(launch));
	}

	/**
	 * Tests that the set dialog message is returned. 
	 */
	@Test
	public void basicDialogTextTest() {
		String dialogText = "Continue launching HelloJava?";
		WaitForDialogStrategy waitForDialogStrategy = new WaitForDialogStrategy(null, dialogText, inMemoryStream);
		assertEquals(
				dialogText,
				waitForDialogStrategy.getDialogText());
	}

	/**
	 * Tests that the dialog message has a default, if null is passed in. 
	 */
	@Test
	public void defaultDialogTextWhenNullTest() {
		WaitForDialogStrategy waitForDialogStrategy = new WaitForDialogStrategy(null, null, inMemoryStream);
		assertEquals(
				LaunchMessages.LaunchGroupConfigurationSelectionDialog_ConfirmLaunch_Dialog_DefaultMessage,
				waitForDialogStrategy.getDialogText());

	}

	/**
	 * Tests that the dialog message has a default, if a whitespace string is passed in. 
	 */
	@Test
	public void defaultDialogTextWhenWhitespaceTest() {
		WaitForDialogStrategy waitForDialogStrategy = new WaitForDialogStrategy(null, "   ", inMemoryStream);
		assertEquals(
				LaunchMessages.LaunchGroupConfigurationSelectionDialog_ConfirmLaunch_Dialog_DefaultMessage,
				waitForDialogStrategy.getDialogText());

	}
}
