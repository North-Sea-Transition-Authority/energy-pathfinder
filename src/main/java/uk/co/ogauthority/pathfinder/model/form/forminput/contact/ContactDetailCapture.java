package uk.co.ogauthority.pathfinder.model.form.forminput.contact;

/**
 * Implement this interface in any entities whose related forms
 * use the ContactDetailFrom object. This allows easy form construction
 * using the constructor that takes in any class implementing ContactDetailCapture
 */
public interface ContactDetailCapture {

  String getName();

  String getPhoneNumber();

  String getJobTitle();

  String getEmailAddress();
}
