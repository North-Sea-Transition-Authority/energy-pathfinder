package uk.co.ogauthority.pathfinder.service.file;

import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;

/**
 * Interface to signify that this is an entity that represents a link between a ProjectDetailFile and another entity.
 * Any entity that implements this interface can be referenced within {@link FileLinkService}
 */
public interface FileLinkEntity {

  ProjectDetailFile getProjectDetailFile();

}
