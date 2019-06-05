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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.ui.ApplicationDialog;
import org.psygrid.data.sampletracking.ConfigInfo;
import org.psygrid.data.sampletracking.SampleInfo;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

/**
 * 
 * Allows editing of sample information.
 * 
 * @author Terry Child
 *
 */
class SampleDialog extends ApplicationDialog {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
	
	Application application;
	
	private ConfigInfo config;
	private SampleInfo sample;
	
	private boolean result = false;
	
	private DefaultFormBuilder builder;

	private JLabel participantIdentifierLabel;
	private JLabel participantIdentifier;
	private JTextField sampleIdentifier1;
	private JTextField sampleIdentifier2;
	private JXDatePicker datePicker;	
	private JComboBox type;
	private JComboBox tube;
	private JComboBox status;
	private JTextArea comment;
	JScrollPane commentScroll;
	private JTextField trackingID;
	private JCheckBox historyCheckBox;
	
	private JButton saveButton;
	private JButton closeButton;

	// Previous history of this sample
	private JTable sampleTable;
	private JScrollPane scrollpane;
	private AbstractTableModel tableModel;
	private List<SampleInfo> samples = new ArrayList<SampleInfo>();

	public SampleDialog(ConfigInfo config,SampleInfo sample,SampleInfo[] revisions,Application application)   {
		super(application, Messages.getString("SampleDialog.dialogTitle"), true);
		this.application=application;
		this.config=config;
		this.sample=sample;
		if(revisions!=null) samples.addAll(Arrays.asList(revisions));
		init();
		pack();
		setLocation(WindowUtils.getPointForCentering(this));
	}

	private void init(){
		builder = new DefaultFormBuilder(new FormLayout("pref, 6dlu, 150dlu:grow"),new JPanel());
		builder.setDefaultDialogBorder();
		
		
		participantIdentifierLabel = new JLabel(Messages.getString("SampleDialog.participantIdentifierLabel"));
		participantIdentifierLabel.setFont(new Font("Dialog", Font.BOLD, 13));		
		participantIdentifier = new JLabel();

		sampleIdentifier1 = new JTextField();
		sampleIdentifier1.setEditable(!config.isAutoSampleID());
		sampleIdentifier2 = new JTextField();
		
		datePicker = new JXDatePicker();
		datePicker.setFormats(new DateFormat[]{dateFormat});
		
		type = new JComboBox(config.getSampleTypes());
		tube = new JComboBox(config.getTubeTypes());
		status = new JComboBox(config.getStatuses());
		comment = new JTextArea(new PlainDocument() {
			  // Limit the length of comments to 255 chars
			  public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
			    if (str == null)
			      return;
			    if ((getLength() + str.length()) <= 255) {
			      super.insertString(offset, str, attr);
			    }
			    else {
			    	Toolkit.getDefaultToolkit().beep();
			    }
			  }
			});
		comment.setRows(4);
		comment.setWrapStyleWord(true);
		commentScroll = new JScrollPane(comment, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		trackingID = new JTextField();
		
		historyCheckBox = new JCheckBox(Messages.getString("SampleDialog.showHistory"));
		saveButton = new JButton(EntryMessages.getString("Entry.save"));
		closeButton = new JButton(EntryMessages.getString("Entry.close"));
		
		historyCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scrollpane.setVisible(!scrollpane.isVisible());
				pack();
				setLocation(WindowUtils.getPointForCentering(SampleDialog.this));
			}
		});
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
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
		        return getValueAt(rowIndex, colIndex).toString();
		    }
		};
		
		sampleTable.setPreferredScrollableViewportSize(new Dimension(500, 100));
		scrollpane = new JScrollPane(sampleTable);
		scrollpane.setVisible(false);
		
		// Build the form
		builder.append(participantIdentifierLabel);
		builder.append(participantIdentifier);
		builder.nextLine();
				
		builder.append(Messages.getString("SampleDialog.sampleIdentifier1Label"),sampleIdentifier1);
		builder.nextLine();
		
		if(!config.isAutoSampleID()){
			builder.append(Messages.getString("SampleDialog.sampleIdentifier2Label"),sampleIdentifier2);
			builder.nextLine();
		}
		
		builder.append(Messages.getString("SampleDialog.sampleDateLabel"),datePicker);
		builder.nextLine();

		builder.append(Messages.getString("SampleDialog.typeLabel"),type);
		builder.nextLine();

		builder.append(Messages.getString("SampleDialog.tubeLabel"),tube);
		builder.nextLine();
		
		builder.append(Messages.getString("SampleDialog.statusLabel"),status);
		builder.nextLine();

		builder.append(Messages.getString("SampleDialog.commentLabel"),commentScroll);
		builder.nextLine();

		builder.append(Messages.getString("SampleDialog.trackingIDLabel"),trackingID);
		builder.nextLine();
		
		builder.append(historyCheckBox);
		builder.nextLine();
		
		builder.append(scrollpane,builder.getColumnCount());
		builder.nextLine();
		
		JPanel buttonsPanel = ButtonBarFactory.buildRightAlignedBar(saveButton,closeButton);
		builder.append(buttonsPanel, builder.getColumnCount());

		getContentPane().add(builder.getPanel());
	}

	public boolean doModal(){
		participantIdentifier.setText(sample.getParticipantID());
		sampleIdentifier1.setText(sample.getSampleID());
		sampleIdentifier2.setText(sample.getSampleID());
		datePicker.setDate(sample.getSampleDate());
		type.setSelectedItem(sample.getSampleType());
		tube.setSelectedItem(sample.getTubeType());
		status.setSelectedItem(sample.getStatus());
		comment.setText(sample.getComment());
		trackingID.setText(sample.getTrackingID());
		setVisible(true);
		return result;
	}
	
	private void save() {
		if(!sampleIdentifier1.getText().equals(sampleIdentifier2.getText())){
			String title = Messages.getString("SampleDialog.sampleIdentifierMismatchTitle");
			String message = Messages.getString("SampleDialog.sampleIdentifierMismatch");
			JOptionPane.showMessageDialog(getParent(), message, title,JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		// Check the label against a regex
		String regex = config.getSampleRegex();
		if(regex!=null && regex.length()>0 && !sampleIdentifier1.getText().matches(regex)){
			String title = Messages.getString("SampleDialog.sampleRegexTitle");
			String message = null;
			if(config.getSampleRegexDescription()!=null && config.getSampleRegexDescription().length()>0){
				message = config.getSampleRegexDescription();
			}
			else {
				message = Messages.getString("SampleDialog.sampleRegexMessage")+"'"+regex+"'";
			}
			JOptionPane.showMessageDialog(getParent(), message, title,JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		sample.setSampleID(sampleIdentifier1.getText());
		sample.setSampleDate(datePicker.getDate());
		sample.setSampleType((String)type.getSelectedItem());
		sample.setTubeType((String)tube.getSelectedItem());
		sample.setStatus((String) status.getSelectedItem());
		sample.setComment(comment.getText());
		sample.setTrackingID(trackingID.getText());
		result = true;
		dispose();		
	}

	private void close() {
		result = false;
		dispose();
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

}
