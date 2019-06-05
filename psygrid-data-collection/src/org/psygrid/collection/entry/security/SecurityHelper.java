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


package org.psygrid.collection.entry.security;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityService;
import org.psygrid.security.attributeauthority.service.AttributeAuthorityServiceLocator;
import org.psygrid.security.policyauthority.client.PAManagementClient;
import org.psygrid.security.policyauthority.client.PAQueryClient;
import org.psygrid.security.policyauthority.service.PolicyAuthorityServiceLocator;

import com.thoughtworks.xstream.core.util.Base64Encoder;

/**
 * Helper class that provides utility methods related to security.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 *
 */
public class SecurityHelper {
    
    private static final Log LOG = LogFactory.getLog(SecurityHelper.class);
    private static final String STRONG_PBE_ALGORITHM = "PBEWITHMD5ANDDES"; //$NON-NLS-1$
    private static final String ENCRYPTION_ALGORITHM = "AES"; //$NON-NLS-1$
    private static final String UNLIMITED_PBE_ALGORITHM = "PBEWithSHA256And256BitAES-CBC-BC"; //$NON-NLS-1$
    private static final int STRONG_ENCRYPTION_KEY_SIZE = 128;
    private static final int UNLIMITED_ENCRYPTION_KEY_SIZE = 256;
    private static final int ITERATIONS = 1000;
    private static boolean UNLIMITED_CRYPTOGRAPHY = true;
    private static final String CRYPTOGRAPHY_TYPE_PROPERTY = "cryptographyType"; //$NON-NLS-1$
    private static final String STRONG_CRYPTOGRAPHY = "strong"; //$NON-NLS-1$
    private static boolean USE_MUTUAL_AUTH_FOR_AUTH = false; //$NON-NLS-1$
    private static String KEY_STORE_PASSWORD = null; //$NON-NLS-1$
    private static String KEY_STORE_ALIAS = null; //$NON-NLS-1$
    private static String TRUST_STORE_PASSWORD = null; //$NON-NLS-1$
    
    private static final String PACKAGE = "org/psygrid/collection/entry/security/"; //$NON-NLS-1$
    
    static {
        Security.addProvider(new BouncyCastleProvider());
        InputStream in = null;
        try {
            in = Thread.currentThread().getContextClassLoader().
                    getResourceAsStream(PACKAGE + "security.properties"); //$NON-NLS-1$
            
            if (in != null) {
                Properties properties = new Properties();
                properties.load(in);
            
                String cryptography = properties.get(CRYPTOGRAPHY_TYPE_PROPERTY).
                        toString();
                if (cryptography.equals(STRONG_CRYPTOGRAPHY)) {
                    UNLIMITED_CRYPTOGRAPHY = false;
                }
                if(properties.get("USE_MUTUAL_AUTH_FOR_AUTH").equals("true")){
                	USE_MUTUAL_AUTH_FOR_AUTH = true;
                } else {
                	USE_MUTUAL_AUTH_FOR_AUTH = false;
                }
                KEY_STORE_PASSWORD = properties.get("CLIENT_KEYSTORE_PASSWORD").toString();
                KEY_STORE_ALIAS = properties.get("CLIENT_KEYSTORE_ALIAS").toString();
                TRUST_STORE_PASSWORD = properties.get("CLIENT_TRUSTSTORE_PASSWORD").toString();                
            }

        } catch (IOException e) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Error reading security.properties. Using default values", //$NON-NLS-1$
                        e);
            }
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // Should not happen. Log anyway
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Error closing stream", e); //$NON-NLS-1$
                    }
                }
            }
        }
    }
    
    /**
     * SHA-256 hashing method.
     * 
     * @param input The input string to hash.
     * @return The hashed value, encoded in Base64.
     */
    public static String hash(char[] input) {

        byte[] byteInput = getBytes(input);
       
        input = null;
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256"); //$NON-NLS-1$
            byte[] result = md.digest(byteInput);
            
            SecurityHelper.clearByteArray(byteInput);
            
            byteInput = null;
            
            Base64Encoder encoder = new Base64Encoder();
            String hashed = encoder.encode(result);
            return hashed;
        }
        catch(NoSuchAlgorithmException ex){
            //just log an error - this catch block should
            //never be entered, since the algorithm name is
            //hard-coded
            if (LOG.isErrorEnabled()) {
                LOG.error(": "+ ex.getClass().getSimpleName(), ex); //$NON-NLS-1$
            }
        }
        //Should never happen
        return null;
    }

    private static byte[] getBytes(char[] input) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Writer out = new BufferedWriter(new OutputStreamWriter(bos));
        try {
            out.write(input);
            out.close();
        } catch (IOException ioe) {
            // Cannot happen since we're using an in-memory stream
            if (LOG.isErrorEnabled()) {
                LOG.error("Unexpected error", ioe); //$NON-NLS-1$
            }
        }
        byte[] byteInput = bos.toByteArray();
        return byteInput;
    }
    
    private static char[] getChars(byte[] input) {
        ByteArrayInputStream bis = new ByteArrayInputStream(input);
        Reader in = new BufferedReader(new InputStreamReader(bis));
        char[] charsArray = new char[input.length];
        try {
            int charsRead = 0;
            for (int character = 0; (character = in.read()) != -1; ++charsRead) {
                if (charsArray.length - 1 < charsRead) {
                    char[] newCharArray = new char[charsArray.length * 2];
                    System.arraycopy(charsArray, 0, newCharArray, 0, charsArray.length);
                    clearCharArray(charsArray);
                    charsArray = newCharArray;
                }
                charsArray[charsRead] = (char) character;
            }
            
            if (charsArray.length > charsRead) {
                char[] newCharArray = new char[charsRead];
                System.arraycopy(charsArray, 0, newCharArray, 0, charsRead);
                clearCharArray(charsArray);
                charsArray = newCharArray;
            }
            
            in.close();
        } catch (IOException ioe) {
            // Cannot happen since we're using an in-memory stream
            if (LOG.isErrorEnabled()) {
                LOG.error("Unexpected error", ioe); //$NON-NLS-1$
            }
        }
        return charsArray;
    }
    
    private static int getEncryptionKeySize() {
        if (UNLIMITED_CRYPTOGRAPHY) {
            return UNLIMITED_ENCRYPTION_KEY_SIZE;
        }
        return STRONG_ENCRYPTION_KEY_SIZE;
    }
    
    /**
     * 
     * @return a <code>SecretKeySpec</code> generated with 
     * <code>KeyGenerator</code> for the {@value #ENCRYPTION_ALGORITHM} 
     * algorithm with the key size specified by
     * <code>getEncryptionKeySize()</code>.
     * 
     * @throws NoSuchAlgorithmException if {@value #ENCRYPTION_ALGORITHM} is
     * not available.
     * @see #ENCRYPTION_ALGORITHM
     * @see #getEncryptionKeySize()
     */
    public static SecretKeySpec getRandomKeySpec() throws NoSuchAlgorithmException  {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        keyGenerator.init(getEncryptionKeySize());
        SecretKey key = keyGenerator.generateKey();
        byte[] encodeKey = key.getEncoded();
        
        SecretKeySpec keySpec = new SecretKeySpec(encodeKey, ENCRYPTION_ALGORITHM);
        return keySpec;
    }

    /**
     * Encrypts <code>clearText</code> using <code>keySpec</code> and returns
     * the encrypted text as a String encoded in Base64.
     * 
     * @param clearText Text to be encrypted.
     * @param keySpec <code>SecretKeySpec</code> to be used in the encryption.
     * @return Base64 encoded String of <code>clearText</code> encrypted.
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     */
    public static String encrypt(char[] clearText, SecretKeySpec keySpec) 
            throws InvalidKeyException, NoSuchPaddingException, 
            NoSuchAlgorithmException, IllegalBlockSizeException   {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] bytes = getBytes(clearText);
        
        byte[] encryptedTextBytes = null;
        try {
            encryptedTextBytes = cipher.doFinal(bytes);
        } catch (BadPaddingException e) {
            // Should not happen since we're using ENCRYPTING_MODE
            ExceptionsHelper.handleFatalException(e);
        }

        clearByteArray(bytes);
        
        Base64Encoder encoder = new Base64Encoder();
        String encryptedText = encoder.encode(encryptedTextBytes);
        return encryptedText;
    }
    
    public static char[] decrypt(String cipherText, SecretKeySpec keySpec) 
            throws NoSuchAlgorithmException, NoSuchPaddingException, 
            InvalidKeyException, BadPaddingException  {
        Base64Encoder decoder = new Base64Encoder();
        byte[] cipherTextBytes = decoder.decode(cipherText);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        
        byte[] decryptedTextBytes = null;
        try {
            decryptedTextBytes = cipher.doFinal(cipherTextBytes);
        } catch (IllegalBlockSizeException e) {
            // Should never happen because we are in DECRYPT_MODE
            ExceptionsHelper.handleFatalException(e);
        }
        return getChars(decryptedTextBytes);
    }
    
    private static String getPBEKeyAlgorithm() {
        if (UNLIMITED_CRYPTOGRAPHY) {
            return UNLIMITED_PBE_ALGORITHM;
        }
        return STRONG_PBE_ALGORITHM;
    }
    
    public static String encrypt(String plainText, char[] password) 
            throws NoSuchAlgorithmException, InvalidKeySpecException, 
            NoSuchPaddingException, InvalidKeyException, 
            InvalidAlgorithmParameterException, IllegalBlockSizeException {
        
        PBEKeySpec keySpec = new PBEKeySpec(password);
        SecretKeyFactory keyFactory = 
            SecretKeyFactory.getInstance(getPBEKeyAlgorithm());
        
        SecretKey key = keyFactory.generateSecret(keySpec);
        // Salt
        byte[] salt = new byte[8];
        
        Random random = new Random();
        random.nextBytes(salt);
        
        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, ITERATIONS);
        
        Cipher cipher = Cipher.getInstance(getPBEKeyAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        
        byte[] cipherText = null;
        try {
            cipherText = cipher.doFinal(plainText.getBytes());
        } 
        catch (BadPaddingException e) {
            //Should not happen since we're in ENCRYPT_MODE
            ExceptionsHelper.handleFatalException(e);
        }
        
        Base64Encoder encoder = new Base64Encoder();
        String saltString = encoder.encode(salt);
        String cipherTextString = encoder.encode(cipherText);
        
        return saltString + cipherTextString;
    }
    
    /**
     * Decrypts a cipherText encrypted with {@link #encrypt(String, char[])} if,
     * and only if the password used in both operations is the same. If the 
     * password used is different, then the original value will not be returned.
     * 
     * Note: If the user does not care about the different exceptions, it may
     * choose to catch GeneralSecurityException.
     * 
     * @param text
     * @param password
     * @return cipherText encoded using base 64.
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeySpecException 
     * @throws NoSuchPaddingException 
     * @throws InvalidAlgorithmParameterException 
     * @throws InvalidKeyException 
     * @throws BadPaddingException 
     */
    public static String decrypt(String text, char[] password) 
            throws NoSuchAlgorithmException, InvalidKeySpecException, 
            NoSuchPaddingException, InvalidKeyException, 
            InvalidAlgorithmParameterException, BadPaddingException  {
        String salt = text.substring(0, 12);
        String cipherText = text.substring(12, text.length());
        
        Base64Encoder decoder = new Base64Encoder();
        byte[] saltArray = decoder.decode(salt);
        byte[] cipherTextArray = decoder.decode(cipherText);
        
        PBEKeySpec keySpec = new PBEKeySpec(password);
        SecretKeyFactory keyFactory = 
            SecretKeyFactory.getInstance(getPBEKeyAlgorithm());
        
        SecretKey key = keyFactory.generateSecret(keySpec);
        
        PBEParameterSpec paramSpec = new PBEParameterSpec(saltArray, ITERATIONS);
        
        Cipher cipher = Cipher.getInstance(getPBEKeyAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        
        byte[] playTextArray = null;
        try {
            playTextArray = cipher.doFinal(cipherTextArray);
        } catch (IllegalBlockSizeException e) {
            // Should not happen since we're in DECRYPT_MODE
            ExceptionsHelper.handleFatalException(e);
        } 
        
        return new String(playTextArray);
    }
    
    /**
     * Replaces all the elements inside <code>arrayToBeCleared</code> with
     * the space character. This method is useful if the char contains sensitive
     * data that should be in memory for as little time as possible.
     * 
     * @param arrayToBeCleared The array containing the items to be cleared.
     */
    public static void clearCharArray(char[] arrayToBeCleared) {
        for (int i = 0; i < arrayToBeCleared.length; ++i) {
            arrayToBeCleared[i] = ' ';
        }
        arrayToBeCleared = null;
    }
    
    static void clearByteArray(byte[] arrayToBeCleared) {
        for (int i = 0; i < arrayToBeCleared.length; ++i) {
            arrayToBeCleared[i] = '\u0000';
        }
        
        arrayToBeCleared = null;
    }
    
    public static AAQueryClient getAAQueryClient()  {
        
        AttributeAuthorityService locator = new AttributeAuthorityServiceLocator();
        String endPointAddress = locator.getAttributeAuthorityPortTypeQuerySOAPPortAddress();
        String keyStoreLocation = 
            PersistenceManager.getInstance().getKeyStoreLocation();
        AAQueryClient client = null;
        try {
            String trustStoreLocation = 
                PersistenceManager.getInstance().getTrustStoreLocation();
            client = new AAQueryClient(endPointAddress, trustStoreLocation, 
                    TRUST_STORE_PASSWORD, keyStoreLocation, 
                    KEY_STORE_PASSWORD);
        } catch (PGSecurityException e) {
            //Should not happen (it's wrapping a ServiceException) 
            ExceptionsHelper.handleFatalException(e);
        }

        return client;
    }
    
    public static AAManagementClient getAAManagementClient()  {
        
        AttributeAuthorityService locator = new AttributeAuthorityServiceLocator();
        String endPointAddress = locator.getAttributeAuthorityPortTypeManagementSOAPPortAddress();
        String keyStoreLocation = 
            PersistenceManager.getInstance().getKeyStoreLocation();
        AAManagementClient client = null;
        try {
            String trustStoreLocation = 
                PersistenceManager.getInstance().getTrustStoreLocation();
            client = new AAManagementClient(endPointAddress, trustStoreLocation, 
                    TRUST_STORE_PASSWORD, keyStoreLocation, 
                    KEY_STORE_PASSWORD);
        } catch (PGSecurityException e) {
            //Should not happen (it's wrapping a ServiceException) 
            ExceptionsHelper.handleFatalException(e);
        }

        return client;
    }
    
    static void wrapIntoRuntimeExceptionAndThrow(Exception e) {
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

    public static PAQueryClient getPAQueryClient() {
        
        PolicyAuthorityServiceLocator locator = new PolicyAuthorityServiceLocator();
        String endPointAddress = locator.getPolicyAuthorityPortTypeQuerySOAPPortAddress();
        String keyStoreLocation = 
            PersistenceManager.getInstance().getKeyStoreLocation();
        PAQueryClient client = null;
        try {
            String trustStoreLocation = 
                PersistenceManager.getInstance().getTrustStoreLocation();
            client = new PAQueryClient(endPointAddress, trustStoreLocation, 
                    TRUST_STORE_PASSWORD, keyStoreLocation, 
                    KEY_STORE_PASSWORD);
        } catch (PGSecurityException e) {
            //Should not happen (it's wrapping a ServiceException) 
            ExceptionsHelper.handleFatalException(e);
        }

        return client;
    }
    
    public static PAManagementClient getPAManagementClient() {
        
        PolicyAuthorityServiceLocator locator = new PolicyAuthorityServiceLocator();
        String endPointAddress = locator.getPolicyAuthorityPortTypeManagementSOAPPortAddress();
        String keyStoreLocation = 
            PersistenceManager.getInstance().getKeyStoreLocation();
        PAManagementClient client = null;
        try {
            String trustStoreLocation = 
                PersistenceManager.getInstance().getTrustStoreLocation();
            client = new PAManagementClient(endPointAddress, trustStoreLocation, 
                    TRUST_STORE_PASSWORD, keyStoreLocation, 
                    KEY_STORE_PASSWORD);
        } catch (PGSecurityException e) {
            //Should not happen (it's wrapping a ServiceException) 
            ExceptionsHelper.handleFatalException(e);
        }

        return client;
    }

	/**
	 * @return the USE_MUTUAL_AUTH_FOR_AUTH flag
	 */
	public static boolean useMutualAuthForAuth() {
		return USE_MUTUAL_AUTH_FOR_AUTH;
	}

	/**
	 * @return the KEY_STORE_PASSWORD
	 */
	public static String getKeyStorePassword() {
		return KEY_STORE_PASSWORD;
	}
	
	/**
	 * @return the KEY_STORE_ALIAS
	 */
	public static String getKeyStoreAlias() {
		return KEY_STORE_ALIAS;
	}

	/**
	 * @return the TRUST_STORE_PASSWORD
	 */
	public static String getTrustStorePassword() {
		return TRUST_STORE_PASSWORD;
	}
}
