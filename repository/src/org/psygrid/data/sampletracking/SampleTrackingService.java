/*
Copyright (c) 2006-2010, The University of Manchester, UK.

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
package org.psygrid.data.sampletracking;

/**
 * A service to track participant samples e.g. blood,plasma,tissue etc.
 * 
 * Several samples may be stored against a record.
 * 
 * The change history of each sample is preserved.
 * 
 * @author Terry
 *
 */
public interface SampleTrackingService {

	/**
	 * Retrieve the configuration of sample tracking for a given project.
	 * @param projectCode - the project
	 * @return the configuration
	 */
	ConfigInfo getSampleConfig(String projectCode);

	/**
	 * Save the configuration of sample tracking for a given project.
	 * @param conf the configuration
	 */
	void saveSampleConfig(ConfigInfo conf);
	
	/**
	 * Retrieve information relating to a sample tracking participant 
	 * via their record identifier.
	 * @param recordID
	 * @return
	 */
	ParticipantInfo getParticipant(String recordID);

	/**
	 * Save sample tracking information for a participant.
	 * @param participant
	 */
	void saveParticipant(ParticipantInfo participant);

	/**
	 * Get the list of samples for a participant via their record identifier.
	 * @param recordID
	 * @return
	 */
	SampleInfo[] getSamples(String recordID);

	/**
	 * Save sample information.
	 * If the ID of the SampleInfo is null then a new sample is 
	 * added for the user, otherwise a new revision is added to
	 * the existing sample with the matching ID.
	 * @param sample
	 * @return
	 */
	SampleInfo saveSample(SampleInfo sample);

	/**
	 * Retrieve the list of revisions of a given sample.
	 * @param sampleID the sample ID
	 * @return
	 */
	SampleInfo[] getSampleRevisions(long sampleID);
	
	/**
	 * Retrieve the next sample suffix number for a given project.
	 * Suffixes can be combined with a participant's sample tracking identifier
	 * to generate a unique identifier per sample.
	 * @param projectCode
	 * @return
	 */
	long getNextSampleNumber(String projectCode);

}
