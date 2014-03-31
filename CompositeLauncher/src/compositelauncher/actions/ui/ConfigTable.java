package compositelauncher.actions.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import compositelauncher.actions.ui.LaunchConfigDialog.LaunchConfig;

/**
 * Configurations table wrapper.
 * @author tdq
 *
 */
public class ConfigTable {
	private Table table;
	private String[] titles = new String[]{"Launch configuration", "Mode", "Delay"};
	
	/**
	 * Constructor
	 * @param parent - parent element of table
	 */
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
	
	/**
	 * Add configuration to table
	 * @param config - instance of {@link LaunchConfig}
	 * @throws CoreException - if get launch configuration by memento fails
	 */
	public void addConfig(LaunchConfig config) throws CoreException {
		if(config != null) {
			TableItem item = new TableItem(table, SWT.NONE);
			String configuration = config.getName();
			String mode = config.getMode();
			String delay = config.getDelay() > 0 ? String.valueOf(config.getDelay()) : "none";
			item.setText(new String[] {configuration, mode, delay});
			item.setData(config);
			
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfiguration conf = manager.getLaunchConfiguration(config.getMemento());
			ImageDescriptor imageDescriptor = DebugUITools.getDefaultImageDescriptor(conf);
			
			if(imageDescriptor != null) {
				item.setImage(new Image(table.getDisplay(), imageDescriptor.getImageData()));
			}
			
			for (int i=0; i<titles.length; i++) {
				table.getColumn (i).pack ();
			}
		}
	}
	
	/**
	 * Get all configurations from table
	 * @return array of {@link LaunchConfig}
	 */
	public LaunchConfig[] getConfigs() {
		TableItem[] items = table.getItems();
		LaunchConfig[] configs = new LaunchConfig[items.length];
		
		for(int i = 0; i < items.length; i++) {
			configs[i] = (LaunchConfig) items[i].getData();
		}
		
		return configs;
	}
	
	/**
	 * Remove all items
	 */
	public void removeAll() {
		table.removeAll();
	}

	/**
	 * Delete selected item
	 */
	public void deleteSelected() {
		table.remove(table.getSelectionIndices());
	}

	/**
	 * Replace selected item with above item
	 * @throws CoreException - if get launch configuration by memento fails
	 */
	public void moveUpSelected() throws CoreException {
		int selected = table.getSelectionIndex();
		if(selected > 0) {
			LaunchConfig[] configs = getConfigs();
			LaunchConfig tmp = configs[selected-1];
			configs[selected-1] = configs[selected];
			configs[selected] = tmp;
			
			removeAll();
			
			for(LaunchConfig config : configs)
				addConfig(config);
			
			table.setSelection(selected-1);
		}
	}

	/**
	 * Replace selected item with below item
	 * @throws CoreException - if get launch configuration by memento fails
	 */
	public void moveDownSelected() throws CoreException {
		int selected = table.getSelectionIndex();
		if(selected > -1 && selected < table.getItemCount()-1) {
			LaunchConfig[] configs = getConfigs();
			LaunchConfig tmp = configs[selected+1];
			configs[selected+1] = configs[selected];
			configs[selected] = tmp;
			
			table.removeAll();
			
			for(LaunchConfig config : configs)
				addConfig(config);
			
			table.setSelection(selected+1);
		}
	}
}
