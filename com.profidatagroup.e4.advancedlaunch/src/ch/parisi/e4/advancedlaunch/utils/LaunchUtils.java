package ch.parisi.e4.advancedlaunch.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.internal.core.LaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationManager;
import org.eclipse.debug.ui.ILaunchGroup;

import ch.parisi.e4.advancedlaunch.LaunchConfigurationModel;
import ch.parisi.e4.advancedlaunch.strategies.AbstractLaunchStrategy;

/**
 * Utility class with methods that can be accessed throughout the entire code.
 */
public class LaunchUtils {

	/**
	 * Finds a {@link ILaunchConfiguration} by name.
	 * 
	 * @param name the name of the <code>ILaunchConfiguration</code>
	 * @return the <code>ILaunchConfiguration</code> or <code>null</code> if not found
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
	 * Loads all the sublaunches of an already existing multilaunch into a list.
	 * 
	 * @param configuration the multilaunch. <b>Cannot</b> be {@code null}.
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
	 * Checks whether a multilaunch references a valid launch.
	 * 
	 * @param config the launch-reference. <b>Cannot</b> be {@code null}.
	 * @return <code>true</code> if it is a valid reference, 
	 * 		   <code>false</code> if launch-configuration should be filtered.
	 * @throws CoreException
	 */
	public static boolean isValidLaunchReference(ILaunchConfiguration config) throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		return manager.isExistingLaunchConfigurationName(config.getName());
	}

	/**
	 * Checks whether all current running launches are terminated. 
	 * 
	 * @return {@code true} if all launches are terminated, 
	 * 		   {@code false} 
	 */
	public static boolean AreAllRunningProcessesTerminated() {
		boolean allTerminated = false;
		for (IProcess[] processesArray : AbstractLaunchStrategy.launchProcesses) {
			for (IProcess process : processesArray) {
				if (process.isTerminated()) {
					allTerminated = true;
				} else {
					allTerminated = false;
					return allTerminated;
				}
			}
		}
		return allTerminated;
	}
	
	//TODO add javadoc
	public static HashMap<String, ILaunchGroup> getAllowedModesMap() {
		HashMap<String, ILaunchGroup> modes = new HashMap<String, ILaunchGroup>();

		LaunchConfigurationManager manager = DebugUIPlugin.getDefault().getLaunchConfigurationManager();
		ILaunchGroup[] launchGroups = manager.getLaunchGroups();

		for (ILaunchGroup launchGroup : launchGroups) {
			if (!modes.containsKey(launchGroup.getMode())) {
				modes.put(launchGroup.getMode(), launchGroup);
			}
		}
		return modes;
	}

	//TODO add javadoc
	public static String[] getAllowedModes() {
		return getAllowedModesMap().keySet().toArray(new String[0]);
	}
}
