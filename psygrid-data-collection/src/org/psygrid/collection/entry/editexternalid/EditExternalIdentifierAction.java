package org.psygrid.collection.entry.editexternalid;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.SubmitRecordStrategy;
import org.psygrid.collection.entry.persistence.IdentifierData;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.ui.IdentifierPanelException;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.utils.security.NotAuthorisedFault;

public class EditExternalIdentifierAction extends AbstractAction {

	
	private final Application parent;
	private final Record record;

    public EditExternalIdentifierAction(Application parent, Record record) {
        super("edit external identifier");
		this.parent = parent;
		this.record = record;
    }
	
	public void actionPerformed(ActionEvent e) {
	
		EditExternalIdentifierDialog dlg = null;
		try {
			dlg = new EditExternalIdentifierDialog(parent, record);
		} catch (IdentifierPanelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
		dlg.pack();
		dlg.setVisible(true);
		EditExternalIdentifierDialog.EditIdentifierResult result = dlg.getDialogResult();
		
		if(result == EditExternalIdentifierDialog.EditIdentifierResult.SAVE){
			((Record)record).setExternalIdentifier(dlg.getNewExternalIdentifier());
			IdentifierData d = PersistenceManager.getInstance().getIdentifiers().get(record.getIdentifier());
			Long recordId = null;
			Record recSummary = null;
			try {
				recSummary = RemoteManager.getInstance().getRecordSummary(record.getIdentifier().getIdentifier());
				recSummary.setExternalIdentifier(dlg.getNewExternalIdentifier());
				recordId = recSummary.getId();
				((Record)record).setId(recordId);
			} catch (ConnectException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SocketTimeoutException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NotAuthorisedFault e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvalidIdentifierException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (RemoteServiceFault e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (EntrySAMLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//record.setIdentifier(d.getIdentifier()); //We do this so that the identifier has an id.
			SubmitRecordStrategy strategy = new SubmitRecordStrategy(parent);
			List<DocumentInstance> emptyDocInstancesList = new ArrayList<DocumentInstance>();
			//((Record)recSummary).setDocInstances(emptyDocInstancesList);
			strategy.submit(record);
		}
		

	}

}
