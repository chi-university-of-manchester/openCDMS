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

package org.psygrid.data.repository.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Session;
import org.psygrid.data.model.dto.IdentifierDTO;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.dao.SpecialDAO;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class SpecialDAOHibernate extends HibernateDaoSupport implements SpecialDAO {

    public void fixNullUnitsInOutlookData() throws DAOException {

        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                
                List result = session.createQuery("from BasicResponse r where r.record.identifier.projectPrefix = ? " +
                                                  "and r.theValue.unit is null and r.entry.units.size > 0")
                                     .setString(0, "OLK")
                                     .list();
                
                for ( Object obj: result ){
                    BasicResponse br = (BasicResponse)obj;
                    List units = session.createQuery("select be.units from BasicEntry be where be.id=?")
                                        .setLong(0, br.getEntry().getId())
                                        .list();
                    br.getValue().setUnit((Unit)units.get(0));
                    session.saveOrUpdate(br);
                }
                
                return null;
            }
        };
        
        getHibernateTemplate().execute(callback);
    
    }

    public void setRecordDocumentNumbers() throws DAOException {

        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                
                List records = session.createQuery("from Record r where r.numIncompleteDocs = 0 " +
                                                   "and r.numPendingDocs = 0 " +
                                                   "and r.numRejectedDocs = 0 " +
                                                   "and r.numApprovedDocs = 0")
                                      .list();
                
                for ( Object obj: records ){
                    Record record = (Record)obj;
                    Long numIncomplete = 
                        (Long)session.createQuery(
                                "select count(*) from DocumentInstance di " +
                                "where di.record.id=? " +
                                "and di.status.shortName='Incomplete'")
                                .setLong(0, record.getId())
                                .uniqueResult();
                    //TODO uncomment these fourlines below if we ever need to go
                    //back to using status counters for performance reasons
                    //record.setNumIncompleteDocs(numIncomplete.intValue());
                    Long numPending = 
                        (Long)session.createQuery(
                                "select count(*) from DocumentInstance di " +
                                "where di.record.id=? " +
                                "and di.status.shortName='Pending'")
                                .setLong(0, record.getId())
                                .uniqueResult();
                    //record.setNumPendingDocs(numPending.intValue());
                    Long numRejected = 
                        (Long)session.createQuery(
                                "select count(*) from DocumentInstance di " +
                                "where di.record.id=? " +
                                "and di.status.shortName='Rejected'")
                                .setLong(0, record.getId())
                                .uniqueResult();
                    //record.setNumRejectedDocs(numRejected.intValue());
                    Long numApproved = 
                        (Long)session.createQuery(
                                "select count(*) from DocumentInstance di " +
                                "where di.record.id=? " +
                                "and di.status.shortName='Approved'")
                                .setLong(0, record.getId())
                                .uniqueResult();
                    //record.setNumApprovedDocs(numApproved.intValue());
                    session.saveOrUpdate(record);
                }
                
                return null;
            }
        };
        
        getHibernateTemplate().execute(callback);
    }

    public void fixNEdenRecordCreatedBy() throws DAOException {
        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                
                List records = session.createQuery("from Record r where r.dataSet.projectCode='NED' " +
                                                   "and r.createdBy='CN=John Ainsworth, OU=users, O=psygrid, C=uk'")
                                      .list();
                DocumentOccurrence docOcc = null;
                for ( Object o: records ){
                    Record r = (Record)o;
                    if ( null == docOcc ){
                        docOcc = r.getDataSet().getDocument(0).getOccurrence(0);
                    }
                    DocumentInstance di = (DocumentInstance)r.getDocumentInstance(docOcc);
                    if ( null != di ){
                        r.setCreatedBy(di.getCreatedBy());
                    }
                    session.saveOrUpdate(r);
                }
                
                return null;
            }
        };
        
        getHibernateTemplate().execute(callback);
    }

	public int getNumberOfRecordsInGroup(final String project, final String group) throws DAOException {
        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
            	Long count = (Long)session.createQuery("select count(*) from Record r where r.identifier.projectPrefix=? and r.identifier.groupPrefix=?")
            		   							.setString(0, project)
            		   							.setString(1, group)
            		   							.uniqueResult();
            	return count;
            }
        };
        Long result = (Long)getHibernateTemplate().execute(callback);
        return result.intValue();
	}

	public Map<String, String> updateIdentifiersforProjectAndGroup(final String project, final String group, final IdentifierDTO[] ids) throws DAOException {
        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
            	List records = session.createQuery("from Record r where r.identifier.projectPrefix=? and r.identifier.groupPrefix=? order by r.identifier.suffix")
            						  .setString(0, project)
            						  .setString(1, group)
            						  .list();
            	Map<String, String> idMap = new LinkedHashMap<String, String>();
            	for ( int i=0; i<records.size(); i++ ){
            		Record r = (Record)records.get(i);
            		idMap.put(r.getIdentifier().getIdentifier(), ids[i].getIdentifier());
            		r.setIdentifier(ids[i].toHibernate());
            		session.saveOrUpdate(r);
            	}
            	return idMap;
            }
        };
        return (Map<String, String>)getHibernateTemplate().execute(callback);
	}
	 
	public Map<String, String> updateIdentifier(final String oldIdentifier, final IdentifierDTO newIdentifier) throws DAOException {
        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                Record r = (Record)session.createQuery("from Record r where r.identifier.identifier=?")
                			.setString(0, oldIdentifier)
                			.uniqueResult();
            	Map<String, String> idMap = new LinkedHashMap<String, String>();
        		idMap.put(r.getIdentifier().getIdentifier(), newIdentifier.getIdentifier());
        		r.setIdentifier(newIdentifier.toHibernate());
        		//update consent & status modified time to ensure user map is refreshed
        		r.setStatusModified(new Date(System.currentTimeMillis()));
        		r.setConsentModified(new Date(System.currentTimeMillis()));
        		session.saveOrUpdate(r);
            	return idMap;
            }
        };
        return (Map<String, String>)getHibernateTemplate().execute(callback);
	}

	public void fixED2RecordsForBug823(final String username) throws DAOException {
        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
            	
            	System.out.println("Initializing...");
            	
            	DataSet ds = (DataSet)session.createQuery("from DataSet ds where ds.projectCode=?")
            								 .setString(0, "ED2")
            								 .uniqueResult();
            	if ( null == ds ){
            		return new DAOException("No dataset found for project=ED2");
            	}
            	
            	//EPQv3
            	Document epqv3 = ds.getDocument(11);
            	if ( !"EPQv3".equals(epqv3.getName()) ){
            		return new DAOException("This is not the EPQv3 document, it is "+epqv3.getName());
            	}
            	Section epqv3Sec = (Section)epqv3.getSection(2);
            	if ( !"Your employment section".equals(epqv3Sec.getName()) ){
            		return new DAOException("This is not the Your employment section, it is "+epqv3Sec.getName());
            	}
            	SectionOccurrence epqv3SecOcc = (SectionOccurrence)epqv3Sec.getOccurrence(0);
            	NumericEntry q10 = (NumericEntry)epqv3.getEntry(19);
            	if ( !"QC10".equals(q10.getName()) ){
            		return new DAOException("This is not the QC10 entry, it is "+q10.getName());
            	}
            	
            	//CAARMS
            	Map<SectionOccurrence, List<OptionEntry>> caarmOpEntMap = new HashMap<SectionOccurrence, List<OptionEntry>>();
            	Map<SectionOccurrence, NumericEntry> caarmNumEntMap = new HashMap<SectionOccurrence, NumericEntry>();
            	Document caarms = ds.getDocument(1);
            	if ( !"CAARMS with GAF".equals(caarms.getName()) ){
            		return new DAOException("This is not the CAARMS with GAF document, it is "+caarms.getName());
            	}
            	//CAARMS Unusual Thought Context section
            	Section caarmsUtSec = (Section)caarms.getSection(1);
            	if ( !"Unusual Thought Section".equals(caarmsUtSec.getName()) ){
            		return new DAOException("This is not the Unusual Thought Section, it is "+caarmsUtSec.getName());
            	}
            	SectionOccurrence caarmsUtSecOcc = (SectionOccurrence)caarmsUtSec.getOccurrence(0);
            	List<OptionEntry> utOpEnts = new ArrayList<OptionEntry>();
            	caarmOpEntMap.put(caarmsUtSecOcc, utOpEnts);
            	OptionEntry utcgrs = (OptionEntry)caarms.getEntry(3);
            	if ( !"Unusual Thought Content Global Rating Scale".equals(utcgrs.getName()) ){
            		return new DAOException("This is not the Unusual Thought Content Global Rating Scale entry, it is "+utcgrs.getName());
            	}
            	utOpEnts.add(utcgrs);
            	OptionEntry utcfd = (OptionEntry)caarms.getEntry(6);
            	if ( !"Unusual Thought Content Frequency and Duration".equals(utcfd.getName()) ){
            		return new DAOException("This is not the Unusual Thought Content Frequency and Duration entry, it is "+utcfd.getName());
            	}
            	utOpEnts.add(utcfd);
            	OptionEntry utcpos = (OptionEntry)caarms.getEntry(7);
            	if ( !"Unusual Thought Content Pattern of Symptoms".equals(utcpos.getName()) ){
            		return new DAOException("This is not the Unusual Thought Content Pattern of Symptoms entry, it is "+utcpos.getName());
            	}
            	utOpEnts.add(utcpos);
            	NumericEntry utcLevelOfDistress = (NumericEntry)caarms.getEntry(8);
            	if ( !"Unusual Thought Content Level of Distress".equals(utcLevelOfDistress.getName()) ){
            		return new DAOException("This is not the Unusual Thought Content Level of Distress entry, it is "+utcLevelOfDistress.getName());
            	}
            	caarmNumEntMap.put(caarmsUtSecOcc, utcLevelOfDistress);
            	//CAARMS Non Bizarre Ideas section
            	Section caarmsNbiSec = (Section)caarms.getSection(2);
            	if ( !"Non-Bizarre Ideas Section".equals(caarmsNbiSec.getName()) ){
            		return new DAOException("This is not the Non-Bizarre Ideas Section, it is "+caarmsNbiSec.getName());
            	}
            	SectionOccurrence caarmsNbiSecOcc = (SectionOccurrence)caarmsNbiSec.getOccurrence(0);
            	List<OptionEntry> nbiOpEnts = new ArrayList<OptionEntry>();
            	caarmOpEntMap.put(caarmsNbiSecOcc, nbiOpEnts);
            	OptionEntry nbigrs = (OptionEntry)caarms.getEntry(9);
            	if ( !"Non-Bizarre Ideas Global Rating Scale".equals(nbigrs.getName()) ){
            		return new DAOException("This is not the Non-Bizarre Ideas Global Rating Scale entry, it is "+nbigrs.getName());
            	}
            	nbiOpEnts.add(nbigrs);
            	OptionEntry nbifd = (OptionEntry)caarms.getEntry(12);
            	if ( !"Non-Bizarre Ideas Frequency and Duration".equals(nbifd.getName()) ){
            		return new DAOException("This is not the Non-Bizarre Ideas Frequency and Duration entry, it is "+nbifd.getName());
            	}
            	nbiOpEnts.add(nbifd);
            	OptionEntry nbipos = (OptionEntry)caarms.getEntry(13);
            	if ( !"Non-Bizarre Ideas Pattern of Symptoms".equals(nbipos.getName()) ){
            		return new DAOException("This is not the Non-Bizarre Ideas Pattern of Symptoms entry, it is "+nbipos.getName());
            	}
            	nbiOpEnts.add(nbipos);
            	NumericEntry nbiLevelOfDistress = (NumericEntry)caarms.getEntry(14);
            	if ( !"Non-Bizarre Ideas Level of Distress".equals(nbiLevelOfDistress.getName()) ){
            		return new DAOException("This is not the Non-Bizarre Ideas Level of Distress entry, it is "+nbiLevelOfDistress.getName());
            	}
            	caarmNumEntMap.put(caarmsNbiSecOcc, nbiLevelOfDistress);
            	//CAARMS Perceptual Abnormalities section
            	Section caarmsPaSec = (Section)caarms.getSection(3);
            	if ( !"Perceptual Abnormalities Section".equals(caarmsPaSec.getName()) ){
            		return new DAOException("This is not the Perceptual Abnormalities Section, it is "+caarmsPaSec.getName());
            	}
            	SectionOccurrence caarmsPaSecOcc = (SectionOccurrence)caarmsPaSec.getOccurrence(0);
            	List<OptionEntry> paOpEnts = new ArrayList<OptionEntry>();
            	caarmOpEntMap.put(caarmsPaSecOcc, paOpEnts);
            	OptionEntry perabgrs = (OptionEntry)caarms.getEntry(15);
            	if ( !"Perceptual Abnormalities Global Rating Scale".equals(perabgrs.getName()) ){
            		return new DAOException("This is not the Perceptual Abnormalities Global Rating Scale entry, it is "+perabgrs.getName());
            	}
            	paOpEnts.add(perabgrs);
            	OptionEntry perabfd = (OptionEntry)caarms.getEntry(18);
            	if ( !"Perceptual Abnormalities Frequency and Duration".equals(perabfd.getName()) ){
            		return new DAOException("This is not the Perceptual Abnormalities Frequency and Duration entry, it is "+perabfd.getName());
            	}
            	paOpEnts.add(perabfd);
            	OptionEntry perabpos = (OptionEntry)caarms.getEntry(19);
            	if ( !"Perceptual Abnormalities Pattern of Symptoms".equals(perabpos.getName()) ){
            		return new DAOException("This is not the Perceptual Abnormalities Pattern of Symptoms entry, it is "+perabpos.getName());
            	}
            	paOpEnts.add(perabpos);
            	NumericEntry perabLevelOfDistress = (NumericEntry)caarms.getEntry(20);
            	if ( !"Perceptual Abnormalities Level of Distress".equals(perabLevelOfDistress.getName()) ){
            		return new DAOException("This is not the Perceptual Abnormalities Level of Distress entry, it is "+perabLevelOfDistress.getName());
            	}
            	caarmNumEntMap.put(caarmsPaSecOcc, perabLevelOfDistress);
            	//CAARMS Disorganised Speech Section
            	Section caarmsDsSec = (Section)caarms.getSection(4);
            	if ( !"Disorganised Speech Section".equals(caarmsDsSec.getName()) ){
            		return new DAOException("This is not the Disorganised Speech Section, it is "+caarmsDsSec.getName());
            	}
            	SectionOccurrence caarmsDsSecOcc = (SectionOccurrence)caarmsDsSec.getOccurrence(0);
            	List<OptionEntry> dsOpEnts = new ArrayList<OptionEntry>();
            	caarmOpEntMap.put(caarmsDsSecOcc, dsOpEnts);
            	OptionEntry disspgrs = (OptionEntry)caarms.getEntry(21);
            	if ( !"Disorganised Speech Global Rating Scale".equals(disspgrs.getName()) ){
            		return new DAOException("This is not the Disorganised Speech Global Rating Scale entry, it is "+disspgrs.getName());
            	}
            	dsOpEnts.add(disspgrs);
            	OptionEntry disspfd = (OptionEntry)caarms.getEntry(24);
            	if ( !"Disorganised Speech Frequency and Duration".equals(disspfd.getName()) ){
            		return new DAOException("This is not the Disorganised Speech Frequency and Duration entry, it is "+disspfd.getName());
            	}
            	dsOpEnts.add(disspfd);
            	OptionEntry dissppos = (OptionEntry)caarms.getEntry(25);
            	if ( !"Disorganised Speech Pattern of Symptoms".equals(dissppos.getName()) ){
            		return new DAOException("This is not the Disorganised Speech Pattern of Symptoms entry, it is "+dissppos.getName());
            	}
            	dsOpEnts.add(dissppos);
            	NumericEntry disspLevelOfDistress = (NumericEntry)caarms.getEntry(26);
            	if ( !"Disorganised Speech Level of Distress".equals(disspLevelOfDistress.getName()) ){
            		return new DAOException("This is not the Disorganised Speech Level of Distress entry, it is "+disspLevelOfDistress.getName());
            	}
            	caarmNumEntMap.put(caarmsDsSecOcc, disspLevelOfDistress);
            	//CAARMS Agressive/Dangerous Behaviour Section
            	Section caarmsAdbSec = (Section)caarms.getSection(5);
            	if ( !"Agressive/Dangerous Behaviour Section".equals(caarmsAdbSec.getName()) ){
            		return new DAOException("This is not the Agressive/Dangerous Behaviour Section, it is "+caarmsAdbSec.getName());
            	}
            	SectionOccurrence caarmsAdbSecOcc = (SectionOccurrence)caarmsAdbSec.getOccurrence(0);
            	List<OptionEntry> adbOpEnts = new ArrayList<OptionEntry>();
            	caarmOpEntMap.put(caarmsAdbSecOcc, adbOpEnts);
            	OptionEntry agdabegrs = (OptionEntry)caarms.getEntry(27);
            	if ( !"Agressive/Dangerous Behaviour Global Rating Scale".equals(agdabegrs.getName()) ){
            		return new DAOException("This is not the Agressive/Dangerous Behaviour Global Rating Scale entry, it is "+agdabegrs.getName());
            	}
            	adbOpEnts.add(agdabegrs);
            	OptionEntry agdabefd = (OptionEntry)caarms.getEntry(30);
            	if ( !"Agressive/Dangerous Behaviour Frequency and Duration".equals(agdabefd.getName()) ){
            		return new DAOException("This is not the Agressive/Dangerous Behaviour Frequency and Duration entry, it is "+agdabefd.getName());
            	}
            	adbOpEnts.add(agdabefd);
            	OptionEntry agdabepos = (OptionEntry)caarms.getEntry(31);
            	if ( !"Agressive/Dangerous Behaviour Pattern of Symptoms".equals(agdabepos.getName()) ){
            		return new DAOException("This is not the Agressive/Dangerous Behaviour Pattern of Symptoms entry, it is "+agdabepos.getName());
            	}
            	adbOpEnts.add(agdabepos);
            	//CAARMS Suicidality Section
            	Section caarmsSuSec = (Section)caarms.getSection(6);
            	if ( !"Suicidality Section".equals(caarmsSuSec.getName()) ){
            		return new DAOException("This is not the Suicidality Section, it is "+caarmsSuSec.getName());
            	}
            	SectionOccurrence caarmsSuSecOcc = (SectionOccurrence)caarmsSuSec.getOccurrence(0);
            	List<OptionEntry> suOpEnts = new ArrayList<OptionEntry>();
            	caarmOpEntMap.put(caarmsSuSecOcc, suOpEnts);
            	OptionEntry suicidalitygrs = (OptionEntry)caarms.getEntry(32);
            	if ( !"Suicidality Global Rating Scale".equals(suicidalitygrs.getName()) ){
            		return new DAOException("This is not the Suicidality Global Rating Scale entry, it is "+suicidalitygrs.getName());
            	}
            	suOpEnts.add(suicidalitygrs);
            	OptionEntry suicidalityfd = (OptionEntry)caarms.getEntry(35);
            	if ( !"Suicidality Frequency and Duration".equals(suicidalityfd.getName()) ){
            		return new DAOException("This is not the Suicidality Frequency and Duration entry, it is "+suicidalityfd.getName());
            	}
            	suOpEnts.add(suicidalityfd);
            	OptionEntry suicidalitypos = (OptionEntry)caarms.getEntry(36);
            	if ( !"Suicidality Pattern of Symptoms".equals(suicidalitypos.getName()) ){
            		return new DAOException("This is not the Suicidality Pattern of Symptoms entry, it is "+suicidalitypos.getName());
            	}
            	suOpEnts.add(suicidalitypos);
            			
            	Iterator records = session.createQuery("from Record r where r.dataSet.projectCode=?")
            						   	  .setString(0, "ED2")
            						   	  .iterate();

            	while ( records.hasNext() ){
            		Record r = (Record)records.next();
            		System.out.println("Processing record "+r.getIdentifier().getIdentifier());
            		//find instances for epqv3
            		List<DocumentInstance> epqv3Insts = r.getDocumentInstances(epqv3);
            		for ( DocumentInstance epqv3Inst: epqv3Insts ){
            			//find responses for q10
            			BasicResponse resp = (BasicResponse)epqv3Inst.getResponse(q10, epqv3SecOcc);
            			if ( null != resp ){
            				NumericValue val = (NumericValue)resp.getValue();
            				if ( null == val.getValue() ){
            					val.setValue(q10.getDefaultValue());
            				}
            			}
            		}
            		
            		//find instances for Caarms
            		List<DocumentInstance> caarmsInsts = r.getDocumentInstances(caarms);
            		for ( DocumentInstance caarmsInst: caarmsInsts ){
            			//deal with option entries
            			for ( Entry<SectionOccurrence, List<OptionEntry>> e: caarmOpEntMap.entrySet() ){
            				SectionOccurrence so = e.getKey();
            				for ( OptionEntry oe: e.getValue() ){
                    			BasicResponse resp = (BasicResponse)caarmsInst.getResponse(oe, so);
                    			if ( null != resp ){
                    				OptionValue val = (OptionValue)resp.getValue();
                    				if ( null == val.getValue() ){
                    					val.setValue(oe.getDefaultValue());
                    				}
                    			}
            				}
            			}
            			//deal with numeric entries
            			for ( Entry<SectionOccurrence, NumericEntry> e: caarmNumEntMap.entrySet() ){
            				SectionOccurrence so = e.getKey();
            				NumericEntry ne = e.getValue();
                			BasicResponse resp = (BasicResponse)caarmsInst.getResponse(ne, so);
                			if ( null != resp ){
                				NumericValue val = (NumericValue)resp.getValue();
                				if ( null == val.getValue() ){
                					val.setValue(ne.getDefaultValue());
                				}
                			}
            			}
            		}
            		
            		session.saveOrUpdate(r);
            		
            	}
            	
            	return null;
            }
        };

		EntityInterceptor.setUserName(username);
        Object result = getHibernateTemplate().execute(callback);
        if ( null != result ){
        	throw (DAOException)result;
        }
	}

	public Map<Long, ChangeHistory> createRecordCreatedChangeHistoryItems(){
        List result = getHibernateTemplate().find("from Record");
        Map<Long, ChangeHistory> map = new HashMap<Long, ChangeHistory>();
        for ( int i=0, c=result.size(); i<c; i++ ){
        	Record r = (Record)result.get(i);
        	map.put(r.getId(), new ChangeHistory(r.getCreatedBy(), ChangeHistory.SAVED, r.getCreated()));
        }
        return map;
	}
	
	public void addRecordCreatedChangeHistory(Long recordId, ChangeHistory history){
		Record r = (Record)getHibernateTemplate().get(Record.class, recordId);
		r.getHistory().add(history);
		EntityInterceptor.setUserName(history.getUser());
		getHibernateTemplate().save(r);
	}

	public void addCompleteStatusToAllDatasets() {
        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
            	HibernateFactory factory = new HibernateFactory();
            	List result = session.createQuery("from DataSet").list();
            	for ( Object o: result ){
            		DataSet ds = (DataSet)o;
            		System.out.println("Processing "+ds.getProjectCode());
            		for ( Document doc: ds.getDocuments() ){
	        			Status incomplete = doc.getStatus(0);
	        			if ( !"Incomplete".equals(incomplete.getShortName()) ){
	        				throw new RuntimeException("This is not the Incomplete status - it is "+incomplete.getShortName());
	        			}
	        			Status pending = doc.getStatus(1);
	        			if ( !"Pending".equals(pending.getShortName()) ){
	        				throw new RuntimeException("This is not the Pending status - it is "+pending.getShortName());
	        			}
	        			Status complete = factory.createStatus(Status.DOC_STATUS_COMPLETE, "Complete", 4);
	        			doc.addStatus(complete);
	        			incomplete.addStatusTransition(complete);
	        			complete.addStatusTransition(incomplete);
	        			complete.addStatusTransition(pending);
            		}
            		ds.setDateModified(new Date());
        			session.saveOrUpdate(ds);
            	}
            	return null;
            }
        };
		getHibernateTemplate().execute(callback);
	}

	public void changeAddressToControlledWorkflow() {
        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
            	List docInsts = session.createQuery("from DocumentInstance di where di.record.dataSet.projectCode=?")
            						  .setString(0, "ADD")
            						  .list();
            	System.out.println("Retrieved "+docInsts.size()+" docInsts");
            	for ( int i=0, c=docInsts.size(); i<c; i++ ){
            		DocumentInstance docInst = (DocumentInstance)docInsts.get(i);
            		System.out.println("Status = "+docInst.getStatus().getShortName());
            		if ( docInst.getStatus().getShortName().equals(Status.DOC_STATUS_APPROVED) ||
            				docInst.getStatus().getShortName().equals(Status.DOC_STATUS_PENDING) ){
            			Document doc = docInst.getOccurrence().getDocument();
            			Status  controlled = null;
            			for ( int j=0, d=doc.numStatus(); j<d; j++ ){
            				Status s  = doc.getStatus(j);
            				if ( s.getShortName().equals(Status.DOC_STATUS_CONTROLLED) ){
            					controlled = s;
            					break;
            				}
            			}
            			System.out.println("Changing status to "+controlled.getShortName());
            			docInst.changeStatus(controlled);
            			docInst.getRecord().setStatusModified(new Date());
            			session.saveOrUpdate(docInst);
            		}
            	}
            	return null;
            }
        };
        
		EntityInterceptor.setUserName("cn=Address CPM,ou=users,o=psygrid,c=uk");
		getHibernateTemplate().execute(callback);

	}
	
	
}
