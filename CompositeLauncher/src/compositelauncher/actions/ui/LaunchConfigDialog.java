package compositelauncher.actions.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class LaunchConfigDialog extends TitleAreaDialog {
	
	public class LaunchConfig {
		private String memento;
		private String name;
		private String mode;
		private int delay;
		
		public LaunchConfig(String memento, String name, String mode, int delay) {
			this.memento = memento;
			this.name = name;
			this.mode = mode;
			this.delay = delay > 0 ? delay : 0;
		}
		
		public LaunchConfig(String view) {
			String[] tokens = view.split("\t");
			
			//TODO check if tokens has all elements otherwise use default values
			
			memento = tokens[0];
			name = tokens[1];
			mode = tokens[2];
			delay = Integer.parseInt(tokens[3]);
		}
		
		public String getMemento() {
			return memento;
		}
		
		public String getName() {
			return name;
		}
		
		public String getMode() {
			return mode;
		}
		
		public int getDelay() {
			return delay;
		}
		
		@Override
		public String toString() {
			return memento+"\t"+name+"\t"+mode+"\t"+delay;
		}
	}
	
	private ILaunchConfiguration configuration;
	private String mode;
	private int delay;
	private Tree launchers;
	private Combo modes;
	private Spinner delays;

	public LaunchConfigDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	public void create() {
		super.create();
		setTitle("Add launch configuration");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		area.setLayout(new GridLayout(1, false));
		
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType[] types = manager.getLaunchConfigurationTypes();
				
		launchers = new Tree(area, SWT.BORDER);
		launchers.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		try {
			for(ILaunchConfigurationType type : types) {
				ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
				
				if(configurations.length > 0) {
					TreeItem typeItem = new TreeItem(launchers, SWT.NONE);
					typeItem.setText(type.getName());
					
					for(ILaunchConfiguration configuration : configurations) {
						TreeItem confItem = new TreeItem(typeItem, SWT.NONE);
						confItem.setText(configuration.getName());
						confItem.setData(configuration);
					}
				}
			}
		} catch (CoreException e) {
			MessageDialog.openError(getShell(), "Error", e.getLocalizedMessage());
			e.printStackTrace();
		}
				
		Composite modeComposite = new Composite(area, SWT.NONE);
		modeComposite.setLayout(new GridLayout(2, false));
		
		Label modeLabel = new Label(modeComposite, SWT.NONE);
		modeLabel.setText("Launch mode:");
		
		modes = new Combo(modeComposite, SWT.READ_ONLY);
		
		ILaunchMode[] launchModes = manager.getLaunchModes();
		
		for(ILaunchMode mode : launchModes) {
			modes.add(mode.getIdentifier());
			modes.setData(mode.getIdentifier(), mode);
		}
		
		modes.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		if(launchModes.length > 0)
			modes.select(0);
		
		Composite delayComposite = new Composite(area, SWT.NONE);
		delayComposite.setLayout(new GridLayout(2, false));
		
		Label delayLabel = new Label(delayComposite, SWT.NONE);
		delayLabel.setText("Post launch delay (sec):");
		
		delays = new Spinner(delayComposite, SWT.BORDER);
		delays.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		delays.setMinimum(0);
		delays.setMaximum(86401);
		
		return area;
	}
	
	@Override
	protected void okPressed() {
		TreeItem[] selectedItems = launchers.getSelection();
		if(selectedItems.length > 0) {
			configuration = (ILaunchConfiguration) selectedItems[0].getData();
		}

		mode = modes.getItem(modes.getSelectionIndex());		
		delay = delays.getSelection();
		
		super.okPressed();
	}
	
	public LaunchConfig getConfig() {
		try {
			return new LaunchConfig(configuration.getMemento(), configuration.getName(), mode, delay);
		} catch (CoreException e) {
			MessageDialog.openError(getShell(), "Error", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return null;
	}
}
