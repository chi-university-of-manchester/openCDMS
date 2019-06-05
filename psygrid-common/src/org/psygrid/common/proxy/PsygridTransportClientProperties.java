package org.psygrid.common.proxy;

import org.apache.axis.AxisProperties;
import org.apache.axis.components.net.DefaultHTTPSTransportClientProperties;

public class PsygridTransportClientProperties extends
		DefaultHTTPSTransportClientProperties {
	
	public enum PropertyType{
		http,
		https
	};
	
	private final static String emptyString = "";
	
	private static PropertyType protocol;
	
	static {
		protocol = PropertyType.https;
	}
	
	public static void setProperty(PropertyType type){
		protocol = type;
	}
	
	private static String getFullPropertyName(String propertySuffix){
		return protocol.toString().concat(".").concat(propertySuffix);
	}
	
    public String getProxyHost() {

      String propertyName = getFullPropertyName("proxyHost");	
      proxyHost = AxisProperties.getProperty(propertyName);
      if (proxyHost == null){
          proxyHost = "";
      }
      return proxyHost;
    }
    

    /**
     * @see org.apache.axis.components.net.TransportClientProperties#getNonProxyHosts()
     */
    public String getNonProxyHosts() {
    	
    	String propertyName = getFullPropertyName("nonProxyHosts");
        
        nonProxyHosts = AxisProperties.getProperty(propertyName);
        
        if (nonProxyHosts == null)
            nonProxyHosts = emptyString;
   
        return nonProxyHosts;
    }

    /**
     * @see org.apache.axis.components.net.TransportClientProperties#getPort()
     */
    public String getProxyPort() {
     
    	String propertyName = getFullPropertyName("proxyPort");
    	
        proxyPort = AxisProperties.getProperty(propertyName);
        
        if (proxyPort == null)
            proxyPort = emptyString;
        
        return proxyPort;
    }

    /**
     * @see org.apache.axis.components.net.TransportClientProperties#getProxyUser()
     */
    public String getProxyUser() {
    	
    	String propertyName = getFullPropertyName("proxyUser");
 
        proxyUser = AxisProperties.getProperty(propertyName);
        if (proxyUser == null)
            proxyUser = emptyString;
        
        return proxyUser;
    }

    /**
     * @see org.apache.axis.components.net.TransportClientProperties#getProxyPassword()
     */
    public String getProxyPassword() {
    	
    	String propertyName = getFullPropertyName("proxyPassword");

        proxyPassword = AxisProperties.getProperty(propertyName);
        if (proxyPassword == null)
            proxyPassword = emptyString;
        
    	return proxyPassword;
    }
}