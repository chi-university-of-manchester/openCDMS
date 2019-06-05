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


package org.psygrid.collection.entry.sampletracking;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.jdic.desktop.Desktop;
import org.jdesktop.jdic.desktop.DesktopException;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.SwingWorkerExecutor;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.ui.ApplicationDialog;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.sampletracking.ConfigInfo;
import org.psygrid.data.sampletracking.ParticipantInfo;
import org.psygrid.data.sampletracking.SampleInfo;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

/**
 * 
 * Shows the list of samples for the current record in the application object.
 * 
 * @author Terry Child
 *
 */
class SampleListDialog extends ApplicationDialog {

	private static final Log LOG = LogFactory.getLog(SampleListDialog.class);

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

	private Application application;
	ConfigInfo conf = null;
	
	private DefaultFormBuilder builder;
	private JLabel participantIdentifierLabel;
	private JLabel participantIdentifier;
	private JTable sampleTable;
	private AbstractTableModel tableModel;
	private JScrollPane scrollpane;
	private JButton editParticipantButton;
	private JButton newButton;
	private JButton editButton;
	private JButton printButton;
	private JButton closeButton;	
	
	private ParticipantInfo participant = null;
	private List<SampleInfo> samples = new ArrayList<SampleInfo>();
	
	public SampleListDialog(Application application) {
		super(application, Messages.getString("SampleListDialog.dialogTitle"),true);
		this.application = application;
		Record record = application.getModel().getCurrentRecord();
		try {
			conf = RemoteManager.getInstance().getSampleConfig(record.getDataSet());
		} catch (Exception e) {
			ExceptionsHelper.handleException(this, e);
		}
		init();
		pack();
		setLocation(WindowUtils.getPointForCentering(this));
	}

	private void init() {

		builder = new DefaultFormBuilder(new FormLayout("pref,6dlu,pref:grow,6dlu,pref"),new JPanel());
		builder.setDefaultDialogBorder();
		
		participantIdentifierLabel = new JLabel(Messages.getString("SampleListDialog.participantIdentifierLabel"));
		participantIdentifierLabel.setFont(new Font("Dialog", Font.BOLD, 13));		
		participantIdentifier = new JLabel();
		editParticipantButton = new JButton(Messages.getString("SampleListDialog.edit"));
		editParticipantButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editParticipant();
			}
		});

		final String[] columnNames = Messages.getString("SampleList.colNames").split(","); 

		tableModel = new AbstractTableModel(){
			public String getColumnName(int col) { return columnNames[col];}
			public int getColumnCount() { return columnNames.length;}
			public int getRowCount() { return samples.size();}
			public Object getValueAt(int rowIndex, int columnIndex) {
				SampleInfo info = samples.get(rowIndex);
				Object result = "";
				switch(columnIndex){
				case 0: result=info.getParticipantID(); break;
				case 1: result=info.getSampleID(); break;
				case 2: result= dateFormat.format(info.getSampleDate()) ; break;
				case 3: result=info.getSampleType(); break;
				case 4: result=info.getStatus(); break;
				case 5: result=info.getTubeType(); break;
				case 6: result=info.getComment(); break;
				case 7: result=info.getTrackingID(); break;
				case 8: result=formatUserName(info.getUser()); break;
				case 9: result=info.getTimestamp(); break;
				}
				return result;
			}
		};
		
		sampleTable = new JTable(tableModel){
			public String getToolTipText(MouseEvent e) {
		        java.awt.Point p = e.getPoint();
		        int rowIndex = rowAtPoint(p);
		        int colIndex = columnAtPoint(p);
		        Object obj = getValueAt(rowIndex, colIndex);
		        return obj!=null?obj.toString():"";
		    }
		};
		sampleTable.setPreferredScrollableViewportSize(new Dimension(700, 150));
		scrollpane = new JScrollPane(sampleTable);	
		newButton = new JButton(Messages.getString("SampleListDialog.new"));
		editButton = new JButton(Messages.getString("SampleListDialog.edit"));
		printButton = new JButton(Messages.getString("SampleListDialog.print"));
		closeButton = new JButton(Messages.getString("SampleListDialog.close"));

		sampleTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				updateButtons();
			}			
		});
		
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newSample();
			}
		});

		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editSample();
			}
		});

		sampleTable.addMouseListener(new MouseAdapter(){
		    public void mouseClicked(MouseEvent e) {
		    	if(e.getClickCount()==2){
		    		editSample();
		    	}
		    }
		});

		printButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				print();
			}
		});

		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		builder.append(participantIdentifierLabel);
		builder.append(participantIdentifier);
		if(!conf.isAutoParticipantID()){
			builder.append(editParticipantButton);
		}
		builder.nextLine();

		builder.append(scrollpane,builder.getColumnCount());
		builder.appendUnrelatedComponentsGapRow();
		builder.nextLine(2);
		JPanel buttonsPanel = ButtonBarFactory.buildRightAlignedBar(newButton,editButton,printButton,closeButton);
		builder.append(buttonsPanel, builder.getColumnCount());
		getContentPane().add(builder.getPanel());		
	}
	
	public boolean doModal(){
		final Record record = application.getModel().getCurrentRecord();
		String identifier = record.getIdentifier().getIdentifier();
		try {
			participant = RemoteManager.getInstance().getSampleParticipant(record);
			if(participant==null){				
				participant = new ParticipantInfo(record.getIdentifier().getIdentifier(),record.getDataSet().getProjectCode(),"");
				if(conf.isAutoParticipantID()){
					if(conf.isUsingExternalID()){
						String externalID = PersistenceManager.getInstance().getExternalIdMap().get(identifier);
						if(externalID!=null) participant.setIdentifier(externalID);
						else return false;
					}
					else {
						participant.setIdentifier(identifier);
					}
				}
				else {
					ParticipantDialog dlg = new ParticipantDialog(conf,participant,application);
					if(!dlg.doModal()){
						return false;
					}
				}
				RemoteManager.getInstance().saveSampleParticipant(record,participant);
			}
		} catch (Exception e) {
			ExceptionsHelper.handleException(this, e);
		}
		participantIdentifier.setText(participant.getIdentifier());
		refresh();
		setVisible(true);
		return true;
	}

	
	private void updateButtons(){
		int row = sampleTable.getSelectedRow();
		boolean enable = row>=0 && row<samples.size();
		editButton.setEnabled(enable);
		printButton.setEnabled(enable);
	}
	
	private void refresh(){

		final Record record = application.getModel().getCurrentRecord();

		SwingWorker<SampleInfo[], Void> worker = new SwingWorker<SampleInfo[], Void>() {

			protected SampleInfo[] doInBackground() throws Exception {
				SampleInfo[] info = RemoteManager.getInstance().getSamples(record);
				return info;
			}

			protected void done() {
				try {
					SampleInfo[] info = get();
					samples.clear();
					samples.addAll(Arrays.asList(info));
					tableModel.fireTableDataChanged();
					updateButtons();
				} catch (InterruptedException e) {
					ExceptionsHelper.handleInterruptedException(e);
				} catch (ExecutionException e) {
					ExceptionsHelper.handleException(SampleListDialog.this,e.getCause());
				}
				finally{
					setWait(false);					
				}
			}
		};
		setWait(true);
		SwingWorkerExecutor.getInstance().execute(worker);		
	}
	
	private void newSample(){
		try {
			Record record = application.getModel().getCurrentRecord();
			String identifier = record.getIdentifier().getIdentifier();
			String participantID=participant.getIdentifier();
			String sampleID="";
			if(conf.isAutoSampleID()){
				long sampleNumber = RemoteManager.getInstance().getNextSampleNumber(record.getDataSet());
				sampleID=participantID+conf.getSeparator()+sampleNumber;
			}
			SampleInfo sample = new SampleInfo(identifier,participantID,sampleID,conf.getStatuses()[0],
					conf.getSampleTypes()[0],conf.getTubeTypes()[0],"",new Date(),"");
			SampleDialog dlg = new SampleDialog(conf,sample,null,application);
			if(dlg.doModal()){
				RemoteManager.getInstance().saveSample(record,sample);
			}
			refresh();
		}  catch (Exception e) {
			ExceptionsHelper.handleException(this,e);
		}
	}

	private void editSample(){
		int row = sampleTable.getSelectedRow();
		if(row>=0 && row<samples.size()){
			try {
				Record record = application.getModel().getCurrentRecord();
				ConfigInfo conf = RemoteManager.getInstance().getSampleConfig(record.getDataSet());
				SampleInfo sample = samples.get(row);
				SampleInfo[] revisions = RemoteManager.getInstance().getSampleRevisions(record, sample.getID());
				SampleDialog dlg = new SampleDialog(conf,sample,revisions,application);
				if(dlg.doModal()){
					RemoteManager.getInstance().saveSample(record,sample);
				}
			} catch (Exception e) {
				ExceptionsHelper.handleException(this,e);
			}
			refresh();
		}
	}
	
	public void print(){
		int row = sampleTable.getSelectedRow();
		if(row==-1 || row>=samples.size()) return;
		SampleInfo sample = samples.get(row);
		File file = null;
		OutputStream out = null;
		try{
			Record record = application.getModel().getCurrentRecord();
			ConfigInfo conf = RemoteManager.getInstance().getSampleConfig(record.getDataSet());
			file = File.createTempFile("Label", ".pdf");
			out = new BufferedOutputStream(new FileOutputStream(file));
			SamplePrinter.print(conf,sample, out);
			out.flush();
		} catch (Exception e) {
			ExceptionsHelper.handleException(this,e);
		}
		finally{
				if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LOG.warn("Failed to close stream.", e);
				}
			}
		}
		try {
			Desktop.open(file);
		} catch (DesktopException e) {
			LOG.warn("Unable to launch pdf viewer for sample label",e);
		}
	}

	/**
	 * Extract a name from the given DN string.
	 * e.g CN=CRO One, OU=users, O=psygrid, C=uk
	 * would return CRO One.
	 * 
	 * @param cnName
	 * @return name
	 */
	private String formatUserName(String dnName) {

		if (dnName == null || dnName.equals("")) {
			return dnName;
		}
		try {
			String[] a = dnName.split(",", 2);
			String[] b = a[0].split("=", 2);
			return b[1];
		}
		catch (Exception e) {
			return dnName;
		}
	}
	
	private void editParticipant() {
		ParticipantDialog dlg = new ParticipantDialog(conf,participant,application);
		if(dlg.doModal()){
			participantIdentifier.setText(participant.getIdentifier());
			Record record = application.getModel().getCurrentRecord();
			try{
				RemoteManager.getInstance().saveSampleParticipant(record,participant);
			} catch (Exception e) {
				ExceptionsHelper.handleException(this,e);
			}
		}
	}

}

