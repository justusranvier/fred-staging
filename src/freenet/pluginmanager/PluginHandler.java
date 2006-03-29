package freenet.pluginmanager;

/**
 * Methods to handle a specific plugin (= set it up and start it)
 * 
 * @author cyberdo
 */
public class PluginHandler {

	/**
	 * Will get all needed info from the plugin, put it into the Wrapper. Then
	 * the Pluginstarter will be greated, and the plugin fedto it, starting the
	 * plugin.
	 * 
	 * the pluginInfoWrapper will then be returned
	 * 
	 * @param plug
	 */
	public static PluginInfoWrapper startPlugin(PluginManager pm, String filename, FredPlugin plug, PluginRespirator pr) {
		PluginStarter ps = new PluginStarter(pr);
		PluginInfoWrapper pi = new PluginInfoWrapper(plug, ps, filename);
		
		// This is an ugly trick... sorry ;o)
		// The thread still exists as an identifier, but is never started if the
		// plugin doesn't require it
		ps.setPlugin(pm, plug);
		if (!pi.isThreadlessPlugin())
			ps.start();
		ps.run();
		return pi;
	}
	
	private static class PluginStarter extends Thread {
		private Object plugin = null;
		private PluginRespirator pr;
		private PluginManager pm = null;
		
		public PluginStarter(PluginRespirator pr) {
			this.pr = pr;
		}
		
		public void setPlugin(PluginManager pm, Object plugin) {
			this.plugin = plugin;
			this.pm = pm;
		}
		
		public void run() {
			int seconds = 120; // give up after 2 min
			while (plugin == null) {
				// 1s polling
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				if (seconds-- <= 0)
					return;
			}
			
			if (plugin instanceof FredPlugin) {
				FredPlugin plug = ((FredPlugin)plugin);
				//if (plug.handles(FredPlugin.handleFproxy))
				
				((FredPlugin)plugin).runPlugin(pr);
			}
			// If not FredPlugin, then the whole thing is aborted,
			// and then this method will return, killing the thread
			
			pm.removePlugin(this);
		}
		
	}
}
