package org.psygrid.imantra.patches.v1_7_3;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.common.email.Email;
import org.psygrid.data.model.hibernate.EslCustomField;
import org.psygrid.datasetdesigner.model.GroupModel;
import org.psygrid.datasetdesigner.model.RandomisationHolderModel;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.esl.model.ICustomField;
import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IRandomisation;
import org.psygrid.esl.model.IStrata;
import org.psygrid.esl.model.hibernate.HibernateFactory;
import org.psygrid.esl.randomise.EmailType;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.randomization.model.hibernate.Stratum;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

public class SaveIMantraESL {

	private static EslClient client = new EslClient();
	private static IFactory factory = new HibernateFactory();
	private static LoginServicePortType aa1 = null;
	private static StudyDataSet ds = null;
	/**
	 * The groups for the dataset
	 */
	private static  GroupType[] gta = null;
	
	private static String fullPathToiMantraStudy = null;
	
	/**
	 * Adds a new centre to the esl, and adds this to the randomization stratum, if the
	 * project randomizes by centre.
	 * @param args[0] - username delimiter (-u)
	 * @param args[1] - username
	 * @param args[2] - password delimiter (-w)
	 * @param args[3] - fullPathToiMantraStudy : remainingArgs[0]
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		Properties properties = PropertyUtilities.getProperties("test.properties");
		
		System.out.println(properties.getProperty("org.psygrid.security.authentication.client.trustStoreLocation"));
		
		client = new EslClient(new URL(properties.getProperty("org.psygrid.esl.client.serviceURL")));

		SAMLAssertion sa = null;
		IProject project = null;
		String saml = null;
        
        sa = login(args);
        saml = sa.toString();
		
		PersistenceManager.getInstance().setAliases();
		
		Object obj2 = PersistenceManager.getInstance().load(fullPathToiMantraStudy);
		ds = (StudyDataSet)obj2;
		
		//save basic proj info
		org.psygrid.esl.model.hibernate.HibernateFactory eslFactory = new org.psygrid.esl.model.hibernate.HibernateFactory();

		//basic project info
		project = eslFactory.createProject(ds.getDs().getProjectCode());
		project.setProjectName(ds.getDs().getName());
		project.setProjectCode(ds.getDs().getProjectCode());

		for (int i=0; i<ds.getDs().numGroups(); i++) {
			project.setGroup(eslFactory.createGroup(ds.getDs().getGroup(i).getLongName(), 
					ds.getDs().getGroup(i).getName().toString()));	
		}
		
		//custom fields
		for (int i=0, c=ds.getDs().getEslCustomFieldCount(); i<c; i++ ){
			EslCustomField field = ds.getDs().getEslCustomField(i);
			ICustomField eslField = eslFactory.createCustomField(field.getName());
			for ( int j=0, d=field.getValueCount(); j<d; j++ ){
				eslField.addValue(field.getValue(j));
			}
			project.addCustomField(eslField);
		}
		
		if (ds.getDs().isRandomizationRequired()) {
			//LOG.info("saving ESL randomisation found the project " + project.getProjectCode());
			//set up default emails - not customisable 
			Map<String,Email> emails = new HashMap<String,Email>();
			Email e1 = eslFactory.createEmail();
			e1.setSubject("Notification of Invocation");
			e1.setBody("Notification of Invocation");
			Email e2 = eslFactory.createEmail();
			e2.setSubject("Notification of Decision");
			e2.setBody("A treatment arm has been allocated to the subject '%subjectCode%'.");
			Email e3 = eslFactory.createEmail();
			e3.setSubject("Notification of Treatment");
			e3.setBody("The subject '%subjectCode%' has been allocated the treatment %treatment% (code: %treatmentCode%).\n\n" +
					"The subject has the following risk issues:\n\n" +
			"%riskIssues%");

			emails.put(EmailType.INVOCATION.type(), e1);
			emails.put(EmailType.DECISION.type(), e2);
			emails.put(EmailType.TREATMENT.type(), e3);
			
			//setUpRandomisation
			IRandomisation r = null;
			r = project.getRandomisation();

			if ( r == null) {
				r = eslFactory.createRandomisation(ds.getDs().getProjectCode());
			}

			RandomisationHolderModel rhm = ds.getRandomHolderModel();
			if (rhm != null) {
				ArrayList<Stratum> eslStrata = rhm.getRandomisationStrata();
				ArrayList<IStrata> iStrata = new ArrayList<IStrata>();
				//configure strata and add to randomisation
				for (int j=0; j<eslStrata.size(); j++) {
					IStrata s = eslFactory.createStrata(eslStrata.get(j).getName());
					configureEslStrataValues((org.psygrid.esl.model.hibernate.Strata)s);
					iStrata.add(s);
				}
				
				r.setStrata(iStrata);

				//configure treatments for randomisation
				for (int i=0; i<rhm.getRandomisationTreatments().size(); i++) {
					r.getTreatments().put(rhm.getRandomisationTreatments().get(i).getTreatmentCode(), 
							rhm.getRandomisationTreatments().get(i).getTreatmentName());
				}

				//configure email settings for randomisation
				r.setRolesToNotify(ds.getEslModel().getRoles());
				r.setEmails(emails);
				project.setRandomisation(r);
			}
		}	
		
		//save the project
		client.saveProject(project, saml);
	}
	
	public static SAMLAssertion login(String[] args) throws Exception {

		System.setProperty("axis.socketSecureFactory",
		"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		Options opts = new Options(args);
		String [] remainingArgs = opts.getRemainingArgs();
		fullPathToiMantraStudy = remainingArgs[0];

		
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
		
		//TODO:  Make sure that this works before running on the live system.
		SAMLAssertion sa = qc.getSAMLAssertion(new ProjectType("?", "IMAA", "?", "?", false));

		return sa;
	}
	
	private static void configureEslStrataValues(org.psygrid.esl.model.hibernate.Strata s) {
		if (s.getName().equals("sex")) {
			ArrayList<String> sexValues = new ArrayList<String>(); 
			sexValues.add("Male");
			sexValues.add("Female");
			s.setValues(sexValues);
		}
		else if (s.getName().equals("centreNumber")) {
			ArrayList<String> centreValues = new ArrayList<String>();
			for ( GroupType g: getGroups() ) {
				centreValues.add(g.getIdCode());
			}
			s.setValues(centreValues);
	
		}
		else{
			for ( EslCustomField field: ds.getEslCustomFields()){
				ArrayList<String> customFields = new ArrayList<String>();
				if ( s.getName().equals(field.getName()) ){
					for ( int i=0, c=field.getValueCount(); i<c; i++ ){
						customFields.add(field.getValue(i));
					}
				}
				s.setValues(customFields);
			}
		}
	}
	
	/*
	 * Get the groups for the dataset
	 * @return an array of <code>GroupType</code> containing the groups for the dataset
	 */
	private static GroupType[] getGroups() {
		ArrayList<GroupModel> groups = ds.getGroups();
		if (gta == null) {
			gta = new GroupType[groups.size()];
			for (int i=0; i<groups.size(); i++) {
				gta[i] = new GroupType(groups.get(i).getGroup().getLongName(), 
						groups.get(i).getGroup().getName(), 
						ds.getDs().getProjectCode());
			}
		}
		return gta;
	}

}
