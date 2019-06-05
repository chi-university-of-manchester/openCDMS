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


package org.psygrid.collection.entry;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URL;
import java.util.logging.LogManager;

import org.psygrid.collection.entry.persistence.PersistenceManager;

public class LogConfigurator {
    private final URL logPropertiesUrl;

    private static final String LOGGING_PROPERTIES = "org/psygrid/collection/entry/logging.properties"; //$NON-NLS-1$
    
    // We throw the exception here so that the LogManager (or any other caller) 
    // can catch it, report it and proceed to use the default configuration file.
    public LogConfigurator() throws IOException {
        logPropertiesUrl = Thread.currentThread().getContextClassLoader()
                .getResource(LOGGING_PROPERTIES);
        /*
         * Get the location of the user dir
         * 
         * See Bug #962
         */
        String home = PersistenceManager.getInstance().getHomeDir();
        String location = "\njava.util.logging.FileHandler.pattern="+home+"/log-%u.%g.log\n";
        location = location.replace("\\", "\\\\");
        
        InputStream in = logPropertiesUrl.openStream();
        
        /*
         * Append the location to the other properties
         */
        byte[] myStrBytes = location.getBytes();
        ByteArrayInputStream stringIS = new ByteArrayInputStream(myStrBytes);
        InputStream newIn = new SequenceInputStream(in, stringIS);

        
        
        LogManager.getLogManager().readConfiguration(newIn);
    }
}
