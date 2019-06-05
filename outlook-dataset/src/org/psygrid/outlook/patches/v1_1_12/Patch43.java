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


package org.psygrid.outlook.patches.v1_1_12;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.outlook.patches.AbstractPatch;

/**
 * @author Rob Harper
 *
 */
public class Patch43 extends AbstractPatch {

	@Override
	public String getName() {
		return "Prepare Outlook dataset for dual data entry with NEden";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		ds.setPrimaryProjectCode("NED");

		Document trtmntDoc = ds.getDocument(0);
		if ( !"TreatmentDocumentation".equals(trtmntDoc.getName()) ){
			throw new RuntimeException("This is not the TreatmentDocumentation document, it is "+trtmntDoc.getName());
		}
		trtmntDoc.setPrimaryDocIndex(22L);
		DocumentOccurrence trtmntDocOcc = trtmntDoc.getOccurrence(0);
		trtmntDocOcc.setPrimaryOccIndex(0L);

		Document panss = ds.getDocument(6);
		if ( !"PANSS".equals(panss.getName()) ){
			throw new RuntimeException("This is not the PANSS document, it is "+panss.getName());
		}
		panss.setPrimaryDocIndex(1L);
		DocumentOccurrence panssBL = panss.getOccurrence(0);
		panssBL.setPrimaryOccIndex(0L);
		DocumentOccurrence panss6 = panss.getOccurrence(1);
		panss6.setPrimaryOccIndex(1L);
		DocumentOccurrence panss12 = panss.getOccurrence(2);
		panss12.setPrimaryOccIndex(2L);

		Document youngMania = ds.getDocument(7);
		if ( !"Young Mania".equals(youngMania.getName()) ){
			throw new RuntimeException("This is not the Young Mania document, it is "+youngMania.getName());
		}
		youngMania.setPrimaryDocIndex(2L);
		DocumentOccurrence youngManiaBL = youngMania.getOccurrence(0);
		youngManiaBL.setPrimaryOccIndex(0L);
		DocumentOccurrence youngMania6 = youngMania.getOccurrence(1);
		youngMania6.setPrimaryOccIndex(1L);
		DocumentOccurrence youngMania12 = youngMania.getOccurrence(2);
		youngMania12.setPrimaryOccIndex(2L);

		Document gaf = ds.getDocument(8);
		if ( !"GAF Data Entry Sheet".equals(gaf.getName()) ){
			throw new RuntimeException("This is not the GAF Data Entry Sheet document, it is "+gaf.getName());
		}
		gaf.setPrimaryDocIndex(7L);
		DocumentOccurrence gafBL = gaf.getOccurrence(0);
		gafBL.setPrimaryOccIndex(0L);
		DocumentOccurrence gaf12 = gaf.getOccurrence(1);
		gaf12.setPrimaryOccIndex(1L);

		Document dup = ds.getDocument(9);
		if ( !"DUP".equals(dup.getName()) ){
			throw new RuntimeException("This is not the DUP document, it is "+dup.getName());
		}
		dup.setPrimaryDocIndex(9L);
		DocumentOccurrence dupBL = dup.getOccurrence(0);
		dupBL.setPrimaryOccIndex(0L);

		Document drugCheck = ds.getDocument(10);
		if ( !"Drug Check".equals(drugCheck.getName()) ){
			throw new RuntimeException("This is not the Drug Check document, it is "+drugCheck.getName());
		}
		drugCheck.setPrimaryDocIndex(6L);
		DocumentOccurrence drugCheckBL = drugCheck.getOccurrence(0);
		drugCheckBL.setPrimaryOccIndex(0L);
		DocumentOccurrence drugCheck12 = drugCheck.getOccurrence(1);
		drugCheck12.setPrimaryOccIndex(1L);

		Document pathways = ds.getDocument(11);
		if ( !"PathwaysToCare".equals(pathways.getName()) ){
			throw new RuntimeException("This is not the PathwaysToCare document, it is "+pathways.getName());
		}
		pathways.setPrimaryDocIndex(10L);
		DocumentOccurrence pathwaysBL = pathways.getOccurrence(0);
		pathwaysBL.setPrimaryOccIndex(0L);

		Document premorbid = ds.getDocument(12);
		if ( !"Premorbid Summary Sheet".equals(premorbid.getName()) ){
			throw new RuntimeException("This is not the Premorbid Summary Sheet document, it is "+premorbid.getName());
		}
		premorbid.setPrimaryDocIndex(11L);
		DocumentOccurrence premorbidBL = premorbid.getOccurrence(0);
		premorbidBL.setPrimaryOccIndex(0L);

		Document calgary = ds.getDocument(13);
		if ( !"Calgary".equals(calgary.getName()) ){
			throw new RuntimeException("This is not the Calgary document, it is "+calgary.getName());
		}
		calgary.setPrimaryDocIndex(4L);
		DocumentOccurrence calgaryBL = calgary.getOccurrence(0);
		calgaryBL.setPrimaryOccIndex(0L);
		DocumentOccurrence calgary6 = calgary.getOccurrence(1);
		calgary6.setPrimaryOccIndex(1L);
		DocumentOccurrence calgary12 = calgary.getOccurrence(2);
		calgary12.setPrimaryOccIndex(2L);

		Document eis = ds.getDocument(14);
		if ( !"EIS Family History".equals(eis.getName()) ){
			throw new RuntimeException("This is not the EIS Family History document, it is "+eis.getName());
		}
		eis.setPrimaryDocIndex(5L);
		DocumentOccurrence eisBL = eis.getOccurrence(0);
		eisBL.setPrimaryOccIndex(0L);

		Document insight = ds.getDocument(18);
		if ( !"Insight Scale Scoring".equals(insight.getName()) ){
			throw new RuntimeException("This is not the Insight Scale Scoring document, it is "+insight.getName());
		}
		insight.setPrimaryDocIndex(3L);
		DocumentOccurrence insightBL = insight.getOccurrence(0);
		insightBL.setPrimaryOccIndex(0L);
		DocumentOccurrence insight12 = insight.getOccurrence(1);
		insight12.setPrimaryOccIndex(1L);

		Document adOutDetSH = ds.getDocument(19);
		if ( !"Adverse Outcomes Detailed Questionnaire (Self-Harm)".equals(adOutDetSH.getName()) ){
			throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Self-Harm) document, it is "+adOutDetSH.getName());
		}
		adOutDetSH.setPrimaryDocIndex(12L);
		DocumentOccurrence adOutDetSHSerious = adOutDetSH.getOccurrence(0);
		adOutDetSHSerious.setPrimaryOccIndex(0L);
		DocumentOccurrence adOutDetSHClosest = adOutDetSH.getOccurrence(1);
		adOutDetSHClosest.setPrimaryOccIndex(1L);

		Document adOutDetV = ds.getDocument(20);
		if ( !"Adverse Outcomes Detailed Questionnaire (Violence)".equals(adOutDetV.getName()) ){
			throw new RuntimeException("This is not the Adverse Outcomes Detailed Questionnaire (Violence) document, it is "+adOutDetV.getName());
		}
		adOutDetV.setPrimaryDocIndex(13L);
		DocumentOccurrence adOutDetVSerious = adOutDetV.getOccurrence(0);
		adOutDetVSerious.setPrimaryOccIndex(0L);
		DocumentOccurrence adOutDetVClosest = adOutDetV.getOccurrence(1);
		adOutDetVClosest.setPrimaryOccIndex(1L);


		Document timeUse = ds.getDocument(23);
		if ( !"Time Use Interview Score Sheet".equals(timeUse.getName()) ){
			throw new RuntimeException("This is not the Time Use Interview Score Sheet document, it is "+timeUse.getName());
		}
		timeUse.setPrimaryDocIndex(18L);
		DocumentOccurrence timeUseBL = timeUse.getOccurrence(0);
		timeUseBL.setPrimaryOccIndex(0L);
		DocumentOccurrence timeUse6 = timeUse.getOccurrence(1);
		timeUse6.setPrimaryOccIndex(1L);
		DocumentOccurrence timeUse12 = timeUse.getOccurrence(2);
		timeUse12.setPrimaryOccIndex(2L);

		Document adOutScreen = ds.getDocument(26);
		if ( !"Adverse Outcomes Screening Questionnaire".equals(adOutScreen.getName()) ){
			throw new RuntimeException("This is not the Adverse Outcomes Screening Questionnaire document, it is "+adOutScreen.getName());
		}
		adOutScreen.setPrimaryDocIndex(21L);
		DocumentOccurrence adOutScreen6 = adOutScreen.getOccurrence(0);
		adOutScreen6.setPrimaryOccIndex(0L);
		DocumentOccurrence adOutScreen12 = adOutScreen.getOccurrence(1);
		adOutScreen12.setPrimaryOccIndex(1L);

		Document cssriFU = ds.getDocument(27);
		if ( !"CSSRI".equals(cssriFU.getName()) ){
			throw new RuntimeException("This is not the CSSRI document, it is "+cssriFU.getName());
		}
		cssriFU.setPrimaryDocIndex(20L);
		//Note that the occurrence indices for CSSRI Follow Up are transposed between NED and OLK:
		//6 months is index 0 in NED and 1 in OLK; 12 months is index 1 in NED and 0 in OLK
		DocumentOccurrence cssriFU12 = cssriFU.getOccurrence(0);
		cssriFU12.setPrimaryOccIndex(1L);
		DocumentOccurrence cssriFU6 = cssriFU.getOccurrence(1);
		cssriFU6.setPrimaryOccIndex(0L);

		Document relapse = ds.getDocument(29);
		if ( !"Relapse Rating".equals(relapse.getName()) ){
			throw new RuntimeException("This is not the Relapse Rating document, it is "+relapse.getName());
		}
		relapse.setPrimaryDocIndex(23L);
		DocumentOccurrence relapseOcc = relapse.getOccurrence(0);
		relapseOcc.setPrimaryOccIndex(0L);

		Document opcrit = ds.getDocument(30);
		if ( !"Opcrit Data Entry Sheet".equals(opcrit.getName()) ){
			throw new RuntimeException("This is not the Opcrit Data Entry Sheet document, it is "+opcrit.getName());
		}
		opcrit.setPrimaryDocIndex(24L);
		DocumentOccurrence opcritOcc = opcrit.getOccurrence(0);
		opcritOcc.setPrimaryOccIndex(0L);

	}

}
