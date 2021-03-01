package uk.co.ogauthority.pathfinder.service.project.subseainfrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.subseainfrastructure.SubseaInfrastructureView;
import uk.co.ogauthority.pathfinder.model.view.subseainfrastructure.SubseaInfrastructureViewUtil;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.SubseaInfrastructureTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class SubseaInfrastructureSectionSummaryServiceTest {

  @Mock
  private SubseaInfrastructureService subseaInfrastructureService;

  @Mock
  private DifferenceService differenceService;

  private SubseaInfrastructureSectionSummaryService subseaInfrastructureSectionSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    subseaInfrastructureSectionSummaryService = new SubseaInfrastructureSectionSummaryService(
        subseaInfrastructureService,
        differenceService
    );
  }

  @Test
  public void canShowSection_whenCanShowInTaskList_thenTrue() {
    when(subseaInfrastructureService.canShowInTaskList(detail)).thenReturn(true);

    assertThat(subseaInfrastructureSectionSummaryService.canShowSection(detail)).isTrue();
  }

  @Test
  public void canShowSection_whenCannotShowInTaskList_thenFalse() {
    when(subseaInfrastructureService.canShowInTaskList(detail)).thenReturn(false);

    assertThat(subseaInfrastructureSectionSummaryService.canShowSection(detail)).isFalse();
  }

  @Test
  public void getSummary() {
    var subseaInfrastructure1 = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withDevUkFacility();
    var subseaInfrastructure2 = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();
    var previousSubseaInfrastructures = List.of(subseaInfrastructure1, subseaInfrastructure2);
    var previousSubseaInfastructureViews = List.of(
        SubseaInfrastructureViewUtil.from(subseaInfrastructure1, 1),
        SubseaInfrastructureViewUtil.from(subseaInfrastructure2, 2)
    );

    var subseaInfrastructure3 = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withDevUkFacility();
    var currentSubseaInfrastructures = List.of(subseaInfrastructure3);
    var currentSubseaInfrastructureViews = List.of(
        SubseaInfrastructureViewUtil.from(subseaInfrastructure3, 1)
    );

    when(subseaInfrastructureService.getSubseaInfrastructures(detail)).thenReturn(currentSubseaInfrastructures);
    when(subseaInfrastructureService.getSubseaInfrastructuresByProjectAndVersion(
        detail.getProject(),
        detail.getVersion() - 1
    )).thenReturn(previousSubseaInfrastructures);
    var sectionSummary = subseaInfrastructureSectionSummaryService.getSummary(detail);

    assertModelProperties(sectionSummary);
    assertInteractions(currentSubseaInfrastructureViews, previousSubseaInfastructureViews);
  }

  @Test
  public void getSummary_noSubseaInfrastructures() {
    when(subseaInfrastructureService.getSubseaInfrastructures(detail)).thenReturn(Collections.emptyList());

    var sectionSummary = subseaInfrastructureSectionSummaryService.getSummary(detail);

    assertModelProperties(sectionSummary);

    verify(differenceService, never()).differentiate(any(), any(), any());
  }

  private void assertModelProperties(ProjectSectionSummary projectSectionSummary) {
    assertThat(projectSectionSummary.getDisplayOrder()).isEqualTo(SubseaInfrastructureSectionSummaryService.DISPLAY_ORDER);
    assertThat(projectSectionSummary.getSidebarSectionLinks()).isEqualTo(List.of(SubseaInfrastructureSectionSummaryService.SECTION_LINK));
    assertThat(projectSectionSummary.getTemplatePath()).isEqualTo(SubseaInfrastructureSectionSummaryService.TEMPLATE_PATH);

    var model = projectSectionSummary.getTemplateModel();

    assertThat(model).containsOnlyKeys(
        "sectionTitle",
        "sectionId",
        "subseaInfrastructureDiffModel"
    );

    assertThat(model).containsEntry("sectionTitle", SubseaInfrastructureSectionSummaryService.PAGE_NAME);
    assertThat(model).containsEntry("sectionId", SubseaInfrastructureSectionSummaryService.SECTION_ID);
  }

  private void assertInteractions(List<SubseaInfrastructureView> currentSubseaInfrastructureViews,
                                  List<SubseaInfrastructureView> previousSubseaInfrastructureViews) {

    currentSubseaInfrastructureViews.forEach(subseaInfrastructureView -> {

      var previousSubseaInfrastructureView = previousSubseaInfrastructureViews
          .stream()
          .filter(view -> view.getDisplayOrder().equals(subseaInfrastructureView.getDisplayOrder()))
          .findFirst()
          .orElse(new SubseaInfrastructureView());

      verify(differenceService, times(1)).differentiate(
          subseaInfrastructureView,
          previousSubseaInfrastructureView,
          Set.of("summaryLinks")
      );
    });
  }
}
