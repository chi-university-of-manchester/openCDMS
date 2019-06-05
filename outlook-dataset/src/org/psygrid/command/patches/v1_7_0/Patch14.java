package org.psygrid.command.patches.v1_7_0;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch14 extends AbstractPatch {
	
	static final String SLonSE = "South London and South East Hub";
	static final String NLon = "North London Hub";
	static final String NW = "North West Hub";

	private void correctSiteNamesForCNWL(Group cnwl){
		int numSites = cnwl.numSites();
		for(int i = 0; i < numSites; i++){
			Site s = (Site)cnwl.getSite(i);
			s.setSiteName("CNWL - " + s.getSiteName());
			int debug = 2;
		}
	}
	
	private void changeNameToDeprecated(Group gp){
		gp.setLongName("DEPRECATED");
	}
	
	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		
		DataSet theDS = (DataSet)ds;
		Group CNWL = null, camden = null, eastLondon = null, northWest = null, manchesterMentalHealth = null, southLondonAndSoutheast = null;
		
		Map<String, Group> groups = new HashMap<String, Group>();
		
		int numCentres = ds.numGroups();
		for(int i = 0; i < numCentres; i++){
			Group group = (Group) theDS.getGroup(i);
			String name = group.getName();
		
			if(name.equals("002001")){
				northWest = group;
				groups.put("002001", group);
			}else if(name.equals("003001")){
				southLondonAndSoutheast = group;
				groups.put("003001", group);
			}else if(name.equals("004001")){
				camden = group;
				groups.put("04001", group);
			}else if(name.equals("005001")){
				CNWL = group;
				groups.put("005001", group);
			}else if(name.equals("006001")){
				manchesterMentalHealth = group;
				groups.put("006001", group);
			}else if(name.equals("007001")){
				eastLondon = group;
				groups.put("007001", group);
			}
			
		}
		
		if(northWest == null || camden == null || CNWL == null || manchesterMentalHealth == null || eastLondon == null
				|| southLondonAndSoutheast == null){
			throw new Exception("Didn't find all the centres");
		}

		//Set the 'camden...' & 'east london....' group names to deprecated.
		changeNameToDeprecated(camden);
		changeNameToDeprecated(eastLondon);
		changeNameToDeprecated(manchesterMentalHealth);
		
		//Change the name of the CNWL group to 'North London'
		CNWL.setLongName("North London");
		
		
		HibernateFactory factory = new HibernateFactory();
		URL url = Thread.currentThread().getContextClassLoader().getResource("org/psygrid/command/resources/Command Centres and Sites Feb 2010.xls");
		Workbook book = Workbook.getWorkbook(new File(url.toURI()));
		Sheet sheet = book.getSheet(0);
		
		//First we need to preface all the sites that are in the CNWL centre (005001) with 'CNWL - '
		this.correctSiteNamesForCNWL(CNWL);
		
		//Iterate through the spreadsheet and add  the site in a row to the appropriate centre.
		for ( int i=1, c=sheet.getRows(); i<c; i++ ){
			Cell[] row = sheet.getRow(i);
			if(row.length == 0){
				break;
			}
			
			addSiteToCentre(groups, row);
		}
		
		int debug = 2;
	}
	
	private Group getGroupForSiteAddition(Map<String, Group> centres, String nameInSpreadsheet) throws Exception{
		/*
		static final String SLonSE = "South London and South East Hub";
		static final String NLon = "North London Hub";
		static final String NW = "North West Hub";
		*/
		
		Group correctGroup = null;
		
		if(nameInSpreadsheet.equals(SLonSE)){
			correctGroup = centres.get("003001");
		}else if(nameInSpreadsheet.equals(NLon)){
			correctGroup = centres.get("005001");
		}else if(nameInSpreadsheet.equals(NW)){
			correctGroup = centres.get("002001");
		}
		
		if(correctGroup == null){
			throw new Exception("Could not find correct centre to which the new site is to be added.");
		}
		
		return correctGroup;
	}
	
	private void addSiteToCentre(Map<String, Group> centres, Cell[] row) throws Exception{
		
		//Need to look in column 0 - determine which group to which the site should be added.
		
		Group centre = getGroupForSiteAddition(centres, row[0].getContents());

		//Centres are at location 0 in the row
		//Sites are at location 1 in the row
		//Consultant is at location 2 in the row
		//Post code is at location 3 in the row.
		
		String siteName = row[1].getContents();
		String postCode = row[3].getContents();
		String consultant = row[2].getContents();
		
		Site s = new Site(siteName, "??", postCode, centre);
		s.addConsultant(consultant);
		
		centre.addSite(s);
		
		
	}

	
	protected Group getCentre(String centreName, List<Group> centres){
		
		Group matchingGroup = null;
		
		for(Group centre: centres){
			if(centre.getLongName().equals(centreName)){
				matchingGroup = centre;
				break;
				
			}
		}
		
		return matchingGroup;
		
		
	}
	

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Corrects Patch13";
	}

}
