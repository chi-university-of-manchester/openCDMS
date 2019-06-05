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

package org.psygrid.randomization;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.logging.AuditLogger;
import org.psygrid.randomization.dao.DuplicateRandomizerException;
import org.psygrid.randomization.dao.RandomizationDAO;
import org.psygrid.randomization.dao.RandomizerDAOException;
import org.psygrid.randomization.dao.UnknownRandomizerException;
import org.psygrid.randomization.model.DuplicateSubjectException;
import org.psygrid.randomization.model.RandomizerException;
import org.psygrid.randomization.model.dto.Randomizer;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.accesscontrol.AEFAction;
import org.psygrid.security.accesscontrol.AEFGroup;
import org.psygrid.security.accesscontrol.AEFProject;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.services.SecureSoapBindingImpl;
import org.springframework.context.ApplicationContext;

/**
 * Implementation of the PsyGrid Randomization web-service.
 * 
 * @author Rob Harper
 *
 */
public class RandomizationSoapBindingImpl extends SecureSoapBindingImpl implements Randomization {

    /**
     * Name of the component, used for audit logging
     */
    private static final String COMPONENT_NAME = "Randomization";
    
    /**
     * General purpose logger
     */
    private static Log sLog = LogFactory.getLog(RandomizationSoapBindingImpl.class);

    /**
     * Audit logger
     */
    private static AuditLogger logHelper = new AuditLogger(RandomizationSoapBindingImpl.class);
    
    /**
     * Randomization data access object to handle communication with the
     * database.
     */
    private RandomizationDAO dao;
    
    
    protected void onInit() throws ServiceException {
        super.onInit();
        ApplicationContext ctx = getWebApplicationContext();
        dao = (RandomizationDAO)ctx.getBean("randomizationDAOService");
    }
    
    
    public void destroy() {
        super.destroy();
    }

    public String getVersion() throws RemoteException {
        final String METHOD_NAME = "getVersion";
        
        String version = null;
        try{
            Properties props = PropertyUtilities.getProperties(getServletContext().getInitParameter("randomizationProperties"));
            version = props.getProperty("org.psygrid.randomization.version");
        }
        catch(Exception ex){
            //can't load the version, so set it to Unknown
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            version = "Unknown";
        }
        return version;
    }

    public String allocate(String rdmzrName, String subject, Parameter[] parameters, String saml) 
            throws RemoteException, DuplicateSubjectFault, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault {

        final String METHOD_NAME = "allocate";

        try{
            String userName = findUserName(saml);
            String callerIdentity = accessControl.getCallersIdentity();
            logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

            if ( !accessControl.authoriseUser(saml, new AEFGroup(), 
                                                    RBACAction.ACTION_RS_ALLOCATE.toAEFAction(), 
                                                    new AEFProject(null, rdmzrName, false) ) ){
                logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
                throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for randomizer '"+rdmzrName+"'");
            }
            return dao.allocate(rdmzrName, subject, parameters);
        }        
        catch(PGSecurityInvalidSAMLException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
        }
        catch(PGSecuritySAMLVerificationException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
        }
        catch(PGSecurityException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("An error occurred during authorisation", ex);
        }
        catch(DuplicateSubjectException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new DuplicateSubjectFault(ex);
        }
        catch(UnknownRandomizerException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new UnknownRandomizerFault(ex);
        }
        catch(RandomizerDAOException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new RandomizationFault(ex);
        }
        catch(RandomizerException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new RandomizationFault(ex);
        }
        catch(RuntimeException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw ex;
        }

    }

    public String getAllocation(String rdmzrName, String subject, String saml) 
            throws RemoteException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault {

        final String METHOD_NAME = "getAllocation";

        try{
            String userName = findUserName(saml);
            String callerIdentity = accessControl.getCallersIdentity();
            logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

            if ( !accessControl.authoriseUser(saml, new AEFGroup(), 
                                                    RBACAction.ACTION_RS_GET_ALLOCATION.toAEFAction(), 
                                                    new AEFProject(null, rdmzrName, false) ) ){
                logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
                throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for randomizer '"+rdmzrName+"'");
            }
            return dao.getAllocation(rdmzrName, subject);
        }        
        catch(PGSecurityInvalidSAMLException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
        }
        catch(PGSecuritySAMLVerificationException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
        }
        catch(PGSecurityException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("An error occurred during authorisation", ex);
        }
        catch(UnknownRandomizerException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new UnknownRandomizerFault(ex);
        }
        catch(RandomizerDAOException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new RandomizationFault(ex);
        }
        catch(RuntimeException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw ex;
        }
    }

    public boolean checkIntegrity(String rdmzrName, String saml) 
            throws RemoteException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault {

        final String METHOD_NAME = "checkIntegrity";

        try{
            String userName = findUserName(saml);
            String callerIdentity = accessControl.getCallersIdentity();
            logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

            if ( !accessControl.authoriseUser(saml, new AEFGroup(), 
                                                    RBACAction.ACTION_RS_CHECK_INTEGRITY.toAEFAction(), 
                                                    new AEFProject(null, rdmzrName, false) ) ){
                logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
                throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for randomizer '"+rdmzrName+"'");
            }
            return dao.checkIntegrity(rdmzrName);
        }        
        catch(PGSecurityInvalidSAMLException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
        }
        catch(PGSecuritySAMLVerificationException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
        }
        catch(PGSecurityException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("An error occurred during authorisation", ex);
        }
        catch(UnknownRandomizerException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new UnknownRandomizerFault(ex);
        }
        catch(RandomizerDAOException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new RandomizationFault(ex);
        }
        catch(RandomizerException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new RandomizationFault(ex);
        }
        catch(RuntimeException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw ex;
        }
    }

    public void saveRandomizer(Randomizer rdmzr, String saml) 
            throws RemoteException, DuplicateRandomizerFault, RandomizationFault, NotAuthorisedFault {

        final String METHOD_NAME = "saveRandomizer";

        try{
            String userName = findUserName(saml);
            String callerIdentity = accessControl.getCallersIdentity();
            logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

            if ( !accessControl.authoriseUser(saml, new AEFGroup(), 
                                                    RBACAction.ACTION_RS_SAVE_RANDOMIZER.toAEFAction(), 
                                                    new AEFProject(null, rdmzr.getName(), false) ) ){
                logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
                throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for randomizer '"+rdmzr.getName()+"'");
            }
            dao.saveRandomizer(rdmzr);
        }
        catch(PGSecurityInvalidSAMLException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
        }
        catch(PGSecuritySAMLVerificationException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
        }
        catch(PGSecurityException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("An error occurred during authorisation", ex);
        }
        catch(DuplicateRandomizerException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new DuplicateRandomizerFault(ex.getMessage(), ex);
        }
        catch(RandomizerDAOException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new RandomizationFault(ex.getMessage(), ex);
        }
        catch(RuntimeException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw ex;
        }
    }

    public String[][] getAllAllocations(String rdmzrName, String saml) 
            throws RemoteException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault {

        final String METHOD_NAME = "getAllAllocations";

        try{
            String userName = findUserName(saml);
            String callerIdentity = accessControl.getCallersIdentity();
            logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

            if ( !accessControl.authoriseUser(saml, new AEFGroup(), 
                                                    RBACAction.ACTION_RS_GET_ALLOCATIONS.toAEFAction(), 
                                                    new AEFProject(null, rdmzrName, false) ) ){
                logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
                throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for randomizer '"+rdmzrName+"'");
            }
            return dao.getAllAllocations(rdmzrName);
        }        
        catch(PGSecurityInvalidSAMLException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
        }
        catch(PGSecuritySAMLVerificationException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
        }
        catch(PGSecurityException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("An error occurred during authorisation", ex);
        }
        catch(UnknownRandomizerException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new UnknownRandomizerFault(ex);
        }
        catch(RandomizerDAOException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new RandomizationFault(ex);
        }
        catch(RuntimeException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw ex;
        }
    }

    public String[][] getRandomizerStatistics(String rdmzrName, String saml) 
            throws RemoteException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault {

        final String METHOD_NAME = "getRandomizerStatistics";

        try{
            String userName = findUserName(saml);
            String callerIdentity = accessControl.getCallersIdentity();
            logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

            if ( !accessControl.authoriseUser(saml, new AEFGroup(), 
                                                    RBACAction.ACTION_RS_GET_RANDOMIZER_STATISTICS.toAEFAction(), 
                                                    new AEFProject(null, rdmzrName, false) ) ){
                logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
                throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for randomizer '"+rdmzrName+"'");
            }
            return dao.getRandomizerStatistics(rdmzrName);
        }        
        catch(PGSecurityInvalidSAMLException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
        }
        catch(PGSecuritySAMLVerificationException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
        }
        catch(PGSecurityException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("An error occurred during authorisation", ex);
        }
        catch(UnknownRandomizerException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new UnknownRandomizerFault(ex);
        }
        catch(RandomizerException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new RandomizationFault(ex);
        }
        catch(RandomizerDAOException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new RandomizationFault(ex);
        }
        catch(RuntimeException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw ex;
        }

    }

    public String[][] getRandomizerStatistics(String rdmzrName, Parameter[] parameters, String saml) throws RemoteException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault {
        
        final String METHOD_NAME = "getRandomizerStatistics";

        try{
            String userName = findUserName(saml);
            String callerIdentity = accessControl.getCallersIdentity();
            logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

            if ( !accessControl.authoriseUser(saml, new AEFGroup(), 
                    RBACAction.ACTION_RS_GET_RANDOMIZER_STATISTICS.toAEFAction(), 
                    new AEFProject(null, rdmzrName, false) ) ){
                logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
                throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for randomizer '"+rdmzrName+"'");
            }
            return dao.getRandomizerStatistics(rdmzrName, parameters);
        }        
        catch(PGSecurityInvalidSAMLException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
        }
        catch(PGSecuritySAMLVerificationException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
        }
        catch(PGSecurityException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new NotAuthorisedFault("An error occurred during authorisation", ex);
        }
        catch(UnknownRandomizerException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new UnknownRandomizerFault(ex);
        }
        catch(RandomizerException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new RandomizationFault(ex);
        }
        catch(RandomizerDAOException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw new RandomizationFault(ex);
        }
        catch(RuntimeException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw ex;
        }

    }
    
	public Calendar[] getSubjectRandomizationEvents(String rdmzrName, String subjectCode, String saml) 
	throws RemoteException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault {

		final String METHOD_NAME = RBACAction.ACTION_RS_GET_SUBJECT_RANDOMIZATION_EVENTS.toString();

		try{
			String userName = findUserName(saml);
			String callerIdentity = accessControl.getCallersIdentity();
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

			if ( !accessControl.authoriseUser(saml, new AEFGroup(), 
					RBACAction.ACTION_RS_GET_SUBJECT_RANDOMIZATION_EVENTS.toAEFAction(), 
					new AEFProject(null, rdmzrName, false) ) ){
				logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
				throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for randomizer '"+rdmzrName+"'");
			}
			return dao.getSubjectRandomizationEvents(rdmzrName, subjectCode);
		}   
		catch(PGSecurityInvalidSAMLException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}
		catch(UnknownRandomizerException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new UnknownRandomizerFault(ex);
		}
		catch(RandomizerDAOException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new RandomizationFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
	}

    public String[] getRandomizationResultForDate(String rdmzrName, String subject, Calendar date, String saml) 
    throws RemoteException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault {
		final String METHOD_NAME = RBACAction.ACTION_RS_LOOKUP_RANDOMIZATION_RESULT_FOR_DATE.toString();

		try{
			String userName = findUserName(saml);
			String callerIdentity = accessControl.getCallersIdentity();
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

			if ( !accessControl.authoriseUser(saml, new AEFGroup(), 
					new AEFAction(METHOD_NAME, null), 
					new AEFProject(null, rdmzrName, false) ) ){
				logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
				throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for randomizer '"+rdmzrName+"'");
			}
			return dao.getAllocation(rdmzrName, subject, date.getTime());
		}   
		catch(PGSecurityInvalidSAMLException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}
		catch(UnknownRandomizerException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new UnknownRandomizerFault(ex);
		}
		catch(RandomizerDAOException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new RandomizationFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
    }
    
    public void deleteRandomizer(String rdmzrName, String saml) 
    	throws RemoteException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault {

		final String METHOD_NAME = "deleteRandomizer";
		
		try{
		    String userName = findUserName(saml);
		    String callerIdentity = accessControl.getCallersIdentity();
		    logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
		
		    if ( !accessControl.authoriseUser(saml, new AEFGroup(), 
		                                            RBACAction.ACTION_RS_DELETE_RANDOMIZATION.toAEFAction(), 
		                                            new AEFProject(null, rdmzrName, false) ) ){
		        logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
		        throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for randomizer '"+rdmzrName+"'");
		    }
		    dao.deleteRandomizer(rdmzrName);
		}        
		catch(PGSecurityInvalidSAMLException ex){
		    sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
		    throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
		    sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
		    throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
		    sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
		    throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}
		catch(UnknownRandomizerException ex){
		    sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
		    throw new UnknownRandomizerFault(ex);
		}
		catch(RandomizerDAOException ex){
		    sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
		    throw new RandomizationFault(ex);
		}
		catch(RuntimeException ex){
		    sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
		    throw ex;
		}
	}
    
    public Randomizer getRandomizer(String rdmzrName, String saml) 
		throws RemoteException, UnknownRandomizerFault, RandomizationFault, NotAuthorisedFault {

		final String METHOD_NAME = "getRandomizer";
		
		try{
		    String userName = findUserName(saml);
		    String callerIdentity = accessControl.getCallersIdentity();
		    logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
		
		    if ( !accessControl.authoriseUser(saml, new AEFGroup(), 
		                                            RBACAction.ACTION_RS_GET_RANDOMIZER.toAEFAction(), 
		                                            new AEFProject(null, rdmzrName, false) ) ){
		        logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
		        throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for randomizer '"+rdmzrName+"'");
		    }
		    return dao.getRandomizer(rdmzrName);
		}        
		catch(PGSecurityInvalidSAMLException ex){
		    sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
		    throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
		    sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
		    throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
		    sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
		    throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}
		catch(UnknownRandomizerException ex){
		    sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
		    throw new UnknownRandomizerFault(ex);
		}
		catch(RandomizerDAOException ex){
		    sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
		    throw new RandomizationFault(ex);
		}
		catch(RuntimeException ex){
		    sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
		    throw ex;
		}
    }

	
	public String[] getRandomisedParticipantsWithinTimeframe(
			String randomiserName, Calendar startBoundaryInclusive, Calendar endBoundaryDelimiter, String saml)
			throws RemoteException, UnknownRandomizerFault, NotAuthorisedFault {
		final String METHOD_NAME = "getRandomisedParticipantsWithinTimeframe";

		try{
			String userName = findUserName(saml);
			String callerIdentity = accessControl.getCallersIdentity();
			logHelper.logMethodCall(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);

			if ( !accessControl.authoriseUser(saml, new AEFGroup(), 
					new AEFAction(RBACAction.ACTION_RS_GET_SUBJECT_RANDOMIZATION_EVENTS.toString(), null), 
					new AEFProject(null, randomiserName, false) ) ){
				logHelper.logAccessDenied(COMPONENT_NAME, METHOD_NAME, userName, callerIdentity);
				throw new NotAuthorisedFault("User '"+userName+"' is not authorised to perform the action '"+METHOD_NAME+"' for randomizer '"+randomiserName+"'");
			}
			
			return dao.getRandomisedParticipantsWithinTimeframe(randomiserName, startBoundaryInclusive.getTime(), endBoundaryDelimiter.getTime());
		}   
		catch(PGSecurityInvalidSAMLException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("The supplied SAML assertion has expired", ex);
		}
		catch(PGSecuritySAMLVerificationException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("The supplied SAML assertion does not come from a trusted issuer", ex);
		}
		catch(PGSecurityException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new NotAuthorisedFault("An error occurred during authorisation", ex);
		}
		catch(UnknownRandomizerException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw new UnknownRandomizerFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}

	}
    
}
