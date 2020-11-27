package uk.co.ogauthority.pathfinder.service.project;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.project.platformsfpsos.PlatformsFpsosService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectCleanUpServiceTest {

  @Mock
  private UpcomingTenderService upcomingTenderService;

  @Mock
  private PlatformsFpsosService platformsFpsosService;

  private ProjectCleanUpService projectCleanUpService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    projectCleanUpService = new ProjectCleanUpService(List.of(upcomingTenderService, platformsFpsosService));
  }

  @Test
  public void removeProjectSectionDataIfNotRelevant_whenAllSectionsInTaskList_noRemoveMethodInvoked() {

    when(upcomingTenderService.canShowInTaskList(projectDetail)).thenReturn(true);
    when(platformsFpsosService.canShowInTaskList(projectDetail)).thenReturn(true);

    projectCleanUpService.removeProjectSectionDataIfNotRelevant(projectDetail);

    verify(upcomingTenderService, times(0)).removeSectionData(projectDetail);
    verify(platformsFpsosService, times(0)).removeSectionData(projectDetail);
  }

  @Test
  public void removeProjectSectionDataIfNotRelevant_whenAllSectionsNotInTaskList_removeMethodInvoked() {

    when(upcomingTenderService.canShowInTaskList(projectDetail)).thenReturn(false);
    when(platformsFpsosService.canShowInTaskList(projectDetail)).thenReturn(false);

    projectCleanUpService.removeProjectSectionDataIfNotRelevant(projectDetail);

    verify(upcomingTenderService, times(1)).removeSectionData(projectDetail);
    verify(platformsFpsosService, times(1)).removeSectionData(projectDetail);
  }

  @Test
  public void removeProjectSectionDataIfNotRelevant_whenSomeSectionsNotInTaskList_removeMethodInvokedOnSelectedSections() {

    when(upcomingTenderService.canShowInTaskList(projectDetail)).thenReturn(true);
    when(platformsFpsosService.canShowInTaskList(projectDetail)).thenReturn(false);

    projectCleanUpService.removeProjectSectionDataIfNotRelevant(projectDetail);

    verify(upcomingTenderService, times(0)).removeSectionData(projectDetail);
    verify(platformsFpsosService, times(1)).removeSectionData(projectDetail);
  }
}