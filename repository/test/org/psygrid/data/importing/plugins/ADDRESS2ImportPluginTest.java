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
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.data.utils.wrappers.AAQCWrapper;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * Some tests for ADDRESS2 imports
 * 
 * @author Terry Child
 *
 */
public class ADDRESS2ImportPluginTest extends TestCase {

	String USERNAME = "TerryChild";
	String PASSWORD = "cherry";
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGenerateDummyBloodAnalysisGADAndIA2Data() throws FileNotFoundException{

		int MIN = 0;
		int MAX = 20;
		
		PrintStream out = new PrintStream(new FileOutputStream("c:/aaa/dummy_gad.csv"));
		out.println("Shipment ID,Sample ID,UAS,Date rec,GAD result,IA2 result,Date Report");
		for(int i=MIN;i<MAX;i++){
			out.print(i+"00,");
			out.print("123456,");
			out.print(i%2+",");
			out.print("01/02/1950,");
			out.print(i+1000+",");
			out.print(i+2000+",");
			out.print("29/10/2009");
			out.println();
		}
		out.close();
	}

	public void testGenerateDummyBloodAnalysisZnt8Data() throws FileNotFoundException{

		int MIN = 0;
		int MAX = 100;
		
		PrintStream out = new PrintStream(new FileOutputStream("c:/aaa/dummy_znt8.csv"));
		out.println("Shipment ID,Sample ID,UAS,Date rec,CIAA result,ZnT8 result,Date Report");
		for(int i=MIN;i<MAX;i++){
			out.print(i+"00,");
			out.print("123456,");
			out.print(i%2+",");
			out.print("01/02/1950,");
			out.print(i+1000+",");
			out.print(i+2000+",");
			out.print("29/10/2009");
			out.println();
		}
		out.close();
	}

	public void testImport() throws Exception {
		
		
		String projectName = "ADDRESS-2";
		String projectCode = "ADD2";
		String username = "TerryChild";
		String userDN = "CN=Terry Child, OU=users, O=openCDMS Virtualized, C=UK";
		String password = "cherry";
		
//		String fileName = "c:/aaa/example_report.csv";
//		String fileName = "c:/aaa/dummy_gad.csv";
		String fileName = "c:/aaa/dummy_znt8.csv";
		
		int importTypeIndex = 2;
		
		AAQCWrapper aaqcWrapper = getAAQCWrapper(username,password,projectName,projectCode);
		
		String saml = aaqcWrapper.getSAMLAssertion(userDN);
		
		RepositoryClient repClient = new RepositoryClient();
		DataSet d = repClient.getDataSet(Long.valueOf(16177), saml);


			    		
		long start=System.currentTimeMillis();
		ImportPlugin plugin = null; //ImportPluginFactory.getImportPlugin("ADD2");
		String[] docTypes=plugin.getImportTypes();
		System.out.println("About to run plugin");
		
		plugin.run("ADD2",docTypes[importTypeIndex],fileName,userDN,aaqcWrapper,System.out);

		long end=System.currentTimeMillis();
		System.out.println("time="+(end-start)+"ms");
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
	        
	        String credential = null;
//			long start = System.currentTimeMillis();
//			int ITERS = 100;
//			for(int i=0;i<ITERS;i++){
	        	credential = tc.getPort().login(username, pwd);
//	        }
//			long end = System.currentTimeMillis();
//			System.out.println("Total logins time = "+(end-start)+"ms");
//			System.exit(0);
			
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

}
