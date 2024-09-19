package uk.co.ogauthority.pathfinder.model.form.subscription;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.enums.subscription.RelationToPathfinder;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.email.ValidEmail;
import uk.co.ogauthority.pathfinder.model.form.validation.lengthrestrictedstring.LengthRestrictedString;

public class SubscribeForm {

  @LengthRestrictedString(messagePrefix = "Your first name", groups = {FullValidation.class})
  @NotNull(message = "Enter your first name", groups = FullValidation.class)
  private String forename;

  @LengthRestrictedString(messagePrefix = "Your last name", groups = {FullValidation.class})
  @NotNull(message = "Enter your last name", groups = FullValidation.class)
  private String surname;

  @ValidEmail(messagePrefix = "Your email address", groups = {FullValidation.class})
  @NotNull(message = "Enter your email address", groups = FullValidation.class)
  private String emailAddress;

  @NotNull(message = "Select your relation to Energy Pathfinder", groups = FullValidation.class)
  private RelationToPathfinder relationToPathfinder;

  private String subscribeReason;

  @NotNull(message = "Select yes if you are interested in being updated on all pathfinder projects", groups = FullValidation.class)
  private Boolean interestedInAllProjects;

  private List<String> fieldStages;

  public String getForename() {
    return forename;
  }

  public void setForename(String forename) {
    this.forename = forename;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public RelationToPathfinder getRelationToPathfinder() {
    return relationToPathfinder;
  }

  public void setRelationToPathfinder(
      RelationToPathfinder relationToPathfinder) {
    this.relationToPathfinder = relationToPathfinder;
  }

  public String getSubscribeReason() {
    return subscribeReason;
  }

  public void setSubscribeReason(String subscribeReason) {
    this.subscribeReason = subscribeReason;
  }

  public Boolean getInterestedInAllProjects() {
    return interestedInAllProjects;
  }

  public void setInterestedInAllProjects(Boolean interestedInAllProjects) {
    this.interestedInAllProjects = interestedInAllProjects;
  }

  public List<String> getFieldStages() {
    return fieldStages;
  }

  public void setFieldStages(List<String> fieldStages) {
    this.fieldStages = fieldStages;
  }
}
