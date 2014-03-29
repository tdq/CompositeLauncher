package compositelauncher.actions.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import compositelauncher.actions.ui.LaunchConfigDialog.LaunchConfig;

public class ConfigTable {
	private Table table;
	private String[] titles = new String[]{"Launch configuration", "Mode", "Delay"};
	
	public ConfigTable(Composite parent) {
		table = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		for(String title : titles) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(title);
		}
	}
	
	public void addConfig(LaunchConfig config) {
		if(config != null) {
			TableItem item = new TableItem(table, SWT.NONE);
			String configuration = config.getName();
			String mode = config.getMode();
			String delay = config.getDelay() > 0 ? String.valueOf(config.getDelay()) : "none";
			item.setText(new String[] {configuration, mode, delay});
			//item.setData(config.toString());
			item.setData(config);
			
			for (int i=0; i<titles.length; i++) {
				table.getColumn (i).pack ();
			}
		}
	}
	
	public LaunchConfig[] getConfigs() {
		TableItem[] items = table.getItems();
		LaunchConfig[] configs = new LaunchConfig[items.length];
		
		for(int i = 0; i < items.length; i++) {
			configs[i] = (LaunchConfig) items[i].getData();
		}
		
		return configs;
	}
	
	public void removeAll() {
		table.removeAll();
	}

	public void deleteSelected() {
		table.remove(table.getSelectionIndices());
	}
}
