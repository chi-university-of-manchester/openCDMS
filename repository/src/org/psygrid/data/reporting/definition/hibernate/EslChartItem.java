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


package org.psygrid.data.reporting.definition.hibernate;

import java.util.Map;

import org.hibernate.Session;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.reporting.ChartPoint;
import org.psygrid.data.reporting.definition.IEslChartItem;
import org.psygrid.data.reporting.definition.ReportException;
import org.psygrid.data.utils.esl.IRemoteClient;

/**
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_esl_chart_items" 
 * @hibernate.joined-subclass-key column="c_id"
 */
public class EslChartItem extends AbstractChartItem implements IEslChartItem {

	private String fieldName;

	public EslChartItem(){}
	
	public EslChartItem(String fieldName){
		this.fieldName = fieldName;
	}
	
	/**
	 * @hibernate.property column="c_fieldname"
	 */
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
    @Override
	public String getLabel() throws ReportException {
    	//To provide the label just capitalize the field name
    	StringBuilder builder = new StringBuilder();
    	builder.append(Character.toUpperCase(fieldName.charAt(0)));
    	builder.append(fieldName.substring(1));
		return builder.toString();
	}

	@Override
	public ChartPoint getPoint(Session session, IRemoteClient eslClient, Long recordId, String saml) throws ReportException {
    	//Step 1. find the identifier for this record
		String identifier = (String)session.createQuery("select r.identifier.identifier from Record r where r.id=?")
										   .setLong(0, recordId)
										   .uniqueResult();
		//Step 2. query the ESL to get the value of the specified property for this identifier
		String value = null;
		try{
			value = eslClient.getEslProperty(identifier, fieldName, saml);
		}
		catch(Exception ex){
			throw new ReportException(ex);
		}
		
		ChartPoint point = new ChartPoint();
		point.setValue(value);
		point.setValueType(IValue.TYPE_STRING);
		
		return point;
	}

	@Override
    public org.psygrid.data.reporting.definition.dto.EslChartItem toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        //check for an already existing instance of a dto object for this 
        //simple chart item in the map of references
        org.psygrid.data.reporting.definition.dto.EslChartItem dtoECI = null;
        if ( dtoRefs.containsKey(this)){
            dtoECI = (org.psygrid.data.reporting.definition.dto.EslChartItem)dtoRefs.get(this);
        }
        else {
            //an instance of the element has not already
            //been created, so create it, and add it to the
            //map of references
            dtoECI = new org.psygrid.data.reporting.definition.dto.EslChartItem();
            dtoRefs.put(this, dtoECI);
            toDTO(dtoECI, dtoRefs, depth);
        }
        
        return dtoECI;
    }

    public void toDTO(org.psygrid.data.reporting.definition.dto.EslChartItem dtoECI, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        super.toDTO(dtoECI, dtoRefs, depth);
        dtoECI.setFieldName(this.fieldName);
    }

}
