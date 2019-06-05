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

package org.psygrid.data.repository.transformer;

import org.apache.axis.client.Call;

/**
 * Class to represent a single callable transformer web-service
 * client.
 * 
 * @author Rob Harper
 *
 */
public class TransformerClient {

    /**
     * The callable transformer web-service client.
     */
    private Call webService;
    
    /**
     * The fully qualified name of the class that the result of
     * calling the web-service will be stored in.
     */
    private String resultClass;
    
    /**
     * If true, the output of the transformation can be viewed
     * by humans; if false the output of the transformation should
     * not be viewed by humans.
     */
    private boolean viewableOutput;

    /**
     * Get the fully qualified name of the class that the result of
     * calling the web-service will be stored in.
     * 
     * @return The class name.
     */
    public String getResultClass() {
        return resultClass;
    }

    /**
     * Set the fully qualified name of the class that the result of
     * calling the web-service will be stored in.
     * 
     * @param resultClass The class name.
     */
    public void setResultClass(String resultClass) {
        this.resultClass = resultClass;
    }

    /**
     * Get the callable transformer web-service client.
     * 
     * @return The web-service client.
     */
    public Call getWebService() {
        return webService;
    }

    /**
     * Set the callable transformer web-service client.
     * 
     * @param webService The web-service client.
     */
    public void setWebService(Call webService) {
        this.webService = webService;
    }

    /**
     * Get the value of the viewable output flag.
     * <p>
     * If true, the output of the transformation can be viewed
     * by humans; if false the output of the transformation should
     * not be viewed by humans.
     * 
     * @return The viewable output flag.
     */
    public boolean isViewableOutput() {
        return viewableOutput;
    }

    /**
     * Set the value of the viewable output flag.
     * <p>
     * If true, the output of the transformation can be viewed
     * by humans; if false the output of the transformation should
     * not be viewed by humans.
     * 
     * @param viewableOutput The viewable output flag.
     */
    public void setViewableOutput(boolean viewableOutput) {
        this.viewableOutput = viewableOutput;
    }
    
}
