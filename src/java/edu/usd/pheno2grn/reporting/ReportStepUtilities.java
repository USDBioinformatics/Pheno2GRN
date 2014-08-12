package edu.usd.pheno2grn.reporting;

import java.util.List;

/**
 * Contains functionality for adding a step to the reporting step list.
 */
public class ReportStepUtilities {

    /**
     * Adds a report step to both lists if it is not present already. If the
     * step is already present, it updates each list with the contents of the
     * passed in step.
     *
     * @param completeList Complete list of all reporting steps
     * @param selectedList Selected list of all reporting steps
     * @param stepToBeAdded New step to be added, or to be used to update the
     * lists
     */
    public static void addStepToCompleteListAndSelectedList(List<ReportStep> completeList,
            List<ReportStep> selectedList, ReportStep stepToBeAdded) {

        //getting the position of the step in both selected and complete lists
        int posInComplete = completeList.indexOf(stepToBeAdded);
        if (posInComplete == -1) {
            completeList.add(stepToBeAdded);
        } else {
            completeList.set(posInComplete, stepToBeAdded);
        }

        int posInSelected = selectedList.indexOf(stepToBeAdded);
        if (posInComplete == -1) {
            selectedList.add(stepToBeAdded);
        } else {
            selectedList.set(posInSelected, stepToBeAdded);
        }
    }
}
