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

package org.psygrid.securitymanager.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.LogManager;

public class LogConfigurator {
    private final URL logPropertiesUrl;

    private static final String LOGGING_PROPERTIES = "org/psygrid/securitymanager/properties/commons-logging.properties"; //$NON-NLS-1$
    
    // We throw the exception here so that the LogManager (or any other caller) 
    // can catch it, report it and proceed to use the default configuration file.
    public LogConfigurator() throws IOException {
        logPropertiesUrl = Thread.currentThread().getContextClassLoader()
                .getResource(LOGGING_PROPERTIES);
        InputStream in = logPropertiesUrl.openStream();
        LogManager.getLogManager().readConfiguration(in);
    }
}
