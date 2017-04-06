package ch.parisi.e4.advancedlaunch;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.parisi.e4.advancedlaunch.utils.PostLaunchAction;

/**
 * Tests {@link LaunchConfigurationModel}.
 */
public class LaunchConfigurationModelTest {

	private String name = "Multilauncher";
	private String mode = "run";
	private String param = "";
	private PostLaunchAction postLaunchAction = PostLaunchAction.NONE;
	private boolean abortLaunchOnError = false;
	private boolean active = true;
	private LaunchConfigurationModel launchConfigurationModel = new LaunchConfigurationModel(
			name,
			mode,
			postLaunchAction,
			param,
			abortLaunchOnError,
			active);

	/**
	 * Tests a {@code LaunchConfigurationModel}'s name.
	 */
	@Test
	public void getNameTest() {
		assertEquals(name, launchConfigurationModel.getName());
	}

	/**
	 * Tests a {@code LaunchConfigurationModel}'s launch mode.
	 */
	@Test
	public void getModeTest() {
		assertEquals(mode, launchConfigurationModel.getMode());
	}

	/**
	 * Tests a {@code LaunchConfigurationModel}'s runtime parameter.
	 */
	@Test
	public void getParamTest() {
		assertEquals(param, launchConfigurationModel.getParam());
	}

	/**
	 * Tests a {@code LaunchConfigurationModel}'s {@link PostLaunchAction}.
	 */
	@Test
	public void getPostLaunchActionTest() {
		assertEquals(postLaunchAction, launchConfigurationModel.getPostLaunchAction());
	}

	/**
	 * Tests a {@code LaunchConfigurationModel}'s abortLaunchOnError flag.
	 */
	@Test
	public void isAbortLaunchOnErrorTest() {
		assertEquals(abortLaunchOnError, launchConfigurationModel.isAbortLaunchOnError());
	}

	/**
	 * Tests a {@code LaunchConfigurationModel}'s active flag.
	 */
	@Test
	public void isActiveTest() {
		assertEquals(active, launchConfigurationModel.isActive());
	}

}
