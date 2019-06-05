/*
Copyright (c) 2006-2008, The University of Manchester, UK.

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
package org.psygrid.neden;

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

public class AdverseOutcomesCarerViolent extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        ValidationRule after1900 = ValidationRulesWrapper.instance().getRule("After 1900");

        Document doc = factory.createDocument("Adverse Outcomes Detailed " +
                "Questionnaire (Carer, Violence)", "Adverse Outcomes Detailed " +
                "Questionnaire (Carer, Violence)");

        createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main section");
        doc.addSection(mainSec);
        mainSec.setDisplayText("Main");
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence("Main " +
                "Section Occurrence");
        mainSec.addOccurrence(mainSecOcc);
        mainSecOcc.setDisplayText("Main");

        DateEntry when = factory.createDateEntry("When harmed others", "When " +
                "did you physically harm other(s)?");
        doc.addEntry(when);
        when.setSection(mainSec);
        when.addValidationRule(after1900);

        OptionEntry howMany = factory.createOptionEntry("Number of victims",
                "How many victims were there?");
        doc.addEntry(howMany);
        howMany.setSection(mainSec);
        howMany.addOption(factory.createOption("1", "1", 1));
        howMany.addOption(factory.createOption("2", "2", 2));
        howMany.addOption(factory.createOption(">2", ">2", 3));

        OptionEntry injurySelf = factory.createOptionEntry("Injury to self",
                "Injury to self during assault (record most serious)");
        doc.addEntry(injurySelf);
        injurySelf.setSection(mainSec);
        injurySelf.addOption(factory.createOption("No injury", "No injury", 0));
        injurySelf.addOption(factory.createOption("Bruises, cuts", "Bruises, cuts", 1));
        injurySelf.addOption(factory.createOption("Broken bones/teeth", "Broken bones/teeth", 2));
        injurySelf.addOption(factory.createOption("Unconscious, internal injuries", "Unconscious, internal injuries", 3));
        injurySelf.addOption(factory.createOption("Stab/gunshot", "Stab/gunshot", 4));
        Option injurySelfOther = factory.createOption("Other (please specify)", "Other (please specify)", 5);
        injurySelfOther.setTextEntryAllowed(true);
        injurySelf.addOption(injurySelfOther);

        OptionEntry hospital = factory.createOptionEntry("Went to hospital",
                "If you yourself were injured during the assault, did you go " +
                        "to general hospital?");
        doc.addEntry(hospital);
        hospital.setSection(mainSec);
        hospital.addOption(factory.createOption("No", "No", 0));
        hospital.addOption(factory.createOption("Yes", "Yes", 1));

        OptionEntry mainVictim = factory.createOptionEntry("Main victim",
                "Who was the main victim? (most seriously injured)");
        doc.addEntry(mainVictim);
        mainVictim.setSection(mainSec);
        mainVictim.addOption(factory.createOption("Spouse/female cohabitee/male " +
                "cohabitee", "Spouse/female cohabitee/male cohabitee", 1));
        mainVictim.addOption(factory.createOption("Ex - any of the above",
                "Ex - any of the above", 2));
        mainVictim.addOption(factory.createOption("Girlfriend/boyfriend/lover/" +
                "non-cohabitee/ex-girl/boyfriend", "Girlfriend/boyfriend/lover/" +
                "non-cohabitee/ex-girl/boyfriend", 3));
        mainVictim.addOption(factory.createOption("Child - non-spank", "Child - non-spank", 4));
        mainVictim.addOption(factory.createOption("Child - spank", "Child - spank", 5));
        mainVictim.addOption(factory.createOption("Male parental figure", "Male parental figure", 6));
        mainVictim.addOption(factory.createOption("Female parental figure", "Female parental figure", 7));
        mainVictim.addOption(factory.createOption("Other family", "Other family", 8));
        mainVictim.addOption(factory.createOption("Friend", "Friend", 9));
        mainVictim.addOption(factory.createOption("Health/care staff", "Health/care staff", 10));
        mainVictim.addOption(factory.createOption("Police", "Police", 11));
        mainVictim.addOption(factory.createOption("Stranger", "Stranger", 12));
        Option mainVictimOther = factory.createOption("Other (please specify)",
                "Other (please specify)", 13);
        mainVictimOther.setTextEntryAllowed(true);
        mainVictim.addOption(mainVictimOther);

        OptionEntry mainVictimGender = factory.createOptionEntry("Main victim " +
                "- gender", "Main victim - gender");
        doc.addEntry(mainVictimGender);
        mainVictimGender.setSection(mainSec);
        mainVictimGender.addOption(factory.createOption("Male", "Male", 1));
        mainVictimGender.addOption(factory.createOption("Female", "Female", 2));

        OptionEntry mainVictimHow = factory.createOptionEntry("How was main " +
                "victim harmed",
                "How did you harm main victim? (record most serious)");
        doc.addEntry(mainVictimHow);
        mainVictimHow.setSection(mainSec);
        mainVictimHow.addOption(factory.createOption("Pushed, grabbed",
                "Pushed, grabbed", 1));
        mainVictimHow.addOption(factory.createOption("Slapped, scratched",
                "Slapped, scratched", 2));
        mainVictimHow.addOption(factory.createOption("Bit", "Bit", 3));
        mainVictimHow.addOption(factory.createOption("Kicked / hit with knee",
                "Kicked / hit with knee", 4));
        mainVictimHow.addOption(factory.createOption("Hit with a fist",
                "Hit with a fist", 5));
        mainVictimHow.addOption(factory.createOption("Hit with head",
                "Hit with head", 6));
        mainVictimHow.addOption(factory.createOption("Strangulated/suffocated/choked",
                "Strangulated/suffocated/choked", 7));
        mainVictimHow.addOption(factory.createOption("Sexually assaulted",
                "Sexually assaulted", 9));
        mainVictimHow.addOption(factory.createOption("Knife or other sharp " +
                "instrument", "Knife or other sharp instrument", 10));
        mainVictimHow.addOption(factory.createOption("Blunt instrument (eg " +
                "baseball bat, brick)", "Blunt instrument (eg baseball bat, " +
                "brick)", 11));
        mainVictimHow.addOption(factory.createOption("Fired gun",
                "Fired gun", 13));
        mainVictimHow.addOption(factory.createOption("Hit with car", "Hit with car", 14));
        mainVictimHow.addOption(factory.createOption("Arson/fire/burns",
                "Arson/fire/burns", 15));
        mainVictimHow.addOption(factory.createOption("Threw liquid at someone",
                "Threw liquid at someone", 16));
        mainVictimHow.addOption(factory.createOption("Maced someone (or other " +
                "spray/gas)", "Maced someone (or other spray/gas)", 17));
        Option mainVictimHowOther = factory.createOption(
                "Other (please specify)", "Other (please specify)", 18);
        mainVictimHowOther.setTextEntryAllowed(true);
        mainVictimHow.addOption(mainVictimHowOther);

        OptionEntry mainVictimInjury = factory.createOptionEntry("Injury to " +
                "main victim", "Injury to main victim (record most serious)");
        doc.addEntry(mainVictimInjury);
        mainVictimInjury.setSection(mainSec);
        mainVictimInjury.addOption(factory.createOption("No injury",
                "No injury", 0));
        mainVictimInjury.addOption(factory.createOption("Bruises, cuts",
                "Bruises, cuts", 1));
        mainVictimInjury.addOption(factory.createOption("Broken bones/teeth",
                "Broken bones/teeth", 2));
        mainVictimInjury.addOption(factory.createOption("Unconscious, " +
                "internal injuries", "Unconscious, internal injuries", 3));
        mainVictimInjury.addOption(factory.createOption("Stab/gunshot",
                "Stab/gunshot", 4));
        mainVictimInjury.addOption(factory.createOption("Death", "Death", 5));
        mainVictimInjury.addOption(factory.createOption("Other", "Other", 7));

        OptionEntry mainVictimHospital = factory.createOptionEntry("Main victim " +
                "went to hospital",
                "If the main victim was injured, did they go to general " +
                        "hospital?");
        doc.addEntry(mainVictimHospital);
        mainVictimHospital.setSection(mainSec);
        mainVictimHospital.addOption(factory.createOption("No", "No", 0));
        mainVictimHospital.addOption(factory.createOption("Yes", "Yes", 1));

        OptionEntry secondVictim = factory.createOptionEntry("Second victim",
                "Who was the second victim? (second most seriously injured)");
        doc.addEntry(secondVictim);
        secondVictim.setSection(mainSec);
        secondVictim.addOption(factory.createOption(
                "Spouse/female cohabitee/male " + "cohabitee",
                "Spouse/female cohabitee/male cohabitee", 1));
        secondVictim.addOption(factory.createOption("Ex - any of the above",
                "Ex - any of the above", 2));
        secondVictim.addOption(factory.createOption("Girlfriend/boyfriend/lover/"
                + "non-cohabitee/ex-girl/boyfriend",
                "Girlfriend/boyfriend/lover/"
                        + "non-cohabitee/ex-girl/boyfriend", 3));
        secondVictim.addOption(factory.createOption("Child - non-spank",
                "Child - non-spank", 4));
        secondVictim.addOption(factory.createOption("Child - spank",
                "Child - spank", 5));
        secondVictim.addOption(factory.createOption("Male parental figure",
                "Male parental figure", 6));
        secondVictim.addOption(factory.createOption("Female parental figure",
                "Female parental figure", 7));
        secondVictim.addOption(factory.createOption("Other family",
                "Other family", 8));
        secondVictim.addOption(factory.createOption("Friend", "Friend", 9));
        secondVictim.addOption(factory.createOption("Health/care staff",
                "Health/care staff", 10));
        secondVictim.addOption(factory.createOption("Police", "Police", 11));
        secondVictim.addOption(factory.createOption("Stranger", "Stranger", 12));
        Option secondVictimOther = factory.createOption(
                "Other (please specify)", "Other (please specify)", 13);
        secondVictimOther.setTextEntryAllowed(true);
        secondVictim.addOption(secondVictimOther);

        OptionEntry secondVictimGender = factory.createOptionEntry(
                "Second victim - gender", "Second victim - gender");
        doc.addEntry(secondVictimGender);
        secondVictimGender.setSection(mainSec);
        secondVictimGender.addOption(factory.createOption("Male", "Male", 1));
        secondVictimGender.addOption(factory.createOption("Female", "Female", 2));

        OptionEntry secondVictimHow = factory.createOptionEntry("How was " +
                "second victim harmed",
                "Who did you harm the second victim? (record most serious)");
        doc.addEntry(secondVictimHow);
        secondVictimHow.setSection(mainSec);
        secondVictimHow.addOption(factory.createOption("Pushed, grabbed",
                "Pushed, grabbed", 1));
        secondVictimHow.addOption(factory.createOption("Slapped, scratched",
                "Slapped, scratched", 2));
        secondVictimHow.addOption(factory.createOption("Bit", "Bit", 3));
        secondVictimHow.addOption(factory.createOption("Kicked / hit with knee",
                "Kicked / hit with knee", 4));
        secondVictimHow.addOption(factory.createOption("Hit with a fist",
                "Hit with a fist", 5));
        secondVictimHow.addOption(factory.createOption("Hit with head",
                "Hit with head", 6));
        secondVictimHow.addOption(factory.createOption(
                "Strangulated/suffocated/choked",
                "Strangulated/suffocated/choked", 7));
        secondVictimHow.addOption(factory.createOption("Sexually assaulted",
                "Sexually assaulted", 9));
        secondVictimHow.addOption(factory.createOption("Knife or other sharp "
                + "instrument", "Knife or other sharp instrument", 10));
        secondVictimHow.addOption(factory.createOption("Blunt instrument (eg "
                + "baseball bat, brick)", "Blunt instrument (eg baseball bat, "
                + "brick)", 11));
        secondVictimHow.addOption(factory.createOption("Fired gun", "Fired gun",
                13));
        secondVictimHow.addOption(factory.createOption("Hit with car",
                "Hit with car", 14));
        secondVictimHow.addOption(factory.createOption("Arson/fire/burns",
                "Arson/fire/burns", 15));
        secondVictimHow.addOption(factory.createOption("Threw liquid at someone",
                "Threw liquid at someone", 16));
        secondVictimHow.addOption(factory.createOption("Maced someone (or other "
                + "spray/gas)", "Maced someone (or other spray/gas)", 17));
        Option secondVictimHowOther = factory.createOption(
                "Other (please specify)", "Other (please specify)", 18);
        secondVictimHowOther.setTextEntryAllowed(true);
        secondVictimHow.addOption(secondVictimHowOther);

        OptionEntry secondVictimInjury = factory.createOptionEntry("Injury to "
                + "second victim", "Injury to second victim (record most serious)");
        doc.addEntry(secondVictimInjury);
        secondVictimInjury.setSection(mainSec);
        secondVictimInjury.addOption(factory.createOption("No injury",
                "No injury", 0));
        secondVictimInjury.addOption(factory.createOption("Bruises, cuts",
                "Bruises, cuts", 1));
        secondVictimInjury.addOption(factory.createOption("Broken bones/teeth",
                "Broken bones/teeth", 2));
        secondVictimInjury.addOption(factory.createOption("Unconscious, "
                + "internal injuries", "Unconscious, internal injuries", 3));
        secondVictimInjury.addOption(factory.createOption("Stab/gunshot",
                "Stab/gunshot", 4));
        secondVictimInjury.addOption(factory.createOption("Death", "Death", 5));
        secondVictimInjury.addOption(factory.createOption("Other", "Other", 7));

        OptionEntry secondVictimHospital = factory.createOptionEntry(
                "Second victim went to hospital",
                "If the second victim was injured, did they go to general "
                        + "hospital?");
        doc.addEntry(secondVictimHospital);
        secondVictimHospital.setSection(mainSec);
        secondVictimHospital.addOption(factory.createOption("No", "No", 0));
        secondVictimHospital.addOption(factory.createOption("Yes", "Yes", 1));

        OptionEntry locationIncident = factory.createOptionEntry("Location of "
                + "incident", "Location of incident");
        doc.addEntry(locationIncident);
        locationIncident.setSection(mainSec);
        locationIncident.addOption(factory.createOption("Your home", "Your home", 1));
        locationIncident.addOption(factory.createOption("Other residence", "Other residence", 2));
        locationIncident.addOption(factory.createOption("Store/bank etc.", "Store/bank etc.", 3));
        locationIncident.addOption(factory.createOption("Fast food restaurant", "Fast food restaurant", 4));
        locationIncident.addOption(factory.createOption("Bar/tavern", "Bar/tavern", 5));
        locationIncident.addOption(factory.createOption("Other commercial", "Other commercial", 6));
        locationIncident.addOption(factory.createOption("Work", "Work", 7));
        locationIncident.addOption(factory.createOption("School", "School", 8));
        locationIncident.addOption(factory.createOption("Outdoors", "Outdoors", 9));
        locationIncident.addOption(factory.createOption("Out-patient treatment " +
                "- general hospital", "Out-patient treatment - general hospital", 10));
        locationIncident.addOption(factory.createOption("Out-patient treatment " +
                "- psychiatric hospital", "Out-patient treatment - psychiatric " +
                "hospital", 11));
        locationIncident.addOption(factory.createOption("In-patient - general " +
                "hospital", "In-patient - general hospital", 12));
        locationIncident.addOption(factory.createOption("In-patient - " +
                "psychiatric hospital", "In-patient - psychiatric hospital", 13));
        locationIncident.addOption(factory.createOption("Police cell/vehicle",
                "Police cell/vehicle", 14));
        locationIncident.addOption(factory.createOption("Prison", "Prison", 15));
        Option locationIncidentInstOther = factory.createOption("Other " +
                "institution", "Other institution (please specify)", 16);
        locationIncidentInstOther.setTextEntryAllowed(true);
        locationIncident.addOption(locationIncidentInstOther);
        Option locationIncidentOther = factory.createOption("Other",
                "Other (please specify)", 17);
        locationIncidentOther.setTextEntryAllowed(true);
        locationIncident.addOption(locationIncidentOther);

        OptionEntry together = factory.createOptionEntry("What brought together",
                "What brought you and victim together?");
        doc.addEntry(together);
        together.setSection(mainSec);
        together.addOption(factory.createOption("Happened to be there",
                "Happened to be there (i.e. at bar or other social event)", 1));
        together.addOption(factory.createOption("Regularly scheduled activity",
                "Regularly scheduled activity - planned or contracted by both " +
                "parties (e.g. having meals at home)", 2));
        together.addOption(factory.createOption("Irregularly scheduled activity",
                "Irregularly scheduled activity - planned or contracted by both parties", 3));
        together.addOption(factory.createOption("Sought victim out",
                "You sought victim out - not a planned activity", 4));
        together.addOption(factory.createOption("Victim sought subject",
                "Victim sought subject out - not a planned activity", 5));
        Option togetherOther = factory.createOption("Other",
                "Other (please specify)", 6);
        togetherOther.setTextEntryAllowed(true);
        together.addOption(togetherOther);

        OptionEntry problem = factory.createOptionEntry(
                "Problem that led to incident",
                "What was the problem that led to this incident?");
        doc.addEntry(problem);
        problem.setSection(mainSec);
        problem.addOption(factory.createOption("Clearly unprovoked/no problem",
                "Clearly unprovoked/no problem", 0));
        problem.addOption(factory.createOption("Treatment of or possession of " +
                "children", "Treatment of or possession of children", 1));
        problem.addOption(factory.createOption("Infidelity/jealousy",
                "Infidelity/alleged infidelity/jealousy", 2));
        problem.addOption(factory.createOption("Personal rejection",
                "Personal rejection", 3));
        problem.addOption(factory.createOption("Family or domestic matters",
                "Family or domestic matters", 4));
        problem.addOption(factory.createOption("Money", "Money", 5));
        problem.addOption(factory.createOption("Possession of goods/property",
                "Possession of goods/property", 6));
        problem.addOption(factory.createOption("Destruction or violation of " +
                "property", "Destruction or violation of property", 7));
        problem.addOption(factory.createOption("Stealing of money, property, " +
                "or goods", "Stealing of money, property, or goods", 8));
        problem.addOption(factory.createOption("Alcohol or drug use",
                "Alcohol or drug use", 9));
        problem.addOption(factory.createOption("Embarrassing someone",
                "Embarrassing someone", 10));
        problem.addOption(factory.createOption("Failure to live up to obligations",
                "Failure to live up to personal or business obligations", 11));
        problem.addOption(factory.createOption("Physical violation",
                "Physical violation", 12));
        problem.addOption(factory.createOption("Physical assault",
                "Physical assault", 13));
        problem.addOption(factory.createOption("Medication", "Medication", 14));
        problem.addOption(factory.createOption("Verbal assault/insult",
                "Verbal assault/insult", 15));
        problem.addOption(factory.createOption("Coming to someone's defence",
                "Coming to someone's defence", 16));
        problem.addOption(factory.createOption("Delusional at time of incident",
                "Delusional at time of incident", 17));
        Option problemOther = factory.createOption("Other",
                "Other (please specify)", 18);
        problemOther.setTextEntryAllowed(true);
        problem.addOption(problemOther);

        OptionEntry wanted = factory.createOptionEntry(
                "Had something wanted",
                "Did you do this because the person had something you wanted?");
        doc.addEntry(wanted);
        wanted.setSection(mainSec);
        wanted.addOption(factory.createOption("No", "No", 0));
        wanted.addOption(factory.createOption("Yes", "Yes", 1));

        OptionEntry victimAlcohol = factory.createOptionEntry(
                "Victim drinking alcohol",
                "Do you think the main victim was drinking alcohol just before " +
                        "this happened?");
        doc.addEntry(victimAlcohol);
        victimAlcohol.setSection(mainSec);
        victimAlcohol.addOption(factory.createOption("No", "No", 0));
        victimAlcohol.addOption(factory.createOption("Possibly", "Possibly", 1));
        victimAlcohol.addOption(factory.createOption("Yes", "Yes", 2));

        OptionEntry alcohol = factory.createOptionEntry(
                "Drinking alcohol",
                "Were you drinking alcohol just before this happened?");
        doc.addEntry(alcohol);
        alcohol.setSection(mainSec);
        alcohol.addOption(factory.createOption("No", "No", 0));
        alcohol.addOption(factory.createOption("Yes", "Yes", 1));

        OptionEntry victimDrugs = factory.createOptionEntry(
                "Victim street drugs",
                "Do you think the main victim was using any street drugs just " +
                        "before this happened?");
        doc.addEntry(victimDrugs);
        victimDrugs.setSection(mainSec);
        victimDrugs.addOption(factory.createOption("No", "No", 0));
        victimDrugs.addOption(factory.createOption("Possibly", "Possibly", 1));
        victimDrugs.addOption(factory.createOption("Yes", "Yes", 2));

        OptionEntry drugs = factory.createOptionEntry(
                "Street drugs",
                "Were you using any street drugs just before this happened?");
        doc.addEntry(drugs);
        drugs.setSection(mainSec);
        drugs.addOption(factory.createOption("No", "No", 0));
        drugs.addOption(factory.createOption("Yes", "Yes", 1));

        OptionEntry angry = factory.createOptionEntry(
                "Angry",
                "Were you angry just before this happened?");
        doc.addEntry(angry);
        angry.setSection(mainSec);
        angry.addOption(factory.createOption("No", "No", 0));
        angry.addOption(factory.createOption("Yes", "Yes", 1));

        OptionEntry delusions = factory.createOptionEntry(
                "Experiencing delusions",
                "Were you experiencing delusions at the time?");
        doc.addEntry(delusions);
        delusions.setSection(mainSec);
        delusions.addOption(factory.createOption("No", "No", 0));
        delusions.addOption(factory.createOption("Possibly", "Possibly", 1));
        delusions.addOption(factory.createOption("Yes", "Yes", 2));

        OptionEntry delusionsViolent = factory.createOptionEntry(
                "Delusions violent",
                "Were the delusions violent?");
        doc.addEntry(delusionsViolent);
        delusionsViolent.setSection(mainSec);
        delusionsViolent.addOption(factory.createOption("No", "No", 0));
        delusionsViolent.addOption(factory.createOption("Possibly", "Possibly", 1));
        delusionsViolent.addOption(factory.createOption("Yes", "Yes", 2));

        OptionEntry delusionsRelated = factory.createOptionEntry(
                "Delusions related to events",
                "Were delusions related to the victim/events?");
        doc.addEntry(delusionsRelated);
        delusionsRelated.setSection(mainSec);
        delusionsRelated.addOption(factory.createOption("No", "No", 0));
        delusionsRelated.addOption(factory.createOption("Possibly", "Possibly", 1));
        delusionsRelated.addOption(factory.createOption("Yes", "Yes", 2));

        OptionEntry voices = factory.createOptionEntry(
                "Hearing voices before event",
                "Were you hearing voices just before this happened? " +
                        "(hallucinations)");
        doc.addEntry(voices);
        voices.setSection(mainSec);
        voices.addOption(factory.createOption("No", "No", 0));
        voices.addOption(factory.createOption("Possibly", "Possibly", 1));
        voices.addOption(factory.createOption("Yes", "Yes", 2));

        OptionEntry voicesViolence = factory.createOptionEntry(
                "Voices talking about violence",
                "Were the voices talking about violence?");
        doc.addEntry(voicesViolence);
        voicesViolence.setSection(mainSec);
        voicesViolence.addOption(factory.createOption("No", "No", 0));
        voicesViolence.addOption(factory.createOption("Possibly", "Possibly", 1));
        voicesViolence.addOption(factory.createOption("Yes", "Yes", 2));

        OptionEntry voicesViolent = factory.createOptionEntry(
                "Voices telling to be violent",
                "Were the voices telling you to be violent?");
        doc.addEntry(voicesViolent);
        voicesViolent.setSection(mainSec);
        voicesViolent.addOption(factory.createOption("No", "No", 0));
        voicesViolent.addOption(factory.createOption("Possibly", "Possibly", 1));
        voicesViolent.addOption(factory.createOption("Yes", "Yes", 2));

        OptionEntry end = factory.createOptionEntry("Incident end",
                "How did the incident end");
        doc.addEntry(end);
        end.setSection(mainSec);
        end.addOption(factory.createOption("I left her/him alone",
                "I left her/him alone", 1));
        end.addOption(factory.createOption("They left me alone",
                "They left me alone", 2));
        end.addOption(factory.createOption("You left the scene",
                "You left the scene", 3));
        end.addOption(factory.createOption("Other person left the scene",
                "Other person left the scene", 4));
        end.addOption(factory.createOption("You apologised",
                "You apologised", 5));
        end.addOption(factory.createOption("Victim apologised",
                "Victim apologised", 6));
        end.addOption(factory.createOption("Just stopped", "Just stopped", 7));
        end.addOption(factory.createOption("You were knocked out", "You were knocked out, passed out, fell asleep", 8));
        end.addOption(factory.createOption("Other person was knocked out",
                "Other person was knocked out, passed out, fell asleep", 9));
        end.addOption(factory.createOption("Someone intervened (not police)",
                "Someone intervened (not police)", 10));
        end.addOption(factory.createOption("Police intervened",
                "Police intervened", 11));
        Option endOther = factory.createOption("Other",
                "Other (please specify)", 12);
        endOther.setTextEntryAllowed(true);
        end.addOption(endOther);

        OptionEntry timeOff = factory.createOptionEntry(
                "Time off from work/school",
                "Did anyone have to take any time off from work/school?");
        doc.addEntry(timeOff);
        timeOff.setSection(mainSec);
        timeOff.addOption(factory.createOption("No", "No", 0));
        timeOff.addOption(factory.createOption("Yes - victim (any)",
                "Yes - victim (any)", 1));
        timeOff.addOption(factory.createOption("Yes - you", "Yes - you", 2));

        OptionEntry arrested = factory.createOptionEntry(
                "Arrested",
                "Were you arrested?");
        doc.addEntry(arrested);
        arrested.setSection(mainSec);
        arrested.addOption(factory.createOption("No", "No", 0));
        arrested.addOption(factory.createOption("Yes", "Yes", 1));

        OptionEntry psych = factory.createOptionEntry(
                "Sent to psychiatric hospital",
                "Were you sent to a psychiatric hospital?");
        doc.addEntry(psych);
        psych.setSection(mainSec);
        psych.addOption(factory.createOption("No", "No", 0));
        psych.addOption(factory.createOption("Yes", "Yes", 1));


        return doc;
    }
}
