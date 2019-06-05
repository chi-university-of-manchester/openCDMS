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


package org.psygrid.securitymanager;

import java.awt.Component;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.rmi.RemoteException;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXErrorDialog;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.attributeauthority.service.InputFaultMessage;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.attributeauthority.service.ProcessingFaultMessage;
import org.psygrid.securitymanager.security.RemoteServiceFault;
import org.psygrid.securitymanager.utils.PropertiesHelper;



public class ExceptionsHelper {
    private final static Log LOG = LogFactory.getLog(ExceptionsHelper.class);

    public static void handleRemoteException(Component parentComponent,
            RemoteException e) {
        String title = PropertiesHelper.getPropertyHelper().getStringFor("noConnectionTitle"); //$NON-NLS-1$
        String message = PropertiesHelper.getPropertyHelper().getStringFor("noConnectionMessage"); //$NON-NLS-1$
        handleException(parentComponent, title, e, message, false);
    }
    
    public static void handleNotAuthorisedFault(Component parentComponent, 
    		NotAuthorisedFaultMessage nafm)
    {
        String title = PropertiesHelper.getPropertyHelper().getStringFor("notAuthorisedTitle"); //$NON-NLS-1$
        String message = PropertiesHelper.getPropertyHelper().getStringFor("notAuthorisedMessage"); //$NON-NLS-1$
        handleException(parentComponent, title, nafm, message, false);
    }

    public static void handleNotAuthorisedFault(Component parentComponent, 
    		org.psygrid.security.policyauthority.service.NotAuthorisedFaultMessage nafm)
    {
        String title = PropertiesHelper.getPropertyHelper().getStringFor("notAuthorisedTitle"); //$NON-NLS-1$
        String message = PropertiesHelper.getPropertyHelper().getStringFor("notAuthorisedMessage"); //$NON-NLS-1$
        handleException(parentComponent, title, nafm, message, false);
    }
    
    public static void handleInputFaultMessage(Component parentComponent,
    		InputFaultMessage ifm)
    {
        String title = PropertiesHelper.getPropertyHelper().getStringFor("inputFaultTitle"); //$NON-NLS-1$
        String message = PropertiesHelper.getPropertyHelper().getStringFor("inputFaultMessage"); //$NON-NLS-1$
        handleException(parentComponent, title, ifm, message, false);
    }
    
    public static void handlePGSecurityException(Component parentComponent,
    						PGSecurityException pgse)
    {
    	String title = PropertiesHelper.getPropertyHelper().getStringFor("securityException");
    	String message = PropertiesHelper.getPropertyHelper().getStringFor("securityExceptionMessage");
    	handleException(parentComponent, title, pgse, message, false);
    }
    
    public static void handleProcessingFaultMessage(Component parentComponent,
    						ProcessingFaultMessage pfm)
    {
        String title = PropertiesHelper.getPropertyHelper().getStringFor("processingFaultTitle"); //$NON-NLS-1$
        String message = PropertiesHelper.getPropertyHelper().getStringFor("processingFaultMessage"); //$NON-NLS-1$
        handleException(parentComponent, title, pfm, message, false);
    }
    
    public static void handleConnectException(Component parentComponent,
            ConnectException e) {
        String title = PropertiesHelper.getPropertyHelper().getStringFor("noConnectionTitle"); //$NON-NLS-1$
        String message = PropertiesHelper.getPropertyHelper().getStringFor("noConnectionMessage"); //$NON-NLS-1$
        handleException(parentComponent, title, e, message, false);
    }

    public static void handleSocketTimeoutException(Component parentComponent,
            SocketTimeoutException e) {
        String title = PropertiesHelper.getPropertyHelper().getStringFor("socketTimeoutTitle"); //$NON-NLS-1$
        String message = PropertiesHelper.getPropertyHelper().getStringFor("socketTimeoutMessage"); //$NON-NLS-1$
        handleException(parentComponent, title, e, message, false);
    }

    public static void handleRemoteServiceFault(Component parentComponent, 
            RemoteServiceFault e) {
        String title = PropertiesHelper.getPropertyHelper().getStringFor("serverTitle"); //$NON-NLS-1$
        String message = PropertiesHelper.getPropertyHelper().getStringFor("serverError"); //$NON-NLS-1$
        handleException(parentComponent, title, e, message, false);
    }

    public static void handleIOException(Component parentComponent, IOException e,
            boolean fatal) {
        String title = PropertiesHelper.getPropertyHelper().getStringFor("diskErrorTitle"); //$NON-NLS-1$
        String message;
        if (fatal) {
            message = PropertiesHelper.getPropertyHelper().getStringFor("fatalDiskErrorMessage"); //$NON-NLS-1$
        }
        else {
            message = PropertiesHelper.getPropertyHelper().getStringFor("diskErrorMessage"); //$NON-NLS-1$
        }
        handleException(parentComponent, title, e, message, fatal);
    }
    
    public static String getCommitPrefixMessage(boolean singleRecord) {
        if (!singleRecord) {
            return "More than one record did not commit successfully. The details about one of them follow.\n\n";
        }
        return ""; //$NON-NLS-1$
    }
    
    public static String getCommitSuffixMessage(String recordIdentifier) {
        if (recordIdentifier != null) {
            return "\nRecord identifier: " + recordIdentifier;
        }
        return ""; //$NON-NLS-1$
    }

    public static void wrapIntoRuntimeExceptionAndThrow(Exception e) {
        RuntimeException re = new RuntimeException(e);
        // Our default handler should catch the RuntimeException and log it,
        // but we log it here to make sure we don't lose information, since
        // there are some cases where the default handler doesn't behave as
        // it should. Better to have duplicated logs than missing ones
        if (LOG.isFatalEnabled()) {
            LOG.fatal(re.getMessage(), re);
        }
        throw re;
    }
    
    public static void handleException(Component parentComponent, String title,
            Throwable e, String message, boolean fatal) {
        if (fatal && LOG.isFatalEnabled()) {
            LOG.fatal(message, e);
            
            //try to email logs to support
            try{
//                RemoteManager.getInstance().emailLogFileToSupport();
            }
            catch(Exception ex){
                //do nothing - if an exception is thrown when trying to
                //email the logs then we just have to live with getting
                //the user to email them to us manually
            }
            
            JXErrorDialog.showDialog(parentComponent, title, message, e);
        }
        else if (LOG.isErrorEnabled()) {
            LOG.error(message, e);
            JOptionPane.showMessageDialog(parentComponent, message, title, 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void handleInterruptedException(InterruptedException e) {
        if (LOG.isWarnEnabled()) {
            LOG.warn(e.getMessage(), e);
        }
    }

    public static void handleUnknownException(Throwable e) throws RuntimeException  {
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        }
        throw new RuntimeException(e);        
    }
    
}
