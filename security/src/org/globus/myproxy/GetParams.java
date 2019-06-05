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

package org.globus.myproxy;

/**
 * Holds the parameters for the <code>get</code> operation.
 */
public class GetParams
    extends Params {

    private String credentialName;

    public GetParams() {
	super(0);
    }

    public GetParams(String username, char[] passphrase) {
	super(0, username, passphrase);
    }

    public void setCredentialName(String credentialName) {
	this.credentialName = credentialName;
    }

    public String getCredentialName() {
	return this.credentialName;
    }

    public String makeRequest() {
	StringBuffer buf = new StringBuffer();
	buf.append(super.makeRequest());
	add(buf, CRED_NAME, credentialName);
	return buf.toString();
    }
    
}
