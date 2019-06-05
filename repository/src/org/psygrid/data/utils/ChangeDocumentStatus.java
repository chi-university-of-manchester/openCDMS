package org.psygrid.data.utils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.RepositoryNoSuchDatasetFault;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;

/**
 * 
 * @author Bill
 *
 */
public class ChangeDocumentStatus {
	
	public enum DocumentStatus {
	    NOT_STARTED,
		INCOMPLETE,
		PENDING,
		REJECTED,
		APPROVED,
		LOCALLY_INCOMPLETE,
		READY_TO_SUBMIT,
		VIEW_ONLY,
		DATASET_DESIGNER,
		COMPLETE,
		CONTROLLED,
		COMMIT_FAILED;

		/**
		 * Returns a set containing the statuses for remote documents. In other
		 * words, all statuses apart from the ones returned by {@link #getInternal()}
		 * and {@link #getLocal()}.
		 */
		public static Set<DocumentStatus> getRemote() {
		    EnumSet<DocumentStatus> toExclude = getLocal();
		    toExclude.addAll(getInternal());
		    return EnumSet.complementOf(toExclude);
		}
		
		/**
		 * Returns a set containing the statuses for local documents, documents
		 * that have not yet been submitted to the repository. 
		 */
		public static EnumSet<DocumentStatus> getLocal() {
		    return EnumSet.of(NOT_STARTED, LOCALLY_INCOMPLETE, READY_TO_SUBMIT);
		}
		
		/**
		 * Returns a set containing the statuses that are not user visible.
		 */
		public static EnumSet<DocumentStatus> getInternal(){
		    return EnumSet.of(DATASET_DESIGNER, VIEW_ONLY);
		}
		
		/**
	     * Returns a set containg the user visible statuses. In other words, all
	     * statuses apart from the ones returned from {@link #getInternal()}.
	     */
		public static EnumSet<DocumentStatus> getUserVisible(boolean isNoReviewAndApprove, boolean isAlwaysOnline) {
			if ( isNoReviewAndApprove ){
				if ( isAlwaysOnline ){
					return EnumSet.of(NOT_STARTED, INCOMPLETE, COMPLETE, CONTROLLED);
				}
				else{
					return EnumSet.of(NOT_STARTED, INCOMPLETE, LOCALLY_INCOMPLETE, READY_TO_SUBMIT, COMPLETE, CONTROLLED);				
				}
			}
			else{
				if ( isAlwaysOnline ){
					return EnumSet.of(NOT_STARTED, INCOMPLETE, COMPLETE, PENDING, REJECTED, APPROVED);
				}
				else{
					return EnumSet.of(NOT_STARTED, INCOMPLETE, LOCALLY_INCOMPLETE, READY_TO_SUBMIT, COMPLETE, PENDING, REJECTED, APPROVED);				
				}
			}
		}
		
		/**
		 * Returns the <code>DocumentStatus</code> equivalent to <code>status</code>.
		 * 
		 * @param status equivalent to the DocumentStatus that is required.
		 * @return the <code>DocumentStatus</code> equivalent to <code>status</code>.
		 * 
		 * @throws IllegalArgumentException if the short name of IStatus does not
		 * match any of allowed values for a document status. These are mentioned
		 * in the class description.
		 */
		public static DocumentStatus valueOf(Status status) {
			String name = null;
			if (status != null) {
				name = status.getShortName();
				for (DocumentStatus docStatus : DocumentStatus.values())    {
					if (docStatus.toString().equals(name)) {
						return docStatus;
					}
				}
			}
			throw new IllegalArgumentException("There is no DocumentStatus that " + //$NON-NLS-1$
					"matches with: " + name); //$NON-NLS-1$
		}

		/**
		 * Returns the equivalent IStatus in <code>document</code> 
		 * for <code>docStatus</code>.
		 * 
		 * @param document IDocument to look for an equivalent IStatus.
		 * @param docStatus The DocumentStatus whose equivalent IStatus is required.
		 * @return the equivalent IStatus in <code>document</code> for 
		 * <code>docStatus</code>.
		 * 
		 * @throws IllegalArgumentException if <code>document</code> has no IStatus
		 * equivalent to <code>docStatus</code>.
		 */
		public static Status toIStatus(Document document, DocumentStatus docStatus) {
			for (int i = 0, c = document.numStatus(); i < c; ++i) {
				Status status = document.getStatus(i);
				if (docStatus.toString().equals(status.getShortName())) {
					return status;
				}
			}
			throw new IllegalArgumentException("There is no IStatus that matches with: " + //$NON-NLS-1$
					docStatus);
		}
		
		public static DocumentStatus fromStatusLongName(String statusLongName) {
		    for (DocumentStatus status : EnumSet.allOf(DocumentStatus.class)) {
		        if (status.toStatusLongName().equals(statusLongName))
		            return status;
		    }
		    return null;
		}

		/**
		 * Returns the same value as calling {@link IStatus#getLongName()} in
		 * the equivalent IStatus.
		 * 
		 * @return longName
		 */
		public String toStatusLongName() {
			switch (this) {
			case NOT_STARTED:
			    return "Not Started";
			case INCOMPLETE:
				return "Incomplete";
			case PENDING:
				return "Pending Approval";
			case REJECTED:
				return "Rejected";
			case APPROVED:
				return "Approved";
			case LOCALLY_INCOMPLETE:
				return "Locally Incomplete";
			case READY_TO_SUBMIT:
				return "Ready to Submit";
			case DATASET_DESIGNER:
				return "Dataset Designer";
			case VIEW_ONLY:
				return "View Only";
			case COMPLETE:
				return "Complete";
			case CONTROLLED:
				return "Controlled";
			case COMMIT_FAILED:
				return "Commit Failed";
			}
			//Should never happen
			return null;
		}

		/**
		 * Returns the same value as calling {@link IStatus#getShortName()} in
		 * the equivalent IStatus.
		 */
		@Override
		public String toString() {
			switch (this) {
			case NOT_STARTED:
			    return "Not Started"; //$NON-NLS-1$
			case INCOMPLETE:
				return "Incomplete"; //$NON-NLS-1$
			case PENDING:
				return "Pending"; //$NON-NLS-1$
			case REJECTED:
				return "Rejected"; //$NON-NLS-1$
			case APPROVED:
				return "Approved"; //$NON-NLS-1$
			case LOCALLY_INCOMPLETE:
				return "Locally Incomplete"; //$NON-NLS-1$
			case READY_TO_SUBMIT:
				return "Ready to Submit"; //$NON-NLS-1$
			case DATASET_DESIGNER:
				return "Dataset Designer"; //$NON-NLS-1$
			case VIEW_ONLY:
				return "View Only"; //$NON-NLS-1$
			case COMPLETE:
				return "Complete"; //$NON-NLS-1$
			case CONTROLLED:
				return "Controlled"; //$NON-NLS-1$
			case COMMIT_FAILED:
				return "Commit Failed";
			}
			//Should never happen
			return null;
		}
	}
	
	/**
	 * logger
	 */
	private static LoginServicePortType aa1 = null;

	public ChangeDocumentStatus(Record record, DocumentOccurrence docOcc, DocumentStatus newStatus){
		
	}
	
	private static String doLogin(Options opts) throws IOException, PGSecurityException, PGSecuritySAMLVerificationException, PGSecurityInvalidSAMLException{
		
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

		
		String saml = sa.toString();

		return saml;
	}
	
	private static String doRetrieveProjectCodeFromIdentifier(String identifier){
		
		String projectCode = identifier.split("/")[0];
		
		return projectCode;
		
	}
	
	/**
	 *The arguments should be
	 *
	 * -u &lt;username&gt; -w &lt;password&gt;	 
	 * 
	 * followed by:
	 * 
	 * remaining[0] - 'true' or 'false'. If false, then the program doesn't make any changes. It merely prints out the document_name to index mappings.
	 * and for each document asks you if this is the one you are interested in. If so, it will then spit out the document_occurrence to index mappings.
	 * This exploratory mode helps you to find out the required indices of the document and docOccurrence that defines the docInstance you want to change
	 * the status of. If 'true' the program changes the status of the docInstance according to the input parameters.
	 * remaining[1] - the openCDMS identifier of the record that contains the docInstance of the occurrence needing status modification.
	 * remaining[2] - the index of the document needing status modification
	 * remaining[3] - the index of the docOccurrence needing status modification
	 * @throws RepositoryNoSuchDatasetFault 
	 * @throws NotAuthorisedFault 
	 * @throws RepositoryServiceFault 
	 * @throws PGSecurityInvalidSAMLException 
	 * @throws PGSecuritySAMLVerificationException 
	 * @throws PGSecurityException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws RepositoryServiceFault, NotAuthorisedFault, RepositoryNoSuchDatasetFault, IOException, PGSecurityException, PGSecuritySAMLVerificationException, PGSecurityInvalidSAMLException {
		
		System.setProperty("axis.socketSecureFactory",
		"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		Options opts = new Options(args);
		String[] remaining = opts.getRemainingArgs();
		
		String saml = doLogin(opts);
			
		//Parse the project code from the identifier.
		String projectCode = doRetrieveProjectCodeFromIdentifier(remaining[1]);
		
		Properties properties = PropertyUtilities.getProperties("test.properties");
		RepositoryClient client = new RepositoryClient(new URL(properties.getProperty("org.psygrid.data.client.serviceURL")));
		
		DataSet ds = client.getDataSetSummary(projectCode, new Date(0), saml);
		ds = client.getDataSet(ds.getId(), saml);
		
		if(remaining[0].equals("true")){
			boolean success = changeRecordStatus(client, remaining, ds, saml);
			System.out.println("Record status change was successful");
		}else{
			int numDocuments = ds.numDocuments();
			for(int i = 0; i < numDocuments; i++){
				Document doc = ds.getDocument(i);
				System.out.println("Doc index: " + i + " and name: " + doc.getName());

			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter the docIndex of the document you want to find the right occurrence for: ");
			String str = in.readLine();
			int docIndex = Integer.valueOf(str).intValue();
			System.out.println();
			Document chosenDoc = ds.getDocument(docIndex);
			int numDocOccs = chosenDoc.numOccurrences();
			for(int i = 0; i < numDocOccs; i++){
				DocumentOccurrence docOcc = chosenDoc.getOccurrence(i);
				System.out.println("Dococc index: " + i + " and name: " + docOcc.getName());
				
			}
		}
	}
	
	private static boolean changeRecordStatus(RepositoryClient client, String[] remaining, DataSet ds, String saml) throws ConnectException, SocketTimeoutException, RemoteException, RepositoryServiceFault, NotAuthorisedFault{
		int indexOfDocToReject = Integer.valueOf(remaining[2]);
		
		Document docToReject = ds.getDocument(indexOfDocToReject);
		
		int indexOfOccurrenceToReject = Integer.valueOf(remaining[3]);
		DocumentOccurrence docOccToReject = docToReject.getOccurrence(indexOfOccurrenceToReject);
		
		//With all this stuff we can retrieve a record now. This is starting to get exciting.
		Record rec = client.getRecordSingleDocumentForOccurrence(remaining[1], docOccToReject.getId(), ds, saml);
		
		DocumentInstance instToReject = rec.getDocumentInstance(docOccToReject);
		
		client.changeStatus(instToReject.getId(), DocumentStatus.toIStatus(docToReject, DocumentStatus.REJECTED).getId(), saml);
		
		System.out.println("It worked - status changed!");
		//Need to retrieve the record (by identifier) from the repository. Do we need to attach then?
		//What does Collect do? When a user selects an identifier, a blank record is created with RecordHelper,
		//and its identifier is set.
		
		//So how is this used to change the status of a real document instance?
		//This skeleton record can be seen in use in Application::setSelectedDocOccurrence().
		//Effectively, if you have a docOccurrence, and a skeleton record, you can get a real record back by calling
		//return repositoryClient.getRecordSingleDocumentForOccurrence(record.getIdentifier().getIdentifier(), docOcc.getId(), dataSet, saml);
		//and once you have this record, you can do this to get a real docInstance:
		//instance = (DocumentInstance)record.getDocumentInstance(docOccurrence);
		//Then we can do this to change its actual status:
		//repositoryClient.changeStatus(docInstance.getId(), status.getId(), saml);
		//What I'm still unclear about is whether I still need to attach the instance and the dataset?
		
		return true;
	}

}
