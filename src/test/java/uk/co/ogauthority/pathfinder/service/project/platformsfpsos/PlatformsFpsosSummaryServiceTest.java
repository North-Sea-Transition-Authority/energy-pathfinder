package uk.co.ogauthority.pathfinder.service.project.platformsfpsos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.platformfpso.PlatformFpsoView;
import uk.co.ogauthority.pathfinder.model.view.platformfpso.PlatformFpsoViewUtil;
import uk.co.ogauthority.pathfinder.testutil.PlatformFpsoTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class PlatformsFpsosSummaryServiceTest {

  @Mock
  private PlatformsFpsosService platformsFpsosService;

  private PlatformsFpsosSummaryService platformsFpsosSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private final PlatformFpso platformFpso = PlatformFpsoTestUtil.getPlatformFpso_withSubstructuresRemoved_manualStructure(detail);

  private final PlatformFpso platformFpsoWithoutSubstructuresRemoved = PlatformFpsoTestUtil.getPlatformFpso_NoSubstructuresRemoved(detail);

  @Before
  public void setUp() throws Exception {
    platformsFpsosSummaryService = new PlatformsFpsosSummaryService(platformsFpsosService);
    when(platformsFpsosService.getPlatformsFpsosForDetail(detail)).thenReturn(
        List.of(platformFpso, platformFpsoWithoutSubstructuresRemoved)
    );
  }

  @Test
  public void getSummaryViews() {
    var views = platformsFpsosSummaryService.getSummaryViews(detail);
    assertThat(views.size()).isEqualTo(2);
    var view1 = views.get(0);
    var view2 = views.get(1);

    assertThat(view1.getPlatformFpso()).isEqualTo(platformFpso.getManualStructureName());
    assertThat(view1.getDisplayOrder()).isEqualTo(1);
    assertThat(view2.getPlatformFpso()).isEqualTo(platformFpsoWithoutSubstructuresRemoved.getStructure().getFacilityName());
    assertThat(view2.getDisplayOrder()).isEqualTo(2);
    checkCommonFields(view1, platformFpso);
    checkCommonFields(view2, platformFpsoWithoutSubstructuresRemoved);
  }

  @Test
  public void getValidatedSummaryViews_allValid() {
    when(platformsFpsosService.isValid(any(), any())).thenReturn(true);
    var views = platformsFpsosSummaryService.getValidatedSummaryViews(detail);
    assertThat(views.size()).isEqualTo(2);
    var view1 = views.get(0);
    var view2 = views.get(1);
    assertThat(view1.isValid()).isTrue();
    assertThat(view2.isValid()).isTrue();
  }

  @Test
  public void getValidatedSummaryViews_containsInvalidEntry() {
    when(platformsFpsosService.isValid(platformFpso, ValidationType.FULL)).thenReturn(true);
    var views = platformsFpsosSummaryService.getValidatedSummaryViews(detail);
    assertThat(views.size()).isEqualTo(2);
    var view1 = views.get(0);
    var view2 = views.get(1);
    assertThat(view1.isValid()).isTrue();
    assertThat(view2.isValid()).isFalse();
  }

  @Test
  public void getErrors() {
    var views = List.of(
        PlatformFpsoViewUtil.createView(platformFpso, 1, 1, true),
        PlatformFpsoViewUtil.createView(platformFpsoWithoutSubstructuresRemoved, 2, 1, false)
    );
    var errors = platformsFpsosSummaryService.getErrors(views);
    assertThat(errors.size()).isEqualTo(1);
    assertThat(errors.get(0).getDisplayOrder()).isEqualTo(2);
    assertThat(errors.get(0).getFieldName()).isEqualTo(String.format(PlatformsFpsosSummaryService.ERROR_FIELD_NAME, 2));
    assertThat(errors.get(0).getErrorMessage()).isEqualTo(String.format(PlatformsFpsosSummaryService.ERROR_MESSAGE, 2));
  }

  @Test
  public void getErrors_emptyList() {
    var errors = platformsFpsosSummaryService.getErrors(Collections.emptyList());
    assertThat(errors.size()).isEqualTo(1);
    assertThat(errors.get(0).getDisplayOrder()).isEqualTo(1);
    assertThat(errors.get(0).getFieldName()).isEqualTo(PlatformsFpsosSummaryService.EMPTY_LIST_ERROR);
    assertThat(errors.get(0).getErrorMessage()).isEqualTo(PlatformsFpsosSummaryService.EMPTY_LIST_ERROR);
  }

  private void checkCommonFields(PlatformFpsoView view, PlatformFpso platformFpso) {
    assertThat(view.getTopsideFpsoMass()).isEqualTo(PlatformFpsoViewUtil.getMass(platformFpso.getTopsideFpsoMass()));
    assertThat(view.getTopsideRemovalEarliestYear()).isEqualTo(PlatformFpsoViewUtil.getYearText(platformFpso.getEarliestRemovalYear(), PlatformFpsoViewUtil.EARLIEST_YEAR_TEXT));
    assertThat(view.getTopsideRemovalLatestYear()).isEqualTo(PlatformFpsoViewUtil.getYearText(platformFpso.getLatestRemovalYear(), PlatformFpsoViewUtil.LATEST_YEAR_TEXT));
    assertThat(view.getSubstructuresExpectedToBeRemoved()).isEqualTo(platformFpso.getSubstructuresExpectedToBeRemoved());
    if (platformFpso.getSubstructuresExpectedToBeRemoved()) {
      assertThat(view.getSubstructureRemovalPremise()).isEqualTo(platformFpso.getSubstructureRemovalPremise().getDisplayName());
      assertThat(view.getSubstructureRemovalMass()).isEqualTo(PlatformFpsoViewUtil.getMass(platformFpso.getSubstructureRemovalMass()));
      assertThat(view.getSubstructureRemovalEarliestYear()).isEqualTo(
          PlatformFpsoViewUtil.getYearText(platformFpso.getSubStructureRemovalEarliestYear(), PlatformFpsoViewUtil.EARLIEST_YEAR_TEXT)
      );
      assertThat(view.getSubstructureRemovalLatestYear()).isEqualTo(
          PlatformFpsoViewUtil.getYearText(platformFpso.getSubStructureRemovalLatestYear(), PlatformFpsoViewUtil.LATEST_YEAR_TEXT)
      );
    }
    assertThat(view.getFpsoType()).isEqualTo(platformFpso.getFpsoType());
    assertThat(view.getFpsoDimensions()).isEqualTo(platformFpso.getFpsoDimensions());
    assertThat(view.getEditLink().getLinkText()).isEqualTo(SummaryLinkText.EDIT.getDisplayName());
    assertThat(view.getDeleteLink().getLinkText()).isEqualTo(SummaryLinkText.DELETE.getDisplayName());
  }

}
