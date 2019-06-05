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


package org.psygrid.collection.entry.util;

import org.psygrid.collection.entry.model.ResponsePresModel;
import org.psygrid.data.model.hibernate.*;

/**
 * @author Rob Harper
 *
 */
public class DdeHelper {

	public static Response findResponseForSecondary(ResponsePresModel presModel){
    	Entry primEntry = presModel.getResponse().getEntry();
    	DocumentInstance primDocInst = presModel.getDocInstance();
    	Section primSec = primEntry.getSection();
    	SectionOccurrence primSecOcc = presModel.getSectionOccPresModel().getSectionOccurrence();
    	SecOccInstance primSecOccInst = presModel.getSectionOccPresModel().getSecOccInstance();
    	DocumentOccurrence primDocOcc = primDocInst.getOccurrence();
    	Document primDoc = primDocOcc.getDocument();
    	Record primRecord = presModel.getResponse().getRecord();
    	int index = primDoc.getIndexOfEntry(primEntry);
    	Record secRecord = primRecord.getSecondaryRecord();
    	DataSet secDs = secRecord.getDataSet();
    	Document secDoc = secDs.getDocument(primDoc.getSecondaryDocIndex().intValue());
    	DocumentOccurrence secDocOcc = secDoc.getOccurrence(primDocOcc.getSecondaryOccIndex().intValue());
    	Entry secEntry = secDoc.getEntry(index);
    	DocumentInstance secDocInst = secRecord.getDocumentInstance(secDocOcc);
    	Section secSec = secEntry.getSection();
    	Response secResp = null;
    	if ( null != primSecOccInst ){
    		int soiIndex = primDocInst.getIndexOfSecOccInstance(primSecOccInst);
    		SecOccInstance secSecOccInst = secDocInst.getSecOccInstance(soiIndex);
    		secResp = secDocInst.getResponse(secEntry, secSecOccInst);
    		if ( null == secResp ){
    			//required if an ISecOccInstance is added whilst document is rejected
    			secResp = secEntry.generateInstance(secSecOccInst);
    			secDocInst.addResponse(secResp);
    		}
    	}
    	else{
    		int soIndex = primSec.getIndexOfSectionOccurrence(primSecOcc);
    		SectionOccurrence secSecOcc = secSec.getOccurrence(soIndex);
    		secResp = secDocInst.getResponse(secEntry, secSecOcc);
    	}
    	return secResp;
	}
	
	public static Entry findEntryForPrimary(ResponsePresModel presModel){
		return presModel.getResponse().getEntry();
	}
	
	public static Entry findEntryForSecondary(ResponsePresModel presModel){
    	Entry primEntry = presModel.getResponse().getEntry();
    	DocumentInstance primDocInst = presModel.getDocInstance();
    	DocumentOccurrence primDocOcc = primDocInst.getOccurrence();
    	Document primDoc = primDocOcc.getDocument();
    	Record primRecord = presModel.getResponse().getRecord();
    	int index = primDoc.getIndexOfEntry(primEntry);
    	Record secRecord = primRecord.getSecondaryRecord();
    	DataSet secDs = secRecord.getDataSet();
    	Document secDoc = secDs.getDocument(primDoc.getSecondaryDocIndex().intValue());
    	return secDoc.getEntry(index);
	}
	
	public static DocumentInstance findDocInstForSecondary(DocumentInstance primDocInst, Record secRecord){
		DocumentOccurrence primDocOcc = primDocInst.getOccurrence();
		Document primDoc = primDocOcc.getDocument();
		DataSet secDs = secRecord.getDataSet();
		Document secDoc = secDs.getDocument(primDoc.getSecondaryDocIndex().intValue());
		DocumentOccurrence secDocOcc = secDoc.getOccurrence(primDocOcc.getSecondaryOccIndex().intValue());
		return secRecord.getDocumentInstance(secDocOcc);
	}
	
}
