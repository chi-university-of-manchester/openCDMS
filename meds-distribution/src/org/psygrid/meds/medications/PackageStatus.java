package org.psygrid.meds.medications;

public enum PackageStatus {
	unverified, //package has not yet been QP checked yet
	unusable, //package has rejected by pharmacist in QP.
	available, //package is available to be allocated
	allocated, //package has been allocated to a participant
	distributed, //package has been distributed
	returned //package has been returned
}
