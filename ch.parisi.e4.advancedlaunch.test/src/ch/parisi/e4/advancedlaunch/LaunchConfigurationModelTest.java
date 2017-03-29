package ch.parisi.e4.advancedlaunch;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.parisi.e4.advancedlaunch.LaunchConfigurationModel;
import ch.parisi.e4.advancedlaunch.utils.PostLaunchAction;

/**
 * The {@link LaunchConfigurationModelTest}.
 */
public class LaunchConfigurationModelTest {

	private String name = "Multilauncher";
	private String mode = "run";
	private String param = "";
	private boolean abortLaunchOnError = false;
	private PostLaunchAction postLaunchAction = PostLaunchAction.NONE;
	private LaunchConfigurationModel launchConfigurationModel = new LaunchConfigurationModel(name, mode, postLaunchAction, param, abortLaunchOnError);

	/**
	 * Asserts a {@code LaunchConfigurationModel}'s name.
	 */
	@Test
	public void getNameTest() {
		assertEquals(name, launchConfigurationModel.getName());
	}

	/**
	 * Asserts a {@code LaunchConfigurationModel}'s launch mode.
	 */
	@Test
	public void getModeTest() {
		assertEquals(mode, launchConfigurationModel.getMode());
	}

	/**
	 * Asserts a {@code LaunchConfigurationModel}'s runtime parameter.
	 */
	@Test
	public void getParamTest() {
		assertEquals(param, launchConfigurationModel.getParam());
	}

	/**
	 * Asserts a {@code LaunchConfigurationModel}'s abortLaunchOnError flag.
	 */
	@Test
	public void isAbortLaunchOnErrorTest() {
		assertEquals(abortLaunchOnError, launchConfigurationModel.isAbortLaunchOnError());
	}

	/**
	 * Asserts a {@code LaunchConfigurationModel}'s {@link PostLaunchAction}.
	 */
	@Test
	public void getPostLaunchActionTest() {
		assertEquals(postLaunchAction, launchConfigurationModel.getPostLaunchAction());
	}

}
