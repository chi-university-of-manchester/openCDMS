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


package org.psygrid.collection.entry.chooser;

import java.awt.BorderLayout;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.chooser.ChooserPanel.CancelActionEvent;
import org.psygrid.collection.entry.chooser.ChooserPanel.FinishedLoadingEvent;
import org.psygrid.collection.entry.event.JobEvent;
import org.psygrid.collection.entry.event.JobListener;
import org.psygrid.collection.entry.ui.ApplicationDialog;
import org.psygrid.data.model.hibernate.Record;


public abstract class ChooserDialog extends ApplicationDialog    {

    private static final long serialVersionUID = 1L;
    private static final Log LOG = LogFactory.getLog(ChooserDialog.class);
    
    private ChooserPanel mainPanel;

    protected Application application;
    
    /**
     * Chreate a document chooser dialog window.
     * 
     * @param parent
     * @param choosableList
     * @param chooserModel
     */
    public ChooserDialog(final Application parent, 
            ChoosableList choosableList, ChooserModel chooserModel) {
        super(parent, Messages.getString("ChooserDialog.title"), true); //$NON-NLS-1$
        this.application = parent;
        init(choosableList, chooserModel);
    }
    
    /**
     * Create a document chooser dialog window, with the specified title.
     * 
     * @param parent
     * @param choosableList
     * @param chooserModel
     * @param title
     */
    public ChooserDialog(final Application parent, 
            ChoosableList choosableList, ChooserModel chooserModel, String title) {
        super(parent, title, true); 
        this.application = parent;
        init(choosableList, chooserModel);
    }
    
    private void init(ChoosableList choosableList, ChooserModel chooserModel) {
        setLayout(new BorderLayout());
        mainPanel = createChooserPanel();
        
        mainPanel.addJobListener(new JobListener() {
            public void jobStarted(JobEvent event) {
                setWait(true);
            }

            public void jobFinished(JobEvent event) {
                setWait(false);
            }
        });
        
        // Safe not to remove listener
        mainPanel.addFinishedLoadingListener(new ChooserPanel.FinishedLoadingListener() {
           public void finishedLoading(FinishedLoadingEvent event) {
               pack();
               setLocation(WindowUtils.getPointForCentering(ChooserDialog.this));
               setVisible(true);
               if (event.getChoosableException() != null) {
                   handleException(event.getChoosableException());
               }
            }
        });
        
        mainPanel.init(choosableList, chooserModel);
        
        addChooserSelectedListener();
        
        getContentPane().add(mainPanel);
        
        // Safe not to remove listener
        mainPanel.addCancelActionListener(new ChooserPanel.CancelActionListener() {
            public void cancelAction(CancelActionEvent event) {
                dispose();
            }
        });
    }
    
    private void handleException(ChoosableException choosableException) {
        if (LOG.isErrorEnabled()) {
            LOG.error(choosableException.getMessage(), choosableException);
        }
        String title = Messages.getString("ChooserDialog.loadingDocsErrorMessageTitle");
        String message = Messages.getString("ChooserDialog.loadingDocsErrorMessage");
        JOptionPane.showMessageDialog(ChooserDialog.this, message, title,
                JOptionPane.ERROR_MESSAGE);
        dispose();
    } 
    
    /**
     * Adds a listener that is invoked when a Choosable that can be loaded
     * into Application is selected. The type of Choosable is defined by
     * subclasses.
     *
     */
    protected abstract void addChooserSelectedListener();
    
    protected abstract ChooserPanel createChooserPanel();
    
    /**
     * Provide an action that will occur when a record is selected
     * through the EslSearchPanel
     * 
     * @param record
     */
    public abstract void eslRecordSelectedAction(Record record);
    
    public ChooserPanel getMainPanel() {
        return mainPanel;
    }
}
