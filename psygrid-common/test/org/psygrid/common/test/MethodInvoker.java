package org.psygrid.common.test;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;



/**
 * This class is useful when it is necessary to call a method repeatedly.
 * @author williamvance
 *
 */
public class MethodInvoker {

	Method method = null;
	
	/**
	 * 
	 * @param subject The class which contains the method to be invoked.
	 * @param methodName - the name of the method to be invoked.
	 * @param methodArgumentTypes - the arguments of the method to be invoked
	 * @throws IllegalArgumentException - thrown if method cannot be found as specified.
	 */
	public MethodInvoker(Class subject, String methodName, Class[] methodArgumentTypes) throws IllegalArgumentException{
		try {
			method = subject.getMethod(methodName, methodArgumentTypes);
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Could not fetch method due to security exception");
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Could not find the specified method");
		}
	}
	
	/**
	 * Invokes the method.
	 * @param subject - the object on which to invoke the method (can be null if method is static).
	 * @param arguments - the arguments to the method
	 * @return - the object retured by the method
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Object callMethod(Object subject, Object[] arguments) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		return method.invoke(subject, arguments);
	}
	
}
