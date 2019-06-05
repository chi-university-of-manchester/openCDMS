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


package org.psygrid.drn.address;

import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.TargetType;

/**
 * @author Rob Harper
 *
 */
public class ADDGroups {

	private static final String project = "ADDRESS";

	public static final GroupType group1 = new GroupType("North Cumbria Acute Hospitals NHS Trust", "640001", project);
	public static final GroupType group2 = new GroupType("South Tees Hospital NHS Trust", "640002", project);
	public static final GroupType group3 = new GroupType("City Hospitals Sunderland NHS Foundation Trust", "640003", project);
	public static final GroupType group4 = new GroupType("Gateshead Hospitals NHS Foundation Trust", "640004", project);
	public static final GroupType group5 = new GroupType("Newcastle upon Tyne Hospitals NHS Foundation Trust", "640005", project);
	public static final GroupType group6 = new GroupType("Salford Royal Hospitals NHS Foundation Trust", "651001", project);
	public static final GroupType group7 = new GroupType("NW London Hospitals NHS Trust", "650001", project);
	public static final GroupType group8 = new GroupType("Hillingdon Hospital NHS Trust", "650002", project);
	public static final GroupType group9 = new GroupType("Royal Cornwall Healthcare NHS Trust", "652001", project);
	public static final GroupType group10 = new GroupType("Plymouth Hospitals NHS Trust", "652002", project);
	public static final GroupType group11 = new GroupType("Royal Devon and Exeter NHS Foundation Trust", "652003", project);
	public static final GroupType group12 = new GroupType("South Devon Healthcare NHS Foundation Trust", "652004", project);
	public static final GroupType group13 = new GroupType("Oxford Radcliffe Hospitals NHS Trust", "649001", project);
	public static final GroupType group14 = new GroupType("The Ipswich Hospital NHS Trust", "654001", project);
	public static final GroupType group15 = new GroupType("Barts and the London NHS Trust", "653001", project);
	public static final GroupType group16 = new GroupType("Whipps Cross University Hospital Trust", "653002", project);
	public static final GroupType group17 = new GroupType("Newham University Hospital NHS Trust", "653004", project);
	public static final GroupType group18 = new GroupType("Southend University Hospital NHS Foundation", "653003", project);
	public static final GroupType group19 = new GroupType("University Hospitals of Leicester NHS Trust", "655001", project);
	public static final GroupType group20 = new GroupType("Ealing Hospital", "650003", project);
	public static final GroupType group21 = new GroupType("Imperial College Healthcare NHS trust", "650004", project);
	public static final GroupType group22 = new GroupType("Chelsea and Westminister Hospital ", "650005", project);
	public static final GroupType group23 = new GroupType("West Middlesex University Hospitals", "650006", project);
	public static final GroupType group24 = new GroupType("Barnet and chase farm hospitals", "650007", project);
	public static final GroupType group25 = new GroupType("Central Manchester and Manchester Childrens Hospital", "651002", project);
	public static final GroupType group26 = new GroupType("The Royal Berkshire NHS foundation trust", "649005", project);
	public static final GroupType group27 = new GroupType("Buckinghamshire Hospitals NHS Trust", "649006", project);
	public static final GroupType group28 = new GroupType("Homerton University Hospital NHS Foundation Trust", "653005", project);
	public static final GroupType group29 = new GroupType("Broomfield Hospital", "653006", project);
	public static final GroupType group30 = new GroupType("Royal Blackburn Hospital", "651003", project);
	public static final GroupType group31 = new GroupType("Burnley General Hospital", "651004", project);
	public static final GroupType group32 = new GroupType("South Warwickshire General Hospital NHS Trust", "649007", project);
	public static final GroupType group33 = new GroupType("Norfolk and Norwich University Hospitals NHS Foundation Trust", "654004", project);
	public static final GroupType group34 = new GroupType("Aintree University Hopsital NHS Foundation Trust", "651005", project);
	public static final GroupType group35 = new GroupType("East Lancashire NHS trust", "651006", project);
	public static final GroupType group36 = new GroupType("St Helens and Knowsley Hospitals NHS", "651007", project);
	public static final GroupType group37 = new GroupType("University Hospital of South Manchester NHS Foundation Trust", "651008", project);
	public static final GroupType group38 = new GroupType("Wirral University Teaching Hospital NHS Foundation Trust", "651009", project);
	public static final GroupType group39 = new GroupType("West Hertfordshire hospital NHS trust", "650008", project);
	public static final GroupType group40 = new GroupType("Wycombe Hospital", "649002", project);
	public static final GroupType group41 = new GroupType("George Eliot Hospital", "649003", project);
	public static final GroupType group42 = new GroupType("Countess of Chester Hospital NHS Foundation Trust", "651010", project);
	public static final GroupType group43 = new GroupType("James Paget University Hospitals NHS Foundation Trust", "654005", project);
	public static final GroupType group44 = new GroupType("United Lincolnshire Hospitals NHS Trust", "655002", project);
	public static final GroupType group45 = new GroupType("Queen Elizabeth Hospital NHS Trust", "640006", project);
	public static final GroupType group46 = new GroupType("Newcastle Diabetes Centre", "640008", project);
	public static final GroupType group47 = new GroupType("South Tyneside Healthcare NHS Trust", "640009", project);
	public static final GroupType group48 = new GroupType("Northumbria Healthcare NHS Trust", "640010", project);
	public static final GroupType group49 = new GroupType("North Tees and Hartlepool NHS Foundation Trust", "640011", project);
	public static final GroupType group50 = new GroupType("Milton Keynes Hospital", "649008", project);
	public static final GroupType group51 = new GroupType("Pennine Acute Trust", "651011", project);
	public static final GroupType group52 = new GroupType("Durham and Darlington NHS Foundation Trust","640007", project);
	public static final GroupType group53 = new GroupType("The Queen Elizabeth Hospital King's Lynn","654006", project);
	public static final GroupType group54 = new GroupType("University Hospitals Coventry","649004", project);
	public static final GroupType group55 = new GroupType("Barking, Havering and Redbridge Hospitals NHS Trust", "653007", project);
	public static final GroupType group56 = new GroupType("Pennine Acute Trust - Oldham", "651012", project);
	public static final GroupType group57 = new GroupType("Stockport NHS Trust", "651013", project);
	public static final GroupType group58 = new GroupType("Lancashire Teaching Hospitals Trust", "651014", project);
	public static final GroupType group59 = new GroupType("Addenbrooke's Hospital", "654002", project);
	public static final GroupType group60 = new GroupType("Pendleton Gateway centre", "651015", project);
	public static final GroupType group61 = new GroupType("Wrightington, Wigan and Leigh NHS Foundation Trust", "651016", project);
	public static final GroupType group62 = new GroupType("University Hospitals of Morecambe Bay NHS Trust", "640012", project);
	public static final GroupType group63 = new GroupType("Blackpool, Fylde and Wyre Hospitals NHS Foundation Trust", "651017", project);
	public static final GroupType group64 = new GroupType("Central Manchester University Hospitals NHS Foundation Trust", "651018", project);
	public static final GroupType group65 = new GroupType("DEPRECATED", "651019", project); //This is actually Pennine Acute Trust in the repository, but was added in error TO THE REPOSITORY ONLY (not the aa or pa)
	public static final GroupType group66 = new GroupType("Harrogate & District Foundation NHS Trust", "640013", project);
	public static final GroupType group67 = new GroupType("Mid Yorkshire NHS", "640014", project);
	public static final GroupType group68 = new GroupType("Leeds Teaching Hospitals NHS", "640015", project);
	public static final GroupType group69 = new GroupType("Bradford Teaching Hospitals NHS Foundation", "640016", project);
	public static final GroupType group70 = new GroupType("Airedale NHS", "640017", project);
	public static final GroupType group71 = new GroupType("Medway NHS Foundation Trust", "653008", project);
	public static final GroupType group72 = new GroupType("Kettering General Hospital", "655003", project);
	public static final GroupType group73 = new GroupType("Dartford and Gravesham NHS Trust", "653009", project);
	public static final GroupType group74 = new GroupType("East Cheshire NHS Trust", "651020", project);
	public static final GroupType group75 = new GroupType("Warrington and Halton Hospitals NHS Foundation Trust", "651021", project);
	public static final GroupType group76 = new GroupType("North Wales West NHS Trust", "651022", project);



	static final GroupType[] gta = new GroupType[]{
    	group1, group2, group3, group4, group5, group6, group7, group8, group9, group10,
    	group11, group12, group13, group14, group15, group16, group17, group18, group19,
    	group20, group21, group22, group23, group24, group25, group26, group27, group28,
    	group29, group30, group31, group32, group33, group34, group35, group36, group37,
    	group38, group39, group40, group41, group42, group43, group44, group45, group46,
    	group47, group48, group49, group50, group51, group52, group53, group54, group55,
    	group56, group57, group58, group59, group60, group61, group62, group63, group64,
    	group65, group66, group67, group68, group69, group70, group71, group72, group73,
    	group74, group75, group76};


	public static GroupType[] allGroups(){
		return gta;
	}

	public static TargetType[] allAsTargets(){
		TargetType[] tta = new TargetType[gta.length];
		for(int i=0; i <gta.length; i++){
			tta[i]=new TargetType(gta[i].getName(),gta[i].getIdCode());
		}
		return tta;
	}

	public static PrivilegeType[] allAsPrivileges(){
		PrivilegeType[] tta = new PrivilegeType[gta.length];
		for(int i=0; i <gta.length; i++){
			tta[i]=new PrivilegeType(null, gta[i]);
		}
		return tta;
	}

	public static GroupType[] noGroups(){
		return new GroupType[]{};
	}}
