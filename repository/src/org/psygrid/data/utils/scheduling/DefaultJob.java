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

package org.psygrid.data.utils.scheduling;

import org.psygrid.data.repository.Repository;
import org.psygrid.data.repository.RepositoryServiceInternal;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Abstract Quartz job bean to hold common properties and
 * functionality for all concrete job beans.
 * 
 * @author Rob Harper
 *
 */
public abstract class DefaultJob extends QuartzJobBean {

    /**
     * The mail sender used to send the email messages
     */
    protected JavaMailSender mailSender;
        
    /**
     * Repository service
     */
    protected RepositoryServiceInternal repositoryService;

	/**
     * A property to allow whether mails are actually sent, or just recorded
     * in the system logs, to be set declaratively. To aid testing.
     */
    protected boolean sendMails;
    
    /**
     * The email address of the system administrator
     */
    protected String sysAdminEmail;
    
    
	/**
	 * @return the repositoryService
	 */
	public RepositoryServiceInternal getRepositoryService() {
		return repositoryService;
	}

	/**
	 * @param repositoryService the repositoryService to set
	 */
	public void setRepositoryService(RepositoryServiceInternal repositoryService) {
		this.repositoryService = repositoryService;
	}

	public JavaMailSender getMailSender() {
        return mailSender;
    }
    
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public boolean isSendMails() {
        return sendMails;
    }

    public void setSendMails(boolean sendMails) {
        this.sendMails = sendMails;
    }

    public String getSysAdminEmail() {
        return sysAdminEmail;
    }

    public void setSysAdminEmail(String sysAdminEmail) {
        this.sysAdminEmail = sysAdminEmail;
    }

}
