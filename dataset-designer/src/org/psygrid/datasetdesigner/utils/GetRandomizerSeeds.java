package org.psygrid.datasetdesigner.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.data.model.hibernate.EslCustomField;
import org.psygrid.datasetdesigner.model.GroupModel;
import org.psygrid.datasetdesigner.model.RandomisationHolderModel;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.randomization.model.RandomizerException;
import org.psygrid.randomization.model.hibernate.StratifiedRandomizer;
import org.psygrid.randomization.model.hibernate.Stratum;
import org.psygrid.www.xml.security.core.types.GroupType;

import randomX.randomHotBits;
import randomX.randomX;

public class GetRandomizerSeeds {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws RandomizerException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, RandomizerException {

		//First argument is the full path of the dataset file. From this the number of required seeds can
		//be determined.
		String dsName = args[0];
		PersistenceManager.getInstance().setAliases();
		
		//Second argument is the full path of the file that will contain the obtained seeds.
		String randomSeedsFile = args[1];
		
		Object obj1 = PersistenceManager.getInstance().load(dsName);
		
		StudyDataSet dSet = (StudyDataSet)obj1;
		
		StratifiedRandomizer rnd = new StratifiedRandomizer(dSet.getDs().getProjectCode());
		RandomisationHolderModel rhm  = dSet.getRandomHolderModel();
		
		if (rhm != null && rhm.getRandomisationStrata() != null &&
				rhm.getRandomisationStrata().size() > 0) {
			int seedlen = 0;
			for (int j=0; j<rhm.getRandomisationStrata().size(); j++) {
				Stratum s = rhm.getRandomisationStrata().get(j);

				configureStrataValues(s, dSet);

				rnd.addStratum(s);

				if (seedlen == 0) {
					seedlen = s.getValues().size();
				} else {
					seedlen *= s.getValues().size();
				}
			}

			int minBlockSize = rhm.getMinimumBlockSize();
			int maxBlockSize = rhm.getMaximumBlockSize();
			minBlockSize /= rhm.getRandomisationTreatments().size();
			maxBlockSize /= rhm.getRandomisationTreatments().size();

			rnd.generateCombinations("org.psygrid.randomization.model.hibernate.IBRpbrblRandomizer", minBlockSize, maxBlockSize);
			long[] seeds= new long[seedlen];

			for (int i=0; i<seedlen; i++) {
				seeds[i] = getSeed();
			}
			
			File seedFile = new File(randomSeedsFile);
			BufferedWriter writer = new BufferedWriter(new FileWriter(seedFile));
			
			for(int i = 0; i < seeds.length; i++){
				writer.write(Long.toString(seeds[i]));
				if(i != seeds.length - 1){
					writer.newLine();
				}
			}
			
			writer.flush();
			writer.close();

		}
	
		
		
		

	}

	public static GroupType[] getGroups(StudyDataSet dSet) {
		GroupType[] gta = null;
		ArrayList<GroupModel> groups = dSet.getGroups();
		if (gta == null) {
			gta = new GroupType[groups.size()];
			for (int i=0; i<groups.size(); i++) {
				gta[i] = new GroupType(groups.get(i).getGroup().getLongName(), 
						groups.get(i).getGroup().getName(), 
						dSet.getDs().getProjectCode());
			}
		}
		return gta;
	}
	
	public static void configureStrataValues(Stratum s, StudyDataSet dSet) {
		if (s.getName().equals("sex")) {
			ArrayList<String> sexValues = new ArrayList<String>(); 
			sexValues.add("Male");
			sexValues.add("Female");
			s.setValues(sexValues);
		}
		else if (s.getName().equals("centreNumber")) {
			ArrayList<String> centreValues = new ArrayList<String>();
			for ( GroupType g: getGroups(dSet) ) {
				centreValues.add(g.getIdCode());
			}
			s.setValues(centreValues);

		}
		else{
			for ( EslCustomField field: dSet.getEslCustomFields()){
				ArrayList<String> customFields = new ArrayList<String>();
				if ( s.getName().equals(field.getName()) ){
					for ( int i=0, c=field.getValueCount(); i<c; i++ ){
						customFields.add(field.getValue(i));
					}
				}
				s.setValues(customFields);
			}
		}
	}
	
	private static Long getSeed() {
		Long randomLong = null;
		try {
			randomX randomizer = new randomHotBits();
			randomLong = randomizer.nextLong();
		} catch (Exception ex) {
			randomLong = RandomGenerator.getInstance().nextLong();
		}

		//if randomLong still hasn't  been set then throw a new runtime exception
		if (randomLong == null) {
			throw new RuntimeException("Cannot connect to either random number generators (hotbits or random.org)");
		}
		
		return randomLong;
	}


}
