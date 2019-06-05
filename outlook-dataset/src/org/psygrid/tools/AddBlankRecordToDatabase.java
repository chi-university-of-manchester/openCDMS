package org.psygrid.tools;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;

public class AddBlankRecordToDatabase {
	
	/**
	 * logger
	 */
	private static LoginServicePortType aa1 = null;


	/**
	 * This tool has been created to remedy the situation when a new participant gets generated for the study via Collect,
	 * but a loss of connectivity prevents the record from being saved, even though the participant's personal details have
	 * already been saved in the Participant Register database. After running this programme, a blank record will have been created
	 * for the participant, which allows Collect to continue to be used with the allocated identifier.
	 * 
	 * After this programme is run, the user who created the participant in the first place must either
	 * 1) delete their .psygrid folder or
	 * 2) modify the correct identifiers.xml so that the participant identifier's <used> tag is set to 'true'
	 * 
	 * Either one of these actions will prevent the identifier from being offered again when the user goes to create a new participant.
	 * 
	 * Obviously, the option 1 is easier to give instructions for, but option 2 is the best choice if it is feasible.
	 * 
	 * @param args
	 * args[0] username delimiter, -u | REQUIRED
	 * args[1] username | REQUIRED
	 * args[2] password delimiter, -w | REQUIRED
	 * args[3] password | REQUIRED
	 * args[4] project code, e.g. 'OLK' (referred to as remaining[0] below) | REQUIRED
	 * args[5] hibernate id of the identifier to associate with the new record (referred to as remaining[1] below) | REQUIRED
	 * args[6] the name of the centre that this record belongs to (referred to as remaining[2] below) | REQUIRED
	 * args[7] the index of the site id within the centre array that this record belongs to (referred to as remaining[3] below) | REQUIRED
	 * args[8] the string version of the participant identifier that this record is to be associated with (referred to as remaining[4] below) | REQUIRED
	 * args[9] the index of the consultant with respect to the site that this participant is associated with (referred to as remaining[5] below) | OPTIONAL
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		System.setProperty("axis.socketSecureFactory",
		"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		Options opts = new Options(args);
		String[] remaining = opts.getRemainingArgs();
		Properties properties = PropertyUtilities.getProperties("test.properties");
		System.out.println(properties.getProperty("org.psygrid.security.authentication.client.trustStoreLocation"));
		LoginClient tc = null;
		
		try {
			tc = new LoginClient("test.properties");
			aa1 = tc.getPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		char[] password = opts.getPassword().toCharArray();
		short[] pwd = new short[password.length];
		for (int i = 0; i < pwd.length; i++) {
			pwd[i] = (short) password[i];
		}
		String credential = tc.getPort().login(opts.getUser(), pwd);
		if (credential != null) {
			byte[] ks = Base64.decode(credential);
			FileOutputStream fos = new FileOutputStream(properties
					.getProperty("org.psygrid.security.authentication.client.keyStoreLocation"));
			fos.write(ks);
			fos.flush(); 
			fos.close();
		}
		System.out.println("loggedin");
		System.setProperty("javax.net.ssl.keyStorePassword", new String(password));
		PsyGridClientSocketFactory.reinit();
		AAQueryClient qc = new AAQueryClient("test.properties");
		System.out.println("getAssertion");
		SAMLAssertion sa = qc.getSAMLAssertion();

		String repositoryServiceURLStr = properties.getProperty("org.psygrid.data.client.serviceURL");
		URL repositoryServiceURL = new URL(repositoryServiceURLStr);
		RepositoryClient rc = new RepositoryClient(repositoryServiceURL);
		
		DataSet dsSummary = rc.getDataSetSummary(remaining[0], new Date(0), sa.toString());
        DataSet ds = rc.getDataSet(dsSummary.getId(), sa.toString());
        
        Record record = ds.generateInstance();
        Identifier id = new Identifier();
        id.setId(new Long(remaining[1]));
        id.setProjectPrefix(remaining[0]);
        id.setGroupPrefix(remaining[2]);
        id.setIdentifier(remaining[4]);
        record.setIdentifier(id);
        
        Group gp = getGroupById(ds, remaining[2]);
        Integer siteIndex = new Integer(remaining[3]);
        Site site = gp.getSite(siteIndex);
        
        if(remaining.length>5 && remaining[5] != null){
        	Integer consultantIndex = new Integer(remaining[5]);
        	String consultantName = site.getConsultants().get(consultantIndex.intValue());
        	record.setConsultant(consultantName);
        }
        
        record.setSite(site);
        
        RecordData rd = record.generateRecordData();
        record.setRecordData(rd, null);
        record.attach(ds);
        
        rc.saveRecord(record, true, sa.toString());
        
	}
	
	private static Group getGroupById(DataSet ds, String groupId){
		int numberOfGroups = ds.numGroups();
		
		for(int i = 0; i < numberOfGroups; i++){
			Group g = (Group)ds.getGroup(i);
			if(g.getName().equals(groupId)){
				return g;
			}
			
		}
		return null;
	}

}
