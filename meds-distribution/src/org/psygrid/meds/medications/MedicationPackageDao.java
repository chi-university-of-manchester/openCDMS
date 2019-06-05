package org.psygrid.meds.medications;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.psygrid.meds.events.InvalidEventException;
import org.psygrid.meds.events.MedsPackageStatusChangeEvent;
import org.psygrid.meds.events.MedsPackageStatusChangeEventInterpreter;
import org.psygrid.meds.events.PackageViewEvent;
import org.psygrid.meds.events.StatusChangeEventType;
import org.psygrid.meds.project.Pharmacy;
import org.psygrid.meds.project.Project;
import org.psygrid.meds.project.Treatment;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class MedicationPackageDao extends HibernateDaoSupport {
	
	/**
	 * This is a helper method in order to determine which pharmacy a medication package has been shipped to.
	 * @param medicationPackageId
	 * @return
	 * @throws MedicationPackageNotFoundException 
	 */
	public Pharmacy getPharmacyOfMedicationPackage(final String medicationPackageId) throws MedicationPackageNotFoundException{
		
		Pharmacy p = (Pharmacy) getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				Query q = session.createQuery("select p.pharmacy from MedicationPackage p where p.packageId = :packageId").
						setString("packageId", medicationPackageId);
				
				Pharmacy p = (Pharmacy)q.uniqueResult();
				return p;
			}
		});
		
		if(p == null){
			throw new MedicationPackageNotFoundException("Medication package " + medicationPackageId + " not found in database");
		}
		
		return p;
	}
	
	
	public MedicationPackage getLastAllocatedPackageForUser(final String participantIdentifier) throws ParticipantNotFoundException{
		
		MedicationPackage p = (MedicationPackage) getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				Query q = session.createQuery("select p from MedsPackageStatusChangeEvent e join e.eventObject p where e.statusChangeEvent = :allocatedStatus and e.additionalInfo = :participantId order by e.eventDate asc").
				setString("allocatedStatus", StatusChangeEventType.packageAllocation.toString()).
				setString("participantId", participantIdentifier);
		
				List<MedicationPackage> matchingPackages = q.list();
				
				if(matchingPackages.size() == 0){
					return null;
				}else{
					int size = matchingPackages.size();
					MedicationPackage p = matchingPackages.get(size-1);
					return p;
				}
			}
		});
		
		if (p == null){
			throw new ParticipantNotFoundException("Participant " + participantIdentifier + " not found in meds-dist database");
		}
	
		return p;
	}
	
	public void setMedicationPackageQPReleaseFlag(final String projectCode, final List<String> packageIdentifiers, final boolean qpFlagValue, final String user){
		
		//For each change, we need to generate a status change event.
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				
				Query q = session.createQuery("update MedicationPackage p set p.qpRelease = :qpRelease where p.packageId in :packageIdList and p.projectCode = :projectCode")
				.setParameter(":qpRelease", qpFlagValue)
				.setParameterList(":packageIdList", packageIdentifiers)
				.setParameter(":projectCode", projectCode);
				
				int numPackagesUpdated = q.executeUpdate();
				if(numPackagesUpdated != packageIdentifiers.size()){
					//Throw an exception!
				}
				return null;
			}
		});
	}
	
	public void changeMedicationPackagesStatus(final String projectCode, final List<String> packageIdentifiers, final String currentStatus, final String newStatus, final String additionalInfo, final String user){
		
		//For each change, we need to generate a status change event.
		boolean result = (Boolean)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				
				Query q = session.createQuery("select p from MedicationPackage p where p.packageId in :packageIdList and p.projectCode = :projectCode and p.status = :currentPackageStatus")
				.setParameterList(":packageIdList", packageIdentifiers)
				.setParameter(":projectCode", projectCode)
				.setParameter(":currentPackageStatus", currentStatus);
				
				List<MedicationPackage> packages = q.list();
				
				if(packages.size() < packageIdentifiers.size()){
					return false;
				}
				
				for(MedicationPackage p : packages){
					p.setStatus(newStatus);
					
					StatusChangeEventType eventType = MedsPackageStatusChangeEventInterpreter.assessEventType(PackageStatus.valueOf(currentStatus), PackageStatus.valueOf(newStatus));
					MedsPackageStatusChangeEvent changeEvent = new MedsPackageStatusChangeEvent(user, new Date(), eventType, additionalInfo, p);
					p.addStatusChangeEvent(changeEvent);
					
					session.saveOrUpdate(p);
				}
				
				return true;
			}
		});
		
		if(!result){
			//throw an exception - packages didn't get their status changed.
		}
	}
	
	public void saveMedicationPackage(MedicationPackage p) throws HibernateException, InvalidMedicationPackageException{
		
		//Centre c = p.getCentre();
		Pharmacy ph = p.getPharmacy();
		
		if(ph.getId() == null){
			ph = getPersistedPharmacyForNewMedsPackage(p);
		}
		
		Treatment t = p.getTreatment();
		
		if(t.getId() == null){
			t=this.getPersistedTreatmentForNewMedsPackage(p);
		}
		
		if(t == null || ph == null){
			StringBuffer errorMsg = new StringBuffer();
			errorMsg.append("The medicaton package to be saved has invalid information.");
			if(t == null){
				errorMsg.append(" The associated treatment does not match the project's treatments in the db.");
			}
			if(ph == null){
				errorMsg.append(" The associated pharmacy does not match the project's pharmacies in the db.");
			}
			
			throw new InvalidMedicationPackageException(errorMsg.toString());
		}
		
		
		p.setPharmacy(ph);
		p.setTreatment(t);
		p.setImportDate(new Date());
		
		getHibernateTemplate().saveOrUpdate(p);
	}
	
	public List<MedicationPackage> getMedicationPackagesForParticipant(final String projectCode, final String participantIdentifier){
		List<MedicationPackage> packages = (List<MedicationPackage>)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				
				Query q = session.createQuery("select p from MedicationPackage p join p.statusChangeEvents e where e.statusChangeEvent = :allocatedStatus and e.additionalInfo = :participantIdentifier")
				.setString(":allocatedStatus", StatusChangeEventType.packageAllocation.toString())
				.setString(":participantIdentifier", participantIdentifier);
				
				return q.list();
			}
		});
		
		return packages;
	}
	
	public List<MedicationPackage> getMedicationPackagesForProject(final String projectCode){
		List<MedicationPackage> packages = (List<MedicationPackage>)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				
				Query q = session.createQuery("select p from MedicationPackage p where p.projectCode = :projectCode")
				.setString(":projectCode", projectCode);
				
				return q.list();
			}
		});
		
		return packages;
	}
	
	public List<MedicationPackage> getMedicationPackagesByPharmacyAndStatus(final String projectCode, final List<String> pharmacies, final PackageStatus status){
		
		List<MedicationPackage> packages = (List<MedicationPackage>)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				
				Query q = session.createQuery("select p from MedicationPackage p where p.projectCode = :projectCode and p.pharmacy.pharmacyCode in :pharmacies")
				.setString(":projectCode", projectCode)
				.setParameterList(":pharmacies", pharmacies);
				
				return q.list();
			}
		});
		
		return packages;
		
		
	}
	
	public MedicationPackage getMedicationPackage(final String packageIdentifier, final String projectCode){
		
		MedicationPackage p = (MedicationPackage) getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				Query q = session.createQuery("select p from MedicationPackage p where p.packageId = :packageId and p.projectCode = :projectCode").
				setString("packageId", packageIdentifier).
				setString("projectCode", projectCode);
		
				return q.uniqueResult();
			}
		});
		
		return p;	
	}
	
	public String allocateMedicationPackage(final String projectCode, final String centreCode, final String treatmentCode, final String participantId, final String userName){
		
		String packageId = (String)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				Query q = session.createQuery("select p from MedicationPackage p where p.centre.centreCode = :cCode and " +
				"p.treatment.treatmentCode = :tCode and p.projectCode = :pCode and p.status = :status order by p.importDate desc")
				.setString("cCode", centreCode)
				.setString("tCode", treatmentCode)
				.setString("pCode", projectCode)
				.setString("status", PackageStatus.available.toString());
		
				List availablePackages = q.list();
				
				MedicationPackage chosenPackage = (MedicationPackage) availablePackages.get(0);
				chosenPackage.setStatus(PackageStatus.allocated.toString());
				
				MedsPackageStatusChangeEvent allocation;
				allocation = new MedsPackageStatusChangeEvent(userName, new Date(), StatusChangeEventType.packageAllocation, participantId, chosenPackage);
				chosenPackage.addStatusChangeEvent(allocation);
				
				session.saveOrUpdate(chosenPackage);
				return chosenPackage.getPackageId();

			}
		});
		
		return packageId;
		
	}
	
	public boolean undistributeMedication(final String projectCode, final String packageIdentifier, final String sysUser){
		
		//We need to make sure that this medication package is currently 'distributed'. Once it is undistributed (back to allocated), we must make sure that
		//We need to create and save a 'cancellation' distribution event.
		
		boolean success = (Boolean)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				
				Query q = session.createQuery("select from MedicationPackage p where p.status = :status  and p.packageId = :packageIdentifier")
				.setString("status", PackageStatus.distributed.toString())
				.setString("packageIdentifier", packageIdentifier);
				
				Object result = q.uniqueResult();
				
				boolean returnValue = false;
				
				if(result == null){
					//TODO - if this is null then throw an exception to notify the caller that the query returned no results.
				}else{
					MedicationPackage p = (MedicationPackage)result;
					p.setStatus(PackageStatus.allocated.toString());
					
					
					MedsPackageStatusChangeEvent undistribute = new MedsPackageStatusChangeEvent(sysUser, new Date(), StatusChangeEventType.packageDistributionUndo, null, p);
					p.addStatusChangeEvent(undistribute);
					
					session.update(p);
					returnValue = true;
				}
				
				return returnValue;
			}
			
		});
		
		return success;
	}
	
	public boolean assertMedicationPackageStatus(final String projectCode, final String packageIdentifier, final String currentStatus){
		
		boolean packageHasExpectedStatus = false;
		
		packageHasExpectedStatus = (Boolean)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				
				Query q = session.createQuery("select p.status from MedicationPackage p where p.packageId = :packageId and p.projectCode = :projectCode")
				.setString("packageId", packageIdentifier)
				.setString("projectCode", projectCode);
				
				String status = (String)q.uniqueResult();
				
				if(status.equals(currentStatus)){
					return true;
				}else{
					return  false;
				}

			}
			
		});
		
		return packageHasExpectedStatus;
	}
	
	public String distributeMedication(final String projectCode, final String medicationPackageId, final String sysUser) throws RemoteException{
		
		String packageIdentifier = (String)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				
				Query q = session.createQuery("select p from MedicationPackage p where p.packageId = :medsPackageId and p.projectCode = :projCode and p.status = :status")
				.setString("medsPackageId", medicationPackageId)
				.setString("projCode", projectCode)
				.setString("status", PackageStatus.allocated.toString());
				
				MedicationPackage p = (MedicationPackage)q.uniqueResult();
				
				if(p != null){
					p.setStatus(PackageStatus.distributed.toString());
					
					MedsPackageStatusChangeEvent distribution = new MedsPackageStatusChangeEvent(sysUser, new Date(), StatusChangeEventType.packageDistribution, null, p);
					p.addStatusChangeEvent(distribution);
					
					session.saveOrUpdate(p);
					
					return p.getPackageId();
				}else{
					return null;
				}
				
							
				
			}
			

		});
		if(packageIdentifier == null){
			throw new RemoteException("Medication Package " + medicationPackageId + " wasn't found or it was not eligible for distribution.");
		}
		return packageIdentifier;
	}
	
	
	protected Treatment getPersistedTreatmentForNewMedsPackage(MedicationPackage p){
	
		final String projCode = p.getProjectCode();
		final String treatmentCode = p.getTreatment().getTreatmentCode();
		
		Treatment t = (Treatment)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				Query q = session.createQuery("select t from Project p join p.treatments t where t.treatmentCode = :treatmentCode and p.projectCode = :projCode").
				setString("treatmentCode", treatmentCode).
				setString("projCode", projCode);
				
				Treatment t = (Treatment)q.uniqueResult();
				
				return t;
			}
		});

		return t;
	}
	
	
	protected Pharmacy getPersistedPharmacyForNewMedsPackage(MedicationPackage p){
		
		final String projCode = p.getProjectCode();
		final String pharmacyCode = p.getPharmacy().getPharmacyCode();
		
		
		Pharmacy ph = (Pharmacy)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				Query q = session.createQuery("select ph from Project p join p.centres c join c.pharmacies ph where p.projectCode = :projCode and ph.pharmacyCode = :pharmCode").
				setString("pharmCode", pharmacyCode).
				setString("projCode", projCode);
				
				Pharmacy ph = (Pharmacy)q.uniqueResult();
				
				return ph;
			}
		});
		
		return ph;
				
	}
	

}
