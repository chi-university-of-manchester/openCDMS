package org.psygrid.meds.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.meds.events.MedsPackageStatusChangeEvent;
import org.psygrid.meds.events.MedsPackageStatusChangeEventInterpreter;
import org.psygrid.meds.events.PackageViewEvent;
import org.psygrid.meds.export.MedsExportRequest;
import org.psygrid.meds.medications.MedicationPackage;

import com.csvreader.CsvWriter;

public class MedsCSVExportFormatter {
	
	private static final Log LOG = LogFactory.getLog(MedsCSVExportFormatter.class);
	
	private static final SimpleDateFormat timeDateFormatter = new SimpleDateFormat("HH:mm dd-MMM-yyyy");
	private static final SimpleDateFormat dateOnlyFormatter = new SimpleDateFormat("dd-MMM-yyyy");

	public static final char CSV_DELIMITER = ',';
	public static final String CSV_SUFFIX = ".csv";
	public static final String METADATA_FILE = "_metadata";

	private static final Long METADATA_ID = Long.valueOf(-1);
	
	public static void medsPackagesToCSV(List<MedicationPackage> packages, MedsExportRequest req, String outputPath) throws IOException {
	
		CsvWriter output = null;
		
		try{
			output = new CsvWriter(new FileOutputStream(outputPath), CSV_DELIMITER, Charset.defaultCharset());
			//Write the user information
			writeMedsExportUserData(req, output);
			
			//Write the headers
			writeHeadersForMedsExport(output);
			
			//Write a row for each package.
			writeMedsPackagesInfo(packages, output);
		}finally{
			if(output != null){
				output.close();
			}
		}
		
		
	}
	

	public static void medsPackageWorkflowEventsToCSV(List<MedicationPackage> packages, MedsExportRequest req, String outputPath) throws IOException {
		
		CsvWriter output = null;
		
		try{
			output = new CsvWriter(new FileOutputStream(outputPath), CSV_DELIMITER, Charset.defaultCharset());
			writeMedsExportUserData(req, output);
			writeHeadersForMedsExportWorkflowEvents(packages, output);
			writeWorkflowEvents(packages, output);
		}finally{
			if(output != null){
				output.close();
			}
		}
		
	}
	
	public static void medsPackageViewInfoToCSV(List<MedicationPackage> packages, MedsExportRequest req, String outputPath) throws IOException {
		
		CsvWriter output = null;
		
		try{
			output = new CsvWriter(new FileOutputStream(outputPath), CSV_DELIMITER, Charset.defaultCharset());
			writeMedsExportUserData(req, output);
			writeHeadersForViewEvents(packages, output);
			writeViewEvents(packages, output);
			
		}finally{
			if(output != null){
				output.close();
			}
		}
		
		
	}
	
	public static void writeViewEvents(List<MedicationPackage> packages, CsvWriter output) throws IOException {
		
		for(MedicationPackage p : packages){
			output.write(p.getPackageId());
			for(PackageViewEvent e : p.getViewEvents()){
				output.write(timeDateFormatter.format(e.getEventDate()));
				output.write(e.getSystemUser());
			}
			output.endRecord();
		}
	}
	
	public static void writeHeadersForViewEvents(List<MedicationPackage> packages, CsvWriter output) throws IOException {
		
		output.write("Package Identifier");
		
		int maxViewEvents = 0;
		
		for(MedicationPackage p : packages){
			
			if(p.getViewEvents() != null){
				maxViewEvents = Math.max(maxViewEvents, p.getViewEvents().size());
			}
		}
		
		for(int i = 0; i < maxViewEvents; i++){
			output.write("View " + Integer.toString(i+1) + " Date");			
			output.write("View " + Integer.toString(i+1) + " User");
 
		}
		
		output.endRecord();

		
	}
	
	public static void writeWorkflowEvents(List<MedicationPackage> packages, CsvWriter output) throws IOException{
		
		for(MedicationPackage p : packages){
			output.write(p.getPackageId());
			for(MedsPackageStatusChangeEvent e : p.getStatusChangeEvents()){
				Date eventDate = e.getEventDate();
				output.write(timeDateFormatter.format(eventDate));
				String eventType = e.getStatusChangeEvent();
				output.write(eventType);
				output.write(e.getSystemUser());
				
				MedsPackageStatusChangeEventInterpreter interpreter = new MedsPackageStatusChangeEventInterpreter(e);
				MedsPackageStatusChangeEventInterpreter.AdditionalStatusChangeEventInfo additionalInfo = interpreter.getAdditionalInfo();
				output.write(additionalInfo != null ? additionalInfo.getAdditionalInfoSummary() : null);
			}
			output.endRecord();
		}
		
	}
	
	public static void writeHeadersForMedsExportWorkflowEvents(List<MedicationPackage> packages, CsvWriter output) throws IOException{
		
		output.write("Package Identifier");
		
		int maxWorkflowEvents = 0;
		
		for(MedicationPackage p : packages){
			
			if(p.getStatusChangeEvents() != null){
				maxWorkflowEvents = Math.max(maxWorkflowEvents, p.getStatusChangeEvents().size());
			}
		}
		
		for(int i = 0; i < maxWorkflowEvents; i++){
			output.write("Event " + Integer.toString(i+1) + " Date");
			output.write("Event " + Integer.toString(i+1) + " Type");
			output.write("Event " + Integer.toString(i+1) + " User");
			output.write("Event " + Integer.toString(i+1) + " Additional Info"); 
		}
		
		output.endRecord();
		
	}
	

	

	private static void writeMedsExportUserData(final MedsExportRequest req, final CsvWriter output) throws IOException{
		output.write("Requestor");
		output.write(req.getRequestor());
		output.endRecord();
		
		output.write("Exported Date");
		output.write(timeDateFormatter.format(req.getRequestDate()));
		
	}


	private static void writeHeadersForMedsExport(final CsvWriter output) throws IOException {
		
		//Must display the following information:
		//packageId 
		//pharmacy name
		//treatment name
		//package status
		//expiry date
		//import date
		//shipment number
		//batch number
		
		output.write("Package Identifier");
		output.write("Pharmacy");
		output.write("Treatment");
		output.write("Status");
		output.write("Expiry Date");
		output.write("Import Date");
		output.write("Shipment Number");
		output.write("Batch Number");
		output.endRecord();
		
	}
	
	public static void writeMedsPackagesInfo(List<MedicationPackage> packages, CsvWriter output) throws IOException{
		/*
		output.write("Package Identifier");
		output.write("Pharmacy");
		output.write("Treatment");
		output.write("Status");
		output.write("Expiry Date");
		output.write("Import Date");
		output.write("Shipment Number");
		output.write("Batch Number");
		*/
		
		for(MedicationPackage p : packages){
			output.write(p.getPackageId());
			output.write(p.getPharmacy().getPharmacyName());
			output.write(p.getTreatment().getTreatmentName());
			output.write(p.getStatus());
			output.write(dateOnlyFormatter.format(p.getExpiryDate())); 
			output.write(dateOnlyFormatter.format(p.getImportDate())); 
			output.write(p.getShipmentNumber());
			output.write(p.getBatchNumber());
			output.endRecord();
		}
		
	}




}
