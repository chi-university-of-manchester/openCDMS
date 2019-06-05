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


package org.psygrid.securitymanager.security;

/**
 * Holds a hashedUserName and hashedPassword and provides a decent hashCode()
 * and equals() implementation.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 *
 */
public final class User {
    /* Not final to allow it to work with xStream normal mode */
    private String hashedUserName;

    /* Not final to allow it to work with xStream normal mode */
    private String hashedPassword;
    
    /**
     * Creates a user with the provided parameters.
     * 
     * @param hashedUserName The value to set the <code>hashedUserName</code>
     * property to.
     * @param hashedPassword The value to set the <code>hashedPassword</code>
     * property to.
     * @throws IllegalArgumentException if hashedUserName is null or if
     * hashedPassword is null.
     */
    public User(String hashedUserName, String hashedPassword) {
        if (hashedUserName == null) {
            throw new IllegalArgumentException("hashedUserName cannot be null."); //$NON-NLS-1$
        }
        if (hashedPassword == null) {
            throw new IllegalArgumentException("hashedPassword cannot be null"); //$NON-NLS-1$
        }
        this.hashedUserName = hashedUserName;
        this.hashedPassword = hashedPassword;
    }
    
    /**
     * Respects the <code>equals()</code> contract and returns whether both
     * string properties are equal.
     * 
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        
        if (o instanceof User == false) {
            return false;
        }
        
        User user = (User) o;
        
        return (this.hashedUserName.equals(user.hashedUserName)) &&
                (this.hashedPassword.equals(user.hashedPassword));
        
    }
    
    /**
     * Respects the <code>hashCode()</code> contract and returns a value
     * derived from both string properties.
     * 
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + hashedPassword.hashCode();
        result = PRIME * result + hashedUserName.hashCode();
        return result;
    }
    
    /**
     * @return the value of the <code>hashedPassword</code> property.
     */
    public final String getHashedPassword() {
        return hashedPassword;
    }

    /**
     * @return the value of the <code>hashedUserName</code> property.
     */
    public final String getHashedUserName() {
        return hashedUserName;
    }
}
