package co.wds.testingtools;

import java.security.Permission;

public class SystemExitTestUtils {
	private SystemExitTestUtils() {
		
	}
	
	public static class NoExitSecurityManager extends SecurityManager {
		private void allowAnything() {
		}
		
		@Override
	    public void checkPermission(Permission perm) {
			allowAnything();
	    }

		@Override
		public void checkPermission(Permission perm, Object context) {
			allowAnything();
	    }
		
	    @Override
		public void checkExit(int status) {
			super.checkExit(status);
			throw new SystemExitException(status);
		}
	}
	
	public static class SystemExitException extends SecurityException {
		private static final long serialVersionUID = 1L;
		private int status;
		
		public SystemExitException(int status) {
			this.status = status;
		}
		
		public int getStatus() {
			return status;
		}
	}
	
	public static void disableSystemExit() {
		System.setSecurityManager(new NoExitSecurityManager());
	}

	public static void enableSystemExit() {
		System.setSecurityManager(null);
	}
}
