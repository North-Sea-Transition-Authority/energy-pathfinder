package uk.co.ogauthority.pathfinder.service.projectmanagement.updaterequest;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.projectupdate.RegulatorUpdateRequestViewUtil;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorUpdateRequestService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUpdateTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementUpdateRequestSectionServiceTest {

  @Mock
  private RegulatorUpdateRequestService regulatorUpdateRequestService;

  @Mock
  private WebUserAccountService webUserAccountService;

  @Mock
  private ServiceProperties serviceProperties;

  private ProjectManagementUpdateRequestSectionService projectManagementUpdateRequestSectionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectManagementUpdateRequestSectionService = new ProjectManagementUpdateRequestSectionService(
        regulatorUpdateRequestService,
        webUserAccountService,
        serviceProperties
    );
  }

  @Test
  public void getSection_whenCurrentVersionAndUpdateRequest_thenReturnView() {
    var regulatorUpdateRequest = ProjectUpdateTestUtil.createRegulatorUpdateRequest();

    when(regulatorUpdateRequestService.getUpdateRequest(projectDetail)).thenReturn(Optional.of(regulatorUpdateRequest));
    when(webUserAccountService.getWebUserAccountOrError(regulatorUpdateRequest.getRequestedByWuaId())).thenReturn(authenticatedUser);

    var section = projectManagementUpdateRequestSectionService.getSection(projectDetail, authenticatedUser);
    assertThat(section.getTemplatePath()).isEqualTo(ProjectManagementUpdateRequestSectionService.TEMPLATE_PATH);
    assertThat(section.getDisplayOrder()).isEqualTo(ProjectManagementUpdateRequestSectionService.DISPLAY_ORDER);
    assertThat(section.getSectionType()).isEqualTo(ProjectManagementUpdateRequestSectionService.SECTION_TYPE);

    var regulatorUpdateRequestView = RegulatorUpdateRequestViewUtil.from(regulatorUpdateRequest, authenticatedUser);
    assertThat(section.getTemplateModel()).containsOnly(
        entry("regulatorUpdateRequestView", regulatorUpdateRequestView),
        entry("service", serviceProperties)
    );
  }

  @Test
  public void getSection_whenNotCurrentVersion_thenReturnView() {
    var previousVersionProjectDetail = ProjectUtil.getProjectDetails();
    previousVersionProjectDetail.setIsCurrentVersion(false);

    verify(regulatorUpdateRequestService, never()).getUpdateRequest(previousVersionProjectDetail);

    var section = projectManagementUpdateRequestSectionService.getSection(projectDetail, authenticatedUser);
    assertThat(section.getTemplatePath()).isEqualTo(ProjectManagementUpdateRequestSectionService.TEMPLATE_PATH);
    assertThat(section.getDisplayOrder()).isEqualTo(ProjectManagementUpdateRequestSectionService.DISPLAY_ORDER);
    assertThat(section.getSectionType()).isEqualTo(ProjectManagementUpdateRequestSectionService.SECTION_TYPE);

    assertThat(section.getTemplateModel()).isEmpty();
  }

  @Test
  public void getSection_whenCurrentVersionAndNoUpdateRequest_thenNoView() {
    when(regulatorUpdateRequestService.getUpdateRequest(projectDetail)).thenReturn(Optional.empty());

    var section = projectManagementUpdateRequestSectionService.getSection(projectDetail, authenticatedUser);
    assertThat(section.getTemplatePath()).isEqualTo(ProjectManagementUpdateRequestSectionService.TEMPLATE_PATH);
    assertThat(section.getDisplayOrder()).isEqualTo(ProjectManagementUpdateRequestSectionService.DISPLAY_ORDER);
    assertThat(section.getSectionType()).isEqualTo(ProjectManagementUpdateRequestSectionService.SECTION_TYPE);

    assertThat(section.getTemplateModel()).isEmpty();
  }
}
