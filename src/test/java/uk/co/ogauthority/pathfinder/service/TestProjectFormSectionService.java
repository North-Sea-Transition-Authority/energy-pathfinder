package uk.co.ogauthority.pathfinder.service;

import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;

@Service
public class TestProjectFormSectionService implements ProjectFormSectionService {

  @Override
  public boolean isComplete(ProjectDetail detail) {
    return false;
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail) {
    return ProjectFormSectionService.super.canShowInTaskList(detail);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    ProjectFormSectionService.super.removeSectionData(projectDetail);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {

  }

  @Override
  public boolean alwaysCopySectionData(ProjectDetail projectDetail) {
    return ProjectFormSectionService.super.alwaysCopySectionData(projectDetail);
  }

  @Override
  public boolean allowSectionDataCleanUp(ProjectDetail projectDetail) {
    return ProjectFormSectionService.super.allowSectionDataCleanUp(projectDetail);
  }
}
