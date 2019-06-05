package org.psygrid.meds.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.psygrid.common.email.Email;
import org.psygrid.common.simplemap.Pair;

public class ProjectObjectTranslator {

	public static Project translateProjectInfoToProject(ProjectInfo projInfo){
		
		List<PharmacyInfo> pharmaciesInfo = Arrays.asList(projInfo.getPharmacies());
		List<TreatmentInfo> treatmentsInfo = Arrays.asList(projInfo.getTreatments());
		
		List<Pharmacy> pharmacies = new ArrayList<Pharmacy>();
		List<Treatment> treatments = new ArrayList<Treatment>();
		List<Email> emails = new ArrayList<Email>();
		
		for(PharmacyInfo p: pharmaciesInfo){
			pharmacies.add(ProjectObjectTranslator.translatePharmacyInfoToPharmacy(p));
		}
		
		for(TreatmentInfo t: treatmentsInfo){
			treatments.add(ProjectObjectTranslator.translateTreatmentInfoToTreatment(t));
		}
		
		Pair<String, EmailInfo>[] emailInfoMapArray = projInfo.getEmails();
		Map<String, Email> emailMap = new HashMap<String, Email>();
		
		for(int count = 0; count < emailInfoMapArray.length; count++){
			String emailLabel = emailInfoMapArray[count].getName();
			EmailInfo eInfo = emailInfoMapArray[count].getValue();
			Email e = ProjectObjectTranslator.translateEmailInfoToEmail(eInfo);
			emailMap.put(emailLabel, e);
		}
		
		Project p = new Project(projInfo.getProjectName(), projInfo.getProjectCode(), treatments, pharmacies, emailMap);
		return p;
	}
	
	public static Treatment translateTreatmentInfoToTreatment(TreatmentInfo t){
		
		Treatment treatment = new Treatment(t.getTreatmentName(), t.getTreatmentCode());
		return treatment;
	}
	
	
	public static Pharmacy translatePharmacyInfoToPharmacy(PharmacyInfo p){
		Pharmacy pharm = new Pharmacy(p.getPharmacyName(), p.getPharmacyCode());
		return pharm;
	}
	
	public static Email translateEmailInfoToEmail(EmailInfo e){
		Email mail = new Email();
		mail.setBody(e.getBody());
		mail.setSubject(e.getSubject());
		return mail;
	}
	
	public static EmailInfo translateEmailToEmailInfo(Email e){
		EmailInfo eInfo = new EmailInfo(e.getSubject(), e.getBody());
		return eInfo;
	}
	
	public static ProjectInfo translateProjectToProjectInfo(Project proj) throws InvalidProjectException{
		
		PharmacyInfo[] pharmaciesInfo = new PharmacyInfo[proj.getPharmacies().size()];
		List<Pharmacy> pharmaciesList = proj.getPharmacies();
		List<PharmacyInfo> pharmacyInfoList = new ArrayList<PharmacyInfo>();
		for(Pharmacy p : pharmaciesList){
			pharmacyInfoList.add(ProjectObjectTranslator.translatePharmacyToPharmacyInfo(p));
		}
		
		List<Treatment> treatments = proj.getTreatments();
		List <TreatmentInfo> treatmentInfos = new ArrayList<TreatmentInfo>();
		TreatmentInfo[] treatmentsInfo = new TreatmentInfo[proj.getTreatments().size()];
		for(Treatment t : treatments){
			treatmentInfos.add(ProjectObjectTranslator.translateTreatmentToTreatmentInfo(t));
		}
		
		Map<String, Email> emailsMap = proj.getEmails();
		Pair<String, EmailInfo>[] emailInfoArray = new Pair[emailsMap.size()];
		Set<String> emailMapKeys = emailsMap.keySet();
		
		int count = 0;
		for(String emailKey : emailMapKeys){
			Email m = emailsMap.get(emailKey);
			EmailInfo mInfo = ProjectObjectTranslator.translateEmailToEmailInfo(m);
			emailInfoArray[count] = new Pair<String, EmailInfo>(emailKey, mInfo);
			count++;
		}
		
		ProjectInfo i = new ProjectInfo(proj.getProjectName(), proj.getProjectCode(), pharmacyInfoList.toArray(pharmaciesInfo), treatmentInfos.toArray(treatmentsInfo), emailInfoArray);
		
		return i;
	}
	
	public static TreatmentInfo translateTreatmentToTreatmentInfo(Treatment t) throws InvalidProjectException{
		
		TreatmentInfo i = new TreatmentInfo(t.getTreatmentName(), t.getTreatmentCode());
		return i;
	}
	
	
	public static PharmacyInfo translatePharmacyToPharmacyInfo(Pharmacy p) throws InvalidProjectException{
		PharmacyInfo pI = new PharmacyInfo(p.getPharmacyName(), p.getPharmacyCode());
		return pI;
	}
	
	
	public static void validateTreamentInfo(TreatmentInfo t) throws InvalidProjectException{
		t.validate();
	}
	
	public static void validatePharmacyInfo(PharmacyInfo p) throws InvalidProjectException{
		p.validate();
	}
	
}
