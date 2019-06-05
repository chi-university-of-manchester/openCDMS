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


package org.psygrid.collection.entry;

import java.awt.Component;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.jdic.desktop.DesktopException;
import org.jdesktop.swingx.JXErrorDialog;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.renderer.RendererHelper;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.ui.NoNetworkDialog;
import org.psygrid.data.repository.DuplicateDocumentsFault;
import org.psygrid.data.repository.RepositoryInvalidIdentifierFault;
import org.psygrid.data.repository.RepositoryNoConsentFault;
import org.psygrid.data.repository.RepositoryOutOfDateFault;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.repository.transformer.TransformerFault;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.esl.services.ESLDuplicateObjectFault;
import org.psygrid.esl.services.ESLOutOfDateFault;
import org.psygrid.esl.services.ESLSubjectExistsException;
import org.psygrid.esl.services.RandomisationException;

public class ExceptionsHelper {
    private final static Log LOG = LogFactory.getLog(ExceptionsHelper.class);
    
    public static void handleConnectException(Component parentComponent,
            ConnectException e) {
        String title = EntryMessages.getString("noConnectionTitle"); //$NON-NLS-1$
        String message = EntryMessages.getString("noConnectionMessage"); //$NON-NLS-1$
        if (!showNoNetworkDialog(parentComponent))
            handleException(parentComponent, title, e, message, false);
    }

    public static void handleConnectException(Component parentComponent,
            ConnectException e, String subTitle) {
        String title = EntryMessages.getString("noConnectionTitle"); //$NON-NLS-1$
        String message = EntryMessages.getString("noConnectionMessage"); //$NON-NLS-1$
        if (!showNoNetworkDialog(parentComponent))
            handleException(parentComponent, title, e, subTitle+"\n\n"+message, false);
    }

    private static boolean showNoNetworkDialog(Component parentComponent) {
        //if in DSD mode, do not show no network dialog
    	if (!PersistenceManager.getInstance().isDsdMode() &&
        		PersistenceManager.getInstance().getData().isAlwaysOnlineMode()) {
            new NoNetworkDialog((Application) RendererHelper.getInstance().findJFrame(parentComponent))
                .setVisible(true);
            return true;
        }
        return false;
    }

    public static void handleSocketTimeoutException(Component parentComponent,
            SocketTimeoutException e) {
        String title = EntryMessages.getString("socketTimeoutTitle"); //$NON-NLS-1$
        String message = EntryMessages.getString("socketTimeoutMessage"); //$NON-NLS-1$
        if (!showNoNetworkDialog(parentComponent))
            handleException(parentComponent, title, e, message, false);
    }

    public static void handleSocketTimeoutException(Component parentComponent,
            SocketTimeoutException e, String subTitle) {
        String title = EntryMessages.getString("socketTimeoutTitle"); //$NON-NLS-1$
        String message = EntryMessages.getString("socketTimeoutMessage"); //$NON-NLS-1$
        if (!showNoNetworkDialog(parentComponent))
            handleException(parentComponent, title, e, subTitle+"\n\n"+message, false);
    }

    public static void handleEntrySAMLException(Component parentComponent, 
            EntrySAMLException e) {
        String title = EntryMessages.getString("securitySystemTitle"); //$NON-NLS-1$
        String message = EntryMessages.getString("securitySystemMessage"); //$NON-NLS-1$
        handleException(parentComponent, title, e, message, false);
    }
    
    public static void handleEntrySAMLException(Component parentComponent, 
            EntrySAMLException e, String subTitle) {
        String title = EntryMessages.getString("securitySystemTitle"); //$NON-NLS-1$
        String message = EntryMessages.getString("securitySystemMessage"); //$NON-NLS-1$
        handleException(parentComponent, title, e, subTitle+"\n\n"+message, false);
    }
    
    public static void handleRemoteServiceFault(Component parentComponent, 
            RemoteServiceFault e) {
        String title = EntryMessages.getString("serverProblem"); //$NON-NLS-1$
        String message = EntryMessages.getString("serverError"); //$NON-NLS-1$
        handleException(parentComponent, title, e, message, false);
    }

    public static void handleRemoteServiceFault(Component parentComponent, 
            RemoteServiceFault e, String subTitle) {
        String title = EntryMessages.getString("serverProblem"); //$NON-NLS-1$
        String message = EntryMessages.getString("serverError"); //$NON-NLS-1$
        handleException(parentComponent, title, e, subTitle+"\n\n"+message, false);
    }

    public static void handleServiceFault(Component parentComponent, Throwable cause) {
        String title = EntryMessages.getString("serverProblem"); //$NON-NLS-1$
        String message = EntryMessages.getString("serverError"); //$NON-NLS-1$
        handleException(parentComponent, title, cause, message, false);
    }
    
    public static void handleTransformerFault(Component parentComponent, 
            TransformerFault e, boolean singleRecord) {
        String message = getCommitPrefixMessage(singleRecord);
        String title = EntryMessages.getString("ExceptionsHelper.errorTransformingValueTitle");
        message += e.getMessage();
        handleException(parentComponent, title, e, message, false);
    }
    
    public static void handleIOException(Component parentComponent, IOException e,
            boolean fatal) {
        String title = EntryMessages.getString("diskErrorTitle"); //$NON-NLS-1$
        String message;
        if (fatal) {
            message = EntryMessages.getString("fatalDiskErrorMessage"); //$NON-NLS-1$
        }
        else {
            message = EntryMessages.getString("diskErrorMessage"); //$NON-NLS-1$
        }
        handleException(parentComponent, title, e, message, fatal);
    }
    
    public static void handleIOException(Component parentComponent, IOException e,
            boolean fatal, String subTitle) {
        String title = EntryMessages.getString("diskErrorTitle"); //$NON-NLS-1$
        String message;
        if (fatal) {
            message = EntryMessages.getString("fatalDiskErrorMessage"); //$NON-NLS-1$
        }
        else {
            message = EntryMessages.getString("diskErrorMessage"); //$NON-NLS-1$
        }
        handleException(parentComponent, title, e, subTitle+"\n\n"+message, fatal);
    }
    
    public static void handleNotAuthorisedFault(Component parentComponent, 
            NotAuthorisedFault e) {
        String title = EntryMessages.getString("notAuthorisedTitle"); //$NON-NLS-1$
        String message = EntryMessages.getString("notAuthorisedMessage"); //$NON-NLS-1$
        handleException(parentComponent, title, e, message, false);
    }
    
    public static void handleNotAuthorisedFault(Component parentComponent, 
            NotAuthorisedFault e, String subTitle) {
        String title = EntryMessages.getString("notAuthorisedTitle"); //$NON-NLS-1$
        String message = EntryMessages.getString("notAuthorisedMessage"); //$NON-NLS-1$
        handleException(parentComponent, title, e, subTitle+"\n\n"+message, false);
    }
    
    public static void handleRepositoryNoConsentFault(Component parentComponent,
            RepositoryNoConsentFault e, String recordIdentifier, boolean singleRecord) {
        String message = getCommitPrefixMessage(singleRecord);
        
        String title = EntryMessages.getString("noConsentTitle"); //$NON-NLS-1$
        message += EntryMessages.getString("noConsentMessage"); //$NON-NLS-1$
        message += getCommitSuffixMessage(recordIdentifier);
        handleException(parentComponent, title, e, message, false);
    }
    
    public static String getCommitPrefixMessage(boolean singleRecord) {
        if (!singleRecord) {
            return EntryMessages.getString("ExceptionsHelper.commitFailurePrefixMessage");
        }
        return ""; //$NON-NLS-1$
    }
    
    public static void handleRepositoryOutOfDateFault(Component parentComponent,
            RepositoryOutOfDateFault e, String recordIdentifier, boolean singleRecord)  {
        String message = getCommitPrefixMessage(singleRecord);
        
        String title = EntryMessages.getString("repositoryOutOfDateTitle"); //$NON-NLS-1$
        message += EntryMessages.getString("repositoryOutOfDateMessage"); //$NON-NLS-1$
        message += getCommitSuffixMessage(recordIdentifier);
        handleException(parentComponent, title, e, message, false);
    }
    
    public static String getCommitSuffixMessage(String recordIdentifier) {
        if (recordIdentifier != null) {
            return EntryMessages.getString("ExceptionsHelper.commitSuffixMessage") + recordIdentifier;
        }
        return ""; //$NON-NLS-1$
    }

    public static void handleRepositoryInvalidIdentifierFault(
            Component parentComponent, RepositoryInvalidIdentifierFault e,
            String recordIdentifier, boolean singleRecord) {
        String message = getCommitPrefixMessage(singleRecord);
        String title = EntryMessages.getString("invalidIdentifierTitle"); //$NON-NLS-1$
        message += EntryMessages.getString("invalidIdentifierMessage"); //$NON-NLS-1$
        message += getCommitSuffixMessage(recordIdentifier);
        handleException(parentComponent, title, e, message, false);
    }
    
    public static void handleException(Component parentComponent, String title,
            Throwable e, String message, boolean fatal) {
        if (fatal && LOG.isFatalEnabled()) {
            LOG.fatal(message, e);
            
            //try to email logs to support
            try{
                RemoteManager.getInstance().emailLogFileToSupport();
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

    /**
     * Simply calls the default uncaught exception handler. 
     * @param e An unhandled exception.
     */
    public static void handleFatalException(final Throwable e)
            throws RuntimeException {
        Thread.getDefaultUncaughtExceptionHandler().uncaughtException(
                Thread.currentThread(), e);
        /* 
         * In case the default handler has not caused the application to
         * exit immediately (e.g. it queued the UI operations to be executed
         * in the Event Thread, re-throw the exception to ensure that this
         * thread does not continue execution.
         */
        
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        }
        throw new RuntimeException(e);
    }

    public static void handleEslOutOfDateFault(Component parentComponent,
            ESLOutOfDateFault e, String studyNumber)  {
        String message = "";
        String title = EntryMessages.getString("eslOutOfDateTitle"); //$NON-NLS-1$
        message += EntryMessages.getString("eslOutOfDateMessage"); //$NON-NLS-1$
        message += getEslStudyNumberMessage(studyNumber);
        handleException(parentComponent, title, e, message, false);
    }
    
    public static void handleEslDuplicateObjectFault(Component parentComponent,
            ESLDuplicateObjectFault e, String studyNumber)  {
        String message = "";
        String title = EntryMessages.getString("eslDuplicateObjectTitle"); //$NON-NLS-1$
        message += EntryMessages.getString("eslDuplicateObjectMessage"); //$NON-NLS-1$
        message += getEslStudyNumberMessage(studyNumber);
        handleException(parentComponent, title, e, message, false);
    }
    
    public static void handleEslSubjectExistsException(Component parentComponent,
            ESLSubjectExistsException e)  {
        String message = e.getMessage();
        String title = "Unable to save participant";
        handleException(parentComponent, title, e, message, false);
    }
    
    public static void handleEslRandomisationException(Component parentComponent,
            RandomisationException e, String studyNumber)  {
        String message = "";
        String title = EntryMessages.getString("eslRandomizationExceptionTitle"); //$NON-NLS-1$
        message += EntryMessages.getString("eslRandomizationExceptionMessage"); //$NON-NLS-1$
        message += getEslStudyNumberMessage(studyNumber);
        handleException(parentComponent, title, e, message, false);
    }
    
    public static String getEslStudyNumberMessage(String studyNumber){
        if (studyNumber != null) {
            return "\nStudy number: " + studyNumber;
        }
        return ""; //$NON-NLS-1$
    }
        
    public static void handleCannotOpenPdfException(Component parentComponent, DesktopException ex){
        String message = "";
        String title = EntryMessages.getString("cannotOpenPdfTitle"); //$NON-NLS-1$
        message += EntryMessages.getString("cannotOpenPdfMessage"); //$NON-NLS-1$
        handleException(parentComponent, title, ex, message, false);
    }
    
    public static void handleDuplicateDocumentsFault(Component parentComponent, DuplicateDocumentsFault ex){
    	String message = ex.getMessage();
    	String title = ex.getTitle();
    	handleException(parentComponent, title, ex, message, false);
    }
 
	public static void handleException(Component parent,Throwable cause){
		if (cause instanceof ConnectException) {
			ExceptionsHelper.handleConnectException(parent,(ConnectException) cause);
		} else if (cause instanceof SocketTimeoutException) {
			ExceptionsHelper.handleSocketTimeoutException(parent,(SocketTimeoutException) cause);
		} else if (cause instanceof IOException) {
			ExceptionsHelper.handleIOException(parent,(IOException) cause, false);
		} else if (cause instanceof NotAuthorisedFault) {
			ExceptionsHelper.handleNotAuthorisedFault(parent,(NotAuthorisedFault) cause);
		} else if (cause instanceof RemoteServiceFault) {
			ExceptionsHelper.handleRemoteServiceFault(parent,(RemoteServiceFault) cause);
		} else if (cause instanceof RepositoryServiceFault) {
			ExceptionsHelper.handleServiceFault(parent,cause);
		} else if (cause instanceof EntrySAMLException) {
			ExceptionsHelper.handleEntrySAMLException(parent,(EntrySAMLException) cause);
		} else {
			ExceptionsHelper.handleFatalException(cause);
		}
		
	}

    
}
