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
			item.setData(config);
			
			try {
				ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
				ILaunchConfiguration conf = manager.getLaunchConfiguration(config.getMemento());
				ImageDescriptor imageDescriptor = DebugUITools.getDefaultImageDescriptor(conf);
				
				if(imageDescriptor != null) {
					item.setImage(new Image(table.getDisplay(), imageDescriptor.getImageData()));
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
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

	public void moveUpSelected() {
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

	public void moveDownSelected() {
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
