package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkFacility;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.FuturePlans;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.PlatformFpsoInfrastructureType;
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

  public static PlatformFpso getPlatformFpso(Integer id, ProjectDetail detail) {
    var platformFpso = new PlatformFpso(id);
    platformFpso.setProjectDetail(detail);
    platformFpso.setInfrastructureType(PlatformFpsoInfrastructureType.FPSO);
    platformFpso.setStructure(FACILITY);
    setCommonFields(platformFpso, true);
    setFpsoCommonFields(platformFpso);
    return platformFpso;
  }

  public static PlatformFpso getPlatformFpso_withFpso(ProjectDetail detail) {
    return getPlatformFpso_withFpsoAndSubstructuresRemoved(detail);
  }

  public static PlatformFpso getPlatformFpso_withFpsoAndSubstructuresRemoved(ProjectDetail detail) {
    var platformFpso = new PlatformFpso(detail);
    platformFpso.setInfrastructureType(PlatformFpsoInfrastructureType.FPSO);
    platformFpso.setStructure(FACILITY);
    setCommonFields(platformFpso, true);
    setFpsoCommonFields(platformFpso);
    return platformFpso;
  }

  public static PlatformFpso getPlatformFpso_withPlatform(ProjectDetail detail) {
    return getPlatformFpso_withPlatformAndSubstructuresRemoved(detail);
  }

  public static PlatformFpso getPlatformFpso_withPlatformAndSubstructuresRemoved(ProjectDetail detail) {
    var platformFpso = new PlatformFpso(detail);
    platformFpso.setInfrastructureType(PlatformFpsoInfrastructureType.PLATFORM);
    platformFpso.setStructure(FACILITY);
    setCommonFields(platformFpso, true);
    return platformFpso;
  }

  public static PlatformFpso getPlatformFpso_withPlatformAndSubstructuresNotRemoved(ProjectDetail detail) {
    var platformFpso = new PlatformFpso(detail);
    platformFpso.setInfrastructureType(PlatformFpsoInfrastructureType.PLATFORM);
    platformFpso.setStructure(FACILITY);
    setCommonFields(platformFpso, false);
    return platformFpso;
  }

  public static PlatformFpso getPlatformFpso_withPlatform_manualStructure(ProjectDetail detail) {
    var platformFpso = new PlatformFpso(detail);
    platformFpso.setInfrastructureType(PlatformFpsoInfrastructureType.PLATFORM);
    platformFpso.setManualStructureName(MANUAL_STRUCTURE_NAME);
    setCommonFields(platformFpso, true);
    return platformFpso;
  }

  public static PlatformFpsoForm getPlatformFpsoForm_withFpsoAndSubstructuresToBeRemoved() {
    var platformFpsoForm = new PlatformFpsoForm();
    platformFpsoForm.setInfrastructureType(PlatformFpsoInfrastructureType.FPSO);
    platformFpsoForm.setFpsoStructure(FACILITY.getSelectionId());
    setCommonFields(platformFpsoForm, true);
    setFpsoCommonFields(platformFpsoForm);
    return platformFpsoForm;
  }

  public static PlatformFpsoForm getPlatformFpsoForm_withPlatform() {
    var platformFpsoForm = new PlatformFpsoForm();
    platformFpsoForm.setInfrastructureType(PlatformFpsoInfrastructureType.PLATFORM);
    platformFpsoForm.setPlatformStructure(FACILITY.getSelectionId());
    setCommonFields(platformFpsoForm, true);
    return platformFpsoForm;
  }

  public static PlatformFpsoForm getPlatformFpsoForm_withPlatform_manualStructure() {
    var platformFpsoForm = new PlatformFpsoForm();
    platformFpsoForm.setInfrastructureType(PlatformFpsoInfrastructureType.PLATFORM);
    platformFpsoForm.setPlatformStructure(SearchSelectorService.getValueWithManualEntryPrefix(MANUAL_STRUCTURE_NAME));
    setCommonFields(platformFpsoForm, true);
    return platformFpsoForm;
  }

  public static PlatformFpsoForm getPlatformFpsoForm_withFpso_manualStructure() {
    var platformFpsoForm = new PlatformFpsoForm();
    platformFpsoForm.setInfrastructureType(PlatformFpsoInfrastructureType.FPSO);
    platformFpsoForm.setFpsoStructure(SearchSelectorService.getValueWithManualEntryPrefix(MANUAL_STRUCTURE_NAME));
    setCommonFields(platformFpsoForm, true);

    platformFpsoForm.setFpsoType(FPSO_TYPE);
    platformFpsoForm.setFpsoDimensions(FPSO_DIMENSIONS);
    return platformFpsoForm;
  }

  private static void setCommonFields(PlatformFpso platformFpso, boolean substructuresRemoved) {
    platformFpso.setTopsideFpsoMass(TOPSIDE_FPSO_MASS);
    platformFpso.setEarliestRemovalYear(EARLIEST_REMOVAL_YEAR);
    platformFpso.setLatestRemovalYear(LATEST_REMOVAL_YEAR);
    platformFpso.setFuturePlans(FUTURE_PLANS);
    platformFpso.setSubstructuresExpectedToBeRemoved(substructuresRemoved);

    if (substructuresRemoved) {
      platformFpso.setSubstructureRemovalPremise(SUBSTRUCTURE_REMOVAL_PREMISE);
      platformFpso.setSubstructureRemovalMass(SUBSTRUCTURE_REMOVAL_MASS);
      platformFpso.setSubStructureRemovalEarliestYear(SUBSTRUCTURE_EARLIEST_REMOVAL_YEAR);
      platformFpso.setSubStructureRemovalLatestYear(SUBSTRUCTURE_LATEST_REMOVAL_YEAR);
    }
  }

  private static void setCommonFields(PlatformFpsoForm platformFpsoForm, boolean substructuresRemoved) {
    platformFpsoForm.setTopsideFpsoMass(TOPSIDE_FPSO_MASS);
    platformFpsoForm.setTopsideRemovalYears(new MinMaxDateInput(EARLIEST_REMOVAL_YEAR, LATEST_REMOVAL_YEAR));
    platformFpsoForm.setFuturePlans(FUTURE_PLANS);
    platformFpsoForm.setSubstructureExpectedToBeRemoved(substructuresRemoved);

    if (substructuresRemoved) {
      platformFpsoForm.setSubstructureRemovalMass(SUBSTRUCTURE_REMOVAL_MASS);
      platformFpsoForm.setSubstructureRemovalPremise(SUBSTRUCTURE_REMOVAL_PREMISE);
      platformFpsoForm.setSubstructureRemovalYears(
          new MinMaxDateInput(SUBSTRUCTURE_EARLIEST_REMOVAL_YEAR, SUBSTRUCTURE_LATEST_REMOVAL_YEAR)
      );
    }
  }

  private static void setFpsoCommonFields(PlatformFpso platformFpso) {
    platformFpso.setFpsoType(FPSO_TYPE);
    platformFpso.setFpsoDimensions(FPSO_DIMENSIONS);
  }

  private static void setFpsoCommonFields(PlatformFpsoForm platformFpsoForm) {
    platformFpsoForm.setFpsoType(FPSO_TYPE);
    platformFpsoForm.setFpsoDimensions(FPSO_DIMENSIONS);
  }
}
