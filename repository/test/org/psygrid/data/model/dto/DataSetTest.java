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

package org.psygrid.data.model.dto;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataSetTest {

    @Test()
	public void testToHibernate(){
            DataSetDTO ds = new DataSetDTO();
            
            Date modified = new Date();
            ds.setDateModified(modified);
            
            BinaryObjectDTO info = new BinaryObjectDTO();
            Long infoId = new Long(2);
            info.setId(infoId);
            ds.setInfo(info);
            
            boolean published = true;
            ds.setPublished(published);
            
            String versionNo = "2.3.4";
            ds.setVersionNo(versionNo);
            
            ds.setAllConsentFormGroups(new ConsentFormGroupDTO[2]);
            ConsentFormGroupDTO cfg = new ConsentFormGroupDTO();
            Long cfgId = new Long(3);
            cfg.setId(cfgId);
            ds.getAllConsentFormGroups()[0] = cfg;
            ConsentFormGroupDTO cfg2 = new ConsentFormGroupDTO();
            Long cfg2Id = new Long(4);
            cfg2.setId(cfg2Id);
            ds.getAllConsentFormGroups()[1] = cfg2;

            ds.setValidationRules(new ValidationRuleDTO[2]);
            ValidationRuleDTO r1 = new TextValidationRuleDTO();
            Long r1Id = new Long(5);
            r1.setId(r1Id);
            ds.getValidationRules()[0] = r1;
            ValidationRuleDTO r2 = new TextValidationRuleDTO();
            Long r2Id = new Long(6);
            r2.setId(r2Id);
            ds.getValidationRules()[1] = r2;
            
            ds.setTransformers(new TransformerDTO[2]);
            TransformerDTO t1 = new TransformerDTO();
            Long t1Id = new Long(7);
            t1.setId(t1Id);
            ds.getTransformers()[0] = t1;
            TransformerDTO t2 = new TransformerDTO();
            Long t2Id = new Long(8);
            t2.setId(t2Id);
            ds.getTransformers()[1] = t2;
            
            String code = "Code";
            ds.setProjectCode(code);
            
            ds.setDocumentGroups(new DocumentGroupDTO[2]);
            DocumentGroupDTO dg1 = new DocumentGroupDTO();
            Long dg1Id = new Long(9);
            dg1.setId(dg1Id);
            ds.getDocumentGroups()[0] = dg1;
            DocumentGroupDTO dg2 = new DocumentGroupDTO();
            Long dg2Id = new Long(10);
            dg2.setId(dg2Id);
            ds.getDocumentGroups()[1] = dg2;
            
            ds.setUnits(new UnitDTO[2]);
            UnitDTO u1 = new UnitDTO();
            Long u1Id = new Long(11);
            u1.setId(u1Id);
            ds.getUnits()[0] = u1;
            UnitDTO u2 = new UnitDTO();
            Long u2Id = new Long(12);
            u2.setId(u2Id);
            ds.getUnits()[1] = u2;
            
            ds.setDocuments(new DocumentDTO[2]);
            DocumentDTO d1 = new DocumentDTO();
            Long d1Id = new Long(13);
            d1.setId(d1Id);
            ds.getDocuments()[0] = d1;
            DocumentDTO d2 = new DocumentDTO();
            Long d2Id = new Long(14);
            d2.setId(d2Id);
            ds.getDocuments()[1] = d2;
            
            ds.setDeletedObjects(new PersistentDTO[2]);
            TextEntryDTO te1 = new TextEntryDTO();
            Long te1Id = new Long(15);
            te1.setId(te1Id);
            ds.getDeletedObjects()[0] = te1;
            TextEntryDTO te2 = new TextEntryDTO();
            Long te2Id = new Long(16);
            te2.setId(te2Id);
            ds.getDeletedObjects()[1] = te2;
            
            ds.setStatuses(new StatusDTO[1]);
            ds.getStatuses()[0] = new StatusDTO();
            
            ds.setGroups(new GroupDTO[2]);
            GroupDTO grp1 = new GroupDTO();
            Long grp1Id = new Long(17);
            grp1.setId(grp1Id);
            ds.getGroups()[0] = grp1;
            GroupDTO grp2 = new GroupDTO();
            Long grp2Id = new Long(18);
            grp2.setId(grp2Id);
            ds.getGroups()[1] = grp2;
            
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
            
            Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
            org.psygrid.data.model.hibernate.DataSet hDS = ds.toHibernate(hRefs);
            
            AssertJUnit.assertNotNull("Hibernate dataset is null", hDS);
            AssertJUnit.assertEquals("Hibernate dataset has incorrect date modified",modified,hDS.getDateModified());
            AssertJUnit.assertNotNull("Hibernate dataset has null info sheet",hDS.getInfoSheet());
            AssertJUnit.assertEquals("Hibernate dataset has info sheet with the wrong id",infoId,hDS.getInfoSheet().getId());
            AssertJUnit.assertEquals("Hibernate dataset has the wrong published",published,hDS.isPublished());
            AssertJUnit.assertEquals("Hibernate dataset has the wrong version no",versionNo,hDS.getVersionNo());
            AssertJUnit.assertEquals("Hibernate dataset has the wrong number of consent form groups",ds.getAllConsentFormGroups().length, hDS.getAllConsentFormGroups().size());
            AssertJUnit.assertEquals("Hibernate dataset has consent form group with the wrong id at pos 0",cfgId,hDS.getAllConsentFormGroups().get(0).getId());
            AssertJUnit.assertEquals("Hibernate dataset has consent form group with the wrong id at pos 1",cfg2Id,hDS.getAllConsentFormGroups().get(1).getId());
            AssertJUnit.assertEquals("Hibernate dataset has the wrong number of validation rules",ds.getValidationRules().length, hDS.getValidationRules().size());
            AssertJUnit.assertEquals("Hibernate dataset has validation rule with the wrong id at pos 0",r1Id,hDS.getValidationRules().get(0).getId());
            AssertJUnit.assertEquals("Hibernate dataset has validation rule with the wrong id at pos 1",r2Id,hDS.getValidationRules().get(1).getId());
            AssertJUnit.assertEquals("Hibernate dataset has the wrong number of transformers",ds.getTransformers().length, hDS.getTransformers().size());
            AssertJUnit.assertEquals("Hibernate dataset has transformer with the wrong id at pos 0",t1Id,hDS.getTransformers().get(0).getId());
            AssertJUnit.assertEquals("Hibernate dataset has transformer with the wrong id at pos 1",t2Id,hDS.getTransformers().get(1).getId());
            AssertJUnit.assertEquals("Hibernate dataset has the wrong number of document groups",ds.getDocumentGroups().length, hDS.getDocumentGroups().size());
            AssertJUnit.assertEquals("Hibernate dataset has document group with the wrong id at pos 0",dg1Id,hDS.getDocumentGroups().get(0).getId());
            AssertJUnit.assertEquals("Hibernate dataset has document group with the wrong id at pos 1",dg2Id,hDS.getDocumentGroups().get(1).getId());
            AssertJUnit.assertEquals("Hibernate dataset has the wrong number of units",ds.getUnits().length, hDS.getUnits().size());
            AssertJUnit.assertEquals("Hibernate dataset has unit with the wrong id at pos 0",u1Id,hDS.getUnits().get(0).getId());
            AssertJUnit.assertEquals("Hibernate dataset has unit with the wrong id at pos 1",u2Id,hDS.getUnits().get(1).getId());
            AssertJUnit.assertEquals("Hibernate dataset has the wrong project code",code,hDS.getProjectCode());
            AssertJUnit.assertEquals("Hibernate dataset has the wrong number of documents",ds.getDocuments().length, hDS.getDocuments().size());
            AssertJUnit.assertEquals("Hibernate dataset has document with the wrong id at pos 0",d1Id,hDS.getDocuments().get(0).getId());
            AssertJUnit.assertEquals("Hibernate dataset has document with the wrong id at pos 1",d2Id,hDS.getDocuments().get(1).getId());
            AssertJUnit.assertEquals("Hibernate dataset has the wrong number of deleted objects",ds.getDeletedObjects().length, hDS.getDeletedObjects().size());
            AssertJUnit.assertEquals("Hibernate dataset has deleted object with the wrong id at pos 0",te1Id,hDS.getDeletedObjects().get(0).getId());
            AssertJUnit.assertEquals("Hibernate dataset has deleted object with the wrong id at pos 1",te2Id,hDS.getDeletedObjects().get(1).getId());
            AssertJUnit.assertEquals("Hibernate dataset has the wrong number of statuses",1,hDS.getStatuses().size());
            AssertJUnit.assertEquals("Hibernate dataset has the wrong number of groups",ds.getGroups().length, hDS.getGroups().size());
            AssertJUnit.assertEquals("Hibernate dataset has group with the wrong id at pos 0",grp1Id,hDS.getGroups().get(0).getId());
            AssertJUnit.assertEquals("Hibernate dataset has group with the wrong id at pos 1",grp2Id,hDS.getGroups().get(1).getId());
            AssertJUnit.assertEquals("Hibernate dataset has the wrong schedule start question",qu,hDS.getScheduleStartQuestion());
            AssertJUnit.assertEquals("Hibernate dataset has the wrong esl used flag",esl,hDS.isEslUsed());
            AssertJUnit.assertEquals("Hibernate dataset has the wrong randomization required flag",rnd,hDS.isRandomizationRequired());
            AssertJUnit.assertEquals("Hibernate dataset has the wrong send monthly flag",sendMonthly,hDS.isSendMonthlySummaries());
            AssertJUnit.assertEquals("Hibernate dataset has the wrong review reminder count",rrc,hDS.getReviewReminderCount());
            AssertJUnit.assertEquals("Hibernate dataset has the wrong primary project code",priCode,hDS.getPrimaryProjectCode());
            AssertJUnit.assertEquals("Hibernate dataset has the wrong secondary project code",secCode,hDS.getSecondaryProjectCode());
    }
}
