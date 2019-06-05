package org.psygrid.data.clintouch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmTimes {
	private static final int NUMBER_OF_ALARMS_PER_DAY = 4;
	private static final int ALARM_TIMEOUT_MINUTES = 15;
	
	private List<AlarmData> alarms = new ArrayList<AlarmData>();
	
	public AlarmTimes() {
		alarms.add(new AlarmData(10,20,1,1));
		alarms.add(new AlarmData(12,45,1,2));
		alarms.add(new AlarmData(14,15,1,3));
		alarms.add(new AlarmData(20,0,1,4));
		
		alarms.add(new AlarmData(10,30,2,1));
		alarms.add(new AlarmData(12,45,2,2));
		alarms.add(new AlarmData(15,20,2,3));
		alarms.add(new AlarmData(19,5,2,4));
		
		alarms.add(new AlarmData(9,30,3,1));
		alarms.add(new AlarmData(14,30,3,2));
		alarms.add(new AlarmData(15,55,3,3));
		alarms.add(new AlarmData(18,25,3,4));
		
		alarms.add(new AlarmData(11,5,4,1));
		alarms.add(new AlarmData(12,40,4,2));
		alarms.add(new AlarmData(16,5,4,3));
		alarms.add(new AlarmData(19,30,4,4));
		
		alarms.add(new AlarmData(9,0,5,1));
		alarms.add(new AlarmData(15,0,5,2));
		alarms.add(new AlarmData(18,0,5,3));
		alarms.add(new AlarmData(20,35,5,4));
		
		alarms.add(new AlarmData(9,50,6,1));
		alarms.add(new AlarmData(13,20,6,2));
		alarms.add(new AlarmData(17,0,6,3));
		alarms.add(new AlarmData(19,55,6,4));
	}
	
	/**
	 * Get the currently active event number, based on the current time. Returns -1 if no alarm active
	 * @param startDate When the participant started the study
	 * @return			The currently active event number or -1 if no alarm active
	 */
	public int getCurrentEventNumber(Date startDate) {
		int currentDayOfStudy = calculateCurrentDayOfStudy(startDate);
		return findMatchingEventNumberWithinDay(currentDayOfStudy, startDate);
	}
	
	/**
	 * Get the most recent (but not current event number)
	 * @param startDate When the participant started the study
	 * @return			The most recent number or -1 if no alarms have happened yet or all alarms have passed
	 */
	public int getLastEventNumber(Date startDate) {
		int currentDayOfStudy = calculateCurrentDayOfStudy(startDate);
		return findMostRecentEventWithinDay(currentDayOfStudy, startDate);
	}
	
	public int getTotalAlarmCount() {
		return alarms.size();
	}
	
	public boolean hasDemoTimedOut(Calendar demoStartTime) {
		// Need to use a temporary here to stop demo start time being updated.
		Calendar tempDemoStartTime = Calendar.getInstance();
		tempDemoStartTime.setTime(demoStartTime.getTime());
		if(isAlarmStillActive(Calendar.getInstance(), tempDemoStartTime)) {
			return false;
		}
		
		return true;
	}
	
	private int calculateCurrentDayOfStudy(Date startDate) {
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);
		Calendar currentCalendar = Calendar.getInstance();
		int currentDayOfStudy = 0;
		startCalendar.add(Calendar.DAY_OF_MONTH, 1);
		while(currentCalendar.after(startCalendar)) {
			currentDayOfStudy++;
			startCalendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		return currentDayOfStudy;
	}
	
	private int findMatchingEventNumberWithinDay(int currentDayOfStudy, Date startDate) {
		Calendar alarmCalendar = getAlarmCalendar(currentDayOfStudy, startDate);
		Calendar currentCalendar = Calendar.getInstance();
		int firstEventForCurrentDay = getFirstEventForCurrentDay(currentDayOfStudy);
		if(firstEventForCurrentDay < 0) {
			return firstEventForCurrentDay;
		}
		int result = -1;
		
		for(int eventNumber = firstEventForCurrentDay; eventNumber < firstEventForCurrentDay+NUMBER_OF_ALARMS_PER_DAY; eventNumber++) {
			alarmCalendar.set(Calendar.HOUR_OF_DAY, alarms.get(eventNumber).getHour());
			alarmCalendar.set(Calendar.MINUTE, alarms.get(eventNumber).getMinute());
			if(currentCalendar.after(alarmCalendar)) {
				if(isAlarmStillActive(currentCalendar, alarmCalendar)) {
					result = eventNumber;
				}
			}
		}
		
		return result;
	}
	
	private int findMostRecentEventWithinDay(int currentDayOfStudy, Date startDate) {
		Calendar alarmCalendar = getAlarmCalendar(currentDayOfStudy, startDate);
		Calendar currentCalendar = Calendar.getInstance();
		int firstEventForCurrentDay = getFirstEventForCurrentDay(currentDayOfStudy);
		if(firstEventForCurrentDay < 0) {
			return firstEventForCurrentDay;
		}
		int result = -1;
		
		for(int eventNumber = firstEventForCurrentDay; eventNumber < firstEventForCurrentDay+NUMBER_OF_ALARMS_PER_DAY; eventNumber++) {
			alarmCalendar.set(Calendar.HOUR_OF_DAY, alarms.get(eventNumber).getHour());
			alarmCalendar.set(Calendar.MINUTE, alarms.get(eventNumber).getMinute()+ALARM_TIMEOUT_MINUTES);
			if(currentCalendar.after(alarmCalendar)) {
				result = eventNumber;
			}
		}
		
		return result;
	}
	
	/**
	 * The alarm will timeout after ALARM_TIMEOUT_MINUTES and no further answers should be processed
	 * @param currentCalendar
	 * @param alarmCalendar
	 * @return
	 */
	private boolean isAlarmStillActive(Calendar currentCalendar, Calendar alarmCalendar) {
		alarmCalendar.add(Calendar.MINUTE, ALARM_TIMEOUT_MINUTES);
		if(currentCalendar.before(alarmCalendar)) {
			return true;
		}
		
		return false;
	}
	
	private int getFirstEventForCurrentDay(int currentDayOfStudy) {
		int firstEventForCurrentDay = currentDayOfStudy*NUMBER_OF_ALARMS_PER_DAY;
		if(firstEventForCurrentDay >= alarms.size()) {
			return -1;
		}
		
		return firstEventForCurrentDay;
	}
	
	private Calendar getAlarmCalendar(int currentDayOfStudy, Date startDate) {
		Calendar alarmCalendar = Calendar.getInstance();
		alarmCalendar.setTime(startDate);
		alarmCalendar.add(Calendar.DAY_OF_MONTH, currentDayOfStudy);
		
		return alarmCalendar;
	}
}
