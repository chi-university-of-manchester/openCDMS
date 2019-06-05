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

package org.psygrid.randomization.client;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.randomization.DuplicateRandomizerFault;
import org.psygrid.randomization.DuplicateSubjectFault;
import org.psygrid.randomization.NotAuthorisedFault;
import org.psygrid.randomization.Parameter;
import org.psygrid.randomization.Randomization;
import org.psygrid.randomization.RandomizationFault;
import org.psygrid.randomization.RandomizationServiceLocator;
import org.psygrid.randomization.UnknownRandomizerFault;
import org.psygrid.randomization.model.hibernate.Randomizer;

/**
 * Class to act as a layer of abstraction between a Java client
 * and the web services exposed by the randomization service.
 * 
 * @author Rob Harper
 *
 */
public class RandomizationClient extends org.psygrid.common.AbstractClient {

	private final static Log LOG = LogFactory.getLog(RandomizationClient.class);
	
    /**
     * The url where the web-service is located.
     */
    private URL url = null;
    
    /**
     * Default no-arg constructor
     */
    public RandomizationClient(){}
    
    /**
     * Constructor that accepts a value for the url where the web
     * service is located.
     * 
     * @param url
     */
    public RandomizationClient(URL url){
        this.url = url;
    }
    
    /**
     * Get the software version of the randomization web-service.
     * 
     * @return The software version.
     * @throws ConnectException if the client cannot connect to the remote
     * web-service host.
     */
    public String getVersion() throws ConnectException, SocketTimeoutException {
        try{
            Randomization service = getService();
            return service.getVersion();
        }
        catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
        }
        catch(ServiceException ex){
            throw new RuntimeException(ex);
        }
        catch(RemoteException ex){
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Allocate a subject to a treatment arm of a clinical trial
     * using the named randomizer and the given parameters.
     * 
     * @param rdmzrName The name of the randomizer to use.
     * @param subject The name/code of the subject being randomized.
     * @param parameters The parameters that will be used during the
     * randomization.
     * @param saml SAML assertion for security system
     * @return The code of the treatment allocation for the subject.
     * @throws ConnectException if the client cannot connect to the remote
     * web-service host.
     * @throws DuplicateSubjectFault if the subject trying to be randomized as already
     * been allocated to a treatment arm.
     * @throws UnknownRandomizerFault if the named randomizer does not exist.
     * @throws RandomizationFault if an unrecoverable error occurred during randomization.
     * @throws NotAuthorisedFault if authorisation fails or returns false.
     */
    public String allocate(String rdmzrName, String subject, Parameter[] parameters, String saml) 
            throws ConnectException, SocketTimeoutException, RandomizationFault, UnknownRandomizerFault, DuplicateSubjectFault, NotAuthorisedFault {
        try{
            Randomization service = getService();
            return service.allocate(rdmzrName, subject, parameters, saml);
        }
        catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
        }
        catch(ServiceException ex){
            throw new RuntimeException(ex);
        }
        catch(RemoteException ex){
            throw new RuntimeException(ex);
        }

    }
    
    /**
     * Retrieve the treatment allocation fpr the given subject, as
     * allocated by the named randomizer.
     * 
     * @param rdmzrName The name of the randomizer to use.
     * @param subject The name/code of the subject whose allocation
     * is to be retrieved.
     * @param saml SAML assertion for security system
     * @return The code of the treatment allocation for the subject.
     * @throws ConnectException if the client cannot connect to the remote
     * web-service host.
     * @throws UnknownRandomizerFault if the named randomizer does not exist.
     * @throws NotAuthorisedFault if authorisation fails or returns false.
     */
    public String getAllocation(String rdmzrName, String subject, String saml) 
            throws ConnectException, SocketTimeoutException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault {
        try{
            Randomization service = getService();
            return service.getAllocation(rdmzrName, subject, saml);
        }
        catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
        }
        catch(ServiceException ex){
            throw new RuntimeException(ex);
        }
        catch(RemoteException ex){
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Check the integrity of the named randomizer i.e. if it appears
     * that it has been tampered with.
     * 
     * @param rdmzrName The name of the randomizer
     * @param saml SAML assertion for security system
     * @return Boolean, True if appears that the randomizer has been
     * tampered with, False if all is OK.
     * @throws ConnectException if the client cannot connect to the remote
     * web-service host.
     * @throws UnknownRandomizerFault if the named randomizer does not exist.
     * @throws NotAuthorisedFault if authorisation fails or returns false.
     */
    public boolean checkIntegrity(String rdmzrName, String saml) 
            throws ConnectException, SocketTimeoutException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault {
        try{
            Randomization service = getService();
            return service.checkIntegrity(rdmzrName, saml);
        }
        catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			throw new RandomizationFault();
        }
        catch(ServiceException ex){
            throw new RuntimeException(ex);
        }
        catch(RemoteException ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * Save a randomizer to the database.
     * 
     * @param rdmzr The randomizer to save.
     * @param saml SAML assertion for security system
     * @throws ConnectException if the client cannot connect to the remote
     * web-service host.
     * @throws DuplicateRandomizerFault if a randomizer with the same name already exists
     * @throws NotAuthorisedFault if authorisation fails or returns false.
     */
    public void saveRandomizer(Randomizer rdmzr, String saml) 
            throws ConnectException, SocketTimeoutException, DuplicateRandomizerFault, RandomizationFault, NotAuthorisedFault {
        try{
            Randomization service = getService();
            service.saveRandomizer(rdmzr.toDTO(), saml);
        }
        catch(AxisFault fault){
			handleAxisFault(fault, LOG);
        }
        catch(ServiceException ex){
            throw new RuntimeException(ex);
        }
        catch(RemoteException ex){
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Retrieve all allocations for a given randomizer, detailing the treatment
     * arm allocated for each subject so far.
     * 
     * @param rdmzrName The name of the randomizer.
     * @param saml SAML assertion for security system
     * @return Array containing all treatment allocations for the randomizer.
     * The structure of the array is that the first array dimension retrieves data
     * for one subject, the second array dimension gets either the subject name 
     * or the treatment arm they were allocated to i.e. result[0][0] will be the name
     * of the subject; result[0][1] will be the treatment arm they were allocated to.
     * @throws ConnectException if the client cannot connect to the remote
     * web-service host.
     * @throws UnknownRandomizerFault if no randomizer exists with the given name.
     * @throws NotAuthorisedFault if authorisation fails or returns false.
     */
    public String[][] getAllAllocations(String rdmzrName, String saml) 
            throws ConnectException, SocketTimeoutException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault {
        try{
            Randomization service = getService();
            return service.getAllAllocations(rdmzrName, saml);
        }
        catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
        }
        catch(ServiceException ex){
            throw new RuntimeException(ex);
        }
        catch(RemoteException ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * Retrieve statistics on the randomizer, detailing the number of subjects
     * allocated to each of its treatment arms.
     * 
     * @param rdmzrName The name of the randomizer.
     * @param saml SAML assertion for security system
     * @return Array containing the number of allocations for each treatment arm.
     * The structure of the array is that the first array dimension retrieves data
     * for one treatment arm, the second array dimension gets either the treatment 
     * arm name or the number of allocations i.e. result[0][0] will be the name
     * of the first treatment arm; result[0][1] will be the number of allocations
     * for it.
     * @throws ConnectException if the client cannot connect to the remote
     * web-service host.
     * @throws UnknownRandomizerFault if the named randomizer does not exist.
     * @throws NotAuthorisedFault if authorisation fails or returns false.
     */
    public String[][] getRandomizerStatistics(String rdmzrName, String saml) 
            throws ConnectException, SocketTimeoutException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault {
        try{
            Randomization service = getService();
            return service.getRandomizerStatistics(rdmzrName, saml);
        }
        catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
        }
        catch(ServiceException ex){
            throw new RuntimeException(ex);
        }
        catch(RemoteException ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * Retrieve statistics on the randomizer, detailing the number of subjects
     * allocated to each of its treatment arms.
     * <p>
     * Only subjects who were allocated with the given parameters will
     * feature in the statistics.
     * <p>
     * This endpoint may therefore be used to get statistics on a particular branch
     * of a stratified randomizer.
     * 
     * @param rdmzrName The name of the randomizer.
     * @param parameters The parameters that will be used whilst retrieving
     * the statistics.
     * @param saml SAML assertion for security system
     * @return Array containing the number of allocations for each treatment arm.
     * The structure of the array is that the first array dimension retrieves data
     * for one treatment arm, the second array dimension gets either the treatment 
     * arm name or the number of allocations i.e. result[0][0] will be the name
     * of the first treatment arm; result[0][1] will be the number of allocations
     * for it.
     * @throws ConnectException if the client cannot connect to the remote
     * web-service host.
     * @throws UnknownRandomizerFault if the named randomizer does not exist.
     * @throws NotAuthorisedFault if authorisation fails or returns false.
     */
    public String[][] getRandomizerStatistics(String rdmzrName, Parameter[] parameters, String saml) 
            throws ConnectException, SocketTimeoutException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault {
        try{
            Randomization service = getService();
            return service.getRandomizerStatistics(rdmzrName, parameters, saml);
        }
        catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
        }
        catch(ServiceException ex){
            throw new RuntimeException(ex);
        }
        catch(RemoteException ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * Retrieve the dates of all randomization events (i.e treatment allocations)
     * for a given subject.
     * 
     * @param rdmzrName
     * @param subjectCode
     * @param saml
     * @return dates of randomisations
     * @throws ConnectException
     * @throws UnknownRandomizerFault
     * @throws RandomizationFault
     */
    public List<Calendar> getSubjectRandomizationEvents(String rdmzrName, String subjectCode, String saml) 
    	throws ConnectException, SocketTimeoutException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault {
    	try{
            Randomization service = getService();
            Calendar[] dateList = service.getSubjectRandomizationEvents(rdmzrName, subjectCode, saml);
            if (dateList == null) { 
            	return null; 
            }
            
            List<Calendar> dates = new ArrayList<Calendar>();
            for (Calendar d: dateList) {
            	dates.add(d);
            }
            return dates;
        }
        catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
        }
        catch(ServiceException ex){
            throw new RuntimeException(ex);
        }
        catch(RemoteException ex){
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Retrieve an array of subjects that have been randomised between the specified boundary dates.
     * The starting boundary date is inclusive and the ending boundary date is exclusive 
     * (e.g. if it were necessary to retrieve all participants randomised 
     * in June and July 2011, the required boundary dates would be 1 June 2011 00:00 and 1 August 2011 00:00)
     * 
     * @param randomiserName
     * @param startBoundaryInclusive
     * @param endBoundaryDelimiter
     * @return - an array of all subjects randomised within the specified timeframe. Returns a non-null empty
     * array if there are no matching results.
     * @throws RemoteException
     * @throws UnknownRandomizerFault
     * @throws NotAuthorisedFault
     */
    public String[] getRandomisedParticipantsWithinTimeframe(String randomiserName, Calendar startBoundaryInclusive, Calendar endBoundaryDelimiter, String saml)
    	throws ConnectException, SocketTimeoutException, NotAuthorisedFault, UnknownRandomizerFault{
    	
    	/*
    	Calendar start = Calendar.getInstance();
    	start.setTime(startBoundaryInclusive);
    	
    	Calendar end = Calendar.getInstance();
    	end.setTime(endBoundaryDelimiter);
    	*/
    	
    	String [] randomisedParticipants = new String[0];
    	
    	Randomization service;
		try {
			service = getService();
			randomisedParticipants = service.getRandomisedParticipantsWithinTimeframe(randomiserName, startBoundaryInclusive, endBoundaryDelimiter, saml);
		} catch (AxisFault e){
			handleAxisFault(e, LOG);
		} catch (ServiceException e) {
			throw new RuntimeException(e);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		} 
    	
    	return randomisedParticipants;
    }

    
    public String[] getRandomizationResultForDate(String rdmzrName, String subjectCode, Calendar date, String saml) 
	throws ConnectException, SocketTimeoutException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault  {
    	try{
            Randomization service = getService();
            return service.getRandomizationResultForDate(rdmzrName, subjectCode, date, saml);
        }
        catch(AxisFault fault){
			handleAxisFault(fault, LOG);
			return null;
        }
        catch(ServiceException ex){
            throw new RuntimeException(ex);
        }
        catch(RemoteException ex){
            throw new RuntimeException(ex);
        }
    }
	
	
    private Randomization getService() throws ServiceException{
        RandomizationServiceLocator locator = new RandomizationServiceLocator();
        Randomization service = null;
        if ( null == this.url ){
            service = locator.getrandomization();
        }
        else{
            service = locator.getrandomization(url);
        }
        return service;
    }
    
    /**
     * Retrieve the treatment allocation fpr the given subject, as
     * allocated by the named randomizer.
     * 
     * @param rdmzrName The name of the randomizer to use.
     * @param subject The name/code of the subject whose allocation
     * is to be retrieved.
     * @param saml SAML assertion for security system
     * @throws ConnectException if the client cannot connect to the remote
     * web-service host.
     * @throws SocketTimeoutException
     * @throws UnknownRandomizerFault if the named randomizer does not exist.
     * @throws RandomizationException
     * @throws NotAuthorisedFault if authorisation fails or returns false.
     */
    public void deleteRandomization(String rdmzrName, String saml) 
            throws ConnectException, SocketTimeoutException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault {
        try{
            Randomization service = getService();
            service.deleteRandomizer(rdmzrName, saml);
        }
        catch(AxisFault fault){
			handleAxisFault(fault, LOG);
        }
        catch(ServiceException ex){
            throw new RuntimeException(ex);
        }
        catch(RemoteException ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * Retrieve the treatment allocation fpr the given subject, as
     * allocated by the named randomizer.
     * 
     * @param rdmzrName The name of the randomizer to use.
     * @param subject The name/code of the subject whose allocation
     * is to be retrieved.
     * @param saml SAML assertion for security system
     * @throws ConnectException if the client cannot connect to the remote
     * web-service host.
     * @throws SocketTimeoutException
     * @throws UnknownRandomizerFault if the named randomizer does not exist.
     * @throws RandomizationException
     * @throws NotAuthorisedFault if authorisation fails or returns false.
     */
    public Randomizer getRandomizer(String rdmzrName, String saml) 
            throws ConnectException, SocketTimeoutException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault {
        try{
            Randomization service = getService();
            org.psygrid.randomization.model.dto.Randomizer rndmzr = service.getRandomizer(rdmzrName, saml);
            return rndmzr.toHibernate();
        }
        catch(AxisFault fault){
			handleAxisFault(fault, LOG);
        }
        catch(ServiceException ex){
            throw new RuntimeException(ex);
        }
        catch(RemoteException ex){
            throw new RuntimeException(ex);
        }
        
        return null;
    }
    
}
