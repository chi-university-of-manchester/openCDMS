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


package org.psygrid.collection.entry.remote;

/**
 * Holds a server and port and provides a decent hashCode()
 * and equals() implementation.
 * 
 * @author John Ainsworth
 *
 */
public final class ProxySetting {
    /* Not final to allow it to work with xStream normal mode */
    private String server;

    /* Not final to allow it to work with xStream normal mode */
    private String port;

    /* Not final to allow it to work with xStream normal mode */
    private String name;
    
    /* Not final to allow it to work with xStream normal mode */
    private String authenticationMethod;

    /* Not final to allow it to work with xStream normal mode */
    private String domain;
    
    private Boolean defaultProxy;
    
    /**
     * Creates a proxy setting with the provided parameters.
     * 
     * @param name The value to set the <code>name</code>
     * property to.
     * @param server The value to set the <code>server</code>
     * property to.
     * @param port The value to set the <code>port</code>
     * property to.
     * @param authenticationMethod The value to set the <code>authenticationMethod</code>
     * property to.
     * @param domain The value to set the <code>domain</code>
     * property to.
     * @throws IllegalArgumentException if server is null or if
     * port is null.
     */
    public ProxySetting(String name, String server, String port, String pam, String domain, Boolean defaultProxy) {
        if (server == null) {
            throw new IllegalArgumentException("server cannot be null."); //$NON-NLS-1$
        }
        if (port == null) {
            throw new IllegalArgumentException("port cannot be null"); //$NON-NLS-1$
        }
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null"); //$NON-NLS-1$
        }
        if (pam == null) {
            throw new IllegalArgumentException("auth method cannot be null"); //$NON-NLS-1$
        }
        if (defaultProxy == null) {
            throw new IllegalArgumentException("defaultProxy cannot be null"); //$NON-NLS-1$
        }
        this.name = name;
        this.server = server;
        this.port = port;
        this.authenticationMethod = pam;
        this.domain = domain;
        this.defaultProxy = defaultProxy;
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
        
        if (o instanceof ProxySetting == false) {
            return false;
        }
        
        ProxySetting proxy = (ProxySetting) o;
        
        return (this.server.equals(proxy.server) &&
                this.port.equals(proxy.port) && this.name.equals(proxy.name)
                && this.authenticationMethod.equals(proxy.authenticationMethod));
        
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
        result = PRIME * result + port.hashCode();
        result = PRIME * result + server.hashCode();
        result = PRIME * result + name.hashCode();
        result = PRIME * result + authenticationMethod.hashCode();
        return result;
    }
    
    /**
     * @return the value of the <code>port</code> property.
     */
    public final String getPort() {
        return port;
    }

    /**
     * @return the value of the <code>server</code> property.
     */
    public final String getServer() {
        return server;
    }
    /**
     * @return the value of the <code>name</code> property.
     */
    public final String getName() {
        return name;
    }

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param port The port to set.
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @param server The server to set.
	 */
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * @return Returns the domain.
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @param domain The domain to set.
	 */
	public void setDomain(String password) {
		this.domain = password;
	}

	/**
	 * @return Returns the authenticationMethod.
	 */
	public String getAuthenticationMethod() {
		return authenticationMethod;
	}

	/**
	 * @param authenticationMethod The authenticationMethod to set.
	 */
	public void setAuthenticationMethod(String pam) {
		this.authenticationMethod = pam;
	}

	public Boolean getDefaultProxy() {
		return defaultProxy;
	}

	public void setDefaultProxy(Boolean defaultProxy) {
		this.defaultProxy = defaultProxy;
	}
}
