/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as 
published by the Free Software Foundation, either version 3 of 
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public 
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
*/


package org.psygrid.collection.entry.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.security.SecurityManager;
import org.psygrid.data.model.hibernate.BasicResponse;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import org.psygrid.data.model.hibernate.Provenance;
import org.psygrid.data.model.hibernate.Response;

/**
 * @author Rob Harper
 *
 */
public class ViewProvenanceDialog extends JDialog {

	private static final long serialVersionUID = -8876064520230764562L;

	private static SimpleDateFormat hhMmSsddMmmYyyy = new SimpleDateFormat("HH:mm:ss dd-MMM-yyyy");
	
	private DefaultFormBuilder builder;
	
	private JButton okButton;
	
	private JTable provTable;
	
	private JScrollPane scrollpane;
	
	private Response response;

	public ViewProvenanceDialog(Response response, JFrame owner){
		super(owner, Messages.getString("ViewProvenanceDialog.dialogTitle"), true);
		this.response = response;
		initBuilder();
		initComponents();
		initEventHandling();
		build();
		pack();
		setLocation(WindowUtils.getPointForCentering(this));
	}

	private void initBuilder() {
		builder = new DefaultFormBuilder(new FormLayout(
				"default:grow"), //$NON-NLS-1$
				new JPanel());
		builder.setDefaultDialogBorder();
	}

	private void initComponents() {

		/*
		 * "Spoof last change" - if a value is ammended before it has been
		 * committed then at edit time the old value is modified directly,
		 * with proper history only being recorded at the end of the editing
		 * session. This causes the history to look wrong, as the most recent
		 * edit is missed. All of the code related to spoofLastChange just
		 * does some manipulation of the history to make it look more sane.
		 */
		boolean spoofLastChange = false;
		String oldValue = null;
		if ( response instanceof BasicResponse ){
			BasicResponse br = (BasicResponse)response;
			if ( br.getValue().isValueChanged() ){
				spoofLastChange = true;
				oldValue = br.getValue().getOldValueAsString();
			}
		}
		
		String[] columnNames = new String[]{
		Messages.getString("ViewProvenanceDialog.currentValColName"), 
		Messages.getString("ViewProvenanceDialog.userColName"), 
		Messages.getString("ViewProvenanceDialog.timeColName"), 
		Messages.getString("ViewProvenanceDialog.commentColName")};
		int size = response.getProvenance().size();
		if ( spoofLastChange ){
			//we will be adding one more item to the history than
			//there is in the provenance list
			size++;
		}
		Object[][] data = new Object[size][4];
		for (int i=0, c=response.getProvenance().size(); i<c; i++ ){
			Provenance prov = response.getProvenance().get(i);

			String user = prov.getUser();
			if ( null == user ){
				user = SecurityManager.getInstance().getUserName();
			}
			else{
				if ( user.startsWith("CN") || user.startsWith("cn")){
					user = user.substring(user.indexOf("=")+1, user.indexOf(","));
				}
			}

			String currentValue = null;
			if ( spoofLastChange && i == c-1 ){
				//Last item in the provenance list and we need to spoof
				//the last change - instead of the value referenced by
				//the provenance object (which will have been edited)
				//we use the value as it was at the start of the editing session
				currentValue = oldValue;
			}
			else{
				if ( null != prov.getCurrentValue() ){
					currentValue = prov.getCurrentValue().getValueAsString();
				}
			}
			
			data[i] = new String[]{ currentValue,
									user,
									hhMmSsddMmmYyyy.format(prov.getTimestamp()),
									prov.getComment() };
									
			if ( spoofLastChange && i == c-1 ){
				//Now we add an extra row to the history table to represent
				//the change that has been made in this editing session.
				String newValue = null;
				if ( null != prov.getCurrentValue() ){
					newValue = prov.getCurrentValue().getValueAsString();
				}
				data[i+1] = new String[]{ newValue,
										  user,
										  hhMmSsddMmmYyyy.format(new Date()),
										  null };
			}
		}
		
		DefaultTableModel model = new DefaultTableModel(data, columnNames){

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				//Make table read-only
				return false;
			}

		};
		
		provTable = new JTable(model);
		provTable.setPreferredScrollableViewportSize(new Dimension(500, 100));
		
		scrollpane = new JScrollPane(provTable);
	
		okButton = new JButton(EntryMessages.getString("Entry.ok"));
		
	}

	private void initEventHandling(){
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeWindow();
			}
		});
		
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

	}
	
	private void build(){
		builder.append(scrollpane);
		builder.appendUnrelatedComponentsGapRow();
		builder.nextLine(2);
		JPanel buttonsPanel = ButtonBarFactory.buildRightAlignedBar(okButton);
		builder.append(buttonsPanel, builder.getColumnCount());
		getContentPane().add(builder.getPanel());
	}
	
	private void closeWindow(){
		unlock();
		dispose();
	}

	/**
	 * Unlock the values so they may be edited again.
	 */
	private void unlock(){
		for (int i=0, c=response.getProvenance().size(); i<c; i++ ){
			Provenance prov = response.getProvenance().get(i);
			prov.unlock();
		}
	}
	
}
