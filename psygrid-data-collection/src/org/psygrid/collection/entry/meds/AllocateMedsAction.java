package org.psygrid.collection.entry.meds;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.model.hibernate.Record;

public class AllocateMedsAction extends AbstractAction {

	
	private final static Log LOG = LogFactory.getLog(AllocateMedsAction.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7849182164629015733L;


	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	public static boolean isMedsAllocationEnabled(Record record){
		boolean result = false;
		try {
			result = record.getDataSet().getUseMedsService();
		} catch (Exception e1) {
			// Not a major problem so just log it for now.
			LOG.warn("Problem identifying whether study uses the meds service.",e1);
		}
		return result;
	}

}
