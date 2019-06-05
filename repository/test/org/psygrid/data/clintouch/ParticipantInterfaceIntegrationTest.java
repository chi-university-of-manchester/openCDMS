package org.psygrid.data.clintouch;

import java.util.ArrayList;

import org.psygrid.data.model.dto.RecordDTO;
import org.psygrid.data.utils.esl.EslException;
import org.psygrid.data.utils.esl.RemoteClient;
import org.testng.annotations.Test;

/**
 * Tests the ParticipantInterface
 * Similar to ParticipantInterfaceTest, but uses the actual Repository 
 * @author MattMachin
 *
 */
@Test(groups = {"integration-tests"})
public class ParticipantInterfaceIntegrationTest {
	private final ParticipantInterface participantInterface = new ParticipantInterface();
	
	@Test
	public void getListOfParticipantsWithNoParticipantsInSystem() {
		try {
			participantInterface.setEslClient(new RemoteClient());
		} catch (EslException e) {
			e.printStackTrace();
		}
		
//		assert participantInterface.getListOfParticipantsWithAlarmsDue(new ArrayList<RecordDTO>(), null).isEmpty();
	}
}
