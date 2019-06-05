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


package org.psygrid.collection.entry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;

import org.psygrid.collection.entry.event.ChangeVetoedEvent;
import org.psygrid.collection.entry.event.ChangeVetoedListener;
import org.psygrid.collection.entry.event.SectionChangedEvent;
import org.psygrid.collection.entry.event.SectionEvent;
import org.psygrid.collection.entry.event.SectionListener;
import org.psygrid.collection.entry.model.SectionComboBoxModel;
import org.psygrid.collection.entry.remote.ChangeApprovedDocToPendingWorker;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.psygrid.data.model.hibernate.Status;

/**
 * This panel contains a title, identifier label and section drop-down.
 * 
 * The information label updates itself automatically to display help
 * information associated with the JComponent that has focus.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 */
public class InformationView {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private JLabel titleLabel;

    private JLabel historyLabel;
    
    private JLabel validationLabel;
    
    private JLabel dataReplicationLabel;
    
    private JPanel panel;
    
    private JScrollPane pane;
    
    private Status status;
    
    private Application application;
    
    private JComboBox sectionComboBox;

    private JToolBar sectionToolBar;

    private SectionComboBoxModel sectionComboBoxModel;
    
    private SectionListener sectionListener;

    /**
     * Creates an instance of InformationView with the given <code>title</code>,
     * </code>identifierLabel</code> and <code>identifier</code>.
     */
    public InformationView(String title, Status status,
            final Application application, String history, String primaryIdentifier) {
    	
    	this.status = status;
    	this.application = application;
    	
        panel = new JPanel();
        pane = new JScrollPane(panel);
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        panel.setLayout(new BorderLayout());
        titleLabel = new JLabel();
        titleLabel.setOpaque(false);
        
        validationLabel = new JLabel();
        validationLabel.setOpaque(false);
        
        if ( null != history ){
	        historyLabel = new JLabel(EntryMessages.getString("InformationView.historyLabel")+history);
	        historyLabel.setOpaque(false);
        }
        
        Font titleFont = Fonts.getInstance().getTitleFont();
        titleLabel.setFont(titleFont);
        titleLabel.setText(title);
        
        if ( null != primaryIdentifier ){
        	dataReplicationLabel = new JLabel(EntryMessages.getString("InformationView.dataReplicationText")+primaryIdentifier);
        	dataReplicationLabel.setOpaque(false);
        	dataReplicationLabel.setFont(Fonts.getInstance().getBoldLabelFont());
        }
        
        sectionComboBoxModel = new SectionComboBoxModel(application.getModel());
        sectionComboBox = new JComboBox(sectionComboBoxModel);
        sectionComboBoxModel.addChangeVetoedListener(new ChangeVetoedListener() {
            public void changeVetoed(ChangeVetoedEvent event) {
                JOptionPane.showMessageDialog(application,
                        "The selected section transition is not yet enabled.");
            }
        });
        
        /* 
         * We delegate to the look and feel renderer and customise it instead
         * of replacing it.
         */
        final ListCellRenderer renderer = sectionComboBox.getRenderer();
        sectionComboBox.setRenderer(new ListCellRenderer() {
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                Component comp = renderer.getListCellRendererComponent(list, value,
                        index, isSelected, cellHasFocus);
                comp.setEnabled(sectionComboBoxModel.isEnabled(value));
                return comp;
            }
        });
        
        sectionToolBar = createSecOccInstToolBar();

        panel.add(createToolBar(), BorderLayout.NORTH);
        panel.setBackground(Color.WHITE);
        panel.add(getContentContainer());
        panel.add(new JSeparator(), BorderLayout.SOUTH);
        
        sectionToolBar.setVisible(sectionComboBoxModel.isShowToolbarOnOpen());
        sectionListener = new SectionListener(){

			public void sectionAdded(SectionEvent event) {
				//Do nothing
			}

			public void sectionChanged(SectionChangedEvent event) {
				if ( null != event.getCurrentSectionOccPresModel().getSecOccInstance() ){
					sectionToolBar.setVisible(true);
					if ( application.getModel().isSecOccInstActionsActive() ){
						if ( application.getModel().getCurrentSecOccInstCount() <= 1 ){
							application.getModel().getRemoveSecOccInstAction().setEnabled(false);
						}
						else{
							application.getModel().getRemoveSecOccInstAction().setEnabled(true);
						}
					}
				}
				else{
					sectionToolBar.setVisible(false);
				}
			}

			public void sectionRemoved(SectionEvent event) {
				//Do nothing
			}
        	
        };
        application.getModel().addSectionListener(sectionListener);
        
    }
    
    private JToolBar createToolBar() {
        JToolBar bar = new JToolBar();
        bar.setOpaque(false);
        bar.setFloatable(false);
        bar.setRollover(true);
        ApplicationModel model = application.getModel();
        bar.add(model.getBackAction());
        bar.add(model.getForwardAction());
        bar.addSeparator();
        bar.add(application.getSaveIncompleteDocumentAction());
        bar.add(model.getPrintAction());
        bar.add(model.getApplyStdCodeAction());
        bar.add(model.getApplyImportAction());
        bar.addSeparator();
        bar.add(model.getCloseAction());
        return bar;
    }

    private JToolBar createSecOccInstToolBar() {
        JToolBar bar = new JToolBar();
        bar.setOpaque(false);
        bar.setFloatable(false);
        bar.setRollover(true);
        ApplicationModel model = application.getModel();
        bar.add(model.getInsertBeforeSecOccInstAction());
        bar.add(model.getInsertAfterSecOccInstAction());
        bar.add(model.getRemoveSecOccInstAction());
        return bar;
    }

   public void dispose() {
        sectionComboBoxModel.dispose();
    }

    public JScrollPane getPane() {
        return pane;
    }
    
    public void setValidationMessage(ImageIcon icon, String message) {
        String htmlMessage = insertHtmlBreaks(message);
        validationLabel.setToolTipText(htmlMessage);
        validationLabel.setIcon(icon);
    }
    
    JLabel getValidationLabel() {
        return validationLabel;
    }
    
    private String insertHtmlBreaks(String message) {
        if (message == null) {
            return null;
        }
        String htmlMessage = message;
        htmlMessage = htmlMessage.replace("\n", "<br>"); //$NON-NLS-1$ //$NON-NLS-2$
        htmlMessage = "<html>" + htmlMessage + "</html>"; //$NON-NLS-1$ //$NON-NLS-2$
        return htmlMessage;
    }
   
    /**
     * Creates the panel that contains the title and information label and lays
     * out the components in it.
     * 
     * @return correctly laid out JPanel.
     */
    private JPanel getContentContainer() {
        JPanel containerPanel = new JPanel();
        containerPanel.setBackground(Color.WHITE);
        DefaultFormBuilder builder = new DefaultFormBuilder(
                new FormLayout("default, 2dlu, default:grow"), containerPanel); //$NON-NLS-1$
        builder.setBorder(BorderFactory.createEmptyBorder(0, 7, 7, 7));
        builder.append(titleLabel);
        builder.append(validationLabel);
        if ( null != historyLabel){
	        builder.appendRelatedComponentsGapRow();
	        builder.appendRow(String.valueOf(historyLabel.getPreferredSize().height));
	        builder.nextLine(2);
	        builder.append(historyLabel, builder.getColumnCount());
        }
        
        if (sectionComboBoxModel.isShowSectionCombo()) {
            builder.appendRelatedComponentsGapRow();
            builder.appendRow(String
                    .valueOf(sectionToolBar.getPreferredSize().height));
            builder.nextLine(2);

            DefaultFormBuilder sectionBuilder = new DefaultFormBuilder(
                    new FormLayout("default, 2dlu, default, 2dlu, default"), new JPanel()); //$NON-NLS-1$
            sectionBuilder.getPanel().setOpaque(false);
            sectionBuilder.append(new JLabel(EntryMessages
                    .getString("SectionOccurrencesView.sections"))); //$NON-NLS-1$
            sectionBuilder.append(sectionComboBox);
            sectionBuilder.append(sectionToolBar);
            builder.append(sectionBuilder.getPanel(), builder.getColumnCount());
        }
        if ( null != dataReplicationLabel ){
	        builder.appendRelatedComponentsGapRow();
	        builder.appendRow(String.valueOf(dataReplicationLabel.getPreferredSize().height));
	        builder.nextLine(2);
	        builder.append(dataReplicationLabel, builder.getColumnCount());
        }
        if ( null != status && status.getShortName().equals(DocumentStatus.APPROVED.toString()) && null == dataReplicationLabel ){
        	//Add button to move back to pending
        	JButton button = new JButton(EntryMessages.getString("InformationView.moveApprovedToPendingButton"));
        	button.addActionListener(
        			new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							ChangeApprovedDocToPendingWorker worker = new ChangeApprovedDocToPendingWorker(application);
							SwingWorkerExecutor.getInstance().execute(worker);
						}        				
        			});
        	JPanel buttonPanel = new JPanel();
        	buttonPanel.setBackground(Color.WHITE);
            FormLayout buttonLayout = new FormLayout("default, 2dlu, default:grow"); //$NON-NLS-1$
        	DefaultFormBuilder buttonBuilder = new DefaultFormBuilder(buttonLayout,
        			buttonPanel);
        	buttonBuilder.append(button);
        	
	        builder.appendRelatedComponentsGapRow();
	        builder.appendRow(String.valueOf(buttonPanel.getPreferredSize().height));
	        builder.nextLine(2);
	        builder.append(buttonPanel);
        	
        }
        return builder.getPanel();
    }
    
    
}
