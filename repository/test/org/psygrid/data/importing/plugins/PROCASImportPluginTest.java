/**
 * 
 */
package org.psygrid.data.importing.plugins;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.axis.encoding.Base64;
import org.psygrid.data.importing.ImportStatus;
import org.psygrid.data.importing.client.ImportClient;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.data.utils.wrappers.AAQCWrapper;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * @author Terry
 *
 */
public class PROCASImportPluginTest extends TestCase {

	String USERNAME = "TerryChild";
	String PASSWORD = "cherry";

//	String fileName = "etc/studies/procas/registration.csv";
//	String fileName = "etc/conf/procas/questionnaire.csv";
//	String fileName = "Z:/procas/v7/week1-short.csv";
//	String fileName = "Z:/procas/v7/Week 1 screening names.csv";
//	String fileName = "Z:/procas/v7/PROCAS Version 8.1.CSV";

	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGenerateDummyRegistration() throws FileNotFoundException {

		int MIN = 0;
		int MAX = 100;
		
		String[] CONSENT_COMMENTS = {"","NoToData","NoToSamples","NoToDataAndSamples","Other"};
		PrintStream out = new PrintStream(new FileOutputStream("c:/aaa/dummy_reg.csv"));
		out.println("SxNumber,NhsNumber,DateOfBirth,StudyEntryDate,DateOfFirstOfferedAppointment,DateOfMammogram,FullName,AddressOneLine,GpFullName,PracticeName,PracticeAddressLine1,PracticeAddressLine2,PracticeAddressLine3,PracticeAddressLine4,PracticePostcode,Location,Van no,Consented to DNA,To receive risk letter,Consent complete,ConsentComments,ConsentIssues");
		for(int i=MIN;i<MAX;i++){
			out.print(i+"00,");
			out.print(i+"99,");
			out.print("01/02/1950,");
			out.print("27/10/2009,");
			out.print("28/10/2009,");
			out.print("29/10/2009,");
			out.print("Name"+i+",");
			out.print("AddressOne"+i+",");
			out.print("GPName"+i+",");
			out.print("PracticeName"+i+",");
			out.print("PracticeAddressLineOne"+i+",");
			out.print("PracticeAddressLineTwo"+i+",");
			out.print("PracticeAddressLineThree"+i+",");
			out.print("PracticeAddressLineFour"+i+",");
			out.print("PracticePostCode"+i+",");
			out.print("WCH,");
			out.print(i+",");
			out.print(i%2==0?"Yes,":"No,");
			out.print(i%2==0?"No,":"Yes,");
			out.print("Yes,");
			out.print(CONSENT_COMMENTS[i%5]+",");
			out.print("Issue"+i);
			out.println();
		}
		out.close();
	}

	public void testGenerateDummyScreeningAppointment() throws FileNotFoundException{

		int MIN = 0;
		int MAX = 100;
		
		PrintStream out = new PrintStream(new FileOutputStream("c:/aaa/dummy_screening_appointment.csv"));
		out.println("NhsNumber,DateOfSecondOfferedAppointment,SecondVanNo,SecondLocation");
		for(int i=MIN;i<MAX;i++){
			out.print(i+"99,");
			out.print("01/02/1950,");
			out.print(i+",");
			out.print("WCH");
			out.println();
		}
		out.close();
	}

	public void testGenerateConsentedToDNA() throws FileNotFoundException{

		int MIN = 0;
		int MAX = 100;
		
		PrintStream out = new PrintStream(new FileOutputStream("c:/aaa/dummy_consented_to_dna.csv"));
		out.println("NhsNumber");
		for(int i=MIN;i<MAX;i++){
			out.print(i+"99");
			out.println();
		}
		out.close();
	}

	
	public void testMultiDummyRegistration() throws FileNotFoundException{

		int MIN = 0;
		int MAX = 1000;
		
		for(int file=0;file<100;file++){
			String[] CONSENT_COMMENTS = {"","NoToData","NoToSamples","NoToDataAndSamples","Other"};
			PrintStream out = new PrintStream(new FileOutputStream("c:/aaa/dummy_reg_"+file+".csv"));
			out.println("SxNumber,NhsNumber,DateOfBirth,StudyEntryDate,DateOfFirstOfferedAppointment,DateOfMammogram,FullName,AddressOneLine,GpFullName,PracticeName,PracticeAddressLine1,PracticeAddressLine2,PracticeAddressLine3,PracticeAddressLine4,PracticePostcode,Location,Van no,Consented to DNA,To receive risk letter,ConsentComments,ConsentIssues");
			for(int i=MIN;i<MAX;i++){
				int unique=file*1000+i;
				out.print(unique+",");
				out.print(unique+",");
				out.print("01/02/1950,");
				out.print("27/10/2009,");
				out.print("28/10/2009,");
				out.print("29/10/2009,");
				out.print("Name"+unique+",");
				out.print("AddressOne"+unique+",");
				out.print("GPName"+unique+",");
				out.print("PracticeName"+unique+",");
				out.print("PracticeAddressLineOne"+unique+",");
				out.print("PracticeAddressLineTwo"+unique+",");
				out.print("PracticeAddressLineThree"+unique+",");
				out.print("PracticeAddressLineFour"+unique+",");
				out.print("PracticePostCode"+unique+",");
				out.print("WCH,");
				out.print(unique+",");
				out.print(i%2==0?"Yes,":"No,");
				out.print(i%2==0?"No,":"Yes,");
				out.print(CONSENT_COMMENTS[i%5]+",");
				out.print("Issue"+unique);
				out.println();
			}
			out.close();
		}
	}
	
	public void testGenerateDummyVAS() throws FileNotFoundException{

		int MIN = 0;
		int MAX = 100;
		
		PrintStream out = new PrintStream(new FileOutputStream("c:/aaa/dummy_VAS.csv"));
		out.println("NHS Number,D.O.B.,RCC,RMLO,LCC,LMLO,Initials,Warnings");
		for(int i=MIN;i<MAX;i++){
			out.print(i+"99,");
			out.print("01/02/1950,");
			out.print(i+",");
			out.print((i+1)+",");
			out.print((i+2)+",");
			out.print((i+3)+",");
			out.print("XY"+i+",");
			out.print("Warning"+i);
			out.println();
			out.print(i+"99,");
			out.print("01/02/1950,");
			out.print((i+10)+",");
			out.print((i+11)+",");
			out.print((i+12)+",");
			out.print((i+13)+",");
			out.print("AB"+i+",");
			out.print("Warning2_"+i);
			out.println();
		}
		out.close();
	}


	public void testGenerateDummyCumulus() throws FileNotFoundException{

		int MIN = 5000;
		int MAX = 10000;
		
		PrintStream out = new PrintStream(new FileOutputStream("c:/aaa/dummy_cumulus.csv"));
		out.println("file_name,EdgeThreshold,DensityThreshold,BreastArea_raster,DenseArea_raster,Reader initials,comments");
		for(int i=MIN;i<MAX;i++){
			out.print(i+"99-lcc.bmp,");
			out.print(i+",");
			out.print((i+1)+",");
			out.print((i+2)+",");
			out.print((i+3)+",");
			out.print("AB"+i+",");
			out.print("Commentlcc"+i);
			out.println();
			out.print(i+"99-rcc.bmp,");
			out.print(i+",");
			out.print((i+1)+",");
			out.print((i+2)+",");
			out.print((i+3)+",");
			out.print("XY"+i+",");
			out.print("Commentrcc"+i);
			out.println();
		}
		out.close();
	}

	public void testGenerateDummyQuantra() throws FileNotFoundException{

		int MIN = 0;
		int MAX = 100;
		
		PrintStream out = new PrintStream(new FileOutputStream("c:/aaa/dummy_quantra.csv"));
		out.println("PatientName,PatientId,StudyDate,Quantra Date,Right Glandular Volume,Right Breast Volume,Right Breast Density,Right Confidence,Left Glandular Volume,Left Breast Volume,Left Breast Density,Left Confidence,Comments");
		for(int i=MIN;i<MAX;i++){
			out.print("Name"+i+",");
			out.print(i+"99,");
			out.print("20101025,");
			out.print("20101125,");
			out.print((i+1)+",");
			out.print((i+2)+",");
			out.print((i+3)+",");
			out.print((i+4)+",");
			out.print((i+5)+",");
			out.print((i+6)+",");
			out.print((i+7)+",");
			out.print((i+8)+",");
			out.print("Comments"+(i));
			out.println();
		}
		out.close();
	}

	public void testGenerateDummyStepWedge() throws FileNotFoundException{

		int MIN = 0;
		int MAX = 100;
		
		PrintStream out = new PrintStream(new FileOutputStream("c:/aaa/dummy_stepwedge.csv"));
		out.println("Name,Confidence level,Volume breast,Volume gland,Volume gland (%),Comment");
		for(int i=MIN;i<MAX;i++){
			out.print(i+"99_lcc,");
			out.print(i%3+",");
			out.print((i+1)+",");
			out.print((i+2)+",");
			out.print((i+3)+",");
			out.print("Commentlcc"+i);
			out.println();
			out.print(i+"99_rcc,");
			out.print(i%3+",");
			out.print((i+4)+",");
			out.print((i+5)+",");
			out.print((i+6)+",");
			out.print("Commentrcc"+i);
			out.println();
			out.print(i+"99_lmlo,");
			out.print(i%3+",");
			out.print((i+7)+",");
			out.print((i+8)+",");
			out.print((i+9)+",");
			out.print("Commentlmlo"+i);
			out.println();
			out.print(i+"99_rmlo,");
			out.print(i%3+",");
			out.print((i+10)+",");
			out.print((i+11)+",");
			out.print((i+12)+",");
			out.print("Commentrmlo"+i);
			out.println();
		}
		out.close();
	}

	public void testGenerateDummyVolpara() throws FileNotFoundException{

		int MIN = 0;
		int MAX = 100;
		
		PrintStream out = new PrintStream(new FileOutputStream("c:/aaa/dummy_volpara.csv"));
		out.println("VolparaVersion,PatientID,BreastSide,MammoView,BreastVolumeCm3,HintVolumeCm3,VolumetricBreastDensity");
		for(int i=MIN;i<MAX;i++){
			out.print("1.3.1 | 987 |,");
			out.print(i+"99,");
			out.print("Left,");
			out.print("CC,");
			out.print((i+1)+",");
			out.print((i+2)+",");
			out.print((i+3));
			out.println();
			out.print("1.3.1 | 987 |,");
			out.print(i+"99,");
			out.print("Right,");
			out.print("CC,");
			out.print((i+4)+",");
			out.print((i+5)+",");
			out.print((i+6));
			out.println();
			out.print("1.3.1 | 987 |,");
			out.print(i+"99,");
			out.print("Left,");
			out.print("MLO,");
			out.print((i+7)+",");
			out.print((i+8)+",");
			out.print((i+9));
			out.println();
			out.print("1.3.1 | 987 |,");
			out.print(i+"99,");
			out.print("Right,");
			out.print("MLO,");
			out.print((i+10)+",");
			out.print((i+11)+",");
			out.print((i+12));
			out.println();
		}
		out.close();
	}

	public void testGenerateDummyDNAResults() throws FileNotFoundException {

		int MIN = 0;
		int MAX = 100;
		
		PrintStream out = new PrintStream(new FileOutputStream("c:/aaa/dummy_dna_results.csv"));
		out.println("96 well,384,NHS Number,Patient no Customer ID,rs614367,rs614367 score,rs704010,rs704010 score,rs713588,rs713588 score,rs889312,rs889312 score,rs909116,rs909116 score,rs1011970,rs1011970 score,rs1562430,rs1562430 score,rs2981579,rs2981579 score,rs3757318,rs3757318 score,rs3803662,rs3803662 score,rs4973768,rs4973768 score,rs1156287,rs1156287 score,rs8009944,rs8009944 score,rs9790879,rs9790879 score,rs10941679,rs10941679 score,rs10995190,rs10995190 score,rs11249433,rs11249433 score,rs13387042,rs13387042 score,rs10931936,rs10931936 score,SumScore");
		for(int i=MIN;i<MAX;i++){
			for(int j=1;j<=43;j++){
				if(j==3){
					out.print(i+"99,");
				}
				else {
					out.print("DNA-"+i+"-"+j+(j<43?",":""));				
				}
			}
			out.println();
		}
		out.close();
	}
	
	
	static void threadMessage(String message) {
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, message);
    }
	

	/**
	 * Run multi threaded import
	 * 
	 * To run this:
	 * 
	 * 1. Publish the PROCAS study locally
	 * 2. Run the test method testMultiDummyRegistration() to generate some test data
	 * 3. Run this main() method
	 * 
	 */
	public static void main(String[] args) throws Exception {

		for(int i=0;i<10;i++){
			final int num = i;
			Thread t = new Thread(){
				public void run()  {
					try {
						String projectName = "PROCAS_8";
						String projectCode = "PRC_8";
						String username = "TerryChild";
						String userDN = "CN=Terry Child, OU=users, O=openCDMS Virtualized, C=UK";
						String password = "cherry";
						
						String fileName = "c:/aaa/dummy_reg_"+num+".csv";
						int importTypeIndex =0;
						
				        threadMessage("Login in...");
						AAQCWrapper aaqcWrapper = getAAQCWrapper(username,password,projectName,projectCode);
						
				        threadMessage("Getting SAML...");
				        String saml = aaqcWrapper.getSAMLAssertion(null,new ProjectType(projectName, projectCode, null, null, false));
	
				        RepositoryClient repClient = new RepositoryClient();
					    
						DataSet dataset = repClient.getDataSetSummary(projectCode, new Date(0), saml);
						dataset = repClient.getDataSet(dataset.getId(), saml);		
						
						long start=System.currentTimeMillis();
						ImportPlugin plugin = null;//ImportPluginFactory.getImportPlugin("PRC");
						String[] docTypes=plugin.getImportTypes();
						threadMessage("About to run plugin");
						
						plugin.run(projectCode,docTypes[importTypeIndex],fileName,userDN,aaqcWrapper,System.out);
	
						long end=System.currentTimeMillis();
						threadMessage("time="+(end-start)+"ms");
					} catch (Exception e) {
						e.printStackTrace();
				        threadMessage(e.getMessage());
					}
			    }				
			};
			t.start();
			Thread.sleep(10000);
		}
		
	}

	
	
	public void testImport() throws Exception {
		
		
		String projectName = "Procas";
		String projectCode = "PRC";
		String username = "TerryChild";
		String userDN = "CN=Terry Child, OU=users, O=openCDMS Virtualized, C=UK";
		String password = "cherry";
//		String fileName = "c:/aaa/VAS 61.csv";
		
//		String fileName = "c:/aaa/dummy_reg.csv";
//		int importTypeIndex =0;

//		String fileName = "c:/aaa/dummy_questionnaire.csv";
//		int importTypeIndex = 1;
		
//		String fileName = "c:/aaa/dummy_cumulus.csv";
//		int importTypeIndex =3;

//		String fileName = "c:/aaa/dummy_quantra.csv";
//		int importTypeIndex =4;

//		String fileName = "c:/aaa/dummy_stepwedge.csv";
//		int importTypeIndex =5;

		String fileName = "c:/aaa/dummy_volpara.csv";
		int importTypeIndex =6;

		//		String fileName = "C:/myprojects/opencdms/studies/PROCAS/specification/tyrer-cuzick-feedback-4/test2reg.csv";
		
		AAQCWrapper aaqcWrapper = getAAQCWrapper(username,password,projectName,projectCode);
		
        String saml = aaqcWrapper.getSAMLAssertion(null,new ProjectType(projectName, projectCode, null, null, false));

        RepositoryClient repClient = new RepositoryClient();
	    
		DataSet dataset = repClient.getDataSetSummary(projectCode, new Date(0), saml);
		dataset = repClient.getDataSet(dataset.getId(), saml);		
		
		long start=System.currentTimeMillis();
		ImportPlugin plugin = null;//ImportPluginFactory.getImportPlugin("PRC");
		String[] docTypes=plugin.getImportTypes();
		System.out.println("About to run plugin");
		
		plugin.run("PRC",docTypes[importTypeIndex],fileName,userDN,aaqcWrapper,System.out);

		long end=System.currentTimeMillis();
		System.out.println("time="+(end-start)+"ms");
	}

	public void testImportStatuses() throws Exception {
		String projectName = "Procas";
		String projectCode = "PRC";

//		String username = "TerryChild";
//		String password = "cherry";
		String username = "TestOne";
		String password = "cherry";
		
		AAQCWrapper aaqcWrapper = getAAQCWrapper(username,password,projectName,projectCode);		
        String saml = aaqcWrapper.getSAMLAssertion(null,new ProjectType(projectName, projectCode, null, null, false));

        ImportClient repClient = new ImportClient();

		ImportStatus[] statuses = repClient.getImportStatuses(projectCode, saml);
		System.out.println(statuses);
	    
	}
	
	public  String convertFileToString(String file) throws IOException {
		FileInputStream is = new FileInputStream(file);
	    Writer writer = new StringWriter();		
	    char[] buffer = new char[1024];
	    try {
	        Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        int n;
	        while ((n = reader.read(buffer)) != -1) {
	            writer.write(buffer, 0, n);
	        }
	    } finally {
	        is.close();
	    }
	    return writer.toString();
	}
	
	private static AAQCWrapper getAAQCWrapper(String username,String password,String projectName,String projectCode) throws Exception {
			copyDefaultJKS();
			System.setProperty("axis.socketSecureFactory",
	        "org.psygrid.security.components.net.PsyGridClientSocketFactory");
	        Properties properties = PropertyUtilities.getProperties("test.properties");
	        System.out.println(properties.getProperty("org.psygrid.security.authentication.client.trustStoreLocation"));
	        LoginClient tc = null;
	        LoginServicePortType aa1 = null;

	        try {
	            tc = new LoginClient("test.properties");
	            aa1 = tc.getPort();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        char[] pwdchars = password.toCharArray();
	        short[] pwd = new short[pwdchars.length];
	        for (int i = 0; i < pwd.length; i++) {
	            pwd[i] = (short) pwdchars[i];
	        }
	        String credential = tc.getPort().login(username, pwd);
	        if (credential != null) {
	            byte[] ks = Base64.decode(credential);
	            FileOutputStream fos = new FileOutputStream(properties
	                    .getProperty("org.psygrid.security.authentication.client.keyStoreLocation"));
	            fos.write(ks);
	            fos.flush(); 
	            fos.close();
	        }
	        System.setProperty("javax.net.ssl.keyStorePassword", new String(pwdchars));
	        PsyGridClientSocketFactory.reinit();
	        
	        AAQCWrapper aaqcWrapper = new AAQCWrapper();	        
	        aaqcWrapper.setProperties("test.properties");
	        return aaqcWrapper;
	}

	private static void copyDefaultJKS(){
		try {
	        // Create channel on the source
	        FileChannel srcChannel = new FileInputStream("Z:\\certs\\localhost\\default\\default.jks").getChannel();
	    
	        // Create channel on the destination
	        FileChannel dstChannel = new FileOutputStream("Z:\\certs\\localhost\\default.jks").getChannel();
	    
	        // Copy file contents from source to destination
	        dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
	    
	        // Close the channels
	        srcChannel.close();
	        dstChannel.close();
	    } catch (IOException e) {
	    }
	}

	
	public void testProfiler(){
		long start=System.currentTimeMillis();
		String s="";
		for(int i=0;i<20000;i++){
			s=s+i;
		}
		long end=System.currentTimeMillis();
		System.out.println("s.length()="+s.length()+" time="+(end-start)+"ms");
	}

	public void testProfiler2(){
		long start=System.currentTimeMillis();
		StringBuilder s=new StringBuilder();
		for(int i=0;i<20000;i++){
			s.append(i);
		}
		long end=System.currentTimeMillis();
		System.out.println("s.length()="+s.length()+" time="+(end-start)+"ms");
	}

	/*
	public static void main(String[] args){
		long start=System.currentTimeMillis();
		String s="";
		for(int i=0;i<20000;i++){
			s=s+i;
		}
		long end=System.currentTimeMillis();
		System.out.println("s.length()="+s.length()+" time="+(end-start)+"ms");
	}
	*/

}
