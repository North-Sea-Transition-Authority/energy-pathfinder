package uk.co.ogauthority.pathfinder.publicdata;

import java.time.Instant;
import java.util.List;

class InfrastructureProjectJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private Builder() {
    }

    private Integer id = 1;
    private String operatorName = "BP";
    private String title = "Test project";
    private String summary = "Test summary";
    private String fieldStage = "DECOMMISSIONING";
    private String fieldStageSubCategory;
    private String contactName = "Test contact name";
    private String contactPhoneNumber = "01303 123 456";
    private String contactJobTitle = "Test contact job title";
    private String contactEmailAddress = "test@email.address";
    private String firstProductionDateQuarter;
    private Integer firstProductionDateYear;
    private String fieldName = "MERCURY";
    private String fieldType = "CARBON_STORAGE";
    private String ukcsArea = "CNS";
    private Integer maximumWaterDepthMeters = 60;
    private List<String> licenceBlocks = List.of("12/34, 12/56");
    private Instant submittedOn = Instant.now();

    Builder withId(Integer id) {
      this.id = id;
      return this;
    }

    Builder withOperatorName(String operatorName) {
      this.operatorName = operatorName;
      return this;
    }

    Builder withTitle(String title) {
      this.title = title;
      return this;
    }

    Builder withSummary(String summary) {
      this.summary = summary;
      return this;
    }

    Builder withFieldStage(String fieldStage) {
      this.fieldStage = fieldStage;
      return this;
    }

    Builder withFieldStageSubCategory(String fieldStageSubCategory) {
      this.fieldStageSubCategory = fieldStageSubCategory;
      return this;
    }

    Builder withContactName(String contactName) {
      this.contactName = contactName;
      return this;
    }

    Builder withContactPhoneNumber(String contactPhoneNumber) {
      this.contactPhoneNumber = contactPhoneNumber;
      return this;
    }

    Builder withContactJobTitle(String contactJobTitle) {
      this.contactJobTitle = contactJobTitle;
      return this;
    }

    Builder withContactEmailAddress(String contactEmailAddress) {
      this.contactEmailAddress = contactEmailAddress;
      return this;
    }

    Builder withFirstProductionDateQuarter(String firstProductionDateQuarter) {
      this.firstProductionDateQuarter = firstProductionDateQuarter;
      return this;
    }

    Builder withFirstProductionDateYear(Integer firstProductionDateYear) {
      this.firstProductionDateYear = firstProductionDateYear;
      return this;
    }

    Builder withFieldName(String fieldName) {
      this.fieldName = fieldName;
      return this;
    }

    Builder withFieldType(String fieldType) {
      this.fieldType = fieldType;
      return this;
    }

    Builder withUkcsArea(String ukcsArea) {
      this.ukcsArea = ukcsArea;
      return this;
    }

    Builder withMaximumWaterDepthMeters(Integer maximumWaterDepthMeters) {
      this.maximumWaterDepthMeters = maximumWaterDepthMeters;
      return this;
    }

    Builder withLicenceBlocks(List<String> licenceBlocks) {
      this.licenceBlocks = licenceBlocks;
      return this;
    }

    Builder withSubmittedOn(Instant submittedOn) {
      this.submittedOn = submittedOn;
      return this;
    }

    InfrastructureProjectJson build() {
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
}
