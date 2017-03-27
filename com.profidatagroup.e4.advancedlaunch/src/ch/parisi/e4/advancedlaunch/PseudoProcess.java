package ch.parisi.e4.advancedlaunch;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;

/**
 * A PseudoProcess is added to the ILaunch in order
 * to set a name (label) to the multilaunch.
 */
public class PseudoProcess implements IProcess {

	private ILaunch launch;
	private boolean isTerminated = false;

	/**
	 * This constant accomplishes that the user cannot terminate
	 * this process by the standard eclipse-ui-terminate-button,
	 * but has to use this plugin-specific terminate-all-button.
	 */
	private static final boolean CAN_TERMINATE = false;
	private String label;

	/**
	 * Constructs a {@link PseudoProcess}.
	 * 
	 * @param launch the launch
	 */
	public PseudoProcess(ILaunch launch) {
		this.launch = launch;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	@Override
	public boolean canTerminate() {
		return CAN_TERMINATE;
	}

	@Override
	public boolean isTerminated() {
		return isTerminated;
	}

	/**
	 * Sets whether the multilaunch is terminated.
	 * 
	 * @param isTerminated {@code true} if multilaunch is terminated, otherwise {@code false}.
	 */
	public void setIsTerminated(boolean isTerminated) {
		this.isTerminated = isTerminated;
	}

	@Override
	public void terminate() throws DebugException {
		setIsTerminated(true);
		isTerminated();
	}

	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label text to this Process.
	 * 
	 * @param label the label text
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public ILaunch getLaunch() {
		return launch;
	}

	@Override
	public IStreamsProxy getStreamsProxy() {
		return null;
	}

	@Override
	public void setAttribute(String key, String value) {
		// empty 
	}

	@Override
	public String getAttribute(String key) {
		return null;
	}

	@Override
	public int getExitValue() throws DebugException {
		return 0;
	}
}
