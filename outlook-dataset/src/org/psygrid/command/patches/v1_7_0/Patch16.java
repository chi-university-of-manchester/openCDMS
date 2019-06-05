package org.psygrid.command.patches.v1_7_0;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch16 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		
		DataSet theDS = (DataSet)ds;
		
		//List of changes to make
		//1) Rename the CNWL (005001) centre to 'North London'
		//2) Get all sites from Camden and Islington (004001) and East London NHS Foundation Trust (007001) and place them in the 'North London' (005001) centre.
		//3) Get the site from Manchester Mental health and Social Care Trust (006001) and place it in 
		//		'North West' (002001).
		//4) Rename site 004001 and 007001 to deprecated.
		//5) Rename the site 007001 to deprecated.
		
		
		Group CNWL = null, camden = null, eastLondon = null, northWest = null, manchesterMentalHealth = null;
		
		int numCentres = ds.numGroups();
		for(int i = 0; i < numCentres; i++){
			Group group = (Group) theDS.getGroup(i);
			String name = group.getName();
		
			if(name.equals("002001")){
				northWest = group;
			}else if(name.equals("004001")){
				camden = group;
			}else if(name.equals("005001")){
				CNWL = group;
			}else if(name.equals("006001")){
				manchesterMentalHealth = group;
			}else if(name.equals("007001")){
				eastLondon = group;
			}
			
		}
		
		if(northWest == null || camden == null || CNWL == null || manchesterMentalHealth == null || eastLondon == null){
			throw new Exception("Didn't find all the centres");
		}
		
		CNWL.setLongName("North London");
		
		int numEastLondonSites = eastLondon.numSites();
		int numCamdenSites = camden.numSites();
		
		for(int i = 0; i < numEastLondonSites; i++){
			Site site = (Site)eastLondon.getSite(i);
			site.setId(null);
			CNWL.addSite(site);
		}
		
		for(int i = 0; i < numCamdenSites; i++){
			Site site = (Site)camden.getSite(i);
			site.setId(null);
			CNWL.addSite(site);
		}
	
		camden.setLongName("DEPRECATED");
		eastLondon.setLongName("DEPRECATED");
		
		int numMMHSites = manchesterMentalHealth.numSites();
		
		for(int i = 0; i < numMMHSites; i++){
			Site site = (Site)manchesterMentalHealth.getSite(i);
			site.setId(null);
			northWest.addSite(site);
		}
		
		manchesterMentalHealth.setLongName("DEPRECATED");
		
	}

	@Override
	public String getName() {
		return "Corrects Patch13";
	}

}
