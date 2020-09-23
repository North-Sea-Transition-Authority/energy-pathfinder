package uk.co.ogauthority.pathfinder.model.entity.file;

public enum FileLinkStatus {
  TEMPORARY, //Uploaded files which have not been actively "saved" against a form page
  FULL, // Uploaded files which have been actively "saved" against a form page
  ALL // When querying based on link status, get both full and temporary linked files
}
