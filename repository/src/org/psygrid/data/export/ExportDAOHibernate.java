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


package org.psygrid.data.export;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.psygrid.data.export.hibernate.ExportDocument;
import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.data.export.hibernate.ExportSecurityActionMap;
import org.psygrid.data.export.hibernate.ExternalQuery;
import org.psygrid.data.repository.dao.DAOException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author Rob Harper
 *
 */
public class ExportDAOHibernate extends HibernateDaoSupport implements ExportDAO {

	private static final Log sLog = LogFactory.getLog(ExportDAOHibernate.class);

	/**
	 * @see org.psygrid.data.export.ExportDAO#getCompletedExport(java.lang.String, java.lang.String)
	 */
	public byte[] getCompletedExport(final String user, final Long id) throws NoSuchExportException {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				ExportRequest result = (ExportRequest)session.createQuery("from ExportRequest er where er.requestor=? and er.id=? and er.status=?")
				.setString(0, user)
				.setLong(1, id)
				.setString(2, ExportRequest.STATUS_COMPLETE)
				.uniqueResult();
				if ( null == result ){
					return new NoSuchExportException("No export data exists for the specified id ("+id+")");
				}
				return result.toDTO();
			}
		};
		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof NoSuchExportException ){
			throw (NoSuchExportException)result;
		}
		org.psygrid.data.export.dto.ExportRequest req = (org.psygrid.data.export.dto.ExportRequest)result;
		byte[] data = null;
		if ( null != req ){
			try{
				//load zip data from file into byte array
				File file = new File(req.getPath());

				data = generateByteArray(file);
			}
			catch(Exception ex){
				sLog.error("getCompletedExport: exception when loading zip file from '"+req.getPath()+"'", ex);
			}
		}

		return data;
	}

	/**
	 * @see org.psygrid.data.export.ExportDAO#getCompletedExportHash(java.lang.String, java.lang.String, java.lang.String)
	 */
	public byte[] getCompletedExportHash(final String user, final String format, final Long id) throws NoSuchExportException {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				ExportRequest result = (ExportRequest)session.createQuery("from ExportRequest er where er.requestor=? and er.id=? and er.status=?")
				.setString(0, user)
				.setLong(1, id)
				.setString(2, ExportRequest.STATUS_COMPLETE)
				.uniqueResult();
				if ( null == result ){
					return new NoSuchExportException("No export data exists for the specified id ("+id+")");
				}
				return result.toDTO();
			}
		};
		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof NoSuchExportException ){
			throw (NoSuchExportException)result;
		}
		org.psygrid.data.export.dto.ExportRequest req = (org.psygrid.data.export.dto.ExportRequest)result;
		byte[] data = null;
		if ( null != req ){
			try{
				//load zip data from file into byte array
				File file = null;

				if ("sha1".equalsIgnoreCase(format)) {
					file = new File(req.getSha1Path());	
				}
				if ("md5".equalsIgnoreCase(format)) {
					file = new File(req.getMd5Path());	
				}

				data = generateByteArray(file);
			}
			catch(Exception ex){
				sLog.error("getCompletedExportHash: exception when loading hash file for '"+req.getId()+"' of the type: "+format, ex);
			}
		}

		return data;
	}

	/**
	 * @see org.psygrid.data.export.ExportDAO#getRequestsForUser(java.lang.String)
	 */
	public org.psygrid.data.export.dto.ExportRequest[] getRequestsForUser(final List<String> projects, final String user) {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				Criteria c = session.createCriteria(ExportRequest.class);
				c.add( Restrictions.eq("requestor", user) );
				c.add( Restrictions.in("projectCode", projects));
				c.addOrder( Order.asc("id"));
				List result = c.list();
				/*
	            List result = session.createQuery("from ExportRequest er where er.requestor=?")
	            					 .setString(0, user)
	            					 .list();
				 */
				org.psygrid.data.export.dto.ExportRequest[] erArray = new org.psygrid.data.export.dto.ExportRequest[result.size()];
				for ( int i=0; i<result.size(); i++ ){
					//place a copy of the persisted request into the array, just
					//to make sure that all fields have been initialized from
					//the database
					erArray[i] = ((ExportRequest)result.get(i)).toDTO();
				}

				return erArray;
			}
		};

		return (org.psygrid.data.export.dto.ExportRequest[])getHibernateTemplate().execute(callback);
	}

	/**
	 * @see org.psygrid.data.export.ExportDAO#requestExport(java.lang.String, java.lang.String)
	 */
	public void requestExport(ExportRequest request, boolean applyExportSecurity) {
		getHibernateTemplate().saveOrUpdate(request);
	}

	public org.psygrid.data.export.dto.ExportRequest getNextPendingRequest(final boolean immediate) {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				Long count = (Long)session.createQuery(
				"select count(*) from ExportRequest er where er.status=?")
				.setString(0, ExportRequest.STATUS_PROCESSING)
				.uniqueResult();
				if ( count.intValue() > 0 ){
					//there is already an export in progress
					return null;
				}
				List result = session.createQuery(
				"from ExportRequest er where er.status=? and er.immediate=? order by er.requestDate asc")
				.setString(0, ExportRequest.STATUS_PENDING)
				.setBoolean(1, immediate)
				.list();
				ExportRequest req = null;
				for ( int i=0; i<result.size(); i++ ){
					//get the first export request in the list. A copy is taken, just
					//to make sure that all fields have been initialized from
					//the database
					req = (ExportRequest)result.get(i);
					break;
				}
				if ( null == req ){
					return null;
				}
				return req.toDTO();
			}
		};

		return (org.psygrid.data.export.dto.ExportRequest)getHibernateTemplate().execute(callback);
	}

	public org.psygrid.data.export.dto.ExportRequest updateExportRequest(org.psygrid.data.export.dto.ExportRequest req) {
		final ExportRequest hReq = req.toHibernate();
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				session.saveOrUpdate(hReq);
				session.refresh(hReq);
				return hReq.toDTO();
			}
		};
		return (org.psygrid.data.export.dto.ExportRequest)getHibernateTemplate().execute(callback);
	}

	public void updateRequestStatus(final Long requestId, final String newStatus) throws DAOException {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				ExportRequest req = (ExportRequest)session.createQuery("from ExportRequest er where er.id=?")
				.setLong(0, requestId)
				.uniqueResult();
				if ( null == req ){
					return new DAOException("No export request exists for id "+requestId);
				}
				req.setStatus(newStatus);
				session.saveOrUpdate(req);
				return null;
			}
		};
		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}
	}

	public void updateRequestSetComplete(final Long requestId, final String path, final String md5Path, final String shaPath) throws DAOException {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				ExportRequest req = (ExportRequest)session.createQuery("from ExportRequest er where er.id=?")
				.setLong(0, requestId)
				.uniqueResult();
				if ( null == req ){
					return new DAOException("No export request exists for id "+requestId);
				}
				req.setStatus(ExportRequest.STATUS_COMPLETE);
				req.setCompletedDate(new Date());
				req.setPath(path);
				req.setMd5Path(md5Path);
				req.setSha1Path(shaPath);
				session.saveOrUpdate(req);
				return null;
			}
		};
		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}
	}

	public String getProjectForExportRequest(final Long requestId) throws DAOException {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				String project = (String)session.createQuery("select er.projectCode from ExportRequest er where er.id=?")
				.setLong(0, requestId)
				.uniqueResult();
				if ( null == project ){
					return new DAOException("No export request exists for id "+requestId);
				}
				return project;
			}
		};
		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}
		return (String)result;
	}

	public void deleteExportRequest(final String user, final Long requestId) throws UnableToCancelExportException {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				ExportRequest er = (ExportRequest)session.createQuery("from ExportRequest er where er.id=? and er.requestor=?")
				.setLong(0, requestId)
				.setString(1, user)
				.uniqueResult();
				if ( null == er ){
					return new UnableToCancelExportException("You do not have a pending export request with id="+requestId);
				}
				if ( !ExportRequest.STATUS_PENDING.equals(er.getStatus()) ){
					return new UnableToCancelExportException("Cannot cancel the export request; it is no longer in the Pending state");
				}

				session.delete(er);
				return null;
			}
		};
		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof UnableToCancelExportException ){
			throw (UnableToCancelExportException)result;
		}

	}

	public void generateHashes(String filePath, String md5Path, String shaPath) throws IOException, NoSuchAlgorithmException {
		//load zip data from file into byte array
		File file = new File(filePath);
		byte[] fileData = generateByteArray(file);

		//Generate the MD5 and SHA hashes for the zip data
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(fileData);
		byte[] md5Digest = md5.digest();
		MessageDigest sha1 = MessageDigest.getInstance("SHA");
		sha1.update(fileData);
		byte[] sha1Digest = sha1.digest();

		StringBuffer md5sb = new StringBuffer();
		for (int i = 0; i < md5Digest.length; i++) {
			//As Integer.toHexString() takes an int it is necessary to do
			//the weird casting to prevent, say, -1 from turning into 0xFFFFFFFF.
			//Also need to treat 0 as a special case otherwise it's ignored.
			if (((int)md5Digest[i] & 0xFF) < 0x10) {
				md5sb.append("0");
			}
			md5sb.append(Integer.toHexString(((int) md5Digest[i]) & 0xFF));
		}
		StringBuffer sha1sb = new StringBuffer();
		for (int i = 0; i < sha1Digest.length; i++) {
			if (((int)sha1Digest[i] & 0xFF) < 0x10) {
				sha1sb.append("0");
			}
			sha1sb.append(Integer.toHexString(((int) sha1Digest[i]) & 0xFF));
		}

		//Write out the MD5 hash file
		File outputMD5 = new File(md5Path);
		OutputStream osMD5 = new FileOutputStream(outputMD5);
		BufferedOutputStream bosMD5 = new BufferedOutputStream(osMD5, md5sb.length());
		bosMD5.write(md5sb.toString().getBytes());
		bosMD5.close();

		//Write out the SHA1 hash file
		File outputSHA = new File(shaPath);
		OutputStream osSHA = new FileOutputStream(outputSHA);
		BufferedOutputStream bosSHA = new BufferedOutputStream(osSHA, sha1sb.length());
		bosSHA.write(sha1sb.toString().getBytes());
		bosSHA.close();
	}


	private byte[] generateByteArray(File file) throws FileNotFoundException, IOException {
		InputStream is = new FileInputStream(file);

		//Get the size of the file
		long length = file.length();

		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] data = new byte[(int)length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < data.length
				&& (numRead=is.read(data, offset, data.length-offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < data.length) {
			throw new IOException("Could not completely read file "+file.getName());
		}

		// Close the input stream and return bytes
		is.close();

		return data;
	}

	public ExternalQuery[] getExternalQueries(final String projectCode) {
			HibernateCallback callback = new HibernateCallback(){
				public Object doInHibernate(Session session){
					Criteria c = session.createCriteria(ExternalQuery.class);
					c.add( Restrictions.eq("projectCode", projectCode));
					List result = c.list();
					ExternalQuery[] results = (ExternalQuery[])result.toArray(new ExternalQuery[result.size()]);
					return results;
				}
			};

			return (ExternalQuery[])getHibernateTemplate().execute(callback);
	}
}
