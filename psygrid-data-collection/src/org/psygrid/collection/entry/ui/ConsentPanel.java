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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.psygrid.collection.entry.event.ConsentEvent;
import org.psygrid.collection.entry.event.ConsentListener;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.FormLayout;
import org.psygrid.data.model.hibernate.*;

public class ConsentPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Record record;
	private DefaultFormBuilder builder;
	private Map<ConsentForm, Boolean> consentMap;
	private BasicDatePicker schStartDatePicker;
	private BasicDatePicker studyEntryDatePicker;
	private JLabel schStartDateVal;
	private JLabel studyEntryDateVal;

	public ConsentPanel(Record record) {
		this.record = record;
		init();
	}

	private void init() {
		consentMap = new HashMap<ConsentForm, Boolean>();

		builder = new DefaultFormBuilder(new FormLayout("default, 1dlu, 100dlu, default:grow"), this); //$NON-NLS-1$
		builder.setDefaultDialogBorder();

		DataSet ds = record.getDataSet();
		for ( int i=0; i<ds.numAllConsentFormGroups(); i++ ){
			if ( i>0 ){
				builder.appendUnrelatedComponentsGapRow();
				builder.nextLine(2);                
			}
			ConsentFormGroup cfg = ds.getAllConsentFormGroup(i);
			JLabel cfgLabel = new JLabel(cfg.getDescription());
			Font f = cfgLabel.getFont();
			cfgLabel.setFont(new Font(f.getName(), Font.BOLD, f.getSize()));
			builder.append(cfgLabel, builder.getColumnCount());
			builder.appendRow(FormFactory.DEFAULT_ROWSPEC);
			builder.nextLine();
			for ( int j=0; j<cfg.numConsentForms(); j++ ){
				final PrimaryConsentForm pcf = cfg.getConsentForm(j);
				consentMap.put(pcf, new Boolean(false));
				if ( j>0 ){
					JLabel orLabel = new JLabel(Messages.getString("ConsentPanel.orLabel"));
					builder.append(orLabel, builder.getColumnCount());
					builder.appendRow(FormFactory.DEFAULT_ROWSPEC);
					builder.nextLine();
				}
				JCheckBox pcfCheckBox = new JCheckBox();
				pcfCheckBox.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e){
						changeConsent(pcf, ((JCheckBox)e.getSource()).isSelected());
					}
				});
				EntryLabel pcfLabel = new EntryLabel(pcf.getQuestion());
				builder.append(pcfCheckBox);
				builder.append(pcfLabel, 2);
				builder.appendRow(FormFactory.DEFAULT_ROWSPEC);
				builder.nextLine();
				for ( int k=0; k<pcf.numAssociatedConsentForms(); k++ ){
					final AssociatedConsentForm acf = pcf.getAssociatedConsentForm(k);
					consentMap.put(acf, new Boolean(false));
					JCheckBox acfCheckBox = new JCheckBox();
					acfCheckBox.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e){
							changeConsent(acf, ((JCheckBox)e.getSource()).isSelected());
						}
					});
					EntryLabel acfLabel = new EntryLabel(acf.getQuestion());                    
					builder.append(acfCheckBox);
					builder.append(acfLabel, 2);
					builder.appendRow(FormFactory.DEFAULT_ROWSPEC);
					builder.nextLine();
				}
			}
		}

	}

	public JPanel getStartDatePanel() {
		JPanel entryDates = new JPanel();

		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("default, 1dlu, 100dlu, default:grow"), entryDates); //$NON-NLS-1$
		builder.setBorder(Borders.createEmptyBorder("4dlu, 7dlu, 4dlu, 7dlu"));
		
		//date of entry into study
		builder.appendSeparator();
		JLabel studyEntryLabel = new JLabel(Messages.getString("ConsentPanel.studyEntryDateLabel"));
		studyEntryDatePicker = new BasicDatePicker(System.currentTimeMillis());        
		builder.append(studyEntryLabel, builder.getColumnCount());
		
		studyEntryDateVal = new JLabel("  ");
		builder.append(studyEntryDateVal);
		builder.append(studyEntryDatePicker);
		builder.appendUnrelatedComponentsGapRow();
		builder.nextLine(2);

		//schedule start date question
		String question = record.getDataSet().getScheduleStartQuestion();
		if ( null != question ){
			JLabel quLabel = new JLabel(question);
			schStartDatePicker = new BasicDatePicker(System.currentTimeMillis());
			builder.append(quLabel, builder.getColumnCount());
			schStartDateVal = new JLabel();
			builder.append(schStartDateVal);
			builder.append(schStartDatePicker);
			builder.nextLine(2);
		}
		return entryDates;
	}

	public void addConsentListener(ConsentListener listener) {
		listenerList.add(ConsentListener.class, listener);
	}

	public void removeConsentListener(ConsentListener listener) {
		listenerList.remove(ConsentListener.class, listener);
	}

	protected void fireConsentEvent(ConsentEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ConsentListener.class) {
				((ConsentListener) listeners[i + 1]).consentChosen(event);
			}
		}
	}

	public void setConsent(){
		//validate
		if ( !DateValidationHelper.validateDate(studyEntryDatePicker.getDate(), studyEntryDateVal, this) ){
			return;
		}
		if ( null != schStartDatePicker ){
			if ( !DateValidationHelper.validateDate(schStartDatePicker.getDate(), schStartDateVal, this) ){
				return;
			}
		}

		RecordData rd = record.generateRecordData();
		rd.setStudyEntryDate(studyEntryDatePicker.getDate());
		if ( null != schStartDatePicker ){
			rd.setScheduleStartDate(schStartDatePicker.getDate());
		}
		record.setRecordData(rd, null);

		for ( Map.Entry<ConsentForm, Boolean> entry: this.consentMap.entrySet() ){
			if ( entry.getValue().booleanValue() ){
				Consent c = entry.getKey().generateConsent();
				c.setConsentGiven(entry.getValue().booleanValue());
				record.addConsent(c);
			}
		}
		fireConsentEvent(new ConsentEvent(this, true));
	}

	private void changeConsent(ConsentForm consentForm, boolean consentGiven){
		consentMap.put(consentForm, new Boolean(consentGiven));
	}

}
