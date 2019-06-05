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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Class to represent a single reminder that is associated with
 * a schedulable object.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_reminders"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Reminder extends Persistent {

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
    
    public static final String SUBJECT = "subject";
    public static final String BODY = "body";
    
    protected Integer time;
    
    protected TimeUnits timeUnits;
    
    protected ReminderLevel level;
    
    /**
     * Default no-arg constructor.
     */
    public Reminder(){}
    
    public Reminder(Integer time, TimeUnits units, ReminderLevel level){
        this.time = time;
        this.timeUnits = units;
        this.level = level;
    }
    
    /**
     * Get the value of the time when the reminder is to be sent,
     * relative to the time of creation of the Record that the 
     * instance of the schedulable that the reminder is associated
     * with will be a part of.
     * 
     * @return The time value.
     * @hibernate.property column="c_time"
     */
    public Integer getTime() {
        return this.time;
    }

    /**
     * Set the value of the time when the reminder is to be sent,
     * relative to the time of creation of the Record that the 
     * instance of the schedulable that the reminder is associated
     * with will be a part of.
     * 
     * @param time The time value.
     */
    public void setTime(Integer scheduleTime) {
        this.time = scheduleTime;
    }

    /**
     * Get the units of the reminder time.
     * 
     * @return The units of the reminder time.
     */
    public TimeUnits getTimeUnits() {
        return this.timeUnits;
    }

    /**
     * Set the units of the reminder time.
     * 
     * @param units The units of the reminder time.
     */
    public void setTimeUnits(TimeUnits scheduleUnits) {
        this.timeUnits = scheduleUnits;
    }

    public ReminderLevel getLevel() {
        return level;
    }

    public void setLevel(ReminderLevel level) {
        this.level = level;
    }

    /**
     * Get the string value of the enumerated schedule units.
     * <p>
     * Only used by Hibernate to persist the string value of the 
     * enumerated schedule units.
     * 
     * @return The string value of the enumerated schedule units.
     * 
     * @hibernate.property column="c_units"
     */
    protected String getEnumUnits() {
        if ( null == this.timeUnits ){
            return null;
        }
        else{
            return this.timeUnits.toString();
        }
    }

    /**
     * Set the string value of the enumerated schedule units.
     * <p>
     * Only used by Hibernate to un-persist the string value of 
     * the enumerated schedule units.
     * 
     * @param enumType The string value of the enumerated 
     * schedule units
     */
    protected void setEnumUnits(String enumUnits) {
        if ( null == enumUnits ){
            setTimeUnits(null);
        }
        else{
            setTimeUnits(TimeUnits.valueOf(enumUnits));
        }
    }
    
    /**
     * Get the string value of the enumerated reminder level.
     * <p>
     * Only used by Hibernate to persist the string value of the 
     * enumerated reminder level.
     * 
     * @return The string value of the enumerated reminder level.
     * 
     * @hibernate.property column="c_level"
     */
    protected String getEnumLevel() {
        if ( null == this.level ){
            return null;
        }
        else{
            return this.level.toString();
        }
    }

    /**
     * Set the string value of the enumerated reminder level.
     * <p>
     * Only used by Hibernate to un-persist the string value of 
     * the enumerated reminder level.
     * 
     * @param enumType The string value of the enumerated 
     * reminder level
     */
    protected void setEnumLevel(String enumLevel) {
        if ( null == enumLevel ){
            setLevel(null);
        }
        else{
            setLevel(ReminderLevel.valueOf(enumLevel));
        }
    }
    
    public Map<String, String> generateMessage(String element, 
                                               String record,
                                               Date dueDate){
        StringBuilder body = new StringBuilder();
        String subject = null;
        switch (this.level){
        case MILD:
        case NORMAL:
            subject = "PsyGrid Reminder";
            body.append("This is a reminder that the assessment '");
            body.append(element).append("' ");
            body.append("for subject '").append(record).append("' ");
            body.append("is due to be completed on or soon after ");
            body.append(dateFormatter.format(dueDate));
        case SEVERE:
            subject = "PsyGrid Warning";
            body.append("This is a warning that the assessment '");
            body.append(element).append("' ");
            body.append("for subject '").append(record).append("' ");
            body.append("should have been completed on ");
            body.append(dateFormatter.format(dueDate));
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put(Reminder.SUBJECT, subject);
        map.put(Reminder.BODY, body.toString());
        return map;
    }
    
    public Map<String, String> generateMessage(String element, 
                                               String occurrence,
                                               String record,
                                               Date dueDate){
        StringBuilder body = new StringBuilder();
        String subject = null;
        switch (this.level){
        case MILD:
            subject = "PSYGRID: Notification for "+record;
            body.append("This is a notification that the assessment '");
            body.append(element).append(" - ").append(occurrence).append("' ");
            body.append("for subject '").append(record).append("' ");
            body.append("is due to be completed on or soon after ");
            body.append(dateFormatter.format(dueDate));
            break;
        case NORMAL:
            subject = "PSYGRID: Reminder for "+record;
            body.append("This is a reminder that the assessment '");
            body.append(element).append(" - ").append(occurrence).append("' ");
            body.append("for subject '").append(record).append("' ");
            body.append("is due to be completed on or soon after ");
            body.append(dateFormatter.format(dueDate));
            break;
        case SEVERE:
            subject = "PSYGRID: Warning for "+record;
            body.append("This is a warning that the assessment '");
            body.append(element).append(" - ").append(occurrence).append("' ");
            body.append("for subject '").append(record).append("' ");
            body.append("should have been completed on ");
            body.append(dateFormatter.format(dueDate));
            break;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put(Reminder.SUBJECT, subject);
        map.put(Reminder.BODY, body.toString());
        return map;
    }

    public org.psygrid.data.model.dto.ReminderDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        org.psygrid.data.model.dto.ReminderDTO dtoR = new org.psygrid.data.model.dto.ReminderDTO();
        toDTO(dtoR, dtoRefs, depth);
        return dtoR;        
    }

    public void toDTO(org.psygrid.data.model.dto.ReminderDTO dtoR, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoR, dtoRefs, depth);
        dtoR.setTime(this.time);
        if ( null != this.timeUnits ){
            dtoR.setTimeUnits(this.timeUnits.toString());
        }
        if ( null != this.level ){
            dtoR.setLevel(this.level.toString());
        }
    }
    
}
