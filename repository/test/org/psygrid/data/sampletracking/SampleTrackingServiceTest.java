package org.psygrid.data.sampletracking;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.sampletracking.ConfigInfo;
import org.psygrid.data.sampletracking.SampleInfo;
import org.psygrid.data.sampletracking.SampleTrackingService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SampleTrackingServiceTest extends TestCase {

	private static Log logger = LogFactory.getLog(SampleTrackingServiceTest.class);

	private final static String SERVICE_NAME = "sampleTrackingService";
	private static String applicationContext = "org/psygrid/data/sampletracking/sampleTrackingTestContext.xml";
	
	public void testSampleConfig() {
	    
		// Can use xml contexts to change only the last one
        String[] paths = {applicationContext};
		ApplicationContext ctx = new ClassPathXmlApplicationContext(paths);
	    SampleTrackingService service = (SampleTrackingService) ctx.getBean(SERVICE_NAME);
	    
	    ConfigInfo defaultConfig = service.getSampleConfig("TST0");
	    assertNotNull("default should not be null",defaultConfig);
	    
	    ConfigInfo conf = new ConfigInfo("TST",false,false,false,"","my participant regex description",true,"","",":",
	    		new String[]{"BLOOD","PLASMA","SERUM"},new String[]{"CLEAR","BLUE","RED"},new String[]{"ALLOCATED","DESPATCHED","RECEIVED"},
	    		144,72,14,false);
	    service.saveSampleConfig(conf);
	    ConfigInfo loaded = service.getSampleConfig("TST");
	    assertFalse("isTracking differs", loaded.isTracking());
	    assertFalse("autoPatientID differs", loaded.isAutoParticipantID());
	    assertFalse("isUsingExternalID differs", loaded.isUsingExternalID());
	    assertFalse("printBarcodes differs", loaded.isPrintBarcodes());
	    assertEquals(144,loaded.getLabelWidth());
	    assertEquals(72,loaded.getLabelHeight());
	    assertEquals(14, loaded.getLabelFontSize());
	    loaded.setTracking(true);
	    loaded.setAutoParticipantID(true);
	    loaded.setUsingExternalID(true);
	    service.saveSampleConfig(loaded);
		ConfigInfo reloaded = service.getSampleConfig("TST");
	    assertTrue("isTracking differs", reloaded.isTracking());
	    assertTrue("autoPatientID differs", reloaded.isAutoParticipantID());
	    assertTrue("isUsingExternalID differs", reloaded.isUsingExternalID());
	    assertEquals("Status invalid","DESPATCHED",reloaded.getStatuses()[1]);
	    assertEquals("Tube type invalid","RED",reloaded.getTubeTypes()[2]);
	    assertEquals("Separator mismatch",":",reloaded.getSeparator());
	    assertFalse("printBarcodes differs", reloaded.isPrintBarcodes());
	    assertEquals(144,reloaded.getLabelWidth());
	    assertEquals(72,reloaded.getLabelHeight());
	    assertEquals(14, reloaded.getLabelFontSize());
	    assertEquals("my participant regex description", reloaded.getParticipantRegexDescription());
	    
	    for(long i=1;i<=10;i++){
	    	long sampleNumber = service.getNextSampleNumber("TST");
	    	assertEquals("next sample number wrong",i, sampleNumber);
	    }
	}

	public void testSaveSample() {
	    
        String[] paths = {applicationContext};
		ApplicationContext ctx = new ClassPathXmlApplicationContext(paths);
	    SampleTrackingService service = (SampleTrackingService) ctx.getBean(SERVICE_NAME);
	    
	    final String testIdentifier = "TST/001001-2";
	    
	    ParticipantInfo p = new ParticipantInfo(testIdentifier,"TST","myparticipantid");
	    service.saveParticipant(p);
	    
	    // Save a sample and test the return values of the save
	    Calendar cal = new GregorianCalendar(2011, 1, 12, 9, 0 , 0);
	    Date date = cal.getTime();
	    SampleInfo info = new SampleInfo(testIdentifier,"myparticipantid","mysampleid","ALLOCATED","BLOOD","CLEAR","MYID123",date,"mycomment");
	    SampleInfo result = service.saveSample(info);
	    assertNotNull("timestamp should have been set by server",result.getTimestamp());
	    assertNotNull("sampleID should have been set by server",result.getSampleID());
	    assertEquals("participant ids differ",info.getParticipantID(),result.getParticipantID());
	    assertEquals("sample ids differ",info.getSampleID(),result.getSampleID());
	    assertEquals("tubes differ",info.getTubeType(),result.getTubeType());
	    assertEquals("status differs",info.getStatus(),result.getStatus());
	    assertEquals("recordID differs",info.getRecordID(),result.getRecordID());
	    assertEquals("tracking ID differs",info.getTrackingID(),result.getTrackingID());
	    assertEquals("date differs",info.getSampleDate(),result.getSampleDate());
	    assertEquals("comment differs",info.getComment(),result.getComment());

	    // Save the sample sample with different values and test the return values of the save
	    info = result;
	    info.setSampleID("mysampleid2");
	    info.setStatus("DESPATCHED");
	    info.setTubeType("BLUE");
	    info.setComment("mycomment2");
	    cal.set(2010, 11, 1, 19, 0, 0);
	    Date date2 = cal.getTime();
	    info.setSampleDate(date2);
	    result = service.saveSample(info);
	    assertNotNull("timestamp should have been set by server",result.getTimestamp());
	    assertNotNull("sampleID should have been set by server",result.getSampleID());
	    assertEquals("labels differ",info.getSampleID(),result.getSampleID());
	    assertEquals("sample types differ",info.getSampleType(),result.getSampleType());
	    assertEquals("tubes differ",info.getTubeType(),result.getTubeType());
	    assertEquals("status differs",info.getStatus(),result.getStatus());
	    
	    // Try to get a non-existent sample
	    SampleInfo[] samples = service.getSamples("TST/001001-999");
	    assertEquals("Should be no samples for record TST/001001-999",0,samples.length);
	    
	    // Get the saved samples - there should be one
	    samples = service.getSamples(testIdentifier);
	    assertEquals("Should be one sample for record",1,samples.length);
	    assertEquals("Sample identifier","mysampleid2", samples[0].getSampleID());
	    assertEquals("Patient identifier","myparticipantid", samples[0].getParticipantID());
	    assertEquals("Status wrong","DESPATCHED", samples[0].getStatus());
	    assertEquals("comment differs","mycomment2",samples[0].getComment());
	    
	    // NB: We cannot compare the dates directly as hibernate returns java.sql.Timestamp
	    // see - https://forum.hibernate.org/viewtopic.php?t=925275&start=15
	    assertEquals("Wrong date",date2.getTime(),samples[0].getSampleDate().getTime());
	    
	    // Get the revisions of the sample - there should be two
	    SampleInfo[] revisions = service.getSampleRevisions(result.getID());
	    assertEquals("wrong number of revisions",2, revisions.length);
	    assertEquals("revisions status wrong","ALLOCATED",revisions[0].getStatus());	    
	    assertEquals("revisions tube wrong","BLUE",revisions[1].getTubeType());
	    assertEquals("revisions label wrong","mysampleid2",revisions[1].getSampleID());
	    assertEquals("Wrong date",date.getTime(),revisions[0].getSampleDate().getTime());
	    assertEquals("Wrong date",date2.getTime(),revisions[1].getSampleDate().getTime());
	    assertEquals("comment differs","mycomment",revisions[0].getComment());
	    assertEquals("comment differs","mycomment2",revisions[1].getComment());

	    revisions = service.getSampleRevisions(9999);
	    assertEquals("Should be no samples for record 9999",0,revisions.length);
	    	    
	}

	public void testSpeedSaveSample() {
	    
		logger.info("Hello world");
		
        String[] paths = {applicationContext};
		ApplicationContext ctx = new ClassPathXmlApplicationContext(paths);
	    SampleTrackingService service = (SampleTrackingService) ctx.getBean(SERVICE_NAME);
	    
	    int NUM = 5000;
		long start=System.currentTimeMillis();

	    Calendar cal = Calendar.getInstance();
	    cal.set(2011, 1, 12, 9, 0);
	    Date date = cal.getTime();

		for(int i=0;i<NUM;i++){
		    String testIdentifier = "TST/001001-"+i;
			ParticipantInfo p = new ParticipantInfo(testIdentifier,"TST","myparticipantid");
		    service.saveParticipant(p);	    		    		    
		    SampleInfo info = new SampleInfo(testIdentifier,"myparticipantid","mysampleid","ALLOCATED","BLOOD","CLEAR","MYID123",date,"");
		    service.saveSample(info);
		    if(i%100==0) System.out.println(i);
		}
		long end=System.currentTimeMillis();
		
		System.out.println("time="+(end-start)+"ms");
		System.out.println(NUM/((end-start)/1000)+" inserts/sec");
	    	    
	}


}





