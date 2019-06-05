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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch11 extends AbstractPatch {

	@Override
	public String getName() {
		return "Modify SCID to allow multiple diagnoses";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		Factory factory = new HibernateFactory();

		Document scid = ds.getDocument(12);
		if ( !"SCID".equals(scid.getName()) ){
			throw new RuntimeException("This is not the SCID document, it is "+scid.getName());
		}

		OptionEntry diagnosis = (OptionEntry)scid.getEntry(0);
		if ( !"SCID Diagnosis".equals(diagnosis.getName()) ){
			throw new RuntimeException("This is not the SCID Diagnosis entry, it is "+diagnosis.getName());
		}

		scid.removeEntry(0);

		CompositeEntry diagComp = factory.createComposite("SCID composite", "Select the Scid diagnosis/diagnoses from the list");
		scid.addEntry(diagComp);
		diagComp.setSection(diagnosis.getSection());
		diagComp.setLabel("1");

		OptionEntry diagOpt = factory.createOptionEntry("SCID Diagnosis", "SCID Diagnosis");
		diagComp.addEntry(diagOpt);
		diagOpt.setSection(diagnosis.getSection());
		diagOpt.setOptionCodesDisplayed(true);
		diagOpt.setDescription("Select the SCID diagnosis from the list.");

		for ( int i=0, c=diagnosis.numOptions(); i<c; i++ ){
			Option o = diagnosis.getOption(i);
			Option newOpt = factory.createOption(o.getName(), o.getDisplayText(), o.getCode());
			newOpt.setDescription(o.getDescription());
			newOpt.setTextEntryAllowed(o.isTextEntryAllowed());
			diagOpt.addOption(newOpt);
		}

	}

	@Override
	public Object preApplyPatch(DataSet ds, RepositoryClient client, String saml) throws Exception {

		//Data that will be returned and later used in postApplyPatch
		//Map of record id -> list of SCID data (zeroth element is oldest value, nth is newest value)
		//The SCID data is an Object array with 3 elements;
		//    0th is the code of the option/standard code
		//    1st is the additional "other" text for the option (if applicable)
		//    2nd is the change comment (if applicable)
		Map<Long, List<Object[]>> data = new HashMap<Long, List<Object[]>>();

		Document scid = ds.getDocument(12);
		if ( !"SCID".equals(scid.getName()) ){
			throw new RuntimeException("This is not the SCID document, it is "+scid.getName());
		}

		DocumentOccurrence scidOcc = scid.getOccurrence(0);

		Section scidSec = scid.getSection(0);
		SectionOccurrence scidSecOcc = scidSec.getOccurrence(0);

		OptionEntry diagnosis = (OptionEntry)scid.getEntry(0);
		if ( !"SCID Diagnosis".equals(diagnosis.getName()) ){
			throw new RuntimeException("This is not the SCID Diagnosis entry, it is "+diagnosis.getName());
		}

		List<Record> records = client.getRecords(ds.getId(), saml);
        for ( Record record: records ){
            System.out.println("Processing record "+record.getIdentifier().getIdentifier());
            record.attach(ds);
            DocumentInstance scidInst = record.getDocumentInstance(scidOcc);
            if ( null != scidInst ){

                System.out.println("Retrieving data for record "+record.getIdentifier().getIdentifier());
                Record r = client.getRecordSingleDocument(record.getId(), scidInst.getId(), ds, saml);
                DocumentInstance di = r.getDocumentInstance(scidOcc);

                BasicResponse scidResp = (BasicResponse)di.getResponse(diagnosis, scidSecOcc);

                if ( null != scidResp ){
                	List<Object[]> vals = new ArrayList<Object[]>();
                	data.put(record.getId(), vals);
            		List<Provenance> provList = scidResp.getProvenance();
                	//old values
                	for ( Value oldVal: scidResp.getOldValues() ){
                		IOptionValue oldScidVal = (IOptionValue)oldVal;
                    	if ( null != oldScidVal ){
                    		String comment = null;
                    		for ( Provenance p: provList ){
                    			if ( oldScidVal.equals(p.getCurrentValue()) ){
                    				comment = p.getComment();
                    				break;
                    			}
                    		}
                    		Object[] scidData = new Object[3];
                    		scidData[2] = comment;
                    		if ( null != oldScidVal.getStandardCode() ){
                    			scidData[0] = new Integer(oldScidVal.getStandardCode().getCode());
                    		}
                    		else{
                    			scidData[0] = oldScidVal.getValue().getCode();
                    			scidData[1] = oldScidVal.getTextValue();
                    		}
                			vals.add(scidData);
                    	}
                	}
                	//current value
                	IOptionValue scidVal = (IOptionValue)scidResp.getValue();
                	if ( null != scidVal ){
                		String comment = null;
                		for ( Provenance p: provList ){
                			if ( scidVal.equals(p.getCurrentValue()) ){
                				comment = p.getComment();
                				break;
                			}
                		}
                		Object[] scidData = new Object[3];
                		scidData[2] = comment;
                		if ( null != scidVal.getStandardCode() ){
                			scidData[0] = new Integer(scidVal.getStandardCode().getCode());
                		}
                		else{
                			scidData[0] = scidVal.getValue().getCode();
                			scidData[1] = scidVal.getTextValue();
                		}
            			vals.add(scidData);
                	}
                }

                di.removeResponse(diagnosis, scidSecOcc);

                System.out.println("Saving record "+record.getIdentifier().getIdentifier());
                client.saveRecord(r, true, saml);
            }
        }

        System.out.println("Exiting preApplyPatch.");
        return data;
	}

	@Override
	public boolean isolated() {
		return true;
	}

	@Override
	public void postApplyPatch(DataSet ds, Object obj, RepositoryClient client, String saml) throws Exception {

		//Data object constructed in preApplyPatch
		//Map of record id -> list of SCID data (zeroth element is oldest value, nth is newest value)
		//The SCID data is an Object array with 3 elements;
		//    0th is the code of the option/standard code
		//    1st is the additional "other" text for the option (if applicable)
		//    2nd is the change comment (if applicable)
		Map<Long, List<Object[]>> data = (Map<Long, List<Object[]>>)obj;

		List<StandardCode> stdCodes = client.getStandardCodes(saml);

		Document scid = ds.getDocument(12);
		if ( !"SCID".equals(scid.getName()) ){
			throw new RuntimeException("This is not the SCID document, it is "+scid.getName());
		}

		DocumentOccurrence scidOcc = scid.getOccurrence(0);

		Section scidSec = scid.getSection(0);
		SectionOccurrence scidSecOcc = scidSec.getOccurrence(0);

		CompositeEntry diagComp = (CompositeEntry)scid.getEntry(0);
		if ( !"SCID composite".equals(diagComp.getName()) ){
			throw new RuntimeException("This is not the SCID composite entry, it is "+diagComp.getName());
		}

		OptionEntry diagnosis = (OptionEntry)diagComp.getEntry(0);
		if ( !"SCID Diagnosis".equals(diagnosis.getName()) ){
			throw new RuntimeException("This is not the SCID Diagnosis entry, it is "+diagnosis.getName());
		}

		List<Record> records = client.getRecords(ds.getId(), saml);
        for ( Record record: records ){
            System.out.println("Processing record "+record.getIdentifier().getIdentifier());
            record.attach(ds);
            DocumentInstance scidInst = record.getDocumentInstance(scidOcc);
            if ( null != scidInst ){

                System.out.println("Retrieving data for record "+record.getIdentifier().getIdentifier());
                Record r = client.getRecordSingleDocument(record.getId(), scidInst.getId(), ds, saml);
                DocumentInstance di = r.getDocumentInstance(scidOcc);

                List<Object[]> scidVals = data.get(record.getId());

                if ( null != scidVals ){
                	CompositeResponse compResp = diagComp.generateInstance(scidSecOcc);
                	di.addResponse(compResp);
                	CompositeRow row = compResp.createCompositeRow();
                	BasicResponse diagResp = diagnosis.generateInstance(scidSecOcc);
                	row.addResponse(diagResp);

                	//set values
                	for ( Object[] scidData: scidVals ){
                    	IOptionValue optVal = diagnosis.generateValue();
                    	Integer code = (Integer)scidData[0];
                    	if ( code.intValue() > 900 ){
                    		//standard code
                    		for ( StandardCode stdCode: stdCodes ){
                    			if ( code.intValue() == stdCode.getCode()){
                    				optVal.setStandardCode(stdCode);
                    				break;
                    			}
                    		}
                    	}
                    	else{
                    		for ( int i=0, c=diagnosis.numOptions(); i<c; i++ ){
                    			Option o = diagnosis.getOption(i);
                    			if ( code.equals(o.getCode()) ){
                    				optVal.setValue(o);
                    				optVal.setTextValue((String)scidData[1]);
                    				break;
                    			}
                    		}
                    	}
                		diagResp.setValue(optVal, (String)scidData[2]);
                	}

                }

                System.out.println("Saving record "+record.getIdentifier().getIdentifier());
                client.saveRecord(r, true, saml);

            }
        }

        System.out.println("Exiting postApplyPatch.");

	}

}
