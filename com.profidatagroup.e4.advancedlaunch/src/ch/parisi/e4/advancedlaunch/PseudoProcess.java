package ch.parisi.e4.advancedlaunch;

import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.internal.core.DebugCoreMessages;

public class PseudoProcess implements IProcess {

		private ILaunch launch;

		public PseudoProcess(ILaunch launch) {
			this.launch = launch;
		}

		@Override
		public <T> T getAdapter(Class<T> adapter) {
			return null;
		}

		@Override
		public boolean canTerminate() {
			return false;
		}

		@Override
		public boolean isTerminated() {
			return true;
		}

		@Override
		public void terminate() throws DebugException {
			//TODO remove all sublaunches. 
			//LaunchGroupConfigurationDelegate.terminateAllConfigurationsButtonPressed = true;
		}

		@Override
		public String getLabel() {
			return null;
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
