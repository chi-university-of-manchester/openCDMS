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
import java.awt.event.WindowAdapter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.EventListenerList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.FormView;
import org.psygrid.collection.entry.builder.BuilderHandler;
import org.psygrid.collection.entry.builder.BuilderHelper;
import org.psygrid.collection.entry.event.EditEvent;
import org.psygrid.collection.entry.event.PostEditDialogBuiltEvent;
import org.psygrid.collection.entry.event.PostEditDialogBuiltListener;
import org.psygrid.collection.entry.event.RendererCreatedEvent;
import org.psygrid.collection.entry.event.RendererCreatedListener;
import org.psygrid.collection.entry.model.BasicPresModel;
import org.psygrid.collection.entry.model.ResponsePresModel;
import org.psygrid.collection.entry.renderer.BasicRenderer;
import org.psygrid.collection.entry.renderer.PresModelRenderer;
import org.psygrid.collection.entry.renderer.RendererData;
import org.psygrid.collection.entry.renderer.RendererHandler;
import org.psygrid.collection.entry.renderer.RendererHelper;
import org.psygrid.collection.entry.util.DdeHelper;
import org.psygrid.data.model.IValue;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.ValidationResult;
import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.model.hibernate.ResponseStatus;

public class EditDialog extends JDialog   {
    
	private static final int REASON_CHARS = 255;
	
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final BasicRenderer<?> renderer;
    private DefaultFormBuilder builder;
    private final RendererData rendererData;
    private BasicRenderer<?> copyRenderer;
    private JLabel annotationLabel;
    private JTextArea annotationField;
    private JTextArea commentField;
    private JButton okButton;
    private JButton cancelButton;
    private JLabel commentLabel;
    private ResponseStatus initialResponseStatus;
    private JPanel mainPanel;
    
    private EventListenerList listenerList = new EventListenerList();
    
    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(EditDialog.class);
    
    public EditDialog(final RendererData rendererData, final BasicRenderer<?> renderer) {
        super(getJFrame(renderer), true);
        setTitle(Messages.getString("EditDialog.editEntry")); //$NON-NLS-1$
        this.rendererData = rendererData;
        this.renderer = renderer;
        initComponents();
        initEventHandling();
    }
    
    public final void addPostEditDialogBuildEvent(PostEditDialogBuiltListener l) {
        listenerList.add(PostEditDialogBuiltListener.class, l);
    }
    
    public final void removePostEditDialogBuildEvent(PostEditDialogBuiltListener l) {
        listenerList.remove(PostEditDialogBuiltListener.class, l);
    }
    
    private void firePostEditDialogBuiltEvent(PostEditDialogBuiltEvent event) {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == PostEditDialogBuiltListener.class) {
                ((PostEditDialogBuiltListener) listeners[i + 1]).postBuilt(event);
            }
        }        
    }
    
    BasicRenderer<?> getCopyRenderer(){
        return copyRenderer;
    }
    
    private void initEventHandling() {
        final RendererHandler rendererHandler = rendererData.getRendererHandler();
        
        rendererHandler.addRendererCreatedListener(new RendererCreatedListener() {
           public void rendererCreated(RendererCreatedEvent event) {
               copyRenderer = BasicRenderer.class.cast(event.getRenderer());
               BasicPresModel presModel = copyRenderer.getPresModel();
               presModel.getValidationModel().setResult(ValidationResult.EMPTY);
               initialResponseStatus = 
                   (ResponseStatus) presModel.getResponseStatusModel().getValue();
               presModel.getResponseStatusModel().setValue(ResponseStatus.NORMAL);
               presModel.getEntryStatusModel().setValue(
                       renderer.getPresModel().getEntryStatusModel().getValue());
               rendererHandler.removeRendererCreatedListener(this);
               firePostEditDialogBuiltEvent(
                       new PostEditDialogBuiltEvent(EditDialog.this, renderer,
                               copyRenderer));
           }
        });
        
        // Safe not to release
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cancelChange();
            }
        });

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                acceptChange();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelChange();
            }
        });
    }
    
    private void initComponents() {
        initBuilder();
        if (renderer.getPresModel().getResponseStatusModel().getValue() == ResponseStatus.FLAGGED_INVALID) {
            annotationLabel = new JLabel(Messages.getString("EditDialog.reasonMarkedInvalidLabel"));
            annotationField = new JTextArea(3, 10);
            annotationField.setEditable(false);
            annotationField.setLineWrap(true);
            annotationField.setWrapStyleWord(true);
            annotationField.setText(renderer.getPresModel().getResponse().getAnnotation());
        }
        commentLabel = new JLabel(Messages.getString("EditDialog.reasonForChangeLabel"));
        commentField = new JTextArea(3, 10);
        commentField.setLineWrap(true);
        commentField.setWrapStyleWord(true);
        okButton = new JButton(EntryMessages.getString("Entry.ok")); //$NON-NLS-1$
        cancelButton = new JButton(EntryMessages.getString("Entry.cancel")); //$NON-NLS-1$
    }
    
    private void initBuilder() {
    	mainPanel = new JPanel();
        builder = new DefaultFormBuilder(new FormLayout(FormView.getColumnSpecs()), 
                mainPanel);
        builder.setDefaultDialogBorder();
    }
    
    private String validateInput() {
        if (commentField.getText().length() == 0) {
            return Messages.getString("EditDialog.reasonRequiredMessage");
        }
        if (commentField.getText().length() > REASON_CHARS) {
            return Messages.getString("EditDialog.reasonTooLongMessage_p1")+REASON_CHARS+Messages.getString("EditDialog.reasonTooLongMessage_p2")+commentField.getText().length()+Messages.getString("EditDialog.reasonTooLongMessage_p3");
        }
        BasicPresModel newPresModel = copyRenderer.getPresModel();
        if (newPresModel.getValidationModel().getResult().hasErrors()) {
            return EntryMessages.getString("ApplicationModel.validationErrors"); //$NON-NLS-1$
        }
        return null;
    }
    
    private void acceptChange() {
        String errorMessage = validateInput();
        if (errorMessage != null) {
            showErrorDialog(errorMessage);
            return;
        }
        BasicPresModel copyPresModel = copyRenderer.getPresModel();
        BasicPresModel presModel = renderer.getPresModel();
        
        IValue value = (IValue) copyPresModel.getBean();
        
        addValueToResponse(value, commentField.getText());
        presModel.setBean(value);
        
        rendererData.getRendererHandler().fireEditEvent(new EditEvent(this,
                presModel, copyPresModel));
        
        Entry entry = presModel.getEntry();
        RendererHelper.getInstance().getAppModel().getEditedEntries().add(entry);
        
        dispose();
    }

    private void showErrorDialog(String message) {
        String title = EntryMessages.getString("Application.validationErrorsTitle"); //$NON-NLS-1$
        JOptionPane.showMessageDialog(this, message, title, 
                JOptionPane.ERROR_MESSAGE);
    }

    private void cancelChange() {
        copyRenderer.getPresModel().getResponseStatusModel().setValue(initialResponseStatus);
        dispose();
    }
    
    private static JFrame getJFrame(PresModelRenderer<?> renderer) {
        if (renderer == null) {
            throw new IllegalArgumentException("renderer cannot be null"); //$NON-NLS-1$
        }
        return RendererHelper.getInstance().findJFrameFromRenderer(renderer);
    }
    
    public final void build() {
        buildEntries();
        builder.appendRow(FormFactory.DEFAULT_ROWSPEC);
        
        builder.nextLine();
        if (annotationField != null) {
            BuilderHelper.appendLabel(builder, annotationLabel);
            BuilderHelper.appendLabel(builder, annotationField);
            builder.appendUnrelatedComponentsGapRow();
            builder.nextLine();
        }

        BuilderHelper.appendLabel(builder, commentLabel);
        BuilderHelper.appendLabel(builder, commentField);

        builder.appendUnrelatedComponentsGapRow();
        builder.nextLine(2);
        JPanel buttonsPanel = 
            ButtonBarFactory.buildOKCancelBar(okButton, cancelButton);
        builder.append(buttonsPanel, builder.getColumnCount() - 1);

        JScrollPane scrollPane = new JScrollPane(builder.getPanel());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(scrollPane);
        pack();
        setLocation(WindowUtils.getPointForCentering(this));
    }
    
    private void buildEntries() {
        builder.setRow(0);
        BuilderHandler builderHandler = 
            rendererData.getRendererHandler().getBuilderHandler();
        builderHandler.build(builder, rendererData.getModel(), rendererData.getModelParent(),
                rendererData.getModelParentResponse(), true, rendererData.getRowIndex());
    }
    
    private void addValueToResponse(IValue value, String comment) {
        BasicPresModel presModel = renderer.getPresModel();
        BasicResponse response = presModel.getResponse();
        RendererHelper.getInstance().setResponseValue(response, value, comment);
        addValueToSecondaryResponse(response, value, comment);
        presModel.getResponseStatusModel().setValue(ResponseStatus.FLAGGED_EDITED);
    }
    
    private void addValueToSecondaryResponse(BasicResponse primResp, IValue value, String comment){
    	Record primRecord = primResp.getRecord();
    	if ( null != primRecord.getSecondaryRecord() ){
    		ResponsePresModel presModel = renderer.getPresModel();
    		BasicResponse secResponse = (BasicResponse)DdeHelper.findResponseForSecondary(presModel);
    		IValue secValue = value.ddeCopy(
    				(BasicEntry)DdeHelper.findEntryForPrimary(presModel),
    				(BasicEntry)DdeHelper.findEntryForSecondary(presModel));
    		secResponse.setValue(secValue, comment);
    	}
    }
}
