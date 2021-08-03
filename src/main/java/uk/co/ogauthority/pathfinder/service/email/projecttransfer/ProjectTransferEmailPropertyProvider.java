package uk.co.ogauthority.pathfinder.service.email.projecttransfer;

import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.IncomingOperatorProjectTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.OutgoingOperatorProjectTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

/**
 * Interface allowing consumers to provide custom project operator transfer email properties
 * for a specific project type.
 */
public interface ProjectTransferEmailPropertyProvider {

  /**
   * The project type supported by this implementation.
   * @return The project type supported by this implementation
   */
  ProjectType getSupportedProjectType();

  /**
   * The incoming operator requested properties specific to this implementation.
   * @param projectDetail The project detail being processed
   * @param transferReason The reason for the project transfer
   * @param previousOperatorName The name of the previous operator
   * @return the incoming operator email properties specific to this project type
   */
  IncomingOperatorProjectTransferEmailProperties getIncomingOperatorTransferEmailProperties(ProjectDetail projectDetail,
                                                                                            String transferReason,
                                                                                            String previousOperatorName);

  /**
   * The outgoing operator requested properties specific to this implementation.
   * @param projectDetail The project detail being processed
   * @param transferReason The reason for the project transfer
   * @param currentOperatorName The name of the current operator
   * @return the outgoing operator email properties specific to this project type
   */
  OutgoingOperatorProjectTransferEmailProperties getOutgoingOperatorTransferEmailProperties(ProjectDetail projectDetail,
                                                                                            String transferReason,
                                                                                            String currentOperatorName);
}
