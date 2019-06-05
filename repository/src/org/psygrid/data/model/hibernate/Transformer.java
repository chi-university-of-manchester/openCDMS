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

package org.psygrid.data.model.hibernate;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to represent a transformer for transforming
 * the value of a data item.
 * <p>
 * The attributes of the web-service method that is called
 * to perform the transformation are defined in this class.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_transformers"  
 * 							
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Transformer extends Persistent {

    /**
     * The web-service URL for the transformer.
     */
    private String wsUrl;
    
    /**
     * The web-service namespace for the transformer.
     */
    private String wsNamespace;
    
    /**
     * The web-service operation for the transformer.
     */
    private String wsOperation;
    
    /**
     * The name of the sub-class of Value that the result
     * of the transformation will be stored in.
     */
    private String resultClass;
    
    /**
     * If true, the output of the transformation can be viewed
     * by humans; if false the output of the transformation should
     * not be viewed by humans.
     */
    private boolean viewableOutput;
    
    /**
     * Default no-arg constructor.
     */
    public Transformer(){}

    /**
     * Constructor that accepts the URL, namespace, operation and
     * result class of the new transformer object.
     * 
     * @param wsUrl The web-service URL.
     * @param wsNamespace The web-service namespace.
     * @param wsOperation The web-service operation.
     * @param resultClass The name of the sub-class of Value that 
     * the result of the transformation will be stored in.
     */
    public Transformer(String wsUrl, String wsNamespace, String wsOperation, String resultClass){
        this.wsUrl = wsUrl;
        this.wsNamespace = wsNamespace;
        this.wsOperation = wsOperation;
        this.resultClass = resultClass;
        this.viewableOutput = false;
    }
    
    /**
     * Constructor that accepts the URL, namespace, operation, result
     * class and viewable output flag of the new transformer object.
     * 
     * @param wsUrl The web-service URL.
     * @param wsNamespace The web-service namespace.
     * @param wsOperation The web-service operation.
     * @param resultClass The name of the sub-class of Value that 
     * the result of the transformation will be stored in.
     */
    public Transformer(String wsUrl, String wsNamespace, String wsOperation, String resultClass, boolean viewableOutput){
        this.wsUrl = wsUrl;
        this.wsNamespace = wsNamespace;
        this.wsOperation = wsOperation;
        this.resultClass = resultClass;
        this.viewableOutput = viewableOutput;
    }
    
    /**
     * Get the namespace of the web-service that the transformer
     * utilizes to perform data transformation.
     * 
     * @return The namespace of the web-service.
     * @hibernate.property column="c_namespace"
     */
    public String getWsNamespace() {
        return wsNamespace;
    }

    /**
     * Set the namespace of the web-service that the transformer
     * utilizes to perform data transformation.
     * 
     * @param wsNamespace The namespace of the web-service.
     */
    public void setWsNamespace(String wsNamespace) {
        this.wsNamespace = wsNamespace;
    }

    /**
     * Get the operation of the web-service that the 
     * transformer utilizes to perform data transformation.
     * 
     * @return The operation of the web-service.
     * @hibernate.property column="c_operation"
     */
    public String getWsOperation() {
        return wsOperation;
    }

    /**
     * Set the operation of the web-service that the 
     * transformer utilizes to perform data transformation.
     * 
     * @param wsOperation The operation of the web-service.
     */
    public void setWsOperation(String wsOperation) {
        this.wsOperation = wsOperation;
    }

    /**
     * Get the URL of the web-service that the 
     * transformer utilizes to perform data transformation.
     * 
     * @return The URL of the web-service.
     * @hibernate.property column="c_url"
     */
    public String getWsUrl() {
        return wsUrl;
    }

    /**
     * Set the URL of the web-service that the 
     * transformer utilizes to perform data transformation.
     * 
     * @param wsUrl The URL of the web-service.
     */
    public void setWsUrl(String wsUrl) {
        this.wsUrl = wsUrl;
    }
    
    /**
     * Get the name of the sub-class of Value that the result
     * of the transformation will be stored in.
     * 
     * @return The name of the sub-class of Value.
     * @hibernate.property column="c_result_class"
     */
    public String getResultClass() {
        return resultClass;
    }

    /**
     * Set the name of the sub-class of Value that the result
     * of the transformation will be stored in.
     * 
     * @param resultClass The name of the sub-class of Value.
     */
    public void setResultClass(String resultClass) {
        this.resultClass = resultClass;
    }

    /**
     * Get the value of the viewable output flag.
     * <p>
     * If true, the output of the transformation can be viewed
     * by humans; if false the output of the transformation should
     * not be viewed by humans.
     * 
     * @return The viewable output flag.
     * @hibernate.property column="c_viewable_out"
     */
    public boolean isViewableOutput() {
        return viewableOutput;
    }

    /**
     * Set the value of the viewable output flag.
     * <p>
     * If true, the output of the transformation can be viewed
     * by humans; if false the output of the transformation should
     * not be viewed by humans.
     * 
     * @param viewableOutput The viewable output flag.
     */
    public void setViewableOutput(boolean viewableOutput) {
        this.viewableOutput = viewableOutput;
    }

    public org.psygrid.data.model.dto.TransformerDTO toDTO() {
    	 return toDTO(RetrieveDepth.DS_SUMMARY);
    }
    
    public org.psygrid.data.model.dto.TransformerDTO toDTO(RetrieveDepth depth){
        //create list to hold references to objects in the transformers's
        //object graph which have multiple references to them within
        //the object graph. This is used so that each object instance
        //is copied to its DTO equivalent once and once only
        Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
        org.psygrid.data.model.dto.TransformerDTO dtoT = toDTO(dtoRefs, depth);
        dtoRefs = null;
        return dtoT;
    }
    
    public org.psygrid.data.model.dto.TransformerDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //transformer in the map of references
        org.psygrid.data.model.dto.TransformerDTO dtoT = null;
        if ( dtoRefs.containsKey(this)){
            dtoT = (org.psygrid.data.model.dto.TransformerDTO)dtoRefs.get(this);
        }
        if ( null == dtoT ){
            //an instance of the element has not already
            //been created, so create it, and add it to the
            //map of references
            dtoT = new org.psygrid.data.model.dto.TransformerDTO();
            dtoRefs.put(this, dtoT);
            toDTO(dtoT, dtoRefs, depth);
        }
        
        return dtoT;
    }
    
    public void toDTO(org.psygrid.data.model.dto.TransformerDTO dtoT, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoT, dtoRefs, depth);
        //if this method is being called when converting a Record
        //object then there is no need to get the details of the
        //transformer, all we need is its ID.
        if ( RetrieveDepth.RS_COMPLETE != depth &&
                RetrieveDepth.RS_NO_BINARY != depth &&
                RetrieveDepth.RS_SUMMARY != depth ){
            dtoT.setWsNamespace(this.wsNamespace);
            dtoT.setWsOperation(this.wsOperation);
            dtoT.setWsUrl(this.wsUrl);
            dtoT.setResultClass(this.resultClass);
            dtoT.setViewableOutput(this.viewableOutput);
        }
    }
    
    /**
     * Tests for logical equivalence
     * @param comparisonTransformer
     * @return true only if the comparison transformer is not null and they are
     * 			logically equal.
     */
    public boolean isEquivalentTo(Transformer comparisonTransformer) {
    	
    	if(comparisonTransformer == null) {
    		return false;
    	}
    	
    	if(wsUrl == null) {
    		if(comparisonTransformer.wsUrl != null) {
    			return false;
    		}
    	}else if(!wsUrl.equals(comparisonTransformer.wsUrl)) {
    		return false;
    	}
    	
    	if(wsNamespace == null) {
    		if(comparisonTransformer.wsNamespace != null) {
    			return false;
    		}
    	}else if(!wsNamespace.equals(comparisonTransformer.wsNamespace)) {
    		return false;
    	}
    	
    	if(wsOperation == null) {
    		if(comparisonTransformer.wsOperation != null) {
    			return false;
    		}
    	}else if(!wsOperation.equals(comparisonTransformer.wsOperation)) {
    		return false;
    	}
    	
    	if(resultClass == null) {
    		if(comparisonTransformer.resultClass != null) {
    			return false;
    		}
    	}else if(!resultClass.equals(comparisonTransformer.resultClass)) {
    		return false;
    	}
    	
    	if(viewableOutput != comparisonTransformer.viewableOutput) {
    		return false;
    	}
    	
    	return true;
    }
}
