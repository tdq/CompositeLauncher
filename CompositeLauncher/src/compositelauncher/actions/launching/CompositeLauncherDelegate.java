package compositelauncher.actions.launching;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.ui.DebugUITools;

import compositelauncher.actions.ui.LaunchConfig;

/**
 * Delegate for launching composite launcher
 * @author Nikolay Gorokhov
 *
 */
public class CompositeLauncherDelegate implements ILaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {
		@SuppressWarnings("unchecked")
		List<String> configuratoins = configuration.getAttribute("configurations", new ArrayList<String>());
		
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		
		for(String configView : configuratoins) {
			LaunchConfig config = new LaunchConfig(configView);
			
			ILaunchConfiguration conf = manager.getLaunchConfiguration(config.getMemento());
			DebugUITools.buildAndLaunch(conf, config.getMode(), monitor);
			
			try {
				Thread.sleep(config.getDelay()*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				// HOWTO say about the problem?
				break;
			}
		}
	}

}
