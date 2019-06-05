/*
Copyright (c) 2008-2010, The University of Manchester, UK.

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

package org.psygrid.common.test.utils;

import java.util.Properties;

import org.psygrid.common.email.PsyGridMailSenderImpl;

public class MailSenderInitializer {

	public static void initToSendPartial(PsyGridMailSenderImpl sender, Properties props){
		props.put("mail.smtp.sendpartial", "true");
		sender.setJavaMailProperties(props);
	}
	
	public static void initLegitimateServer(PsyGridMailSenderImpl sender, Properties props){
		sender.setHost(props.getProperty("smtp.host"));
		
		if(props.getProperty("smtp.username") != null && props.getProperty("smtp.password") != null){
			sender.setUsername(props.getProperty("smtp.username")); 
			sender.setPassword(props.getProperty("smtp.password")); 
		}
		
		sender.setPort(Integer.valueOf(props.getProperty("smtp.port", String.valueOf(PsyGridMailSenderImpl.DEFAULT_PORT)))); 
	}
	
	public static void initBogusServer(PsyGridMailSenderImpl sender, Properties props){
		sender.setHost(props.getProperty("smtp.bogus.host"));
		
		if(props.getProperty("smtp.username") != null && props.getProperty("smtp.password") != null){
			sender.setUsername(props.getProperty("smtp.username")); 
			sender.setPassword(props.getProperty("smtp.password")); 
		}
		
		sender.setPort(Integer.valueOf(props.getProperty("smtp.port", String.valueOf(PsyGridMailSenderImpl.DEFAULT_PORT)))); 
	}
	
}
