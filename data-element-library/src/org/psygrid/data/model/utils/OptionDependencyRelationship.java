package org.psygrid.data.model.utils;

import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.EntryStatus;
import org.psygrid.data.model.hibernate.OptionEntry;

/**
 * This class stores option dependency relationships - and stores the involved objects by reference.
 * @author williamvance
 *
 */
public class OptionDependencyRelationship {
	
	private OptionEntry optionEntry;
	private Integer optionIndex;
	private Entry dependentEntry;
	private EntryStatus status;
	
	public OptionDependencyRelationship(OptionEntry optionEntry, Integer optionIndex, Entry dependentEntry, EntryStatus status){
		this.optionEntry = optionEntry;
		this.optionIndex = optionIndex;
		this.dependentEntry = dependentEntry;
		this.status = status;
	}

	public Entry getDependentEntry() {
		return dependentEntry;
	}

	public OptionEntry getOptionEntry() {
		return optionEntry;
	}

	public Integer getOptionIndex() {
		return optionIndex;
	}

	public EntryStatus getStatus() {
		return status;
	}
	
}
