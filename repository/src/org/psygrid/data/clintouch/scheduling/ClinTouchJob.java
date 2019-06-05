package org.psygrid.data.clintouch.scheduling;

import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.clintouch.ClinTouchService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class ClinTouchJob extends QuartzJobBean {
	private static Log sLog = LogFactory.getLog(ClinTouchJob.class);
	
	private ClinTouchService clinTouchService;
	
	private boolean clinTouchEnabled;
	
	public ClinTouchService getClinTouchService() {
		return clinTouchService;
	}

	public void setClinTouchService(ClinTouchService clinTouchService) {
		this.clinTouchService = clinTouchService;
	}
	
	public void setClinTouchEnabled(boolean clinTouchEnabled) {
		this.clinTouchEnabled = clinTouchEnabled;
	}

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		if(!clinTouchEnabled) {
			return;
		}
		sLog.info("Running ClinTouchJob");
		try {			
			clinTouchService.run();
		} catch (RemoteException e) {
			sLog.error("Error running ClinTouchService");
		}		
	}

}
