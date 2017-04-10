package ch.parisi.e4.advancedlaunch.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationManager;
import org.eclipse.debug.ui.ILaunchGroup;

import ch.parisi.e4.advancedlaunch.LaunchConfigurationModel;
import ch.parisi.e4.advancedlaunch.PseudoProcess;

/**
 * Utility class with methods that can be accessed throughout the entire code.
 */
@SuppressWarnings("restriction") // To get a ILaunchGroup one needs the eclipse internal LaunchConfigurationManager. See getModesMap().
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
	 * Terminates every running configuration in reverse order.
	 */
	public static void terminateRunningConfigurations() {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();

		ILaunch[] launches = manager.getLaunches();
		Collections.reverse(Arrays.asList(launches));

		for (ILaunch launch : launches) {
			IProcess[] processes = launch.getProcesses();
			Collections.reverse(Arrays.asList(processes));

			for (IProcess process : processes) {
				if (process.canTerminate() || process instanceof PseudoProcess) {
					try {
						process.terminate();
					}
					catch (DebugException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Loads all the sublaunches of a multilaunch into a list.
	 * 
	 * @param configuration the multilaunch. <b>Cannot</b> be {@code null}.
	 * @return the sublaunches as a list of {@link LaunchConfigurationModel}s.
	 * @throws CoreException
	 */
	public static List<LaunchConfigurationModel> loadLaunchConfigurations(ILaunchConfiguration configuration) throws CoreException {
		List<LaunchConfigurationModel> launchConfigurationDataList = new ArrayList<>();

		List<String> names = configuration.getAttribute(MultilauncherConfigurationAttributes.CHILDLAUNCH_NAMES_ATTRIBUTE, new ArrayList<String>());
		List<String> modes = configuration.getAttribute(MultilauncherConfigurationAttributes.CHILDLAUNCH_MODES_ATTRIBUTE, new ArrayList<String>());
		List<String> postLaunchActions = configuration.getAttribute(MultilauncherConfigurationAttributes.CHILDLAUNCH_POST_LAUNCH_ACTIONS_ATTRIBUTE, new ArrayList<String>());
		List<String> params = configuration.getAttribute(MultilauncherConfigurationAttributes.CHILDLAUNCH_PARAMS_ATTRIBUTE, new ArrayList<String>());
		List<String> abortLaunchesOnError = configuration.getAttribute(MultilauncherConfigurationAttributes.CHILDLAUNCH_ABORT_LAUNCHES_ON_ERROR_ATTRIBUTE, new ArrayList<String>());
		List<String> actives = configuration.getAttribute(MultilauncherConfigurationAttributes.CHILDLAUNCH_ACTIVES_ATTRIBUTE, new ArrayList<String>());

		for (int i = 0; i < names.size(); i++) {
			launchConfigurationDataList.add(new LaunchConfigurationModel(
					names.get(i),
					modes.get(i),
					PostLaunchActionUtils.convertToPostLaunchAction(postLaunchActions.get(i)),
					params.get(i),
					Boolean.parseBoolean(abortLaunchesOnError.get(i)),
					Boolean.parseBoolean(actives.get(i))));
		}
		return launchConfigurationDataList;
	}

	/**
	 * Returns a list of active {@link LaunchConfigurationModel}s. 
	 * 
	 * @param launchConfigurationModels the launch configuration models or {@code null}.
	 * @return the list of active {@code LaunchConfigurationModel}s or empty if none or parameter was {@code null}.
	 */
	public static List<LaunchConfigurationModel> getActiveLaunchConfigurationModels(List<LaunchConfigurationModel> launchConfigurationModels) {
		if (launchConfigurationModels != null) {
			return launchConfigurationModels
					.stream()
					.filter(launchConfigurationModel -> launchConfigurationModel.isActive())
					.collect(Collectors.toList());
		}
		else {
			return new ArrayList<LaunchConfigurationModel>();
		}
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
	 * Returns all supported modes of a {@link ILaunchConfiguration}.
	 * @param launchConfigurationModel the model with the {@code ILaunchConfiguration}'s name
	 * @return the supported modes of the {@code ILaunchConfiguration}
	 */
	public static List<String> getSupportedModes(LaunchConfigurationModel launchConfigurationModel) {
		List<String> supportedModes = new ArrayList<>();

		try {
			ILaunchConfiguration launchConfiguration = LaunchUtils.findLaunchConfiguration(launchConfigurationModel.getName());
			String[] launchModes = LaunchUtils.getModes();
			for (String launchMode : launchModes) {
				if (launchConfiguration.supportsMode(launchMode)) {
					supportedModes.add(launchMode);
				}
			}
		}
		catch (CoreException e) {
			e.printStackTrace();
		}

		return supportedModes;
	}

	/**
	 * Returns a map which maps a LaunchMode to an 'example' LaunchGroup. 
	 * 
	 *  A launch group identifies a group of launch configurations by a launch
	 *  mode and category.
	 * 
	 *  The method checks for each LaunchGroup if its corresponding mode is already in the map;
	 *  if not, the mode(key) will be put in the map with the first launchGroup that contained it. 
	 *  
	 * @return a map with all modes
	 */
	public static HashMap<String, ILaunchGroup> getModesMap() {
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

	/**
	 * Gets all the modes based on the returned {@code Map} of the {@link #getModesMap()} method.
	 * @return a {@code String} array with all modes
	 */
	public static String[] getModes() {
		return getModesMap().keySet().toArray(new String[0]);
	}

}
