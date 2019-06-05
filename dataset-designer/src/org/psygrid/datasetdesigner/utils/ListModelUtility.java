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
package org.psygrid.datasetdesigner.utils;

import org.psygrid.data.model.hibernate.*;

import org.psygrid.datasetdesigner.model.CustomFieldValueModel;
import org.psygrid.datasetdesigner.model.DSDocumentOccurrence;
import org.psygrid.datasetdesigner.model.GroupModel;
import org.psygrid.datasetdesigner.model.TreatmentHolderModel;

import org.psygrid.randomization.model.hibernate.Stratum;

import org.psygrid.www.xml.security.core.types.RoleType;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;


/**
 * Utility method used to convert lists to DefaultListModels and DefaultComboBoxModels
 * and vice versa
 *
 * @author pwhelan
 *
 */
public class ListModelUtility {
    /**
     * Create a default combo box model form the list passed
     * @param list
     * @return the configured default combo box model
     */
    public static DefaultComboBoxModel convertListToComboModel(List<?> list) {
        DefaultComboBoxModel boxModel = new DefaultComboBoxModel();

        if (list == null) {
            return boxModel;
        }

        for (int i = 0; i < list.size(); i++) {
            boxModel.addElement(list.get(i));
        }

        return boxModel;
    }

    /**
     * Create a default list model from the ArrayList passed
     * @param list
     * @return the configured default list model
     */
    public static DefaultListModel convertArrayListToListModel(
        ArrayList<?> list) {
        DefaultListModel listModel = new DefaultListModel();

        if (list == null) {
            return listModel;
        }

        for (int i = 0; i < list.size(); i++) {
            listModel.addElement(list.get(i));
        }

        return listModel;
    }
    
    /**
     * Create a default list model from the list passed
     * @param list
     * @return the configured default list model
     */
    public static DefaultListModel convertListToListModel(
        List<?> list) {
        DefaultListModel listModel = new DefaultListModel();

        if (list == null) {
            return listModel;
        }

        for (int i = 0; i < list.size(); i++) {
            listModel.addElement(list.get(i));
        }

        return listModel;
    }

    /**
     * Convert the array list to the list model
     * and remove the previews 
     *
     * @param list the list to convert
     *
     * @return the configured DefaultListModel
     */
    public static DefaultListModel convertArrayListToListModelRemovePreviews(
        ArrayList<DSDocumentOccurrence> list) {
        DefaultListModel listModel = new DefaultListModel();

        for (int i = 0; i < list.size(); i++) {
            DSDocumentOccurrence currentOcc = list.get(i);

            if (!currentOcc.getDocOccurrence().getName().startsWith("Preview")) {
                listModel.addElement(list.get(i));
            }
        }

        return listModel;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<DocumentGroup> convertListModelToIDocGroupList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<DocumentGroup> list = new ArrayList<DocumentGroup>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((DocumentGroup) model.get(i));
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<DocumentGroup> convertListModelToDocGroupList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<DocumentGroup> list = new ArrayList<DocumentGroup>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((DocumentGroup) model.get(i));
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<EslCustomField> convertListModelToEslCustomFieldList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<EslCustomField> list = new ArrayList<EslCustomField>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((EslCustomField) model.get(i));
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<Site> convertListModelToISiteList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<Site> list = new ArrayList<Site>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((Site) model.get(i));
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<Section> convertListModelToISectionList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<Section> list = new ArrayList<Section>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((Section) model.get(i));
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<Section> convertListModelToSectionList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<Section> list = new ArrayList<Section>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((Section) model.get(i));
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<Site> convertListModelToSiteList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<Site> list = new ArrayList<Site>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((Site) model.get(i));
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<Group> convertListModelToIGroupList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<Group> list = new ArrayList<Group>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((Group) model.get(i));
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<AssociatedConsentForm> convertListModelToIAssociatedConsentFormList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<AssociatedConsentForm> list = new ArrayList<AssociatedConsentForm>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((AssociatedConsentForm) model.get(i));
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<GroupModel> convertListModelToGroupModelList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<GroupModel> list = new ArrayList<GroupModel>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((GroupModel) model.get(i));
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<Stratum> convertListModelToStratumList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<Stratum> list = new ArrayList<Stratum>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((Stratum) model.get(i));
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<Document> convertListModelToDocumentList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<Document> list = new ArrayList<Document>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((Document) model.get(i));
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<String> convertListModelToStringList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<String> list = new ArrayList<String>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((String) model.get(i));
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<String> convertListModelToCustomFieldValueList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<String> list = new ArrayList<String>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add(((CustomFieldValueModel)model.get(i)).getValue());
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<TreatmentHolderModel> convertListModelToTreatmentHolderModelList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<TreatmentHolderModel> list = new ArrayList<TreatmentHolderModel>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((TreatmentHolderModel) model.get(i));
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<Status> convertListModelToStatusList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<Status> list = new ArrayList<Status>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((Status) model.get(i));
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<Status> convertListModelToIStatusList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<Status> list = new ArrayList<Status>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((Status) model.get(i));
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<ConsentFormGroup> convertListModelToConsentFormGroupList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<ConsentFormGroup> list = new ArrayList<ConsentFormGroup>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((ConsentFormGroup) model.get(i));
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<RoleType> convertListModelToConsentRoleTypeList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<RoleType> list = new ArrayList<RoleType>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((RoleType) model.get(i));
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<Transformer> convertListModelToTransformerList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<Transformer> list = new ArrayList<Transformer>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((Transformer) model.get(i));
        }

        return list;
    }

    /**
     * Create an array list from the list model
     *
     * In this case, the list passed might contains Associated Consent Forms
     * so we check this and only include the primary consent forms here
     *
     * @param model the model to process
     *
     * @return the configured arraylist
     */
    public static ArrayList<PrimaryConsentForm> convertListModelToPrimaryConsentFormList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<PrimaryConsentForm> list = new ArrayList<PrimaryConsentForm>();

        for (int i = 0; i < model.getSize(); i++) {
        	ConsentForm icf = (ConsentForm)model.get(i);
        	if (icf instanceof PrimaryConsentForm) {
                list.add((PrimaryConsentForm) model.get(i));
        	}
        }

        return list;
    }

    /**
     * Convert a default list model to an arraylist of Units
     * @param model
     * @return the converted arraylist
     */
    public static ArrayList<Unit> convertListModelToUnitList(
        DefaultListModel model) {
        if (model == null) {
            return null;
        }

        ArrayList<Unit> list = new ArrayList<Unit>();

        for (int i = 0; i < model.getSize(); i++) {
            list.add((Unit) model.get(i));
        }

        return list;
    }

    /**
     * Convert a list to a defaultlistmodel
     * @param the list
     * @return the default combo box model
     */
    public static DefaultComboBoxModel convertListToComboBoxModel(List<?> list) {
        DefaultComboBoxModel listModel = new DefaultComboBoxModel();

        for (int i = 0; i < list.size(); i++) {
            listModel.addElement(list.get(i));
        }

        return listModel;
    }
}
