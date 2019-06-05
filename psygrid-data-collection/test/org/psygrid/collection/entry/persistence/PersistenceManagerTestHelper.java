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
package org.psygrid.collection.entry.persistence;

import java.io.File;
import java.io.IOException;

public class PersistenceManagerTestHelper {
    public static void initPersistenceManager(String userPrefix, boolean doLock) 
            throws FileLockException, IOException {
        PersistenceManager.getInstance().initBaseDir(doLock);
        PersistenceManager.getInstance().initUserLocations(userPrefix);
        PersistenceManager.getInstance().initUserDir();
    }
    
    public static void save(Object persistable, File location) throws
            IOException {
        PersistenceManager.getInstance().save(persistable, location.getAbsolutePath());
    }
    
    public static Object load(File location) throws IOException {
        return PersistenceManager.getInstance().load(location.getAbsolutePath());
    }
}
