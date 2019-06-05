package org.random.rjgodoy.util;


import org.apache.commons.logging.Log;

public class LogTrace {

	public static void trace(Log log) {
		if (!log.isTraceEnabled()) return;
		Throwable t = new Throwable();
		t.fillInStackTrace();
		StackTraceElement ste[] = t.getStackTrace();
		if (ste==null||ste.length<=1) log.trace("Missing stack trace");
		else {log.trace(Thread.currentThread()+" - "+ste[1]);}
	}
}
