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

import java.io.OutputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axis.client.Call;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.export.XMLExporter;
import org.psygrid.data.export.XMLExporterWithExportSecurity;
import org.psygrid.data.export.hibernate.ExportDocument;
import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.data.export.hibernate.ExportSecurityActionMap;
import org.psygrid.data.model.IPersistent;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.model.dto.extra.ConsentResult;
import org.psygrid.data.model.dto.extra.ConsentStatusResult;
import org.psygrid.data.model.dto.extra.DocInstChangeHistoryResult;
import org.psygrid.data.model.dto.extra.GroupSummary;
import org.psygrid.data.model.dto.extra.IdentifierData;
import org.psygrid.data.model.dto.extra.ProvenanceForChangeResult;
import org.psygrid.data.model.dto.extra.RecordChangeHistoryResult;
import org.psygrid.data.model.dto.extra.SearchRecordChangeHistoryResult;
import org.psygrid.data.model.dto.extra.StatusResult;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.dao.DuplicateDocumentsException;
import org.psygrid.data.repository.dao.NoConsentException;
import org.psygrid.data.repository.dao.NoDatasetException;
import org.psygrid.data.repository.dao.ObjectOutOfDateException;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.psygrid.data.repository.dao.UnknownIdentifierException;
import org.psygrid.data.repository.transformer.TransformerClient;
import org.psygrid.data.repository.transformer.TransformerException;
import org.psygrid.data.utils.esl.EslException;
import org.psygrid.data.utils.esl.EslNoSubjectException;
import org.psygrid.data.utils.esl.IRemoteClient;
import org.psygrid.data.utils.security.DocumentSecurityHelper;
import org.psygrid.data.utils.wrappers.AAQCWrapper;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class RepositoryDAOHibernate extends HibernateDaoSupport implements RepositoryDAO {
    
	private static Log sLog = LogFactory.getLog(RepositoryDAOHibernate.class);
    
	/**
	 * 
	 * @author williamvance
	 */
	public enum IdentifierType{
		ProjectCode, //data specified by project code (string)
		DataSetId;   //data specified by dataSetId (long)
	}

	/**
	 * 
	 * @author williamvance
	 * This simple class is present because the dataset to be operated on can be specified by either
	 * projectCode (String) or dataSetId (long). The stores whichever id as a String (in member variable 'id')
	 * and also records the type.
	 * Therefore users of this class must query the id type and in the case of it being of type DataSetId, the
	 * string version must be converted to a type long before it can be used.
	 */
	public class DataIdentifier{
		private String id;
		private IdentifierType idType;
		public DataIdentifier(String id, IdentifierType idType){
			this.id = id;
			this.idType = idType;
		}

		public IdentifierType getDataIdentifierType(){
			return idType;
		}

		public String getDataIdentifier(){
			return id;
		}
	}

	/**
	 * The role defined by the attribute authority for users
	 * who are directly involved in data collection
	 */
	private String officerRole;

	/**
	 * The role defined by the attribute authority for users
	 * who are managing data collection
	 */
	private String managerRole;

	/**
	 * The email address of the system administrator
	 */
	private String sysAdminEmail;

	/**
	 * If false, emails for docum,ent status changes are not actually
	 * sent, but are just put in the system logs for debugging purposes.
	 */
	private boolean sendMails;

	private EntityInterceptor interceptor;

	/**
	 * Mail sender bean.
	 */
	private JavaMailSender mailSender;

	/**
	 * Attribute authority query client
	 */
	private AAQCWrapper aaqc;

	/**
	 * Remote client for accessing the ESL
	 */
	private IRemoteClient eslClient;
	
	/**
	 * Wired in by the application context.
	 */
	private XMLExporter exporter = null;
	
	
    /**
	 * Wired in by the application context.
	 */
	public void setExporter(XMLExporter exporter) {
		this.exporter = exporter;
	}

	public IPersistent getPersistent(Long persistentId) throws DAOException {
        Persistent p = (Persistent)getHibernateTemplate().get(Persistent.class, persistentId);
        if ( null == p ){
            throw new DAOException("No Persistent exists in the repository for id = "+persistentId);
        }
        return p;
    }
    
    public boolean doesObjectExist(String objectName, Long objectId) throws DAOException {
    	final String query = "select id from "+objectName+" where id=?";
    	
    	Iterator it = (Iterator)getHibernateTemplate().find(query, objectId).iterator();
    	try {
    		return it.hasNext();
    	}
    	catch (NullPointerException npe) {
    		//throw DAOException("");
    		return false;
    	} 	
    }
        
    public BinaryData getBinaryData(Long binaryObjectId) throws DAOException {
        List results = getHibernateTemplate().find("select bo.data from BinaryObject bo where bo.id = ?",binaryObjectId);
        BinaryData bd = null;
        if ( 0 == results.size() ){
            throw new DAOException("No binary object exists for unique identifier "+binaryObjectId);
        }
        else if ( 1 < results.size()){
            throw new DAOException("Multiple binary objects exist for unique identifier "+binaryObjectId);
        }
        else{
            bd = (BinaryData)results.get(0);
        }
        return bd;
    }

    // Dataset related methods
    
    public org.psygrid.data.model.dto.DataSetDTO getDataSet(final Long dataSetId) throws DAOException {

        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                DataSet ds = (DataSet)session.createCriteria(DataSet.class)
                                             .add(Restrictions.idEq(dataSetId))
                                             .uniqueResult();
                
                org.psygrid.data.model.dto.DataSetDTO dtoDS = null;
                if ( null != ds ){
                    dtoDS = ds.toDTO(RetrieveDepth.DS_NO_BINARY);
                    // Need to clear the hibernate session as RepositoryServiceImpl.patchDataset()
                    // saves the new dataset with the same id. 
                    session.clear();
                }
                return dtoDS;
            }
        };
        
        org.psygrid.data.model.dto.DataSetDTO ds = (org.psygrid.data.model.dto.DataSetDTO)getHibernateTemplate().execute(callback);
        if ( null == ds ){
            throw new DAOException("No DataSet exists in the repository for id = "+dataSetId);
        }
        return ds;
    }
    

    public org.psygrid.data.model.dto.DataSetDTO[] getDataSets() {

        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                List dataSets = session.createQuery("from DataSet")
                                       .list();
                org.psygrid.data.model.dto.DataSetDTO[] dtoDataSets = new org.psygrid.data.model.dto.DataSetDTO[dataSets.size()]; 
                for ( int i=0; i<dataSets.size(); i++){
                    DataSet ds = (DataSet)dataSets.get(i);
                    dtoDataSets[i] = ds.toDTO(RetrieveDepth.DS_SUMMARY);
                }
                return dtoDataSets;
            }
        };
 
        return (org.psygrid.data.model.dto.DataSetDTO[])getHibernateTemplate().execute(callback);
    }

    public org.psygrid.data.model.dto.DataSetDTO[] getModifiedDataSets(final Date referenceDate) {

        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                List dataSets = session.createCriteria(DataSet.class)
                                       .add(Restrictions.gt("dateModified", referenceDate))
                                       .list();
                org.psygrid.data.model.dto.DataSetDTO[] dtoDataSets = new org.psygrid.data.model.dto.DataSetDTO[dataSets.size()]; 
                for ( int i=0; i<dataSets.size(); i++){
                    DataSet ds = (DataSet)dataSets.get(i);
                    dtoDataSets[i] = ds.toDTO(RetrieveDepth.DS_SUMMARY);
                }
                return dtoDataSets;
            }
        };
 
        return (org.psygrid.data.model.dto.DataSetDTO[])getHibernateTemplate().execute(callback);
    }

    public void publishDataSet(final Long dataSetId) throws DAOException {

        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                DataSet ds = (DataSet)session.get(DataSet.class, dataSetId);
                if ( null == ds ){
                    return new DAOException("No DataSet exists in the repository for id = "+dataSetId);
                }
                if ( ds.isPublished() ){
                    return new DAOException("DataSet is already published");
                }
                if ( null != ds ){
                    ds.publish();
                    session.saveOrUpdate(ds);                    
                }
                return null;
            }
        };
        
        DAOException result = (DAOException)getHibernateTemplate().execute(callback);
        if ( null != result ){
            throw result;
        }
    }

    public void removeDataSet(Long dataSetId) throws DAOException {
        DataSet ds = (DataSet)getHibernateTemplate().get(DataSet.class, dataSetId);
        if ( null == ds ){
            throw new DAOException("No DataSet exists in the repository for id = "+dataSetId);
        }
        if ( ds.isPublished() ){
            throw new DAOException("Cannot remove DataSet from the repository - it has been published");
        }
        getHibernateTemplate().delete(ds);
    }

    public void removePublishedDataSet(Long dataSetId, String dataSetProjectCode) throws DAOException {
    	if (dataSetId == null || dataSetProjectCode == null) {
    		throw new DAOException("Data Set identifier or project code is null.");
    	}
    	DataSet ds = (DataSet)getHibernateTemplate().get(DataSet.class, dataSetId);
        if ( null == ds ){
            throw new DAOException("No DataSet exists in the repository for id = "+dataSetId);
        }
        if (! ds.getProjectCode().equals(dataSetProjectCode)) {
        	throw new DAOException("Project code and unique ID do not match for DataSet id = "+dataSetId);
        }
        
        //remove any identifiers that weren't removed when the DataSet's Records were removed.
        try {
        	removeIdentifiers(dataSetProjectCode);
        }
        catch (DAOException ex) {
        	throw new DAOException("Problem when deleting identifiers for DataSet id " + dataSetId);
        }
        
        getHibernateTemplate().delete(ds);
    }
       
    public Long saveDataSet(org.psygrid.data.model.dto.DataSetDTO dataSet) throws DAOException, ObjectOutOfDateException{
        DataSet ds = dataSet.toHibernate();
        if ( null != ds.getId() ){
            //for existing DataSets, check that it has not been deleted or published
            DataSet storedDataSet = (DataSet)getHibernateTemplate().get(DataSet.class, ds.getId());
            if (null == storedDataSet){
                //data set has been deleted by another session
                throw new ObjectOutOfDateException("Cannot save DataSet - the object is out-of-date");
            }
            if (storedDataSet.isPublished()){
                throw new DAOException("Cannot save DataSet - it has already been published");
            }
            //when transactions are used the same session is used
            //throughout the transaction. Need to evict the "stored"
            //dataset object otherwise an exception will be thrown
            //when trying to saveOrUpdate the dataset due to the existence
            //of an object with the same ID already being in the session
            getHibernateTemplate().evict(storedDataSet);
        }
        try{
            //set the modified date
            ds.setDateModified(new Date());
            
            getHibernateTemplate().saveOrUpdate(ds);
            
            for (Persistent p:ds.getDeletedObjects()){
                getHibernateTemplate().delete(p);
            }
            return ds.getId();
        }
        catch (HibernateOptimisticLockingFailureException ex){
            //Note that this catch block will NEVER be entered if
            //this method is called using Spring declarative transaction
            //management. In that case, the exception is caught in
            //SaveObjectInterceptor
            throw new ObjectOutOfDateException("Cannot save DataSet - the object is out-of-date",ex);
        }
    }

    public org.psygrid.data.model.dto.IdentifierDTO[] generateIdentifiers(
            final String projectCode, final String groupCode, final int number, 
            final int maxSuffix, final String user) throws DAOException {

        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){

                DataSet ds = (DataSet)session.createQuery("from DataSet ds where ds.projectCode=?")
                                             .setString(0, projectCode)
                                             .uniqueResult();
                
                if ( null == ds ){
                    return new DAOException("No dataset exists for project code '"+projectCode+"'");
                }
                
                int idSuffixSize = ds.getIdSuffixSize();
                
                //generate the batch of identifiers
                Identifier[] identifiers = new Identifier[number];
                for (int i=0; i<number; i++){
                    Identifier id = new Identifier(user);
                    int suffix = i+(maxSuffix-number)+1;
                    id.initialize(projectCode, groupCode, suffix, idSuffixSize);
                    session.saveOrUpdate(id);
                    identifiers[i] = id;
                }
                
                //have to manually flush at this point to ensure that database
                //ids are generated - otherwise, database writes are only done
                //at the end of the transaction and all Identifier objects have 
                //null ids
                session.flush();
                
                org.psygrid.data.model.dto.IdentifierDTO[] dtoIds = new org.psygrid.data.model.dto.IdentifierDTO[number];
                Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> refs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
                for ( int i=0; i<number; i++){
                    dtoIds[i] = identifiers[i].toDTO(refs, RetrieveDepth.DS_COMPLETE);
                }
                
                return dtoIds;
            }
        };
        
        //check for a valid number of identifiers to generate
        if ( number < 1 ){
            throw new DAOException("The number of identifiers to generate must be a positive, non-zero integer");
        }
        
        //check for non-null group
        if ( null == groupCode ){
            throw new DAOException("The group code cannot be null");
        }
        
        //check for non-null group
        if ( null == projectCode ){
            throw new DAOException("The project code cannot be null");
        }
        
        Object result = getHibernateTemplate().execute(callback);
        if ( result instanceof DAOException ){
            throw (DAOException)result;
        }
        else{
            return (org.psygrid.data.model.dto.IdentifierDTO[])result;
        }
    }


    public String getProjectCodeForDataset(Long dataSetId) throws DAOException {
        Iterator result = getHibernateTemplate().find("select ds.projectCode from DataSet ds where ds.id=?", dataSetId)
                                                .iterator();
        String code = null;
        while ( result.hasNext() ){
            code = (String)result.next();
        }
        
        if (null == code){
            throw new DAOException("No DataSet exists in the repository for id = "+dataSetId);
        }
        
        return code;
    }


    public boolean getDataSetModified(String projectCode, Date referenceDate) 
            throws NoDatasetException {
        Iterator result = getHibernateTemplate().find("select ds.dateModified from DataSet ds where ds.projectCode=?", projectCode)
                                                .iterator();
        Date modified = null;
        while ( result.hasNext() ){
            modified = (Date)result.next();
        }
        
        if ( null == modified ){
            throw new NoDatasetException("No DataSet exists in the repository for projectCode = "+projectCode);
        }
        
        boolean after = modified.after(referenceDate);
        return after;
    }


    public org.psygrid.data.model.dto.DataSetDTO getSummaryForProjectCode(final String projectCode, final RetrieveDepth depth) throws NoDatasetException {

        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                DataSet ds = (DataSet)session.createQuery("from DataSet ds where ds.projectCode=?")
                                             .setString(0, projectCode)
                                             .uniqueResult();
                org.psygrid.data.model.dto.DataSetDTO dtoDataSet = null;
                if ( null != ds ){
                    dtoDataSet = ds.toDTO(depth);
                }
                return dtoDataSet;
            }
        };
 
        
        org.psygrid.data.model.dto.DataSetDTO result = (org.psygrid.data.model.dto.DataSetDTO)getHibernateTemplate().execute(callback);
        if ( null == result ){
            throw new NoDatasetException("No DataSet exists in the repository for projectCode = "+projectCode);
        }
        
        return result;
    }    

    public String getStatusShortName(Long statusId) throws DAOException {
       
        Iterator it = getHibernateTemplate().find("select s.shortName from Status s where s.id=?", statusId).iterator();
        String shortName = null;
        while ( it.hasNext() ){
            shortName = (String)it.next();
        }
        if ( null == shortName ){
            throw new DAOException("No status exists for id="+statusId);
        }
        
        return shortName;
    }


    public Integer reserveIdentifierSpace(final Long dataSetId, final String group, final int nIdentifiers) throws DAOException {
        
        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                
                Group g = (Group)session.createQuery("from Group g where g.dataSet.id=? and g.name=?")
                                   .setLong(0, dataSetId)
                                   .setString(1, group)
                                   .uniqueResult();
                if ( null == g ){
                    //no rows were updated so dataSetId and/or group are not correct
                    return null;
                }
                
                int newMaxSuffix = g.getMaxSuffix()+nIdentifiers;
                g.setMaxSuffix(newMaxSuffix);
                
                session.saveOrUpdate(g);
                
                return newMaxSuffix;
            }
        };
        
        if ( nIdentifiers < 1 ){
            throw new DAOException("Number of identifiers must be greater than zero");
        }
        
        Integer result = (Integer)getHibernateTemplate().execute(callback);
        if ( null == result ){
            throw new DAOException("No group exists for dataset="+dataSetId+", group='"+group+"'");
        }
        
        return result;
    }


    public org.psygrid.data.model.dto.DataSetDTO patchDataSet(org.psygrid.data.model.dto.DataSetDTO dataSet) throws DAOException, ObjectOutOfDateException {
        DataSet ds = dataSet.toHibernate();
        try{
            //set the modified date
            ds.setDateModified(new Date());

            //need to auto-increment the dataset version number
            ds.incrementAutoVersionNo();
            
            //increment verion numbers for entries and documents as necessary
            for (org.psygrid.data.model.hibernate.Document d: ds.getDocuments()) {
            	//if doc has changed, increment it and check the entries
        		for (Entry entry: d.getEntries()) {
        			if (entry.isChanged()) {
        				//containing doc has changed too
        				d.setChanged(true);
        				entry.incrementAutoVersionNo();
        				entry.setChanged(false);
        			}
        		}
            		
	    		//if the doc has changed, increment the version number
	    		if (d.isChanged()) {
	    			d.incrementAutoVersionNo();
	    			d.setChanged(false);
	    		}
        	}
            
            getHibernateTemplate().update(ds);
            for (Persistent p:ds.getDeletedObjects()){
                getHibernateTemplate().delete(p);
            }
            
            org.psygrid.data.model.dto.DataSetDTO dtoDS = ds.toDTO(RetrieveDepth.DS_NO_BINARY);
            return dtoDS;
        }
        catch (HibernateOptimisticLockingFailureException ex){
            //Note that this catch block will NEVER be entered if
            //this method is called using Spring declarative transaction
            //management. In that case, the exception is caught in
            //SaveObjectInterceptor
            throw new ObjectOutOfDateException("Cannot save DataSet - the object is out-of-date",ex);
        }
    }
    
    
    
    @SuppressWarnings("unchecked")
    public List<Long> getTransformerIds(final Long entryId) throws DAOException {
        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                
                List transformers = session.createQuery("select be.transformers from BasicEntry be where be.id=?")
                                   .setLong(0, entryId)
                                   .list();
                
                List<Long> result = new ArrayList<Long>();
                for ( Object o: transformers){
                    Transformer t = (Transformer)o;
                    result.add(t.getId());
                }
                
                return result;
            }
        };
        
        return (List<Long>)getHibernateTemplate().execute(callback);
    }
    
    @SuppressWarnings("unchecked")
    public List<org.psygrid.data.model.dto.TransformerDTO> getTransformers(final Long entryId) throws DAOException {
    	HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                
                List transformers = session.createQuery("select be.transformers from BasicEntry be where be.id=?")
                                   .setLong(0, entryId)
                                   .list();
                
                List<org.psygrid.data.model.dto.TransformerDTO> result = new ArrayList<org.psygrid.data.model.dto.TransformerDTO>();
                for ( Object o: transformers){
                    Transformer t = (Transformer)o;
                    result.add(t.toDTO());
                }
                
                return result;
            }
        };
        
        List<org.psygrid.data.model.dto.TransformerDTO> result = (List<org.psygrid.data.model.dto.TransformerDTO>)getHibernateTemplate().execute(callback);
        if ( null == result ){
            throw new DAOException("No Transformers exist in the repository for the entry = "+entryId);
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public List<Long> getOutputTransformerIds(final Long entryId) throws DAOException {
        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                
                List transformers = session.createQuery("select be.outputTransformers from BasicEntry be where be.id=?")
                                   .setLong(0, entryId)
                                   .list();
                
                List<Long> result = new ArrayList<Long>();
                for ( Object o: transformers){
                    Transformer t = (Transformer)o;
                    result.add(t.getId());
                }
                
                return result;
            }
        };
        
        return (List<Long>)getHibernateTemplate().execute(callback);
    }
    
    


    public String createTransformerErrorMessage(
            final String identifier, 
            final Long docOccId, 
            final Long secOccId, 
            final Long entryId, 
            final Long childEntryId, 
            final Integer row,
            final String error) throws DAOException {

        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                
                String entryName;
                DocumentOccurrence d = (DocumentOccurrence)session.createQuery("from DocumentOccurrence d where d.id=?")
                                                                  .setLong(0, docOccId)
                                                                  .uniqueResult();
                String docName = d.getCombinedDisplayText();
                
                SectionOccurrence s = (SectionOccurrence)session.createQuery("from SectionOccurrence s where s.id=?")
                                                                .setLong(0, secOccId)
                                                                .uniqueResult();
                String secName = s.getCombinedDisplayText();
                
                Entry e = (Entry)session.createQuery("from Entry e where e.id=?")
                                        .setLong(0, entryId)
                                        .uniqueResult();
                if ( null == childEntryId){
                    entryName = e.getDisplayText();
                }
                else{
                    Entry ce = (Entry)session.createQuery("from Entry e where e.id=?")
                                             .setLong(0, childEntryId)
                                             .uniqueResult();
                    entryName = e.getDisplayText()+": "+ce.getDisplayText()+" (Row "+row+")";
                }

                StringBuilder builder = new StringBuilder();
                builder.append("An error occurred whilst trying to transform a value in the record being saved.\n\n");
                builder.append("Please correct this problem using the following information, then try again.\n\n");
                builder.append("Record = "+identifier+"\n");
                builder.append("Document = "+docName+"\n");
                builder.append("Section = "+secName+"\n");
                builder.append("Entry = "+entryName+"\n\n");
                builder.append("Error = "+error);
                
                return builder.toString();
            }

        };
        
        return (String)getHibernateTemplate().execute(callback);
    }

    public Object[] executeHQLQuery(final String query)
    {
        Object[] result = null;
    	HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                
                List result = session.createQuery(query).list();
                return result.toArray();
            }
        };
        return result;
    }
    

    private void removeIdentifiers(String projectCode) throws DAOException{
 	   
 	   Iterator identifiers = getHibernateTemplate().find("from Identifier where projectPrefix = ?", projectCode).iterator();
 	   while (identifiers.hasNext()) {
 		   getHibernateTemplate().delete(identifiers.next());
 	   }
    }

    public String[] getPublishedDatasets(final String[] projects) {
        HibernateCallback callback = new HibernateCallback(){
        	@SuppressWarnings("unchecked")
            public Object doInHibernate(Session session){
            	List<String> publishedProjects = (List<String>)session.createQuery(
            			"select ds.projectCode from DataSet ds " +
            			"where ds.published=:published and ds.projectCode in (:projects)")
            	.setParameter("published", Boolean.TRUE)
            	.setParameterList("projects", projects)
            	.list();
            	return publishedProjects.toArray(new String[publishedProjects.size()]);
            }
        };
        
        return (String[])getHibernateTemplate().execute(callback);
    }
    

       


	public String getOfficerRole() {
		return officerRole;
	}

	public void setOfficerRole(String officerRole) {
		this.officerRole =officerRole;
	}

	public String getManagerRole() {
		return managerRole;
	}

	public void setManagerRole(String managerRole) {
		this.managerRole = managerRole;
	}

	public String getSysAdminEmail() {
		return sysAdminEmail;
	}

	public void setSysAdminEmail(String sysAdminEmail) {
		this.sysAdminEmail = sysAdminEmail;
	}

	public boolean isSendMails() {
		return sendMails;
	}

	public void setSendMails(boolean sendMails) {
		this.sendMails = sendMails;
	}

	public EntityInterceptor getInterceptor() {
		return interceptor;
	}

	public void setInterceptor(EntityInterceptor interceptor) {
		this.interceptor = interceptor;
	}

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public AAQCWrapper getAaqc() {
		return aaqc;
	}

	public void setAaqc(AAQCWrapper aaqc) {
		this.aaqc = aaqc;
	}

	public IRemoteClient getEslClient() {
		return eslClient;
	}

	public void setEslClient(IRemoteClient client) {
		this.eslClient = client;
	}

	public org.psygrid.data.model.dto.RecordDTO getRecord(final Long recordId, final RetrieveDepth depth) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				Record r = (Record)session.createCriteria(Record.class)
				.add(Restrictions.idEq(recordId))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.uniqueResult();
				org.psygrid.data.model.dto.RecordDTO dtoR = null;
				if ( null != r ){
					dtoR = r.toDTO(depth);
				}

				return dtoR;
			}
		};

		org.psygrid.data.model.dto.RecordDTO r = (org.psygrid.data.model.dto.RecordDTO)getHibernateTemplate().execute(callback);
		if ( null == r ){
			throw new DAOException("No Record exists in the repository for id = "+recordId);
		}

		if (isRecordValid(r)) {
			return r;
		}

		return null;
	}

	public org.psygrid.data.model.dto.RecordDTO getRecord(final String identifier, final RetrieveDepth depth) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				Record r = (Record)session.createQuery("from Record r where r.identifier.identifier=? and r.deleted=?")
				.setString(0, identifier)
				.setBoolean(1, false)
				.uniqueResult();
				org.psygrid.data.model.dto.RecordDTO dtoR = null;
				if ( null != r ){
					dtoR = r.toDTO(depth);
				}

				return dtoR;
			}
		};

		org.psygrid.data.model.dto.RecordDTO r = (org.psygrid.data.model.dto.RecordDTO)getHibernateTemplate().execute(callback);
		if ( null == r ){
			throw new DAOException("No Record exists in the repository for identifier = "+identifier);
		}

		if (isRecordValid(r)) {
			return r;
		}

		return null;
	}

	public org.psygrid.data.model.dto.RecordDTO getRecordByExternalID(final long datasetID,final String externalID, final RetrieveDepth depth) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				Record r = (Record)session.createQuery("from Record r where r.dataSet.id=? and r.externalIdentifier=? and r.deleted=?")
				.setLong(0, datasetID)
				.setString(1, externalID)
				.setBoolean(2, false)
				.uniqueResult();
				org.psygrid.data.model.dto.RecordDTO dtoR = null;
				if ( null != r ){
					dtoR = r.toDTO(depth);
				}
				return dtoR;
			}
		};

		org.psygrid.data.model.dto.RecordDTO r = (org.psygrid.data.model.dto.RecordDTO)getHibernateTemplate().execute(callback);

//		Shouldn't be throwing exceptions for normal conditions
//		if ( null == r ){
//			throw new DAOException("No Record exists in the repository for external identifier = "+externalID);
//		}

		if (r!=null && !isRecordValid(r)) {
			r = null;
		}

		return r;
	}

	
	public org.psygrid.data.model.dto.RecordDTO getRecordsDocumentsByStatus(final String identifier, final String status) throws DAOException {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				Record r = (Record)session.createQuery("from Record r where r.identifier.identifier=? and r.deleted=?")
				.setString(0, identifier)
				.setBoolean(1, false)
				.uniqueResult();

				org.psygrid.data.model.dto.RecordDTO dtoR = null;
				if ( null != r ){
					dtoR = r.toDTO(RetrieveDepth.RS_DOC_ONLY, status);
				}

				return dtoR;
			}
		};

		org.psygrid.data.model.dto.RecordDTO r = (org.psygrid.data.model.dto.RecordDTO)getHibernateTemplate().execute(callback);
		if ( null == r ){
			throw new DAOException("No Record exists in the repository for identifier = "+identifier);
		}

		if (isRecordValid(r)) {
			return r;
		}

		return null;
	}

	public Long saveRecord(org.psygrid.data.model.dto.RecordDTO record, boolean discardDuplicates, final DocumentSecurityHelper docHelper, String userName) 
	throws DAOException, ObjectOutOfDateException, NoConsentException, DuplicateDocumentsException {
		Record r = record.toHibernate();
		return saveRecord(r, discardDuplicates, docHelper, userName);
	}

	public Long saveRecord(Record r, final boolean discardDuplicates, final DocumentSecurityHelper docHelper, final String userName)
	throws DAOException, ObjectOutOfDateException, NoConsentException, DuplicateDocumentsException {

		final Record record = (Record)r;

		sLog.info("A record is being saved - identifier=\'"+r.getIdentifier().getIdentifier()+"\' id="+r.getId());

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				DataSet ds = (DataSet)session.get(DataSet.class, record.getDataSetId());
				if ( !(ds.isPublished())){
					return new DAOException("Cannot save record - its DataSet has not been published");
				}

				record.attach((DataSet)ds);

				StringBuilder message = new StringBuilder();
				List<Long> discards = new ArrayList<Long>();
				Record recordToSave = null;
				boolean newRecord = false;
				boolean noId = false;
				boolean recordDeleted = false;
				boolean duplicateDocuments = false;
				if ( null == record.getId() ){
					noId = true;

					//the record being saved does not have a database id yet, so it is either
					//new, or is a subset of a record to append to an existing record
					if ( null == record.getIdentifier() ){
						return new DAOException("Cannot save record - it has not been assigned an identifier");
					}

					//check that the identifier is known
					Identifier id = (Identifier)session.createQuery("from Identifier i where i.identifier=?")
					.setString(0, record.getIdentifier().getIdentifier())
					.uniqueResult();
					if ( null == id ){
						return new UnknownIdentifierException("Cannot save record - its identifier '"+
								record.getIdentifier().getIdentifier()+
						"' is not known by the repository");
					}

					Record savedRecord = (Record)session.createQuery("from Record r where r.identifier.identifier=?")
					.setString(0, record.getIdentifier().getIdentifier())
					.uniqueResult();
					if ( null != savedRecord ){
						if ( savedRecord.isDeleted() ){
							//The record has been marked as deleted - therefore we don't want to
							//attach the documents to it, we just need to throw these away in the same
							//way we would duplicate documents - also set a flag so we don't save
							//the record itself, as there is no point
							for (DocumentInstance inst:record.getDocInstances()){
								if ( discardDuplicates ){
									sLog.info("Not saving instance of document '"+
											inst.getOccurrence().getDocument().getDisplayText()+"', occurrence '"+
											inst.getOccurrence().getDisplayText()+"' for record '"+
											record.getIdentifier().getIdentifier()+"' - the record has been deleted.");
								}
								else{
									message.append(inst.getOccurrence().getCombinedDisplayText());
									message.append("\n");
									discards.add(inst.getOccurrence().getId());
								}
							}
							recordDeleted = true;
						}
						else{
							//a record already exists in the repository with this unique identifier
							//try to attach all document instances of the new record to the existing
							//record
							for (DocumentInstance inst:record.getDocInstances()){
								//check for existing instance in the current saved record that
								//references the same document occurrence
								DocumentInstance currentInst = null;
								for (DocumentInstance i:savedRecord.getDocInstances()){
									if ( i.getOccurrence().equals(inst.getOccurrence())){
										currentInst = i;
										break;
									}
								}
								if ( null != currentInst && !currentInst.getId().equals(inst.getId()) ){
									//a document instance that references the same document occurrence
									//already exists and the document instance we are trying to save has
									//a different database id - this is not allowed!
									duplicateDocuments = true;
									if ( discardDuplicates ){
										sLog.info("Not saving instance of document '"+
												currentInst.getOccurrence().getDocument().getDisplayText()+"', occurrence '"+
												currentInst.getOccurrence().getDisplayText()+"' for record '"+
												record.getIdentifier().getIdentifier()+"' - it has already been saved. " +
												"[Repository DocID="+currentInst.getId()+", New DocId="+inst.getId());
									}
									else{
										Date when  = null;
										if ( currentInst.getHistoryCount() > 0 ){
											when = currentInst.getHistory(0).getWhen();
										}
										else{
											when = currentInst.getCreated();
										}
										sLog.info("Duplicate document found - original created at "+when+" ("+
												record.getIdentifier().getIdentifier()+", "+
												currentInst.getOccurrence().getCombinedDisplayText());
										message.append(currentInst.getOccurrence().getCombinedDisplayText());
										message.append("\n");
										discards.add(currentInst.getOccurrence().getId());
									}
								}
								else{
									/*
									 * If control reaches this point then an existing saved record has been found and
									 * its document instances need to be compared with the new instances for security
									 * purposes, before the existing instance is removed from the record's set of
									 * instances, ready for saving the new record.
									 */
									if ( null != currentInst ){
										if (docHelper == null) {
											sLog.warn("No document security performed as the DocumentSecurityHelper was null");
										}
										else {
											//get the permissions for the original document instance 
											DocumentInstance secureInst = docHelper.authoriseDocumentInstance(currentInst, userName);

											//Check permissions for the document

											//The editing permission has been recalculated for the old document instance.
											//Compare to the permission assigned to the current instance to check that it
											//hasn't been tampered with.
											if (secureInst.isEditingPermitted() != inst.isEditingPermitted()) {
												//Something has gone wrong..
												return new DAOException("The editing permission of the document instance does not match the permission of the saved document instance.");
											}

											//Check permissions for the entries/responses
											try {
												for (Response currentResponse: inst.getResponses()) {
													for (Response secureResponse: secureInst.getResponses()) {
														if (secureResponse.getId().equals(currentResponse.getId())) {

															EditAction oldAction = secureResponse.getEditingPermitted();
															EditAction newAction = currentResponse.getEditingPermitted();
															if (!oldAction.equals(newAction)) {
																return new DAOException("The editing permission of the response does not match the permission of the saved response.");
															}

															if (EditAction.READONLY.equals(newAction)) {
																if (!(secureResponse.getValueAsString() == currentResponse.getValueAsString()
																		|| secureResponse.getValueAsString().equals(currentResponse.getValueAsString()))) {
																	//If the two responses are not the same then the response object must have been tampered
																	//with, as it is supposed to be read-only.
																	return new DAOException("The editing permission of the response does not match the permission of the saved response.");
																}
															}
															else if (EditAction.DENY.equals(newAction)) {
																//Reset the values of the current response to those that were already saved,
																//as the values for any unviewable responses are removed before being sent to
																//the client.
																if (currentResponse instanceof BasicResponse) {
																	((BasicResponse)currentResponse).setTheValue(((BasicResponse)secureResponse).getTheValue());
																}
																else if (currentResponse instanceof CompositeResponse) {
																	//Can't do this as the secureResponse contains minimal information and doesn't reference an entry
																	//((CompositeResponse)currentResponse).setCompositeRows(((CompositeResponse)secureResponse).getCompositeRows());
																	if (((CompositeResponse)currentResponse).getCompositeRows().size() != ((CompositeResponse)secureResponse).getCompositeRows().size()) {
																		return new DAOException("The number of rows for the composite table has changed.");
																	}
																	for (CompositeRow row: ((CompositeResponse)currentResponse).getCompositeRows()) {
																		nextResponse: for (BasicResponse r: row.getBasicResponses()) {			
																			for (CompositeRow secureRow: ((CompositeResponse)secureResponse).getCompositeRows()) {
																				for (BasicResponse sr: secureRow.getBasicResponses()) {
																					if (r.getId().equals(sr.getId())) {
																						r.setTheValue(sr.getTheValue());
																						continue nextResponse;
																					}
																				}
																			}
																		}
																	}
																}
																else {
																	return new DAOException("Response type was not recognised: "+currentResponse);
																}
															}
														}
													}
												}
											}
											catch (Exception e) {
												e.printStackTrace();
												return new DAOException("General problem: ", e);
											}
										}

										/*
										 * If control reaches this point then it is OK to merge this document
										 * instance with the saved record. If an existing instance has been
										 * found then this needs to be removed from the record's set of instances
										 * and evicted from the session (otherwise Hibernate will throw an exception :-)
										 */
										savedRecord.getDocInstances().remove(currentInst);
										session.evict(currentInst);
									}
									savedRecord.addInstanceServer(inst);
								}
							}
						}
						recordToSave = savedRecord;
					}
					else{
						//This is the first time the record has been saved (its database id
						//is null and there is no other record in the database with the same
						//identifier). The consent and status modified dates set
						record.setConsentModified(new Date());
						record.setStatusModified(new Date());
						//If there is no RecordData object assume that the scheduleStartDate and 
						//studyEntryData properties on the Record are being used. So create an 
						//equivalent RecordData object for the Record - this is for legacy 
						//records created (but not committed) before upgrade to v1.1.14
						if ( null == record.getRecordData() ){
							RecordData recordData = record.generateRecordData();
							recordData.setScheduleStartDate(record.getScheduleStartDate());
							recordData.setStudyEntryDate(record.getStudyEntryDate());
							record.setRecordData(recordData, null);
						}
						//set reference to the new record, just so rest of the code is generic
						//whether a new record is being saved or an existing record is being
						//appended to.
						recordToSave = record;
						newRecord = true;
					}
				}
				else{
					//an existing record is being updated, having been retrieved in its entirity
					//from the repository
					//This case is only usef AFAIK when updating records whilst patching a
					//dataset. The consent and/or record status should not be modified by a patch
					//so we do not update the consent and status last modified dates.
					recordToSave = record;
				}

				//check that consent has been obtained (where required) for all element 
				//instances in the record
				for ( DocumentInstance inst: recordToSave.getDocInstances() ){
					if ( !checkConsent(inst) ){
						return new NoConsentException("Cannot save record - there is insufficient consent to save document '"+inst.getOccurrence().getDocument().getDisplayText()+"'");
					}

					/*
					 * Check that the document instance is allowed to be saved by confirming that
					 * the RBACActions haven't been altered from the original documents.
					 */
					String orgEditableAction = inst.getOccurrence().getDocument().getInstanceEditableAction();
					String orgAction = inst.getOccurrence().getDocument().getInstanceAction();
					if (orgEditableAction != null && inst.getEditableAction() != null
							&& !(orgEditableAction.equals(inst.getEditableAction()))) {
						return new DAOException("Could not save record. The editable RBACAction has been altered from the original document.");
					}
					if (orgAction != null && inst.getAction() != null
							&& !(orgAction.equals(inst.getAction()))) {
						return new DAOException("Could not save record. The RBACAction has been altered from the original document.");
					}
				}
				//Duplicate documents found
				if ( duplicateDocuments && !discardDuplicates){
					return new DuplicateDocumentsException("The following documents for record "+record.getIdentifier().getIdentifier()+
							" are duplicates and will therefore be discarded:\n"+message.toString(), "Duplicate Documents", discards, message.toString());
				}

				if ( recordDeleted && !discardDuplicates){
					return new DuplicateDocumentsException("Record "+record.getIdentifier().getIdentifier()+" has been deleted from the repository " +
							"and the following documents will therefore be discarded:\n"+message.toString(),"Record Deleted", discards, message.toString());
				}

				//Check for a null schedule start date and log if this is the case
				if ( ds.getScheduleStartQuestion()!=null && recordToSave.getScheduleStartDate()==null ){
					sLog.error("A record is being saved with a null schedule start date!! Study number="+
							recordToSave.getIdentifier().getIdentifier()+"; newRecord="+newRecord+"; noId="+noId);
				}
				//save/update the record
				if ( recordDeleted ){
					//Not saving the record as it has been deleted
					sLog.info("Not saving record as it has been deleted. Study number="+recordToSave.getIdentifier().getIdentifier());
				}
				else{
					recordToSave.addToHistory(userName, ChangeHistory.SAVED);
					EntityInterceptor.setUserName(userName);
					EntityInterceptor.setParentHistory(recordToSave.getLatestHistory());
					session.saveOrUpdate(recordToSave);
					for (Persistent p: record.getDeletedObjects()){
						Object obj = session.get(p.getClass(), p.getId());
						session.delete(obj);
					}
				}
				if ( newRecord && ds.getReviewReminderCount() > 0 ){
					//find out how many records have been committed for this
					//hub - send an email to the CPM reminding them about review
					//and approve every 20th
					String project = recordToSave.getIdentifier().getProjectPrefix();
					String group = recordToSave.getIdentifier().getGroupPrefix();

					Long result = (Long)session.createQuery("select count(*) from Record r where r.identifier.projectPrefix=? and r.identifier.groupPrefix=?")
					.setString(0, project)
					.setString(1, group)
					.uniqueResult();

					if ( 0 == (result.intValue() % ds.getReviewReminderCount()) ){
						sendReviewReminder(result.intValue(), project, group);
					}
				}

				return recordToSave.getId();
			}
		};

		try{
			Object result = (Object)getHibernateTemplate().execute(callback);
			if ( result instanceof DuplicateDocumentsException ){
				throw (DuplicateDocumentsException)result;
			}
			if ( result instanceof DAOException ){
				sLog.error((DAOException)result);
				throw (DAOException)result;
			}
			return (Long)result;
		}
		catch(HibernateOptimisticLockingFailureException ex){
			//Note that this catch block will NEVER be entered if
			//this method is called using Spring declarative transaction
			//management. In that case, the exception is caught in
			//SaveObjectInterceptor
			sLog.error(ex);
			throw new ObjectOutOfDateException("Could not save record - the object is out-of-date",ex);
		}
		catch(DataAccessException ex){
			sLog.error(ex);
			throw new DAOException("Could not save record",ex);
		}

	}

	public org.psygrid.data.model.dto.RecordDTO[] getRecords(final Long dataSetId, final RetrieveDepth rD) {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				List records = session.createCriteria(Record.class)
				.add(Restrictions.eq("dataSet.id", dataSetId))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.list();

				List<org.psygrid.data.model.dto.RecordDTO> dtoRecords = new ArrayList<org.psygrid.data.model.dto.RecordDTO>();
				for ( int i=0; i<records.size(); i++){
					Record r = (Record)records.get(i);
					org.psygrid.data.model.dto.RecordDTO dtoRecord = r.toDTO(rD);
					if (isRecordValid(dtoRecord)) {
						dtoRecords.add(dtoRecord);
					}
					session.evict(r);
				}
				
				return dtoRecords;
			}
		};

		List recordList = (List) getHibernateTemplate().execute(callback);
		return this.convertObjectListToRecordArray(recordList);

	}

	@SuppressWarnings("unchecked")
	public String[] getIdentifiers(final Long dataSetId) {
		HibernateCallback callback = new HibernateCallback(){
			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session){

				List identifiers = session.createQuery("select i.identifier from Identifier i, Record r " +
						"where r.dataSet.id=:dataSetId and " +
						"r.deleted=:deleted and " +
						"r.status.enumGenericState not like :invalidState and " +	//this does the same job as the isRecordValid() method
						"r.identifier=i.id" +
				" order by r.identifier.groupPrefix, r.identifier.suffix asc")
				.setLong("dataSetId", dataSetId)
				.setString("invalidState", GenericState.INVALID.toString())
				.setBoolean("deleted", false)
				.list();

				return identifiers;
			}
		};

		List<String> results = (List<String>)getHibernateTemplate().execute(callback);
		String[] identifiers = new String[results.size()];

		for (int i = 0; i < results.size(); i++) {
			identifiers[i] = results.get(i);
		}

		return identifiers;
	}
	
	public String[] getIdentifiersByResponse(final String projectCode, final String documentName, final String entryName, final String textValue) {

		HibernateCallback callback = new HibernateCallback(){
			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session){
				List identifiers = session.createQuery("select distinct id.identifier from TextValue v, BasicResponse br " +
						"join br.entry e "+
						"join br.docInstance di "+
						"join di.occurrence occ "+
						"join occ.document doc "+
						"join doc.myDataSet ds "+
						"join br.record r "+
						"join r.identifier id "+
						"where br.theValue=v "+
						"and v.value = :textValue "+
						"and e.name = :entryName "+
						"and doc.name = :documentName "+
						"and ds.projectCode = :projectCode "+
						"and r.deleted=:deleted " +
						"and r.status.enumGenericState not like :invalidState "	//this does the same job as the isRecordValid() method
						)
				.setString("projectCode", projectCode)
				.setString("documentName", documentName)
				.setString("entryName", entryName)
				.setString("textValue", textValue)
				.setString("invalidState", GenericState.INVALID.toString())
				.setBoolean("deleted", false)
				.list();

				return identifiers;
			}
		};

		List<String> results = (List<String>)getHibernateTemplate().execute(callback);
		String[] identifiers = results.toArray(new String[results.size()]);		
		return identifiers;
	}


	@SuppressWarnings("unchecked")
	public IdentifierData[] getIdentifiersExtended(final Long dataSetId) {

		HibernateCallback callback = new HibernateCallback(){
			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session){

				Boolean useExternalID = (Boolean)session.createQuery("select d.useExternalIdAsPrimary from DataSet d where d.id=:id ")
				.setLong("id", dataSetId)
				.uniqueResult();

				String hql = "select i.identifier, r.externalIdentifier, r.useExternalIdAsPrimary from Identifier i, Record r " +
				"where r.dataSet.id=:dataSetId and " +
				"r.deleted=:deleted and " +
				"r.status.enumGenericState not like :invalidState and " +	//this does the same job as the isRecordValid() method
				"r.identifier=i.id";
				
				// Conditionally add an order by clause
				if(useExternalID){
					hql+=" order by r.externalIdentifier asc";
				}
				else {
					hql+=" order by r.identifier.groupPrefix, r.identifier.suffix asc";					
				}
				
				List results = session.createQuery(hql)
				.setLong("dataSetId", dataSetId)
				.setString("invalidState", GenericState.INVALID.toString())
				.setBoolean("deleted", false)
				.list();

				return results;
			}
		};

		List results = (List)getHibernateTemplate().execute(callback);

		IdentifierData[] identifiers = new IdentifierData[results.size()];

		for (int i = 0; i < results.size(); i++) {
			Object[] data = (Object[])results.get(i);
			String identifier = (String)data[0];
			String externalID = (String)data[1];
			Boolean useExternalID = (Boolean)data[2];
			identifiers[i] = new IdentifierData(identifier,externalID,useExternalID);
		}
		return identifiers;
	}
	
	public org.psygrid.data.model.dto.RecordDTO[] getRecordsByStatus(final Long dataSetId, final Long statusId) {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				List records = session.createQuery("from Record r where r.dataSet.id=? and r.status.id=? and r.deleted=?")
				.setLong(0, dataSetId)
				.setLong(1, statusId)
				.setBoolean(2, false)
				.list();

				List<org.psygrid.data.model.dto.RecordDTO> dtoRecords = new ArrayList<org.psygrid.data.model.dto.RecordDTO>();
				for ( int i=0; i<records.size(); i++){
					Record r = (Record)records.get(i);
					org.psygrid.data.model.dto.RecordDTO dtoRecord = r.toDTO(RetrieveDepth.RS_SUMMARY);
					if (isRecordValid(dtoRecord)) {
						dtoRecords.add(dtoRecord); 
					}
				}

				return dtoRecords;

			}
		};

		List recordList = (List) getHibernateTemplate().execute(callback);
		return this.convertObjectListToRecordArray(recordList);

	}

	public List<SimpleMailMessage> getAllScheduledReminders(Date now) throws DAOException {
		List<SimpleMailMessage> reminders = new ArrayList<SimpleMailMessage>();
		Iterator dataSets = getHibernateTemplate().find("from DataSet").iterator();
		while ( dataSets.hasNext() ){
			DataSet ds = (DataSet)dataSets.next();
			reminders.addAll(getScheduledRemindersForDataset(now, ds.getId()));
		}
		return reminders;
	}

	@SuppressWarnings("unchecked")
	public List<SimpleMailMessage> getScheduledRemindersForDataset(final Date now, final Long dataSetId) throws DAOException{

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				List<SimpleMailMessage> reminders = new ArrayList<SimpleMailMessage>();

				//Get all records for the given dataset
				Iterator records = session.createCriteria(Record.class)
				.add(Restrictions.eq("dataSet.id", dataSetId))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.list()
				.iterator();
				while ( records.hasNext() ){
					Record r = (Record)records.next();
					if ( null == r.getStatus() || (!r.getStatus().isInactive() && isRecordValid(r))){
						//only send reminders for records with an "active" status
						try{
							//use the schedule start date as the base, unless this is
							//null in which case we use the study entry date, unless this too
							//is null, in which case we use the date of creation of the record
							Date start = null;
							if ( null != r.getScheduleStartDate() ){
								start = r.getScheduleStartDate();
							}
							else if ( null != r.getStudyEntryDate() ){
								start = r.getStudyEntryDate();
							}
							else{
								start = r.getHistory(0).getWhen();
							}

							findReminders(r.getDataSet().getDocuments(),
									r.getDocInstances(),
									reminders,
									removeTimeComponent(now),
									r,
									removeTimeComponent(start));
						}
						catch(DAOException ex){
							return ex;
						}
					}
				}

				return reminders;
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}
		else{
			return (List<SimpleMailMessage>)result;
		}

	}

	public String getProjectCodeForInstance(Long instanceId) throws DAOException {
		Iterator result = getHibernateTemplate().find("select ei.record.identifier.projectPrefix from ElementInstance ei where ei.id=?", instanceId)
		.iterator();
		String code = null;
		while ( result.hasNext() ){
			code = (String)result.next();
		}

		if ( null == code ){
			throw new DAOException("No element instance exists in the repository for id = "+instanceId);
		}

		return code;
	}

	public String getGroupCodeForInstance(Long instanceId) throws DAOException {
		Iterator result = getHibernateTemplate().find("select ei.record.identifier.groupPrefix from ElementInstance ei where ei.id=?", instanceId)
		.iterator();
		String code = null;
		while ( result.hasNext() ){
			code = (String)result.next();
		}

		if ( null == code ){
			throw new DAOException("No element instance exists in the repository for id = "+instanceId);
		}

		return code;
	}

	public String[] getProjectAndGroupForInstance(Long instanceId) throws DAOException {
		Iterator result = getHibernateTemplate().find("select ei.record.identifier.projectPrefix, ei.record.identifier.groupPrefix "+
				"from ElementInstance ei where ei.id=?", instanceId)
				.iterator();
		String[] codes = new String[2];
		while ( result.hasNext() ){
			Object[] objs = (Object[])result.next();
			codes[0] = (String)objs[0];
			codes[1] = (String)objs[1];
		}

		if ( null == codes[0] && null == codes[1] ){
			throw new DAOException("No element instance exists in the repository for id = "+instanceId);
		}

		return codes;
	}

	public String[] withdrawConsentDryRun(final Long recordId, final Long consentFormId, final String reason, final String userName, final String saml) 
	throws DAOException {
		
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				Record record = (Record)session.get(Record.class, recordId);
				if ( null == record ){
					return new DAOException("No record exists with the given id");
				}

				//find the consent for the consent form being withdrawn
				//and remove it
				ConsentForm cf = (ConsentForm)session.get(ConsentForm.class, consentFormId);
				if ( null == cf ){
					return new DAOException("No consent form exists with the given id");
				}
				Consent rc = null;
				for (Consent con:record.getConsents()){
					if ( con.getConsentForm().getId().equals(cf.getId()) ){
						rc = con;
					}
				}
				if ( null == rc ){
					return new DAOException("No consent exists for the given consent form and record");
				}
				record.removeConsent(rc, reason);
				record.setConsentModified(new Date());

				//for each document instance in the record check to see that 
				//there is still sufficient positive consent. If not, add to a list
				//of doc instances awaiting deletion
				List<DocumentInstance> toRemove = new ArrayList<DocumentInstance>();
				for ( DocumentInstance docInst: record.getDocInstances()){
					if ( !checkConsent(docInst) ){
						//consent for this instance no longer exists, so it has 
						//to be deleted.
						toRemove.add(docInst);
					}
				}
				
				String[] result = new String[toRemove.size()];
				for ( int i=0, c=toRemove.size(); i<c; i++ ){
					result[i] = toRemove.get(i).getOccurrence().getCombinedDisplayText();
				}
				
				String[] updatedResult;
				try {
					updatedResult = checkEslImpactOfWithdrawingConsent(record, result, saml);
				} catch (EslException ex) {
					return ex;
				}
				
				//remove the record from the session so changes to the object are not 
				//persisted to the database
				session.evict(record);
				
				return updatedResult;
			}
		};
		
		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}
		else{
			return (String[])result;
		}

	}
	
	public void withdrawConsent(final Long recordId, final Long consentFormId, final String reason, final String userName, final String saml) 
	throws DAOException, EslException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				Record record = (Record)session.get(Record.class, recordId);
				if ( null == record ){
					return new DAOException("No record exists with the given id");
				}

				//find the consent for the consent form being withdrawn
				//and remove it
				ConsentForm cf = (ConsentForm)session.get(ConsentForm.class, consentFormId);
				if ( null == cf ){
					return new DAOException("No consent form exists with the given id");
				}
				Consent c = null;
				for (Consent con:record.getConsents()){
					if ( con.getConsentForm().getId().equals(cf.getId()) ){
						c = con;
					}
				}
				if ( null == c ){
					return new DAOException("No consent exists for the given consent form and record");
				}
				record.removeConsent(c, reason);
				record.setConsentModified(new Date());

				//for each document instance in the record check to see that 
				//there is still sufficient positive consent. If not, add to a list
				//of doc instances awaiting deletion
				List<DocumentInstance> toRemove = new ArrayList<DocumentInstance>();
				for ( DocumentInstance docInst: record.getDocInstances()){
					if ( !checkConsent(docInst) ){
						//consent for this instance no longer exists, so it has 
						//to be deleted.
						toRemove.add(docInst);
					}
				}
				//perform the actual deletion of objects marked to be deleted
				for ( DocumentInstance docInst: toRemove ){
					record.getDocInstances().remove(docInst);
					session.delete(docInst);
				}

				//if the ESL is used then also check to see if the corresponding
				//ESL subject needs to be deleted.
				//Also clear the external identifier 
				if( !record.checkConsentForEsl() ) {
					record.setExternalIdentifier(null);
					if ( record.getDataSet().isEslUsed() ) {
						sLog.info("withdrawConsent: Deleting or locking subject in the ESL");
						try{
							eslClient.handleConsentWithdrawn(record.getIdentifier().getIdentifier(), saml);
						}
						catch(EslException ex){
							return ex;
						}
					}
				}

				EntityInterceptor.setUserName(userName);
				session.saveOrUpdate(record);

				return null;
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( null != result ){
			if ( result instanceof DAOException ){
				throw (DAOException)result;
			}
			if ( result instanceof EslException ){
				throw (EslException)result;
			}
		}
	}

	public void addConsent(final Long recordId, final Long consentFormId, final String location, final String userName, final String saml) 
	throws DAOException, EslException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				Record record = (Record)session.get(Record.class, recordId);
				if ( null == record ){
					return new DAOException("No record exists with the given id");
				}

				//see if consent already exists for the consent form 
				//we are trying to add consent for
				ConsentForm cf = (ConsentForm)session.get(ConsentForm.class, consentFormId);
				if ( null == cf ){
					return new DAOException("No consent form exists with the given id");
				}
				Consent c = null;
				for (Consent con:record.getConsents()){
					if ( con.getConsentForm().getId().equals(cf.getId()) ){
						c = con;
					}
				}
				if ( null != c ){
					return new DAOException("Consent already exists for consent form id="+consentFormId);
				}

				//add the consent
				c = new Consent();
				c.setConsentForm(cf);
				c.setConsentGiven(true);
				c.setLocation(location);
				record.addConsent(c);
				record.setConsentModified(new Date());
				
				//save the record
				EntityInterceptor.setUserName(userName);
				session.saveOrUpdate(record);

				//if the ESL is used then also check to see if the corresponding
				//ESL subject needs to be unlocked.
				if ( record.getDataSet().isEslUsed() && record.checkConsentForEsl() ){
					sLog.info("addConsent: Unlocking subject in the ESL");
					try{
						eslClient.unlockSubject(record.getIdentifier().getIdentifier(), saml);
					}
					catch(EslNoSubjectException ex){
						//do nothing - it is possible that the subject has not
						//yet been added to the ESL
					}
					catch(EslException ex){
						return ex;
					}
				}

				return null;
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( null != result ){
			if ( result instanceof DAOException ){
				throw (DAOException)result;
			}
			if ( result instanceof EslException ){
				throw (EslException)result;
			}
		}
	}

	public void changeStatus(final Long statusedInstanceId, final Long newStatusId, final String userName, final boolean ignorePermittedTransitions) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				//find the instance whose status is to be changed
				StatusedInstance inst = (StatusedInstance)session.get(StatusedInstance.class, statusedInstanceId);
				if ( null == inst ){
					return new DAOException("No statused instance exists with the given id");
				}

				String currentStatus = inst.getStatus().getShortName();

				//find the status object to change its status to
				//NOTE: not using session.get as this was causing a ClassClassException
				//when the supplied id was for a Record, not a Status.
				//Status newStatus = (Status)session.get(Status.class, newStatusId);
				Status newStatus = (Status)session.createQuery("from Status s where s.id=?")
				.setLong(0, newStatusId)
				.uniqueResult();
				if ( null == newStatus ){
					return new DAOException("No status exists with the given id");
				}

				//check that it's OK to change the status to the new status
				try{
					inst.changeStatus(newStatus, ignorePermittedTransitions);
				}
				catch(ModelException ex){
					return new DAOException(ex);
				}

				if ( inst instanceof DocumentInstance ){
					DocumentInstance docInst = (DocumentInstance)inst;
					//we are changing the status on a document instance, so need to 
					//send an email
					String docName = docInst.getOccurrence().getDocument().getDisplayText() + "-"+ docInst.getOccurrence().getDisplayText();
					String identifier = docInst.getRecord().getIdentifier().getIdentifier();
					String createdBy = null;
					if ( inst.getHistoryCount() > 0 ){
						createdBy = inst.getHistory(0).getUser();
					}
					else{
						createdBy = inst.getCreatedBy();
					}

					sendDocumentStatusChangeEmail(identifier, currentStatus, newStatus.getShortName(), docName, createdBy);
					docInst.getRecord().setStatusModified(new Date());
				}
				else if ( inst instanceof Record ){
					Record r = (Record)inst;
					r.setStatusModified(new Date());
				}

				EntityInterceptor.setUserName(userName);
				session.saveOrUpdate(inst);
				return null;
			}
		};

		DAOException ex = (DAOException)getHibernateTemplate().execute(callback);
		if ( null != ex ){
			throw ex;
		}

	}

	public void changeStatus(final Long statusedInstanceId, final Long newStatusId, final String userName) throws DAOException {
		changeStatus(statusedInstanceId, newStatusId, userName, false);
	}

	public void changeRecordStatus(final String identifier, final Long newStatusId, final String userName, final boolean ignorePermittedTransitions, final String saml) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				String hql = "from Record r where r.identifier.identifier=? ";
				Record record = (Record)session.createQuery(hql)
											   .setString(0, identifier)
											   .uniqueResult();
				if ( null == record ){
					return new DAOException("No record exists for record identifier="+identifier);
				}
				
				//find the status object to change its status to
				//NOTE: not using session.get as this was causing a ClassClassException
				//when the supplied id was for a Record, not a Status.
				//Status newStatus = (Status)session.get(Status.class, newStatusId);
				Status newStatus = (Status)session.createQuery("from Status s where s.id=?")
												  .setLong(0, newStatusId)
												  .uniqueResult();
				if ( null == newStatus ){
					return new DAOException("No status exists with the given id");
				}

				//check that it's OK to change the status to the new status
				try{
					record.changeStatus(newStatus, ignorePermittedTransitions);
				}
				catch(ModelException ex){
					return new DAOException(ex);
				}

				record.setStatusModified(new Date());

				//if the ESL is used then update the subject accordingly
				if ( record.getDataSet().isEslUsed() ){
					try{
						modifyEslAccordingToRecordStatus(record, saml, newStatus.getGenericState());
					}
					catch(EslException ex){
						if(ex instanceof EslNoSubjectException){
							//Do nothing. If there's genuinely no subject, then it can't be modified.
						}else{
							return ex;
						}
						
					}
				}
				
				EntityInterceptor.setUserName(userName);
				session.saveOrUpdate(record);

				return null;
			}
		};

		DAOException ex = (DAOException)getHibernateTemplate().execute(callback);
		if ( null != ex ){
			throw ex;
		}

	}


	public void changeDocumentStatus(final String identifier, final Long docOccId, final Long newStatusId, final String userName) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				//find the instance whose status is to be changed
				String hql = "from DocumentInstance di "+
				"where di.record.identifier.identifier=? "+
				"and di.occurrence.id=?";
				DocumentInstance inst = (DocumentInstance)session.createQuery(hql)
				.setString(0, identifier)
				.setLong(1, docOccId)
				.uniqueResult();
				if ( null == inst ){
					return new DAOException("No document instance exists for record identifier="+identifier+
							" and document occurrence="+docOccId);
				}

				String currentStatus = inst.getStatus().getShortName();
				String docName = inst.getOccurrence().getDocument().getDisplayText() + "-"+ inst.getOccurrence().getDisplayText();
				
				String createdBy = null;
				if ( inst.getHistoryCount() > 0 ){
					createdBy = inst.getHistory(0).getUser();
				}
				else{
					createdBy = inst.getCreatedBy();
				}
				
				//find the status object to change its status to
				//NOTE: not using session.get as this was causing a ClassClassException
				//when the supplied id was for a Record, not a Status.
				//Status newStatus = (Status)session.get(Status.class, newStatusId);
				Status newStatus = (Status)session.createQuery("from Status s where s.id=?")
				.setLong(0, newStatusId)
				.uniqueResult();
				if ( null == newStatus ){
					return new DAOException("No status exists with the given id");
				}

				//check that it's OK to change the status to the new status
				try{
					inst.changeStatus(newStatus);
				}
				catch(ModelException ex){
					return new DAOException(ex);
				}

				EntityInterceptor.setUserName(userName);
				inst.getRecord().setStatusModified(new Date());
				session.saveOrUpdate(inst);

				sendDocumentStatusChangeEmail(identifier, currentStatus, newStatus.getShortName(), docName, createdBy);

				return null;
			}
		};

		DAOException ex = (DAOException)getHibernateTemplate().execute(callback);
		if ( null != ex ){
			throw ex;
		}
	}

	public long[] changeDocumentStatus(final String identifier, final List<Long> docOccIds, final String newStatus, final String userName) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				if ( null == newStatus ){
					return new DAOException("No status provided");
				}

				EntityInterceptor.setUserName(userName);

				List<String> documents = new ArrayList<String>();
				List<String> oldStatuses = new ArrayList<String>();
				Set<String> creators = new HashSet<String>();
				List<Long> notChangedIdList = new ArrayList<Long>();
				for ( Long docOccId: docOccIds ){

					//find the instance whose status is to be changed
					String hql = "from DocumentInstance di "+
					"where di.record.identifier.identifier=? "+
					"and di.occurrence.id=?";
					DocumentInstance inst = (DocumentInstance)session.createQuery(hql)
					.setString(0, identifier)
					.setLong(1, docOccId)
					.uniqueResult();
					if ( null == inst ){
						notChangedIdList.add(docOccId);
						sLog.error("changeDocumentStatus: No document instance exists for record identifier="+identifier+
								" and document occurrence="+docOccId);
						continue;
					}

					Status newS = null;
					for ( Status s: inst.getOccurrence().getDocument().getStatuses() ){
						if ( s.getShortName().equals(newStatus) ){
							newS = s;
							break;
						}
					}

					if ( null == newStatus ){
						notChangedIdList.add(docOccId);
						sLog.error("changeDocumentStatus: No status object exists for document="+inst.getOccurrence().getDocument().getId()+" with name '"+newStatus+"'");
						continue;
					}

					String currentStatus = inst.getStatus().getShortName();
					oldStatuses.add(currentStatus);
					String docName = inst.getOccurrence().getCombinedDisplayText();
					documents.add(docName);
					String createdBy = null;
					if ( inst.getHistoryCount() > 0 ){
						createdBy = inst.getHistory(0).getUser();
					}
					else{
						createdBy = inst.getCreatedBy();
					}
					creators.add(createdBy);

					//check that it's OK to change the status to the new status
					try{
						inst.changeStatus(newS);
					}
					catch(ModelException ex){
						notChangedIdList.add(docOccId);
						sLog.error(ex);
						continue;
					}

					inst.getRecord().setStatusModified(new Date());
					session.saveOrUpdate(inst);
				}             

				sendDocumentStatusChangeEmail(identifier, oldStatuses, newStatus, documents, creators);

				long[] result = new long[notChangedIdList.size()];
				for ( int i=0, c=result.length; i<c; i++ ){
					result[i] = notChangedIdList.get(i).longValue();
				}
				return result;
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}
		else{
			return (long[])result;
		}
	}

	public String getShortNameOfCurrentStatus(Long statusedInstanceId) throws DAOException {

		Iterator it = getHibernateTemplate().find("select si.status.shortName from StatusedInstance si where si.id=?", statusedInstanceId).iterator();
		String shortName = null;
		while ( it.hasNext() ){
			shortName = (String)it.next();
		}
		if ( null == shortName ){
			throw new DAOException("No statused instance exists for id="+statusedInstanceId+
			", or the statused instance does not have a status");
		}

		return shortName;
	}


	public String getShortNameOfCurrentStatus(String identifier, Long docOccId) throws DAOException {

		String hql = "select di.status.shortName from DocumentInstance di "+
		"where di.record.identifier.identifier=? "+
		"and di.occurrence.id=?";
		Object[] params = new Object[]{identifier, docOccId};
		Iterator it = getHibernateTemplate().find(hql, params).iterator();
		String shortName = null;
		while ( it.hasNext() ){
			shortName = (String)it.next();
		}
		if ( null == shortName ){
			throw new DAOException("No document instance exists for record identifier="+identifier+
					" and document occurrence="+docOccId+", or the document instance does not have a status");
		}

		return shortName;
	}

	public void updateResponseStatusAnnotation(final Long responseId, final ResponseStatus status, final String annotation) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				//find the response to update
				Response resp = (Response)session.get(Response.class, responseId);
				if ( null == resp ){
					return new DAOException("No response exists for id="+responseId);
				}

				resp.setStatus(status);
				resp.setAnnotation(annotation);

				session.saveOrUpdate(resp);

				return null;
			}
		};

		DAOException ex = (DAOException)getHibernateTemplate().execute(callback);
		if ( null != ex ){
			throw ex;
		}
	}

	public String[] getRecordsByGroups(final String project, final String[] groups) throws DAOException {
		HibernateCallback callback = new HibernateCallback(){
			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session){

				List<String> identifiers = (List<String>)session.createQuery(
						"select i.identifier from Identifier i, Record r " +
						"where r.identifier=i.id " +
						"and i.projectPrefix=:project "+
						"and i.groupPrefix in (:groups) "+
						"and r.deleted=:deleted "+
						"and r.status.enumGenericState not like :invalidState "+
						"order by i.identifier.groupPrefix, i.identifier.suffix asc")
									 	  .setParameter("project", project)
									 	  .setParameterList("groups", groups)
									 	  .setParameter("deleted", Boolean.FALSE)
									 	  .setParameter("invalidState", GenericState.INVALID.toString()).list();

				return identifiers.toArray(new String[identifiers.size()]);

			}
		}; 

		if ( 0 == groups.length){
			throw new DAOException("At least one group is required to execute this query");
		}
		return (String[]) getHibernateTemplate().execute(callback);
		
	}

	public org.psygrid.data.model.dto.RecordDTO[] getRecordsByGroups(final String project, final String[] groups, final Date referenceDate, final RetrieveDepth depth) throws DAOException {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				sLog.info("getRecordsByGroups: referenceDate="+referenceDate);

				//Had to use a Criteria query as for some reason a normal HQL query
				//ignores ">= date" terms in the where clause!
				Criteria c = session.createCriteria(Record.class);
				c.createAlias("identifier", "i");
				c.add( Restrictions.eq("i.projectPrefix", project));
				c.add( Restrictions.in("i.groupPrefix", groups));
				c.add( Restrictions.eq("deleted", Boolean.FALSE));
				c.add( Restrictions.or(
						Restrictions.ge("consentModified", referenceDate),
						Restrictions.ge("statusModified", referenceDate) ) );
				c.addOrder( Order.asc("i.identifier.groupPrefix"));
				c.addOrder( Order.asc("i.identifier.suffix"));
				c.setFetchSize(1000);

				List records = c.list();
				List<org.psygrid.data.model.dto.RecordDTO> dtoRecords = new ArrayList<org.psygrid.data.model.dto.RecordDTO>();
				for ( int i=0; i<records.size(); i++){
					Record r = (Record)records.get(i);
					sLog.info("Retrieved record "+r.getIdentifier().getIdentifier());
					if (isRecordValid(r)) { 
						dtoRecords.add(r.toDTO(depth));
					}
				}
				return dtoRecords;

			}
		};

		if ( 0 == groups.length){
			throw new DAOException("At least one group is required to execute this query");
		}
		List recordList = (List) getHibernateTemplate().execute(callback);
		return this.convertObjectListToRecordArray(recordList);
	}

	public org.psygrid.data.model.dto.RecordDTO[] getRecordsByGroups(final String project, final String[] groups, final Date referenceDate, final int batchSize, final int offset, final RetrieveDepth depth) throws DAOException {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				sLog.info("getRecordsByGroups: referenceDate="+referenceDate);

				//Had to use a Criteria query as for some reason a normal HQL query
				//ignores ">= date" terms in the where clause!
				Criteria c = session.createCriteria(Record.class);
				c.createAlias("identifier", "i");
				c.add( Restrictions.eq("i.projectPrefix", project));
				c.add( Restrictions.in("i.groupPrefix", groups));
				c.add( Restrictions.eq("deleted", Boolean.FALSE));
				c.add( Restrictions.or(
						Restrictions.ge("consentModified", referenceDate),
						Restrictions.ge("statusModified", referenceDate) ) );
				c.addOrder( Order.asc("i.groupPrefix"));
				c.addOrder( Order.asc("i.suffix"));
				c.setFetchSize(1000);

				List records = c.list();
				int thisBatchSize = 0;
				if ( records.size() - offset > batchSize ){
					thisBatchSize = batchSize;
				}
				else if ( records.size() - offset <= 0 ){
					thisBatchSize = 0;
				}
				else{
					thisBatchSize = records.size() - offset;
				}

				List<org.psygrid.data.model.dto.RecordDTO> dtoRecords = new ArrayList<org.psygrid.data.model.dto.RecordDTO>();
				for ( int i=offset; i<offset+thisBatchSize; i++ ){
					Record r = (Record)records.get(i);
					sLog.info("Retrieved record at index "+i+": "+r.getIdentifier().getIdentifier());
					if (isRecordValid(r)) {
						dtoRecords.add(r.toDTO(depth));
					}
				}
				return dtoRecords;

			}
		};

		if ( 0 == groups.length){
			throw new DAOException("At least one group is required to execute this query");
		}
		List recordList = (List) getHibernateTemplate().execute(callback);
		return this.convertObjectListToRecordArray(recordList);
	}

	public String[] getRecordsByGroupsAndDocStatus(final String project, final String[] groups, final String status) throws DAOException {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				StringBuilder newHQL = new StringBuilder();
				newHQL.append("select i.identifier from Identifier i where i.projectPrefix=:project " +
						"and groupPrefix in (:groups) and i.id in " +
						"(select r.identifier.id from Record r where r.deleted=:deleted and r.id in " +
						"(select di.record.id from DocumentInstance di where di.status.shortName=:status)) " +
				"order by i.groupPrefix, i.suffix asc");
				Query q2 = session.createQuery(newHQL.toString());
				q2.setString("project", project);
				q2.setParameterList("groups", groups);
				q2.setString("status", status);
				q2.setBoolean("deleted", false);
				q2.setFetchSize(1000);

				List ids = q2.list();
				return ids;
			}
		};

		if ( 0 == groups.length){
			throw new DAOException("At least one group is required to execute this query");
		}
		List recordList = (List) getHibernateTemplate().execute(callback);
		String[] identifiers = new String[recordList.size()];
		recordList.toArray(identifiers);
		return identifiers;
	}

	/**
	 * Converts an object list containing objects of type dto.Record to an array of object of dto.Record.
	 * This is required because hibernate callbacks return an object, and an object cannot be directly
	 * converted to a typed array.
	 * @param list - the list of objects - object MUST be of type dto.Record
	 * @return - and array dto.Record objects.
	 */
	private org.psygrid.data.model.dto.RecordDTO[] convertObjectListToRecordArray(List list){
		int size = list.size();
		org.psygrid.data.model.dto.RecordDTO[] recordArray = new org.psygrid.data.model.dto.RecordDTO[size];
		for(int i = 0; i < size; i++){
			recordArray[i] = (org.psygrid.data.model.dto.RecordDTO)list.get(i); 
		}
		return recordArray;
	}

	public String[] getDeletedRecordsByGroups(final String project, final String[] groups, final Date referenceDate) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				Query query = session.createQuery("select r.identifier.identifier from Record r " +
						"left join r.identifier i " +
						"where i.projectPrefix=:project " + 
						"and i.groupPrefix in (:groups) " +
						"and ( (r.deleted=:deleted " +
						"and r.edited > :edited )" +
						"or ( r.status.enumGenericState=:state " +
						"and r.statusModified > :modified) )" +
				"order by r.identifier.identifier")
				.setParameter("project", project)
				.setParameterList("groups", groups)
				.setParameter("deleted", Boolean.TRUE)
				.setParameter("edited", referenceDate)
				.setParameter("state", GenericState.INVALID.toString())
				.setParameter("modified", referenceDate);


				List results = query.list();
				String[] identifiers = new String[results.size()];
				for ( int i=0; i<results.size(); i++){
					identifiers[i] = (String)results.get(i);
				}
				return identifiers;

			}
		};

		if ( 0 == groups.length){
			throw new DAOException("At least one group is required to execute this query");
		}
		return (String[])getHibernateTemplate().execute(callback);

	}    


	public org.psygrid.data.model.dto.IdentifierDTO addIdentifier(org.psygrid.data.model.dto.IdentifierDTO identifier) throws DAOException {

		try{
			Identifier id = identifier.toHibernate();
			getHibernateTemplate().save(id);
			getHibernateTemplate().flush();
			return id.toDTO();
		}
		catch(DataAccessException ex){
			throw new DAOException(ex);
		}

	}

	public boolean isObjectARecord(Long objectId){
		Object result = getHibernateTemplate().get(Record.class, objectId);
		if ( null == result ){
			return false;
		}
		else{
			return true;
		}
	}

	public boolean isObjectADocument(Long objectId){
		Object result = getHibernateTemplate().get(DocumentInstance.class, objectId);
		if ( null == result ){
			return false;
		}
		else{
			return true;
		}
	}

	public void removeRecordsForDataSet(final Long dataSetId, final String projectCode) throws DAOException {

		if (dataSetId == null || projectCode == null) {
			throw new DAOException("Data Set identifier or project code is null.");
		}

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				Iterator results = session.createQuery("from Record r where r.dataSet.id=? and r.dataSet.projectCode=?")
				.setLong(0, dataSetId)
				.setString(1, projectCode)
				.iterate();

				int counter = 0;
				while ( results.hasNext() ){
					session.delete(results.next());
					counter++;
					sLog.info("Deleted record "+counter);
				}

				return null;
			}
		};

		getHibernateTemplate().execute(callback);

	}

	public void removeRecordCircularRef(final Long dataSetId, final String projectCode) throws DAOException {

		if (dataSetId == null || projectCode == null) {
			throw new DAOException("Data Set identifier or project code is null.");
		}

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				List records = session.createQuery("from Record r where r.dataSet.id=? and r.dataSet.projectCode=?")
				.setLong(0, dataSetId)
				.setString(1, projectCode)
				.list();

				for ( Object o: records ){
					Record r = (Record)o;
					r.setRecord(null);
					session.saveOrUpdate(r);
				}

				return null;
			}
		};

		getHibernateTemplate().execute(callback);
	}

	public void removeRecordCircularRef(final String identifier) throws DAOException {

		if (identifier == null) {
			throw new DAOException("Record identifier is null.");
		}

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				Record record = (Record)session.createQuery("from Record r where r.identifier.identifier=?")
				.setString(0, identifier)
				.uniqueResult();

				if ( null == record ){
					return new DAOException("No Record exists with the identifier '"+identifier+"'");
				}

				record.setRecord(null);
				session.saveOrUpdate(record);

				return null;
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}
	}

	public void removeRecord(final String identifier) throws DAOException {
		if (identifier == null) {
			throw new DAOException("Record identifier is null.");
		}

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				Record record = (Record)session.createQuery("from Record r where r.identifier.identifier=?")
				.setString(0, identifier)
				.uniqueResult();

				if ( null == record ){
					return new DAOException("No Record exists with the identifier '"+identifier+"'");
				}

				record.setRecord(null);
				session.delete(record);

				return null;
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}
	}

	public boolean removeRecordForDataSet(final Long dataSetId, final String projectCode) throws DAOException {

		if (dataSetId == null || projectCode == null) {
			throw new DAOException("Data Set identifier or project code is null.");
		}

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				List results = session.createQuery("from Record r where r.dataSet.id=? and r.dataSet.projectCode=?")
				.setLong(0, dataSetId)
				.setString(1, projectCode)
				.list();

				Boolean moreRecords = null;
				if ( results.size() > 0 ){
					moreRecords = new Boolean(true);
					//delete the record
					session.delete(results.get(0));
				}
				else{
					moreRecords = new Boolean(false);
				}

				return moreRecords;
			}
		};

		Boolean result = (Boolean)getHibernateTemplate().execute(callback);
		return result.booleanValue();

	}

	public void deleteRecord(final String identifier) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				Record record = (Record)session.createQuery("from Record r where r.identifier.identifier=?")
				.setString(0, identifier)
				.uniqueResult();

				if ( null == record ){
					return new DAOException("No Record exists with the identifier '"+identifier+"'");
				}

				for ( DocumentInstance di: record.getDocInstances() ){
					session.delete(di);
				}

				//remove all the document instances
				record.getDocInstances().clear();

				//set the deleted flag to true
				record.setDeleted(true);

				//save the record
				session.saveOrUpdate(record);

				return null;
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}

	}

	public org.psygrid.data.model.dto.RecordDTO getRecordSingleDocument(final Long recordId, final Long docInstId) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				Record record = (Record)session.createQuery("from Record r where r.id=? and r.deleted=?")
				.setLong(0, recordId)
				.setBoolean(1, false)
				.uniqueResult();
				org.psygrid.data.model.dto.RecordDTO dtoR = null;
				if ( null != record ){
					//set the database id of the record to null, otherwise we
					//won't be able to save the document again
					record.setId(null);
					dtoR = record.toDTO(RetrieveDepth.RS_NO_BINARY, docInstId);
				}

				if (isRecordValid(dtoR)) {
					return dtoR;

				}
				return null;
			}
		};

		org.psygrid.data.model.dto.RecordDTO r = (org.psygrid.data.model.dto.RecordDTO)getHibernateTemplate().execute(callback);
		if ( null == r ){
			throw new DAOException("No Record exists in the repository for record with id = "+recordId);
		}

		return r;
	}

	public org.psygrid.data.model.dto.RecordDTO getRecordSingleDocumentForOccurrence(final String identifier, final Long docOccId) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				Record record = (Record)session.createQuery("from Record r where r.identifier.identifier=? and r.deleted=?")
				.setString(0, identifier)
				.setBoolean(1, false)
				.uniqueResult();
				org.psygrid.data.model.dto.RecordDTO dtoR = null;

				DocumentOccurrence occurrence = (DocumentOccurrence)session.get(DocumentOccurrence.class, docOccId);

				if ( null != record ){
					//set the database id of the record to null, otherwise we
					//won't be able to save the document again
					record.setId(null);

					DocumentInstance inst = null;
					if (occurrence != null) {
						inst = record.getDocumentInstance(occurrence);
					}
					Long docInstId = null;
					if (inst != null) {
						docInstId = inst.getId();
					}
					dtoR = record.toDTO(RetrieveDepth.RS_NO_BINARY, docInstId);
				}

				if (isRecordValid(dtoR)) {
					return dtoR;

				}
				return null;
			}
		};

		org.psygrid.data.model.dto.RecordDTO r = (org.psygrid.data.model.dto.RecordDTO)getHibernateTemplate().execute(callback);
		if ( null == r ){
			throw new DAOException("No Record exists in the repository for record with the identifier = "+identifier);
		}

		return r;
	}

	/**
	 * For a single document instance check that there is sufficient positive consent
	 * to be able to save the instance.
	 * 
	 * @param inst The document instance to check.
	 * @return True if there is sufficient consent, false otherwise.
	 */
	private boolean checkConsent(DocumentInstance inst) {

		Document d = inst.getOccurrence().getDocument();
		boolean docConsent = true;
		for (ConsentFormGroup cfg: d.getConFrmGrps()){
			boolean grpConsent = false;
			for (PrimaryConsentForm cf:cfg.getConsentForms()){
				boolean pcfConsent = false;
				Consent c = inst.getRecord().getConsent(cf);
				if ( null != c ){
					pcfConsent = c.isConsentGiven();
					if ( pcfConsent ){
						//check associated consent forms
						for (AssociatedConsentForm acf:cf.getAssociatedConsentForms()){
							Consent ac = inst.getRecord().getConsent(acf);
							if ( null == ac ){
								pcfConsent &= false;
							}
							else{
								pcfConsent &= ac.isConsentGiven();
							}
						}
					}
				}
				//consent must be obtained for one of the primary consent forms
				//in the consent form group
				grpConsent |= pcfConsent;
			}
			//consent must be obtained for all of the consent form groups associated
			//with the document
			docConsent &= grpConsent;
		}

		return docConsent;
	}

	/**
	 * Helper method that does the work for finding the reminders that need
	 * to be sent on the current day for a single record.
	 * 
	 * @param elements List of elements to check if any are scheduled for today.
	 * @param instances List of element instances equivalent to the list of elements.
	 * @param reminders List of email reminders to add reminders to.
	 * @param now The current date.
	 * @param start The start date for the record.
	 * @param baseTime The base date, which all times used in the method are 
	 * relative to.
	 */
	private void findReminders(List<Document> elements,
			Set<DocumentInstance> instances,
			List<SimpleMailMessage> reminders,
			Date now,
			Record r,
			Date baseDate) throws DAOException{

		try{

			for (Element e:elements){
				Document d = (Document)e;
				//see if it has occurrences that are scheduled (and ignore occurrences that are locked)
				for ( DocumentOccurrence o: d.getOccurrences() ){
					if ( null != o.getScheduleTime() && null != o.getScheduleUnits() && !o.isLocked()){
						//find the time when the occurrence is scheduled for
						Calendar scheduledTime = Calendar.getInstance();
						scheduledTime.setTime(baseDate);
						TimeUnits units = o.getScheduleUnits();
						Integer time = o.getScheduleTime();
						scheduledTime.add(getCalendarUnit(units), time);
						//see if any of this occurrence's reminders are scheduled for today
						for ( Reminder reminder:o.getReminders() ){
							Calendar cal = Calendar.getInstance();
							cal.setTime(baseDate);
							cal.add(getCalendarUnit(reminder.getTimeUnits()), reminder.getTime());
							if ( now.equals(cal.getTime()) ){
								//this reminder is scheduled for today!
								//check to see if an instance exists
								boolean sendMessage = true;
								for ( DocumentInstance di:instances){
									if ( di.getOccurrence().getDocument().equals(e) && di.getOccurrence().equals(o) ){
										//an instance already exists, so no need to send a reminder
										sendMessage = false;
										break;
									}
								}
								try{
									if ( sendMessage ){
										//look up email addresses to send the reminder to
										List<InternetAddress> addresses = 
											aaqc.lookUpEmailAddress(new ProjectType(null, r.getIdentifier().getProjectPrefix(), null, null, false),
													new GroupType(null, r.getIdentifier().getGroupPrefix(), null),
													new RoleType(officerRole, null));
										InternetAddress creator = aaqc.lookUpEmailAddress(r.getHistory(0).getUser());
										if ( null != creator && !addresses.contains(creator) ){
											addresses.add(creator);
										}

										if ( 0 == addresses.size() ){
											try{
												addresses.add(new InternetAddress(sysAdminEmail));
											}
											catch(AddressException ex){
												//just log a warning - sys admin address should be static
												sLog.warn("System administrator email address is not valid");
											}                    
										}
										Map<String, String> msgText = reminder.generateMessage(e.getDisplayText(),
												o.getName(),
												r.getIdentifier().getIdentifier(),
												scheduledTime.getTime());

										String[] addressArray = new String[addresses.size()];
										for (int i=0; i<addresses.size(); i++){
											addressArray[i] = addresses.get(i).getAddress();
										}

										SimpleMailMessage message = new SimpleMailMessage();
										message.setSubject(msgText.get(Reminder.SUBJECT));
										message.setText(msgText.get(Reminder.BODY));
										message.setTo(addressArray);
										message.setFrom(sysAdminEmail);
										message.setSentDate(new Date());
										reminders.add(message);
									}
								}
								catch(PGSecurityException ex){
									sLog.error("Unable to look up email address for project='"+r.getIdentifier().getProjectPrefix()+"', "+
											"group='"+r.getIdentifier().getGroupPrefix()+"', role='"+officerRole+"'.", ex);
								}
							}
						}
					}
				}
			}
		}
		catch(NotAuthorisedFaultMessage ex){
			throw new DAOException("Not authorised to connect to attribute authority query client.", ex);
		}
		catch(ConnectException ex){
			throw new DAOException("Cannot connect to attribute authority query client.", ex);
		}
	}

	/**
	 * Get the equivalent Calendar constant value for a given time unit.
	 * 
	 * @param timeUnit The time unit.
	 * @return The Calendar constant.
	 */
	private int getCalendarUnit(TimeUnits timeUnit){
		int unit = 0;
		if ( TimeUnits.DAYS == timeUnit){
			unit = Calendar.DAY_OF_MONTH;
		}
		else if ( TimeUnits.WEEKS == timeUnit){
			unit = Calendar.WEEK_OF_YEAR;
		}
		else if ( TimeUnits.MONTHS == timeUnit){
			unit = Calendar.MONTH;
		}
		else if ( TimeUnits.YEARS == timeUnit){
			unit = Calendar.YEAR;
		}
		return unit;
	}

	private Date removeTimeComponent(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.clear(Calendar.MILLISECOND);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.HOUR);
		cal.clear(Calendar.HOUR_OF_DAY);
		cal.clear(Calendar.AM_PM);
		return cal.getTime();
	}

	private void sendDocumentStatusChangeEmail(String identifier, List<String> oldStatuses, String newStatus, List<String> documents, Set<String> creators){

		String project = null;
		String group = null;

		try{
			project = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
			group = IdentifierHelper.getGroupCodeFromIdentifier(identifier);

			//find all the roles this email needs to be sent to
			Set<String> emailRoles = new HashSet<String>();
			//In some cases we also send an email to the creator of the document
			//instance whose state has been changed. I _think_ this is to cover
			//the case when a user who isn't a CRO added the document instance e.g.
			//if a CPM added it.
			//However, this email only needs to be sent when a document instance
			//is being rejected - this flag is set to true in this case.
			boolean sendToCreator = false;
			for ( String oldStatus: oldStatuses ){

				String emailRole = null;
				if ( Status.DOC_STATUS_PENDING.equals(newStatus) &&
						(Status.DOC_STATUS_REJECTED.equals(oldStatus) ||
								Status.DOC_STATUS_COMPLETE.equals(oldStatus)) ){
					//officer has just moved a document to the pending approval
					//state, so an email needs to be sent to the manager
					emailRole = this.managerRole;
				}
				else if ( Status.DOC_STATUS_REJECTED.equals(newStatus) ){
					//document has been moved to the rejected state, so
					//an email needs to be sent to the officer
					emailRole = this.officerRole;
					sendToCreator = true;
				}
				else if ( Status.DOC_STATUS_INCOMPLETE.equals(newStatus) ){
					//document has been moved back to the incomplete state
					//by the officer, so send email to the manager
					emailRole = this.managerRole;
				}
				if ( null != emailRole ){
					emailRoles.add(emailRole);
				}
			}
			
			if ( emailRoles.isEmpty() ){
				//if no roles found to send the emails to then exit
				return;
			}
			
			Set<InternetAddress> addresses = new HashSet<InternetAddress>();
			try{
				for ( String emailRole: emailRoles ){
					//look up email addresses to send the reminder to
					addresses.addAll(aaqc.lookUpEmailAddress(new ProjectType(null, project, null, null, false),
							new GroupType(null, group, null),
							new RoleType(emailRole, null)));
				}
				if ( sendToCreator ){
					for ( String creator: creators ){
						InternetAddress addr = aaqc.lookUpEmailAddress(creator);
						if ( null != addr ){
							addresses.add(addr);
						}
					}
				}
			}
			catch(Exception ex){
				//catch exceptions when trying to look up email addresses
				sLog.error("Exception from AAQC", ex);
			}

			if ( 0 == addresses.size() ){
				try{
					addresses.add(new InternetAddress(sysAdminEmail));
				}
				catch(AddressException ex){
					//just log a warning - sys admin address should be static
					sLog.warn("System administrator email address is not valid");
				}                    
			}

			String subject = "PSYGRID: Record "+identifier+" - documents marked as "+newStatus;
			StringBuilder body = new StringBuilder();
			body.append("Record: ");
			body.append(identifier);
			body.append("\n\n");
			body.append("The following documents have been moved to the '");
			body.append(newStatus);
			body.append("' state:\n\n");
			for ( String doc: documents ){
				body.append(doc);
				body.append("\n");
			}

			SimpleMailMessage message = new SimpleMailMessage();
			String[] addressArray = new String[addresses.size()];
			int counter = 0;
			for ( InternetAddress address: addresses ){
				addressArray[counter] = address.getAddress();
				counter++;
			}
			message.setTo(addressArray);
			message.setFrom(sysAdminEmail);
			message.setSentDate(new Date());
			message.setSubject(subject);
			message.setText(body.toString());
			boolean mailSent = false;
			if ( sendMails ){
				if ( null == mailSender ){
					sLog.warn("The mail sender has not been initialized - it is not possible to send a document status change notification.");
				}
				else{
					try{
						mailSender.send(message);
						mailSent = true;
					}
					catch(Exception ex){
						sLog.error("Exception from mailSender", ex);
					}
				}
			}
			if ( !mailSent ){
				StringBuilder emails = new StringBuilder();
				for ( int i=0; i<message.getTo().length; i++ ){
					if ( i > 0 ){
						emails.append("; ");
					}
					emails.append(message.getTo()[i]);
				}
				sLog.info("Email: To="+emails.toString());
				sLog.info("Email: Subject="+message.getSubject());
				sLog.info("Email: Body="+message.getText());
			}
		}
		catch(InvalidIdentifierException ex){
			//do nothing - it should not be possible to get this far with an
			//identifier in the wrong format
			sLog.error("Invalid project or group code from identifier '"+identifier, ex);
		}
		catch(Exception ex){
			//catch all other exceptions and log them - don't want to do any
			//further handling as sending emails is a secondary task, and its
			//failure should not affect the execution of the primary task
			sLog.error("Exception", ex);
		}	    	

	}

	private void sendDocumentStatusChangeEmail(String identifier, String oldStatus, String newStatus, String document, String createdBy){

		final String METHOD_NAME = "sendDocumentStatusChangeEmail";

		String project = null;
		String group = null;

		try{
			project = IdentifierHelper.getProjectCodeFromIdentifier(identifier);
			group = IdentifierHelper.getGroupCodeFromIdentifier(identifier);

			//In some cases we also send an email to the creator of the document
			//instance whose state has been changed. I _think_ this is to cover
			//the case when a user who isn't a CRO added the document instance e.g.
			//if a CPM added it.
			//However, this email only needs to be sent when a document instance
			//is being rejected - this flag is set to true in this case.
			boolean sendToCreator = false;
			String emailRole = null;
			if ( Status.DOC_STATUS_PENDING.equals(newStatus) &&
					(Status.DOC_STATUS_COMPLETE.equals(oldStatus) ||
							Status.DOC_STATUS_REJECTED.equals(oldStatus) ) ){
				//officer has just moved a document to the pending approval
				//state, so an email needs to be sent to the manager
				emailRole = this.managerRole;
			}
			else if ( Status.DOC_STATUS_REJECTED.equals(newStatus) ){
				//document has been moved to the rejected state, so
				//an email needs to be sent to the officer
				emailRole = this.officerRole;
				sendToCreator = true;
			}
			
			if ( null != emailRole ){

				//look up email addresses to send the reminder to
				List<InternetAddress> addresses = aaqc.lookUpEmailAddress(new ProjectType(null, project, null, null, false),
						new GroupType(null, group, null),
						new RoleType(emailRole, null));
				if ( sendToCreator ){
					InternetAddress creator = aaqc.lookUpEmailAddress(createdBy);
					if ( null != creator && !addresses.contains(creator) ){
						addresses.add(creator);
					}
				}

				if ( 0 == addresses.size() ){
					try{
						addresses.add(new InternetAddress(sysAdminEmail));
					}
					catch(AddressException ex){
						//just log a warning - sys admin address should be static
						sLog.warn("System administrator email address is not valid");
					}                    
				}
				String subject = "PSYGRID: Record "+identifier+" - document marked as "+newStatus;
				String body = "Record: "+identifier+"\n"+"Document: "+document+"\n\n"+
				"This document has been moved to the '"+newStatus+"' state.";
				SimpleMailMessage message = new SimpleMailMessage();
				String[] addressArray = new String[addresses.size()];
				for (int i=0; i<addresses.size(); i++){
					addressArray[i] = addresses.get(i).getAddress();
				}
				message.setTo(addressArray);
				message.setFrom(sysAdminEmail);
				message.setSentDate(new Date());
				message.setSubject(subject);
				message.setText(body);
				boolean mailSent = false;
				if ( sendMails ){
					if ( null == mailSender ){
						sLog.warn("The mail sender has not been initialized - it is not possible to send a document status change notification.");
					}
					else{
						mailSender.send(message);
						mailSent = true;
					}
				}
				if ( !mailSent ){
					StringBuilder emails = new StringBuilder();
					for ( int i=0; i<message.getTo().length; i++ ){
						if ( i > 0 ){
							emails.append("; ");
						}
						emails.append(message.getTo()[i]);
					}
					sLog.info("Email: To="+emails.toString());
					sLog.info("Email: Subject="+message.getSubject());
					sLog.info("Email: Body="+message.getText());
				}
			}
		}
		catch(InvalidIdentifierException ex){
			//do nothing - it should not be possible to get this far with an
			//identifier in the wrong format
		}
		catch(Exception ex){
			//catch all other exceptions and log them - don't want to do any
			//further handling as sending emails is a secondary task, and its
			//failure should not affect the execution of the primary task
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
		}

	}

	public List<SimpleMailMessage> getAllMonthlySummaries(Date now) throws DAOException {
		List<SimpleMailMessage> reminders = new ArrayList<SimpleMailMessage>();
		Iterator dataSets = getHibernateTemplate().find("from DataSet ds where ds.sendMonthlySummaries=true").iterator();
		while ( dataSets.hasNext() ){
			DataSet ds = (DataSet)dataSets.next();
			sLog.info("getAllMonthlySummaries: dataset="+ds.getId());
			reminders.addAll(getMonthlySummariesForDataset(removeTimeComponent(now), ds.getId()));
		}
		return reminders;
	}

	@SuppressWarnings("unchecked")
	public List<SimpleMailMessage> getMonthlySummariesForDataset(final Date now, final Long dataSetId) throws DAOException {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				List<SimpleMailMessage> emails = new ArrayList<SimpleMailMessage>();

				//Get all records for the given dataset
				Iterator records = session.createCriteria(Record.class)
				.add(Restrictions.eq("dataSet.id", dataSetId))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.list()
				.iterator();
				while ( records.hasNext() ){
					Record r = (Record)records.next();
					sLog.info("getMonthlySummariesForDataset: record="+r.getId());
					if ( null == r.getStatus() || !r.getStatus().isInactive() ){
						//only send summaries for records with an "active" status
						try{
							//use the schedule start date as the base, unless this is
							//null in which case we use the study entry date, unless this too
							//is null, in which case we use the date of creation of the record
							Date start = null;
							if ( null != r.getScheduleStartDate() ){
								start = removeTimeComponent(r.getScheduleStartDate());
							}
							else if ( null != r.getStudyEntryDate() ){
								start = removeTimeComponent(r.getStudyEntryDate());
							}
							else{
								//start = removeTimeComponent(r.getCreated());
								start = removeTimeComponent(r.getHistory(0).getWhen());
							}

							SimpleMailMessage msg = buildMonthlySummaryEmailForRecord(r, now, start);
							if ( null != msg ){
								emails.add(msg);
							}

						}
						catch(DAOException ex){
							return ex;
						}
					}
				}

				return emails;
			}
		};

		sLog.info("getMonthlySummariesForDataset: now="+now+"; dataset="+dataSetId);
		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}
		else{
			return (List<SimpleMailMessage>)result;
		}

	}

	private SimpleMailMessage buildMonthlySummaryEmailForRecord(Record record, Date now, Date baseDate) throws DAOException{

		sLog.info("buildMonthlySummaryEmailForRecord called for record="+record.getId()+", now="+now+", baseDate="+baseDate);

		SimpleMailMessage msg = null;

		try{

			//look up email addresses to send the monthly summary to
			List<InternetAddress> addresses = 
				aaqc.lookUpEmailAddress(new ProjectType(null, record.getIdentifier().getProjectPrefix(), null, null, false),
						new GroupType(null, record.getIdentifier().getGroupPrefix(), null),
						new RoleType(officerRole, null));
			InternetAddress creator = aaqc.lookUpEmailAddress(record.getHistory(0).getUser());
			if ( null != creator && !addresses.contains(creator) ){
				addresses.add(creator);
			}

			if ( 0 == addresses.size() ){
				try{
					addresses.add(new InternetAddress(sysAdminEmail));
				}
				catch(AddressException ex){
					//just log a warning - sys admin address should be static
					sLog.warn("System administrator email address is not valid");
				}                    
			}

			String addressList = "";
			for ( InternetAddress a: addresses ){
				addressList+=a.getAddress();
				addressList+="; ";
			}            

			StringBuilder msgBody = new StringBuilder();
			for (Document d:record.getDataSet().getDocuments()){
				//see if it has occurrences that are scheduled (and ignore occurrences that are locked)
				for ( DocumentOccurrence o: d.getOccurrences() ){
					if ( null != o.getScheduleTime() && null != o.getScheduleUnits() && !o.isLocked() ){
						//find the time when the occurrence is scheduled for
						Calendar scheduledTime = Calendar.getInstance();
						scheduledTime.setTime(baseDate);
						TimeUnits units = o.getScheduleUnits();
						Integer time = o.getScheduleTime();
						scheduledTime.add(getCalendarUnit(units), time);

						//is this in the same month as the "now" date?
						Calendar nowCal = Calendar.getInstance();
						nowCal.setTime(now);                    
						if ( nowCal.get(Calendar.MONTH) == scheduledTime.get(Calendar.MONTH) 
								&& nowCal.get(Calendar.YEAR) == scheduledTime.get(Calendar.YEAR)){
							//this scheduled occurrence is scheduled to occur in the next month

							//see if an instance already exists for this occurrence, if it does
							//then there is no need to add it to the monthly summary email
							boolean sendMessage = true;
							for ( DocumentInstance di:record.getDocInstances()){
								if ( di.getOccurrence().getDocument().equals(d) && di.getOccurrence().equals(o) ){
									//an instance already exists, so no need to send a reminder
									sendMessage = false;
									break;
								}
							}

							if ( sendMessage ){
								msgBody.append("The assessment '");
								msgBody.append(o.getCombinedDisplayText()).append("' ");
								msgBody.append("is due to be completed on or soon after ");
								SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
								msgBody.append(dateFormatter.format(scheduledTime.getTime()));
								msgBody.append(".\n\n");
							}
						}
					}
				}
			}



			String[] addressArray = new String[addresses.size()];
			for (int i=0; i<addresses.size(); i++){
				addressArray[i] = addresses.get(i).getAddress();
			}

			if ( msgBody.length() > 0 ){
				msg = new SimpleMailMessage();
				String studyNumber = record.getIdentifier().getIdentifier();
				SimpleDateFormat monthYearFormatter = new SimpleDateFormat("MMMM yyyy");
				String monthYear = monthYearFormatter.format(now);
				msg.setSubject("PSYGRID: Monthly summary for Record "+studyNumber+" - "+monthYear);
				msg.setTo(addressArray);
				msg.setFrom(sysAdminEmail);
				msg.setSentDate(new Date());
				msg.setText("The following assessments are due to be completed for record "+
						studyNumber+" during "+monthYear+":\n\n"+msgBody.toString());
			}

		}
		catch(PGSecurityException ex){
			sLog.error("Unable to look up email address for project='"+record.getIdentifier().getProjectPrefix()+"', "+
					"group='"+record.getIdentifier().getGroupPrefix()+"', role='"+officerRole+"'.", ex);
		}
		catch(NotAuthorisedFaultMessage ex){
			throw new DAOException("Not authorised to connect to attribute authority query client.", ex);
		}
		catch(ConnectException ex){
			throw new DAOException("Cannot connect to attribute authority query client.", ex);
		}

		return msg;
	}


	private void sendReviewReminder(int count, String project, String group){
		try{
			sLog.info("Sending review reminder: count="+count+"; project="+project+"; group="+group);
			List<InternetAddress> addresses = aaqc.lookUpEmailAddress(new ProjectType(null, project, null, null, false),
					new GroupType(null, group, null),
					new RoleType(managerRole, null));

			String subject = "PSYGRID: Review and Approve reminder";
			String body = 
				"The "+count+"th record for project '"+project+"', group '"+group+"' has just been saved in the " +
				"PsyGrid data repository.";
			SimpleMailMessage message = new SimpleMailMessage();
			String[] addressArray = new String[addresses.size()];
			for (int i=0; i<addresses.size(); i++){
				addressArray[i] = addresses.get(i).getAddress();
			}
			message.setTo(addressArray);
			message.setFrom(sysAdminEmail);
			message.setSentDate(new Date());
			message.setSubject(subject);
			message.setText(body);
			boolean mailSent = false;
			if ( sendMails ){
				if ( null == mailSender ){
					sLog.warn("The mail sender has not been initialized - it is not possible to send a review reminder notification.");
				}
				else{
					mailSender.send(message);
					mailSent = true;
				}
			}
			if ( !mailSent ){
				StringBuilder emails = new StringBuilder();
				for ( int i=0; i<message.getTo().length; i++ ){
					if ( i > 0 ){
						emails.append("; ");
					}
					emails.append(message.getTo()[i]);
				}
				sLog.info("Email: To="+emails.toString());
				sLog.info("Email: Subject="+message.getSubject());
				sLog.info("Email: Body="+message.getText());
			}

		}
		catch(Exception ex){
			//do nothing, just log - this is a secondary action so it is not vital that it proceeds
			sLog.error("sendReviewReminder: "+ex.getClass().getSimpleName(),ex);
		}
	}

	public void updateRecordMetadata(final Long recordId, final org.psygrid.data.model.dto.RecordDataDTO recordData, final String reason, final String userName) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				Record r = (Record)session.createCriteria(Record.class)
				.add(Restrictions.idEq(recordId))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.uniqueResult();

				if ( null == r ){
					return new DAOException("No record exists with id="+recordId);
				}

				RecordData rd = recordData.toHibernate();
				r.setRecordData(rd, reason);

				EntityInterceptor.setUserName(userName);
				session.saveOrUpdate(r);

				return null;
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}

	}



	public String[] getLinkableRecords(final String project, final String[] groups) throws DAOException {
		HibernateCallback callback = new HibernateCallback(){
			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session){

				Query query = session.createQuery(
						"select i.identifier " +
						"from Record r, DataSet d, Identifier i " +
						"where r.dataSet=d.id " +
						"and r.identifier=i.id " +
						"and i.projectPrefix=:project "+
						"and i.groupPrefix in (:groups) "+
						"and r.deleted=:deleted "+
						"and r.status.enumGenericState not like :invalidState "+
						"and d.secondaryProjectCode is not null " +
						"and r.secondaryIdentifier is null " +
						"order by i.identifier.groupPrefix, i.identifier.suffix asc")
									.setParameter("project", project)
									.setParameterList("groups", groups)
									.setParameter("deleted", Boolean.FALSE)
									.setParameter("invalidState", GenericState.INVALID.toString());

				List<String> identifiers = (List<String>)query.list();
				return identifiers.toArray(new String[identifiers.size()]);

			}
		}; 

		if ( 0 == groups.length){
			throw new DAOException("At least one group is required to execute this query");
		}
		return (String[])getHibernateTemplate().execute(callback);
	}

	public String[] getLinkedRecords(final String project, final String[] groups) throws DAOException {
		HibernateCallback callback = new HibernateCallback(){
			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session){

				Query query = session.createQuery(
						"select i.identifier from Identifier i, Record r " +
						"where r.identifier=i.id " +
						"and i.projectPrefix=:project "+
						"and i.groupPrefix in (:groups) "+
						"and r.deleted=:deleted "+
						"and r.status.enumGenericState not like :invalidState "+
						"and r.secondaryIdentifier is not null " +
						"order by i.identifier.groupPrefix, i.identifier.suffix asc")
									.setParameter("project", project)
									.setParameterList("groups", groups)
									.setParameter("deleted", Boolean.FALSE)
									.setParameter("invalidState", GenericState.INVALID.toString());


				List<String> identifiers = (List<String>)query.list();
				return identifiers.toArray(new String[identifiers.size()]);

			}
		}; 

		if ( 0 == groups.length){
			throw new DAOException("At least one group is required to execute this query");
		}
		return (String[])getHibernateTemplate().execute(callback);
	}

	public boolean updatePrimaryIdentifier(final String identifier, final String primaryIdentifier) {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				Record record = (Record)session.createQuery("from Record r where r.identifier.identifier=?")
				.setString(0, identifier)
				.uniqueResult();

				if ( null != record ){
					record.setPrimaryIdentifier(primaryIdentifier);
					session.saveOrUpdate(record);
					return Boolean.TRUE;
				}
				else{
					return Boolean.FALSE;
				}

			}
		}; 

		return (Boolean)getHibernateTemplate().execute(callback);

	}

	public boolean updateSecondaryIdentifier(final String identifier, final String secondaryIdentifier) {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				Record record = (Record)session.createQuery("from Record r where r.identifier.identifier=?")
				.setString(0, identifier)
				.uniqueResult();

				if ( null != record ){
					record.setSecondaryIdentifier(secondaryIdentifier);
					session.saveOrUpdate(record);
					return Boolean.TRUE;
				}
				else{
					return Boolean.FALSE;
				}

			}
		}; 

		return (Boolean)getHibernateTemplate().execute(callback);

	}

	public void synchronizeDocumentStatusesWithPrimary(final String identifier) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				//find the record
				Record record = (Record)session.createQuery("from Record r where r.identifier.identifier=?")
				.setString(0, identifier)
				.uniqueResult();
				if ( null == record ){
					return new DAOException("No record exists for the identifier '"+identifier+"'");
				}

				//check the record has a primary
				if ( null == record.getPrimaryIdentifier() ){
					//no primary - nothing to do
					return null;
				}

				//load the primary record
				Record primRecord = (Record)session.createQuery("from Record r where r.identifier.identifier=?")
				.setString(0, record.getPrimaryIdentifier())
				.uniqueResult();
				if ( null == primRecord ){
					//no record exists in the repository for the primary record.
					//this is possible if the primary and secondary are being committed for the
					//first time during the same commit process - in this case there would be
					//no status synchronization to be done anyway so just exit here
					return null;
				}

				for ( DocumentInstance secDocInst: record.getDocInstances() ){
					DocumentOccurrence secDocOcc = secDocInst.getOccurrence();
					Document secDoc = secDocOcc.getDocument();
					if ( null != secDoc.getPrimaryDocIndex() && null != secDocOcc.getPrimaryOccIndex() ){

						//find the equivalent primary instance
						DataSet primDs = primRecord.getDataSet();
						Document primDoc = primDs.getDocument(secDoc.getPrimaryDocIndex().intValue());
						DocumentOccurrence primDocOcc = primDoc.getOccurrence(secDocOcc.getPrimaryOccIndex().intValue());
						DocumentInstance primDocInst = primRecord.getDocumentInstance(primDocOcc);

						//check the statuses
						if ( null == primDocInst ){
							//TODO THIS SHOULD NOT HAPPEN!! But it does apparently.
							sLog.error("No instance in primary record "+record.getPrimaryIdentifier()+" for document "+
									primDocOcc.getCombinedDisplayText()+" [secondary record="+identifier+"; secondary document="+secDocOcc.getCombinedDisplayText());
						}
						if ( !secDocInst.getStatus().getShortName().equals(primDocInst.getStatus().getShortName())){
							//status of primary and secondary instances are different
							//update the status of the secondary

							for ( Status s: secDoc.getStatuses() ){
								if ( s.getShortName().equals(primDocInst.getStatus().getShortName()) ){
									secDocInst.setStatus(s);
									break;
								}
							}

						}
					}
				}

				//save the secondary record
				session.saveOrUpdate(record);

				return null;
			}
		}; 

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}

	}

	/**
	 * Checks that the record does NOT have an invalid state (which indicates that
	 * the record was created in error and has been removed).
	 * 
	 * @param record
	 * @return boolean
	 */
	private boolean isRecordValid(org.psygrid.data.model.dto.RecordDTO record) {
		Record r = record.toHibernate();
		return isRecordValid(r);
	}


	/**
	 * Checks that the record does NOT have an invalid state (which indicates that
	 * the record was created in error and has been removed).
	 * 
	 * @param record
	 * @return boolean
	 */
	private boolean isRecordValid(Record record) {
		Status  status = record.getStatus();
		if (status != null) {
			if (GenericState.INVALID.equals(status.getGenericState())) {
				return false;
			}
		}
		return true;
	}

	public ConsentStatusResult getConsentAndStatusInfoForGroups(final String project, final String[] groups, final Date lastModifiedDate){

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				
				Query query1 = session.createQuery(
						"select r.identifier.identifier, r.primaryIdentifier, r.secondaryIdentifier, " +
						"c.consentGiven, cf.id, r.externalIdentifier " +
						"from Record r " +
						"left join r.consents c " +
						"left join c.consentForm cf " +
						"where r.identifier.projectPrefix=:project " +
						"and r.identifier.groupPrefix in (:groups) " +
						"and r.consentModified>:lastModDate " +
						"and r.deleted != :deleted and r.status.enumGenericState != :state " + 
				"order by r.identifier.identifier asc, cf.id asc")
				.setParameter("project", project)
				.setParameter("state", GenericState.INVALID.toString())
				.setParameter("deleted", Boolean.TRUE)
				.setParameterList("groups", groups)
				.setParameter("lastModDate", lastModifiedDate);

				List query1Result = query1.list();
				ConsentResult[] consentResult = new ConsentResult[query1Result.size()];
				for ( int i=0, c=query1Result.size(); i<c; i++ ){
					Object[] row = (Object[])query1Result.get(i);
					Boolean consentGivenObject = (Boolean)row[3];
					boolean consentGiven = false;
					if ( null != consentGivenObject ){
						consentGiven = consentGivenObject.booleanValue();
					}
					consentResult[i] = new ConsentResult((String)row[0],
							(String)row[1],
							(String)row[2],
							consentGiven,
							(Long)row[4],
							(String)row[5]);
				}
				
				Query query2 = session.createQuery(
						"select r.identifier.identifier, r.status.id, inst.id, occ.id, inst.status.id " +
						"from Record r " +
						"left join r.docInstances inst " +
						"left join inst.occurrence occ " +
						"where r.identifier.projectPrefix=:project " +
						"and r.identifier.groupPrefix in (:groups) " +
						"and r.statusModified>:lastModDate " +
						"and r.deleted != :deleted and r.status.enumGenericState != :state " +
						"order by r.identifier.identifier, occ.id asc")
				.setParameter("project", project)
				.setParameter("state", GenericState.INVALID.toString())
				.setParameter("deleted", Boolean.TRUE)
				.setParameterList("groups", groups)
				.setParameter("lastModDate", lastModifiedDate);

				List query2Result = query2.list();
				StatusResult[] statusResult = new StatusResult[query2Result.size()];
				for ( int i=0, c=query2Result.size(); i<c; i++ ){
					Object[] row = (Object[])query2Result.get(i);
					statusResult[i] = new StatusResult((String)row[0],
							(Long)row[1],
							(Long)row[2],
							(Long)row[3],
							(Long)row[4]);
				}

				return new ConsentStatusResult(consentResult, statusResult);

			}
		};

		return (ConsentStatusResult)getHibernateTemplate().execute(callback);
	}

	public SearchRecordChangeHistoryResult searchRecordChangeHistory(final String project,
			final Date start, final Date end, final String user, final String identifier, final int startIndex) throws DAOException {

		if ( null == project ){
			throw new DAOException("A project code must be provided");
		}
		
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				
				final int maxResultSize = 20;
				
				sLog.info("startIndex="+startIndex);
				
				StringBuilder hql = new StringBuilder();
				hql.append("select r.id, i.identifier, h.id, h.user, h.when, h.whenSystem, h.action " +
						   "from Record r join r.history h " +
						   "join r.dataSet ds " +
						   "join r.identifier i " +
						   "where ds.projectCode=:project ");
				if ( null != start ){
					hql.append("and h.when>:start ");
				}
				if ( null != end ){
					hql.append("and h.when<:end ");
				}
				if ( null != user ){
					hql.append("and h.user=:user ");
				}
				if ( null != identifier ){
					hql.append("and i.identifier=:identifier ");
				}
				hql.append("order by h.when desc, i.projectPrefix, i.groupPrefix, i.suffix");
				
				Query query = session.createQuery(hql.toString());
				query.setParameter("project", project);
				if ( null != start ){
					query.setParameter("start", start);
				}
				if ( null != end ){
					query.setParameter("end", end);
				}
				if ( null != user ){
					query.setParameter("user", user);
				}
				if ( null != identifier ){
					query.setParameter("identifier", identifier);
				}
				
				List result = query.list();
				SearchRecordChangeHistoryResult resultObject = new SearchRecordChangeHistoryResult();
				resultObject.setTotalCount(result.size());
				int endIndex = (result.size()>(startIndex+maxResultSize) ? startIndex+maxResultSize : result.size());
				resultObject.setFirstResult(startIndex+1);
				resultObject.setLastResult(endIndex);
				resultObject.setMaxResultCount(maxResultSize);
				RecordChangeHistoryResult[] rchr = new RecordChangeHistoryResult[endIndex-startIndex];
				int count = 0;
				for ( int i=startIndex, c=endIndex; i<c; i++){
					Object[] row = (Object[])result.get(i);
					rchr[count] = new RecordChangeHistoryResult(
									(Long)row[0],
									(String)row[1],
									(Long)row[2],
									(String)row[3],
									(Date)row[4],
									(Date)row[5],
									(String)row[6]);
					count++;
				}
				resultObject.setResults(rchr);
				
				return resultObject;
			}
		};

		return (SearchRecordChangeHistoryResult)getHibernateTemplate().execute(callback);

	}

	public DocInstChangeHistoryResult[] searchDocInstChangeHistory(final String identifier, final Long parentId) throws DAOException {
		
		if ( null == parentId ){
			throw new DAOException("A parent Id must be provided");
		}
		
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				Query query = session.createQuery(
						"select di.id, o.displayText, d.displayText, h.id, h.user, h.when, h.whenSystem, h.action " +
						"from DocumentInstance di join di.history h " +
						"join di.occurrence o " +
						"join o.document d " +
						"join di.record r " +
						"join r.identifier i " +
						"where h.parentId=:parentId " +
						"and i.identifier=:identifier")
									 .setParameter("parentId", parentId)
									 .setParameter("identifier", identifier);

				List result = query.list();
				DocInstChangeHistoryResult[] dichr = new DocInstChangeHistoryResult[result.size()];
				for ( int i=0, c=result.size(); i<c; i++){
					Object[] row = (Object[])result.get(i);
					dichr[i] = new DocInstChangeHistoryResult(
							(Long)row[0],
							(String)row[1]+" - "+(String)row[2],
							(Long)row[3],
							(String)row[4],
							(Date)row[5],
							(Date)row[6],
							(String)row[7]);
				}

				return dichr;
			}
		};

		return (DocInstChangeHistoryResult[])getHibernateTemplate().execute(callback);

	}
	
	public ProvenanceForChangeResult[] getProvenanceForChange(final String identifier, final Long changeId) throws DAOException {
		sLog.info("getProvenanceForChange: identifier="+identifier+", changeId="+changeId);
		if ( null == changeId ){
			throw new DAOException("A parent Id must be provided");
		}
		
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				Query query = session.createQuery(
						"select r.entry.displayText, p.comment, p.id " +
						"from Response r, Provenance p " +
						"where p in elements(r.provItems) " +
						"and p.parentChange.id=:changeId " +
						"and r.record.identifier.identifier=:identifier")
							.setParameter("changeId", changeId)
							.setParameter("identifier", identifier);

				List result = query.list();
				ProvenanceForChangeResult[] pfcr = new ProvenanceForChangeResult[result.size()];
				for ( int i=0, c=result.size(); i<c; i++){
					Object[] row = (Object[])result.get(i);
					pfcr[i] = new ProvenanceForChangeResult();
					pfcr[i].setEntry((String)row[0]);
					pfcr[i].setComment((String)row[1]);
					List result2 = session.createQuery("select p.theCurrentValue from Provenance p where p.id=?")
										  .setLong(0, (Long)row[2])
										  .list();
					if ( result2.size()>1 ){
						return new DAOException("Query 2 returned more than one provenance object!");
					}
					for ( int j=0,d=result2.size(); j<d; j++ ){
						Object o = result2.get(j);
						if ( o instanceof Value ){
							Value v = (Value)o;
							pfcr[i].setCurrentValue(v.getValueAsString());
						}
					}
					List result3 = session.createQuery("select p.thePrevValue from Provenance p where p.id=?")
					  					  .setLong(0, (Long)row[2])
					  					  .list();
					if ( result3.size()>1 ){
						return new DAOException("Query 3 returned more than one provenance object!");
					}
					for ( int j=0,d=result3.size(); j<d; j++ ){
						Object o = result3.get(j);
						if ( o instanceof Value ){
							Value v = (Value)o;
							pfcr[i].setPrevValue(v.getValueAsString());
						}
					}
				}
				return pfcr;
			}
		};
		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}
		return (ProvenanceForChangeResult[])result;
	}

	public void exportToXml(
			final ExportRequest request, final String group,
			final List<ExportSecurityActionMap> actionMap, 
			final OutputStream out, final org.psygrid.data.export.metadata.DataSetMetaData meta) 
	throws DAOException, RemoteException, NoDatasetException, TransformerException, XMLStreamException {
		
		final String projectCode = request.getProjectCode(); 
		
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				//Flush mode set to NEVER because although the hibernate object must be modified,
				//These mods are for export data only, and are NOT to be reflected back to the database.
				// TMC 2012-04-21 : The export code has been refactored to not mess with the dataset,
				//                  this is now just extra caution.
				session.setFlushMode(org.hibernate.FlushMode.NEVER); 
				
				Boolean useExternalID = (Boolean)session.createQuery("select d.useExternalIdAsPrimary from DataSet d where d.projectCode=:projectCode ")
				.setString("projectCode", projectCode)
				.uniqueResult();
				
				Criteria c = session.createCriteria(Record.class);
				c.createAlias("identifier", "i");
				c.createAlias("status", "s");
				c.add( Restrictions.eq("i.projectPrefix", projectCode));
				c.add( Restrictions.eq("i.groupPrefix", group));
				c.add( Restrictions.eq("deleted", Boolean.FALSE));
				c.add( Restrictions.ne("s.enumGenericState", GenericState.INVALID.toString()));
				c.add( Restrictions.ne("s.enumGenericState", GenericState.INACTIVE.toString()));

				if(useExternalID){
					c.addOrder( Order.asc("externalIdentifier"));					
				}
				else {
					c.addOrder( Order.asc("i.groupPrefix"));
					c.addOrder( Order.asc("i.suffix"));
				}
								
				try{
					exporter.export(out, session, c, meta, actionMap, request);
				} catch (XMLStreamException e) {
					return e;
				} catch (DAOException e) {
					return e;
				} catch (RemoteException e){
					return e;
				} catch (TransformerException e) {
					return e;
				}
				
				return null;
			}
		};
		
		Object result = getHibernateTemplate().execute(callback);

		if(result!=null){
			if(result instanceof NoDatasetException){
				throw (NoDatasetException)result;
			}
			else if (result instanceof RemoteException){
				throw (RemoteException)result;
			}
			else if (result instanceof DAOException){
				throw (DAOException)result;
			}
			else if (result instanceof TransformerException){
				throw (TransformerException) result;
			}
			else if (result instanceof XMLStreamException){
				throw (XMLStreamException) result;
			}
		}
	}

	public void exportToXml(
			final ExportRequest request, final List<String> identifiers,
			final List<ExportSecurityActionMap> actionMap, 
			final OutputStream out, final org.psygrid.data.export.metadata.DataSetMetaData meta) 
	throws DAOException, RemoteException, NoDatasetException, TransformerException, XMLStreamException {
				
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				//Flush mode set to NEVER because although the hibernate object must be modified,
				//These mods are for export data only, and are NOT to be reflected back to the database.
				// TMC 2012-04-21 : The export code has been refactored to not mess with the dataset,
				//                  this is now just extra caution.
				session.setFlushMode(org.hibernate.FlushMode.NEVER); 

				String projectCode = request.getProjectCode();
				
				Boolean useExternalID = (Boolean)session.createQuery("select d.useExternalIdAsPrimary from DataSet d where d.projectCode=:projectCode ")
				.setString("projectCode", projectCode)
				.uniqueResult();

				// THIS WILL NOT SCALE!!!
				Criteria c = session.createCriteria(Record.class);
						c.createAlias("identifier", "i");
						c.add(Restrictions.in("i.identifier", identifiers));
						
				if(useExternalID){
					c.addOrder( Order.asc("externalIdentifier"));					
				}
				else {
					c.addOrder( Order.asc("i.groupPrefix"));
					c.addOrder( Order.asc("i.suffix"));
				}					
									
				try{
					exporter.export(out, session, c, meta, actionMap, request);
				} catch (XMLStreamException e) {
					return e;
				} catch (DAOException e) {
					return e;
				} catch (RemoteException e){
					return e;
				} catch (TransformerException e) {
					return e;
				}
				
				return null;
			}
		};
		
		Object result = getHibernateTemplate().execute(callback);

		if ( result!=null){
			if(result instanceof NoDatasetException){
				throw (NoDatasetException)result;
			}
			else if (result instanceof RemoteException){
				throw (RemoteException)result;
			}
			else if (result instanceof DAOException){
				throw (DAOException)result;
			}
			else if (result instanceof TransformerException){
				throw (TransformerException) result;
			}
			else if (result instanceof XMLStreamException){
				throw (XMLStreamException) result;
			}
		}
	}

	public long getStatusIdForDocument(final String identifier, final long docOccId)
			throws DAOException {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				Query query = session.createQuery(
						"select s.id from DocumentInstance di " +
						"join di.status s " +
						"join di.record r " +
						"join di.occurrence occ " +
						"join r.identifier i " +
						"where i.identifier=:identifier "+
						"and occ.id=:occId")
						.setString("identifier", identifier)
						.setLong("occId", docOccId);
				Object result = query.uniqueResult();
				return result;
			}
		};
		return (Long)getHibernateTemplate().execute(callback);
	}

	/**
	 * Returns a list of records that have changed entries and require resaving to
	 * the database
	 * 
	 * @param dsPatchedId  the id of the dataset
	 * @param patchedDataSet the dataset that has been patched
	 * @param prePatchedDataSet the dataset before it has been patched
	 * @param stdCode the standard code to be used as a filler for entries
	 */
	public List<org.psygrid.data.model.dto.RecordDTO> getRecordsToPatch(org.psygrid.data.model.dto.DataSetDTO patchedDataSet, org.psygrid.data.model.dto.DataSetDTO prePatchedDataSet, org.psygrid.data.model.dto.StandardCodeDTO stdCode, String userName) throws DAOException {

		Long dsPatchedId = patchedDataSet.getId();
		org.psygrid.data.model.dto.RecordDTO[] records = getRecords(dsPatchedId, RetrieveDepth.RS_COMPLETE);  
		
		List<org.psygrid.data.model.dto.RecordDTO> recordsToPatch = new ArrayList<org.psygrid.data.model.dto.RecordDTO>();
		
		DataSet preDataSet = (DataSet)prePatchedDataSet.toHibernate();
		DataSet postDataSet = (DataSet)patchedDataSet.toHibernate();
		
		//set up a hashmap mapping documents to their document names
		HashMap<String, Document> docNameMap = new HashMap<String, Document>();
		for (int i=0; i< preDataSet.numDocuments(); i++) {
			docNameMap.put(preDataSet.getDocument(i).getName(), preDataSet.getDocument(i));
		}

        //iterate through all existing records
        for (org.psygrid.data.model.dto.RecordDTO r: records ){
        	Record record = r.toHibernate();
            //attach the current record to the dataset
            record.attach(postDataSet);
        	
        	//create a list of documents now in the dataset
            List<Document> docs = new ArrayList<Document>();
            for (int i =0; i<postDataSet.numDocuments(); i++) {
            	docs.add(postDataSet.getDocument(i));
            }
            
            // see if we need to make modifications to this record
            // this is only if the record has relevant doc instances 
            boolean download = false;
            for ( Document doc: docs ){
                for ( int i=0; i<doc.numOccurrences(); i++ ){
                    if ( null != record.getDocumentInstance(doc.getOccurrence(i)) ){
                        download = true;
                    }
                }
            }
            
            //if we need to make modifications
            if ( download ){
            	//iterate through all docs (could make this more efficient by
            	//creating a subset of relevant docs from the download check above)
            	for (Document curDoc: docs) {
            		for ( int i=0; i<curDoc.numOccurrences(); i++ ){
            			//get the document instance 
            			DocumentOccurrence docOcc = curDoc.getOccurrence(i);
	                    DocumentInstance docInst = record.getDocumentInstance(docOcc);
	                    
	                    if ( null != docInst ){
	                    	//if doc has been patched (saved version no != new version no)	
	                    	Document newDoc = docInst.getOccurrence().getDocument();
	                    	Document oldDoc = docNameMap.get(newDoc.getName());
	                    	
	                    	//old doc can be null if it was just added in the patching
	                    	if (oldDoc != null) {
	                    		//if doc has diff version numbers, then it has been patched
	                    		if (newDoc.getAutoVersionNo() > oldDoc.getAutoVersionNo()) {

	                    			//first store old entries in hashmap so they can be easily fetched by name
	                    			HashMap<String, Entry> oldEntries = new HashMap<String, Entry>();
	                    			for (int z=0; z<oldDoc.numEntries(); z++) {
	                    				Entry oldEntry = oldDoc.getEntry(z);
	                    				oldEntries.put(oldEntry.getName(), oldEntry);
	                    			}
	                    			
	                    			//go through section occs (need this to generate response)
	                    			for ( int k=0, e= newDoc.numSections(); k<e; k++ ){
	                    				Section section = newDoc.getSection(k);

	                    				//iterate the section occurrences
	                    				for ( int j=0, d=section.numOccurrences(); j<d; j++ ) {
	                    					SectionOccurrence secOcc = section.getOccurrence(j);
	                    					
	                    					//iterate the entries and set response
	                    					for (int y=0; y<newDoc.numEntries(); y++) {
	                    						
	                    						Entry  curEntry = newDoc.getEntry(y);

	                    						//check we are working with the correct section
	                    						if (!curEntry.getSection().equals(section)){
	                    							continue;
	                    						}
	                    						
	                    						//ensure the entry was not present in the old version
	                    						if (!oldEntries.containsKey(curEntry.getName())) {
	                    							//set the responses according to the element patching flag
	                    							if (curEntry.getElementPatchingAction().equals(Entry.ACCEPT_ALL_EXISTING_ELEMENTS)) {
	                    								fillEntryWithStandardCode(record.getDocumentInstance(docOcc), secOcc, curEntry, (StandardCode)stdCode.toHibernate());
	                    							}
                    								else if (curEntry.getElementPatchingAction().equals(Entry.REJECT_ALL_EXISTING_ELEMENTS)) {
                    									markEntryAsInvalid(record.getDocumentInstance(docOcc), secOcc, curEntry, userName);
                    								}
	                    						} else {
	                    							//if entry was present, then only patch for rejections
	                    							Entry  oldEntry = oldEntries.get(curEntry.getName());
	                    							if (oldEntry.getAutoVersionNo() != null) {
		                    							if (oldEntry.getAutoVersionNo() < curEntry.getAutoVersionNo()) {
		                    								if (curEntry.getElementPatchingAction().equals(Entry.REJECT_ALL_EXISTING_ELEMENTS)) {
		                    									markEntryAsInvalid(record.getDocumentInstance(docOcc), secOcc, curEntry, userName);
		                    								}
		                    							}
	                    							}
	                    						}
	                    					}
	                    				}	
	                    				//only add once! (otherwise, potential out of date exception
	                    				//when it tries to save the second one)!
	                    				boolean exists = false;
	                    				for (int z=0; z<recordsToPatch.size(); z++) {
	                    					org.psygrid.data.model.dto.RecordDTO rec = recordsToPatch.get(z);
	                    					if (rec.getId().equals(record.getId())) {
	                    						recordsToPatch.set(z, record.toDTO());
	                    						exists = true;
	                    						break;
	                    					}
	                    				}
	                    				
	                    				if (!exists) {
		                    				recordsToPatch.add(record.toDTO());
	                    				}
	                    			}
	                    		}
	                    	}
	                    }
            		}
            	}
            }
        }
        return recordsToPatch;
	}
	

	/**
	 * Create a response filled with the given standard code
	 * @param entry the entry to which this response belongs
	 * @param secOcc the section occurrence to which the response belongs
	 * @param stdCode the newly created rejected response
	 * @return the newly created response filled with the given standard code
	 */
	private BasicResponse createStdCodeFilledResponse(BasicEntry entry, SectionOccurrence secOcc, StandardCode stdCode) {
		BasicResponse entryResp = (BasicResponse)entry.generateInstance(secOcc);
		IValue basicValue = ((BasicEntry)entry).generateValue();
		entryResp.setValue(basicValue);
		basicValue.setStandardCode(stdCode);
		return entryResp;
	}
	                    			
	                    			
	/**
	 * Fill the response with a standard code;
	 * If a composite; create a composite row with std codes
	 * @param docInst the document instance to which this response belongs
	 * @param secOcc the section occurrence to which this response belongs
	 * @param entry the entry to which this response relates to
	 * @param stdCode the std code 
	 */
	private void fillEntryWithStandardCode(DocumentInstance docInst, SectionOccurrence secOcc, Entry  entry, StandardCode stdCode) {
		if (entry instanceof BasicEntry) {
			docInst.addResponse(createStdCodeFilledResponse((BasicEntry)entry, secOcc, stdCode));
		} else if (entry instanceof CompositeEntry) {
			CompositeResponse compResponse = ((CompositeEntry)entry).generateInstance(secOcc);
			CompositeRow compRow = compResponse.createCompositeRow();
			for (int i=0; i<((CompositeEntry)entry).numEntries(); i++) {
				BasicEntry bEntry = ((CompositeEntry)entry).getEntry(i);
				compRow.addResponse(createStdCodeFilledResponse(bEntry, secOcc, stdCode));
			}
			docInst.addResponse(compResponse);
		}
	}	                    			
	           
	/**
	 * Mark the entry response as invalid 
	 * @param docInst the document instance to which this response belongs
	 * @param secOcc the section occurrence to which this response belongs
	 * @param entry the entry that this response relates to
	 */
	private void markEntryAsInvalid(DocumentInstance docInst, SectionOccurrence secOcc, Entry  entry, String userName) {
		//docInst status should not be null here!
		if (docInst.getStatus() == null) {
			return;
		}
		
		boolean alreadyRejected = false;
		
		//only do this to docs that are in pending or approved statuses
		if (docInst.getStatus().getShortName().equals(Status.DOC_STATUS_APPROVED) ||
				docInst.getStatus().getShortName().equals(Status.DOC_STATUS_REJECTED) ||
				docInst.getStatus().getShortName().equals(Status.DOC_STATUS_PENDING) || 
				docInst.getStatus().getShortName().equals(Status.DOC_STATUS_COMPLETE)) {
			
			if(docInst.getStatus().getShortName().equals(Status.DOC_STATUS_REJECTED)){
				alreadyRejected = true;
			}
			
			
			if (entry instanceof BasicEntry && !(entry instanceof DerivedEntry)) {
				if(docInst.getResponse(entry, secOcc) == null){
					docInst.addResponse(createRejectionResponse(docInst, (BasicEntry)entry, secOcc, null));
				}else{
					createRejectionResponse(docInst, (BasicEntry) entry, secOcc, null);
				}
			} else if (entry instanceof CompositeEntry) {
				
					//Looks like the approach here is to create a row and reject it.
					//Obviously this won't work if we have fixed labels, and anyway, it is wrong in any case.
					//Need to make sure we reject all entries that are now invalid, and the existing behaviour doesn't do that.
					CompositeResponse compResponse = ((CompositeEntry)entry).generateInstance(secOcc);
					CompositeRow compRow = compResponse.createCompositeRow();
					
					for (int i=0; i<((CompositeEntry)entry).numEntries(); i++) {
						BasicEntry bEntry = ((CompositeEntry)entry).getEntry(i);
						if(docInst.getResponse(entry, secOcc) == null){
							compRow.addResponse(createRejectionResponse(docInst, bEntry, secOcc, null));	
						}else{
							createRejectionResponse(docInst, bEntry, secOcc, null);
						}
						
					}
				docInst.addResponse(compResponse);
			} else if (entry instanceof DerivedEntry) {
				//reject first variable of derived entry (makes no sense to reject derived entry itself)
				Set varNames = ((DerivedEntry)entry).getVariableNames();
				if (!varNames.isEmpty()) {
					String invalidationMessage = "Variable marked as invalid to force recalculation of " + entry.getDisplayText() + ". Its value should not change.";
					BasicEntry basEntry = ((DerivedEntry)entry).getVariable(varNames.toArray()[0].toString());
					if(docInst.getResponse(basEntry, secOcc) == null){
						docInst.addResponse(createRejectionResponse(docInst, (BasicEntry)basEntry, secOcc, invalidationMessage));
					}else{
						createRejectionResponse(docInst, basEntry, secOcc, invalidationMessage);
					}
				}
			}
			
			if(!alreadyRejected){
				try {
					Document document = docInst.getOccurrence().getDocument();
					for (int i = 0, c = document.numStatus(); i < c; ++i) {
						Status  status = document.getStatus(i);
						//What about non review and approve projects? shouldn't document instances sitting at
						//Controlled be moved back to complete or something? Hmmm..... It really seems that if a record's
						//calculated entry gets changed that the values should be changed server-side instead of forcing
						//all the documents to have to be opened. It's kind of crazy to do that - you can imagine how much
						//effort this would take if there were say 1000 documents already filled in.
						if (status.getShortName().equals(Status.DOC_STATUS_REJECTED)) {
							((DocumentInstance)docInst).setStatus(status);
							break;
						}
					}
				} catch (Exception ex) {
					sLog.error("Exception marking entry as invalid", ex);
				}
			}
		}
	}
	
	/**
	 * Create a rejected response for the given entry in the given section occurrence.
	 * 
	 * @param entry the entry to which the response belongs
	 * @param secOcc the section occurrence to which the response belongs
	 * @return the newly created rejected response
	 */
	private BasicResponse createRejectionResponse(DocumentInstance docInst, BasicEntry entry, SectionOccurrence secOcc, String invalidationMessage) {
		BasicResponse entryRespReject = null;
		if (docInst.getResponse(entry, secOcc) == null) {
			entryRespReject = (BasicResponse)entry.generateInstance(secOcc);
			IValue basicValueReject = ((BasicEntry)entry).generateValue();
			entryRespReject.setValue(basicValueReject);
		} else {
			entryRespReject = (BasicResponse)docInst.getResponse(entry, secOcc);
		}
		
		if(invalidationMessage == null || invalidationMessage.equals("")){
			invalidationMessage = "New entry patched";
		}
		entryRespReject.setAnnotation(invalidationMessage);
		entryRespReject.setStatus(ResponseStatus.FLAGGED_INVALID);
		return entryRespReject;
	}
	
	public boolean canRecordBeRandomized(final String identifier){
		
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
		
				Criteria c = session.createCriteria(Record.class);
				c.createAlias("identifier", "i");
				c.createAlias("status", "s");
				c.createAlias("dataSet", "ds");
				c.add( Restrictions.eq("i.identifier", identifier));
				c.add( Restrictions.eq("deleted", Boolean.FALSE));
				c.add( Restrictions.ne("s.enumGenericState", GenericState.INVALID.toString()));
				c.add( Restrictions.ne("s.enumGenericState", GenericState.INACTIVE.toString()));
				c.add( Restrictions.eq("ds.randomizationRequired", Boolean.TRUE));
				Criteria diCrit = c.createCriteria("docInstances");
				diCrit.add( Restrictions.ne("isRandomised", Boolean.TRUE));
				diCrit.createCriteria("status")
					.add( Restrictions.ne("shortName", Status.DOC_STATUS_INCOMPLETE));
				diCrit.createCriteria("occurrence")
						.add(Restrictions.eq("randomizationTrigger", Boolean.TRUE));
				c.setProjection(Projections.rowCount());
				
				Long count = (Long)c.uniqueResult();
				if ( count > 0 ){
					return Boolean.TRUE;
				}
				else{
					return Boolean.FALSE;
				}
			}
		};
	
		return (Boolean)getHibernateTemplate().execute(callback);
	
	}
	
	
	public String[] importDocuments(final long datasetID,long occurrenceID,String csvData) throws DAOException {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				String[] result = null;
                DataSet ds = (DataSet)session.get(DataSet.class, datasetID);
                if ( null == ds ){
                    return new DAOException("No DataSet exists in the repository for id = "+datasetID);
                }
                
                
                return result;
			}
		};
	
		return (String[])getHibernateTemplate().execute(callback);
	}

    
    public org.psygrid.data.model.dto.RecordDTO[] getRecords(Long dataSetId) {
        return getRecords(dataSetId, RetrieveDepth.RS_SUMMARY);
    }

    
	public Long saveStandardCode(org.psygrid.data.model.dto.StandardCodeDTO standardCode) throws ObjectOutOfDateException {
		org.psygrid.data.model.hibernate.StandardCode code = standardCode.toHibernate();
		try {
			getHibernateTemplate().saveOrUpdate(code);
		} catch (HibernateOptimisticLockingFailureException ex) {
			throw new ObjectOutOfDateException(
					"Cannot save Unit - the object is out-of-date");
		}
		return code.getId();
	}

	public org.psygrid.data.model.dto.StandardCodeDTO[] getStandardCodes() {
		List results = getHibernateTemplate().find("from StandardCode");
		org.psygrid.data.model.dto.StandardCodeDTO[] codes = new org.psygrid.data.model.dto.StandardCodeDTO[results.size()];
		for (int i = 0; i < results.size(); i++) {
			org.psygrid.data.model.hibernate.StandardCode code = (org.psygrid.data.model.hibernate.StandardCode) results.get(i);
			codes[i] = code.toDTO();
		}
		return codes;
	}

	public org.psygrid.data.model.dto.StandardCodeDTO getStandardCode(Long standardCodeId) throws DAOException {
		org.psygrid.data.model.hibernate.StandardCode code = (org.psygrid.data.model.hibernate.StandardCode) getHibernateTemplate().get(StandardCode.class, standardCodeId);
		if (null == code) {
			throw new DAOException(
					"No standard code exists on the repository for id="
							+ standardCodeId);
		}
		return code.toDTO();
	}    
    
    public Map<Long, TransformerClient> getTransformerClients(Long dsId) throws DAOException{
    	
        Map<Long, TransformerClient> clients = new HashMap<Long, TransformerClient>();
        Iterator results = getHibernateTemplate().find("select ds.transformers from DataSet ds where ds.id=?",dsId).iterator();
        while ( results.hasNext() ){
            Transformer t = (Transformer)results.next();
            Call call = null;
            try{
                call = new Call(t.getWsUrl());
            }
            catch(MalformedURLException ex){
                throw new DAOException("Transformer with id '"+t.getId()+"' has an invalid web-service URL", ex);
            }
            call.setOperationName(new QName(t.getWsNamespace(), t.getWsOperation()));
            TransformerClient tws = new TransformerClient();
            tws.setWebService(call);
            tws.setResultClass(t.getResultClass());
            tws.setViewableOutput(t.isViewableOutput());
            clients.put(t.getId(), tws);
        }        
        return clients;
    }

    /**
     * If consent is about to be withdrawn, this may delete participant data 
     * from the ESL. This method checks this and if so appends a message to 
     * the result 
     * @param record
     * @param result
     * @param saml
     * @return
     * @throws EslException 
     */
    private String[] checkEslImpactOfWithdrawingConsent(Record record, String[] result, String saml) throws EslException {
    	if ( record.getDataSet().isEslUsed() && !record.checkConsentForEsl() 
    			&& eslClient.willSubjectBeDeletedWhenConsentIsWithdrawn(record.getIdentifier().getIdentifier(), saml)) {
    		// Create a new result object and copy all the existing data across
    		// This is horrible, but the original data is in an array rather than
    		// a collection so there is no easy alternative
    		String[] modifiedResult = new String[result.length+1];
    		for(int index = 0; index < result.length; index++) {
    			modifiedResult[index] = result[index];
    		}
    		modifiedResult[result.length] = "Participant data in the participant register";
    		return modifiedResult;
    	}
    	return result;
    }
    
    /**
     * If the Record Status is INVALID, the PR should be locked
     * If the Record Status is LEFT, COMPLETED, REFERRED or ACTIVE, the PR should be unlocked
     * The other Record Status (INACTIVE) is handled by the consent mechanism
     * @param record			The record to be modified
     * @param saml				For authentication
     * @param newGenericState	The state into which the record is transitioning
     * @throws EslException		Thrown if there is a problem locking or unlocking
     */
    private void modifyEslAccordingToRecordStatus(Record record, String saml, GenericState newGenericState) throws EslException {
    	if(newGenericState.equals(GenericState.INVALID)) {
    		sLog.info("changeRecordStatus: Locking subject in the ESL");
    		eslClient.lockSubject(record.getIdentifier().getIdentifier(), saml);    		
    	} else if(newGenericState.equals(GenericState.LEFT) || newGenericState.equals(GenericState.COMPLETED) || newGenericState.equals(GenericState.REFERRED) || newGenericState.equals(GenericState.ACTIVE)) {
    		sLog.info("changeRecordStatus: Unlocking subject in the ESL");
    		eslClient.unlockSubject(record.getIdentifier().getIdentifier(), saml);
    	}
    }
    
	@SuppressWarnings("unchecked")
	public List<GroupSummary> getGroupSummary(List<String> projectCodes) {
		if(projectCodes.size()==0) return new ArrayList<GroupSummary>();
    	Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
    	Query q = session.createQuery("select g.id as groupID, g.name as groupCode," +
    			"g.longName as groupName, ds.projectCode as datasetCode,"+
    			"ds.name as datasetName " +
    			"from Group g join g.dataSet ds where ds.projectCode in (:projectCodes) " +
    			"order by ds.projectCode, g.longName");
    	q.setResultTransformer(Transformers.aliasToBean(GroupSummary.class));
    	q.setParameterList("projectCodes", projectCodes);
    	return q.list();
	}

	public boolean isProjectRandomized(String projectCode) {
    	List<?> results = getHibernateTemplate().find("select ds.randomizationRequired from DataSet ds where ds.projectCode=?", projectCode);
    	return (Boolean)DataAccessUtils.requiredSingleResult(results);
	}    

	public Group getGroup(final Long groupID){
    	// Eager load all the sites and consultants
    	List<?> results = getHibernateTemplate().find("select distinct g from Group g left join fetch g.dataSet left join fetch g.sites s "+
    			"left join fetch s.consultants where g.id=?", groupID);
    	return (Group)DataAccessUtils.singleResult(results);
    }

    public void updateGroup(final Group group){
    	
    	getHibernateTemplate().update(group);
    	// 'touch' the dataset - so it will be downloaded by Collect.
    	DataSet ds = group.getDataSet();
    	ds.setDateModified(new Date());
        ds.incrementAutoVersionNo();
    	getHibernateTemplate().update(ds);
    }

	public void addGroup(final String projectCode, final Group group) {
    	List<?> results = getHibernateTemplate().find("from DataSet ds where ds.projectCode=?", projectCode);
    	DataSet ds = (DataSet)DataAccessUtils.singleResult(results);		
        group.setDataSet(ds);
        ds.getGroups().add(group);
        // 'touch' the dataset so it will be downloaded by Collect
        ds.setDateModified(new Date());
        ds.incrementAutoVersionNo();
    }

	public void deleteGroup(Long groupID) {
		Group g = (Group) getHibernateTemplate().load(Group.class, groupID);
		g.getDataSet().getGroups().remove(g);
		getHibernateTemplate().delete(g);
	}

}
