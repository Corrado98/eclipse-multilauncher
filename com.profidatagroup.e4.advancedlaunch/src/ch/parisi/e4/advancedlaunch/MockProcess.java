package ch.parisi.e4.advancedlaunch;

import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.internal.core.DebugCoreMessages;

public class MockProcess implements IProcess {

		private ILaunch launch;

		public MockProcess(ILaunch launch) {
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
			MultiStatus status = new MultiStatus(DebugPlugin.getUniqueIdentifier(), DebugException.REQUEST_FAILED,
					DebugCoreMessages.Launch_terminate_failed, null);
			//stop targets first to free up and sockets, etc held by the target
			// terminate or disconnect debug target if it is still alive
			IDebugTarget[] targets = launch.getDebugTargets();
			for (int i = 0; i < targets.length; i++) {
				IDebugTarget target = targets[i];
				if (target != null) {
					if (target.canTerminate()) {
						try {
							target.terminate();
						} catch (DebugException e) {
							status.merge(e.getStatus());
						}
					} else {
						if (target.canDisconnect()) {
							try {
								target.disconnect();
							} catch (DebugException de) {
								status.merge(de.getStatus());
							}
						}
					}
				}
			}
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
