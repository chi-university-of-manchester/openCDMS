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


package org.psygrid.command;

import org.psygrid.common.ValidationRulesWrapper;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.AssessmentForm;

/**
 * @author Rob Harper
 *
 */
public class CTCH extends AssessmentForm {

    public static Document createDocument(Factory factory) {

        Document doc = factory.createDocument(
                "CTCH",
                "CTCH - Treatment Adherence Protocol");

        createDocumentStatuses(factory, doc);

        Section mainSec = factory.createSection("Main Section", "Main");
        doc.addSection(mainSec);
        SectionOccurrence mainSecOcc = factory.createSectionOccurrence("Main Sec Occ");
        mainSec.addOccurrence(mainSecOcc);

        NarrativeEntry n1 = factory.createNarrativeEntry("N1", "Engagement Phase");
        doc.addEntry(n1);
        n1.setSection(mainSec);
        n1.setStyle(NarrativeStyle.HEADER);

        OptionEntry ep1 = factory.createOptionEntry("Establishment of rapport",
                "Establishment of rapport: Did the therapist successfully establish rapport " +
                        "and trust: used empathic listening; explored beliefs and psychotic experiences " +
                        "in a non-judgemental way; helped the client feel understood.");
        doc.addEntry(ep1);
        ep1.setSection(mainSec);
        ep1.setLabel("1");
        createOptions(factory, ep1, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							new int[]{1,2,3,4,5,});

        OptionEntry ep2 = factory.createOptionEntry("Normalising",
                "Normalising: Did the therapist help the client to recognise that their psychotic experiences " +
                        "are similar to the experiences of many people who have not been diagnosed with a mental illness?");
        doc.addEntry(ep2);
        ep2.setSection(mainSec);
        ep2.setLabel("2");
        createOptions(factory, ep2, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							new int[]{1,2,3,4,5,});

        OptionEntry ep3 = factory.createOptionEntry("Addressing Engagement Beliefs",
                "Addressing Engagement Beliefs: Did the therapist explore and address any beliefs that may " +
                        "threaten engagement: inability to change; resistance by voices; inability of the therapist " +
                        "to understand experiences?");
        doc.addEntry(ep3);
        ep3.setSection(mainSec);
        ep3.setLabel("3");
        createOptions(factory, ep3, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							new int[]{1,2,3,4,5,});

        NarrativeEntry n2 = factory.createNarrativeEntry("N2", "Establishing The Basis For Intervention");
        doc.addEntry(n2);
        n2.setSection(mainSec);
        n2.setStyle(NarrativeStyle.HEADER);

        OptionEntry bfi1 = factory.createOptionEntry("Relocating the Problem at B",
                "Relocating the Problem at B: Did the therapist help the client to view the problem as a belief " +
                        "instead of hearing a voice per se and/or the emotional/behavioural distress associated with it.");
        doc.addEntry(bfi1);
        bfi1.setSection(mainSec);
        bfi1.setLabel("1");
        createOptions(factory, bfi1, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							new int[]{1,2,3,4,5,});

        OptionEntry bfi2 = factory.createOptionEntry("Agreeing the Beliefs to be Targeted",
                "Agreeing the Beliefs to be Targeted: Did the therapist develop a collaborative description of the " +
                        "beliefs concerning Power and Compliance and agree which beliefs and in which order they would be tackled?");
        doc.addEntry(bfi2);
        bfi2.setSection(mainSec);
        bfi2.setLabel("2");
        createOptions(factory, bfi2, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							new int[]{1,2,3,4,5,});

        OptionEntry bfi3 = factory.createOptionEntry("Clarifying the Evidence for Beliefs",
                "Clarifying the Evidence for Beliefs: Did the therapist assess the evidence that the client uses to " +
                        "support the beliefs about Power and Compliance.");
        doc.addEntry(bfi3);
        bfi3.setSection(mainSec);
        bfi3.setLabel("3");
        createOptions(factory, bfi3, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							new int[]{1,2,3,4,5,});

        NarrativeEntry n3 = factory.createNarrativeEntry("N3", "INTERVENTION PHASE: Power; Control & Compliance Beliefs");
        doc.addEntry(n3);
        n3.setSection(mainSec);
        n3.setStyle(NarrativeStyle.HEADER);

        OptionEntry ip1 = factory.createOptionEntry("Reviewing & Enhancing Coping Strategies",
                "Reviewing & Enhancing Coping Strategies: Did the therapist systematically review the effectiveness " +
                        "of the client's coping strategies for addressing " +
                        "power imbalances, reducing compliance and improving control (ie: reviewing when they were used; how consistently " +
                        "they are applied and how effective they were)? Were efforts subsequently made to improve these and introduce " +
                        "further strategies where appropriate?");
        doc.addEntry(ip1);
        ip1.setSection(mainSec);
        ip1.setLabel("1");
        createOptions(factory, ip1, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							new int[]{1,2,3,4,5,});

        OptionEntry ip2 = factory.createOptionEntry("Disputing Power & Compliance Beliefs",
                "Disputing Power & Compliance Beliefs: Did the therapist challenge the client's beliefs through discussion; " +
                        "offering challenges in a sensitive and tentative manner/Colombo Style? Was there evidence that the therapist a) " +
                        "highlighted logical inconsistencies in the belief system; b) encouraged the client to consider alternative explanations.");
        doc.addEntry(ip2);
        ip2.setSection(mainSec);
        ip2.setLabel("2");
        createOptions(factory, ip2, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							new int[]{1,2,3,4,5,});

        OptionEntry ip3 = factory.createOptionEntry("Behavioural Experiments/Reality Testing",
                "Behavioural Experiments/Reality Testing: Did the therapist encourage the client to seek disconfirmatory evidence and experiences? " +
                        "Did the therapist use RTHC? Was a clear behavioural experiment devised as a true test of the client's beliefs?");
        doc.addEntry(ip3);
        ip3.setSection(mainSec);
        ip3.setLabel("3");
        createOptions(factory, ip3, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							new int[]{1,2,3,4,5,});

        NarrativeEntry n4 = factory.createNarrativeEntry("N4", "ADVANCED INTERVENTION I: Beliefs about Identity, Meaning/Purpose");
        doc.addEntry(n4);
        n4.setSection(mainSec);
        n4.setStyle(NarrativeStyle.HEADER);

        OptionEntry ai11 = factory.createOptionEntry("Agreeing the Beliefs to be Targeted",
                "Agreeing the Beliefs to be Targeted: Did the therapist develop a collaborative description of the beliefs concerning " +
                        "Identity, Meaning/Purpose and agree which beliefs and in which order they would be tackled?");
        doc.addEntry(ai11);
        ai11.setSection(mainSec);
        ai11.setLabel("1");
        createOptions(factory, ai11, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							new int[]{1,2,3,4,5,});

        OptionEntry ai12 = factory.createOptionEntry("Clarifying the Evidence for Beliefs",
                "Clarifying the Evidence for Beliefs: Did the therapist assess the evidence that the client uses to support " +
                        "the beliefs about Identity, Meaning and Purpose.");
        doc.addEntry(ai12);
        ai12.setSection(mainSec);
        ai12.setLabel("2");
        createOptions(factory, ai12, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							new int[]{1,2,3,4,5,});

        OptionEntry ai13 = factory.createOptionEntry("Disputing Beliefs About Identity, Meaning/Purpose",
                "Disputing Beliefs About Identity, Meaning/Purpose: Did the therapist challenge the client's beliefs through " +
                        "discussion; offering challenges in a sensitive and tentative manner/Colombo Style? Was there evidence that " +
                        "the therapist a) highlighted logical inconsistencies in the belief system; b) encouraged the client to consider " +
                        "alternative explanations.");
        doc.addEntry(ai13);
        ai13.setSection(mainSec);
        ai13.setLabel("3");
        createOptions(factory, ai13, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							new int[]{1,2,3,4,5,});

        OptionEntry ai14 = factory.createOptionEntry("Behavioural Experiments/Reality Testing",
                "Behavioural Experiments/Reality Testing: Did the therapist encourage the client to seek disconfirmatory evidence " +
                        "and experiences? Was a clear behavioural experiment devised as a true test of the client's beliefs?");
        doc.addEntry(ai14);
        ai14.setSection(mainSec);
        ai14.setLabel("4");
        createOptions(factory, ai14, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							new int[]{1,2,3,4,5,});

        NarrativeEntry n5 = factory.createNarrativeEntry("N5", "ADVANCED INTERVENTION II: Self Evaluations");
        doc.addEntry(n5);
        n5.setSection(mainSec);
        n5.setStyle(NarrativeStyle.HEADER);

        OptionEntry ai21 = factory.createOptionEntry("Exploring Implications of Psychotic Beliefs for Beliefs About Self",
                "Exploring Implications of Psychotic Beliefs for Beliefs About Self: Did the therapist explore developmental and " +
                        "vulnerability factors that led to the development of psychotic experiences and beliefs?");
        doc.addEntry(ai21);
        ai21.setSection(mainSec);
        ai21.setLabel("1");
        createOptions(factory, ai21, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							new int[]{1,2,3,4,5,});

        OptionEntry ai22 = factory.createOptionEntry("Identifying Core Beliefs About Self",
                "Identifying Core Beliefs About Self: Did the therapist explore and identify the client's core self beliefs: " +
                        "Negative Self Evaluations & Dysfunctional Assumptions?  Did the therapist explore developmental and vulnerability " +
                        "factors that led to the development of core self beliefs?");
        doc.addEntry(ai22);
        ai22.setSection(mainSec);
        ai22.setLabel("2");
        createOptions(factory, ai22, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							new int[]{1,2,3,4,5,});

        OptionEntry ai23 = factory.createOptionEntry("Connecting Beliefs About Identity, Meaning/Purpose to Beliefs About Self",
                "Connecting Beliefs About Identity, Meaning/Purpose to Beliefs About Self: Did the therapist help the client to develop " +
                        "a personal model of their psychotic experiences based on a shared understanding of: a)  the role of developmental and " +
                        "vulnerability factors in giving rise to and shaping core beliefs; b) the role of psychotic experiences as a protective " +
                        "layer/defence?");
        doc.addEntry(ai23);
        ai23.setSection(mainSec);
        ai23.setLabel("3");
        createOptions(factory, ai23, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							new int[]{1,2,3,4,5,});

        OptionEntry ai24 = factory.createOptionEntry("Disputing Core beliefs",
                "Disputing Core beliefs: Did the therapist assess and dispute the evidence for the client's core beliefs; " +
                        "disputing the evidence; pointing out logical inconsistencies in the self belief system; looking for alternative " +
                        "explanations. Did the therapist use specific philosophical disputation techniques: Big I little I; evaluating " +
                        "behaviour Vs whole person evaluations; changing nature of self?");
        doc.addEntry(ai24);
        ai24.setSection(mainSec);
        ai24.setLabel("4");
        createOptions(factory, ai24, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							new int[]{1,2,3,4,5,});



        Section eotlaSec = factory.createSection("EOTLA Section", "Evaluation Of Treatment Level Attained");
        doc.addSection(eotlaSec);
        SectionOccurrence eotlaSecOcc = factory.createSectionOccurrence("EOTLA Sec Occ");
        eotlaSec.addOccurrence(eotlaSecOcc);

        IntegerEntry noOfSessions = factory.createIntegerEntry("Number of Sessions", "Number of Sessions");
        noOfSessions.setSection(eotlaSec);
        doc.addEntry(noOfSessions);

        NarrativeEntry n6 = factory.createNarrativeEntry("N6",
        		"Tick each of the following treatment elements if they were completed during the course of therapy.");
        doc.addEntry(n6);
        n6.setSection(eotlaSec);

        NarrativeEntry n7 = factory.createNarrativeEntry("N7", "Engagement Phase");
        doc.addEntry(n7);
        n7.setSection(eotlaSec);
        n7.setStyle(NarrativeStyle.HEADER);

        BooleanEntry e_ep_1 = factory.createBooleanEntry("Established Rapport",
                "Established Rapport: Successfully established rapport and trust: used empathic listening; explored " +
                        "beliefs and psychotic experiences in a non-judgemental way; helped the client feel understood.");
        doc.addEntry(e_ep_1);
        e_ep_1.setSection(eotlaSec);

        BooleanEntry e_ep_2 = factory.createBooleanEntry("Normalised",
                "Normalised: Helped the client to recognise that their psychotic experiences were similar to the " +
                        "experiences of many people who have not been diagnosed with a mental illness?");
        doc.addEntry(e_ep_2);
        e_ep_2.setSection(eotlaSec);

        BooleanEntry e_ep_3 = factory.createBooleanEntry("Addressed Engagement Beliefs",
                "Addressed Engagement Beliefs: Explored and addressed any beliefs that threatened the engagement process: " +
                        "inability to change; resistance by voices; inability of the therapist to understand experiences?");
        doc.addEntry(e_ep_3);
        e_ep_3.setSection(eotlaSec);

        NarrativeEntry n8 = factory.createNarrativeEntry("N8", "Establishing the Basis For Intervention");
        doc.addEntry(n8);
        n8.setSection(eotlaSec);
        n8.setStyle(NarrativeStyle.HEADER);

        BooleanEntry e_bi_1 = factory.createBooleanEntry("Relocated the Problem at B",
                "Relocated the Problem at B: Helped the client to view the problem as a belief instead of hearing a " +
                        "voice per se and/or the emotional/behavioural distress associated with it.");
        doc.addEntry(e_bi_1);
        e_bi_1.setSection(eotlaSec);

        BooleanEntry e_bi_2 = factory.createBooleanEntry("Agreed the Beliefs to be Targeted",
                "Agreed the Beliefs to be Targeted: Developed a collaborative description of the beliefs concerning Power " +
                        "and Compliance and agreed which beliefs and in which order they would be tackled?");
        doc.addEntry(e_bi_2);
        e_bi_2.setSection(eotlaSec);

        BooleanEntry e_bi_3 = factory.createBooleanEntry("Clarified the Evidence for Beliefs",
                "Clarified the Evidence for Beliefs: Assessed the evidence that the client used to support " +
                        "beliefs about Power and Compliance.");
        doc.addEntry(e_bi_3);
        e_bi_3.setSection(eotlaSec);

        NarrativeEntry n9 = factory.createNarrativeEntry("N9", "INTERVENTION PHASE: Power; Control & Compliance Beliefs");
        doc.addEntry(n9);
        n9.setSection(eotlaSec);
        n9.setStyle(NarrativeStyle.HEADER);

        BooleanEntry e_ip_1 = factory.createBooleanEntry("Reviewed & Enhanced Coping Strategies",
                "Reviewed & Enhanced Coping Strategies: Systematically reviewed the effectiveness of the client's coping " +
                        "strategies for addressing power imbalances, reduced compliance and improved control (ie: reviewing when they " +
                        "were used; how consistently they are applied and how effective they were)? Improved coping strategies and " +
                        "introduced further strategies where appropriate?");
        doc.addEntry(e_ip_1);
        e_ip_1.setSection(eotlaSec);

        BooleanEntry e_ip_2 = factory.createBooleanEntry("Disputed Power & Compliance Beliefs",
                "Disputed Power & Compliance Beliefs: Challenged beliefs through discussion; offered challenges in a sensitive " +
                        "and tentative manner/Colombo Style? Highlighted logical inconsistencies in the belief system and encouraged " +
                        "the client to consider alternative explanations.");
        doc.addEntry(e_ip_2);
        e_ip_2.setSection(eotlaSec);

        BooleanEntry e_ip_3 = factory.createBooleanEntry("Behavioural Experiments/Reality Testing",
                "Behavioural Experiments/Reality Testing: Encouraged the client to seek disconfirmatory evidence and experiences? " +
                        "Used RTHC? Devised a behavioural experiment as a true test of the client's beliefs?");
        doc.addEntry(e_ip_3);
        e_ip_3.setSection(eotlaSec);

        NarrativeEntry n10 = factory.createNarrativeEntry("N10", "ADVANCED INTERVENTION I: Beliefs about Identity, Meaning/Purpose");
        doc.addEntry(n10);
        n10.setSection(eotlaSec);
        n10.setStyle(NarrativeStyle.HEADER);

        BooleanEntry e_ai1_1 = factory.createBooleanEntry("Agreed the Beliefs to be Targeted",
                "Agreed the Beliefs to be Targeted: Developed a collaborative description of the beliefs concerning Identity, " +
                        "Meaning/Purpose and agreed which beliefs and in which order they would be tackled?");
        doc.addEntry(e_ai1_1);
        e_ai1_1.setSection(eotlaSec);

        BooleanEntry e_ai1_2 = factory.createBooleanEntry("Clarified the Evidence for Beliefs",
                "Clarified the Evidence for Beliefs: Assessed the evidence that the client used to support beliefs " +
                        "about Identity, Meaning and Purpose.");
        doc.addEntry(e_ai1_2);
        e_ai1_2.setSection(eotlaSec);

        BooleanEntry e_ai1_3 = factory.createBooleanEntry("Disputed Beliefs About Identity, Meaning/Purpose",
                "Disputed Beliefs About Identity, Meaning/Purpose: Challenged beliefs through discussion; offered challenges " +
                        "in a sensitive and tentative manner/Colombo Style? Highlighted logical inconsistencies in the belief system and " +
                        "encouraged the client to consider alternative explanations.");
        doc.addEntry(e_ai1_3);
        e_ai1_3.setSection(eotlaSec);

        BooleanEntry e_ai1_4 = factory.createBooleanEntry("Behavioural Experiments/Reality Testing",
                "Behavioural Experiments/Reality Testing: Encouraged the client to seek disconfirmatory evidence and experiences? " +
                        "Devised a behavioural experiment  as a true test of the client's beliefs?");
        doc.addEntry(e_ai1_4);
        e_ai1_4.setSection(eotlaSec);

        NarrativeEntry n11 = factory.createNarrativeEntry("N11", "ADVANCED INTERVENTION II: Self Evaluations");
        doc.addEntry(n11);
        n11.setSection(eotlaSec);
        n11.setStyle(NarrativeStyle.HEADER);

        BooleanEntry e_ai2_1 = factory.createBooleanEntry("Explored Implications of Psychotic Beliefs for Beliefs About Self",
                "Explored Implications of Psychotic Beliefs for Beliefs About Self: Explore developmental and vulnerability factors " +
                        "that led to the development of psychotic experiences and beliefs?");
        doc.addEntry(e_ai2_1);
        e_ai2_1.setSection(eotlaSec);

        BooleanEntry e_ai2_2 = factory.createBooleanEntry("Identified Core Beliefs About Self",
                "Identified Core Beliefs About Self: Explored and identified core self beliefs: Negative Self Evaluations & Dysfunctional " +
                        "Assumptions?  Explored developmental and vulnerability factors that led to the development of core self beliefs?");
        doc.addEntry(e_ai2_2);
        e_ai2_2.setSection(eotlaSec);

        BooleanEntry e_ai2_3 = factory.createBooleanEntry("Connected Beliefs About Identity, Meaning/Purpose to Beliefs About Self",
                "Connected Beliefs About Identity, Meaning/Purpose to Beliefs About Self: Helped the client to develop a personal model of " +
                        "their psychotic experiences based on a shared understanding of: a)  the role of developmental and vulnerability factors " +
                        "in giving rise to and shaping core beliefs; b) the role of psychotic experiences as a protective layer/defence?");
        doc.addEntry(e_ai2_3);
        e_ai2_3.setSection(eotlaSec);

        BooleanEntry e_ai2_4 = factory.createBooleanEntry("Disputed Core beliefs",
                "Disputed Core beliefs: Assessed and disputed the evidence for the client's core beliefs; disputed the evidence; pointed out " +
                        "logical inconsistencies in the self belief system; looked for alternative explanations. Used specific philosophical " +
                        "disputation techniques: Big I little I; evaluating behaviour Vs whole person evaluations; changing nature of self?");
        doc.addEntry(e_ai2_4);
        e_ai2_4.setSection(eotlaSec);




        Section isrSec = factory.createSection("Individual Session Section", "Individual Session Ratings");
        doc.addSection(isrSec);
        SectionOccurrence isrSecOcc = factory.createSectionOccurrence("Individual Session Sec Occ");
        isrSec.addOccurrence(isrSecOcc);

        NarrativeEntry n12 = factory.createNarrativeEntry("N12",
        		"Rate each session on All items (as applicable to the session)");
        doc.addEntry(n12);
        n12.setSection(isrSec);

        CompositeEntry c1 = factory.createComposite("Engagement Phase", "Engagement Phase");
        doc.addEntry(c1);
        c1.setSection(isrSec);

        IntegerEntry c1Session = factory.createIntegerEntry("Session", "Session");
        c1.addEntry(c1Session);
        c1Session.setSection(isrSec);
        c1Session.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

        OptionEntry c1_1 = factory.createOptionEntry("Establishment of Rapport", "Establishment of Rapport");
        c1.addEntry(c1_1);
        c1_1.setSection(isrSec);
        createOptions(factory, c1_1, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							 new int[]{1,2,3,4,5});

        OptionEntry c1_2 = factory.createOptionEntry("Normalising", "Normalising");
        c1.addEntry(c1_2);
        c1_2.setSection(isrSec);
        createOptions(factory, c1_2, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							 new int[]{1,2,3,4,5});

        OptionEntry c1_3 = factory.createOptionEntry("Addressing Engagement Beliefs", "Addressing Engagement Beliefs");
        c1.addEntry(c1_3);
        c1_3.setSection(isrSec);
        createOptions(factory, c1_3, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							 new int[]{1,2,3,4,5});

        CompositeEntry c2 = factory.createComposite("Establishing the Basis For Intervention", "Establishing the Basis For Intervention");
        doc.addEntry(c2);
        c2.setSection(isrSec);

        IntegerEntry c2Session = factory.createIntegerEntry("Session", "Session");
        c2.addEntry(c2Session);
        c2Session.setSection(isrSec);
        c2Session.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

        OptionEntry c2_1 = factory.createOptionEntry("Relocating the Problem at B", "Relocating the Problem at B");
        c2.addEntry(c2_1);
        c2_1.setSection(isrSec);
        createOptions(factory, c2_1, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							 new int[]{1,2,3,4,5});

        OptionEntry c2_2 = factory.createOptionEntry("Agreeing the Beliefs to be Targeted", "Agreeing the Beliefs to be Targeted");
        c2.addEntry(c2_2);
        c2_2.setSection(isrSec);
        createOptions(factory, c2_2, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							 new int[]{1,2,3,4,5});

        OptionEntry c2_3 = factory.createOptionEntry("Clarifying the Evidence for Beliefs", "Clarifying the Evidence for Beliefs");
        c2.addEntry(c2_3);
        c2_3.setSection(isrSec);
        createOptions(factory, c2_3, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							 new int[]{1,2,3,4,5});

        CompositeEntry c3 = factory.createComposite("INTERVENTION PHASE: Power; Control & Compliance Beliefs",
                "INTERVENTION PHASE: Power; Control & Compliance Beliefs");
        doc.addEntry(c3);
        c3.setSection(isrSec);

        IntegerEntry c3Session = factory.createIntegerEntry("Session", "Session");
        c3.addEntry(c3Session);
        c3Session.setSection(isrSec);
        c3Session.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

        OptionEntry c3_1 = factory.createOptionEntry("Reviewing & Enhancing Coping Strategies", "Reviewing & Enhancing Coping Strategies");
        c3.addEntry(c3_1);
        c3_1.setSection(isrSec);
        createOptions(factory, c3_1, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							 new int[]{1,2,3,4,5});

        OptionEntry c3_2 = factory.createOptionEntry("Disputing Power & Compliance Beliefs", "Disputing Power & Compliance Beliefs");
        c3.addEntry(c3_2);
        c3_2.setSection(isrSec);
        createOptions(factory, c3_2, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							 new int[]{1,2,3,4,5});

        OptionEntry c3_3 = factory.createOptionEntry("Behavioural Exp./Reality Testing", "Behavioural Exp./Reality Testing");
        c3.addEntry(c3_3);
        c3_3.setSection(isrSec);
        createOptions(factory, c3_3, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
        							 new int[]{1,2,3,4,5});


        CompositeEntry c4 = factory.createComposite("ADVANCED INTERVENTION I: Beliefs about Identity, Meaning/Purpose",
                "ADVANCED INTERVENTION I: Beliefs about Identity, Meaning/Purpose");
		doc.addEntry(c4);
		c4.setSection(isrSec);

		IntegerEntry c4Session = factory.createIntegerEntry("Session", "Session");
		c4.addEntry(c4Session);
		c4Session.setSection(isrSec);
		c4Session.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

		OptionEntry c4_1 = factory.createOptionEntry("Agreeing the Beliefs to be Targeted", "Agreeing the Beliefs to be Targeted");
		c4.addEntry(c4_1);
		c4_1.setSection(isrSec);
		createOptions(factory, c4_1, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
									 new int[]{1,2,3,4,5});

		OptionEntry c4_2 = factory.createOptionEntry("Clarifying the Evidence for Beliefs", "Clarifying the Evidence for Beliefs");
		c4.addEntry(c4_2);
		c4_2.setSection(isrSec);
		createOptions(factory, c4_2, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
									 new int[]{1,2,3,4,5});

		OptionEntry c4_3 = factory.createOptionEntry("Disputing Ident./Mean./Purpose Beliefs", "Disputing Ident./Mean./Purpose Beliefs");
		c4.addEntry(c4_3);
		c4_3.setSection(isrSec);
		createOptions(factory, c4_3, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
									 new int[]{1,2,3,4,5});

		OptionEntry c4_4 = factory.createOptionEntry("Behav. Exp./Reality Testing", "Behav. Exp./Reality Testing");
		c4.addEntry(c4_4);
		c4_4.setSection(isrSec);
		createOptions(factory, c4_4, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
									 new int[]{1,2,3,4,5});


        CompositeEntry c5 = factory.createComposite("ADVANCED INTERVENTION II: Self Evaluation",
                "ADVANCED INTERVENTION II: Self Evaluation");
		doc.addEntry(c5);
		c5.setSection(isrSec);

		IntegerEntry c5Session = factory.createIntegerEntry("Session", "Session");
		c5.addEntry(c5Session);
		c5Session.setSection(isrSec);
		c5Session.addValidationRule(ValidationRulesWrapper.instance().getRule("Positive Integer"));

		OptionEntry c5_1 = factory.createOptionEntry("Exploring Implications", "Exploring Implications");
		c5.addEntry(c5_1);
		c5_1.setSection(isrSec);
		createOptions(factory, c5_1, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
									 new int[]{1,2,3,4,5});

		OptionEntry c5_2 = factory.createOptionEntry("Identifying Core Self Beliefs", "Identifying Core Self Beliefs");
		c5.addEntry(c5_2);
		c5_2.setSection(isrSec);
		createOptions(factory, c5_2, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
									 new int[]{1,2,3,4,5});

		OptionEntry c5_3 = factory.createOptionEntry("Connecting Psychotic Beliefs to Self", "Connecting Psychotic Beliefs to Self");
		c5.addEntry(c5_3);
		c5_3.setSection(isrSec);
		createOptions(factory, c5_3, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
									 new int[]{1,2,3,4,5});

		OptionEntry c5_4 = factory.createOptionEntry("Disputing Core Beliefs Self", "Disputing Core Beliefs Self");
		c5.addEntry(c5_4);
		c5_4.setSection(isrSec);
		createOptions(factory, c5_4, new String[]{"Not at all", "Slightly", "Moderately", "Considerably", "Extensively"},
									 new int[]{1,2,3,4,5});


        return doc;
    }
}
