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


//Created on Oct 27, 2005 by John Ainsworth



package org.psygrid.security.attributeauthority.model;


/**
 * @author jda
 *
 */
public interface IUser extends IPersistent {
	
	/**
     * Get the userName
     * 
     * @return The userName.
     */
    public String getUserName();

    /**
     * Set the userName
     * 
     * @param userName The user name.
     */
    public void setUserName(String userName);
    
    /**
     * Set the passwordChangeRequired flag
     * 
     * @param tOrf The status of the flag.
     */
    public void setPasswordChangeRequired(boolean tOrf);
    
    /**
     * Get whether the user is dormant or not.
     * <p>
	 * If True, then the user has left and the User object only remains
	 * to prevent a new user being created with the same username.
     * 
     * @return Boolean, the user dormant flag.
     */
	public boolean isDormant();

	/**
     * Set whether the user is dormant or not.
     * <p>
	 * If True, then the user has left and the User object only remains
	 * to prevent a new user being created with the same username.
	 * 
	 * @param dormant Boolean, the user dormant flag.
	 */
	public void setDormant(boolean dormant);

}
