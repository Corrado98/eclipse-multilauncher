package ch.parisi.e4.advancedlaunch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.ui.activities.WorkbenchActivityHelper;

public class LaunchUtils {

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
	
	public static List<LaunchConfigurationBean> loadLaunchConfigurations(ILaunchConfiguration configuration) throws CoreException {
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
			launchConfigurationDataList.add(new LaunchConfigurationBean(names.get(i), modes.get(i),
					postLaunchActions.get(i), params.get(i)));
		}
		return launchConfigurationDataList;
	}
	
	/**
	 * Test if a launch configuration is a valid reference.
	 * 
	 * @param config
	 *            configuration reference
	 * @return <code>true</code> if it is a valid reference, <code>false</code>
	 *         if launch configuration should be filtered
	 */
	public static boolean isValidLaunchReference(ILaunchConfiguration config) {
		return DebugUIPlugin.doLaunchConfigurationFiltering(config) && !WorkbenchActivityHelper.filterItem(config);
	}

}
