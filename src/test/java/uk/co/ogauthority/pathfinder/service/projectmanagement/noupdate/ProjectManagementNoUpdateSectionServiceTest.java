package uk.co.ogauthority.pathfinder.service.projectmanagement.noupdate;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
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
import uk.co.ogauthority.pathfinder.model.view.projectupdate.NoUpdateNotificationViewUtil;
import uk.co.ogauthority.pathfinder.service.projectupdate.OperatorProjectUpdateService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUpdateTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementNoUpdateSectionServiceTest {

  @Mock
  private OperatorProjectUpdateService operatorProjectUpdateService;

  @Mock
  private WebUserAccountService webUserAccountService;

  @Mock
  private ServiceProperties serviceProperties;

  private ProjectManagementNoUpdateSectionService projectManagementNoUpdateSectionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectManagementNoUpdateSectionService = new ProjectManagementNoUpdateSectionService(
        operatorProjectUpdateService,
        webUserAccountService,
        serviceProperties
    );
  }

  @Test
  public void getSection_whenNoUpdateNotification_thenReturnView() {
    var noUpdateNotification = ProjectUpdateTestUtil.createNoUpdateNotification();

    when(operatorProjectUpdateService.getNoUpdateNotificationByUpdateToDetail(projectDetail)).thenReturn(Optional.of(noUpdateNotification));
    when(webUserAccountService.getWebUserAccountOrError(noUpdateNotification.getProjectUpdate().getToDetail().getCreatedByWua())).thenReturn(
        authenticatedUser
    );

    var section = projectManagementNoUpdateSectionService.getSection(projectDetail, authenticatedUser);
    assertThat(section.getTemplatePath()).isEqualTo(ProjectManagementNoUpdateSectionService.TEMPLATE_PATH);
    assertThat(section.getDisplayOrder()).isEqualTo(ProjectManagementNoUpdateSectionService.DISPLAY_ORDER);
    assertThat(section.getSectionType()).isEqualTo(ProjectManagementNoUpdateSectionService.SECTION_TYPE);

    var noUpdateNotificationView = NoUpdateNotificationViewUtil.from(noUpdateNotification, authenticatedUser);
    assertThat(section.getTemplateModel()).containsExactly(
        entry("noUpdateNotificationView", noUpdateNotificationView),
        entry("service", serviceProperties)
    );
  }

  @Test
  public void getSection_whenNoNoUpdateNotificationView_thenNoView() {
    when(operatorProjectUpdateService.getNoUpdateNotificationByUpdateToDetail(projectDetail)).thenReturn(Optional.empty());

    var section = projectManagementNoUpdateSectionService.getSection(projectDetail, authenticatedUser);
    assertThat(section.getTemplatePath()).isEqualTo(ProjectManagementNoUpdateSectionService.TEMPLATE_PATH);
    assertThat(section.getDisplayOrder()).isEqualTo(ProjectManagementNoUpdateSectionService.DISPLAY_ORDER);
    assertThat(section.getSectionType()).isEqualTo(ProjectManagementNoUpdateSectionService.SECTION_TYPE);

    assertThat(section.getTemplateModel()).isEmpty();
  }
}
