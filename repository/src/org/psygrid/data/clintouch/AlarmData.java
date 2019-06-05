package org.psygrid.data.clintouch;

public class AlarmData {
	private int hour;
	private int minute;
	private int dayNumber;
	private int eventNumber;
	
	public AlarmData(int hour, int minute, int dayNumber, int eventNumber) {
		this.hour = hour;
		this.minute = minute;
		this.dayNumber = dayNumber;
		this.eventNumber = eventNumber;
	}
	
	public int getHour() {
		return hour;
	}
	
	public int getMinute() {
		return minute;
	}
	
	public int getDayNumber() {
		return dayNumber;
	}
	
	public int getEventNumber() {
		return eventNumber;
	}
}
