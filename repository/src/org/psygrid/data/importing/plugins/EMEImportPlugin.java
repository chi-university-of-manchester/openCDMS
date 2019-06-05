package org.psygrid.data.importing.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.data.utils.wrappers.AAQCWrapper;
import org.psygrid.meds.medications.PackageInfo;
import org.psygrid.meds.project.PharmacyInfoCodeOnly;
import org.psygrid.meds.project.TreatmentInfoCodeOnly;
import org.psygrid.meds.rmi.MedicationClient;

import au.com.bytecode.opencsv.CSVReader;

public class EMEImportPlugin implements ImportPlugin {

	private static String MEDS_IMPORT = "Medication Packages";
	private static String[] SOURCE_TYPES = {MEDS_IMPORT};
	
	private String[] columnNames = null;
	private List<String[]> data = null;
	private Map<String, Integer> colMap = null;

    // DateFormat objects are not threadsafe - so should not be static	
	private final DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	
	private File csvFile = null;
	private String user = null;
	
	/**
	 * Attribute authority query client
	 */
	private AAQCWrapper aaqc;

	// The last time the saml was updated
	private Date samlTime = null;

	private String saml = null;
	
	PrintStream log = null;
	
	MedicationClient medsClient = null;

	
	public String[] getImportTypes() {
		return SOURCE_TYPES;
	}

	public void run(final String projectCode, final String importType, final String filePath,
			final String user, final AAQCWrapper aaqc, final PrintStream log) throws Exception {

		this.csvFile = new File(filePath);
		this.user=user;
		this.aaqc=aaqc;
		this.saml = aaqc.getSAMLAssertion(user);
		this.samlTime=new Date();
		this.log = log;
		medsClient = new MedicationClient();
		
		readCSVFile(csvFile);
		
		final List<PackageInfo> packages = new ArrayList<PackageInfo>();
		
		for (int i = 0; i < data.size(); i++) {
			refreshSAML();
			final String[] row = data.get(i);
			
			final String medsPackageId = row[colMap.get("PackageNumber".toLowerCase())].trim();
			final String pharmacyCode = row[colMap.get("PharmacyCode".toLowerCase())].trim();
			final String treatmentCode = row[colMap.get("TreatmentCode".toLowerCase())].trim();
			final String shipmentNumber = row[colMap.get("ShipmentNumber".toLowerCase())].trim();
			final String batchNumber = row[colMap.get("BatchNumber".toLowerCase())].trim();
			final String expiryDateString = row[colMap.get("ExpiryDate".toLowerCase())].trim();
			
			final PackageInfo medsPackage = new PackageInfo();
			medsPackage.setQpRelease(false);
			medsPackage.setBatchNumber(batchNumber);
			medsPackage.setShipmentNumber(shipmentNumber);
			medsPackage.setPackageIdentifier(medsPackageId);
			medsPackage.setProjectCode("EME");
			
			final PharmacyInfoCodeOnly pI = new PharmacyInfoCodeOnly(pharmacyCode);
			
			final TreatmentInfoCodeOnly tI = new TreatmentInfoCodeOnly(treatmentCode);
			
			medsPackage.setPharmacyInfo(pI);
			medsPackage.setTreatmentInfo(tI);
			
			final Date expiryDate = dateFormatter.parse(expiryDateString);
			
			medsPackage.setExpiryDate(expiryDate);
			packages.add(medsPackage);
		}
		
		
		medsClient.saveMedicationPackages(packages, saml);

	}
	
	// Refresh the SAML every minute if needed
	private void refreshSAML() throws Exception{
		final Date now = new Date();
		if((now.getTime()-samlTime.getTime())>60*1000){
			this.saml = aaqc.getSAMLAssertion(user);
			samlTime=now;
		}
	}
	
	private void readCSVFile(final File csvFile) throws IOException {
		final CSVReader reader = new CSVReader(new BufferedReader(new FileReader(
				csvFile)));
		final List<String[]> lines = reader.readAll();
		colMap = new HashMap<String, Integer>();
		columnNames = lines.get(0);
		final int numCols = columnNames.length;
		for (int i = 0; i < numCols; i++) {
			final String colName = columnNames[i].toLowerCase().trim();
			if(colName.length()>0) {
				colMap.put(colName, i);
			}
		}
		data = lines.subList(1, lines.size());
		reader.close();
	}

	
	

}
