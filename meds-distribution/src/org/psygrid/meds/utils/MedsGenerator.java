package org.psygrid.meds.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.psygrid.meds.medications.PackageInfo;
import org.psygrid.meds.medications.PackageStatus;
import org.psygrid.meds.project.InvalidProjectException;
import org.psygrid.meds.project.PharmacyInfo;
import org.psygrid.meds.project.TreatmentInfo;

public class MedsGenerator {
	
	public static List<PackageInfo> generateDummyPackages(int number){
		
		List<PackageInfo> packages = new ArrayList<PackageInfo>();
		
		for(int i = 0; i < number; i++){
			packages.add(generateDummyPackage());
		}
		return packages;
	}
	
	private static PackageInfo generateDummyPackage(){
		
		PackageInfo i = new PackageInfo();
		i.setPackageIdentifier(generateRandomString());
		i.setBatchNumber(generateRandomString());
		i.setExpiryDate(new Date());
		i.setPackageStatus(PackageStatus.unverified.toString());
		i.setProjectCode(generateRandomString());
		i.setQpRelease(false);
		i.setShipmentNumber(generateRandomString());
		try {
			i.setPharmacyInfo(new PharmacyInfo("pharmacy Name", "001"));
		} catch (InvalidProjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			i.setTreatmentInfo(new TreatmentInfo("placebo", "001"));
		} catch (InvalidProjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return i;
	}
	
	private static String generateRandomString(){
		
		return UUID.randomUUID().toString();
		
	}

}
