package uk.co.ogauthority.pathfinder.service.file;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;

/**
 * Interface to signify that this entity can have files linked to it. This is required so
 * any entity that implements this interface can be referenced within {@link FileLinkService}
 */
public interface EntityWithLinkedFile {

  ProjectDetail getProjectDetail();

}
