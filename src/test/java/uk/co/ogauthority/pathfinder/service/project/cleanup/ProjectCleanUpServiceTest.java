package uk.co.ogauthority.pathfinder.service.project.cleanup;

import static org.mockito.Mockito.never;
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
import uk.co.ogauthority.pathfinder.service.project.TestProjectFormSectionService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectCleanUpServiceTest {

  @Mock
  private TestProjectFormSectionService testProjectFormSectionServiceA;

  @Mock
  private TestProjectFormSectionService testProjectFormSectionServiceB;

  private ProjectCleanUpService projectCleanUpService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    projectCleanUpService = new ProjectCleanUpService(
        List.of(testProjectFormSectionServiceA, testProjectFormSectionServiceB)
    );
  }

  @Test
  public void removeProjectSectionDataIfNotRelevant_whenAllSectionsInTaskListAndAllowDataCleanUp_onlyRemoveSectionDataIfNotRelevantInvoked() {

    when(testProjectFormSectionServiceA.isTaskValidForProjectDetail(projectDetail)).thenReturn(true);
    when(testProjectFormSectionServiceB.isTaskValidForProjectDetail(projectDetail)).thenReturn(true);

    when(testProjectFormSectionServiceA.allowSectionDataCleanUp(projectDetail)).thenReturn(true);
    when(testProjectFormSectionServiceB.allowSectionDataCleanUp(projectDetail)).thenReturn(true);

    projectCleanUpService.removeProjectSectionDataIfNotRelevant(projectDetail);

    verify(testProjectFormSectionServiceA, times(0)).removeSectionData(projectDetail);
    verify(testProjectFormSectionServiceB, times(0)).removeSectionData(projectDetail);

    verify(testProjectFormSectionServiceA).removeSectionDataIfNotRelevant(projectDetail);
    verify(testProjectFormSectionServiceB).removeSectionDataIfNotRelevant(projectDetail);
  }

  @Test
  public void removeProjectSectionDataIfNotRelevant_whenAllSectionsNotInTaskListAndAllowDataCleanUp_onlyRemoveSectionDataInvoked() {

    when(testProjectFormSectionServiceA.isTaskValidForProjectDetail(projectDetail)).thenReturn(false);
    when(testProjectFormSectionServiceB.isTaskValidForProjectDetail(projectDetail)).thenReturn(false);

    when(testProjectFormSectionServiceA.allowSectionDataCleanUp(projectDetail)).thenReturn(true);
    when(testProjectFormSectionServiceB.allowSectionDataCleanUp(projectDetail)).thenReturn(true);

    projectCleanUpService.removeProjectSectionDataIfNotRelevant(projectDetail);

    verify(testProjectFormSectionServiceA, times(1)).removeSectionData(projectDetail);
    verify(testProjectFormSectionServiceB, times(1)).removeSectionData(projectDetail);

    verify(testProjectFormSectionServiceA, never()).removeSectionDataIfNotRelevant(projectDetail);
    verify(testProjectFormSectionServiceB, never()).removeSectionDataIfNotRelevant(projectDetail);
  }

  @Test
  public void removeProjectSectionDataIfNotRelevant_whenSomeSectionsNotInTaskListAndAllAllowDataCleanUp_correctRemoveMethodInvoked() {

    when(testProjectFormSectionServiceA.isTaskValidForProjectDetail(projectDetail)).thenReturn(true);
    when(testProjectFormSectionServiceB.isTaskValidForProjectDetail(projectDetail)).thenReturn(false);

    when(testProjectFormSectionServiceA.allowSectionDataCleanUp(projectDetail)).thenReturn(true);
    when(testProjectFormSectionServiceB.allowSectionDataCleanUp(projectDetail)).thenReturn(true);

    projectCleanUpService.removeProjectSectionDataIfNotRelevant(projectDetail);

    verify(testProjectFormSectionServiceA, never()).removeSectionData(projectDetail);
    verify(testProjectFormSectionServiceB, times(1)).removeSectionData(projectDetail);

    verify(testProjectFormSectionServiceA, times(1)).removeSectionDataIfNotRelevant(projectDetail);
    verify(testProjectFormSectionServiceB, never()).removeSectionDataIfNotRelevant(projectDetail);
  }

  @Test
  public void removeProjectSectionDataIfNotRelevant_whenAllSectionsNotAllowDataCleanUp_noRemoveMethodInvoked() {

    when(testProjectFormSectionServiceA.allowSectionDataCleanUp(projectDetail)).thenReturn(false);
    when(testProjectFormSectionServiceB.allowSectionDataCleanUp(projectDetail)).thenReturn(false);

    projectCleanUpService.removeProjectSectionDataIfNotRelevant(projectDetail);

    verify(testProjectFormSectionServiceA, never()).removeSectionData(projectDetail);
    verify(testProjectFormSectionServiceB, never()).removeSectionData(projectDetail);

    verify(testProjectFormSectionServiceA, never()).removeSectionDataIfNotRelevant(projectDetail);
    verify(testProjectFormSectionServiceB, never()).removeSectionDataIfNotRelevant(projectDetail);
  }

  @Test
  public void removeProjectSectionDataIfNotRelevant_whenSomeSectionsNotAllowDataCleanUp_onlyRemoveSectionDataMethodInvokedOnSelectedSections() {

    when(testProjectFormSectionServiceA.allowSectionDataCleanUp(projectDetail)).thenReturn(false);
    when(testProjectFormSectionServiceB.allowSectionDataCleanUp(projectDetail)).thenReturn(true);

    when(testProjectFormSectionServiceB.isTaskValidForProjectDetail(projectDetail)).thenReturn(false);

    projectCleanUpService.removeProjectSectionDataIfNotRelevant(projectDetail);

    verify(testProjectFormSectionServiceA, never()).removeSectionData(projectDetail);
    verify(testProjectFormSectionServiceB, times(1)).removeSectionData(projectDetail);

    verify(testProjectFormSectionServiceA, never()).removeSectionDataIfNotRelevant(projectDetail);
    verify(testProjectFormSectionServiceB, never()).removeSectionDataIfNotRelevant(projectDetail);
  }
}
