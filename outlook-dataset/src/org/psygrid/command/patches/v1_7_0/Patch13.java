package org.psygrid.command.patches.v1_7_0;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
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

public class Patch13 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		HibernateFactory factory = new HibernateFactory();
		URL url = Thread.currentThread().getContextClassLoader().getResource("org/psygrid/command/resources/list of sites.xls");
		Workbook book = Workbook.getWorkbook(new File(url.toURI()));
		Sheet sheet = book.getSheet(0);
		
		//Centres are at location 0 in the row
		//Sites are at location 1 in the row
		//Post codes are at location 2 in the row
		//Consultant is at location 3 in the row.
		
		Map<String, Integer> centresMap = new HashMap<String, Integer>();
		
		//Get a map of the unique centres
		for ( int i=1, c=sheet.getRows(); i<c; i++ ){
			Cell[] row = sheet.getRow(i);
			if(row.length == 0){
				break;
			}
			centresMap.put(row[0].getContents(), 1);
		}
		
		//Create the centres.
		List<Group> groups = new ArrayList<Group>();
		
		//new centre names are 004001, 005001, and 006001
		
		List<String> centreCodes = new ArrayList<String>();
		centreCodes.add("004001");
		centreCodes.add("005001");
		centreCodes.add("006001");
		centreCodes.add("007001");
	
		
		for(String centreName : centresMap.keySet()){
			
			Group gp = (Group)factory.createGroup(centreCodes.get(0));
			centreCodes.remove(0);
			gp.setLongName(centreName);
			groups.add(gp);
			
			ds.addGroup(gp);
		}
		
		for ( int i=1, c=sheet.getRows(); i<c; i++ ){
			Cell[] row = sheet.getRow(i);
			
			if(row.length == 0){
				break;
			}
			
			centresMap.put(row[0].toString(), 1);
			
			String siteName = row[1].getContents();
			String postCode = row[2].getContents();
			String consultant = row[3].getContents();
			
			Group centre = getCentre(row[0].getContents(), groups);
			
			Site s = new Site(siteName, "??", postCode, centre);
			s.addConsultant(consultant);
			centre.addSite(s);
		}
		
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
		return "Add new centres to COMMAND study";
	}

}
