package uk.co.ogauthority.pathfinder.model.view.projectinformation;

import java.util.Objects;

public class ProjectInformationView {

  private String projectTitle;

  private String projectSummary;

  private String fieldStage;

  private String developmentFirstProductionDate;

  private String discoveryFirstProductionDate;

  private String decomWorkStartDate;

  private String decomProductionCessationDate;

  private String energyTransitionCategory;

  private String contactName;

  private String contactPhoneNumber;

  private String contactJobTitle;

  private String contactEmailAddress;

  public String getProjectTitle() {
    return projectTitle;
  }

  public void setProjectTitle(String projectTitle) {
    this.projectTitle = projectTitle;
  }

  public String getProjectSummary() {
    return projectSummary;
  }

  public void setProjectSummary(String projectSummary) {
    this.projectSummary = projectSummary;
  }

  public String getFieldStage() {
    return fieldStage;
  }

  public void setFieldStage(String fieldStage) {
    this.fieldStage = fieldStage;
  }

  public String getDevelopmentFirstProductionDate() {
    return developmentFirstProductionDate;
  }

  public void setDevelopmentFirstProductionDate(String developmentFirstProductionDate) {
    this.developmentFirstProductionDate = developmentFirstProductionDate;
  }

  public String getDiscoveryFirstProductionDate() {
    return discoveryFirstProductionDate;
  }

  public void setDiscoveryFirstProductionDate(String discoveryFirstProductionDate) {
    this.discoveryFirstProductionDate = discoveryFirstProductionDate;
  }

  public String getDecomWorkStartDate() {
    return decomWorkStartDate;
  }

  public void setDecomWorkStartDate(String decomWorkStartDate) {
    this.decomWorkStartDate = decomWorkStartDate;
  }

  public String getDecomProductionCessationDate() {
    return decomProductionCessationDate;
  }

  public void setDecomProductionCessationDate(String decomProductionCessationDate) {
    this.decomProductionCessationDate = decomProductionCessationDate;
  }

  public String getEnergyTransitionCategory() {
    return energyTransitionCategory;
  }

  public void setEnergyTransitionCategory(String energyTransitionCategory) {
    this.energyTransitionCategory = energyTransitionCategory;
  }

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public String getContactPhoneNumber() {
    return contactPhoneNumber;
  }

  public void setContactPhoneNumber(String contactPhoneNumber) {
    this.contactPhoneNumber = contactPhoneNumber;
  }

  public String getContactJobTitle() {
    return contactJobTitle;
  }

  public void setContactJobTitle(String contactJobTitle) {
    this.contactJobTitle = contactJobTitle;
  }

  public String getContactEmailAddress() {
    return contactEmailAddress;
  }

  public void setContactEmailAddress(String contactEmailAddress) {
    this.contactEmailAddress = contactEmailAddress;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    var that = (ProjectInformationView) o;

    return Objects.equals(that.getProjectTitle(), getProjectTitle())
        && Objects.equals(that.getProjectSummary(), getProjectSummary())
        && Objects.equals(that.getFieldStage(), getFieldStage())
        && Objects.equals(that.getDevelopmentFirstProductionDate(), getDevelopmentFirstProductionDate())
        && Objects.equals(that.getDiscoveryFirstProductionDate(), getDiscoveryFirstProductionDate())
        && Objects.equals(that.getDecomWorkStartDate(), getDecomWorkStartDate())
        && Objects.equals(that.getDecomProductionCessationDate(), getDecomProductionCessationDate())
        && Objects.equals(that.getContactName(), getContactName())
        && Objects.equals(that.getContactPhoneNumber(), getContactPhoneNumber())
        && Objects.equals(that.getContactJobTitle(), getContactJobTitle())
        && Objects.equals(that.getContactEmailAddress(), getContactEmailAddress());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getProjectTitle(),
        getProjectSummary(),
        getFieldStage(),
        getDevelopmentFirstProductionDate(),
        getDiscoveryFirstProductionDate(),
        getDecomWorkStartDate(),
        getDecomProductionCessationDate(),
        getContactName(),
        getContactPhoneNumber(),
        getContactJobTitle(),
        getContactEmailAddress()
    );
  }
}
