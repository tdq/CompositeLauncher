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

import compositelauncher.actions.ui.LaunchConfigDialog;
import compositelauncher.actions.ui.LaunchConfigDialog.LaunchConfig;

public class CompositeLauncherDelegate implements ILaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {
		List<String> configuratoins = configuration.getAttribute("configurations", new ArrayList<String>());
		
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		
		for(String configView : configuratoins) {
			LaunchConfig config = new LaunchConfigDialog(null).new LaunchConfig(configView);
			System.out.println("Launch "+config.getName()+
					" in mode "+config.getMode()+
					" delay "+config.getDelay());
			
			ILaunchConfiguration conf = manager.getLaunchConfiguration(config.getMemento());
			//conf.launch(config.getMode(), monitor);
			DebugUITools.buildAndLaunch(conf, config.getMode(), monitor);
			
			try {
				Thread.sleep(config.getDelay()*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
				// HOWTO say about the problem?
			}
		}
		
		System.out.println("Finish");
	}

}
