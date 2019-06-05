package org.psygrid.common.email;

import java.util.List;

public interface EmailDAO {
	
	public List<QueuedEmail> getQueuedEmails();
	public Long saveEmail(final QueuedEmail email);
	public void removeQueuedEmail(QueuedEmail email);
}
