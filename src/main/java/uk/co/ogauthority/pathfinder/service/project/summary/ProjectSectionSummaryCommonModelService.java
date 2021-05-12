package uk.co.ogauthority.pathfinder.service.project.summary;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;

@Service
public class ProjectSectionSummaryCommonModelService {

  protected static final String SECTION_TITLE_ATTR_NAME = "sectionTitle";
  protected static final String SECTION_ID_ATTR_NAME = "sectionId";

  /**
   * Returns a map with the common properties for use in services implementing ProjectSectionSummaryService.
   * @param projectDetail The project detail this summary is for
   * @param sectionName The user facing name of the section
   * @param sectionId An id for the section to be used for anchor tags
   * @return a map with the common properties for services implementing ProjectSectionSummaryService
   */
  public Map<String, Object> getCommonSummaryModelMap(ProjectDetail projectDetail,
                                                      String sectionName,
                                                      String sectionId) {
    final var summaryModel = new HashMap<String, Object>();
    summaryModel.put(SECTION_TITLE_ATTR_NAME, sectionName);
    summaryModel.put(SECTION_ID_ATTR_NAME, sectionId);

    ProjectTypeModelUtil.addProjectTypeDisplayNameAttributesToModel(summaryModel, projectDetail);

    return summaryModel;
  }
}
