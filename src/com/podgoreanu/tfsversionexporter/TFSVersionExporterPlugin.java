package com.podgoreanu.tfsversionexporter;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author a.podgoreanu
 */
public class TFSVersionExporterPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.podgoreanu.tfsversionexporter";

	// The shared instance
	private static TFSVersionExporterPlugin plugin;

	/**
	 * The constructor
	 */
	public TFSVersionExporterPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
	 * BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static TFSVersionExporterPlugin getDefault() {
		return plugin;
	}

	public static void log(IStatus status) {
		assert (status != null);
		try {
			TFSVersionExporterPlugin.getDefault().getLog().log(status);
		} catch (Throwable t) {
			System.err.println("Could not write to Oclipse plugin log:");
			t.printStackTrace();
			System.err.println("Log message data:");
			System.err.println((Object) status);
		}
	}

	public static void log(String message) {
		TFSVersionExporterPlugin.log((IStatus) new Status(1, PLUGIN_ID, 0, message, null));
	}

}
