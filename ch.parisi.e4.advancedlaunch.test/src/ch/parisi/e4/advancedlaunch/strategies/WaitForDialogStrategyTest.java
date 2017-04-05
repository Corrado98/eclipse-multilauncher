package ch.parisi.e4.advancedlaunch.strategies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.function.Function;

import org.eclipse.debug.core.ILaunch;
import org.junit.Test;
import org.mockito.Mockito;

import ch.parisi.e4.advancedlaunch.messages.LaunchMessages;

/**
 * Tests {@link WaitForDialogStrategy}.
 */
public class WaitForDialogStrategyTest {

	/**
	 * Tests that {@link WaitForDialogStrategy#waitForLaunch(org.eclipse.debug.core.ILaunch)} returns {@code true} when ok-pressed. 
	 */
	@Test
	public void waitForDialogStrategyOkPressedTest() {
		Function<String, Boolean> okPressedFunction = dialogText -> true;
		WaitForDialogStrategy waitForDialogStrategy = new WaitForDialogStrategy(okPressedFunction, null);
		assertTrue(waitForDialogStrategy.waitForLaunch(null));
	}

	/**
	 * Tests that {@link WaitForDialogStrategy#waitForLaunch(org.eclipse.debug.core.ILaunch)} returns {@code false} when cancel-pressed. 
	 */
	@Test
	public void waitForDialogStrategyCancelPressedTest() {
		Function<String, Boolean> cancelPressedFunction = dialogText -> false;

		WaitForDialogStrategy waitForDialogStrategy = new WaitForDialogStrategy(cancelPressedFunction, null);
		ILaunch launch = Mockito.mock(ILaunch.class);
		Mockito.when(launch.canTerminate()).thenReturn(true);

		assertFalse(waitForDialogStrategy.waitForLaunch(launch));
	}

	/**
	 * Tests that the set dialog message is returned. 
	 */
	@Test
	public void basicDialogTextTest() {
		String dialogText = "Continue launching HelloJava?";
		WaitForDialogStrategy waitForDialogStrategy = new WaitForDialogStrategy(null, dialogText);
		assertEquals(
				dialogText,
				waitForDialogStrategy.getDialogText());
	}

	/**
	 * Tests that the dialog message has a default, if null is passed in. 
	 */
	@Test
	public void defaultDialogTextWhenNullTest() {
		WaitForDialogStrategy waitForDialogStrategy = new WaitForDialogStrategy(null, null);
		assertEquals(
				LaunchMessages.LaunchGroupConfigurationSelectionDialog_ConfirmLaunch_Dialog_DefaultMessage,
				waitForDialogStrategy.getDialogText());

	}

	/**
	 * Tests that the dialog message has a default, if a whitespace string is passed in. 
	 */
	@Test
	public void defaultDialogTextWhenWhitespaceTest() {
		WaitForDialogStrategy waitForDialogStrategy = new WaitForDialogStrategy(null, "   ");
		assertEquals(
				LaunchMessages.LaunchGroupConfigurationSelectionDialog_ConfirmLaunch_Dialog_DefaultMessage,
				waitForDialogStrategy.getDialogText());

	}
}
