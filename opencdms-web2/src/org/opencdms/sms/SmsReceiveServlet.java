package org.opencdms.sms;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.clintouch.client.ClinTouchServiceClient;

/**
 * Class to receive messages from the Kapow SMS Gateway
 * 
 * Messages are sent as HTTP POST and containing the following parameters:
 * mobile - the mobile number the message was sent from
 * sms - the content of the SMS message
 * @author MattMachin
 *
 */
public class SmsReceiveServlet extends HttpServlet {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Log sLog = LogFactory.getLog(SmsReceiveServlet.class);
	
	private ClinTouchServiceClient clinTouchServiceClient = new ClinTouchServiceClient();
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		sLog.info("Parsing post request");
		parseRequest(req);
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		sLog.info("Parsing get request");
		parseRequest(req);
	}
	
	private void parseRequest(HttpServletRequest req) {
		String mobileNumber = req.getParameter("mobile");
		String smsMessage = req.getParameter("sms");
		sLog.info("Received sms " + smsMessage + " from mobile number " + mobileNumber);
		clinTouchServiceClient.messageReceived(mobileNumber, smsMessage);
	}
}
