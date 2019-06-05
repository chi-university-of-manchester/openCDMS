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

package org.psygrid.collection.entry.replication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.RecordStatusMap2;
import org.psygrid.collection.entry.util.RecordHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.model.hibernate.*;

/**
 * Helper methods for performing record linking/data replication.
 * 
 * @author Rob Harper
 *
 */
public class LinkRecordsHelper {

	private static final Log LOG = LogFactory.getLog(LinkRecordsHelper.class);
	
	/**
	 * Method to link a primary and secondary record together and perform
	 * data replication between the two. On completion, the data in the two 
	 * records is synchronized and they will be in a valid state whereby all
	 * future data entered into the primary record may be replicated automatically
	 * to the secondary.
	 * 
	 * @param primLocalRecord Primary record, as persisted locally.
	 * @param primRemoteRecord Primary record, as stored in the data repository.
	 * @param primLocalIncompleteRecord Primary record (incomplete document instances),
	 * as persisted locally.
	 * @param secLocalRecord Secondary record, as persisted locally.
	 * @param secRemoteRecord Secondary record, as stored in the data repository.
	 * @param secLocalIncompleteRecord Secondary record (incomplete document instances),
	 * as persisted locally.
	 * @param reverseCopy If True, then complete document instances in the secondary
	 * record that don't have equivalent document instances in the primary are to
	 * be reverse copied into the primary record.
	 * @param incompleteReverseCopy If True, then incomplete document instances in the 
	 * secondary record that don't have equivalent document instances in the primary are to
	 * be reverse copied into the primary record, before being deleted from the secondary.
	 * @return The result of the linking process.
	 * @throws InvalidIdentifierException
	 * @throws IOException
	 */
    protected static DdeCopyResult ddeCopy(
    		Record primLocalRecord, Record primRemoteRecord, Record primLocalIncompleteRecord,
    		Record secLocalRecord, Record secRemoteRecord, Record secLocalIncompleteRecord,
    		boolean reverseCopy, boolean incompleteReverseCopy )
    	throws InvalidIdentifierException, IOException
    {
    	
    	List<String> docsNotCopied = new ArrayList<String>();
    	List<DocumentInstance> docsToResetStatus = new ArrayList<DocumentInstance>();
    	List<DocumentInstance> docsToSetToPending = new ArrayList<DocumentInstance>();
    	boolean savePrimaryRemoteRecord = false;
    	boolean saveSecondaryRemoteRecord = false;
    	boolean savePrimaryLocalRecord = false;
    	boolean savePrimaryLocalIncompRecord = false;
    	boolean saveSecondaryLocalIncompRecord = false;
    	
    	DataSet primDataSet = primRemoteRecord.getDataSet();
    	DataSet secDataSet = secLocalRecord.getDataSet();
    	
    	RecordStatusMap2 recordStatusMap = PersistenceManager.getInstance().getRecordStatusMap();
    	
    	for ( int i=0, c=primDataSet.numDocuments(); i<c; i++ ){
    		
    		Document primDoc = primDataSet.getDocument(i);
    		if ( null != primDoc.getSecondaryDocIndex() ){
    			Document secDoc = secDataSet.getDocument(primDoc.getSecondaryDocIndex().intValue());
    			
    			for ( int j=0, d=primDoc.numOccurrences(); j<d; j++ ){
    				
    				DocumentOccurrence primDocOcc = primDoc.getOccurrence(j);
    				if ( null != primDocOcc.getSecondaryOccIndex() ){
    					DocumentOccurrence secDocOcc = secDoc.getOccurrence(primDocOcc.getSecondaryOccIndex().intValue());
    					
    					LOG.info("Linking documents "+primDocOcc.getCombinedName()+" and "+secDocOcc.getCombinedName());
    					
    					//find document instances
    					DocumentInstance primRemoteInstance = findDocumentInstance(primRemoteRecord, false, false, primDocOcc, true);
    					DocumentInstance primLocalInstance = findDocumentInstance(primLocalRecord, true, false, primDocOcc, true);
    					DocumentInstance secRemoteInstance = findDocumentInstance(secRemoteRecord, false, false, secDocOcc, true);
    					DocumentInstance secLocalInstance = findDocumentInstance(secLocalRecord, true, false, secDocOcc, true);
    					DocumentInstance primRemoteIncompInstance = findDocumentInstance(primRemoteRecord, false, false, primDocOcc, false);
    					DocumentInstance primLocalIncompInstance = findDocumentInstance(primLocalIncompleteRecord, true, true, primDocOcc, false);
    					DocumentInstance secRemoteIncompInstance = findDocumentInstance(secRemoteRecord, false, false, secDocOcc, false);
    					DocumentInstance secLocalIncompInstance = findDocumentInstance(secLocalIncompleteRecord, true, true, secDocOcc, false);
    					
    					if ( null == primRemoteIncompInstance && null == primLocalIncompInstance &&
    						 null == secRemoteIncompInstance && null == secLocalIncompInstance ){
    						
    						//no incomplete instances to worry about

    						if ( null == secRemoteInstance && null == secLocalInstance ){
	        					//CASE 1 No instance in secondary
	    						//Copy the data from the primary local or remote records
	    						if ( null != primRemoteInstance ){
	    							LOG.info("Primary complete remote; no secondary");
	    							copyDocInstIntoRecord(primRemoteInstance, secDoc, secDocOcc, secLocalRecord, docsNotCopied);
	    							recordStatusMap.addDocStatus(secLocalRecord.getIdentifier().getIdentifier(), secDocOcc, RecordStatusMap2.READY_TO_SUBMIT_ID);
	    						}
	    						else if ( null != primLocalInstance ){
	    							LOG.info("Primary complete local; no secondary");
	    							copyDocInstIntoRecord(primLocalInstance, secDoc, secDocOcc, secLocalRecord, docsNotCopied);
	    							recordStatusMap.addDocStatus(secLocalRecord.getIdentifier().getIdentifier(), secDocOcc, RecordStatusMap2.READY_TO_SUBMIT_ID);
	    						}
	    						
	    					}
	    					else{
	    						//There is an instance in the secondary record (local or remote)
	    						
	    						if ( null != primRemoteInstance || null != primLocalInstance ){
	    							//CASE 2 Instance in the primary record and instance in the secondary record
	    							
	    							if ( null != secLocalInstance ){
	    								//CASE 2A Instance in the primary record and instance in the local secondary record
	    								//The instance in the local secondary record is removed and replaced with a new
	    								//instance generated from the primary
	    								secLocalRecord.detachDocumentInstance(secLocalInstance);
	    								recordStatusMap.removeDocStatus(secLocalRecord.getIdentifier().getIdentifier(), secDocOcc);
	    	    						if ( null != primRemoteInstance ){
			    							LOG.info("Primary complete remote; secondary complete local");
	    	    							copyDocInstIntoRecord(primRemoteInstance, secDoc, secDocOcc, secLocalRecord, docsNotCopied);
	    	    							recordStatusMap.addDocStatus(secLocalRecord.getIdentifier().getIdentifier(), secDocOcc, RecordStatusMap2.READY_TO_SUBMIT_ID);
	    	    						}
	    	    						else if ( null != primLocalInstance ){
			    							LOG.info("Primary complete local; secondary complete local");
	    	    							copyDocInstIntoRecord(primLocalInstance, secDoc, secDocOcc, secLocalRecord, docsNotCopied);
	    	    							recordStatusMap.addDocStatus(secLocalRecord.getIdentifier().getIdentifier(), secDocOcc, RecordStatusMap2.READY_TO_SUBMIT_ID);
	    	    						}
	    							}
	    							else if ( null != secRemoteInstance ){
	    								//CASE 2B Instance in the primary record and instance in the remote secondary record.
	    								//The data in the remote secondary record is overwritten with data copied from
	    								//the primary record.
	    	    						if ( null != primRemoteInstance ){
			    							LOG.info("Primary complete remote; remote complete secondary");
	    	    							copyDocInstIntoRecord(primRemoteInstance, secRemoteInstance);
	    	    							saveSecondaryRemoteRecord = true;
	    	    						}
	    	    						else if ( null != primLocalInstance ){
	    	    							//Delete secondary instance from remote record.
	    	    							//Copy from primary to secondary local record
			    							LOG.info("Primary complete local; remote complete secondary");
	    	    							secRemoteRecord.removeDocumentInstance(secRemoteInstance);
	    	    							saveSecondaryRemoteRecord = true;
	    	    							copyDocInstIntoRecord(primLocalInstance, secDoc, secDocOcc, secLocalRecord, docsNotCopied);		    	    							
	    	    						}
	    							}
	    							
	    						}
	    						else{
	    							//CASE 3 No instance in the primary record but there is an instance in the
	    							//secondary record. If the reverseCopy flag is True then the data is copied
	    							//from the secondary to the primary; if it is False then the instance in the
	    							//secondary record is deleted
	    							if ( reverseCopy ){
	    								if ( null != secRemoteInstance ){
	    									LOG.info("No primary; remote complete secondary (reverse copy)");
	    									copyDocInstIntoRecord(secRemoteInstance, primDoc, primDocOcc, primRemoteRecord, docsNotCopied);
	    									//check the status of the doc instance - if it is Rejected or Approved we need to reset 
	    									//it to Pending, as review and approve will be driven by the primary instance from this point
	    									docsToResetStatus.add(secRemoteInstance);
	    									docsToSetToPending.add(primRemoteRecord.getDocumentInstance(primDocOcc));
	    									savePrimaryRemoteRecord = true;
	    								}
	    								else if (null != secLocalInstance ){
	    									LOG.info("No primary; local complete secondary (reverse copy)");
		    								if ( null == primLocalRecord ){
		    									//we need a local record for the primary to add the new document instances
		    									//to, so we have to create one here if it doesn't already exist
		    									primLocalRecord = RecordHelper.constructRecord(primRemoteRecord);
		    								}
	    									copyDocInstIntoRecord(secLocalInstance, primDoc, primDocOcc, primLocalRecord, docsNotCopied);
	    	    							recordStatusMap.addDocStatus(primLocalRecord.getIdentifier().getIdentifier(), primDocOcc, RecordStatusMap2.READY_TO_SUBMIT_ID);
		    								savePrimaryLocalRecord = true;
	    								}
	    							}
	    							else{
	    								if ( null != secRemoteInstance ){
	    									LOG.info("No primary; remote complete secondary (delete)");
	    									secRemoteRecord.removeDocumentInstance(secRemoteInstance);
	    									recordStatusMap.removeDocStatus(secRemoteRecord.getIdentifier().getIdentifier(), secDocOcc);
	    									saveSecondaryRemoteRecord = true;
	    								}
	    								else if ( null != secLocalInstance ){
	    									LOG.info("No primary; local complete secondary (delete)");
	    									secLocalRecord.detachDocumentInstance(secLocalInstance);
	    	    							recordStatusMap.removeDocStatus(secLocalRecord.getIdentifier().getIdentifier(), secDocOcc);
	    								}
	    							}
	    						}
	    					}
    					}
    					else{
    						
    						//we have some incomplete instances!
    						if ( null != secLocalIncompInstance || null != secRemoteIncompInstance ){
    							
    							//There is an incomplete secondary instance
    							
    							if ( null != primLocalIncompInstance || null != primRemoteIncompInstance ){
    								//CASE 4 Incomplete instance in primary record and incomplete instance
    								//in secondary record. Remove the incomplete instance from the secondary
    								//(it will be copied from the primary when the incomplete primary instance
    								//is completed)
    								if ( null != secRemoteIncompInstance ){
    									LOG.info("Incomplete primary; remote incomplete secondary");
    									secRemoteRecord.removeDocumentInstance(secRemoteIncompInstance);
    									recordStatusMap.removeDocStatus(secRemoteRecord.getIdentifier().getIdentifier(), secDocOcc);
    									saveSecondaryRemoteRecord = true;
    								}
    								else if (null != secLocalIncompInstance ){
    									LOG.info("Incomplete primary; local incomplete secondary");
    									secLocalIncompleteRecord.detachDocumentInstance(secLocalIncompInstance);
    									recordStatusMap.removeDocStatus(secLocalIncompleteRecord.getIdentifier().getIdentifier(), secDocOcc);
    									saveSecondaryLocalIncompRecord = true;
    								}    								
    								
    							}
    							else if ( null == primLocalInstance && null == primRemoteInstance && null == primLocalIncompInstance && null == primRemoteIncompInstance ){
    								//CASE 5 Incomplete instance in secondary record and no instance (complete 
    								//or incomplete) in primary record. Copy the incomplete instance to the 
    								//primary then remove it from the secondary
    								
    								if ( null != secRemoteIncompInstance ){
    									LOG.info("No primary; remote incomplete secondary");
    									copyDocInstIntoRecord(secRemoteIncompInstance, primDoc, primDocOcc, primRemoteRecord, docsNotCopied);
    									secRemoteRecord.removeDocumentInstance(secRemoteIncompInstance);
    									saveSecondaryRemoteRecord = true;
    									savePrimaryRemoteRecord = true;
    									if ( null != secLocalIncompInstance ){
    										//it is possible that there may be a local instance of the incomplete
    										//secondary document too  - in which case we need to remove the instance
    										//from the secondary local record too
	    									secLocalIncompleteRecord.detachDocumentInstance(secLocalIncompInstance);
	    									saveSecondaryLocalIncompRecord = true;
    									}
    									recordStatusMap.removeDocStatus(secRemoteRecord.getIdentifier().getIdentifier(), secDocOcc);
    								}
    								else if (null != secLocalIncompInstance ){
    									LOG.info("No primary; local incomplete secondary");
	    								if ( null == primLocalIncompleteRecord ){
	    									//we need a local incomplete record for the primary to add the new document instances
	    									//to, so we have to create one here if it doesn't already exist
	    									primLocalIncompleteRecord = RecordHelper.constructRecord(primRemoteRecord);    									
	    								}
    									copyDocInstIntoRecord(secLocalIncompInstance, primDoc, primDocOcc, primLocalIncompleteRecord, docsNotCopied);
    	    							recordStatusMap.addDocStatus(primLocalIncompleteRecord.getIdentifier().getIdentifier(), primDocOcc, RecordStatusMap2.LOCALLY_INCOMPLETE_ID);
    									secLocalIncompleteRecord.detachDocumentInstance(secLocalIncompInstance);
    	    							recordStatusMap.removeDocStatus(secLocalIncompleteRecord.getIdentifier().getIdentifier(), secDocOcc);
    									saveSecondaryLocalIncompRecord = true;
	    								savePrimaryLocalIncompRecord = true;
    								}
    								
    							}
    							
    						}
    						else{
    							
    							//Incomplete primary instance, either complete or no secondary instance
    							
    							if ( null != secLocalInstance || null != secRemoteInstance ){
    								//CASE 6 Incomplete instance in primary record and complete instance in
    								//secondary record. Either remove the incomplete primary instance and
    								//do a reverse copy from the complete secondary instance, or just 
    								//remove the complete secondary instance
    								if ( incompleteReverseCopy ){
    									//remove incomplete instance in primary
    									if ( null != primLocalIncompInstance ){
	    									LOG.info("Local incomplete primary (removing)");
    										primLocalIncompleteRecord.detachDocumentInstance(primLocalIncompInstance);
    										recordStatusMap.removeDocStatus(primLocalIncompleteRecord.getIdentifier().getIdentifier(), primDocOcc);
    										savePrimaryLocalIncompRecord = true;
    									}
    									if ( null != primRemoteIncompInstance ){
	    									LOG.info("Remote incomplete primary (removing)");
    										primRemoteRecord.removeDocumentInstance(primRemoteIncompInstance);
    										recordStatusMap.removeDocStatus(primRemoteRecord.getIdentifier().getIdentifier(), primDocOcc);
    										savePrimaryRemoteRecord = true;
    									}
    									
    									//reverse copy complete record from secondary
	    								if ( null != secRemoteInstance ){
	    									LOG.info("Incomplete primary; remote complete secondary (reverse copy)");
	    									copyDocInstIntoRecord(secRemoteInstance, primDoc, primDocOcc, primRemoteRecord, docsNotCopied);
	    									//check the status of the doc instance - if it is Rejected or Approved we need to reset 
	    									//it to Pending, as review and approve will be driven by the primary instance from this point
	    									docsToResetStatus.add(secRemoteInstance);
	    									docsToSetToPending.add(primRemoteRecord.getDocumentInstance(primDocOcc));
	    									savePrimaryRemoteRecord = true;
	    								}
	    								else if (null != secLocalInstance ){
	    									LOG.info("Incomplete primary; local complete secondary (reverse copy)");
		    								if ( null == primLocalRecord ){
		    									//we need a local record for the primary to add the new document instances
		    									//to, so we have to create one here if it doesn't already exist
		    									primLocalRecord = RecordHelper.constructRecord(primRemoteRecord);
		    								}
		    								savePrimaryLocalRecord = true;
	    									copyDocInstIntoRecord(secLocalInstance, primDoc, primDocOcc, primLocalRecord, docsNotCopied);
	    									recordStatusMap.addDocStatus(primLocalRecord.getIdentifier().getIdentifier(), primDocOcc, RecordStatusMap2.READY_TO_SUBMIT_ID);
	    								}
    									    									
    								}
    								else{
    									//Remove the complete instance in the secondary
    									if ( null != secLocalInstance ){
	    									LOG.info("Incomplete primary; local complete secondary (deleting)");
    										secLocalRecord.detachDocumentInstance(secLocalInstance);
    										recordStatusMap.removeDocStatus(secLocalRecord.getIdentifier().getIdentifier(), secDocOcc);
    										saveSecondaryLocalIncompRecord = true;
    									}
    									if ( null != secRemoteInstance ){
	    									LOG.info("Incomplete primary; remote complete secondary (deleting)");
    										secRemoteRecord.removeDocumentInstance(secRemoteInstance);
    										recordStatusMap.removeDocStatus(secRemoteRecord.getIdentifier().getIdentifier(), secDocOcc);
    										saveSecondaryRemoteRecord = true;
    									}
    								}
    							}
    						}
    					}
					}
				}
			}
		}

    	PersistenceManager.getInstance().saveRecordStatusMap();
    
    	return new DdeCopyResult(docsNotCopied, docsToResetStatus, docsToSetToPending,
    							 savePrimaryRemoteRecord, saveSecondaryRemoteRecord, savePrimaryLocalRecord, 
    							 savePrimaryLocalIncompRecord, saveSecondaryLocalIncompRecord, 
    							 primLocalRecord, primLocalIncompleteRecord);
    }

    /**
     * Do some checks before linking two records to look for any conflicts in their
     * document instances.
     * 
     * @param primLocalRecord Primary record, as stored locally.
     * @param primRemoteRecord Primary record, as stored remotely in the data repository.
     * @param primIncompleteLocalRecord Primary record, the incomplete document instances as stored locally
     * @param secLocalRecord Secondary record, as stored locally.
     * @param secRemoteRecord Secondary record, as stored remotely in the data repository.
     * @param secIncompleteLocalRecord Secondary record, the incomplete document instances as stored locally
     * @return The results of the checks.
     */
	public static DdeCheckResult ddeCheckBeforeCopy(Record primLocalRecord,
			 Record primRemoteRecord,
			 Record primIncompleteLocalRecord,
			 Record secLocalRecord,
			 Record secRemoteRecord,
			 Record secIncompleteLocalRecord){
		boolean noPrimaryYesSeconday = false;
		boolean yesPrimaryYesSecondary = false;
		boolean yesIncompPrimYesSecondary = false;
		boolean noPrimaryYesIncompSecondary = false;
		DataSet primDs = primRemoteRecord.getDataSet();
		DataSet secDs = secLocalRecord.getDataSet();
		for ( int i=0, c=primDs.numDocuments(); i<c; i++ ){
			Document doc = primDs.getDocument(i);
			if ( null != doc.getSecondaryDocIndex() ){
				for ( int j=0, d=doc.numOccurrences(); j<d; j++ ){
					DocumentOccurrence docOcc = doc.getOccurrence(j);
					if ( null != docOcc.getSecondaryOccIndex() ){
						//check for instance of this occurrence in primary record
						boolean primaryInstanceExists = (null != findDocumentInstance(primRemoteRecord, false, false, docOcc, true) || null != findDocumentInstance(primLocalRecord, true, false, docOcc, true));
						boolean incompletePrimInstExists = (null != findDocumentInstance(primRemoteRecord, false, false, docOcc, false) || null != findDocumentInstance(primIncompleteLocalRecord, true, true, docOcc, false));
		
						//check for existence of equivalent occurrence in secondary record(s)
						Document secDoc = secDs.getDocument(doc.getSecondaryDocIndex().intValue());
						DocumentOccurrence secDocOcc = secDoc.getOccurrence(docOcc.getSecondaryOccIndex().intValue());
						boolean secondaryInstanceExists = ( null != findDocumentInstance(secRemoteRecord, false, false, secDocOcc, true) || null != findDocumentInstance(secLocalRecord, true, false, secDocOcc, true) );
						boolean incompleteSecInstExists = ( null != findDocumentInstance(secRemoteRecord, false, false, secDocOcc, false) || null != findDocumentInstance(secIncompleteLocalRecord, true, true, secDocOcc, false));
		
						if ( primaryInstanceExists && secondaryInstanceExists ){
							yesPrimaryYesSecondary = true;
						}
						else if ( !primaryInstanceExists && !incompletePrimInstExists && secondaryInstanceExists ){
							noPrimaryYesSeconday = true;
						}
						else if ( incompletePrimInstExists && secondaryInstanceExists ){
							yesIncompPrimYesSecondary = true;
						}
						else if (!primaryInstanceExists && !incompletePrimInstExists && incompleteSecInstExists ){
							noPrimaryYesIncompSecondary = true;
						}
					}
				}
			}
		}
		return new DdeCheckResult(noPrimaryYesSeconday, yesPrimaryYesSecondary, noPrimaryYesIncompSecondary, yesIncompPrimYesSecondary);
	}

	/**
     * Try to find a document instance for a given document occurrence in a record.
     * <p>
     * If an instance cannot be found then <code>null</code> is returned.
     * 
     * @param rec The record to search for the document instance in.
     * @param localRecord True if the record is stored locally, False if it is remote.
     * @param incompleteRecord True if the record only containf incomplete docs, False otherwise.
     * @param docOcc The document occurrence an instance of which we are searching for.
     * @param complete True if we are searching for a complete instance, False for an 
     * incomplete instance.
     * @return The document instance if found, otherwise <code>null</code>. 
     */
	private static DocumentInstance findDocumentInstance(Record rec, boolean localRecord, boolean incompleteRecord, DocumentOccurrence docOcc, boolean complete){
    	if ( null == rec ){
    		return null;
    	}
    	else{
    		DocumentInstance docInst = rec.getDocumentInstance(docOcc);
    		if ( null != docInst ){
    			if ( localRecord ) {
    				if ( incompleteRecord && !complete ){
	    				//the record is local and only contains incomplete doc insts, and that is what
	    				//we are looking for 
	    				return docInst;
	    			}
	    			if ( !incompleteRecord && complete ){
	    				//the record is local and only contains complete doc insts, and that is what
	    				//we are looking for 
	    				return docInst;
	    			}
	    			return null;
    			}
    			else{
	    			//reaching here implies we are dealing with a remote record, so we
    				//can rely upon the status attached to the doc inst being correct
	    			String status = docInst.getStatus().getShortName();
	    			if ( !complete && status.equals(Status.DOC_STATUS_INCOMPLETE) ){
	    				return docInst;
	    			}
	    			if ( complete && !status.equals(Status.DOC_STATUS_INCOMPLETE) ){
	    				return docInst;
	    			}
	    			return null;
    			}
    		}
    		return null;
    	}
    }
	
	/**
	 * Copy the data from a document instance into a new document
	 * instance for the given document and document occurrence, and add the 
	 * new document instance to the given record.
	 * <p>
	 * The document being copied to is assumed to have exactly the same 
	 * structure as the cocument for the document instance being copied
	 * from.
	 * <p>
	 * If it is not possible to copy the document instance due to
	 * insufficient consent in the record being copied to then the name
	 * of the document occurrence is added to the list in the arguments.
	 * 
	 * @param primDocInst The document instance being copied from.
	 * @param secDoc The document being copied to.
	 * @param secDocOcc The document occurrence being copied to.
	 * @param secRecord The record being copied to.
	 * @param docsNotCopied List to add name of document occurrence if it
	 * was not possible to do the copy.
	 */
	private static void copyDocInstIntoRecord(DocumentInstance primDocInst,
			   Document secDoc,
			   DocumentOccurrence secDocOcc,
			   Record secRecord,
			   List<String> docsNotCopied ){
    	DocumentInstance secDocInst = secDoc.generateInstance(secDocOcc, true);
    	if ( secRecord.checkConsent(secDocInst) ){
    		secRecord.addDocumentInstance(secDocInst);
    		primDocInst.ddeCopy(secDocInst);
    	}
    	else{
    		docsNotCopied.add(secDocOcc.getCombinedDisplayText());
    	}    						
    }

	/**
	 * Copy the data from a document instance into an existing document
	 * instance.
	 * <p>
	 * The structure of the underlying documents is assumed to be identical.
	 * 
	 * @param primDocInst The document instance beign copied from.
	 * @param secDocInst The document instance being copied to.
	 */
	private static void copyDocInstIntoRecord(DocumentInstance primDocInst,
			   DocumentInstance secDocInst){
    	primDocInst.ddeCopy(secDocInst);
    }

}
