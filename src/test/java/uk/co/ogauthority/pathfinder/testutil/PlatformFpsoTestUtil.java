package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkFacility;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.FuturePlans;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.SubstructureRemovalPremise;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoForm;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

public class PlatformFpsoTestUtil {
  public static final String MANUAL_STRUCTURE_NAME = "Manual structure";
  public static final DevUkFacility FACILITY = new DevUkFacility(1, "NAME");
  public static final Integer TOPSIDE_FPSO_MASS = 100;
  public static final String EARLIEST_REMOVAL_YEAR = "2020";
  public static final String LATEST_REMOVAL_YEAR = "2030";
  public static final String SUBSTRUCTURE_EARLIEST_REMOVAL_YEAR = "2020";
  public static final String SUBSTRUCTURE_LATEST_REMOVAL_YEAR = "2030";
  public static final SubstructureRemovalPremise SUBSTRUCTURE_REMOVAL_PREMISE = SubstructureRemovalPremise.FULL;
  public static final Integer SUBSTRUCTURE_REMOVAL_MASS = 100;
  public static final String FPSO_TYPE = "FPSO type";
  public static final String FPSO_DIMENSIONS = "some dimensions";
  public static final FuturePlans FUTURE_PLANS = FuturePlans.RECYCLE;


  public static PlatformFpso getPlatformFpso_withSubstructuresRemoved(ProjectDetail detail) {
    var platformFpso = new PlatformFpso(detail);
    platformFpso.setStructure(FACILITY);
    setCommonFields(platformFpso, true);
    return platformFpso;
  }

  public static PlatformFpso getPlatformFpso_NoSubstructuresRemoved(ProjectDetail detail) {
    var platformFpso = new PlatformFpso(detail);
    platformFpso.setStructure(FACILITY);
    setCommonFields(platformFpso, false);
    return platformFpso;
  }

  public static PlatformFpso getPlatformFpso_withSubstructuresRemoved_manualStructure(ProjectDetail detail) {
    var platformFpso = new PlatformFpso(detail);
    platformFpso.setManualStructureName(MANUAL_STRUCTURE_NAME);
    setCommonFields(platformFpso, true);
    return platformFpso;
  }

  public static PlatformFpso getPlatformFpso_NoSubstructuresRemoved_manualStructure(ProjectDetail detail) {
    var platformFpso = new PlatformFpso(detail);
    platformFpso.setManualStructureName(MANUAL_STRUCTURE_NAME);
    setCommonFields(platformFpso, false);
    return platformFpso;
  }

  public static PlatformFpsoForm getPlatformFpsoForm_withSubstructuresToBeRemoved() {
    var platformFpsoForm = new PlatformFpsoForm();
    platformFpsoForm.setStructure(FACILITY.getSelectionId());
    setCommonFields(platformFpsoForm, true);
    return platformFpsoForm;
  }

  public static PlatformFpsoForm getPlatformFpsoForm_noSubstructuresToBeRemoved() {
    var platformFpsoForm = new PlatformFpsoForm();
    platformFpsoForm.setStructure(FACILITY.getSelectionId());
    setCommonFields(platformFpsoForm, false);
    return platformFpsoForm;
  }

  public static PlatformFpsoForm getPlatformFpsoForm_withSubstructuresToBeRemoved_manualStructure() {
    var platformFpsoForm = new PlatformFpsoForm();
    platformFpsoForm.setStructure(SearchSelectorService.getValueWithManualEntryPrefix(MANUAL_STRUCTURE_NAME));
    setCommonFields(platformFpsoForm, true);
    return platformFpsoForm;
  }

  public static PlatformFpsoForm getPlatformFpsoForm_noSubstructuresToBeRemoved_manualStructure() {
    var platformFpsoForm = new PlatformFpsoForm();
    platformFpsoForm.setStructure(SearchSelectorService.getValueWithManualEntryPrefix(MANUAL_STRUCTURE_NAME));
    setCommonFields(platformFpsoForm, false);
    return platformFpsoForm;
  }

  private static void setCommonFields(PlatformFpso platformFpso, Boolean substructuresRemoved) {
    platformFpso.setTopsideFpsoMass(TOPSIDE_FPSO_MASS);
    platformFpso.setEarliestRemovalYear(EARLIEST_REMOVAL_YEAR);
    platformFpso.setLatestRemovalYear(LATEST_REMOVAL_YEAR);
    platformFpso.setSubstructuresExpectedToBeRemoved(substructuresRemoved);
    if (substructuresRemoved) {
      platformFpso.setSubstructureRemovalPremise(SUBSTRUCTURE_REMOVAL_PREMISE);
      platformFpso.setSubstructureRemovalMass(SUBSTRUCTURE_REMOVAL_MASS);
      platformFpso.setSubStructureRemovalEarliestYear(SUBSTRUCTURE_EARLIEST_REMOVAL_YEAR);
      platformFpso.setSubStructureRemovalLatestYear(SUBSTRUCTURE_LATEST_REMOVAL_YEAR);
    }
    platformFpso.setFpsoType(FPSO_TYPE);
    platformFpso.setFpsoDimensions(FPSO_DIMENSIONS);
    platformFpso.setFuturePlans(FUTURE_PLANS);
  }

  private static void setCommonFields(PlatformFpsoForm platformFpsoForm, Boolean substructuresRemoved) {
    platformFpsoForm.setTopsideFpsoMass(TOPSIDE_FPSO_MASS);
    platformFpsoForm.setTopsideRemovalYears(new MinMaxDateInput(EARLIEST_REMOVAL_YEAR, LATEST_REMOVAL_YEAR));
    platformFpsoForm.setSubstructureExpectedToBeRemoved(substructuresRemoved);
    if (substructuresRemoved) {
      platformFpsoForm.setSubstructureRemovalPremise(SUBSTRUCTURE_REMOVAL_PREMISE);
      platformFpsoForm.setSubstructureRemovalMass(SUBSTRUCTURE_REMOVAL_MASS);
      platformFpsoForm.setSubstructureRemovalYears(new MinMaxDateInput(SUBSTRUCTURE_EARLIEST_REMOVAL_YEAR, SUBSTRUCTURE_LATEST_REMOVAL_YEAR));
    }
    platformFpsoForm.setFpsoType(FPSO_TYPE);
    platformFpsoForm.setFpsoDimensions(FPSO_DIMENSIONS);
    platformFpsoForm.setFuturePlans(FUTURE_PLANS);
  }

}
