package compositelauncher.actions.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
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

/**
 * Dialog for selecting launch configuration
 * @author Nikolay Gorokhov
 *
 */
public class LaunchConfigDialog extends TitleAreaDialog {
	
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
					ImageDescriptor imageDescriptor = DebugUITools.getDefaultImageDescriptor(type);
					
					Image icon = null;
					if(imageDescriptor != null) {
						icon = new Image(parent.getDisplay(), imageDescriptor.getImageData());
						typeItem.setImage(icon);
					}
					
					for(ILaunchConfiguration configuration : configurations) {
						TreeItem confItem = new TreeItem(typeItem, SWT.NONE);
						confItem.setText(configuration.getName());
						confItem.setData(configuration);
						if(icon != null) {
							confItem.setImage(icon);
						}
					}
				}
			}
		} catch (CoreException e) {
			MessageDialog.openError(getShell(), "Error", e.getLocalizedMessage());
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
		boolean isOk = false;
		TreeItem[] selectedItems = launchers.getSelection();
		if(selectedItems.length > 0) {
			if(selectedItems[0].getData() != null) {
				configuration = (ILaunchConfiguration) selectedItems[0].getData();
				isOk = true;
			}
		}

		mode = modes.getItem(modes.getSelectionIndex());		
		delay = delays.getSelection();
		
		if(isOk) {
			super.okPressed();
		}
	}
	
	/**
	 * Returns selected configuration
	 * @return instance of {@link LaunchConfig} or null
	 */
	public LaunchConfig getConfig() {
		try {
			if(configuration != null)
				return new LaunchConfig(configuration.getMemento(), configuration.getName(), mode, delay);
			else
				return null;
		} catch (CoreException e) {
			MessageDialog.openError(getShell(), "Error", e.getLocalizedMessage());
		}
		
		return null;
	}
}
