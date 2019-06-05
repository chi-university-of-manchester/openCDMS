package org.psygrid.dataimport.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.hibernate.Identifier;
import org.psygrid.dataimport.identifier.CharacterDelimeter;
import org.psygrid.dataimport.identifier.CustomParser;
import org.psygrid.dataimport.identifier.Delimeter;
import org.psygrid.dataimport.identifier.DelimeterNotFoundException;
import org.psygrid.dataimport.identifier.DirectTranslation;
import org.psygrid.dataimport.identifier.IdentifierInfo;
import org.psygrid.dataimport.identifier.InfoTranslator;
import org.psygrid.dataimport.identifier.ParserException;
import org.psygrid.dataimport.identifier.PosDelimeter;
import org.psygrid.dataimport.identifier.RangeTranslation;
import org.psygrid.dataimport.identifier.RightEdgeDelimeter;
import org.psygrid.dataimport.identifier.AbstractParser;
import org.psygrid.dataimport.identifier.Translation;
import org.psygrid.dataimport.identifier.TranslationException;
import org.psygrid.dataimport.identifier.AbstractParser.InfoType;
import org.psygrid.dataimport.identifier.Delimeter.ReferenceEdge;

import com.thoughtworks.xstream.XStream;

public class CustomIdentifierTest {

	private List<CustomParser> customParsers;
	
	/**
	 * @param args
	 * @throws DelimeterNotFoundException 
	 * @throws ParserException 
	 * @throws IOException 
	 * @throws TranslationException 
	 */
	public static void main(String[] args) throws ParserException, DelimeterNotFoundException, IOException, TranslationException {
		
		CustomParser dare = createTestParser4();
		
		Identifier dareParticipantId = dare.createIdentifier("SWPEXC001HH");
		
		CustomParser parser1 = createTestParser1();
		
		//We want to be able to persist the parser then load it up again!
		//So let's try that now...
		
		XStream xStream = new XStream();
				
		String parserString = xStream.toXML(parser1);
		
		String file = "C:\\Users\\Bill\\customParser.xml";
		
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		try {
			out.write(parserString);
			out.flush();
		} finally {
			out.close();
		}
		
		parser1 = (CustomParser)xStream.fromXML(loadToString(file));
		
		Identifier newIdentifier = parser1.createIdentifier("OLK/214");
		//System.out.println(newIdentifier.getIdentifier());
		
		//System.out.println(newIdentifier.getIdentifier());

		CustomParser parser2 = createTestParser2();
		//AAA-BBB/CC-DD/EE
		//9924 is the centre info.
		//453 is the unique study id
		Identifier newIdentifier2 = parser2.createIdentifier("OLK-9924/Hillingdon-453/released");
		//System.out.println(newIdentifier2.getIdentifier());
		
		
		CustomParser parser3 = createTestParser3();
		
		parserString = xStream.toXML(parser3);
	
		out = new BufferedWriter(new FileWriter(file));
		try {
			out.write(parserString);
			out.flush();
		} finally {
			out.close();
		}
		
		parser3 = null;
		parser3 = (CustomParser)xStream.fromXML(loadToString(file));
		
		Identifier newId3 = parser3.createIdentifier("1245");
		Identifier newId4 = parser3.createIdentifier("2245");
		Identifier newId5 = parser3.createIdentifier("3245");
		Identifier newId6 = parser3.createIdentifier("4245");
		Identifier newId7 = parser3.createIdentifier("5245");
		Identifier newId8 = parser3.createIdentifier("6245");
		Identifier newId9 = parser3.createIdentifier("7245");
		Identifier newId10 = parser3.createIdentifier("8245");
		Identifier newId11 = parser3.createIdentifier("9245");
		
		System.out.println(newId3.getIdentifier());
		System.out.println(newId4.getIdentifier());
		System.out.println(newId5.getIdentifier());
		System.out.println(newId6.getIdentifier());
		System.out.println(newId7.getIdentifier());
		System.out.println(newId8.getIdentifier());
		System.out.println(newId9.getIdentifier());
		System.out.println(newId10.getIdentifier());
		System.out.println(newId11.getIdentifier());
	}
	
	public CustomIdentifierTest(){
		
		//Create ten identifiers of different types, and make sure they can all be parsed correctly.
		
		
		
	}
	
	public static String loadToString(String file) throws FileNotFoundException, IOException {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(file));
			StringBuilder builder = new StringBuilder(2500);
			String s = null;
			while ((s = in.readLine()) != null) {
				builder.append(s);
				builder.append(System.getProperty("line.separator")); //$NON-NLS-1$
			}
			return builder.toString();
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
	
	/**
	 * This creates a parser for studies whose identifiers are in the following format:
	 * AAA/BB where AAA is the project code and BB is the unique identifier.
	 * This is a one-centre study, so this is implicit.
	 * 
	 * The delimeter - '/' - is guaranteed to be at index 4 in the string, so this could be set up as
	 * either a positional or a character delimeter.
	 * 
	 * @return
	 */
	private static CustomParser createTestParser1(){
		
		//Start building each IdentifierInfo piece (one for each required piece of info - such as centre, participant identifier).
		
		//Build up the delimeters for the unique participant identifier.
		CharacterDelimeter leftHandParticipantIdDelimeter = new CharacterDelimeter("/", 1, ReferenceEdge.LH);
		RightEdgeDelimeter rightHandDelimeter = new RightEdgeDelimeter();
		
		IdentifierInfo participantIdInfo = new IdentifierInfo(AbstractParser.InfoType.UNIQUE_STUDY_ID, leftHandParticipantIdDelimeter, rightHandDelimeter);
		
		//And that is all we need to do, because participant identifier is the only information defined explicitly within the external id string.
		
		List<IdentifierInfo> infoList = new ArrayList();
		infoList.add(participantIdInfo);
		
		//The implicit information needs to be added. The centre code will be "001"
		Map<AbstractParser.InfoType, Object> implicitInfoMap = new HashMap<AbstractParser.InfoType, Object>();
		
		implicitInfoMap.put(AbstractParser.InfoType.CENTRE_INFO, "001");
		
		CustomParser parser1 = new CustomParser("T1", 3, infoList, implicitInfoMap);
		
		return parser1;
		
	}
	
	/**
	 * The format of the parser is AAA-BBB/CC-DD/EE
	 * AAA - the project code (mandatory)
	 * BBB - the centre code (mandatory, variable size)
	 * CC - the site Code (mandatory, fixed size)
	 * DD - the participant identifier (mandatory, fixed size)
	 * EE - participant status code (optional, variable size)
	 * 
	 * @return
	 */
	private static CustomParser createTestParser2(){
		
		//Create the delimeters for capturing the centre code.
		CharacterDelimeter leftHandCentreCodeDelimeter = new CharacterDelimeter("-",1, ReferenceEdge.LH);
		CharacterDelimeter rightHandCentreCodeDelimeter = new CharacterDelimeter("/", leftHandCentreCodeDelimeter, 1, ReferenceEdge.RH);
		
		IdentifierInfo centreInfo = new IdentifierInfo(AbstractParser.InfoType.CENTRE_INFO, leftHandCentreCodeDelimeter, rightHandCentreCodeDelimeter);
		
		List<IdentifierInfo> idInfoList = new ArrayList<IdentifierInfo>();
		idInfoList.add(centreInfo);
		
		CharacterDelimeter leftHandParticipantDelimeter = new CharacterDelimeter("-", 2, ReferenceEdge.LH);
		PosDelimeter rightHandParticipantDelimeter = new PosDelimeter(3, leftHandParticipantDelimeter, ReferenceEdge.RH);
		
		IdentifierInfo idInfo = new IdentifierInfo(AbstractParser.InfoType.UNIQUE_STUDY_ID, leftHandParticipantDelimeter, rightHandParticipantDelimeter);
		idInfoList.add(idInfo);
		
		CustomParser parser2 = new CustomParser("T2", 3, idInfoList, null);
		
		
		//NB We can't really do this. The parsers do NOT cope with optionally-displayed information in the
		//middle of the external identifier string.
		
		return parser2;
	}
	
	/**
	 * This mimics what is needed for the MDS study.
	 * The format is this : XAAAAA
	 * where the entirety of the identifier is the unique study id.
	 * The first letter denotes the centre information - however it needs to be translated.
	 * So, for example, a '1' will be translated to 'Centre 1' and a '2' will be translated to 'Centre 2'.
	 * 
	 * @return
	 */
	private static CustomParser createTestParser3(){
		
		PosDelimeter lhUniqueIdDelimeter = new PosDelimeter(-1, ReferenceEdge.LH);
		RightEdgeDelimeter rhUniqueIdDelimeter = rhUniqueIdDelimeter = new RightEdgeDelimeter();
		
		IdentifierInfo uniqueIdInfo = new IdentifierInfo(AbstractParser.InfoType.UNIQUE_STUDY_ID, lhUniqueIdDelimeter, rhUniqueIdDelimeter);
		
		List<IdentifierInfo> idInfoList = new ArrayList<IdentifierInfo>();
		idInfoList.add(uniqueIdInfo);
		
		IdentifierInfo centreInfo = new IdentifierInfo(AbstractParser.InfoType.CENTRE_INFO, lhUniqueIdDelimeter, rhUniqueIdDelimeter);
		
		idInfoList.add(centreInfo);
		
		CustomParser parser3 = new CustomParser("MDS", 4, idInfoList, null);
		
		//Create the info translator that will translate the centre value picked up in the external id
		//To an actual centre code.
		
		RangeTranslation range1 = new RangeTranslation(InfoType.CENTRE_INFO, 1000, 1999, "1001");
		RangeTranslation range2 = new RangeTranslation(InfoType.CENTRE_INFO, 2000, 2999, "2001");
		RangeTranslation range3 = new RangeTranslation(InfoType.CENTRE_INFO, 3000, 3999, "3001");
		RangeTranslation range4 = new RangeTranslation(InfoType.CENTRE_INFO, 4000, 4999, "4001");
		RangeTranslation range5 = new RangeTranslation(InfoType.CENTRE_INFO, 5000, 5999, "5001");
		RangeTranslation range6 = new RangeTranslation(InfoType.CENTRE_INFO, 6000, 6999, "6001");
		RangeTranslation range7 = new RangeTranslation(InfoType.CENTRE_INFO, 7000, 7999, "7001");
		RangeTranslation range8 = new RangeTranslation(InfoType.CENTRE_INFO, 8000, 8999, "8001");
		RangeTranslation range9 = new RangeTranslation(InfoType.CENTRE_INFO, 9000, 9999, "9001");
		
		List<Translation> translations = new ArrayList<Translation>();
		translations.add(range1);
		translations.add(range2);
		translations.add(range3);
		translations.add(range4);
		translations.add(range5);
		translations.add(range6);
		translations.add(range7);
		translations.add(range8);
		translations.add(range9);
		
		InfoTranslator translator = new InfoTranslator(translations);
		
		parser3.setTranslator(translator);
		
		return parser3;
	}
	
	/**
	 * This test using the system to potentially generate external identifiers in anticipation that some studies may want to use their own
	 * style of identifier to drive the openCDMS user interface.
	 * 
	 * The parser below is per requirements for the DARE study, the 1st one to require the system to generate an external identifier.
	 * 
	 * @return
	 */
	private static CustomParser createTestParser4(){
	
		//The format of the DARE external identifier is as follows:
		/*
		Royal Devon and Exeter NHS Foundation Trust: SWPEX0001JB
		Plymouth Hospitals NHS Trust: SWPPL0001JB
		South Devon Healthcare NHS Foundation Trust (Torbay): SWPTB0001JB
		Royal Cornwall Hospitals NHS Trust (Treliske): SWPTRC0001JB
		Northern Devon Healthcare NHS Trust (Barnstaple): SWPBRN0001JB
		Weston Area Health NHS Trust: SWPWS0001JB
		Salford Royal NHS Foundation Trust: NWSAL0001JB
		Lancashire Teaching Hospitals (Preston): NWPRS0001JB
		East Lancashire Hospitals NHS Trust (Blackburn): NWBLK0001JB
		Barts and the London NHS Trust: NELBL0001JB
		North Cumbria University Hospitals NHS Trust: NECUM0001JB
		Newcastle Acute Hospitals NHS Trust: NENEW0001JB
		*/

		//It is clear that there are four aspects to each identifier:
		//1) The data collection region (e.g. 'SWP')
		//2) The site within that region (e.g. 'EX')
		//3) The unique patient identifier
		//4) The participant's initials
		
		List<String> uniqueCentreValues = new ArrayList<String>();
		uniqueCentreValues.add("SWPEX");
		uniqueCentreValues.add("SWPEXC");
		
		List<IdentifierInfo> idInfoList = new ArrayList<IdentifierInfo>();
		
		IdentifierInfo centreInfo = new IdentifierInfo(AbstractParser.InfoType.CENTRE_INFO, uniqueCentreValues);
		
		idInfoList.add(centreInfo);
		
		Delimeter centreEndDelimiter = centreInfo.getStartDelimeter();
		
		RightEdgeDelimeter rED = new RightEdgeDelimeter();
		PosDelimeter rhUniqueDelimiter = new PosDelimeter(-2, rED, ReferenceEdge.RH);
		
		IdentifierInfo uniqueIdInfo = new IdentifierInfo(AbstractParser.InfoType.UNIQUE_STUDY_ID, centreEndDelimiter, rhUniqueDelimiter);
		
		idInfoList.add(uniqueIdInfo);
		
		List<String> projectCodes = new ArrayList<String>();
		projectCodes.add("DAREP");
		projectCodes.add("DAREC");
		
		CustomParser dareParser = new CustomParser(projectCodes, 3, idInfoList, null);
		
		//Now we need to add translations for the centre code to the project code.
		List<Translation> translations = new ArrayList<Translation>();
		Translation exeterControl = new DirectTranslation(InfoType.PROJECT, InfoType.CENTRE_INFO, "DAREC", "SWPEXC");
		Translation exeterPatient = new DirectTranslation(InfoType.PROJECT, InfoType.CENTRE_INFO, "DAREP", "SWPEX");
		translations.add(exeterControl);
		translations.add(exeterPatient);
		
		InfoTranslator translator = new InfoTranslator(translations);

		dareParser.setTranslator(translator);

		return dareParser;
	}

}
