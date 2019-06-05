/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as 
published by the Free Software Foundation, either version 3 of 
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public 
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.psygrid.dataimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.security.SecurityManager;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.RepositoryNoConsentFault;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.dataimport.identifier.CustomParser;
import org.psygrid.dataimport.jaxb.doc.Basicentrytype;
import org.psygrid.dataimport.jaxb.doc.Compositeentrytype;
import org.psygrid.dataimport.jaxb.doc.Constanttype;
import org.psygrid.dataimport.jaxb.doc.Documenttype;
import org.psygrid.dataimport.jaxb.doc.Importdoc;
import org.psygrid.dataimport.jaxb.doc.Inputtype;
import org.psygrid.dataimport.jaxb.doc.Instancetype;
import org.psygrid.dataimport.jaxb.doc.Occurrencetype;
import org.psygrid.dataimport.jaxb.doc.Sectionoccurrencetype;
import org.psygrid.dataimport.jaxb.doc.Sectiontype;
import org.psygrid.dataimport.jaxb.doc.Unittype;
import org.psygrid.dataimport.jaxb.imp.Consentformgrouptype;
import org.psygrid.dataimport.jaxb.imp.Consentformtype;
import org.psygrid.dataimport.jaxb.imp.Csvsourcetype;
import org.psygrid.dataimport.jaxb.imp.Import;
import org.psygrid.dataimport.jaxb.imp.Mappingtype;
import org.psygrid.dataimport.jaxb.imp.Primaryconsentformtype;
import org.psygrid.dataimport.jaxb.imp.Translationtype;
import org.psygrid.dataimport.jaxb.imp.Usertype;
import org.psygrid.dataimport.sources.CsvSource;
import org.psygrid.dataimport.sources.ISource;
import org.psygrid.dataimport.sources.SourceException;
import org.psygrid.dataimport.visitors.ImportSetValueVisitor;

import argparser.ArgParser;
import argparser.BooleanHolder;
import argparser.IntHolder;
import argparser.StringHolder;

public class RunImport {

	public static final String CONSENT_YES = "1";

	private static boolean dryRun = true;
	
	public static final SecurityManager secMan = SecurityManager.getInstance();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//TODO: This will now only work because the id parser needs to be passed in.
		//Can't see why it matters though, the class is no longer used this way.
		try{
			RunImport imp = new RunImport();
			imp.doImport(args, null, System.out, null, null, false);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private static String updateSAML() throws ConnectException, RemoteServiceFault, IOException, EntrySAMLException, NotAuthorisedFault {
		synchronized(secMan) {
			Date keyValidity = secMan.getKeyValidity();
			//check to see that there is a key to refresh
			if (keyValidity != null) {
				Date now = new Date();
				long thirtySeconds = 30 * 1000;
				now.setTime(now.getTime() + thirtySeconds);
				if (keyValidity.getTime() < now.getTime()) {
					secMan.refreshKey();
				}
			}
		}

		return secMan.getSAMLAssertion().toString();
	}

	public void sanityCheck(String arg){
		
	}
	
	/**
	 * Perform an import from the XML definitions into the Repository.
	 * 
	 * @param args Arguments: -file %s, -dir %s, -abort %i, -dryrun %v
	 * @param repositoryUrl The location of the repository
	 * @param out The outputstream for displaying logging
	 * @param saml The saml
	 * @throws ImportException
	 */
	public void doImport(String[] args, String repositoryUrl, PrintStream out, CustomParser idParser, String saml, boolean importToExistingRecords) throws ImportException {

		boolean testMode = false;
		
		
		StringHolder fileArg = new StringHolder();
		StringHolder dirArg = new StringHolder();
		IntHolder abortAfterArg = new IntHolder();
		BooleanHolder dryRunArg = new BooleanHolder();

		ArgParser parser = new ArgParser("java org.psygrid.dataimport.RunImport");

		parser.addOption ("-file %s #name of the import script file", fileArg); 
		parser.addOption ("-dir %s #name of the directory containing the CSV files to import from", dirArg); 
		parser.addOption ("-abort %i #abort after processing the specified number of rows from the primary inmport source", abortAfterArg);
		parser.addOption ("-dryrun %v #run import without saving imported records to the data repository", dryRunArg);

		parser.setDefaultPrintStream(System.err);
		parser.matchAllArgs(args);

		out.println("Starting Import..");
		out.println("file="+fileArg.value);
		out.println("dir="+dirArg.value);
		out.println("abort="+abortAfterArg.value);
		out.println("dryRun="+dryRunArg.value);
		out.flush();
		File inputFile = new File(fileArg.value);
		String csvDir = dirArg.value+File.separator;
		dryRun = dryRunArg.value;
		int abortAfter = abortAfterArg.value;

		String rootDir = inputFile.getParent();

		try {
			JAXBContext jc = JAXBContext.newInstance( "org.psygrid.dataimport.jaxb.imp" );
			Unmarshaller u = jc.createUnmarshaller();

			JAXBContext docJc = JAXBContext.newInstance( "org.psygrid.dataimport.jaxb.doc" );
			Unmarshaller docU = docJc.createUnmarshaller();

			Import imp = (Import)u.unmarshal( new FileInputStream(inputFile) );

			String projectCode = imp.getProject();

			//initialize the data sources
			//TODO only really considering CSV sources at the moment
			BigInteger primaryId = null;
			Map<BigInteger, ISource> sources = new HashMap<BigInteger, ISource>();
			for ( Csvsourcetype csv: imp.getSources().getCsvsource() ){
				CsvSource csvSource = new CsvSource(csv, csvDir, projectCode, 0, idParser);
				sources.put(csv.getId(), csvSource);
				//if this is the primary source the it is used to determine the
				//number of records that are being imported
				if ( csv.isPrimary() ){
					primaryId = csv.getId();
				}
			}

			if ( null == primaryId ){
				throw new ImportException("No primary source has been defined.");
			}

			//load the dataset from the repository
			out.println("Loading dataset...");
			RepositoryClient client = null;;
			if ( null == repositoryUrl ){
				client = new RepositoryClient();
			}
			else{
				client = new RepositoryClient(new URL(repositoryUrl));
			}
			
			DataSet ds = null;
			
			if(!testMode){
				ds = client.getDataSetSummary(projectCode, new Date(0), saml);
				ds = client.getDataSet(ds.getId(), saml);
			}else{
				String dsFileName = "C:\\Users\\Bill\\openCDMS\\New Studies\\mds\\MDS Import Test4_ds.xml";
				Object obj1 = PersistenceManager.getInstance().load(dsFileName);
				ds = (DataSet)obj1;
			}

			System.out.println("Dataset name is: " + ds.getName());
			//load the standard codes
			out.println("Loading standard codes...");
			List<StandardCode> standardCodes = client.getStandardCodes(saml);
			StandardCode defaultStdCode = null;
			for ( StandardCode code: standardCodes ){
				if ( code.isUsedForDerivedEntry() ){
					defaultStdCode = code;
					break;
				}
			}

			//form the users map
			Map<String, String> usersMap = null;
			if ( null != imp.getUsers() ){
				usersMap = new HashMap<String, String>();
				for ( Usertype user: imp.getUsers().getUser() ){
					usersMap.put(user.getNative(), user.getPsygrid().trim());
				}
			}

			//form the set of rows to skip
			Set<Integer> skipRows = new HashSet<Integer>();
			if ( null != imp.getSkiprows() ){
				for ( BigInteger bi: imp.getSkiprows().getRow()){
					skipRows.add(new Integer(bi.intValue()));
				}
			}

			//drive the import process using the primary source -
			//the primary source defines how many records we are going to
			//import
			int rowCounter = 0;
			ISource primary = sources.get(primaryId);
			while ( primary.nextRow() ){
				rowCounter++;
				out.flush();
				out.println("Processing import data, row "+rowCounter);

				//get the native identifier
				String nativeId = primary.getNativeIdentifier();
				if ( null == nativeId ){
					//no native identifier has been found, so just
					//move to next row for all non-primary sources
					for ( BigInteger id: sources.keySet() ){ 
						if ( !id.equals(primaryId) ){
							ISource s = sources.get(id);
							if ( !s.nextRow() ){ //go through ALL sources and forward to the next row (if no native id found)
								throw new ImportException("No more rows for source "+id);
							}
						}
					}
				}
				else{
					//we have a native identifier, so for each of the non-primary
					//sources we move to the equivalent row
					for ( BigInteger id: sources.keySet() ){
						if ( !id.equals(primaryId) ){
							ISource s = sources.get(id);
							int debug = 2;
							if ( !sources.get(id).findRowById(nativeId) ){ //move to the row with the same native id as the primary source
								debug = 3;
								//((CsvSource)sources.get(id)).
								
							
								//If it can't find that, then lets assume for the MDS import that it just means that the document instance is blank.
								//In this case, we just set the source to not_queued.
								//The assumption is that each document occurrence will have all of its entries coming from
								//the same source. So we then just check the source, with the index in provided in the doc occurrence.
								//And if it is not queued then treat it as a blank occurrence.
								
								//throw new ImportException("Cannot find row for native identifier '"+nativeId+"' in source with id="+id);
							}
						}
					}
				}

				//see if we're supposed to skip this row
				if ( skipRows.contains(new Integer(rowCounter))){
					//skipping this row
					out.println("Skipping row "+rowCounter+" (native identifier = "+nativeId+")");
				}
				else{
					//create the record
					Record rec = ds.generateInstance();
					System.out.println("There are consent form groups: "+ds.numAllConsentFormGroups());
					for (int i = 0; i < ds.numAllConsentFormGroups(); i++) {
						System.out.println("Getting consent form group: "+ds.getAllConsentFormGroup(i).getDescription());
					}
					//set the consent
					boolean someConsentGiven = false;
					for ( Consentformgrouptype xmlCfg: imp.getConsentformgroups().getConsentformgroup() ){
						ConsentFormGroup cfg = ds.getAllConsentFormGroup(xmlCfg.getIndex().intValue());
						for ( Primaryconsentformtype xmlPcf: xmlCfg.getPrimaryconsentform() ){
							PrimaryConsentForm pcf = cfg.getConsentForm(xmlPcf.getIndex().intValue());
							String pcfValue = xmlPcf.getConstant() == null ? getValueForMapping(xmlPcf.getMapping(), sources) : xmlPcf.getConstant().getValue();
							//assuming for now that 1 = consent given, 0 = consent not given
							if ( CONSENT_YES.equals(pcfValue) ){
								Consent c = pcf.generateConsent();
								c.setConsentGiven(true);
								rec.addConsent(c);
								someConsentGiven = true;
							}else
							for ( Consentformtype xmlAcf: xmlPcf.getAssociatedconsentform() ){
								AssociatedConsentForm acf = pcf.getAssociatedConsentForm(xmlAcf.getIndex().intValue());
								String acfValue = xmlAcf.getConstant() == null ? getValueForMapping(xmlAcf.getMapping(), sources) : xmlAcf.getConstant().getValue();
								if ( CONSENT_YES.equals(acfValue) ){
									Consent c = acf.generateConsent();
									c.setConsentGiven(true);
									rec.addConsent(c);
									someConsentGiven = true;
								}
							}
						}
					}
					
					//If any consent has been given, and there is a status called "consented" in the study, set the study status to
					//'consented'.
					if(someConsentGiven == true){
						int numStatuses = ds.numStatus();
						Status consentedStatus = null;
						for(int i = 0; i < numStatuses; i++){
							Status s = ds.getStatus(i);
							if(s.getGenericState() == org.psygrid.data.model.hibernate.GenericState.ACTIVE){
								if(s.getLongName().equalsIgnoreCase("consented") || s.getShortName().equalsIgnoreCase("consented")){
									consentedStatus = s;
									break;
								}
							}
						}
						
						if(consentedStatus != null){
							((org.psygrid.data.model.hibernate.Record)rec).setStatus(consentedStatus);
						}
					}

					//set the schedule start date
					Date schStartDate = null;
					System.out.println(nativeId);
					if ( null != imp.getSchedulestartdate() ){
						String value = getValueForMapping(imp.getSchedulestartdate().getMapping(), sources);
						SimpleDateFormat sdf = new SimpleDateFormat(imp.getSchedulestartdate().getFormat());
						try{
							schStartDate = sdf.parse(value);
						}
						catch(NullPointerException ex){
							//assume null date only occurs for rows we're not going to import
						}
					}
					else{
						schStartDate = new Date();
					}
					RecordData rd = rec.generateRecordData();
					//TODO - The import object only carries sch start data info at the moment. Confusingly, this property is actually
					//carrying the study entry date. So we need get both, once the import object has been augmented.
					//rd.setScheduleStartDate(schStartDate);
					rd.setStudyEntryDate(schStartDate);
				
					rec.setRecordData(rd, null);
					
					//documents
					Map<String, Record> interviewerToRecords = new HashMap<String, Record>();
					for ( String file: imp.getDocuments().getDocument() ){

						File docFile = new File(rootDir+"/"+file);
						if(!docFile.exists()){
							continue;
						}
						Importdoc idoc = (Importdoc)docU.unmarshal(new File(rootDir+"/"+file));

						for ( Documenttype xmlDoc: idoc.getDocument() ){

							Document doc = ds.getDocument(xmlDoc.getIndex().intValue());
							int debug = 2;
							for ( Occurrencetype xmlOcc: xmlDoc.getOccurrence() ){

								BigInteger sourceIndex = this.getSourceIndexForOccurrence(xmlOcc);
								
								if(sourceIndex == null || !sources.get(sourceIndex).isQueued()){
									continue;
								}else{
									debug = 2;
								}
								
								
								
								boolean empty = false;
								if ( null != xmlOcc.getEmptydoc() ){
									empty = true;
									for ( org.psygrid.dataimport.jaxb.doc.Mappingtype mapping: xmlOcc.getEmptydoc().getMapping() ){
										empty &= (null == getValueForMapping(mapping, sources));
										//emptyDoc contains fields that, if null in the source, means that the document is empty
										//and does not need to be parsed for the current record. In practice these fields might 
										//consist of provenance information such as 'interview date', 'interviewer' and 'date entered'.
									}
								}

								Record r = null; //contains the record currently being worked on. It appears that there is an assumption here - that a given record is worked on by only one data entrant.
								// but this may not be the case - different data entrants may have entered different documents - right?
								if ( !empty ){

									//This first bit of logic is for the purpose of putting all 
									DocumentOccurrence occ = doc.getOccurrence(xmlOcc.getIndex().intValue());
									out.println("Document '"+doc.getDisplayText()+"', occurrence '"+occ.getDisplayText()+"'...");

									if ( null != xmlOcc.getInterviewer() ){
										String interviewer = null;
										try {
											interviewer = getValueForMapping(xmlOcc.getInterviewer(), sources);
										}
										catch (Exception e) {
											if ( dryRun ){
												out.println("Unable to get interviewer name in "+occ.getCombinedDisplayText());
												e.printStackTrace(out);
											}
											else{
												throw new ImportException("Unable to get interviewer name in "+occ.getCombinedDisplayText(), e);
											}
										}
										if (interviewer != null) {
											//translate interviewer to psygrid user if required
											String user = null;
											if ( null != usersMap){
												user = usersMap.get(interviewer);
												if ( null == user ){
													if ( dryRun ){
														out.println("No PsyGrid user found for native user '"+interviewer+"'");
														user = interviewer;
													}
													else{
														throw new ImportException("No PsyGrid user found for native user '"+interviewer+"' in "+occ.getCombinedDisplayText());
													}
												}
											}
											else{
												user = interviewer;
											}

											r = interviewerToRecords.get(user);
											if ( null == r ){
												r = ds.generateInstance();
												interviewerToRecords.put(user, r);
											}
										}
										else {
											r = rec;	//FIXME is this correct? This will happen if no user is specified in the csv file.
										}
									}
									else{
										r = rec;
									}

									Date date = null;
									if ( null != xmlOcc.getInterviewdate() ){
										String d = null;
										try {
											d = getValueForMapping(xmlOcc.getInterviewdate(), sources);
										} 
										catch (Exception e) {
											if ( dryRun ){
												out.println("Unable to get value for interview date in "+occ.getCombinedDisplayText()+". Cause was "+e.getMessage());
											}
											else{
												throw new ImportException("Unable to get value for interview date' in "+occ.getCombinedDisplayText()+". Cause was "+e.getMessage());
											}
										}
										if ( null == d ){
											out.println("Null interview date");
										}
										else{
											boolean parsed = false;
											if ( null == xmlOcc.getInterviewdate().getFormats() ){
												throw new ImportException("No date formats supplied for the interview date in "+occ.getCombinedDisplayText());
											}
											for ( String format: xmlOcc.getInterviewdate().getFormats().getFormat()){
												try{
													SimpleDateFormat formatter = new SimpleDateFormat(format);
													date = formatter.parse(d);
													parsed = true;
												}
												catch(ParseException ex){
													//do nothing - if none of the formats parse successfully
													//we deal with that later
												}
											}
											if ( !parsed ){
												throw new ImportException("Cannot convert string into a date using any of the supplied formats for the interview date in "+occ.getCombinedDisplayText());
											}
										}
									}

									DocumentInstance di = doc.generateInstance(occ);
									//((DocumentInstance)di).setStatus(IStatus.DOC_STATUS_COMPLETE);
									r.addDocumentInstance(di);
									if ( null != date ){
										//TODO naughty cast from interface to implementation
										((DocumentInstance)di).setCreated(date);
										((DocumentInstance)di).setEdited(date);
									}

									for ( Sectiontype xmlSec: xmlOcc.getSection() ){
										Section sec = doc.getSection(xmlSec.getIndex().intValue());


										//Contains a mapping of section occurrences and differentiates those which do NOT have instances.
										Map<Integer, Boolean> emptySecOccInsts = new LinkedHashMap<Integer, Boolean>();
										Map<Integer, SecOccInstance> secOccInsts = new LinkedHashMap<Integer, SecOccInstance>();

										for ( Sectionoccurrencetype xmlSecOcc: xmlSec.getSectionoccurrence() ){

											//form the map of empty section occurrence instances
											if ( null != xmlSecOcc.getInstances() ){
												for ( Instancetype inst : xmlSecOcc.getInstances().getInstance()){
													boolean emptyInst = false;
													if ( null != inst.getEmptyinst() ){
														emptyInst = true;
														for ( org.psygrid.dataimport.jaxb.doc.Mappingtype mapping: inst.getEmptyinst().getMapping() ){
															emptyInst &= (null == getValueForMapping(mapping, sources));
														}
													}
													emptySecOccInsts.put(new Integer(inst.getIndex().intValue()), new Boolean(emptyInst));
												}
											}

											SectionOccurrence secOcc = sec.getOccurrence(xmlSecOcc.getIndex().intValue());

											List<Basicentrytype> besWithCompDeps = new ArrayList<Basicentrytype>();
											List<Basicentrytype> besDerived = new ArrayList<Basicentrytype>();

											//basic entries
											for ( Basicentrytype xmlBe: xmlSecOcc.getBasicentry() ){
												BasicEntry be = (BasicEntry)doc.getEntry(xmlBe.getIndex().intValue());
												try{
													if ( 0 != xmlBe.getConstant().size() ){
														//Using a constant to define the value of the response to this
														//basic entry
														//If the value of the constant is an empty string then assume that
														//no response is to be created for this entry
														for ( Constanttype constant: xmlBe.getConstant() ){
															BasicResponse br = createBasicResponse(be, secOcc, constant,
                                                                    xmlBe.getFormats(), standardCodes, xmlBe.getUnit(),
                                                                    secOccInsts, emptySecOccInsts, out);
															if ( null != br ){
																di.addResponse(br);
															}
														}
													}
													else if ( null != xmlBe.getCompositeDependent() ){
														//This basic entry defines a composite dependent - this means
														//the the value of the basic entry depends upon whether the
														//composite has any rows in it or not. Therefore, need to wait
														//to process this basic entry until later, after all the composite
														//entries have been populated
														besWithCompDeps.add(xmlBe);
													}
													else{
														List<org.psygrid.dataimport.jaxb.doc.Mappingtype> mappings = xmlBe.getMapping();
														//for a basic entry NOT inside a composite entry there should be
														//only a single mapping element
														if ( mappings.size() > 0 ){
															for ( org.psygrid.dataimport.jaxb.doc.Mappingtype mapping: mappings ){
																BasicResponse br = createBasicResponse(be, secOcc, mapping,
                                                                        xmlBe.getFormats(), sources, imp.getTranslations(), xmlBe.getTranslations(),
                                                                        standardCodes, xmlBe.getUnit(), secOccInsts, emptySecOccInsts, out);
																if ( null != br ){
																	di.addResponse(br);
																}
															}
														}
														else if ( 0 == mappings.size() && be instanceof DerivedEntry){
															//handle derived entries without a mapping i.e. we need to run the 
															//calculation defined in the derived entry - do this later so add
															//to a list that we'll deal with at the end
															besDerived.add(xmlBe);
														}                                                
													}
												}
												catch (RuntimeException ex){
													out.println("EXCEPTION:");
													out.println("Document = "+doc.getDisplayText());
													out.println("Section = "+sec.getDisplayText());
													out.println("Entry = "+be.getDisplayText());
													out.println("Column number = " + (xmlBe.getMapping().get(0).getColumn().intValue() + 1));
													if ( dryRun ){
														out.println(ex.getMessage());
														ex.printStackTrace(out);
													}
													else{
														ImportException iex = new ImportException("Problem occurred processing "+doc.getDisplayText()+" - "+sec.getDisplayText()+" - "+be.getDisplayText(), ex);
														iex.printStackTrace(out);
														throw iex;
													}
												}
											}
											//composite entries
											for ( Compositeentrytype xmlCe: xmlSecOcc.getCompositeentry() ){
												CompositeEntry ce = (CompositeEntry)doc.getEntry(xmlCe.getIndex().intValue());
												//FIXME doesn't handle ISecOccInstances!!
												CompositeResponse cr = ce.generateInstance(secOcc);
												di.addResponse(cr);
												int beCounter = 0;
												//Add the composite rows in two stages.
												//Stage 1. Generate all of the basic responses, without any
												//rows - just generate a list of lists of basic responses, each
												//sub-list being equivalent to a composite row
												List<List<BasicResponse>> responses = new ArrayList<List<BasicResponse>>();
												for ( Basicentrytype xmlBe: xmlCe.getBasicentry() ){
													BasicEntry be = (BasicEntry)ce.getEntry(xmlBe.getIndex().intValue());
													int mCounter = 0;
													if ( xmlBe.getMapping().size() > 0 ){
														for ( org.psygrid.dataimport.jaxb.doc.Mappingtype xmlMapping: xmlBe.getMapping() ){
															if ( 0 == beCounter ){
																//we use the first basic entry in the composite entry as the
																//driver for creating composite rows
																responses.add(new ArrayList<BasicResponse>());
															}
															BasicResponse br = null;
															try{
																br = createBasicResponse(be, secOcc, xmlMapping, 
																		xmlBe.getFormats(), sources, imp.getTranslations(), xmlBe.getTranslations(), 
																		standardCodes, xmlBe.getUnit(), null, null, out);
																responses.get(mCounter).add(br);
															}
															catch(RuntimeException ex){
																out.println("EXCEPTION:");
																out.println("Document = "+doc.getDisplayText());
																out.println("Section = "+sec.getDisplayText());
																out.println("Composite = "+ce.getDisplayText());
																out.println("Entry = "+be.getDisplayText());
																out.println("Column number = " + xmlBe.getMapping().get(0).getColumn().intValue() + 1);
																if ( dryRun ){
																	out.println(ex.getMessage());
																	ex.printStackTrace(out);
																}
																else{
																	ImportException iex = new ImportException("Problem occurred processing "+doc.getDisplayText()+" - "+sec.getDisplayText()+" - "+be.getDisplayText(), ex);
																	iex.printStackTrace(out);
																	throw iex;
																}
															}
															mCounter++;
														}
														beCounter++;
													}
													else if ( xmlBe.getConstant().size() > 0 ){
														for ( org.psygrid.dataimport.jaxb.doc.Constanttype xmlConst: xmlBe.getConstant() ){
															if ( 0 == beCounter ){
																//we use the first basic entry in the composite entry as the
																//driver for creating composite rows
																responses.add(new ArrayList<BasicResponse>());
															}
															BasicResponse br = null;
															try{
																br = createBasicResponse(be, secOcc, xmlConst, 
																		xmlBe.getFormats(), standardCodes, xmlBe.getUnit(), 
																		null, null, out);
																responses.get(mCounter).add(br);
															}
															catch(RuntimeException ex){
																out.println("EXCEPTION:");
																out.println("Document = "+doc.getDisplayText());
																out.println("Section = "+sec.getDisplayText());
																out.println("Composite = "+ce.getDisplayText());
																out.println("Entry = "+be.getDisplayText());
																out.println("Column number = " + xmlBe.getMapping().get(0).getColumn().intValue() + 1);
																if ( dryRun ){
																	out.println(ex.getMessage());
																	ex.printStackTrace(out);
																}
																else{
																	ImportException iex = new ImportException("Problem occurred processing "+doc.getDisplayText()+" - "+sec.getDisplayText()+" - "+be.getDisplayText(), ex);
																	iex.printStackTrace(out);
																	throw iex;
																}
															}
															mCounter++;
														}
														beCounter++;
													}
													else{
														out.println("EXCEPTION:");
														out.println("Document = "+doc.getDisplayText());
														out.println("Section = "+sec.getDisplayText());
														out.println("Composite = "+ce.getDisplayText());
														out.println("Entry = "+be.getDisplayText());
														//out.println("Column number = " + xmlBe.getMapping().get(0).getColumn().intValue() + 1);
														if ( dryRun ){
															out.println("Basic entry "+be.getDisplayText()+" in table "+ce.getDisplayText()+" in "+sec.getDisplayText()+" for the document "+sec.getDisplayText()+" has no mappings or constants");
														}
														else {
															throw new ImportException("Basic entry "+be.getDisplayText()+" in table "+ce.getDisplayText()+" in "+sec.getDisplayText()+" for the document "+sec.getDisplayText()+" has no mappings or constants");
														}
													}

												}                                

												//Stage 2. Create the composite rows themselves, for each sub-list
												//where at least one response has a non-null value!
												//FIXME doesn't handle ISecOccInstances!!
												for ( List<BasicResponse> subList: responses ){
													boolean createRow = false;
													for ( BasicResponse br: subList ){
														if ( !br.getValue().isNull() ){
															createRow |= true;
														}
													}
													if ( createRow ){
														CompositeRow row = cr.createCompositeRow();
														for ( BasicResponse br: subList ){
															row.addResponse(br);
														}
													}
												}                               
											}

											//can now process the list of basic entries with composite dependents
											//FIXME doesn't handle ISecOccInstances!!
											for ( Basicentrytype xmlBe: besWithCompDeps ){
												BasicEntry be = (BasicEntry)doc.getEntry(xmlBe.getIndex().intValue());
												CompositeEntry ce = (CompositeEntry)doc.getEntry(xmlBe.getCompositeDependent().getIndex().intValue());
												CompositeResponse cr = (CompositeResponse)di.getResponse(ce, secOcc);

												String value = null;
												if ( cr.numCompositeRows() > 0 ){
													value = "1";
												}
												else{
													value = "0";
												}

												//create the response
												BasicResponse br = createBasicResponse(be, secOcc, value, xmlBe.getTranslations(), imp.getTranslations(), standardCodes, out);
												di.addResponse(br);
											}

											//and can also process the list of derived entries
											//FIXME doesn't handle ISecOccInstances!!
											for ( Basicentrytype xmlBe: besDerived ){
												DerivedEntry de = (DerivedEntry)doc.getEntry(xmlBe.getIndex().intValue());
												DerivedEntryHelper helper = new DerivedEntryHelper(de, di, secOcc, defaultStdCode);
												BasicResponse br = de.generateInstance(secOcc);
												di.addResponse(br);
												try{
													br.setValue(helper.calculateValue());
												}
												catch(RuntimeException ex){
													out.println("EXCEPTION:");
													out.println("Document = "+doc.getDisplayText());
													out.println("Section = "+sec.getDisplayText());
													out.println("Entry = "+de.getDisplayText());
													out.println("Column number = " + xmlBe.getMapping().get(0).getColumn().intValue() + 1);
													if ( dryRun ){
														ex.printStackTrace(out);
														out.println(ex.getMessage());	
													}
													else{
														ImportException iex = new ImportException("Problem occurred processing "+doc.getDisplayText()+" - "+sec.getDisplayText()+" - "+de.getDisplayText(), ex);
														iex.printStackTrace(out);
														throw iex;
													}
												}
											}

										}

										//add the section occurrence instances
										for ( SecOccInstance soi: secOccInsts.values() ){
											di.addSecOccInstance(soi);
										}
									}
								}
							}
						}
					}

					//reserve identifier for the record
					Identifier psygridIdentifier = null;
					try{
						psygridIdentifier = primary.getPsygridIdentifier();
						out.println("Psygrid identifier for row "+rowCounter+" is "+psygridIdentifier.getIdentifier());
					}
					catch (SourceException ex){
						if ( dryRun ){
							out.println("Cannot create identifier for row "+rowCounter+".\n"+ ex.getMessage());
						}
						else{
							ImportException iex = new ImportException("Cannot create identifier for row "+rowCounter, ex);
							iex.printStackTrace(out);
							throw iex;
						}
					}

					if ( !dryRun ){
						
						saml = updateSAML();
						
						if ( null != psygridIdentifier ){
							
							if(!importToExistingRecords){
								//successfully formed an identifier by parsing the native identifier for
								//the record being imported. Save this identifier in the PsyGrid system
								psygridIdentifier = client.addIdentifier(ds.getId(), psygridIdentifier, saml);
							}
						}
						else{
							
							//no psygrid identifier generated from the native identifier of the record,
							//so we just allocate the next one in the sequence
							List<Identifier> ids =
								client.generateIdentifiers(ds.getId(), 
										getValueForMapping(imp.getGroup().getMapping(), sources), 1, saml);
							psygridIdentifier = ids.get(0);
						}
						rec.setIdentifier(psygridIdentifier);
						
						String groupPrefix = rec.getIdentifier().getGroupPrefix();
						
						int numberOfCentres = ds.numGroups();
						Group recordGroup = null;
						Site recordSite = null;
						for(int i = 0; i < numberOfCentres; i++){
							Group gp = ds.getGroup(i);
							if(gp.getName().equals(groupPrefix)){
								recordGroup = gp;
								break;
							}
						}
						
						if(recordGroup != null){
							//TODO - for now just set the site as the 1st one in the group.
							//This really is only accurate studies that have only one site per centre.
							//Later we will need to provide a mechanism for mapping the site to the record properly.
							//This info will have to come from the csv source file.
							recordSite = recordGroup.getSite(0);
						}
						
						rec.setSite(recordSite);
						
						for ( Record r  : interviewerToRecords.values()){
							r.setIdentifier(psygridIdentifier);
							r.setSite(recordSite);
						}

						try{
							//save the record instance that contains the consent
							//rec.attach(ds);
							
							if(!importToExistingRecords){
								client.saveRecord(rec, true, saml);
								out.println("Record '"+rec.getIdentifier().getIdentifier()+"' saved");
							}
							
							
							//save the record instance containing the document instances (there should
							//be one record instance for each interviewer for the subject)
							for ( Entry<String,  Record> e: interviewerToRecords.entrySet()){
								client.saveRecordAsUser(e.getValue(), e.getKey(), saml);
								out.println("Record '"+rec.getIdentifier().getIdentifier()+"' saved for user '"+e.getKey());
							}
						}
						catch (RepositoryNoConsentFault ex){
							ImportException iex = new ImportException("There is insufficient consent to save Record '"+rec.getIdentifier().getIdentifier()+"'", ex);
							iex.printStackTrace(out);
							throw iex;
						}

					}
				}

				if ( abortAfter > 0 ){
					//if an abortAfter value has been specified abort the import after
					//the specified number of rows in the import data has been processed
					if ( rowCounter == abortAfter ){
						break;
					}
				}

			}

		}
		catch (JAXBException ex) {
			throw new ImportException(ex);
		}
		catch (IOException ioe) {
			throw new ImportException(ioe);
		}
		catch (Exception e) {
			throw new ImportException(e);
		}
	}

	private static BasicResponse createBasicResponse(BasicEntry be, SectionOccurrence secOcc,
                                                     String input, org.psygrid.dataimport.jaxb.doc.Translationstype xmlTranslations, org.psygrid.dataimport.jaxb.imp.Translationstype importLevelTranslations,
                                                     List<StandardCode> stdCodes, PrintStream out) throws ImportException {

		BasicResponse br = be.generateInstance(secOcc);
		//TODO using implementation rather than interface here
		Value val = (Value)be.generateValue();
		br.setValue(val);

		//translate the input value (if required)
		String output = null;
		boolean standardCode = false;
		
		if ( null == output && null != importLevelTranslations ){            
			for ( org.psygrid.dataimport.jaxb.imp.Translationtype t: importLevelTranslations.getTranslation()){
				for ( String i: t.getInput() ){
						if ( i.equals(input) ){
							output = t.getOutput();
							standardCode = t.isStandardCode();
							break;
						}
				}
				if ( null != output ){
					break;
				}
			}
		}
		
		if ( null == output && null != xmlTranslations ){            
			for ( org.psygrid.dataimport.jaxb.doc.Translationtype t: xmlTranslations.getTranslation()){
				for ( Inputtype i: t.getInput() ){
					if ( i.isSpecial() ){
						//if the special attribute of the input element is true then this
						//indicates that the contents of the input element have a special
						//meaning. The currently available special meanings are:
						//  gtN -> if the import value is greater than N then replace it with the output value for this translation
						//  * -> Matches all values
						//  xyz* -> Matches values starting with xyz
						if ( i.getValue().startsWith("gt") ){
							int n = Integer.parseInt(i.getValue().substring("gt".length()));
							if ( Integer.parseInt(input) > n ){
								output=t.getOutput();
								standardCode = t.isStandardCode();
								break;
							}
						}
						if ( i.getValue().equals("*") ){
							output=t.getOutput();
							standardCode = t.isStandardCode();
							break;
						}
						if ( i.getValue().endsWith("*") ){
							if ( input.startsWith(i.getValue().substring(0, i.getValue().length()-1))){
								output=t.getOutput();
								standardCode = t.isStandardCode();
								break;
							}
						}
					}
					else{
						if ( i.getValue().equals(input) ){
							output = t.getOutput();
							standardCode = t.isStandardCode();
							break;
						}
					}
				}
				if ( null != output ){
					break;
				}
			}
		}
		if ( null == output ){
			output = input;
		}

		if ( standardCode ){
			val.setStandardCode(stdCodes.get(Integer.parseInt(output)));
		}
		else{
			ImportSetValueVisitor visitor = new ImportSetValueVisitor();
			visitor.setValue(output);
			visitor.setResponse(br);
			try {
				val.accept(visitor);
			}
			catch (ModelException ex) {
				out.println("EXCEPTION:");
				out.println("Section = "+secOcc.getCombinedDisplayText());
				out.println("Entry = "+be.getDisplayText());
				if (dryRun) {
					out.println("Unable to assign value. "+ex.getMessage());
				}
				else {
					throw new ImportException("Unable to assign value. "+ex.getMessage(), ex);
				}
			}
		}

		return br;
	}

	private static BasicResponse createBasicResponse(
            BasicEntry be,
            SectionOccurrence secOcc,
            org.psygrid.dataimport.jaxb.doc.Mappingtype xmlMapping,
            org.psygrid.dataimport.jaxb.doc.Formatstype xmlFormats,
            Map<BigInteger, ISource> sources,
            org.psygrid.dataimport.jaxb.imp.Translationstype studyLevelTranslations,
            org.psygrid.dataimport.jaxb.doc.Translationstype xmlTranslations,
            List<StandardCode> stdCodes,
            List<Unittype> xmlUnits,
            Map<Integer, SecOccInstance> secOccInsts,
            Map<Integer, Boolean> emptySecOccInsts,
            PrintStream out
    ) throws ImportException {
		
		BasicResponse br = null;
		if ( secOcc.isMultipleAllowed() ){
			//check to see if this section occurrence instance has already been defined
			//to be empty
			Boolean empty = emptySecOccInsts.get(new Integer(xmlMapping.getOccurrence().toString()));
			if ( null == empty ){
				throw new ImportException("Unknown section occurrence instance with index "+xmlMapping.getOccurrence().toString());
			}
			if ( empty.booleanValue() ){
				//the section occurrence instance is empty, so no need to go any further
				return null;
			}

			SecOccInstance secOccInst = secOccInsts.get(new Integer(xmlMapping.getOccurrence().toString()));
			if ( null == secOccInst ){
				secOccInst = secOcc.generateInstance();
				secOccInsts.put(new Integer(xmlMapping.getOccurrence().toString()), secOccInst);
			}
			br = be.generateInstance(secOccInst);
		}
		else{
			br = be.generateInstance(secOcc);
		}

		//TODO using implementation rather than interface here
		Value val = (Value)be.generateValue();
		br.setValue(val);

		//find the source
		String input = null;
		if ( null != xmlMapping ){
			ISource source = sources.get(xmlMapping.getSource());
			input = source.getColumn(xmlMapping.getColumn().intValue());
		}
		else{
			input = "";
		}

		//see if we need to strip any units from the value
		Unit unit = null;
		if ( null != xmlUnits ){
			for ( Unittype xmlUnit: xmlUnits ){
				Unit u = be.getUnit(xmlUnit.getIndex().intValue());
				if ( xmlUnit.isDefault() ){
					unit = u;
				}
				for( String ua: xmlUnit.getInput() ){
					if ( null != input && input.contains(ua) ){
						unit = u;
						//strip the unit
						input = input.substring(0, input.indexOf(ua))+input.substring(input.indexOf(ua)+ua.length(), input.length());
						break;
					}
				}
			}
		}

		//translate the input value (if required)
		String output = null;
		boolean standardCode = false;
		
		//First check the study-level translations - these take precedence over all else.
		if ( null != studyLevelTranslations && null != studyLevelTranslations.getTranslation() ){
			for ( org.psygrid.dataimport.jaxb.imp.Translationtype t: studyLevelTranslations.getTranslation() ){
				for ( String i: t.getInput() ){
					if ( i.equals(input) ){
						output = t.getOutput();
						standardCode = t.isStandardCode();
						break;
					}					
				}
				if ( null != output ){
					break;
				}
			}
		}
		
		
		//first check the mapping specific translations - these
		//take precedence over the translations defined at the basic entry
		//level
		if ( null != xmlMapping && null != xmlMapping.getTranslations() ){
			for ( org.psygrid.dataimport.jaxb.doc.Translationtype t: xmlMapping.getTranslations().getTranslation() ){
				for ( Inputtype i: t.getInput() ){
					if ( i.isSpecial() ){
						//if the special attribute of the input element is true then this
						//indicates that the contents of the input element have a special
						//meaning. The currently available special meanings are:
						//  gtN -> if the import value is greater than N then replace it with the output value for this translation
						//  * -> Matches all values
						//  xyz* -> Matches values starting with xyz
						if ( i.getValue().startsWith("gt") ){
							int n = Integer.parseInt(i.getValue().substring("gt".length()));
							if ( Integer.parseInt(input) > n ){
								output=t.getOutput();
								standardCode = t.isStandardCode();
								break;
							}
						}
						if ( i.getValue().equals("*") ){
							output=t.getOutput();
							standardCode = t.isStandardCode();
							break;
						}
						if ( i.getValue().endsWith("*") ){
							if ( input.startsWith(i.getValue().substring(0, i.getValue().length()-1))){
								output=t.getOutput();
								standardCode = t.isStandardCode();
								break;
							}
						}
					}
					else{
						if ( i.getValue().equals(input) ){
							output = t.getOutput();
							standardCode = t.isStandardCode();
							break;
						}
					}
				}
				if ( null != output ){
					break;
				}
			}
		}
		//if no translation was found at the mapping level check
		//the translations at the basic entry level
		if ( null == output && null != xmlTranslations ){            
			for ( org.psygrid.dataimport.jaxb.doc.Translationtype t: xmlTranslations.getTranslation()){
				for ( Inputtype i: t.getInput() ){
					if ( i.isSpecial() ){
						//if the special attribute of the input element is true then this
						//indicates that the contents of the input element have a special
						//meaning. The currently available special meanings are:
						//  gtN -> if the import value is greater than N then replace it with the output value for this translation
						//  * -> Matches all values
						//  xyz* -> Matches values starting with xyz
						if ( i.getValue().startsWith("gt") ){
							int n = Integer.parseInt(i.getValue().substring("gt".length()));
							if ( Integer.parseInt(input) > n ){
								output=t.getOutput();
								standardCode = t.isStandardCode();
								break;
							}
						}
						if ( i.getValue().equals("*") ){
							output=t.getOutput();
							standardCode = t.isStandardCode();
							break;
						}
						if ( i.getValue().endsWith("*") ){
							if ( input.startsWith(i.getValue().substring(0, i.getValue().length()-1))){
								output=t.getOutput();
								standardCode = t.isStandardCode();
								break;
							}
						}
					}
					else{
						if ( i.getValue().equals(input) ){
							output = t.getOutput();
							standardCode = t.isStandardCode();
							break;
						}
					}
				}
				if ( null != output ){
					break;
				}
			}
		}
		if ( null == output ){
			output = input;
		}

		//see if there is any "other" text (for an "Other (plese specify)" option)
		String otherText = null;
		if ( null != xmlMapping && null != xmlMapping.getOtherText() ){
			ISource source = sources.get(xmlMapping.getOtherText().getSource());
			otherText = source.getColumn(xmlMapping.getOtherText().getColumn().intValue());
		}

		if ( standardCode ){
			val.setStandardCode(stdCodes.get(Integer.parseInt(output)));
		}
		else{
			val.setUnit(unit);
			ImportSetValueVisitor visitor = new ImportSetValueVisitor();
			visitor.setValue(output);
			visitor.setResponse(br);
			visitor.setOtherText(otherText);
			if ( null != xmlFormats ){
				visitor.setFormats(xmlFormats.getFormat());
			}
			try {
				val.accept(visitor);
			}
			catch (ModelException ex) {
				out.println("EXCEPTION:");
				out.println("Section = "+secOcc.getCombinedDisplayText());
				out.println("Entry = "+be.getDisplayText());
				out.println("Column number = " + xmlMapping.getColumn().intValue() + 1);
				out.println("Output value = "  + output);
				if (dryRun) {
					out.println("Unable to assign value. "+ex.getMessage());
				}
				else {
					throw new ImportException("Unable to assign value. "+ex.getMessage(), ex);
				}
			}
		}

		return br;
	}
	
	/**
	 * Retrieves the source Index from the occurrence. Tries to retrieve this from multiple places.
	 * @param xmlOcc
	 * @return
	 * @throws ImportException 
	 */
	private BigInteger getSourceIndexForOccurrence(Occurrencetype xmlOcc) throws ImportException{
		
		System.out.println("Getting source index for " + xmlOcc.getDescription());
		
		BigInteger sourceIndex = null;
		
		org.psygrid.dataimport.jaxb.doc.Mappingtype map = xmlOcc.getInterviewer();
		if(map != null){
			sourceIndex = map.getSource();
		}
		
		if(sourceIndex != null){
			System.out.println("Found source index at interviewer for " + xmlOcc.getDescription() + ": " + sourceIndex);
			return sourceIndex;
		}
		
		map = xmlOcc.getInterviewdate();
		
		if(map != null){
			sourceIndex = map.getSource();
		}
		
		if(sourceIndex != null){
			System.out.println("Found source index at intervew date for " + xmlOcc.getDescription() + ": " + sourceIndex);
			return sourceIndex;
		}
		
		outer : for(Sectiontype sec : xmlOcc.getSection()){
			for(Sectionoccurrencetype secOcc : sec.getSectionoccurrence()){
				for(Basicentrytype bE : secOcc.getBasicentry()){
					for(org.psygrid.dataimport.jaxb.doc.Mappingtype mT : bE.getMapping()){
						if(map != null){
							sourceIndex = map.getSource();
							if(sourceIndex != null){
								break outer;
							}
						}
					}
				}
			}
		}
		
		if(sourceIndex == null){
			System.out.println("ERROR: Could not find source index for " + xmlOcc.getDescription());
			return null;
			//throw new ImportException("Could not retrieve a sourceIndex from " + xmlOcc.getDescription());//Throw an import exception!
		}
		
		System.out.println("Found source index in basic entry for " + xmlOcc.getDescription() + ": " + sourceIndex);
		return sourceIndex;
	}

	private static BasicResponse createBasicResponse(
            BasicEntry be,
            SectionOccurrence secOcc,
            org.psygrid.dataimport.jaxb.doc.Constanttype xmlConstant,
            org.psygrid.dataimport.jaxb.doc.Formatstype xmlFormats,
            List<StandardCode> stdCodes,
            List<Unittype> xmlUnits,
            Map<Integer, SecOccInstance> secOccInsts,
            Map<Integer, Boolean> emptySecOccInsts,
            PrintStream out
    ) throws ImportException {

		BasicResponse br = null;
		if ( secOcc.isMultipleAllowed() ){
			//check to see if this section occurrence instance has already been defined
			//to be empty
			Boolean empty = emptySecOccInsts.get(new Integer(xmlConstant.getOccurrence().toString()));
			if ( null == empty ){
				throw new ImportException("Unknown section occurrence instance with index "+xmlConstant.getOccurrence().toString());
			}
			if ( empty.booleanValue() ){
				//the section occurrence instance is empty, so no need to go any further
				return null;
			}

			SecOccInstance secOccInst = secOccInsts.get(new Integer(xmlConstant.getOccurrence().toString()));
			if ( null == secOccInst ){
				secOccInst = secOcc.generateInstance();
				secOccInsts.put(new Integer(xmlConstant.getOccurrence().toString()), secOccInst);
			}
			br = be.generateInstance(secOccInst);
		}
		else{
			br = be.generateInstance(secOcc);
		}

		//TODO using implementation rather than interface here
		Value val = (Value)be.generateValue();
		br.setValue(val);

		//find the constant value
		String constant = xmlConstant.getValue();

		Unit unit = null;
		if ( null != xmlUnits ){
			for ( Unittype xmlUnit: xmlUnits ){
				Unit u = be.getUnit(xmlUnit.getIndex().intValue());
				if ( xmlUnit.isDefault() ){
					unit = u;
				}
			}
		}

		if ( xmlConstant.isStandardCode() ){
			val.setStandardCode(stdCodes.get(Integer.parseInt(constant)));
		}
		else{
			val.setUnit(unit);
			ImportSetValueVisitor visitor = new ImportSetValueVisitor();
			visitor.setValue(constant);
			visitor.setResponse(br);
			if ( null != xmlFormats ){
				visitor.setFormats(xmlFormats.getFormat());
			}

			try {
				val.accept(visitor);
			}
			catch (ModelException ex) {
				out.println("EXCEPTION:");
				out.println("Section = "+secOcc.getCombinedDisplayText());
				out.println("Entry = "+be.getDisplayText());
				if (dryRun) {
					out.println("Unable to assign value. "+ex.getMessage());
				}
				else {
					throw new ImportException("Unable to assign value. "+ex.getMessage(), ex);
				}
			}
		}

		return br;
	}

	private static String getValueForMapping(Mappingtype xmlMapping, Map<BigInteger, ISource> sources) throws ImportException {
		//find the source
		String output = null;
		if ( null != xmlMapping ){
			ISource source = sources.get(xmlMapping.getSource());
			String input = source.getColumn(xmlMapping.getColumn().intValue());
			//translate the input value (if required)
			if ( null != xmlMapping.getTranslations() ){            
				for ( Translationtype t: xmlMapping.getTranslations().getTranslation()){
					for ( String i: t.getInput() ){
						if ( i.equals(input) ){
							output = t.getOutput();
							break;
						}
					}
					if ( null != output ){
						break;
					}
				}
			}
			if ( null == output ){
				output = input;
			}
			//change empty string to null
			if ( null != output && 0 == output.length()){
				output = null;
			}
		}
		else{
			throw new ImportException("No mapping or constant");
		}
		return output;
	}

	/*
	 * TODO
	 * Note - this method is exactly the same as the one above, except it uses
	 * classes from the package org.psygrid.dataimport.jaxb.doc instead of those
	 * from org.psygrid.dataimport.jaxb.imp. These classes (e.g. Mappingtype) are
	 * exactly the same, but I can't work out how to run JAXBs xjc.sh to generate
	 * "common" classes used by multiple schemas.
	 */
	private static String getValueForMapping(org.psygrid.dataimport.jaxb.doc.Mappingtype xmlMapping, 
			Map<BigInteger, ISource> sources){
		//find the source
		ISource source = sources.get(xmlMapping.getSource());
		String input = source.getColumn(xmlMapping.getColumn().intValue());
		//translate the input value (if required)
		String output = null;
		if ( null != xmlMapping.getTranslations() ){            
			for ( org.psygrid.dataimport.jaxb.doc.Translationtype t: xmlMapping.getTranslations().getTranslation()){
				for ( Inputtype i: t.getInput() ){
					if ( i.isSpecial() ){
						//if the special attribute of the input element is true then this
						//indicates that the contents of the input element have a special
						//meaning. The currently available special meanings are:
						//  gtN -> if the import value is greater than N then replace it with the output value for this translation
						//  * -> Matches all values
						//  xyz* -> Matches values starting with xyz
						if ( i.getValue().startsWith("gt") ){
							int n = Integer.parseInt(i.getValue().substring("gt".length()));
							if ( Integer.parseInt(input) > n ){
								output=t.getOutput();
							}
							break;
						}
						if ( i.getValue().equals("*") ){
							output=t.getOutput();
							break;
						}
						if ( i.getValue().endsWith("*") ){
							if ( input.startsWith(i.getValue().substring(0, i.getValue().length()-1))){
								output=t.getOutput();
							}
							break;
						}
					}
					else{
						if ( i.getValue().equals(input) ){
							output = t.getOutput();
							break;
						}
					}
				}
				if ( null != output ){
					break;
				}
			}
		}
		if ( null == output ){
			output = input;
		}
		//change empty string to null
		if ( null != output && 0 == output.length()){
			output = null;
		}
		return output;
	}

}
