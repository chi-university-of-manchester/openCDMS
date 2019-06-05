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

package org.psygrid.data.model.hibernate;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataSetTest {

    @Test()
	public void testToDTO(){
            DataSet ds = new DataSet();
            
            Date modified = new Date();
            ds.setDateModified(modified);
            
            BinaryObject info = new BinaryObject();
            Long infoId = new Long(12);
            info.setId(infoId);
            ds.setInfoSheet(info);
            
            boolean published = true;
            ds.setPublished(published);
            
            String versionNo = "2.3.4";
            ds.setVersionNo(versionNo);
            
            ConsentFormGroup cfg = new ConsentFormGroup();
            Long cfgId = new Long(98);
            cfg.setId(cfgId);
            ds.getAllConsentFormGroups().add(cfg);
            ConsentFormGroup cfg2 = new ConsentFormGroup();
            Long cfg2Id = new Long(98);
            cfg2.setId(cfg2Id);
            ds.getAllConsentFormGroups().add(cfg2);

            ValidationRule r1 = new TextValidationRule();
            Long r1Id = new Long(5);
            r1.setId(r1Id);
            ds.getValidationRules().add(r1);
            ValidationRule r2 = new TextValidationRule();
            Long r2Id = new Long(6);
            r2.setId(r2Id);
            ds.getValidationRules().add(r2);
            
            Transformer t1 = new Transformer();
            Long t1Id = new Long(7);
            t1.setId(t1Id);
            ds.getTransformers().add(t1);
            Transformer t2 = new Transformer();
            Long t2Id = new Long(8);
            t2.setId(t2Id);
            ds.getTransformers().add(t2);
            
            DocumentGroup dg1 = new DocumentGroup();
            Long dg1Id = new Long(9);
            dg1.setId(dg1Id);
            ds.getDocumentGroups().add(dg1);
            DocumentGroup dg2 = new DocumentGroup();
            Long dg2Id = new Long(10);
            dg2.setId(dg2Id);
            ds.getDocumentGroups().add(dg2);
            
            Unit u1 = new Unit();
            Long u1Id = new Long(11);
            u1.setId(u1Id);
            ds.getUnits().add(u1);
            Unit u2 = new Unit();
            Long u2Id = new Long(12);
            u2.setId(u2Id);
            ds.getUnits().add(u2);
            
            String code = "Code";
            ds.setProjectCode(code);
            
            Document d1 = new Document();
            Long d1Id = new Long(13);
            d1.setId(d1Id);
            ds.getDocuments().add(d1);
            Document d2 = new Document();
            Long d2Id = new Long(14);
            d2.setId(d2Id);
            ds.getDocuments().add(d2);
            
            TextEntry te1 = new TextEntry();
            Long te1Id = new Long(15);
            te1.setId(te1Id);
            ds.getDeletedObjects().add(te1);
            TextEntry te2 = new TextEntry();
            Long te2Id = new Long(16);
            te2.setId(te2Id);
            ds.getDeletedObjects().add(te2);
            
            ds.getStatuses().add(new Status());
            
            Group grp1 = new Group();
            Long grp1Id = new Long(13);
            grp1.setId(grp1Id);
            ds.getGroups().add(grp1);
            Group grp2 = new Group();
            Long grp2Id = new Long(14);
            grp2.setId(grp2Id);
            ds.getGroups().add(grp2);
            
            String qu = "Question";
            ds.setScheduleStartQuestion(qu);

            boolean esl = true;
            ds.setEslUsed(esl);
            
            boolean rnd = true;
            ds.setRandomizationRequired(rnd);
            
            boolean sendMonthly = true;
            ds.setSendMonthlySummaries(sendMonthly);
            
            int rrc = 25;
            ds.setReviewReminderCount(rrc);
            
            String priCode = "Primary";
            ds.setPrimaryProjectCode(priCode);
            
            String secCode = "Secondary";
            ds.setSecondaryProjectCode(secCode);
            
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.DataSetDTO dtoDS = ds.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
            
            AssertJUnit.assertNotNull("DTO dataset is null", dtoDS);
            AssertJUnit.assertEquals("DTO dataset has incorrect date modified",modified,dtoDS.getDateModified());
            AssertJUnit.assertNotNull("DTO dataset has null info sheet",dtoDS.getInfo());
            AssertJUnit.assertEquals("DTO dataset has info sheet with the wrong id",infoId,dtoDS.getInfo().getId());
            AssertJUnit.assertEquals("DTO dataset has the wrong published",published,dtoDS.isPublished());
            AssertJUnit.assertEquals("DTO dataset has the wrong version no",versionNo,dtoDS.getVersionNo());
            AssertJUnit.assertEquals("DTO dataset has the wrong number of consent form groups",ds.getAllConsentFormGroups().size(), dtoDS.getAllConsentFormGroups().length);
            AssertJUnit.assertEquals("DTO dataset has consent form group with the wrong id at pos 0",cfgId,dtoDS.getAllConsentFormGroups()[0].getId());
            AssertJUnit.assertEquals("DTO dataset has consent form group with the wrong id at pos 1",cfg2Id,dtoDS.getAllConsentFormGroups()[1].getId());
            AssertJUnit.assertEquals("DTO dataset has the wrong number of validation rules",ds.getValidationRules().size(), dtoDS.getValidationRules().length);
            AssertJUnit.assertEquals("DTO dataset has validation rule with the wrong id at pos 0",r1Id,dtoDS.getValidationRules()[0].getId());
            AssertJUnit.assertEquals("DTO dataset has validation rule with the wrong id at pos 1",r2Id,dtoDS.getValidationRules()[1].getId());
            AssertJUnit.assertEquals("DTO dataset has the wrong number of transformers",ds.getTransformers().size(), dtoDS.getTransformers().length);
            AssertJUnit.assertEquals("DTO dataset has transformer with the wrong id at pos 0",t1Id,dtoDS.getTransformers()[0].getId());
            AssertJUnit.assertEquals("DTO dataset has transformer with the wrong id at pos 1",t2Id,dtoDS.getTransformers()[1].getId());
            AssertJUnit.assertEquals("DTO dataset has the wrong number of document groups",ds.getDocumentGroups().size(), dtoDS.getDocumentGroups().length);
            AssertJUnit.assertEquals("DTO dataset has document group with the wrong id at pos 0",dg1Id,dtoDS.getDocumentGroups()[0].getId());
            AssertJUnit.assertEquals("DTO dataset has document group with the wrong id at pos 1",dg2Id,dtoDS.getDocumentGroups()[1].getId());
            AssertJUnit.assertEquals("DTO dataset has the wrong number of units",ds.getUnits().size(), dtoDS.getUnits().length);
            AssertJUnit.assertEquals("DTO dataset has unit with the wrong id at pos 0",u1Id,dtoDS.getUnits()[0].getId());
            AssertJUnit.assertEquals("DTO dataset has unit with the wrong id at pos 1",u2Id,dtoDS.getUnits()[1].getId());
            AssertJUnit.assertEquals("DTO dataset has the wrong project code",code,dtoDS.getProjectCode());
            AssertJUnit.assertEquals("DTO dataset has the wrong number of documents",ds.getDocuments().size(), dtoDS.getDocuments().length);
            AssertJUnit.assertEquals("DTO dataset has document with the wrong id at pos 0",d1Id,dtoDS.getDocuments()[0].getId());
            AssertJUnit.assertEquals("DTO dataset has document with the wrong id at pos 1",d2Id,dtoDS.getDocuments()[1].getId());
            AssertJUnit.assertEquals("DTO dataset has the wrong number of deleted objects",ds.getDeletedObjects().size(), dtoDS.getDeletedObjects().length);
            AssertJUnit.assertEquals("DTO dataset has deleted object with the wrong id at pos 0",te1Id,dtoDS.getDeletedObjects()[0].getId());
            AssertJUnit.assertEquals("DTO dataset has deleted object with the wrong id at pos 1",te2Id,dtoDS.getDeletedObjects()[1].getId());
            AssertJUnit.assertEquals("DTO dataset has the wrong number of statuses",1,dtoDS.getStatuses().length);
            AssertJUnit.assertEquals("DTO dataset has the wrong number of groups",ds.getGroups().size(), dtoDS.getGroups().length);
            AssertJUnit.assertEquals("DTO dataset has group with the wrong id at pos 0",d1Id,dtoDS.getGroups()[0].getId());
            AssertJUnit.assertEquals("DTO dataset has group with the wrong id at pos 1",d2Id,dtoDS.getGroups()[1].getId());
            AssertJUnit.assertEquals("DTO dataset has the wrong schedule start question",qu,dtoDS.getScheduleStartQuestion());
            AssertJUnit.assertEquals("DTO dataset has the wrong esl used flag",esl,dtoDS.isEslUsed());
            AssertJUnit.assertEquals("DTO dataset has the wrong randomization required flag",rnd,dtoDS.isRandomizationRequired());
            AssertJUnit.assertEquals("DTO dataset has the wrong send monthly flag",sendMonthly,dtoDS.isSendMonthlySummaries());
            AssertJUnit.assertEquals("DTO dataset has the wrong review reminder count",rrc,dtoDS.getReviewReminderCount());
            AssertJUnit.assertEquals("DTO dataset has the wrong primary project code",priCode,dtoDS.getPrimaryProjectCode());
            AssertJUnit.assertEquals("DTO dataset has the wrong secondary project code",secCode,dtoDS.getSecondaryProjectCode());
    }
}
