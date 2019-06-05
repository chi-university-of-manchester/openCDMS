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

package org.psygrid.common.sms;

/**
 * Settings for sending SMS messages using a remote service.
 * 
 * @author Lucy Bridges
 *
 */
public class SMSSettings {

	private String serviceURL;
	private String serviceUsername;
	private String servicePassword;
	
	private String proxy; 
	private String proxyPort;
	private String proxyAuthType;
	private String proxyWindowsDomain;
	private String proxyUsername;
	private String proxyPassword;
	private String proxyRealm;
	
	
	public String getProxy() {
		return proxy;
	}
	public void setProxy(String proxy) {
		this.proxy = proxy;
	}
	public String getProxyPort() {
		return proxyPort;
	}
	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}
	public String getProxyAuthType() {
		return proxyAuthType;
	}
	public void setProxyAuthType(String proxyAuthType) {
		this.proxyAuthType = proxyAuthType;
	}
	public String getProxyPassword() {
		return proxyPassword;
	}
	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}
	public String getProxyUsername() {
		return proxyUsername;
	}
	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}
	public String getProxyWindowsDomain() {
		return proxyWindowsDomain;
	}
	public void setProxyWindowsDomain(String proxyWindowsDomain) {
		this.proxyWindowsDomain = proxyWindowsDomain;
	}
	public String getServicePassword() {
		return servicePassword;
	}
	public void setServicePassword(String servicePassword) {
		this.servicePassword = servicePassword;
	}
	public String getServiceURL() {
		return serviceURL;
	}
	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}
	public String getServiceUsername() {
		return serviceUsername;
	}
	public void setServiceUsername(String serviceUsername) {
		this.serviceUsername = serviceUsername;
	}
	public String getProxyRealm() {
		return proxyRealm;
	}
	public void setProxyRealm(String proxyRealm) {
		this.proxyRealm = proxyRealm;
	}
	
}
