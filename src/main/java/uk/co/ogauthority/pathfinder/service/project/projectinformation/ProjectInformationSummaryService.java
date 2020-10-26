package uk.co.ogauthority.pathfinder.service.project.projectinformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.project.projectinformation.ProjectInformationController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.model.view.summary.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryService;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@Service
public class ProjectInformationSummaryService implements ProjectSectionSummaryService {
  public static final String TEMPLATE_PATH = "project/projectinformation/projectInformationSummary.ftl";
  public static final String PAGE_NAME = ProjectInformationController.PAGE_NAME;
  public static final String SECTION_ID = "#projectInformation";
  public static final SidebarSectionLink SECTION_LINK = SidebarSectionLink.createAnchorLink(
      PAGE_NAME,
      SECTION_ID
  );
  public static final int DISPLAY_ORDER = 1;

  private final ProjectInformationService projectInformationService;

  @Autowired
  public ProjectInformationSummaryService(ProjectInformationService projectInformationService) {
    this.projectInformationService = projectInformationService;
  }

  @Override
  public ProjectSectionSummary getSummary(ProjectDetail detail) {
    Map<String, Object> summaryModel = new HashMap<>();
    projectInformationService.getProjectInformation(detail).ifPresent(
        projectInformation -> {
          summaryModel.put("sectionTitle", PAGE_NAME);
          summaryModel.put("sectionId", SECTION_ID);
          summaryModel.put("projectTitle", projectInformation.getProjectTitle());
          summaryModel.put("projectSummary", projectInformation.getProjectSummary());
          summaryModel.put("fieldStage", projectInformation.getFieldStage() != null
              ? projectInformation.getFieldStage().getDisplayName()
              : ""
          );
          summaryModel.put("developmentRelated", FieldStage.DEVELOPMENT.equals(projectInformation.getFieldStage()));
          summaryModel.put("discoveryRelated", FieldStage.DISCOVERY.equals(projectInformation.getFieldStage()));
          summaryModel.put("decomRelated", FieldStage.DECOMMISSIONING.equals(projectInformation.getFieldStage()));
          summaryModel.put("developmentFirstProductionDate", getFirstProductionDate(projectInformation));
          summaryModel.put("discoveryFirstProductionDate", getFirstProductionDate(projectInformation));
          summaryModel.put("decomWorkStartDate",
              DateUtil.getDateFromQuarterYear(
                  projectInformation.getDecomWorkStartDateQuarter(),
                  projectInformation.getDecomWorkStartDateYear()
              )
          );
          summaryModel.put("decomProductionCessationDate",
              DateUtil.formatDate(projectInformation.getProductionCessationDate())
          );
          summaryModel.put("name", projectInformation.getName());
          summaryModel.put("phoneNumber", projectInformation.getPhoneNumber());
          summaryModel.put("jobTitle", projectInformation.getJobTitle());
          summaryModel.put("emailAddress", projectInformation.getEmailAddress());
        }
    );


    return new ProjectSectionSummary(
      List.of(SECTION_LINK),
        TEMPLATE_PATH,
        summaryModel,
        DISPLAY_ORDER
    );
  }

  private String getFirstProductionDate(ProjectInformation projectInformation) {
    return DateUtil.getDateFromQuarterYear(
        projectInformation.getFirstProductionDateQuarter(),
        projectInformation.getFirstProductionDateYear()
    );
  }


}
