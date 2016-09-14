package ch.parisi.e4.advancedlaunch;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.ui.activities.WorkbenchActivityHelper;

import ch.parisi.e4.advancedlaunch.messages.LaunchMessages;

/**
 * Utility class with methods, that can be accessed throughout the entire code.
 */
public class LaunchUtils {

	/**
	 * This method finds a {@link ILaunchConfiguration} by name.
	 * 
	 * @param name
	 *            the name of the <code>ILaunchConfiguration</code>.
	 * @return the <code>ILaunchConfiguration</code>
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
	 * custom-launch, into a list.
	 * 
	 * @param configuration
	 *            the custom-launch
	 * @return the sublaunches as a list of {@link LaunchConfigurationBean}s.
	 * @throws CoreException
	 */
	public static List<LaunchConfigurationBean> loadLaunchConfigurations(ILaunchConfiguration configuration)
			throws CoreException {
		List<String> names = new ArrayList<>();
		List<String> modes = new ArrayList<>();
		List<String> postLaunchActions = new ArrayList<>();
		List<String> params = new ArrayList<>();

		List<LaunchConfigurationBean> launchConfigurationDataList = new ArrayList<>();
		
			names = configuration.getAttribute("names", new ArrayList<String>());
			modes = configuration.getAttribute("modes", new ArrayList<String>());
			postLaunchActions = configuration.getAttribute("postLaunchActions", new ArrayList<String>());
			params = configuration.getAttribute("params", new ArrayList<String>());
		
		for (int i = 0; i < names.size(); i++) {
			launchConfigurationDataList.add(
					new LaunchConfigurationBean(names.get(i), modes.get(i), postLaunchActions.get(i), params.get(i)));
		}
		return launchConfigurationDataList;
	}

	/**
	 * Test if a launch configuration has a valid reference.
	 * 
	 * @param config
	 *            configuration reference
	 * @return <code>true</code> if it is a valid reference, <code>false</code>
	 *         if launch configuration should be filtered.
	 */
	public static boolean isValidLaunchReference(ILaunchConfiguration config) {
		return DebugUIPlugin.doLaunchConfigurationFiltering(config) && !WorkbenchActivityHelper.filterItem(config);
	}

}
