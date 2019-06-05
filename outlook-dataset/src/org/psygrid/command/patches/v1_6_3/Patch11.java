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

package org.psygrid.command.patches.v1_6_3;

import java.io.File;
import java.net.URL;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch11 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
	    URL url = Thread.currentThread().getContextClassLoader().getResource("org/psygrid/command/resources/commands_sites.xls");
		Workbook book = Workbook.getWorkbook(new File(url.toURI()));
		Sheet sheet = book.getSheet(0);
		for ( int i=1, c=sheet.getRows(); i<c; i++ ){
			Cell[] row = sheet.getRow(i);
			if ( null == row[0].getContents() || 0 == row[0].getContents().length()){
				//no id - new site
				String group = row[1].getContents();
				String name = row[3].getContents();
				String ukcrn = row[4].getContents();
				String postcode = row[5].getContents();
				for ( int j=0, d=ds.numGroups(); j<d; j++ ){
					Group g = ds.getGroup(j);
					if ( g.getName().equals(group) ){
						Site s = new Site(name, ukcrn, postcode, (Group)g);
						if ( group.equals("001001") ){
							s.addConsultant("Max Birchwood");
						}
						else if ( group.equals("002001")){
							s.addConsultant("Shôn Lewis");
							s.addConsultant("Nick Tarrier");
						}
						else if ( group.equals("003001")){
							s.addConsultant("Til Wykes");
							s.addConsultant("Emmanuelle Peters");
						}
						g.addSite(s);
						System.out.println("Added site "+group+"/"+name+"/"+ukcrn+"/"+postcode);
					}
				}
			}
			else{
				//existing site
				if ( null != row[4].getContents() && row[4].getContents().length() > 2  ){
					//We have a UKCRN code
					String ukcrn = row[4].getContents();
					System.out.println("Processing "+ukcrn);
					boolean found = false;
					for ( int j=0, d=ds.numGroups(); j<d; j++ ){
						Group g = ds.getGroup(j);
						for ( int k=0, e=g.numSites(); k<e; k++ ){
							Site s = g.getSite(k);
							if ( null != s.getId() && s.getId().toString().equals(row[0].getContents()) ){
								s.setSiteName(row[3].getContents());
								s.setSiteId(ukcrn);
								if ( row.length > 5 ){
									s.setGeographicCode(row[5].getContents());
								}
								found = true;
								System.out.println("Site for row "+i+" ("+ukcrn+") updated");
								break;
							}
						}
						if ( found ) break;
					}
					if ( !found ){
						System.out.println("No site found for row "+i+" ("+ukcrn+")");
					}
				}
			}
		}
	}

	@Override
	public String getName() {
		return "Add UKCRN codes to Command sites";
	}

}
