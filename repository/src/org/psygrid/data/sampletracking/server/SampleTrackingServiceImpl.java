package org.psygrid.data.sampletracking.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.jfree.util.Log;
import org.psygrid.data.repository.dao.hibernate.EntityInterceptor;
import org.psygrid.data.sampletracking.ParticipantInfo;
import org.psygrid.data.sampletracking.ConfigInfo;
import org.psygrid.data.sampletracking.SampleInfo;
import org.psygrid.data.sampletracking.SampleTrackingService;
import org.psygrid.data.sampletracking.server.model.Participant;
import org.psygrid.data.sampletracking.server.model.Sample;
import org.psygrid.data.sampletracking.server.model.Action;
import org.psygrid.data.sampletracking.server.model.Config;
import org.psygrid.data.sampletracking.server.model.SampleRevision;
import org.psygrid.data.sampletracking.server.model.SampleTrackingDAO;
import org.psygrid.data.utils.email.MailClient;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Implementation of SampleTrackingService.
 * 
 * Converts info objects (used for data transfer) to and from model objects
 * and saves them using the SampleTrackingDAO.
 * 
 * @author terry
 *
 */
class SampleTrackingServiceImpl implements SampleTrackingService {

	private SampleTrackingDAO dao;
	
	// Event handling should be more abstracted - using OSGi
	private SampleTrackingEventHandler eventHandler;
	
	/**
	 * @param eventHandler the eventHandler to set
	 */
	public void setEventHandler(SampleTrackingEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

	/**
	 * @param sampleTrackingDAO the sampleTrackingDAO to set - injected
	 */
	public void setSampleTrackingDAO(SampleTrackingDAO sampleTrackingDAO) {
		this.dao = sampleTrackingDAO;
	}
	
	public ConfigInfo getSampleConfig(String projectCode) {
		ConfigInfo result = null;
		Config config = dao.getConfig(projectCode);
		if(config==null){
		    result = new ConfigInfo(projectCode,false,true,false,"","",true,"","",":",
		    		new String[]{"BLOOD","PLASMA","SERUM","TISSUE"},new String[]{"CLEAR","BLUE","GREEN","RED"},
		    		new String[]{"ALLOCATED","DESPATCHED","RECEIVED","INVALID"},144,72,12,false);
		    saveSampleConfig(result);
		}
		else {
			result = new ConfigInfo(config.getProjectCode(),config.getTracking(),config.isAutoParticipantID(),config.isUsingExternalID(),
				config.getParticipantRegex(),config.getParticipantRegexDescription(),config.isAutoSampleID(),config.getSampleRegex(),
				config.getSampleRegexDescription(),config.getSeparator(),config.getSampleTypes().toArray(new String[]{}),config.getTubeTypes().toArray(new String[]{}),config.getStatuses().toArray(new String[]{}),
				config.getLabelWidth(),config.getLabelHeight(),config.getLabelFontSize(),config.isPrintBarcodes());
		}
		return result;
	}

	public synchronized void saveSampleConfig(ConfigInfo conf) {
		Config config = dao.getConfig(conf.getProjectCode());
		if(config==null){
			config = new Config();
		}
		config.setAll(conf.getProjectCode(),conf.isTracking(),conf.isAutoParticipantID(),conf.isUsingExternalID(),
				conf.isAutoSampleID(),Arrays.asList(conf.getSampleTypes()),Arrays.asList(conf.getTubeTypes()),Arrays.asList(conf.getStatuses()),
				conf.getSeparator(),
				conf.getLabelWidth(),conf.getLabelHeight(),conf.getLabelFontSize(),conf.isPrintBarcodes(),
				conf.getParticipantRegex(),conf.getSampleRegex(),conf.getParticipantRegexDescription(),conf.getSampleRegexDescription());
		dao.saveConfig(config);
	}

	public ParticipantInfo getParticipant(String recordID) {
		ParticipantInfo result =  null;
		Participant p = dao.getParticipant(recordID);
		if(p!=null){
		 result = new ParticipantInfo(p.getRecordID(),p.getProjectCode(),p.getIdentifier());
		}
		return result;
	}

	public void saveParticipant(ParticipantInfo info) {
		Participant p = dao.getParticipant(info.getRecordID());
		if(p==null){
			p = new Participant(info.getProjectCode(),info.getRecordID(),info.getIdentifier());
		}
		else {
			// Only the identifier can be updated
			p.setIdentifier(info.getIdentifier());
		}
		dao.saveParticipant(p);
	}
	
	
	public SampleInfo[] getSampleRevisions(long sampleID) {
		Sample sample = dao.getSample(sampleID);
		SampleInfo[] result = new SampleInfo[]{};
		if(sample!=null){
			List<SampleInfo> temp = new ArrayList<SampleInfo>(sample.getRevisions().size());
			for(SampleRevision r:sample.getRevisions()){
				SampleInfo info = new SampleInfo(sample.getId(),sample.getParticipant().getRecordID(),
												r.getUser(),r.getTimestamp(),r.getParticipantIdentifier(),r.getIdentifier(),r.getStatus(),
												r.getSampleType(),r.getTubeType(),r.getTrackingID(),r.getSampleDate(),
												r.getComment());
				temp.add(info);
			}
			result=temp.toArray(result);
		}
		return result;
	}

	public SampleInfo[] getSamples(String recordID) {
		List<SampleInfo> temp = new ArrayList<SampleInfo>();
		Participant p = dao.getParticipant(recordID);
		if(p!=null){
			List<Sample> samples = p.getSamples();
			for(Sample s:samples){
				// return the last revision for each sample
				SampleRevision r =  s.getRevisions().get(s.getRevisions().size()-1);
				SampleInfo info = new SampleInfo(s.getId(),s.getParticipant().getRecordID(),
											r.getUser(),r.getTimestamp(),r.getParticipantIdentifier(),r.getIdentifier(),r.getStatus(),
											r.getSampleType(),r.getTubeType(),r.getTrackingID(),r.getSampleDate(),
											r.getComment());
				temp.add(info);
			}
		}
		return temp.toArray(new SampleInfo[]{});
	}

	public SampleInfo saveSample(SampleInfo info) {
		Sample s = null;
		if(info.getID()!=null){
			s = dao.getSample(info.getID());
		}
		if(s==null){
			Participant p = dao.getParticipant(info.getRecordID());
			s = new Sample(p);
			p.getSamples().add(s);
		}
		String user = info.getUser();
		Date timestamp = new Date();
		// Create a new revision object each time a sample is saved
		SampleRevision r = new SampleRevision(user,timestamp,info.getParticipantID(),info.getSampleID(),
										info.getStatus(),info.getSampleType(),info.getTubeType(),info.getTrackingID(),info.getSampleDate(),
										info.getComment());
		s.getRevisions().add(r);
		dao.saveSample(s);
		SampleInfo result = new SampleInfo(s.getId(),s.getParticipant().getRecordID(),
				r.getUser(),r.getTimestamp(),s.getParticipant().getIdentifier(),r.getIdentifier(),r.getStatus(),
				r.getSampleType(),r.getTubeType(),r.getTrackingID(),r.getSampleDate(),
				r.getComment());
		// Fire status change event
		int revisionCount = s.getRevisions().size();
		if(revisionCount==1 || !s.getRevisions().get(revisionCount-2).getStatus().equals(info.getStatus())){
			eventHandler.handleEvent(s.getParticipant().getProjectCode(),info);
		} 
		return result;
	}

	synchronized public long getNextSampleNumber(String projectCode) {
		Config config = dao.getConfig(projectCode);
		long result = config.getSampleCounter()+1;
		config.setSampleCounter(result);
		dao.saveConfig(config);
		return result;
	}
	
}




