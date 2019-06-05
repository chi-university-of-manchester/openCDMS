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

import java.awt.Component;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.persistence.EslSubjectList;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.collection.entry.util.WaitRunnable;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.services.ESLDuplicateObjectFault;
import org.psygrid.esl.services.ESLOutOfDateFault;

public class RemoteEslWorker extends SwingWorker<Map<ISubject, Exception>, Object>{

    private Application application;
    
    public RemoteEslWorker(Application application){
        this.application = application;
    }
    
    @Override
    protected Map<ISubject, Exception> doInBackground() throws Exception {
        new WaitRunnable(application).run();
        Map<ISubject, Exception> exceptionMap = new HashMap<ISubject, Exception>();
        PersistenceManager pManager = PersistenceManager.getInstance();
        synchronized (pManager) {
            RemoteManager rManager = RemoteManager.getInstance();
            EslSubjectList subjectsList = pManager.getEslSubjectsList();
            List<ISubject> uploadedSubjects = new ArrayList<ISubject>();
            for ( ISubject subject: subjectsList.getSubjects() ){
                try{
                    rManager.eslSaveSubject(subject);
                    uploadedSubjects.add(subject);
                }
                catch(ESLDuplicateObjectFault ex){
                    exceptionMap.put(subject, ex);
                }
                catch(ESLOutOfDateFault ex){
                    exceptionMap.put(subject, ex);
                }
                catch(InvalidIdentifierException ex){
                    exceptionMap.put(subject, ex);
                }
                catch(NotAuthorisedFault ex){
                    exceptionMap.put(subject, ex);
                }
                catch(RemoteServiceFault ex){
                    exceptionMap.put(subject, ex);
                }
            }
            //remove uploaded subjects from the list
            for ( ISubject s: uploadedSubjects ){
                subjectsList.removeSubject(s);
            }
            pManager.saveEslSubjectsList();
        }

        return exceptionMap;
    }

    @Override
    protected void done() {
        try {
            Map<ISubject, Exception> exceptionsMap = get();
            if (exceptionsMap.size() != 0) {
                handleExceptions(exceptionsMap);
                return;
            }
            String title = Messages.getString("RemoteEslWorker.successTitle"); //$NON-NLS-1$
            String message = Messages.getString("RemoteEslWorker.successMessage"); //$NON-NLS-1$
            JOptionPane.showMessageDialog(application, message, title,
                    JOptionPane.INFORMATION_MESSAGE);
            
        } catch (ExecutionException ee) {
            Throwable cause = ee.getCause();
            if (cause instanceof ConnectException) {
                ExceptionsHelper.handleConnectException(application, 
                        (ConnectException) cause);
            }
            else if (cause instanceof SocketTimeoutException) {
                ExceptionsHelper.handleSocketTimeoutException(application, 
                        (SocketTimeoutException) cause);
            }
            else if (cause instanceof IOException) {
                ExceptionsHelper.handleIOException(application, 
                        (IOException) cause, true);
                application.exitWithoutConfirmation(true);
            }
            else if (cause instanceof EntrySAMLException) {
                ExceptionsHelper.handleEntrySAMLException(application,
                        (EntrySAMLException) cause);
            }
            else {
                ExceptionsHelper.handleFatalException(cause);
            }
        } catch (InterruptedException e) {
            ExceptionsHelper.handleInterruptedException(e);
        }
        finally {
            new ResetWaitRunnable(application).run();
        }
    }

    private void handleExceptions(Map.Entry<ISubject, Exception> exceptionEntry,
            boolean singleFailure) {
        Exception exception = exceptionEntry.getValue();
        ISubject subject = exceptionEntry.getKey();
        String studyNumber = subject.getStudyNumber();
        if (exception instanceof RemoteServiceFault) {
            ExceptionsHelper.handleRemoteServiceFault(application,
                    (RemoteServiceFault) exception);
        }
        else if (exception instanceof ESLDuplicateObjectFault) {
            ExceptionsHelper.handleEslDuplicateObjectFault(application,
                    (ESLDuplicateObjectFault) exception, studyNumber);
        }
        else if (exception instanceof ESLOutOfDateFault) {
            ExceptionsHelper.handleEslOutOfDateFault(application,
                    (ESLOutOfDateFault) exception, studyNumber);
        }
        else if (exception instanceof InvalidIdentifierException) {
            handleInvalidIdentifierException(application,
                    (InvalidIdentifierException) exception, studyNumber);
        }
        else if (exception instanceof NotAuthorisedFault) {
            ExceptionsHelper.handleNotAuthorisedFault(application,
                    (NotAuthorisedFault) exception);
        }
        else if (exception instanceof RemoteServiceFault) {
            ExceptionsHelper.handleRemoteServiceFault(application,
                    (RemoteServiceFault) exception);
        }
        else {
            ExceptionsHelper.handleFatalException(exception);
        }
    }
        
    private void handleExceptions(Map<ISubject, Exception> exceptionsMap) {
        if (exceptionsMap.size() < 1) {
            throw new IllegalArgumentException("exceptionsMap must have at least one element."); //$NON-NLS-1$
        }
        //TODO At the moment, we only display one of the exceptions to the user.
        //The idea behind passing this Map is to present all the records and
        //their exceptions to the user in a nice and easy to understand manner
        handleExceptions(exceptionsMap.entrySet().iterator().next(), exceptionsMap.size() == 1);
    }
    
    private void handleInvalidIdentifierException(Component parentComponent, 
            InvalidIdentifierException e, String studyNumber) {
        String message = "";
        String title = EntryMessages.getString("invalidStudyNumberTitle"); //$NON-NLS-1$
        message += EntryMessages.getString("invalidStudyNumberMessage"); //$NON-NLS-1$
        message += ExceptionsHelper.getEslStudyNumberMessage(studyNumber);
        ExceptionsHelper.handleException(parentComponent, title, e, message, false);
    }


}
