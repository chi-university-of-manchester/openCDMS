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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.psygrid.collection.entry.remote.ProxySetting;
import org.psygrid.common.proxy.ProxyHelper;
import org.psygrid.common.proxy.ProxyPersistence;

import com.myjavatools.lib.Files;
import com.thoughtworks.xstream.XStream;

/**
 * Handles interaction with local file system.
 * Adapted from CoCoA code..
 * @author pwhelan
 *
 */
public class PersistenceManager implements ProxyPersistence {
    
    private static final Log LOG = LogFactory.getLog(PersistenceManager.class);
    
    private static PersistenceManager INSTANCE = new PersistenceManager();
    
    private final static String WRONG_OBJECT_TYPE_MESSAGE = "Wrong object " + //$NON-NLS-1$
    "type found in file."; //$NON-NLS-1$

    private volatile boolean baseDirInitialized = false;
    private volatile boolean userDirInitialized = false;
    
    private String recordsListLocation;
    private String keyStoreLocation;
    private String unfinishedDocumentLocation;
    private String proxySettingsLocation;
    private String lockLocation;
    private String eslSubjectsLocation;
    
    private FileLock fileLock;
    private FileChannel lockChannel;

    private String trustStoreLocation;

    private String baseDir;
    private String proxyDir;

    private String userDir;
    
    private XStream xStream;
    
    
    public PersistenceManager() {
    	xStream = new XStream();
    }
    
    private String createBaseDir(boolean alternativeLocation, int iteration) {
    	
    	String dir = getHomeDir();
    	
        if (alternativeLocation) {
            String testSuffix = "test"; //$NON-NLS-1$
            if (iteration != -1) {
                testSuffix += iteration;
            }
            dir = dir + testSuffix;
        }
        dir = dir + File.separatorChar;
        return dir;
    }
    
	public String getHomeDir() {

		//Find the system component for the file path
        String system = getSystemName();
        
		String home = System.getProperty("user.home");
		System.out.println("Home now is: "+home);

		String dir = home + File.separator + ".psygrid" + File.separator + system + File.separator + "sm"; //$NON-NLS-1$//$NON-NLS-2$

		//If a .psygrid-dsdesigner folder exists then we need to move all of its contents
		//into the system specific folder one level down
        File oldDir = new File(home + File.separator + ".psygrid-security");
        File newDir = new File(dir);
        if ( oldDir.exists() ){
    		LOG.info("Restructuring for multi-system usage");
        	boolean resultCopy = Files.copy(oldDir, newDir);
        	if ( resultCopy ){
        		Files.deleteFile(oldDir);
				//copy the proxies.xml file
				File oldProxy = new File(dir + File.separator + "proxies.xml");
				File newProxy = new File(home + File.separator + ".psygrid" + File.separator + system + File.separator + "proxies.xml");
				if ( oldProxy.exists() && !newProxy.exists() ){
					Files.copy(oldProxy, newProxy);
				}
        	}
        }
        
		return dir;
	}

	private String getSystemName(){
		//Find the system component for the file path
        String system = null;
        try{
            Properties props = new Properties();
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("client.properties"));
            system = props.getProperty("client.system");
        }
        catch(IOException ex){
        	system = "Unspecified";
        }
        catch(NullPointerException ex){
            //if Properties#load can't find the properties file then
            //it seems to (very helpfully!) throw an NPE
        	system = "Unspecified";
        }
        if ( null == system || 0 == system.length() ){
        	system = "Unspecified";
        }
        LOG.info("getHomeDir: system="+system);
        return system;
	}
	

    boolean deleteBaseDir()    {
        if ((!baseDirInitialized) || (!userDirInitialized)) {
            return false;
        }
        boolean allDeleted = true;
        File userDirFile = new File(userDir);
        for (File userFile : userDirFile.listFiles()) {
            boolean success = userFile.delete();
            if (!success) {
                allDeleted = false;
            }
        }
        boolean success = userDirFile.delete();
        if (!success) {
            allDeleted = false;
        }
        if (baseDir.equals(userDir)) {
            return allDeleted;
        }
        File baseDirFile = new File(baseDir);
        for (File baseFile : baseDirFile.listFiles()) {
            success = baseFile.delete();
            if (!success) {
                allDeleted = false;
            }
        }
        success = baseDirFile.delete();
        if (!success) {
            allDeleted = false;
        }
        return allDeleted;
    }
    
    public void initBaseDir() throws IOException {
        initBaseDir(true);
    }
    
    void initBaseDir(boolean doLock) throws IOException {
        if (baseDirInitialized) {
            return;
        }
        initBaseDirLocations(!doLock, -1);

        for (int i = 0; ; ++i)  {
            File lock = new File(lockLocation);
            if (lock.exists() == false) {
                lock.createNewFile();
            }

            lockChannel = new RandomAccessFile(lock, "rw").getChannel(); //$NON-NLS-1$

            fileLock = lockChannel.tryLock();
            if (fileLock == null) {
                if (doLock) {
                    throw new FileLockException("File is already locked."); //$NON-NLS-1$
                }
                
                initBaseDirLocations(!doLock, i);
                
            } else {
                break;
            }
        }
        
        // Bug#487
        final String PACKAGE = "org/psygrid/securitymanager/security/keystores/"; //$NON-NLS-1$

        File keyStoreFile = new File(keyStoreLocation);
        initStore(PACKAGE + "defaultKeystore.jks", keyStoreFile); //$NON-NLS-1$
        
        File trustStoreFile = new File(trustStoreLocation);
        initStore(PACKAGE + "truststore.jks", trustStoreFile); //$NON-NLS-1$
        
        baseDirInitialized = true;
    }
    
    private void initBaseDirLocations(boolean alternativeLocation, int iteration) {
        baseDir = createBaseDir(alternativeLocation, iteration);
        proxyDir = getProxyDir();
        trustStoreLocation = baseDir + "truststore.jks"; //$NON-NLS-1$
        keyStoreLocation = baseDir + "keystore.jks"; //$NON-NLS-1$
        proxySettingsLocation = proxyDir + "proxies.xml"; //$NON-NLS-1$
        
        File dirFile = new File(baseDir);
        if (dirFile.exists() == false) {
            dirFile.mkdirs();
        }
        lockLocation = baseDir + "lock"; //$NON-NLS-1$

    }

	private String getProxyDir(){
		Preferences prefs = Preferences.userNodeForPackage(PersistenceManager.class); 

		String home = prefs.get("psygrid.user.home", null);
		LOG.info("Home is: "+home);

		if (home == null) {
			home = System.getProperty("user.home");
			LOG.info("Home now is: "+home);
		}

		//Find the system component for the file path
		String system = getSystemName();
		
		String dir = home + File.separator + ".psygrid" + File.separator + system + File.separator; //$NON-NLS-1$
		return dir;
	}
	
    @SuppressWarnings("unchecked")
    public List<ProxySetting> loadProxySettings() throws IOException    {
        Object proxySettingsObj = load(proxySettingsLocation);
        List<ProxySetting> proxySettings = new ArrayList<ProxySetting>();
        if (proxySettingsObj instanceof List) {
			List<?> rawProxySettings = (List<?>) proxySettingsObj;
            for (Object rawProxySetting : rawProxySettings) {
                if (rawProxySetting instanceof ProxySetting) {
                    proxySettings.add((ProxySetting) rawProxySetting);
                }
                else {
                    throw new IOException(WRONG_OBJECT_TYPE_MESSAGE + " " + //$NON-NLS-1$
                            "List<ProxySetting> expected, but object of type " + //$NON-NLS-1$
                            rawProxySetting.getClass() + " was found inside the List");                             //$NON-NLS-1$
                }
            }
            if ( ProxyHelper.convertProxies(proxySettings) ){
            	saveProxySettings(proxySettings);
            }
            return proxySettings;
        }
        
        throw new IOException(getWrongObjectTypeFoundMsg(List.class, 
                proxySettingsObj));
    }
    
    Object load(String file) throws FileNotFoundException, IOException {
        return xStream.fromXML(loadToString(file));
    }
    
    public void saveProxySettings(List<ProxySetting> proxySettings) throws IOException  {
        save(proxySettings, proxySettingsLocation);
    }
    
    /**
     * Saves <code>persistable</code> as an XML representation in the location
     * given by <code>file</code>.
     */
    void save(Object persistable, String file) throws IOException   {
        String xml = xStream.toXML(persistable);
        saveString(xml, file);
    }
    
    String loadToString(String file) throws FileNotFoundException, IOException {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder(2500);
            String s = null;
            while ((s = in.readLine()) != null) {
                builder.append(s);
                builder.append(System.getProperty("line.separator"));
            }
            return builder.toString();
        }
        catch (IOException ioe) {
            throw ioe;
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
    }

    
    public final String getBaseDirLocation() {
        return baseDir;
    }
    
    public final String getUserDirLocation() {
        return userDir;
    }
    
    private void initStore(String storeSource, File storeFile)
            throws IOException {
        if (storeFile.exists()) {
            storeFile.delete();
        }
        storeFile.createNewFile();
        InputStream sourceStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(storeSource);
        
        try {
            copyInputStreamToFile(sourceStream, storeFile);
        } finally {
            if (sourceStream != null) {
                sourceStream.close();
            }
        }

    }
    
    private void copyInputStreamToFile(InputStream source, File destination) throws IOException {
        FileOutputStream outputStream = null;
        
        try {
            outputStream = new FileOutputStream(destination);
            int input;
            while ((input = source.read()) != -1) {
                outputStream.write(input);
            }
            outputStream.flush();
        }
        finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
    
    public void deleteRecordsList() throws IOException {
		File f = new File(recordsListLocation);
		if (f.exists()) {
			delete(recordsListLocation);
		}
	}
    
    public void deleteEslSubjectsList() throws IOException {
        File f = new File(eslSubjectsLocation);
        if (f.exists()) {
            delete(eslSubjectsLocation);
        }
    }
    
    public boolean isBaseDirInitialized() {
        return baseDirInitialized;
    }
    
    public boolean isUserDirInitialized() {
        return userDirInitialized;
    }
    
    public void deleteUnfinishedDocumentInstance() throws IOException {
        File file = new File(unfinishedDocumentLocation);
        delete(unfinishedDocumentLocation);
    }
    
    public void deleteKeyStore() throws IOException {
        File file = new File(keyStoreLocation);
        if (file.exists() == false) {
            return;
        }
        if (file.delete() == false) {
            String errorMessage = 
                "Error deleting file: " + file.getAbsolutePath();
            if (LOG.isWarnEnabled()) {
                LOG.warn(errorMessage);
            }
            if (file.exists()) {
                throw new IOException(errorMessage);
            }
        }
    }
    
    private String getReadyToCommitSuffix(boolean readyToCommit) {
        return readyToCommit ? "-1" : "-0"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    void saveString(String string, String file) throws IOException  {
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        try {
            out.write(string);
            out.flush();
        } catch (IOException ioe) {
            throw ioe;
        }
        finally {
            out.close();
        }
    }
    
    void saveBytes(byte[] bytes, String file) throws IOException    {
        BufferedOutputStream out = new BufferedOutputStream(
                new FileOutputStream(file));
        try {
            out.write(bytes);
            out.flush();
        } catch (IOException ioe) {
            throw ioe;
        }
        finally {
            out.close();
        }
    }
    
    public static PersistenceManager getInstance() {
        return INSTANCE;
    }
    
    
    final void delete(String location) throws IOException {
        File file = new File(location);
        if (file.delete() == false) {
            String errorMessage = 
                "Error deleting file: " + file.getAbsolutePath();
            if (LOG.isWarnEnabled()) {
                LOG.warn(errorMessage);
            }
            if (file.exists()) {
                throw new IOException(errorMessage);
            }
        }
    }
    
    public final void dispose() throws IOException  {
        if (fileLock != null && fileLock.isValid()) {
            fileLock.release();
        }
        if (lockChannel != null && lockChannel.isOpen()) {
            lockChannel.close();
        }
    }

    public final void saveKeyStore(byte[] keyStore) throws IOException {
    	saveBytes(keyStore, keyStoreLocation);
    }

    public final String getKeyStoreLocation() {
        return keyStoreLocation;
    }
    
    public void setKeyStoreLocation(String keyStoreLocation)
    {
    	this.keyStoreLocation = keyStoreLocation; 
    }
    
    public final InputStream loadKeyStore() throws IOException {
        BufferedInputStream inputStream = new BufferedInputStream(
                new FileInputStream(keyStoreLocation));
        
        return inputStream;
    }

    public final String getTrustStoreLocation() {
        return trustStoreLocation;
    }
    // Bug#487
    public final void restoreDefaultKeystore() throws IOException{
        final String PACKAGE = "org/psygrid/securitymanager/security/keystores/"; //$NON-NLS-1$
    	File keyStoreFile = new File(keyStoreLocation);
        initStore(PACKAGE + "defaultKeystore.jks", keyStoreFile); //$NON-NLS-1$
    }
    
    /**
     * 
     * @param expected Class expected.
     * @param found Object type received.
     * @return A error message describing what was expected and what was
     * received.
     */
    private String getWrongObjectTypeFoundMsg(Class<?> expected, Object found) {
        String objectFoundMsg = found == null ? "null" : found.getClass().toString(); //$NON-NLS-1$
        return WRONG_OBJECT_TYPE_MESSAGE + " " + objectFoundMsg + //$NON-NLS-1$
        " found, but " + expected + " expected."; //$NON-NLS-1$ //$NON-NLS-2$
    }


    private class LogFileFilter implements FileFilter{
        public boolean accept(File pathname) {
            if ( pathname.getName().endsWith(".log") ){
                return true;
            }
            return false;
        }
        
    }


	public void exportProxySettingsFile(File toFile) throws IOException {
		Files.copy(new File(proxySettingsLocation), toFile);
	}

	
	public void exportProxySettingsFile(File toFile, List<ProxySetting> proxies)
			throws IOException {
		save(proxies, toFile.getAbsolutePath());
	}

	public void importProxySettingsFile(File fromFile) throws IOException {
		Files.copy(fromFile, new File(proxySettingsLocation));
	}

	public boolean checkProxySettings(File file) {
		try{
			Object proxySettingsObj = load(file.getPath());
			List<ProxySetting> proxySettings = new ArrayList<ProxySetting>();
			if (proxySettingsObj instanceof List) {
				List<?> rawProxySettings = (List<?>) proxySettingsObj;
				for (Object rawProxySetting : rawProxySettings) {
					if (rawProxySetting instanceof ProxySetting) {
						proxySettings.add((ProxySetting) rawProxySetting);
					}
					else {
						throw new IOException(WRONG_OBJECT_TYPE_MESSAGE + " " + //$NON-NLS-1$
								"List<ProxySetting> expected, but object of type " + //$NON-NLS-1$
								rawProxySetting.getClass() + " was found inside the List");                             //$NON-NLS-1$
					}
				}
			}
			else{
				throw new IOException(getWrongObjectTypeFoundMsg(List.class, 
					proxySettingsObj));
			}
		}
		catch(Exception ex){
			return false;
		}
		return true;
	}

}
