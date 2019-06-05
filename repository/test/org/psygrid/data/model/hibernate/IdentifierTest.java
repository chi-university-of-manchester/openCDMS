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
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;

public class IdentifierTest {

    @Test()
	public void testToDTO(){
            Identifier identifier = new Identifier();
            Long id = new Long(2);
            identifier.setId(id);
            String idText = "foo";
            identifier.setIdentifier(idText);
            int suffix = 4;
            identifier.setSuffix(suffix);
            String groupPrefix = "Group";
            identifier.setGroupPrefix(groupPrefix);
            String projectPrefix = "Project";
            identifier.setProjectPrefix(projectPrefix);
            String user = "User";
            identifier.setUser(user);
            Date created = new Date();
            identifier.setCreated(created);
                        
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
            org.psygrid.data.model.dto.IdentifierDTO dtoI = identifier.toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);

            AssertJUnit.assertNotNull("Hibernate identifier is null", dtoI);
            AssertJUnit.assertEquals("Hibernate identifier has the wrong id",id,dtoI.getId());
            AssertJUnit.assertEquals("Hibernate identifier has the wrong identifier",idText,dtoI.getIdentifier());
            AssertJUnit.assertEquals("Hibernate identifier has the wrong suffix",suffix,dtoI.getSuffix());
            AssertJUnit.assertEquals("Hibernate identifier has the wrong group prefix",groupPrefix,dtoI.getGroupPrefix());
            AssertJUnit.assertEquals("Hibernate identifier has the wrong project prefix",projectPrefix,dtoI.getProjectPrefix());
            AssertJUnit.assertEquals("Hibernate identifier has the wrong user",user,dtoI.getUser());
            AssertJUnit.assertEquals("Hibernate identifier has the wrong created",created,dtoI.getCreated());
    }
    
    @Test()
	public void testInitialize_1Arg(){
            Identifier identifier = new Identifier();
            
            String overallIdentifier = "ABC"+IdentifierHelper.PROJ_GRP_SEPARATOR+"123"+IdentifierHelper.GRP_SUFF_SEPARATOR+"000003";
        
            identifier.initialize(overallIdentifier);
            
            AssertJUnit.assertEquals("Overall identifier is not correct", overallIdentifier, identifier.getIdentifier());
            AssertJUnit.assertEquals("Project prefix is not correct", "ABC", identifier.getProjectPrefix());
            AssertJUnit.assertEquals("Group prefix is not correct", "123", identifier.getGroupPrefix());
            AssertJUnit.assertEquals("Suffix is not correct", 3, identifier.getSuffix());
    }
    
    @Test()
	public void testInitialize_1Arg_Invalid(){
            Identifier identifier = new Identifier();
            
            try{
                //No proj grp separator
                identifier.initialize("ABC|123-000003");
                Assert.fail("Exception should have been thrown when trying to initialize the identifier with a string that doesn't contain the correct project-group separator");
            }
            catch(InvalidIdentifierException ex){
                //do nothing
            }

            try{
                //No grp suff separator
                identifier.initialize("ABC/123_000003");
                Assert.fail("Exception should have been thrown when trying to initialize the identifier with a string that doesn't contain the correct group-suffix separator");
            }
            catch(InvalidIdentifierException ex){
                //do nothing
            }
            
            try{
                //Suffix not a number
                identifier.initialize("ABC/123-00000A");
                Assert.fail("Exception should have been thrown when trying to initialize the identifier with a string that doesn't contain a numeric suffix");
            }
            catch(InvalidIdentifierException ex){
                //do nothing
            }
            
            try{
                //separators the wrong way around
                identifier.initialize("ABC"+IdentifierHelper.GRP_SUFF_SEPARATOR+"123"+IdentifierHelper.PROJ_GRP_SEPARATOR+"000005");
                Assert.fail("Exception should have been thrown when trying to initialize the identifier with a string that has the separators the wrong way around");
            }
            catch(InvalidIdentifierException ex){
                //do nothing
            }
            
            try{
                //two project/group separators
                identifier.initialize("ABC"+IdentifierHelper.PROJ_GRP_SEPARATOR+"123"+IdentifierHelper.PROJ_GRP_SEPARATOR+"000005");
                Assert.fail("Exception should have been thrown when trying to initialize the identifier with a string that has two project/group separators");
            }
            catch(InvalidIdentifierException ex){
                //do nothing
            }
            
            try{
                //two group/suffix separators
                identifier.initialize("ABC"+IdentifierHelper.GRP_SUFF_SEPARATOR+"123"+IdentifierHelper.GRP_SUFF_SEPARATOR+"000005");
                Assert.fail("Exception should have been thrown when trying to initialize the identifier with a string that has two group/suffix separators");
            }
            catch(InvalidIdentifierException ex){
                //do nothing
            }            
    }
    
    @Test()
	public void testInitialize_4Args(){
            Identifier identifier = new Identifier();
            
            String projectPrefix = "ABC";
            String groupPrefix = "123";
            int suffix = 256;
            int suffixSize = 6;
            
            identifier.initialize(projectPrefix, groupPrefix, suffix, suffixSize);
            String id = identifier.getIdentifier();
            
            AssertJUnit.assertNotNull("Overall identifier is null", id);
            AssertJUnit.assertEquals("Overall identifier has incorrect project prefix", projectPrefix, id.substring(0,3));
            AssertJUnit.assertEquals("Overall identifier has incorrect project group separator", IdentifierHelper.PROJ_GRP_SEPARATOR, id.substring(3,4));
            AssertJUnit.assertEquals("Overall identifier has incorrect group prefix", groupPrefix, id.substring(4,7));
            AssertJUnit.assertEquals("Overall identifier has incorrect group suffix separator", IdentifierHelper.GRP_SUFF_SEPARATOR, id.substring(7,8));
            String idSuffix = id.substring(8);
            AssertJUnit.assertEquals("Overall identifier has incorrect suffix length", suffixSize, idSuffix.length());
            AssertJUnit.assertEquals("Overall identifier has incorrect suffix", suffix, Integer.parseInt(idSuffix));
    }
    
}
