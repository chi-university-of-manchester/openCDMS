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

package org.psygrid.data.utils.email;

import org.springframework.mail.MailException;

/**
 * Interface to represent an email client used by the repository to send
 * emails.
 * 
 * @author Rob Harper
 *
 */
public interface MailClient {

    /**
     * Send an email to PsyGrid Support with the given title and body.
     * <p>
     * The From address is found by performing an Attribute Authority
     * lookup using the user argument, which is the DN of the user
     * requesting to send the email.
     * 
     * @param subject The subject of the email.
     * @param body The body of the email.
     * @param user The DN of the user sending the email.
     * @throws MailException
     */
    public void sendSupportEmail(String subject, String body, String user) 
            throws MailException;
    
}
