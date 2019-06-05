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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.Selectable;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.RecordsList;
import org.psygrid.collection.entry.persistence.RecordsListWrapper;
import org.psygrid.collection.entry.persistence.SecondaryIdentifierMap;
import org.psygrid.collection.entry.ui.CommitTableModel.Column;

public class CommitDialog extends RecordDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * This constructor builds the dialog to operate in 'show commit results' mode. 
     * @param frame
     * @param recordsList
     * @param showCommitProgress
     */
    public CommitDialog(JFrame application, List<RecordsListWrapper.Item> recordsList, boolean resultDisplayMode) {
    	super(application, title(resultDisplayMode), getTableModel(recordsList, resultDisplayMode), message(resultDisplayMode), resultDisplayMode);    	
    }

    public CommitDialog(Application application, RecordsListWrapper recordsList, boolean resultDisplayMode) {
        super(application, title(resultDisplayMode), getTableModel(recordsList, resultDisplayMode), message(resultDisplayMode), resultDisplayMode);
    }
    
    private static CommitTableModel getTableModel(List<RecordsListWrapper.Item> recordsList, boolean resultDisplayMode){
    	 EnumSet<Column> columns;
         
         if(resultDisplayMode){
         	columns = EnumSet.of(Column.IDENTIFIER, Column.COMMITRESULT);
         }else{
         	columns = EnumSet.of(Column.SELECTED, Column.IDENTIFIER, Column.STATUS);
         }
         
         CommitTableModel tableModel = new CommitTableModel(recordsList, columns);
         return tableModel;
    }
    
    private static CommitTableModel getTableModel(RecordsListWrapper recordsList, boolean resultDisplayMode) {
        EnumSet<Column> columns;
        
        if(resultDisplayMode){
        	columns = EnumSet.of(Column.SELECTED, Column.COMMITRESULT);
        }else{
        	columns = EnumSet.of(Column.SELECTED, Column.IDENTIFIER, Column.STATUS);
        }
                
        CommitTableModel tableModel = new CommitTableModel(recordsList,
                columns);
        return tableModel;
    }
    
    private static String title(boolean resultDisplayMode) {
    	if(!resultDisplayMode){
    	       return Messages.getString("CommitDialog.normalDisplayModeTitle");
    	}else{
    		return Messages.getString("CommitDialog.resultDisplayModeTitle");
    	}
 
    }
    
    private static String message(boolean resultDisplayMode) {
    	if(!resultDisplayMode){
            return Messages.getString("CommitDialog.normalModeInstructions");
    	}else{
    		return Messages.getString("CommitDialog.resultDisplayModeInstructions");
    	}

    }
    
    @Override
    protected void handleRecordSelected(ActionEvent event) {
        CommitTableModel tableModel = (CommitTableModel) getTable().getModel();
        if (tableModel.getSelectedItems().size() < 1) {
            String title = Messages.getString("CommitDialog.noRecordSelectedTitle");
            String message = Messages.getString("CommitDialog.noRecordsSelectedMessage");
            JOptionPane.showMessageDialog(this, message, title, 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        else{
        	//check for secondary records involved in dual data entry being committed
        	//but their primary partners not
        	PersistenceManager pManager = PersistenceManager.getInstance();
        	SecondaryIdentifierMap sidMap = null;
        	synchronized (pManager) {
				sidMap = pManager.getSecondaryIdentifierMap();
			}
        	for ( Selectable<RecordsListWrapper.Item> sItem: tableModel.getSelectedItems()) {
        		RecordsListWrapper.Item item = sItem.getObject();
        		String primaryId = sidMap.getPrimary(item.getIdentifier().getIdentifier());
        		if ( null != primaryId ){
        			for ( Selectable<RecordsListWrapper.Item> srli: tableModel.getItems() ){
        				RecordsListWrapper.Item rli = srli.getObject();
        				if ( rli.getIdentifier().getIdentifier().equals(primaryId) && !srli.isSelected() ){
        					//The primary record for this secondary is in the list to commit,
        					//but it is not selected - this is not allowed so display a message
        					//to the user then exit
        		            String title = Messages.getString("CommitDialog.noPrimarySelectedTitle");
        		            String message = Messages.getString("CommitDialog.noPrimarySelectedMsg_p1") +
        		            	item.getIdentifier().getIdentifier()+
        		            	Messages.getString("CommitDialog.noPrimarySelectedMsg_p2") +
        		            	primaryId + Messages.getString("CommitDialog.noPrimarySelectedMsg_p3");
        		            JOptionPane.showMessageDialog(this, message, title, 
        		                    JOptionPane.ERROR_MESSAGE);
        		            return;
        				}
        			}
        		}
        	}
        }
        dispose();
        fireActionPerformed(new ActionEvent(this, event.getID(), event.getActionCommand()));
    }
    
    
    public void addActionListener(ActionListener listener) {
        listenerList.add(ActionListener.class, listener); 
    }
    
    public void removeActionListener(ActionListener listener) {
        listenerList.remove(ActionListener.class, listener); 
    }
    
    protected void fireActionPerformed(ActionEvent event) {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                ((ActionListener) listeners[i + 1]).actionPerformed(event);
            }
        }
    }

}
