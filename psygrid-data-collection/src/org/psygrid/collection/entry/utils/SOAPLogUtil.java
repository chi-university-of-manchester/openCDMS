package org.psygrid.collection.entry.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;

/**
 * This class logs all SOAP request and response xmls. This works only if you
 * register it in client-config.wsdd (You can find this config file in axis lib
 * file axis.jar path-org\apache\axis\client).
 * 
 * @author Naveen Dharanegowda | gnaveend@gmail.com | www.naveengd.com
 * @version 2010-04-13
 * 
 */

public class SOAPLogUtil extends BasicHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2332448755193168269L;

	private static final String REQUEST_PREFIX = "Request";

	private static final String RESPONSE_PREFIX = "Response";

	private static final String TIME_STAMP_FORMAT = "yyyy-MM-dd HH-mm-ss-SS";

	private static String FILE_NAME = "C:/aaasoap/PREFIX_TIMESTAMP.xml";

	public void invoke(MessageContext messageContext) throws AxisFault {

		Message requestMessage = messageContext.getRequestMessage();
		Message responseMessage = messageContext.getResponseMessage();

		String fileName = FILE_NAME;
		fileName = fileName.replaceAll("TIMESTAMP", getTimeStamp());

		try {
			if (responseMessage != null) {
//				System.out.println("***** RESPONSE XML *****");
//				System.out.println(responseMessage.getSOAPPartAsString());
				fileName = fileName.replaceAll("PREFIX", RESPONSE_PREFIX);
				File file = new File(fileName);
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				responseMessage.writeTo(fileOutputStream);
			} else if (requestMessage != null) {
//				System.out.println("***** REQUEST XML *****");
//				System.out.println(requestMessage.getSOAPPartAsString());
				fileName = fileName.replaceAll("PREFIX", REQUEST_PREFIX);
				File file = new File(fileName);
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				requestMessage.writeTo(fileOutputStream);
			}
		} catch (Exception exception) {

		}

	}

	private static String getTimeStamp() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				TIME_STAMP_FORMAT);
		return simpleDateFormat.format(Calendar.getInstance().getTime());
	}
}
