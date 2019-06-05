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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.CommitExceptionHandler;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.event.RecordSelectedEvent;
import org.psygrid.collection.entry.event.RecordSelectedListener;
import org.psygrid.collection.entry.persistence.RecordsListWrapper;
import org.psygrid.collection.entry.persistence.RecordsList.Item;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

public class RecordDialog extends JDialog implements ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private JXTable table;
    private DefaultFormBuilder builder;

    private JButton okButton;

    private JButton cancelButton;
    
    private JButton handleExceptionsButton;

    protected final EventListenerList listenerList = new EventListenerList();

    private JScrollPane tableScrollPane;

    private JLabel messageLabel;
    
    private Map<RecordsListWrapper.Item, Exception> exceptionsMap = null;
    
    private boolean resultDisplayMode = false; //Whether this is a selection dialog or whether action results are being displayed.
    
    private Application application = null;
    
    private boolean cancelled = false;
    
    public RecordDialog(JFrame application, AbstractTableModel tableModel, String message, boolean resultDisplayMode) {
        this(application, Messages.getString("RecordDialog.dialogTitle"), tableModel, message, resultDisplayMode);
        
    }
    
    public RecordDialog(JFrame application, String title, AbstractTableModel tableModel,
            String message, boolean resultDisplayMode) {
        super(application, title, true);
        this.resultDisplayMode = resultDisplayMode;
        if (application instanceof Application) {
        	this.application = (Application)application;
        }
        initBuilder();
        initComponents(tableModel, message);
        initEventHandling();
        build();
        
        pack();
        setLocation(WindowUtils.getPointForCentering(this));
    }
    
    private void initEventHandling() {
    	
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(!isResultDisplayMode()){
                	handleRecordSelected(e);
            	}else{
            		//If in ResultDisplayMode, the OK button does noting more than to
            		//dismiss the dialog.
            		dispose();
            	}

            }
        });
    	
    	if(!this.resultDisplayMode){
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	cancelled = true;
                dispose();
            }
        });

    	}else{
    		handleExceptionsButton.addActionListener(this);
    	}
    	
        // Safe not to release listener
    	if(!this.resultDisplayMode){
	        table.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseReleased(MouseEvent e) {
	                int row = table.rowAtPoint(e.getPoint());
	                if (row < 0) {
	                    return;
	                }
	                if (e.getClickCount() == 2
	                        && e.getButton() == MouseEvent.BUTTON1) {
	                    handleRecordSelected(row);
	                }
	            }
	        });
    	}
        
    }

    protected void handleRecordSelected(ActionEvent e) {
        int rowIndex = table.getSelectedRow();
        if (rowIndex < 0) {
            String title = Messages.getString("RecordDialog.noRecordSelectedTitle");
            String message = Messages.getString("RecordDialog.noRecordSelectedMessage");
            JOptionPane.showMessageDialog(this, message, title, 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        handleRecordSelected(rowIndex);
    }
    
    private void handleRecordSelected(int rowIndex) {
        RecordsTableModel tableModel = (RecordsTableModel) table.getModel();
        String identifier = tableModel.getRecordValueAtRow(rowIndex);
        dispose();
        fireRecordSelected(new RecordSelectedEvent(this, identifier));
    }

    private void initComponents(AbstractTableModel tableModel, String message) {
        messageLabel = new JLabel(message);
        table = new JXTable(tableModel);
        table.setSortable(false);
        tableScrollPane = new JScrollPane(table);
        Border currentBorder = tableScrollPane.getBorder();
        tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 2, 2, 2), currentBorder));
        initRecordsTable();
        
        okButton = new JButton(EntryMessages.getString("Entry.ok")); //$NON-NLS-1$
        if(!this.resultDisplayMode){
            cancelButton = new JButton(EntryMessages.getString("Entry.cancel")); //$NON-NLS-1$
        }else{
        	handleExceptionsButton = new JButton(EntryMessages.getString("Entry.handleExceptions"));
        }
  
    }
    
    private void initRecordsTable() {
        table.setPreferredScrollableViewportSize(new Dimension(500, 280));
        table.setRowSelectionAllowed(true);
        table.setShowGrid(false);
        table.setColumnMargin(0);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initBuilder() {
        builder = new DefaultFormBuilder(new FormLayout("default"), new JPanel()); //$NON-NLS-1$
        builder.setDefaultDialogBorder();
    }
    
    private void build() {
        builder.append(messageLabel);
        builder.append(tableScrollPane);
        
        builder.appendUnrelatedComponentsGapRow();
        builder.nextLine(2);
        
        JPanel buttonsPanel;
        if(!this.resultDisplayMode)
        	buttonsPanel = ButtonBarFactory.buildOKCancelBar(okButton, 
                cancelButton);
        else{
        	buttonsPanel = ButtonBarFactory.buildCenteredBar(handleExceptionsButton, okButton);
        	okButton.setEnabled(false);
        }
        builder.append(buttonsPanel);
        
        getContentPane().add(builder.getPanel());
    }

    public void addRecordSelectedListener(RecordSelectedListener listener) {
        listenerList.add(RecordSelectedListener.class, listener); 
    }
    
    public void removeRecordSelectedListener(RecordSelectedListener listener) {
        listenerList.remove(RecordSelectedListener.class, listener); 
    }
    
    protected void fireRecordSelected(RecordSelectedEvent event) {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == RecordSelectedListener.class) {
                ((RecordSelectedListener) listeners[i + 1])
                        .recordSelected(event);
            }
        }
    }
    
    public final JXTable getTable() {
        return table;
    }
    
    /**
     * Sets the exceptions map for the object. It is necessary to call this prior making the dialog visible 
     * if 'resultDisplayMode' = true.
     * @param exceptionsMap
     */
	public void setExceptionsMap(Map<RecordsListWrapper.Item, Exception> exceptionsMap) {
		this.exceptionsMap = exceptionsMap;
	}

	
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == handleExceptionsButton){
			okButton.setEnabled(true);
			handleExceptionsButton.setEnabled(false);
			CommitExceptionHandler exHan = new CommitExceptionHandler(application);
			exHan.handleExceptions(this, exceptionsMap);
		}
		
	}
	
	
	public boolean isResultDisplayMode() {
		return resultDisplayMode;
	}

	public boolean isCancelled() {
		return cancelled;
	}

}
