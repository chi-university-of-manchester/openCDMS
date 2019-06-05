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

package org.psygrid.data.utils;

import java.util.Date;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Class to test sending emails for the PsyGrid system.
 * <p>
 * Assuming that you are in the root of the repository project
 * run this with:
 * <br>
 * <code>
 * java -cp ../psygrid-common/lib/spring.jar:../psygrid-common/lib/mail.jar:../psygrid-common/lib/activation.jar:../psygrid-common/lib/commons-logging-1.0.4.jar:build 
 * org.psygrid.data.utils.MailSendTest smtp-server email-address
 * </code>
 * 
 * @author Rob Harper
 *
 */
public class MailSendTest {

    /**
     * @param args
     */
    public static void main(String[] args) {

        if ( args.length != 2 ){
            System.out.println("Usage: MailSendTest <smtp-location> <email-address>");
        }
        
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(args[0]);
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("support@psygrid.org");
        message.setTo(args[1]);
        message.setSentDate(new Date());
        message.setSubject("Test");
        message.setText(
                "This is a test email sent by the PsyGrid system.\n\n"+
                "The mail was sent to email address "+args[1]+" using the mail server "+args[0]+".\n\n"+
                "If you are not the intended recipient of this message please contact support@psygrid.org.");
        
        sender.send(message);
        
    }

}
