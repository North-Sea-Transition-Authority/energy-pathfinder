package uk.co.ogauthority.pathfinder.service.project.platformsfpsos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.model.view.platformfpso.PlatformFpsoView;
import uk.co.ogauthority.pathfinder.model.view.platformfpso.PlatformFpsoViewUtil;
import uk.co.ogauthority.pathfinder.util.summary.SummaryUtil;

@Service
public class PlatformsFpsosSummaryService {
  public static final String ERROR_FIELD_NAME = "platform-fpso-%d";
  public static final String EMPTY_LIST_ERROR = "You must add at least one platform or FPSO";
  public static final String ERROR_MESSAGE = "Platform or FPSO %d is incomplete";

  private final PlatformsFpsosService platformsFpsosService;

  @Autowired
  public PlatformsFpsosSummaryService(PlatformsFpsosService platformsFpsosService) {
    this.platformsFpsosService = platformsFpsosService;
  }

  public List<PlatformFpsoView> getSummaryViews(ProjectDetail detail) {
    return createPlatformFpsoViews(
        platformsFpsosService.getPlatformsFpsosForDetail(detail),
        detail.getProject().getId(),
        ValidationType.NO_VALIDATION
    );
  }

  public List<PlatformFpsoView> getValidatedSummaryViews(ProjectDetail detail) {
    return createPlatformFpsoViews(
        platformsFpsosService.getPlatformsFpsosForDetail(detail),
        detail.getProject().getId(),
        ValidationType.FULL
    );
  }

  public PlatformFpsoView getView(PlatformFpso platformFpso, Integer displayOrder, Integer projectId) {
    return PlatformFpsoViewUtil.createView(platformFpso, displayOrder, projectId);
  }

  public PlatformFpsoView getView(PlatformFpso platformFpso, Integer displayOrder, Integer projectId, Boolean isValid) {
    return PlatformFpsoViewUtil.createView(platformFpso, displayOrder, projectId, isValid);
  }

  public List<ErrorItem> getErrors(List<PlatformFpsoView> views) {
    return SummaryUtil.getErrors(new ArrayList<>(views), EMPTY_LIST_ERROR, ERROR_FIELD_NAME, ERROR_MESSAGE);
  }

  public List<PlatformFpsoView> createPlatformFpsoViews(
      List<PlatformFpso> platformFpsos,
      Integer projectId,
      ValidationType validationType
  ) {

    return IntStream.range(0, platformFpsos.size())
        .mapToObj(index -> {

          var platformFpso = platformFpsos.get(index);
          var displayIndex = index + 1;

          return validationType.equals(ValidationType.NO_VALIDATION)
              ? getView(platformFpso, displayIndex, projectId)
              : getView(
              platformFpso,
              displayIndex,
              projectId,
              platformsFpsosService.isValid(platformFpso, validationType)
          );
        })
        .collect(Collectors.toList());
  }
}
