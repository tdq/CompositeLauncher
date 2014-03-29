package compositelauncher.actions.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import compositelauncher.actions.ui.LaunchConfigDialog.LaunchConfig;

public class MainTab extends AbstractLaunchConfigurationTab {

	private Table table;
	
	@Override
	public void createControl(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		top.setLayout(new GridLayout(2, false));
		
		table = new Table(top, SWT.FULL_SELECTION | SWT.BORDER);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));		
		
		final String[] titles = {"Launch configuration", "Mode", "Delay"};
		for(String title : titles) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(title);
		}

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
					LaunchConfig config = dialog.getConfig();
					
					if(config != null) {
						TableItem item = new TableItem(table, SWT.NONE);
						String configuration = config.getName();
						String mode = config.getMode();
						String delay = config.getDelay() > 0 ? String.valueOf(config.getDelay()) : "none";
						item.setText(new String[] {configuration, mode, delay});
						item.setData(config.toString());
						
						for (int i=0; i<titles.length; i++) {
							table.getColumn (i).pack ();
						}
					}
				}
			}
		});
		
		Button remove = new Button(right, SWT.PUSH);
		remove.setLayoutData(buttonAlign);
		remove.setText("Remove");
		
		Button moveUp = new Button(right, SWT.PUSH);
		moveUp.setLayoutData(buttonAlign);
		moveUp.setText("Move up");
		
		Button moveDown = new Button(right, SWT.PUSH);
		moveDown.setLayoutData(buttonAlign);
		moveDown.setText("Move down");
				
		setControl(top);
	}

	@Override
	public String getName() {
		return "Main";
	}
		
	@Override
	public void activated(ILaunchConfigurationWorkingCopy configuration) {
		try {
			table.clearAll();
			List<String> configurations = configuration.getAttribute("configurations", new ArrayList<String>(0));
			
			for(String configView : configurations) {
				LaunchConfig config = new LaunchConfigDialog(null).new LaunchConfig(configView);
				TableItem item = new TableItem(table, SWT.NONE);
				String name = config.getName();
				String mode = config.getMode();
				String delay = config.getDelay() > 0 ? String.valueOf(config.getDelay()) : "none";
				item.setText(new String[] {name, mode, delay});
				item.setData(config.toString());
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void deactivated(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
		try {
			configuration.removeAttribute("configurations");
			List<String> configurations = new ArrayList<String>();
			TableItem[] items = table.getItems();
			
			for(TableItem item : items) {
				configurations.add((String) item.getData().toString());
			}
			
			configuration.setAttribute("configurations", configurations);
			configuration.doSave();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initializeFrom(ILaunchConfiguration arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy arg0) {
		// TODO Auto-generated method stub
		
	}

}
