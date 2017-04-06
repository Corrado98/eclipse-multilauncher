package ch.parisi.e4.advancedlaunch;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import ch.parisi.e4.advancedlaunch.utils.DatabindingProperties;
import ch.parisi.e4.advancedlaunch.utils.PostLaunchAction;

/**
 * Class which contains user-defined information 
 * of one launch, within a custom-configuration.
 */
public class LaunchConfigurationModel {
	private String name;
	private String mode;
	private String param;
	private boolean abortLaunchOnError;
	private boolean active;

	/**
	 * {@link PostLaunchAction}.
	 */
	private PostLaunchAction postLaunchAction;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/**
	 * Constructs a {@code LaunchConfigurationModel} with the required arguments.
	 * 
	 * @param name the launch-name of the selected launch
	 * @param mode the chosen launch-mode
	 * @param postLaunchAction the chosen postLaunchAction
	 * @param param the chosen runtime-parameter
	 * @param abortLaunchOnError whether to abort a launch on error
	 * @param active whether launch is active
	 */
	public LaunchConfigurationModel(String name, String mode, PostLaunchAction postLaunchAction, String param, boolean abortLaunchOnError, boolean active) {
		this.name = name;
		this.mode = mode;
		this.postLaunchAction = postLaunchAction;
		this.param = param;
		this.abortLaunchOnError = abortLaunchOnError;
		this.active = active;
	}

	/**
	 * Adds a property change listener to the specified property.
	 * 
	 * @param propertyName the specified property name
	 * @param listener the property change listener
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Removes a property change listener.
	 * 
	 * @param listener the property change listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * Gets a {@code LaunchConfigurationModel}'s name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets a {@code LaunchConfigurationModel}'s launch mode.
	 * 
	 * @return the launch mode 
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * Sets a {@code LaunchConfigurationModel}'s launch mode and fires a {@link java.beans.PropertyChangeEvent}.
	 * 
	 * @param mode the launch mode
	 */
	public void setMode(String mode) {
		propertyChangeSupport.firePropertyChange(DatabindingProperties.MODE_PROPERTY, this.mode, this.mode = mode);
	}

	/**
	 * Gets a {@code LaunchConfigurationModel}'s {@link PostLaunchAction}. 
	 * 
	 * @return the post launch action
	 */
	public PostLaunchAction getPostLaunchAction() {
		return postLaunchAction;
	}

	/**
	 * Sets a {@code LaunchConfigurationModel}'s {@link PostLaunchAction} and fires a {@link java.beans.PropertyChangeEvent}.
	 * 
	 * @param postLaunchAction the post launch action
	 */
	public void setPostLaunchAction(PostLaunchAction postLaunchAction) {
		propertyChangeSupport.firePropertyChange(DatabindingProperties.POST_LAUNCH_ACTION_PROPERTY, this.postLaunchAction, this.postLaunchAction = postLaunchAction);
	}

	/**
	 * Gets a {@code LaunchConfigurationModel}'s runtime parameter.
	 *  
	 * @return the parameter
	 */
	public String getParam() {
		return param;
	}

	/**
	 * Sets a {@code LaunchConfigurationModel}'s parameter and fires a {@link java.beans.PropertyChangeEvent}.
	 * 
	 * @param param the parameter
	 */
	public void setParam(String param) {
		propertyChangeSupport.firePropertyChange(DatabindingProperties.PARAM_PROPERTY, this.param, this.param = param);
	}

	/**
	 * Returns whether a multilaunch will stop launching when an error occurs on this childlaunch-entry.
	 * 
	 * @return {@code true} when a multilaunch must abort on error {@code false} when launching continues.
	 */
	public boolean isAbortLaunchOnError() {
		return abortLaunchOnError;
	}

	/**
	 * Sets whether a multilaunch will stop launching when an error occurs on this childlaunch-entry. 
	 * 
	 * This method fires a {@link java.beans.PropertyChangeEvent}.
	 * 
	 * @param abortLaunchOnError {@code true} when multilaunch must abort on error {@code false} when launching should continue.
	 */
	public void setAbortLaunchOnError(boolean abortLaunchOnError) {
		propertyChangeSupport.firePropertyChange(DatabindingProperties.ABORT_LAUNCH_ON_ERROR_PROPERTY, this.abortLaunchOnError, this.abortLaunchOnError = abortLaunchOnError);
	}

	/**
	 * Gets a {@code LaunchConfigurationModel}'s active state. 
	 * 
	 * @return {@code true} when a childlaunch is active, otherwise {@code false}. 
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets whether a {@code LaunchConfigurationModel}'s active.
	 * 
	 * This method fires a {@link java.beans.PropertyChangeEvent}. 
	 * 
	 * @param active the active flag
	 */
	public void setActive(boolean active) {
		propertyChangeSupport.firePropertyChange(DatabindingProperties.ACTIVE_PROPERTY, this.active, this.active = active);
	}

}
