package ch.parisi.e4.advancedlaunch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.ui.activities.WorkbenchActivityHelper;

/**
 * Utility class with methods that can be accessed throughout the entire code.
 */
public class LaunchUtils {

	/**
	 * This method finds a {@link ILaunchConfiguration} by name.
	 * 
	 * @param name
	 *            the name of the <code>ILaunchConfiguration</code>.
	 * @return the <code>ILaunchConfiguration</code> or <code>null</code> if not
	 *         found
	 * @throws CoreException
	 */
	public static ILaunchConfiguration findLaunchConfiguration(String name) throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfiguration[] configurations = manager.getLaunchConfigurations();

		for (ILaunchConfiguration launchConfiguration : configurations) {
			if (name.equals(launchConfiguration.getName())) {
				return launchConfiguration;
			}
		}

		return null;
	}

	/**
	 * This method loads all the sublaunches of an already existing
	 * custom-launch into a list.
	 * 
	 * @param configuration
	 *            the custom-launch. <b>Cannot</b> be {@code null}.
	 * @return the sublaunches as a list of {@link LaunchConfigurationModel}s.
	 * @throws CoreException
	 */
	public static List<LaunchConfigurationModel> loadLaunchConfigurations(ILaunchConfiguration configuration)
			throws CoreException {
		List<String> names = new ArrayList<>();
		List<String> modes = new ArrayList<>();
		List<String> postLaunchActions = new ArrayList<>();
		List<String> params = new ArrayList<>();

		List<LaunchConfigurationModel> launchConfigurationDataList = new ArrayList<>();

		names = configuration.getAttribute("names", new ArrayList<String>());
		modes = configuration.getAttribute("modes", new ArrayList<String>());
		postLaunchActions = configuration.getAttribute("postLaunchActions", new ArrayList<String>());
		params = configuration.getAttribute("params", new ArrayList<String>());

		for (int i = 0; i < names.size(); i++) {
			launchConfigurationDataList.add(new LaunchConfigurationModel(names.get(i), modes.get(i),
					PostLaunchActionUtils.convertToPostLaunchAction(postLaunchActions.get(i)), params.get(i)));
		}
		return launchConfigurationDataList;
	}

	/**
	 * Checks recursively if a custom-launch would cause an
	 * infinite-loop.
	 * 
	 * @param launchName
	 *            the name of the custom-launch
	 * @param launchConfigurationModels
	 *            the childlaunches of the custom-launch
	 * @return {@code true} if an infinite-loop is detected within a custom
	 *         launch. {@code false} if no infinite-loop is ever detected at any
	 *         nesting depth.
	 * @throws CoreException
	 */
	public static boolean isRecursiveLaunchConfiguration(String launchName,
			List<LaunchConfigurationModel> launchConfigurationModels) throws CoreException {
		for (LaunchConfigurationModel launchConfigurationBean : launchConfigurationModels) {
			if (launchName.equals(launchConfigurationBean.getName())) {
				return true;
			}

			ILaunchConfiguration childLaunchConfiguration = LaunchUtils
					.findLaunchConfiguration(launchConfigurationBean.getName());
			if (childLaunchConfiguration != null) {
				List<LaunchConfigurationModel> childLaunchConfigurationModels = LaunchUtils
						.loadLaunchConfigurations(childLaunchConfiguration);
				if (isRecursiveLaunchConfiguration(launchName, childLaunchConfigurationModels)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * This method tests if a custom-configuration references a valid launch.
	 * 
	 * @param config
	 *            configuration reference <b>Cannot</b> be {@code null}.
	 * @return <code>true</code> if it is a valid reference, <code>false</code>
	 *         if launch configuration should be filtered.
	 * @throws CoreException
	 */
	public static boolean isValidLaunchReference(ILaunchConfiguration config) throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		return manager.isExistingLaunchConfigurationName(config.getName());
	}

}
