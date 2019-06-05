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

package org.psygrid.data.reporting.definition.dto;

import java.util.Map;

import org.psygrid.data.model.dto.DocumentOccurrenceDTO;
import org.psygrid.data.model.dto.EntryDTO;
import org.psygrid.data.model.dto.PersistentDTO;
import org.psygrid.data.model.dto.SectionOccurrenceDTO;

public class SimpleChartItem extends AbstractChartItem {

    private EntryDTO entry;
    
    private DocumentOccurrenceDTO docOccurrence;
    
    private SectionOccurrenceDTO secOccurrence;

    private String options;
    
    private String labelOptions;
    
    public DocumentOccurrenceDTO getDocOccurrence() {
        return docOccurrence;
    }

    public void setDocOccurrence(DocumentOccurrenceDTO docOccurrence) {
        this.docOccurrence = docOccurrence;
    }

    public EntryDTO getEntry() {
        return entry;
    }

    public void setEntry(EntryDTO entry) {
        this.entry = entry;
    }

    public SectionOccurrenceDTO getSecOccurrence() {
        return secOccurrence;
    }

    public void setSecOccurrence(SectionOccurrenceDTO secOccurrence) {
        this.secOccurrence = secOccurrence;
    }
    
    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getLabelOptions() {
        return labelOptions;
    }

    public void setLabelOptions(String labelOptions) {
        this.labelOptions = labelOptions;
    }

    @Override
    public org.psygrid.data.reporting.definition.hibernate.SimpleChartItem toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        //check for an already existing instance of a hibernate object for this 
        //simple chart item in the map of references
        org.psygrid.data.reporting.definition.hibernate.SimpleChartItem hSCI = null;
        if ( hRefs.containsKey(this)){
            hSCI = (org.psygrid.data.reporting.definition.hibernate.SimpleChartItem)hRefs.get(this);
        }
        else{
            //an instance of the simple chart item has not already
            //been created, so create it, and add it to the
            //map of references
            hSCI = new org.psygrid.data.reporting.definition.hibernate.SimpleChartItem();
            hRefs.put(this, hSCI);
            toHibernate(hSCI, hRefs);
        }
        
        return hSCI;
    }

    public void toHibernate(org.psygrid.data.reporting.definition.hibernate.SimpleChartItem hSCI, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        super.toHibernate(hSCI, hRefs);
        if ( null != this.entry ){
            hSCI.setEntry(this.entry.toHibernate(hRefs));
        }
        if ( null != this.docOccurrence ){
            hSCI.setDocOccurrence(this.docOccurrence.toHibernate(hRefs));
        }
        if ( null != this.secOccurrence ){
            hSCI.setSecOccurrence(this.secOccurrence.toHibernate(hRefs));
        }
        hSCI.setOptions(this.options);
        hSCI.setLabelOptions(this.labelOptions);
    }

}
