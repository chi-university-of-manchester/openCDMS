package org.psygrid.data.clintouch.util;

import org.psygrid.common.sms.SMSSettings;

public class CreateSMSSettings {
	private static final String SERVICE_URL = "https://www.kapow.co.uk/scripts/";
	private static final String SERVICE_USERNAME = "psygrid";
	private static final String SERVICE_PASSWORD = "173913";
	
	public static SMSSettings getNewSMSSettings() {
		SMSSettings httpSMSSettings = new SMSSettings();
		httpSMSSettings.setServiceURL(SERVICE_URL);
		httpSMSSettings.setServiceUsername(SERVICE_USERNAME);
		httpSMSSettings.setServicePassword(SERVICE_PASSWORD);
		
		return httpSMSSettings;
	}
}
