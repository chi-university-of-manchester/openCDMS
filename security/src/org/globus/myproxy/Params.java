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
 * A generic class for representing basic parameters
 * needed for all MyProxy operations.
 */
public abstract class Params
    implements MyProxyConstants {

    private int command;

    protected String username;
    protected char[] passphrase;
    protected int lifetime;
    
    public Params(int command) {
	this.command = command;
    }

    public Params(int command,
		  String username,
		  char[] passphrase) {
	this.command = command;
	setUserName(username);
	setPassphrase(passphrase);
    }
    
    public void setUserName(String username) {
	this.username = username;
    }
    
    public String getUserName() {
	return this.username;
    }

    public void setPassphrase(char[] passphrase) {
	checkPassphrase(passphrase);
	this.passphrase = passphrase;
    }

    public char[] getPassphrase() {
	return this.passphrase;
    }

    public void setLifetime(int lifetime) {
	this.lifetime = lifetime;
    }
	
    public int getLifetime() {
	return this.lifetime;
    }

    protected void checkPassphrase(char[] passphrase) {
	if (passphrase == null) {
	    throw new IllegalArgumentException("Password is not specified");
	}
	if (passphrase.length < MIN_PASSWORD_LENGTH) {
	    throw new IllegalArgumentException("Password must be at least " +
					       MIN_PASSWORD_LENGTH + 
					       " characters long");
	}
    }

    /**
     * Serializes the parameters into a MyProxy request.
     * Subclasses should overwrite this function and
     * append the custom parameters to the output of
     * this function.
     */
    public String makeRequest() {
	StringBuffer buf = new StringBuffer();
	buf.append(VERSION).append(CRLF);
	buf.append(COMMAND).append(String.valueOf(command)).append(CRLF);
	buf.append(USERNAME).append(this.username).append(CRLF);
	buf.append(PASSPHRASE).append(getPassphrase()).append(CRLF);
	buf.append(LIFETIME).append(String.valueOf(lifetime)).append(CRLF);
	return buf.toString();
    }
    
    protected void add(StringBuffer buf, String prefix, String value) {
	if (value == null) {
	    return;
	}
	buf.append(prefix).append(value).append(CRLF);
    }

    public String toString() {
	return makeRequest();
    }

}
