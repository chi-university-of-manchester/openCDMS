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


package org.psygrid.edie.patches.v1_1_7;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch13 extends AbstractPatch {

	@Override
	public String getName() {
		return "Remove all documents from transition except SCID and Transition";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		Factory factory = new HibernateFactory();

		Document caarms = ds.getDocument(1);
		if ( !"CAARMS with GAF".equals(caarms.getName()) ){
			throw new RuntimeException("This is not the CAARMS with GAF document, it is "+caarms.getName());
		}
		Document bdi7 = ds.getDocument(2);
		if ( !"BDI-7".equals(bdi7.getName()) ){
			throw new RuntimeException("This is not the BDI-7 document, it is "+bdi7.getName());
		}
		Document sias = ds.getDocument(3);
		if ( !"SIAS".equals(sias.getName()) ){
			throw new RuntimeException("This is not the SIAS document, it is "+sias.getName());
		}
		Document eq5d = ds.getDocument(4);
		if ( !"EQ5D".equals(eq5d.getName()) ){
			throw new RuntimeException("This is not the EQ5D document, it is "+eq5d.getName());
		}
		Document pbiq = ds.getDocument(5);
		if ( !"PBIQ-R".equals(pbiq.getName()) ){
			throw new RuntimeException("This is not the PBIQ-R document, it is "+pbiq.getName());
		}
		Document mansa = ds.getDocument(6);
		if ( !"MANSA V2u".equals(mansa.getName()) ){
			throw new RuntimeException("This is not the MANSA V2u document, it is "+mansa.getName());
		}
		Document drugCheck = ds.getDocument(7);
		if ( !"Drug Check".equals(drugCheck.getName()) ){
			throw new RuntimeException("This is not the Drug Check document, it is "+drugCheck.getName());
		}
		Document treatment = ds.getDocument(8);
		if ( !"Treatment".equals(treatment.getName()) ){
			throw new RuntimeException("This is not the Treatment document, it is "+treatment.getName());
		}

		DocumentOccurrence caarmsOcc = caarms.getOccurrence(14);
		if(!"Transition".equals(caarmsOcc.getName())){
			throw new RuntimeException("This is not the Transition Occurrence, it is "+caarmsOcc.getName());
		}
		caarms.removeOccurrence(14);
		DocumentOccurrence bdi7Occ = bdi7.getOccurrence(13);
		if(!"Transition".equals(bdi7Occ.getName())){
			throw new RuntimeException("This is not the Transition Occurrence, it is "+bdi7Occ.getName());
		}
		bdi7.removeOccurrence(13);
		DocumentOccurrence siasOcc = sias.getOccurrence(13);
		if(!"Transition".equals(siasOcc.getName())){
			throw new RuntimeException("This is not the Transition Occurrence, it is "+siasOcc.getName());
		}
		sias.removeOccurrence(13);
		DocumentOccurrence eq5dOcc = eq5d.getOccurrence(13);
		if(!"Transition".equals(eq5dOcc.getName())){
			throw new RuntimeException("This is not the Transition Occurrence, it is "+eq5dOcc.getName());
		}
		eq5d.removeOccurrence(13);
		DocumentOccurrence pbiqOcc = pbiq.getOccurrence(5);
		if(!"Transition".equals(pbiqOcc.getName())){
			throw new RuntimeException("This is not the Transition Occurrence, it is "+pbiqOcc.getName());
		}
		pbiq.removeOccurrence(5);
		DocumentOccurrence mansaOcc = mansa.getOccurrence(5);
		if(!"Transition".equals(mansaOcc.getName())){
			throw new RuntimeException("This is not the Transition Occurrence, it is "+mansaOcc.getName());
		}
		mansa.removeOccurrence(5);
		DocumentOccurrence drugCheckOcc = drugCheck.getOccurrence(5);
		if(!"Transition".equals(drugCheckOcc.getName())){
			throw new RuntimeException("This is not the Transition Occurrence, it is "+drugCheckOcc.getName());
		}
		drugCheck.removeOccurrence(5);
		DocumentOccurrence treatmentOcc = treatment.getOccurrence(13);
		if(!"Transition".equals(treatmentOcc.getName())){
			throw new RuntimeException("This is not the Transition Occurrence, it is "+treatmentOcc.getName());
		}
		treatment.removeOccurrence(13);
	}
	@Override
	public boolean isolated() {
		return true;
	}

	@Override
	public Object preApplyPatch(DataSet ds, RepositoryClient client, String saml) throws Exception {


		Document caarms = ds.getDocument(1);
		if ( !"CAARMS with GAF".equals(caarms.getName()) ){
			throw new RuntimeException("This is not the CAARMS with GAF document, it is "+caarms.getName());
		}
		Document bdi7 = ds.getDocument(2);
		if ( !"BDI-7".equals(bdi7.getName()) ){
			throw new RuntimeException("This is not the BDI-7 document, it is "+bdi7.getName());
		}
		Document sias = ds.getDocument(3);
		if ( !"SIAS".equals(sias.getName()) ){
			throw new RuntimeException("This is not the SIAS document, it is "+sias.getName());
		}
		Document eq5d = ds.getDocument(4);
		if ( !"EQ5D".equals(eq5d.getName()) ){
			throw new RuntimeException("This is not the EQ5D document, it is "+eq5d.getName());
		}
		Document pbiq = ds.getDocument(5);
		if ( !"PBIQ-R".equals(pbiq.getName()) ){
			throw new RuntimeException("This is not the PBIQ-R document, it is "+pbiq.getName());
		}
		Document mansa = ds.getDocument(6);
		if ( !"MANSA V2u".equals(mansa.getName()) ){
			throw new RuntimeException("This is not the MANSA V2u document, it is "+mansa.getName());
		}
		Document drugCheck = ds.getDocument(7);
		if ( !"Drug Check".equals(drugCheck.getName()) ){
			throw new RuntimeException("This is not the Drug Check document, it is "+drugCheck.getName());
		}
		Document treatment = ds.getDocument(8);
		if ( !"Treatment".equals(treatment.getName()) ){
			throw new RuntimeException("This is not the Treatment document, it is "+treatment.getName());
		}
		List<DocumentOccurrence> ldo = new ArrayList<DocumentOccurrence>();

		DocumentOccurrence caarmsOcc = caarms.getOccurrence(14);
		if(!"Transition".equals(caarmsOcc.getName())){
			throw new RuntimeException("This is not the Transition Occurrence, it is "+caarmsOcc.getName());
		}
        ldo.add(caarmsOcc);
		DocumentOccurrence bdi7Occ = bdi7.getOccurrence(13);
		if(!"Transition".equals(bdi7Occ.getName())){
			throw new RuntimeException("This is not the Transition Occurrence, it is "+bdi7Occ.getName());
		}
		ldo.add(bdi7Occ);
		DocumentOccurrence siasOcc = sias.getOccurrence(13);
		if(!"Transition".equals(siasOcc.getName())){
			throw new RuntimeException("This is not the Transition Occurrence, it is "+siasOcc.getName());
		}
		ldo.add(siasOcc);
		DocumentOccurrence eq5dOcc = eq5d.getOccurrence(13);
		if(!"Transition".equals(eq5dOcc.getName())){
			throw new RuntimeException("This is not the Transition Occurrence, it is "+eq5dOcc.getName());
		}
		ldo.add(eq5dOcc);
		DocumentOccurrence pbiqOcc = pbiq.getOccurrence(5);
		if(!"Transition".equals(pbiqOcc.getName())){
			throw new RuntimeException("This is not the Transition Occurrence, it is "+pbiqOcc.getName());
		}
		ldo.add(pbiqOcc);
		DocumentOccurrence mansaOcc = mansa.getOccurrence(5);
		if(!"Transition".equals(mansaOcc.getName())){
			throw new RuntimeException("This is not the Transition Occurrence, it is "+mansaOcc.getName());
		}
		ldo.add(mansaOcc);
		DocumentOccurrence drugCheckOcc = drugCheck.getOccurrence(5);
		if(!"Transition".equals(drugCheckOcc.getName())){
			throw new RuntimeException("This is not the Transition Occurrence, it is "+drugCheckOcc.getName());
		}
		ldo.add(drugCheckOcc);
		DocumentOccurrence treatmentOcc = treatment.getOccurrence(13);
		if(!"Transition".equals(treatmentOcc.getName())){
			throw new RuntimeException("This is not the Transition Occurrence, it is "+treatmentOcc.getName());
		}
		ldo.add(treatmentOcc);

		List<Record> records = client.getRecords(ds.getId(), saml);
        for ( Record record: records ){
            System.out.println("Processing record "+record.getIdentifier().getIdentifier());
            boolean changed = false;
        	System.out.println("Retrieving data for record "+record.getIdentifier().getIdentifier());
        	Record r = client.getRecord(ds, record.getId(), saml);
            for(DocumentOccurrence occ: ldo){
            	DocumentInstance di = r.getDocumentInstance(occ);
            	if(di!=null){
            		System.out.println("Deleting document instance of "+occ.getCombinedName());
            		r.removeDocumentInstance(di);
            		changed = true;
            	}
        	}
        	if(changed){
                System.out.println("Saving record "+record.getIdentifier().getIdentifier());
                client.saveRecord(r, true, saml);
        	}
        }

        System.out.println("Exiting preApplyPatch.");
        return null;
	}
}
