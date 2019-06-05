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

package org.psygrid.command.patches.v1_1_33;

import java.util.List;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.model.IValue;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch6 extends AbstractPatch {

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#applyPatch(org.psygrid.data.model.IDataSet, java.lang.String)
	 */
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		Unit percent = null;
		for ( int i=0, c=ds.numUnits(); i<c; i++ ){
			Unit u = ds.getUnit(i);
			if ( "%".equals(u.getAbbreviation())){
				percent = u;
				break;
			}
		}
		if ( null == percent ){
			throw new RuntimeException("Could not find the % unit");
		}

		ValidationRule zeroToHundred = null;
		for ( int i=0, c=ds.numValidationRules(); i<c; i++ ){
			ValidationRule v = ds.getValidationRule(i);
			if ( "0 to 100".equals(v.getDescription())){
				zeroToHundred = v;
				break;
			}
		}
		if ( null == zeroToHundred ){
			throw new RuntimeException("Could not find the 0 to 100 validation rule");
		}

		Document cav = ds.getDocument(1);
		if ( !"CAV".equals(cav.getName()) ){
			throw new RuntimeException("This is not the CAV document, it is "+cav.getName());
		}

		Section compliance = cav.getSection(9);
		if ( !"Compliance".equals(compliance.getDisplayText()) ){
			throw new RuntimeException("This is not the Compliance section, it is "+compliance.getDisplayText());
		}

		Entry qJ2 = cav.getEntry(68);
		if ( !"How likely do now?".equals(qJ2.getName()) ){
			throw new RuntimeException("This is not the How likely do now? entry, it is "+qJ2.getName());
		}

		HibernateFactory factory = new HibernateFactory();

		NumericEntry qJ2a = factory.createNumericEntry("How likely do now (relative)?", "How likely are you to do as the voice says now? (ask relative/staff member)");
		qJ2a.setSection(compliance);
		qJ2a.setLabel("2a");
		qJ2a.addUnit(percent);
		qJ2a.addValidationRule(zeroToHundred);
		cav.insertEntry(qJ2a, 69);

		Entry qJ3 = cav.getEntry(70);
		if ( !"How likely do future?".equals(qJ3.getName()) ){
			throw new RuntimeException("This is not the How likely do future? entry, it is "+qJ3.getName());
		}

		NumericEntry qJ3a = factory.createNumericEntry("How likely do future (relative)?", "How likely are you to do as the voice says in the future? (ask relative/staff member)");
		qJ3a.setSection(compliance);
		qJ3a.setLabel("3a");
		qJ3a.addUnit(percent);
		qJ3a.addValidationRule(zeroToHundred);
		cav.insertEntry(qJ3a, 71);

	}

	/* (non-Javadoc)
	 * @see org.psygrid.outlook.patches.AbstractPatch#getName()
	 */
	@Override
	public String getName() {
		return "Add questions to compliance section";
	}

	@Override
	public void postApplyPatch(DataSet ds, Object obj,
			RepositoryClient client, String saml) throws Exception {

		Document cav = ds.getDocument(1);
		if ( !"CAV".equals(cav.getName()) ){
			throw new RuntimeException("This is not the CAV document, it is "+cav.getName());
		}

		Section compliance = cav.getSection(9);
		if ( !"Compliance".equals(compliance.getDisplayText()) ){
			throw new RuntimeException("This is not the Compliance section, it is "+compliance.getDisplayText());
		}
		SectionOccurrence complianceOcc = compliance.getOccurrence(0);

		BasicEntry qJ2a = (BasicEntry)cav.getEntry(69);
		if ( !"How likely do now (relative)?".equals(qJ2a.getName()) ){
			throw new RuntimeException("This is not the How likely do now (relative)? entry, it is "+qJ2a.getName());
		}

		BasicEntry qJ3a = (BasicEntry)cav.getEntry(71);
		if ( !"How likely do future (relative)?".equals(qJ3a.getName()) ){
			throw new RuntimeException("This is not the How likely do future (relative)? entry, it is "+qJ3a.getName());
		}

        //get standard codes
        List<StandardCode> stdCodes = client.getStandardCodes(saml);

        //Apply standard code 999 to new questions for all existing records
        List<Record> records = client.getRecords(ds.getId(), saml);
        for ( Record record: records ){
            System.out.println("Processing record "+record.getIdentifier().getIdentifier());
            record.attach(ds);
            for ( int i=0, c=cav.numOccurrences(); i<c; i++ ){
            	DocumentOccurrence docOcc = cav.getOccurrence(i);
	            DocumentInstance docInst = record.getDocumentInstance(docOcc);
	            if ( null != docInst ){

	                System.out.println("Retrieving data for record "+record.getIdentifier().getIdentifier());
	                Record r = client.getRecordSingleDocument(record.getId(), docInst.getId(), ds, saml);
	                DocumentInstance di = r.getDocumentInstance(docOcc);

	                BasicResponse br2a = (BasicResponse)qJ2a.generateInstance(complianceOcc);
	                di.addResponse(br2a);
	                IValue val2a = qJ2a.generateValue();
	                br2a.setValue(val2a);
	                val2a.setStandardCode(stdCodes.get(3));

	                BasicResponse br3a = (BasicResponse)qJ3a.generateInstance(complianceOcc);
	                di.addResponse(br3a);
	                IValue val3a = qJ3a.generateValue();
	                br3a.setValue(val3a);
	                val3a.setStandardCode(stdCodes.get(3));

	                System.out.println("Saving record "+record.getIdentifier().getIdentifier());
	                client.saveRecord(r, true, saml);
	            }
            }
        }
	}

	@Override
	public boolean isolated() {
		return true;
	}


}
