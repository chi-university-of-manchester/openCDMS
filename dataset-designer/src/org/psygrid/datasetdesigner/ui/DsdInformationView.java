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


package org.psygrid.datasetdesigner.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;

import org.psygrid.datasetdesigner.model.DsdSectionComboBoxModel;
import org.psygrid.collection.entry.ApplicationModel;
import org.psygrid.collection.entry.Fonts;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import org.psygrid.datasetdesigner.utils.PropertiesHelper;

import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.utils.ElementUtility;

/**
 * This panel contains a title, identifier label and section drop-down.
 * 
 * Slimmed down version of the CoCoA Information View
 * 
 * @author pwhelan
 */
public class DsdInformationView {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private JLabel titleLabel;

    private JLabel validationLabel;
    
    private JPanel panel;
    
    private JScrollPane pane;
    
    private JComboBox sectionComboBox;

    private DsdSectionComboBoxModel sectionComboBoxModel;
    
    private JButton manageSectionsButton;
    

    /**
     * Creates an instance of InformationView with the given <code>title</code>,
     * </code>identifierLabel</code> and <code>identifier</code>.
     */
    public DsdInformationView(String title, ApplicationModel model, final DocumentPanel docPanel) {
    	
        panel = new JPanel();
        
        pane = new JScrollPane(panel);
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        panel.setLayout(new BorderLayout());
        titleLabel = new JLabel();
        titleLabel.setOpaque(false);
        
        validationLabel = new JLabel();
        validationLabel.setOpaque(false);
        
        Font titleFont = Fonts.getInstance().getTitleFont();
        titleLabel.setFont(titleFont);
        titleLabel.setText(title);
        
        sectionComboBoxModel = new DsdSectionComboBoxModel(model);
        sectionComboBox = new JComboBox(sectionComboBoxModel);
        
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
        
        manageSectionsButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.managesections"));
       
       //disable if active ds is null, readonly or in DEL view mode
        if (DatasetController.getInstance().getActiveDs() == null
    		   || DatasetController.getInstance().getActiveDs().isReadOnly()
    		   || docPanel.isInElementViewMode()
    		   || ElementUtility.isDocumentLocked(docPanel.getDocument())
    		   ) {
    	   manageSectionsButton.setEnabled(false);
       }
        
        manageSectionsButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		docPanel.showSectionEditor();
        	}
        });
        
        panel.setBackground(Color.WHITE);
        panel.add(getContentContainer());
        panel.add(new JSeparator(), BorderLayout.SOUTH);
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
        builder.append(new JLabel(""));
        
        //unlike in CoCoA, always show the combo box  
        if (sectionComboBox != null) {
            builder.appendRelatedComponentsGapRow();
            builder.appendRow(String
                    .valueOf(sectionComboBox.getPreferredSize().height + 10));
            builder.nextLine(2);

            JPanel sectionLeftPanel = new JPanel();
            sectionLeftPanel.setOpaque(false);
            sectionLeftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            sectionLeftPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.sections")));
            sectionLeftPanel.add(sectionComboBox);
            sectionLeftPanel.add(manageSectionsButton);
            
            builder.append(sectionLeftPanel);
        }
        
        return builder.getPanel();
    }
}
