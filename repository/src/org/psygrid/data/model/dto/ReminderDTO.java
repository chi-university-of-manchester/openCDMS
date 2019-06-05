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

import org.psygrid.data.model.hibernate.ReminderLevel;
import org.psygrid.data.model.hibernate.TimeUnits;

public class ReminderDTO extends PersistentDTO {

    private Integer time;
    
    private String timeUnits;
    
    private String level;
    
    public Integer getTime() {
        return this.time;
    }

    public void setTime(Integer scheduleTime) {
        this.time = scheduleTime;
    }

    public String getTimeUnits() {
        return this.timeUnits;
    }

    public void setTimeUnits(String scheduleUnits) {
        this.timeUnits = scheduleUnits;
    }
    
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public org.psygrid.data.model.hibernate.Reminder toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        org.psygrid.data.model.hibernate.Reminder hR = new org.psygrid.data.model.hibernate.Reminder();
        toHibernate(hR, hRefs);
        return hR;
    }
    
    public void toHibernate(org.psygrid.data.model.hibernate.Reminder hR, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hR, hRefs);
        hR.setTime(this.time);
        if ( null != this.timeUnits ){
            hR.setTimeUnits(TimeUnits.valueOf(this.timeUnits));
        }
        if ( null != this.level ){
            hR.setLevel(ReminderLevel.valueOf(this.level));
        }
    }

    
}
