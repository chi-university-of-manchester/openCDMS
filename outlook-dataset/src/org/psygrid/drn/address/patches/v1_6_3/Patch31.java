package org.psygrid.drn.address.patches.v1_6_3;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch31 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Factory factory = new HibernateFactory();
		ds.setNoReviewAndApprove(true);
		for ( int i=0, c=ds.numDocuments(); i<c; i++ ){
			Document doc = ds.getDocument(i);
			Status complete = null;
			Status pending = null;
			Status approved = null;
			for ( int j=0, d=doc.numStatus(); j<d; j++ ){
				Status s = doc.getStatus(j);
				if ( s.getShortName().equals(Status.DOC_STATUS_COMPLETE) ){
					complete = s;
				}
				if ( s.getShortName().equals(Status.DOC_STATUS_PENDING) ){
					pending = s;
				}
				if ( s.getShortName().equals(Status.DOC_STATUS_APPROVED) ){
					approved = s;
				}
			}
			Status controlled = factory.createStatus(Status.DOC_STATUS_CONTROLLED, "Controlled", 5);
			complete.addStatusTransition(controlled);
			pending.addStatusTransition(controlled);
			approved.addStatusTransition(controlled);
			doc.addStatus(controlled);
		}
	}

	@Override
	public String getName() {
		return "Change ADDRESS to 'Controlled' workflow";
	}

}
