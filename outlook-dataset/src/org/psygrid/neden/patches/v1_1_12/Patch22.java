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


package org.psygrid.neden.patches.v1_1_12;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch22 extends AbstractPatch {

	@Override
	public String getName() {
		return "Prepare NEden dataset for dual data entry with Outlook";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		ds.setSecondaryProjectCode("OLK");

		Group grp001001 = ds.getGroup(0);
		if ( !"001001".equals(grp001001.getName()) ){
			throw new RuntimeException("This is not the 001001 group, it is "+grp001001.getName());
		}
		grp001001.addSecondaryGroup("008001");

		Group grp002001 = ds.getGroup(1);
		if ( !"002001".equals(grp002001.getName()) ){
			throw new RuntimeException("This is not the 002001 group, it is "+grp002001.getName());
		}
		grp002001.addSecondaryGroup("008001");

		Group grp003001 = ds.getGroup(2);
		if ( !"003001".equals(grp003001.getName()) ){
			throw new RuntimeException("This is not the 003001 group, it is "+grp003001.getName());
		}
		grp003001.addSecondaryGroup("008001");

		Group grp004001 = ds.getGroup(3);
		if ( !"004001".equals(grp004001.getName()) ){
			throw new RuntimeException("This is not the 004001 group, it is "+grp004001.getName());
		}
		grp004001.addSecondaryGroup("006003");

		Group grp004002 = ds.getGroup(4);
		if ( !"004002".equals(grp004002.getName()) ){
			throw new RuntimeException("This is not the 004002 group, it is "+grp004002.getName());
		}
		grp004002.addSecondaryGroup("006003");

		Group grp005001 = ds.getGroup(5);
		if ( !"005001".equals(grp005001.getName()) ){
			throw new RuntimeException("This is not the 005001 group, it is "+grp005001.getName());
		}
		grp005001.addSecondaryGroup("002001");

		Group grp006001 = ds.getGroup(6);
		if ( !"006001".equals(grp006001.getName()) ){
			throw new RuntimeException("This is not the 006001 group, it is "+grp006001.getName());
		}
		grp006001.addSecondaryGroup("002002");

		Group grp007001 = ds.getGroup(7);
		if ( !"007001".equals(grp007001.getName()) ){
			throw new RuntimeException("This is not the 007001 group, it is "+grp007001.getName());
		}
		grp007001.addSecondaryGroup("001001");

		Group grp007002 = ds.getGroup(8);
		if ( !"007002".equals(grp007002.getName()) ){
			throw new RuntimeException("This is not the 007002 group, it is "+grp007002.getName());
		}
		grp007002.addSecondaryGroup("001001");

		Group grp008001 = ds.getGroup(9);
		if ( !"008001".equals(grp008001.getName()) ){
			throw new RuntimeException("This is not the 008001 group, it is "+grp008001.getName());
		}
		grp008001.addSecondaryGroup("008001");

		Group grp004003 = ds.getGroup(10);
		if ( !"004003".equals(grp004003.getName()) ){
			throw new RuntimeException("This is not the 004003 group, it is "+grp004003.getName());
		}
		grp004003.addSecondaryGroup("006003");


		Document panss = ds.getDocument(1);
		if ( !"PANSS".equals(panss.getName()) ){
			throw new RuntimeException("This is not the PANSS document, it is "+panss.getName());
		}
		panss.setSecondaryDocIndex(6L);
		DocumentOccurrence panssBL = panss.getOccurrence(0);
		panssBL.setSecondaryOccIndex(0L);
		DocumentOccurrence panss6 = panss.getOccurrence(1);
		panss6.setSecondaryOccIndex(1L);
		DocumentOccurrence panss12 = panss.getOccurrence(2);
		panss12.setSecondaryOccIndex(2L);

		Document youngMania = ds.getDocument(2);
		if ( !"Young Mania".equals(youngMania.getName()) ){
			throw new RuntimeException("This is not the Young Mania document, it is "+youngMania.getName());
		}
		youngMania.setSecondaryDocIndex(7L);
		DocumentOccurrence youngManiaBL = youngMania.getOccurrence(0);
		youngManiaBL.setSecondaryOccIndex(0L);
		DocumentOccurrence youngMania6 = youngMania.getOccurrence(1);
		youngMania6.setSecondaryOccIndex(1L);
		DocumentOccurrence youngMania12 = youngMania.getOccurrence(2);
		youngMania12.setSecondaryOccIndex(2L);

		Document insight = ds.getDocument(3);
		if ( !"Insight Scale Scoring".equals(insight.getName()) ){
			throw new RuntimeException("This is not the Insight Scale Scoring document, it is "+insight.getName());
		}
		insight.setSecondaryDocIndex(18L);
		DocumentOccurrence insightBL = insight.getOccurrence(0);
		insightBL.setSecondaryOccIndex(0L);
		DocumentOccurrence insight12 = insight.getOccurrence(1);
		insight12.setSecondaryOccIndex(1L);

		Document calgary = ds.getDocument(4);
		if ( !"Calgary".equals(calgary.getName()) ){
			throw new RuntimeException("This is not the Calgary document, it is "+calgary.getName());
		}
		calgary.setSecondaryDocIndex(13L);
		DocumentOccurrence calgaryBL = calgary.getOccurrence(0);
		calgaryBL.setSecondaryOccIndex(0L);
		DocumentOccurrence calgary6 = calgary.getOccurrence(1);
		calgary6.setSecondaryOccIndex(1L);
		DocumentOccurrence calgary12 = calgary.getOccurrence(2);
		calgary12.setSecondaryOccIndex(2L);

		Document eis = ds.getDocument(5);
		if ( !"EIS Family History".equals(eis.getName()) ){
			throw new RuntimeException("This is not the EIS Family History document, it is "+eis.getName());
		}
		eis.setSecondaryDocIndex(14L);
		DocumentOccurrence eisBL = eis.getOccurrence(0);
		eisBL.setSecondaryOccIndex(0L);

		Document drugCheck = ds.getDocument(6);
		if ( !"Drug Check".equals(drugCheck.getName()) ){
			throw new RuntimeException("This is not the Drug Check document, it is "+drugCheck.getName());
		}
		drugCheck.setSecondaryDocIndex(10L);
		DocumentOccurrence drugCheckBL = drugCheck.getOccurrence(0);
		drugCheckBL.setSecondaryOccIndex(0L);
		DocumentOccurrence drugCheck12 = drugCheck.getOccurrence(1);
		drugCheck12.setSecondaryOccIndex(1L);

		Document gaf = ds.getDocument(7);
		if ( !"GAF Data Entry Sheet".equals(gaf.getName()) ){
			throw new RuntimeException("This is not the GAF Data Entry Sheet document, it is "+gaf.getName());
		}
		gaf.setSecondaryDocIndex(8L);
		DocumentOccurrence gafBL = gaf.getOccurrence(0);
		gafBL.setSecondaryOccIndex(0L);
		DocumentOccurrence gaf12 = gaf.getOccurrence(1);
		gaf12.setSecondaryOccIndex(1L);

		Document dup = ds.getDocument(9);
		if ( !"DUP".equals(dup.getName()) ){
			throw new RuntimeException("This is not the DUP document, it is "+dup.getName());
		}
		dup.setSecondaryDocIndex(9L);
		DocumentOccurrence dupBL = dup.getOccurrence(0);
		dupBL.setSecondaryOccIndex(0L);

		Document pathways = ds.getDocument(10);
		if ( !"PathwaysToCare".equals(pathways.getName()) ){
			throw new RuntimeException("This is not the PathwaysToCare document, it is "+pathways.getName());
		}
		pathways.setSecondaryDocIndex(11L);
		DocumentOccurrence pathwaysBL = pathways.getOccurrence(0);
		pathwaysBL.setSecondaryOccIndex(0L);

		Document premorbid = ds.getDocument(11);
		if ( !"Premorbid Summary Sheet".equals(premorbid.getName()) ){
			throw new RuntimeException("This is not the Premorbid Summary Sheet document, it is "+premorbid.getName());
		}
		premorbid.setSecondaryDocIndex(12L);
		DocumentOccurrence premorbidBL = premorbid.getOccurrence(0);
		premorbidBL.setSecondaryOccIndex(0L);

		Document adOutDetSH = ds.getDocument(12);
		if ( !"Adverse Outcomes Detailed Questionnaire (Self-Harm)".equals(adOutDetSH.getName()) ){
			throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Self-Harm) document, it is "+adOutDetSH.getName());
		}
		adOutDetSH.setSecondaryDocIndex(19L);
		DocumentOccurrence adOutDetSHSerious = adOutDetSH.getOccurrence(0);
		adOutDetSHSerious.setSecondaryOccIndex(0L);
		DocumentOccurrence adOutDetSHClosest = adOutDetSH.getOccurrence(1);
		adOutDetSHClosest.setSecondaryOccIndex(1L);

		Document adOutDetV = ds.getDocument(13);
		if ( !"Adverse Outcomes Detailed Questionnaire (Violence)".equals(adOutDetV.getName()) ){
			throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Violence) document, it is "+adOutDetV.getName());
		}
		adOutDetV.setSecondaryDocIndex(20L);
		DocumentOccurrence adOutDetVSerious = adOutDetV.getOccurrence(0);
		adOutDetVSerious.setSecondaryOccIndex(0L);
		DocumentOccurrence adOutDetVClosest = adOutDetV.getOccurrence(1);
		adOutDetVClosest.setSecondaryOccIndex(1L);

		Document timeUse = ds.getDocument(18);
		if ( !"Time Use Interview Score Sheet".equals(timeUse.getName()) ){
			throw new RuntimeException("This is not the Time Use Interview Score Sheet document, it is "+timeUse.getName());
		}
		timeUse.setSecondaryDocIndex(23L);
		DocumentOccurrence timeUseBL = timeUse.getOccurrence(0);
		timeUseBL.setSecondaryOccIndex(0L);
		DocumentOccurrence timeUse6 = timeUse.getOccurrence(1);
		timeUse6.setSecondaryOccIndex(1L);
		DocumentOccurrence timeUse12 = timeUse.getOccurrence(2);
		timeUse12.setSecondaryOccIndex(2L);

		Document cssriFU = ds.getDocument(20);
		if ( !"CSSRI".equals(cssriFU.getName()) ){
			throw new RuntimeException("This is not the CSSRI document, it is "+cssriFU.getName());
		}
		cssriFU.setSecondaryDocIndex(27L);
		//Note that the occurrence indices for CSSRI Follow Up are transposed between NED and OLK:
		//6 months is index 0 in NED and 1 in OLK; 12 months is index 1 in NED and 0 in OLK
		DocumentOccurrence cssriFU6 = cssriFU.getOccurrence(0);
		cssriFU6.setSecondaryOccIndex(1L);
		DocumentOccurrence cssriFU12 = cssriFU.getOccurrence(1);
		cssriFU12.setSecondaryOccIndex(0L);

		Document adOutScreen = ds.getDocument(21);
		if ( !"Adverse Outcomes Screening Questionnaire".equals(adOutScreen.getName()) ){
			throw new RuntimeException("This is not the Adverse Outcomes Screening Questionnaire document, it is "+adOutScreen.getName());
		}
		adOutScreen.setSecondaryDocIndex(26L);
		DocumentOccurrence adOutScreen6 = adOutScreen.getOccurrence(0);
		adOutScreen6.setSecondaryOccIndex(0L);
		DocumentOccurrence adOutScreen12 = adOutScreen.getOccurrence(1);
		adOutScreen12.setSecondaryOccIndex(1L);

		Document trtmntDoc = ds.getDocument(22);
		if ( !"TreatmentDocumentation".equals(trtmntDoc.getName()) ){
			throw new RuntimeException("This is not the TreatmentDocumentation document, it is "+trtmntDoc.getName());
		}
		trtmntDoc.setSecondaryDocIndex(0L);
		DocumentOccurrence trtmntDocOcc = trtmntDoc.getOccurrence(0);
		trtmntDocOcc.setSecondaryOccIndex(0L);

		Document relapse = ds.getDocument(23);
		if ( !"Relapse Rating".equals(relapse.getName()) ){
			throw new RuntimeException("This is not the Relapse Rating document, it is "+relapse.getName());
		}
		relapse.setSecondaryDocIndex(29L);
		DocumentOccurrence relapseOcc = relapse.getOccurrence(0);
		relapseOcc.setSecondaryOccIndex(0L);

		Document opcrit = ds.getDocument(24);
		if ( !"Opcrit Data Entry Sheet".equals(opcrit.getName()) ){
			throw new RuntimeException("This is not the Opcrit Data Entry Sheet document, it is "+opcrit.getName());
		}
		opcrit.setSecondaryDocIndex(30L);
		DocumentOccurrence opcritOcc = opcrit.getOccurrence(0);
		opcritOcc.setSecondaryOccIndex(0L);

	}

}
