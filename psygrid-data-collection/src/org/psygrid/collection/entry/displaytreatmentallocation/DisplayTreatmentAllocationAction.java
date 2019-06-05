package org.psygrid.collection.entry.displaytreatmentallocation;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.esl.services.RandomisationException;


public class DisplayTreatmentAllocationAction extends AbstractAction {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7851984022339068819L;
	
	private final Component parent;
	private final Record record;

    public DisplayTreatmentAllocationAction(Component parent, Record record) {
        super(Messages.getString("DisplayTreatment.menuText"));
		this.parent = parent;
		this.record = record;
    }
	
	public void actionPerformed(ActionEvent arg0) {
		
		TreatmentRetrievalFailReason failReason = TreatmentRetrievalFailReason.NONE;
		String allocatedTreatment = null;
		
		try{
			allocatedTreatment = DisplayTreatmentAllocationAction.retrieveAllocatedTreatment(parent, record);
		}catch(TreatmentRetrievalFailException e){
			failReason = e.getReason();
		}
		
		String title = Messages.getString("DisplayTreatment.title");
		String message = null;
		
		if(failReason == TreatmentRetrievalFailReason.NONE){
			message = Messages.getString("DisplayTreatment.treatmentMessage");
			message = message.replace("%", allocatedTreatment);
		}else if(failReason == TreatmentRetrievalFailReason.OTHER){
			message = Messages.getString("DisplayTreatment.NoTreatmentInfoMessage");
		}else if(failReason == TreatmentRetrievalFailReason.NOT_AUTHORISED){
			message = Messages.getString("DisplayTreatment.NotAuthorizedForTreatmentInfoMessage");
		}else if(failReason == TreatmentRetrievalFailReason.NOT_RANDOMIZED){
			message = Messages.getString("DisplayTreatment.NotRandomisedYetMessage");
		}else{
			//catch-all
			message = Messages.getString("DisplayTreatment.NoTreatmentInfoMessage");
		}
		
		JOptionPane.showMessageDialog(parent, message, title,
				JOptionPane.INFORMATION_MESSAGE);

	}
	
	public static String retrieveAllocatedTreatment(Component comp, Record record) throws TreatmentRetrievalFailException{
		
		String randomisationTreatment = null;
		
		try {
			randomisationTreatment = RemoteManager.getInstance().eslRetrieveRandomisationResult(record);
		} catch (NotAuthorisedFault e) {
			//Raise dialog to notify that user doesn't have the required privilege level to obtain info about the randomisation result.
			//Tell the user that they need to be given 'Treatment Administrator' status first.
			throw new TreatmentRetrievalFailException(TreatmentRetrievalFailReason.NOT_AUTHORISED);
		}  catch(RemoteServiceFault e){
			//Probably because the user isn't in the esl yet (and therefore not randomised).
			throw new TreatmentRetrievalFailException(TreatmentRetrievalFailReason.NOT_RANDOMIZED);
		} catch (RandomisationException e){
			//Probably because the user hasn't been randomised yet.
			throw new TreatmentRetrievalFailException(TreatmentRetrievalFailReason.NOT_RANDOMIZED);
		} catch (Exception e){
			//Need to just say that the randomisation treatment couldn't be retrieved at this time. And to try later from the main menu option.
			throw new TreatmentRetrievalFailException(TreatmentRetrievalFailReason.OTHER);
		}
		
		return randomisationTreatment;
	}
	

}
