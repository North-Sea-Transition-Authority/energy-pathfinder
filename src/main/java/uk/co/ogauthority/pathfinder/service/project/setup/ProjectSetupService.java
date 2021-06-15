package uk.co.ogauthority.pathfinder.service.project.setup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.tasks.ProjectTaskListSetup;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions.TaskListSectionAnswer;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions.TaskListSectionQuestion;
import uk.co.ogauthority.pathfinder.model.form.project.setup.ProjectSetupForm;
import uk.co.ogauthority.pathfinder.model.form.project.setup.ProjectSetupFormValidationHint;
import uk.co.ogauthority.pathfinder.model.form.project.setup.ProjectSetupFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.tasks.ProjectTaskListSetupRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

/**
 * The service used in the 'Set up your project' page to decide what optional features of a project should be included.
 */
@Service
public class ProjectSetupService implements ProjectFormSectionService {

  public static final String MODEL_AND_VIEW_PATH = "project/setup/projectSetup";

  private final ProjectTaskListSetupRepository projectTaskListSetupRepository;
  private final ProjectInformationService projectInformationService;
  private final ProjectSetupFormValidator projectSetupFormValidator;
  private final ValidationService validationService;
  private final EntityDuplicationService entityDuplicationService;

  @Autowired
  public ProjectSetupService(ProjectTaskListSetupRepository projectTaskListSetupRepository,
                             ProjectInformationService projectInformationService,
                             ProjectSetupFormValidator projectSetupFormValidator,
                             ValidationService validationService,
                             EntityDuplicationService entityDuplicationService) {
    this.projectTaskListSetupRepository = projectTaskListSetupRepository;
    this.projectInformationService = projectInformationService;
    this.projectSetupFormValidator = projectSetupFormValidator;
    this.validationService = validationService;
    this.entityDuplicationService = entityDuplicationService;
  }

  public List<TaskListSectionQuestion> getSectionQuestionsForProjectDetail(ProjectDetail detail) {
    return projectInformationService.isDecomRelated(detail)
        ? TaskListSectionQuestion.getAllValues()
        : TaskListSectionQuestion.getNonDecommissioningRelatedValues();
  }

  public ModelAndView getProjectSetupModelAndView(ProjectDetail detail, ProjectSetupForm form) {
    return new ModelAndView(MODEL_AND_VIEW_PATH)
        .addObject("sections", getSectionQuestionsForProjectDetail(detail))
        .addObject("form", form);
  }

  @Transactional
  public ProjectTaskListSetup createOrUpdateProjectTaskListSetup(ProjectDetail detail, ProjectSetupForm form) {
    var taskListSetup = getProjectTaskListSetup(detail).orElse(
        new ProjectTaskListSetup(detail)
    );
    taskListSetup.setTaskListAnswers(getTaskListSectionAnswersFromForm(form));
    taskListSetup.setTaskListSections(getTaskListSectionQuestionsFromForm(form));
    return projectTaskListSetupRepository.save(taskListSetup);
  }

  private ProjectTaskListSetup getProjectTaskListSetupOrError(ProjectDetail projectDetail) {
    return getProjectTaskListSetup(projectDetail).orElseThrow(
        () -> new PathfinderEntityNotFoundException(
            String.format("Could not find ProjectTaskListSetup for ProjectDetail with ID %d", projectDetail.getId())
        )
    );
  }

  public Optional<ProjectTaskListSetup> getProjectTaskListSetup(ProjectDetail detail) {
    return projectTaskListSetupRepository.findByProjectDetail(detail);
  }

  public ProjectSetupForm getForm(ProjectDetail detail) {
    return projectTaskListSetupRepository.findByProjectDetail(detail)
        .map(setup -> {
          var form = new ProjectSetupForm();
          setFormFieldsFromProjectTaskListSetup(setup, form);
          return form;
        })
        .orElse(new ProjectSetupForm());
  }

  private void setFormFieldsFromProjectTaskListSetup(ProjectTaskListSetup taskListSetup, ProjectSetupForm form) {
    //stream list of TaskListSectionQuestionAnswers and match the first which corresponds to the form field
    //Set that as the form field value
    form.setUpcomingTendersIncluded(
        getAnswerForQuestion(taskListSetup.getTaskListAnswers(), TaskListSectionQuestion.UPCOMING_TENDERS)
    );

    form.setAwardedContractsIncluded(
        getAnswerForQuestion(taskListSetup.getTaskListAnswers(), TaskListSectionQuestion.AWARDED_CONTRACTS)
    );

    form.setCollaborationOpportunitiesIncluded(
        getAnswerForQuestion(taskListSetup.getTaskListAnswers(), TaskListSectionQuestion.COLLABORATION_OPPORTUNITIES)
    );

    form.setCampaignInformationIncluded(
        getAnswerForQuestion(taskListSetup.getTaskListAnswers(), TaskListSectionQuestion.CAMPAIGN_INFORMATION)
    );

    form.setWellsIncluded(
        getAnswerForQuestion(taskListSetup.getTaskListAnswers(), TaskListSectionQuestion.WELLS)
    );

    form.setPlatformsFpsosIncluded(
        getAnswerForQuestion(taskListSetup.getTaskListAnswers(), TaskListSectionQuestion.PLATFORM_FPSO)
    );

    // Subsea infrastructure disabled: PAT-495
    // form.setSubseaInfrastructureIncluded(
    //     getAnswerForQuestion(taskListSetup.getTaskListAnswers(), TaskListSectionQuestion.SUBSEA_INFRASTRUCTURE)
    // );

    form.setIntegratedRigsIncluded(
        getAnswerForQuestion(taskListSetup.getTaskListAnswers(), TaskListSectionQuestion.INTEGRATED_RIGS)
    );

    // Pipelines disabled: PAT-457
    // form.setPipelinesIncluded(
    //     getAnswerForQuestion(taskListSetup.getTaskListAnswers(), TaskListSectionQuestion.PIPELINES)
    // );
  }

  private TaskListSectionAnswer getAnswerForQuestion(List<TaskListSectionAnswer> answers, TaskListSectionQuestion question) {
    //stream list of TaskListSectionQuestionAnswers and match the first which corresponds to the form field
    return answers.stream()
        .filter(a -> containsYesOrNoAnswerForQuestion(question, a))
        .findFirst().orElse(null);
  }

  private boolean containsYesOrNoAnswerForQuestion(TaskListSectionQuestion question, TaskListSectionAnswer answer) {
    return question.getNoAnswer().equals(answer) || question.getYesAnswer().equals(answer);
  }

  public List<TaskListSectionQuestion> getTaskListSectionQuestionsFromForm(ProjectSetupForm form) {
    var taskListSectionQuestionAnswers = getTaskListSectionAnswersFromForm(form);

    return TaskListSectionQuestion.getAllValues().stream()
        .filter(tlq -> taskListSectionQuestionAnswers.contains(tlq.getYesAnswer()))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  /**
   * Get the fields from the form and map the set values to a {@link TaskListSectionAnswer }.
   * @param form ProjectSetupForm
   * @return The list of TaskListSectionAnswer values set in the form
   */
  private List<TaskListSectionAnswer> getTaskListSectionAnswersFromForm(ProjectSetupForm form) {
    return Arrays.stream(form.getClass().getDeclaredFields())
        .map(f -> {
          try {
            f.setAccessible(true);
            return (TaskListSectionAnswer) f.get(form);
          } catch (IllegalAccessException | NullPointerException e) {
            return null;
          }
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  /**
   * Validate the ProjectSetupForm, calls custom validator first.
   */
  public BindingResult validate(ProjectSetupForm form,
                                BindingResult bindingResult,
                                ValidationType validationType,
                                ProjectDetail detail) {
    var hint = new ProjectSetupFormValidationHint(projectInformationService.isDecomRelated(detail), validationType);
    projectSetupFormValidator.validate(form, bindingResult, hint);
    return validationService.validate(form, bindingResult, validationType);
  }

  /**
   * Removes any decommissioning related sections and their answers if the project is no longer decommissioning related.
   * @param detail project detail to remove the answers for.
   */
  @Transactional
  public void removeDecomSelectionsIfPresent(ProjectDetail detail) {
    if (!projectInformationService.isDecomRelated(detail)) {
      getProjectTaskListSetup(detail).ifPresent(tls -> {
        var sections = tls.getTaskListSections();
        var answers = tls.getTaskListAnswers();

        TaskListSectionQuestion.getAllValues().forEach(s -> {
          if (s.isDecommissioningRelated()) {
            answers.remove(s.getYesAnswer());
            answers.remove(s.getNoAnswer());
          }
        });
        sections.removeIf(TaskListSectionQuestion::isDecommissioningRelated);

        tls.setTaskListAnswers(answers);
        tls.setTaskListSections(sections);
        projectTaskListSetupRepository.save(tls);
      });
    }
  }

  public boolean taskValidAndSelectedForProjectDetail(ProjectDetail detail, ProjectTask task) {

    final var taskValidForProjectType = (detail.getProjectType() != null)
        && task.getRelatedProjectTypes().contains(detail.getProjectType());

    final var taskSelectedInProjectSetup = projectTaskListSetupRepository.findByProjectDetail(detail)
        .map(setup -> setup.getTaskListSections().stream().anyMatch(q -> q.getProjectTask().equals(task)))
        .orElse(false);

    return taskValidForProjectType && taskSelectedInProjectSetup;
  }

  boolean isDecomRelated(ProjectDetail detail) {
    return projectInformationService.isDecomRelated(detail);
  }

  @Override
  public boolean isComplete(ProjectDetail details) {
    var form = getForm(details);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, ValidationType.FULL, details);
    return !bindingResult.hasErrors();
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    projectTaskListSetupRepository.deleteByProjectDetail(projectDetail);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {
    entityDuplicationService.duplicateEntityAndSetNewParent(
        getProjectTaskListSetupOrError(fromDetail),
        toDetail,
        ProjectTaskListSetup.class
    );
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail) {
    return ProjectService.isInfrastructureProject(detail);
  }

}
