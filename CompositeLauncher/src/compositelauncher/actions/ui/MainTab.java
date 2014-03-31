package compositelauncher.actions.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;

import compositelauncher.actions.ui.LaunchConfigDialog.LaunchConfig;

public class MainTab extends AbstractLaunchConfigurationTab {

	private ConfigTable table;
	
	@Override
	public void createControl(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		top.setLayout(new GridLayout(2, false));
		
		table = new ConfigTable(top);

		Composite right = new Composite(top, SWT.NONE);
		right.setLayout(new GridLayout(1, false));
		right.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		
		GridData buttonAlign = new GridData(SWT.FILL, SWT.TOP, true, false);
		
		Button add = new Button(right, SWT.PUSH);
		add.setLayoutData(buttonAlign);
		add.setText("Add");
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LaunchConfigDialog dialog = new LaunchConfigDialog(getShell());
				dialog.create();
				
				if(dialog.open() == Window.OK) {
					try {
						LaunchConfig config = dialog.getConfig();
						table.addConfig(config);
						updateLaunchConfigurationDialog();
					} catch (CoreException exception) {
						exception.printStackTrace();
						MessageDialog.openError(getShell(), "Error", exception.getLocalizedMessage());
					}
				}
			}
		});
		
		Button remove = new Button(right, SWT.PUSH);
		remove.setLayoutData(buttonAlign);
		remove.setText("Remove");
		
		remove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				table.deleteSelected();
				
				updateLaunchConfigurationDialog();
			}
		});
		
		Button moveUp = new Button(right, SWT.PUSH);
		moveUp.setLayoutData(buttonAlign);
		moveUp.setText("Move up");
		
		moveUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					table.moveUpSelected();
					updateLaunchConfigurationDialog();
				} catch (CoreException exception) {
					exception.printStackTrace();
					MessageDialog.openError(getShell(), "Error", exception.getLocalizedMessage());
				}
			}
		});
		
		Button moveDown = new Button(right, SWT.PUSH);
		moveDown.setLayoutData(buttonAlign);
		moveDown.setText("Move down");
				
		moveDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					table.moveDownSelected();
					updateLaunchConfigurationDialog();
				} catch (CoreException exception) {
					exception.printStackTrace();
					MessageDialog.openError(getShell(), "Error", exception.getLocalizedMessage());
				}
			}
		});
		
		setControl(top);
	}

	@Override
	public String getName() {
		return "Main";
	}
	
	@Override
	public Image getImage() {
		return super.getImage();
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// Do nothing
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			table.removeAll();
			List<String> configurations = configuration.getAttribute("configurations", new ArrayList<String>(0));
			
			for(String configView : configurations) {
				LaunchConfig config = new LaunchConfigDialog(null).new LaunchConfig(configView);			
				table.addConfig(config);
			}
		} catch (CoreException e) {
			MessageDialog.openError(getShell(), "Error", e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		try {
			configuration.removeAttribute("configurations");
			List<String> configurations = new ArrayList<String>();
			
			LaunchConfig[] configs = table.getConfigs();
			for(LaunchConfig config : configs) {
				configurations.add(config.toString());
			}
			
			configuration.setAttribute("configurations", configurations);
			configuration.doSave();
		} catch (CoreException e) {
			MessageDialog.openError(getShell(), "Error", e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}
