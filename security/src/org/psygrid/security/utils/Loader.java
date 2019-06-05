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


package org.psygrid.security.utils;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Load resources (or images) from various sources.
 * <p/>
 *
 * @author Davanum Srinivas (dims@yahoo.com).
 */
public class Loader {
    private static Log log = LogFactory.getLog(Loader.class.getName());

    /**
     * This method will search for <code>resource</code> in different
     * places. The rearch order is as follows:
     * <ol>
     * <p><li>Search for <code>resource</code> using the thread context
     * class loader under Java2. If that fails, search for
     * <code>resource</code> using the class loader that loaded this
     * class (<code>Loader</code>).
     * <p><li>Try one last time with
     * <code>ClassLoader.getSystemResource(resource)</code>, that is is
     * using the system class loader in JDK 1.2 and virtual machine's
     * built-in class loader in JDK 1.1.
     * </ol>
     * <p/>
     *
     * @param resource
     * @return
     */
    static public URL getResource(String resource) {
        ClassLoader classLoader = null;
        URL url = null;
        try {
            // We could not find resource. Ler us now try with the
            // classloader that loaded this class.
            classLoader = Loader.class.getClassLoader();
            if (classLoader != null) {
                log.debug("Trying to find [" + resource + "] using " + classLoader + " class loader.");
                url = classLoader.getResource(resource);
                if (url != null) {
                    return url;
                }
            }
        } catch (Throwable t) {
            log.warn("Caught Exception while in Loader.getResource. This may be innocuous.", t);
        }
    
        // Last ditch attempt: get the resource from the class path. It
        // may be the case that clazz was loaded by the Extentsion class
        // loader which the parent of the system class loader. Hence the
        // code below.
        log.debug("Trying to find [" + resource + "] using ClassLoader.getSystemResource().");
        return ClassLoader.getSystemResource(resource);
    }

    /**
     * Get the Thread context class loader.
     * <p/>
     *
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static ClassLoader getTCL() throws IllegalAccessException, InvocationTargetException {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * If running under JDK 1.2 load the specified class using the
     * <code>Thread</code> <code>contextClassLoader</code> if that
     * fails try Class.forname.
     * <p/>
     *
     * @param clazz
     * @return
     * @throws ClassNotFoundException
     */
    static public Class loadClass(String clazz) throws ClassNotFoundException {
        try {
            Class c = getTCL().loadClass(clazz);
            if (c != null)
                return c;
        } catch (Throwable e) {
        }
        // we reached here because tcl was null or because of a
        // security exception, or because clazz could not be loaded...
        // In any case we now try one more time
        return Class.forName(clazz);
    }
}
