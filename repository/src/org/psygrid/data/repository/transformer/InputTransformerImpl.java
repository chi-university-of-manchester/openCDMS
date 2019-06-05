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

package org.psygrid.data.repository.transformer;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.model.dto.TransformerDTO;
import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.CompositeResponse;
import org.psygrid.data.model.hibernate.CompositeRow;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.EditAction;
import org.psygrid.data.model.hibernate.Provenance;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.model.hibernate.Response;
import org.psygrid.data.model.hibernate.Value;
import org.psygrid.data.model.hibernate.visitors.GetValueVisitor;
import org.psygrid.data.model.hibernate.visitors.SetValueVisitor;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.dao.RepositoryDAO;

public class InputTransformerImpl implements InputTransformer {

	private static Log sLog = LogFactory.getLog(InputTransformerImpl.class);

	private RepositoryDAO dao;

	public RepositoryDAO getDao() {
		return dao;
	}

	public void setDao(RepositoryDAO dao) {
		this.dao = dao;
	}

	public void transform(Record r, Map<Long, TransformerClient> transformerClients)
	throws TransformerException, RemoteException {
		Record record = (Record)r;
		String recordId = record.getIdentifier().getIdentifier();

		for (DocumentInstance inst: record.getDocInstances()){
			Long docOccId = inst.getOccurrenceId();
			for ( Response resp: inst.getResponses() ){
				Long secOccId = null;
				if ( null != resp.getSectionOccurrenceId() ){
					secOccId = resp.getSectionOccurrenceId();
				}
				else if ( null != resp.getSecOccInstance() ){
					//TODO Bug 450 - need to get the index of the sec occ inst - not sure how!
					secOccId = resp.getSecOccInstance().getSectionOccurrenceId();
				}

				Long entryId = resp.getEntryId();

				if ( null != entryId && null != secOccId) {

					if ( resp instanceof BasicResponse 
							&& EditAction.READWRITE.equals(resp.getEditingPermitted())){
						//Only transform editable responses
						transformResponse((BasicResponse)resp, transformerClients, recordId, docOccId, secOccId, entryId, null, null);
					}
					else if ( resp instanceof CompositeResponse) {
						CompositeResponse cr = (CompositeResponse)resp;
						int counter = 0;
						if (EditAction.READWRITE.equals(resp.getEditingPermitted())) {
							for ( CompositeRow row: cr.getCompositeRows() ){
								counter++;
								for ( BasicResponse br: row.getBasicResponses() ){
									if (EditAction.READWRITE.equals(br.getEditingPermitted())) {
										transformResponse(br, transformerClients, recordId, docOccId, secOccId, entryId, br.getEntryId(), new Integer(counter));
									}
								}
							}
						}
					}
				}
			}
		}        
	}


	/**
	 * @param resp - the response to transform
	 * @param transformerClients - transformer clients for the dataSet to which the entry belongs.
	 * @throws TransformerException 
	 * @throws RemoteException
	 */
	public void transformResponseForExport(Response resp, Map<Long, TransformerClient> transformerClients) throws TransformerException, RemoteException {
		//we need to grab the recordId
		String recordId = resp.getRecord().getIdentifier().getIdentifier();
		Long docOccId = resp.getDocInstance().getOccurrenceId();

		Long secOccId = null;
		if ( null != resp.getSectionOccurrenceId() ){
			secOccId = resp.getSectionOccurrenceId();
		}
		else if ( null != resp.getSecOccInstance() ){
			//TODO Bug 450 - need to get the index of the sec occ inst - not sure how!
			secOccId = resp.getSecOccInstance().getSectionOccurrenceId();
		}

		Long entryId = resp.getEntryId(); 
		if(entryId == null){
			entryId = resp.getEntry().getId();
		}
		if ( resp instanceof BasicResponse ){
			transformResponseForExport((BasicResponse)resp, transformerClients, recordId, docOccId, secOccId, entryId, null, null);
		}
		else {
			//response is a composite response
			CompositeResponse cr = (CompositeResponse)resp;
			int counter = 0;
			for ( CompositeRow row: cr.getCompositeRows() ){
				counter++;
				for ( BasicResponse br: row.getBasicResponses() ){
					transformResponseForExport(br, transformerClients, recordId, docOccId, secOccId, entryId, br.getEntryId(), new Integer(counter));
				}
			}
		}
	}

	/**
	 * 
	 * @param br - the BasicResponse to transform
	 * @param transformerClients - the transformer clients for the dataset of which br is a member
	 * @param recordId
	 * @param docOccId
	 * @param secOccId
	 * @param entryId
	 * @param childEntryId
	 * @param row
	 * @throws TransformerException
	 */
	private void transformResponseForExport(BasicResponse br, Map<Long, TransformerClient> transformerClients, 
			String recordId, Long docOccId, Long secOccId, Long entryId, Long childEntryId, Integer row) throws TransformerException {

		final String METHOD_NAME = "transformResponseForExport";

		//see if the response references an element with transformers
		List<Long> transformerIds = null;
		try{
			transformerIds = dao.getOutputTransformerIds(entryId);
		}
		catch(DAOException ex){
			throw new TransformerException(ex);
		}
		if ( transformerIds.size() > 0 ){
			//this response does reference an entry with transformers
			Value v = br.getTheValue();
			if ( !v.isTransformed() && !v.isNull() ){
				//the value needs to be transformed
				GetValueVisitor getVisitor = new GetValueVisitor();
				v.accept(getVisitor);
				String textValue = getVisitor.getValue();
				String resultClass = null;
				boolean viewable = true;
				for ( Long tId:transformerIds ){
					//find the client
					TransformerClient client = transformerClients.get(tId);
					Call call = client.getWebService();
					if ( null == call ){
						throw new TransformerException("No transformer client found for transformer with id="+tId);
					}
					try{
						textValue = (String)call.invoke(new Object[]{textValue});
					}
					catch(AxisFault fault){
						sLog.error(METHOD_NAME+": "+fault.getClass().getSimpleName(),fault);
						if ( fault.getCause() instanceof ConnectException || 
								fault.getCause() instanceof UnknownHostException ||
								fault.getCause() instanceof NoRouteToHostException ){
							sLog.error(METHOD_NAME+": "+fault.getCause().getClass().getSimpleName(),fault.getCause());
							throw new TransformerException("Cannot connect to transformer web-service '"+
									call.getTargetEndpointAddress()+"' (id="+tId+
							"). Please contact the system administrator, and try again later.");
						}
						else {
							String message = null;
							try{
								message = dao.createTransformerErrorMessage(
										recordId, docOccId, secOccId, entryId, childEntryId, row, fault.getFaultString());
							}
							catch(DAOException ex){
								message = fault.getFaultString()+" ("+ex.getMessage()+")";
							}
							throw new TransformerException(message);
						}
					}
					catch(RemoteException ex){
						throw new TransformerException("Exception when trying to call web-service for transformer id="+tId+
								": "+ex.getMessage(), ex);
					}
					resultClass = client.getResultClass();
					viewable &= client.isViewableOutput();
				}
				//replace the original value with a text value containing
				//the transformed output.
				Value transValue = null;
				try{
					transValue = (Value)Class.forName(resultClass).newInstance();
				}
				catch(Exception ex){
					throw new TransformerException("Cannot instantiate an object of class '"+resultClass+"'", ex);
				}
				v.copy(transValue);
				SetValueVisitor setVisitor = new SetValueVisitor();
				setVisitor.setValue(textValue);
				transValue.accept(setVisitor);
				transValue.setTransformed(true);
				boolean hidden = !viewable;
				transValue.setHidden(hidden);
				br.setTheValue(transValue);
				//replace original value with transformed value in provenance
				for ( Provenance p:br.getProvItems()){
					if ( null != p.getTheCurrentValue() && p.getTheCurrentValue().equals(v) ){
						p.setTheCurrentValue(transValue);
					}
					if ( null != p.getThePrevValue() && p.getThePrevValue().equals(v) ){
						p.setThePrevValue(transValue);
					}
				}
			}
		}
	}

	private void transformResponse(BasicResponse br, Map<Long, TransformerClient> transformerClients, 
			String recordId, Long docOccId, Long secOccId, Long entryId, Long childEntryId, Integer row) throws TransformerException {

		final String METHOD_NAME = "transformResponse";
		//see if the response references an element with transformers
		List<Long> transformerIds = null;
		try{
			transformerIds = dao.getTransformerIds(entryId);
		}
		catch(DAOException ex){
			throw new TransformerException(ex);
		}
		catch(NullPointerException npe) {
			npe.printStackTrace();
		}
		if ( transformerIds.size() > 0 ){
			//this response does reference an entry with transformers
			Value v = br.getTheValue();
			if ( !v.isTransformed() && !v.isNull() ){
				//the value needs to be transformed
				GetValueVisitor getVisitor = new GetValueVisitor();
				v.accept(getVisitor);
				String textValue = getVisitor.getValue();
				String resultClass = null;
				boolean viewable = true;
				for ( Long tId:transformerIds ){
					//find the client
					TransformerClient client = transformerClients.get(tId);
					Call call = client.getWebService();
					if ( null == call ){
						throw new TransformerException("No transformer client found for transformer with id="+tId);
					}
					try{
						textValue = (String)call.invoke(new Object[]{textValue});
					}
					catch(AxisFault fault){
						sLog.error(METHOD_NAME+": "+fault.getClass().getSimpleName(),fault);
						if ( fault.getCause() instanceof ConnectException || 
								fault.getCause() instanceof UnknownHostException ||
								fault.getCause() instanceof NoRouteToHostException ){
							sLog.error(METHOD_NAME+": "+fault.getCause().getClass().getSimpleName(),fault.getCause());
							throw new TransformerException("Cannot connect to transformer web-service '"+
									call.getTargetEndpointAddress()+"' (id="+tId+
							"). Please contact the system administrator, and try again later.");
						}
						else {
							String message = null;
							try{
								message = dao.createTransformerErrorMessage(
										recordId, docOccId, secOccId, entryId, childEntryId, row, fault.getFaultString());
							}
							catch(DAOException ex){
								message = fault.getFaultString()+" ("+ex.getMessage()+")";
							}
							throw new TransformerException(message);
						}
					}
					catch(RemoteException ex){
						throw new TransformerException("Exception when trying to call web-service for transformer id="+tId+
								": "+ex.getMessage(), ex);
					}
					resultClass = client.getResultClass();
					viewable &= client.isViewableOutput();
				}
				//replace the original value with a text value containing
				//the transformed output.
				Value transValue = null;
				try{
					transValue = (Value)Class.forName(resultClass).newInstance();
				}
				catch(Exception ex){
					throw new TransformerException("Cannot instantiate an object of class '"+resultClass+"'", ex);
				}
				v.copy(transValue);
				SetValueVisitor setVisitor = new SetValueVisitor();
				setVisitor.setValue(textValue);
				transValue.accept(setVisitor);
				transValue.setTransformed(true);
				boolean hidden = !viewable;
				transValue.setHidden(hidden);
				br.setTheValue(transValue);
				//replace original value with transformed value in provenance
				for ( Provenance p:br.getProvItems()){
					if ( null != p.getTheCurrentValue() && p.getTheCurrentValue().equals(v) ){
						p.setTheCurrentValue(transValue);
					}
					if ( null != p.getThePrevValue() && p.getThePrevValue().equals(v) ){
						p.setThePrevValue(transValue);
					}
				}
			}
		}
	}



	public Object externalTransform(Long dsId, TransformerDTO transformer, String[] variables, String saml) 
	throws TransformerException {

		final String METHOD_NAME = "externalTransform";

		Long tId = transformer.getId();
		Object result;
		try {
			TransformerClient client = dao.getTransformerClients(dsId).get(tId);

			//String textValue = null; //TODO getVisitor.getValue();
			String resultClass = null;
			boolean viewable = true;


			Call call = client.getWebService();

			if ( null == call ){
				throw new TransformerException("No transformer client found for transformer with id="+transformer.getId());
			}
			try{

				//textValue = (String)call.invoke(variables);
				result = (String)call.invoke(new Object[]{exportData(variables)});
			}
			catch(AxisFault fault){
				sLog.error(METHOD_NAME+": "+fault.getClass().getSimpleName(),fault);
				if ( fault.getCause() instanceof ConnectException || 
						fault.getCause() instanceof UnknownHostException ||
						fault.getCause() instanceof NoRouteToHostException ){
					sLog.error(METHOD_NAME+": "+fault.getCause().getClass().getSimpleName(),fault.getCause());
					throw new TransformerException("Cannot connect to transformer web-service '"+
							call.getTargetEndpointAddress()+"' (id="+tId+
					"). Please contact the system administrator, and try again later.");
				}
				else {
					fault.printStackTrace();
					throw new TransformerException(fault.getFaultString());
				}
			}
			catch(RemoteException ex){
				throw new TransformerException("Exception when trying to call web-service for transformer id="+tId+
						": "+ex.getMessage(), ex);
			}
			resultClass = client.getResultClass();
			viewable &= client.isViewableOutput();

			//replace the original value with a text value containing
			//the transformed output.
			Object transValue = null;
			try{
				transValue = Class.forName(resultClass).newInstance();
			}
			catch(Exception ex){
				throw new TransformerException("Cannot instantiate an object of class '"+resultClass+"'", ex);
			}

			//return transValue;
			return result;
			/*v.copy(transValue);
            SetValueVisitor setVisitor = new SetValueVisitor();
            setVisitor.setValue(textValue);
            transValue.accept(setVisitor);
            transValue.setTransformed(true);
            boolean hidden = !viewable;
            transValue.setHidden(hidden);
            //br.setTheValue(transValue);
            //replace original value with transformed value in provenance
            for ( Provenance p:br.getProvItems()){
                if ( null != p.getTheCurrentValue() && p.getTheCurrentValue().equals(v) ){
                    p.setTheCurrentValue(transValue);
                }
                if ( null != p.getThePrevValue() && p.getThePrevValue().equals(v) ){
                    p.setThePrevValue(transValue);
                }
            }*/

		}
		catch (DAOException daoe) {
			throw new TransformerException(daoe.getMessage());
		}
	}

	private String exportData(String[] entries) {
		String csvInput = "";
		boolean first = true;
		for (String name: entries) {	
			if (first) {
				csvInput = name;
				first = false;
			}
			else {
				//should be integer values only so no special formating required
				csvInput += ","+name;
			}
		}
		return csvInput;
	}
}
