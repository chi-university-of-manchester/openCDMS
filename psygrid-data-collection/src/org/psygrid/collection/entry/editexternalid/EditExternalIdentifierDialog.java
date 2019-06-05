package org.psygrid.collection.entry.editexternalid;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.ui.IdentifierPanelException;
import org.psygrid.data.model.hibernate.Record;

public class EditExternalIdentifierDialog extends JDialog implements ActionListener {
	
	
	public enum EditIdentifierResult{
		SAVE,
		CANCEL
	};
	


	private String newExternalIdentifier = null;
	private EditIdentifierResult dialogResult = null;
	
	private EditExternalIdentifierPanel contentPanel;
	
    public EditExternalIdentifierDialog(Application parent, Record rec) throws IdentifierPanelException {
        super(parent, true);
        init(parent, rec);
        
    }
    
    private void init(Application parent, Record rec) throws IdentifierPanelException {
        setTitle("Edit External Identifier"); //$NON-NLS-1$

        getContentPane().setLayout(new BorderLayout());
        contentPanel = new EditExternalIdentifierPanel(rec, this);

        getContentPane().add(contentPanel, BorderLayout.CENTER);
        pack();
        Dimension size = getSize();
        if (size.width < 415) {
            setSize(415, size.height);   
        }
        setLocation(WindowUtils.getPointForCentering(this));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

	public void actionPerformed(ActionEvent arg0) {
		
		String actionCommand = arg0.getActionCommand();
		
		EditExternalIdentifierPanel.actionCommands command = null;
		command = EditExternalIdentifierPanel.actionCommands.valueOf(actionCommand);
		if(command == null){
			return;
		}
		
		switch(command){
		case SAVE:
			{
				dialogResult = EditIdentifierResult.SAVE;
				newExternalIdentifier = contentPanel.getNewIdentifier();
				dialogResult = EditIdentifierResult.SAVE;
				//Need to save the record now - but this do we really want the dialog to do this? No - should fire an
				//event to an appropriate listener and have the listener do it.
				dispose();
				break;
			}
		case CANCEL:
			{
				dialogResult = EditIdentifierResult.CANCEL;
				dispose();
				break;
			}
		}
		
	}
	
	public String getNewExternalIdentifier() {
		return newExternalIdentifier;
	}

	public EditIdentifierResult getDialogResult() {
		return dialogResult;
	}

}
