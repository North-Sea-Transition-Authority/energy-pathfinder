package uk.co.ogauthority.pathfinder.model.view.projectinformation;

import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class ProjectInformationViewUtil {

  private ProjectInformationViewUtil() {
    throw new IllegalStateException("ProjectInformationViewUtil is a util class and should not be instantiated");
  }

  public static ProjectInformationView from(ProjectInformation projectInformation) {

    var projectInformationView = new ProjectInformationView();
    projectInformationView.setProjectTitle(projectInformation.getProjectTitle());
    projectInformationView.setProjectSummary(projectInformation.getProjectSummary());
    projectInformationView.setContactName(projectInformation.getContactName());
    projectInformationView.setContactPhoneNumber(projectInformation.getPhoneNumber());
    projectInformationView.setContactJobTitle(projectInformation.getJobTitle());
    projectInformationView.setContactEmailAddress(projectInformation.getEmailAddress());

    final var fieldStage = projectInformation.getFieldStage();

    projectInformationView.setFieldStage(
        fieldStage != null
            ? projectInformation.getFieldStage().getDisplayName()
            : ""
    );

    if (fieldStage != null) {
      if (fieldStage.equals(FieldStage.DEVELOPMENT)) {
        setDevelopmentFields(projectInformationView, projectInformation);
      } else if (fieldStage.equals(FieldStage.ENERGY_TRANSITION)) {
        setEnergyTransitionFields(projectInformationView, projectInformation);
      }
    }

    return projectInformationView;
  }

  private static void setDevelopmentFields(ProjectInformationView projectInformationView,
                                           ProjectInformation projectInformation) {
    projectInformationView.setDevelopmentFirstProductionDate(getFirstProductionDate(projectInformation));
  }

  private static void setEnergyTransitionFields(ProjectInformationView projectInformationView,
                                                ProjectInformation projectInformation) {
    var energyTransitionCategory = projectInformation.getEnergyTransitionCategory();
    projectInformationView.setEnergyTransitionCategory(
        energyTransitionCategory != null
            ? energyTransitionCategory.getDisplayName()
            : ""
    );
  }

  private static String getFirstProductionDate(ProjectInformation projectInformation) {
    return DateUtil.getDateFromQuarterYear(
        projectInformation.getFirstProductionDateQuarter(),
        projectInformation.getFirstProductionDateYear()
    );
  }
}