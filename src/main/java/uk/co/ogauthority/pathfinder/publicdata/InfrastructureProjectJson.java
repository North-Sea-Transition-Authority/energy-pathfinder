package uk.co.ogauthority.pathfinder.publicdata;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;

record InfrastructureProjectJson(
    Integer id,
    String operatorName,
    String title,
    String summary,
    String fieldStage,
    String fieldStageSubCategory,
    String contactName,
    String contactPhoneNumber,
    String contactJobTitle,
    String contactEmailAddress,
    String firstProductionDateQuarter,
    Integer firstProductionDateYear,
    String fieldName,
    String fieldType,
    String ukcsArea,
    Integer maximumWaterDepthMeters,
    List<String> licenceBlocks,
    Instant submittedOn
) {

  static InfrastructureProjectJson from(
      ProjectDetail projectDetail,
      ProjectOperator projectOperator,
      ProjectInformation projectInformation,
      ProjectLocation projectLocation,
      List<ProjectLocationBlock> projectLocationBlocks
  ) {
    var id = projectDetail.getProject().getId();

    String operatorName;
    if (BooleanUtils.isFalse(projectOperator.isPublishedAsOperator())) {
      operatorName = projectOperator.getPublishableOrganisationUnit().getName();
    } else {
      operatorName = projectOperator.getOrganisationGroup().getName();
    }

    var title = projectInformation.getProjectTitle();
    var summary = projectInformation.getProjectSummary();
    var fieldStage = projectInformation.getFieldStage().name();

    String fieldStageSubCategory = null;
    if (projectInformation.getFieldStageSubCategory() != null) {
      fieldStageSubCategory = projectInformation.getFieldStageSubCategory().name();
    }

    var contactName = projectInformation.getContactName();
    var contactPhoneNumber = projectInformation.getPhoneNumber();
    var contactJobTitle = projectInformation.getJobTitle();
    var contactEmailAddress = projectInformation.getEmailAddress();

    String firstProductionDateQuarter = null;
    if (projectInformation.getFirstProductionDateQuarter() != null) {
      firstProductionDateQuarter = projectInformation.getFirstProductionDateQuarter().name();
    }

    var firstProductionDateYear = projectInformation.getFirstProductionDateYear();

    String fieldName = null;
    String fieldType = null;
    String ukcsArea = null;
    Integer maximumWaterDepthMeters = null;
    List<String> licenceBlocks = null;
    if (projectLocation != null) {
      fieldName = projectLocation.getField().getFieldName();
      fieldType = projectLocation.getFieldType().name();
      ukcsArea = projectLocation.getField().getUkcsArea() != null
          ? projectLocation.getField().getUkcsArea().name()
          : null;
      maximumWaterDepthMeters = projectLocation.getMaximumWaterDepth();

      if (projectLocationBlocks != null) {
        licenceBlocks = projectLocationBlocks
            .stream()
            .sorted(Comparator.comparing(ProjectLocationBlock::getSortKey))
            .map(ProjectLocationBlock::getBlockReference)
            .toList();
      }
    }

    var submittedOn = projectDetail.getSubmittedInstant();

    return new InfrastructureProjectJson(
        id,
        operatorName,
        title,
        summary,
        fieldStage,
        fieldStageSubCategory,
        contactName,
        contactPhoneNumber,
        contactJobTitle,
        contactEmailAddress,
        firstProductionDateQuarter,
        firstProductionDateYear,
        fieldName,
        fieldType,
        ukcsArea,
        maximumWaterDepthMeters,
        licenceBlocks,
        submittedOn
    );
  }
}
