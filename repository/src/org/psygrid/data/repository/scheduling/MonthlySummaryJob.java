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

package org.psygrid.data.repository.scheduling;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.utils.scheduling.DefaultJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;

/**
 * Quartz job to schedule the 
 * 
 * @author Rob Harper
 *
 */
public class MonthlySummaryJob extends DefaultJob {

    private static Log sLog = LogFactory.getLog(MonthlySummaryJob.class);
    
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try{
            //get the current system time
            Date now = new Date();
            
        	sLog.info("Executing MonthlySummaryJob at "+now);
        	
            List<SimpleMailMessage> summaries = repositoryService.getAllMonthlySummaries(now);
            sLog.info("Number of monthly summaries to send="+summaries.size());
            
            for ( SimpleMailMessage msg: summaries ){
                try{
                    if ( sendMails ){
                        mailSender.send(msg);
                    }
                    else{
                        sLog.info("Monthly Summary: To "+msg.getTo()+": Subject="+msg.getSubject()+": Body="+msg.getText());
                    }
                }
                catch(MailException ex) {
                    //log it and go on
                    sLog.error("Error when trying to send email", ex);            
                }
            }
        }
        catch(DAOException ex){
            sLog.error("executeInternal"+": "+ex.getClass().getSimpleName(),ex);
        }
        catch(RuntimeException ex){
            sLog.error("executeInternal"+": "+ex.getClass().getSimpleName(),ex);
        }
    }

}
