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

package org.psygrid.data.model.dto;

import java.util.Map;

public class TransformerDTO extends PersistentDTO {

    private String wsUrl;
    
    private String wsNamespace;
    
    private String wsOperation;

    private String resultClass;
    
    private boolean viewableOutput;
    
    public String getWsNamespace() {
        return wsNamespace;
    }

    public void setWsNamespace(String wsNamespace) {
        this.wsNamespace = wsNamespace;
    }

    public String getWsOperation() {
        return wsOperation;
    }

    public void setWsOperation(String wsOperation) {
        this.wsOperation = wsOperation;
    }

    public String getWsUrl() {
        return wsUrl;
    }

    public void setWsUrl(String wsUrl) {
        this.wsUrl = wsUrl;
    }
    
    public String getResultClass() {
        return resultClass;
    }

    public void setResultClass(String wsResultClass) {
        this.resultClass = wsResultClass;
    }

    public boolean isViewableOutput() {
        return viewableOutput;
    }

    public void setViewableOutput(boolean viewableOutput) {
        this.viewableOutput = viewableOutput;
    }

    public org.psygrid.data.model.hibernate.Transformer toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        //check for an already existing instance of a hibernate object for this 
        //transformer in the map of references
        org.psygrid.data.model.hibernate.Transformer hT = null;
        if ( hRefs.containsKey(this)){
            hT = (org.psygrid.data.model.hibernate.Transformer)hRefs.get(this);
        }
        if ( null == hT ){
            //an instance of the element has not already
            //been created, so create it, and add it to the
            //map of references
            hT = new org.psygrid.data.model.hibernate.Transformer();
            hRefs.put(this, hT);
            toHibernate(hT, hRefs);
        }
        
        return hT;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.Transformer hT, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hT, hRefs);
        hT.setWsNamespace(this.wsNamespace);
        hT.setWsOperation(this.wsOperation);
        hT.setWsUrl(this.wsUrl);
        hT.setResultClass(this.resultClass);
        hT.setViewableOutput(this.viewableOutput);
    }
    
}
