package uk.co.ogauthority.pathfinder.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.annotation.AllowProjectContributorAccess;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.project.projectcontribution.ProjectContributorsCommonService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.testutil.ProjectContributorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

public class ProjectControllerTesterService {

  public static final Set<ProjectPermission> PROJECT_CREATE_PERMISSION_SET = Set.of(
      ProjectPermission.SUBMIT,
      ProjectPermission.EDIT,
      ProjectPermission.PROVIDE_UPDATE,
      ProjectPermission.ARCHIVE
  );

  private final MockMvc mockMvc;

  private final ProjectOperatorService projectOperatorService;

  private final ProjectContributorsCommonService projectContributorsCommonService;

  private final TeamService teamService;

  private final Map<String, String> requestParams = new HashMap<>();

  private HttpMethod requestMethod;

  private ProjectDetail projectDetail;

  private AuthenticatedUserAccount authenticatedUserAccount;

  private Set<ProjectStatus> permittedProjectStatuses;

  private Set<ProjectType> permittedProjectTypes;

  private Set<ProjectPermission> requiredPermissions;

  private boolean allowContributorAccess = false;

  public ProjectControllerTesterService(MockMvc mockMvc,
                                        ProjectOperatorService projectOperatorService,
                                        ProjectContributorsCommonService projectContributorsCommonService,
                                        TeamService teamService) {
    this.projectOperatorService = projectOperatorService;
    this.mockMvc = mockMvc;
    this.projectContributorsCommonService = projectContributorsCommonService;
    this.teamService = teamService;
  }

  public ProjectControllerTesterService withHttpRequestMethod(HttpMethod requestMethod) {
    if (requestMethod != HttpMethod.GET && requestMethod != HttpMethod.POST) {
      throw new IllegalArgumentException(String.format("Only GET and POST request types are supported. Actual: %s", requestMethod));
    }
    this.requestMethod = requestMethod;
    return this;
  }

  public ProjectControllerTesterService withProjectDetail(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
    return this;
  }

  public ProjectControllerTesterService withUser(AuthenticatedUserAccount authenticatedUserAccount) {
    this.authenticatedUserAccount = authenticatedUserAccount;
    return this;
  }

  public ProjectControllerTesterService withRequestParam(String key, String value) {
    requestParams.put(key, value);
    return this;
  }

  public ProjectControllerTesterService withRequestParams(Map<String, String> requestParams) {
    this.requestParams.putAll(requestParams);
    return this;
  }

  public ProjectControllerTesterService withPermittedProjectStatuses(Set<ProjectStatus> projectStatuses) {
    this.permittedProjectStatuses = projectStatuses;
    return this;
  }

  public ProjectControllerTesterService withPermittedProjectTypes(Set<ProjectType> projectTypes) {
    this.permittedProjectTypes = projectTypes;
    return this;
  }

  public ProjectControllerTesterService withRequiredProjectPermissions(Set<ProjectPermission> projectPermissions) {
    this.requiredPermissions = projectPermissions;
    return this;
  }

  public ProjectControllerTesterService withProjectContributorAccess() {
    allowContributorAccess = true;
    return this;
  }

  public void smokeTestProjectContextAnnotationsForControllerEndpoint(
      Object controllerMethodToCheck,
      ResultMatcher successMatcher,
      ResultMatcher failedMatcher
  ) {

    final var originalProjectType = projectDetail.getProjectType();
    final var originalProjectStatus = projectDetail.getStatus();

    smokeTestProjectStatus(
        controllerMethodToCheck,
        permittedProjectStatuses,
        successMatcher,
        failedMatcher
    );

    resetProjectDetail(originalProjectType, originalProjectStatus);

    smokeTestProjectType(
        controllerMethodToCheck,
        permittedProjectTypes,
        successMatcher,
        failedMatcher
    );

    resetProjectDetail(originalProjectType, originalProjectStatus);

    smokeTestProjectPermissions(
        controllerMethodToCheck,
        requiredPermissions,
        successMatcher,
        failedMatcher
    );

    resetProjectDetail(originalProjectType, originalProjectStatus);

    smokeTestProjectContributorAccess(
        controllerMethodToCheck,
        successMatcher,
        failedMatcher
    );

    resetProjectDetail(originalProjectType, originalProjectStatus);
  }

  private void resetProjectDetail(ProjectType projectType, ProjectStatus projectStatus) {
    this.projectDetail.setProjectType(projectType);
    this.projectDetail.setStatus(projectStatus);
  }

  public void smokeTestProjectStatus(
      Object controllerMethodToCheck,
      Set<ProjectStatus> permittedProjectStatuses,
      ResultMatcher matcherWhenPermittedStatus,
      ResultMatcher matcherWhenNonPermittedStatus
  ) {

    when(projectOperatorService.isUserInProjectTeam(projectDetail, authenticatedUserAccount)).thenReturn(true);

    Arrays.asList(ProjectStatus.values()).forEach(projectStatus -> {

      projectDetail.setStatus(projectStatus);

      try {
        final var response = makeRequestWithUser(
            controllerMethodToCheck,
            authenticatedUserAccount
        );

        if (permittedProjectStatuses.contains(projectStatus)) {
          response.andExpect(matcherWhenPermittedStatus);
        } else {
          response.andExpect(matcherWhenNonPermittedStatus);
        }
      } catch (AssertionError | Exception ex) {
        throw new AssertionError(
            String.format("Failed at project status: %s \n %s", projectStatus, ex.getMessage()),
            ex
        );
      }
    });
  }

  public void smokeTestProjectType(
      Object controllerMethodToCheck,
      Set<ProjectType> permittedProjectTypes,
      ResultMatcher matcherWhenPermittedType,
      ResultMatcher matcherWhenNonPermittedType
  ) {

    when(projectOperatorService.isUserInProjectTeam(projectDetail, authenticatedUserAccount)).thenReturn(true);

    Arrays.asList(ProjectType.values()).forEach(projectType -> {

      projectDetail.setProjectType(projectType);

      try {
        final var response = makeRequestWithUser(
            controllerMethodToCheck,
            authenticatedUserAccount
        );

        if (permittedProjectTypes.contains(projectType)) {
          response.andExpect(matcherWhenPermittedType);
        } else {
          response.andExpect(matcherWhenNonPermittedType);
        }
      } catch (AssertionError | Exception ex) {
        throw new AssertionError(
            String.format("Failed at project type: %s \n %s", projectType, ex.getMessage()),
            ex
        );
      }
    });
  }

  public void smokeTestProjectPermissions(
      Object controllerMethodToCheck,
      Set<ProjectPermission> requiredPermissions,
      ResultMatcher matcherWhenRequiredPermission,
      ResultMatcher matcherWhenNotRequiredPermission
  ) {

    Arrays.asList(ProjectPermission.values()).forEach(projectPermission -> {

      final var authenticatedUserWithPrivilege = UserTestingUtil.getAuthenticatedUserAccount(
          projectPermission.getUserPrivileges()
      );

      when(projectOperatorService.isUserInProjectTeam(projectDetail, authenticatedUserWithPrivilege)).thenReturn(true);

      try {
        final var response = makeRequestWithUser(
            controllerMethodToCheck,
            authenticatedUserWithPrivilege
        );

        if (requiredPermissions.contains(projectPermission)) {
          response.andExpect(matcherWhenRequiredPermission);
        } else {
          response.andExpect(matcherWhenNotRequiredPermission);
        }
      } catch (AssertionError | Exception ex) {
        throw new AssertionError(
            String.format("Failed at project permission: %s \n %s", projectPermission, ex.getMessage()),
            ex
        );
      }
    });

    makeRequestWithUnauthenticatedUser(controllerMethodToCheck);
  }

  public void smokeTestProjectContributorAccess(
      Object controllerMethodToCheck,
      ResultMatcher matcherWhenRequiredPermission,
      ResultMatcher matcherWhenNotRequiredPermission
  ) {
    when(projectOperatorService.isUserInProjectTeam(projectDetail, authenticatedUserAccount))
        .thenReturn(false);

    var hasAllowContributorAccess = hasProjectContributorAnnotation(
        (MvcUriComponentsBuilder.MethodInvocationInfo) controllerMethodToCheck
    );

    //If the controller has @AllowProjectContributorAccess the projectControllerTesterService must have allowContributorAccess = true
    if (hasAllowContributorAccess && !allowContributorAccess) {
      throw new IllegalStateException("Controller contains @AllowProjectContributorAccess but smoke test was not properly" +
          " setup to test it. Fix: Use withProjectContributorAccess() when setting your projectControllerTesterService");
    }

    if (allowContributorAccess) {
      var organisationGroup = TeamTestingUtil.generateOrganisationGroup(100, "my Org", "org");
      when(projectContributorsCommonService.getProjectContributorsForDetail(projectDetail)).thenReturn(List.of(
          ProjectContributorTestUtil.contributorWithGroupOrgId(projectDetail, organisationGroup.getOrgGrpId())
      ));

      when(teamService.getOrganisationTeamsPersonIsMemberOf(authenticatedUserAccount.getLinkedPerson())).thenReturn(
          List.of(TeamTestingUtil.getOrganisationTeam(organisationGroup)));

      try {
        final var response = makeRequestWithUser(
            controllerMethodToCheck,
            authenticatedUserAccount
        );
        response.andExpect(matcherWhenRequiredPermission);
      } catch (AssertionError | Exception ex) {
        throw new AssertionError(
            String.format("Failed to access endpoint as a contributor %s", ex.getMessage()),
            ex
        );
      }
    } else {
      try {
        final var response = makeRequestWithUser(
            controllerMethodToCheck,
            authenticatedUserAccount
        );
        response.andExpect(matcherWhenNotRequiredPermission);
      } catch (AssertionError | Exception ex) {
        throw new AssertionError(
            String.format("Failed, access was granted without being a contributor %s", ex.getMessage()),
            ex
        );
      }
    }
  }

  public void makeRequestAndAssertMatcher(
      Object controllerMethodToCheck,
      ResultMatcher expectedMatcher
  ) {
    try {
      final var response = makeRequestWithUser(controllerMethodToCheck, authenticatedUserAccount);
      response.andExpect(expectedMatcher);
    } catch (AssertionError | Exception ex) {
      throw new AssertionError(
          ex.getMessage(),
          ex
      );
    }
  }

  private MultiValueMap<String, String> generateRequestParams() {
    MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
    requestParams.forEach(paramMap::add);
    return paramMap;
  }

  private ResultActions makeRequestWithUser(Object methodCall,
                                            AuthenticatedUserAccount authenticatedUser) throws Exception {

    final var requestParams = generateRequestParams();

    if (this.requestMethod == HttpMethod.GET) {
      return mockMvc.perform(
          get(ReverseRouter.route(methodCall))
              .params(requestParams)
              .with(authenticatedUserAndSession(authenticatedUser))
      );
    } else {
      return mockMvc.perform(
          post(ReverseRouter.route(methodCall))
              .params(requestParams)
              .with(csrf())
              .with(authenticatedUserAndSession(authenticatedUser))
      );
    }
  }

  private void makeRequestWithUnauthenticatedUser(Object controllerMethodToCheck) {

    try {

      final var requestParams = generateRequestParams();

      if (this.requestMethod == HttpMethod.GET) {
        mockMvc.perform(
            get(ReverseRouter.route(controllerMethodToCheck))
                .params(requestParams)
        ).andExpect(status().is3xxRedirection());
      } else {
        mockMvc.perform(
            post(ReverseRouter.route(controllerMethodToCheck))
                .params(requestParams)
                .with(csrf())
        ).andExpect(status().is3xxRedirection());
      }
    } catch (AssertionError | Exception ex) {
      throw new AssertionError(String.format("Unauthenticated check expected 3xx redirect\n %s", ex.getMessage()), ex);
    }
  }

  private boolean hasProjectContributorAnnotation(MvcUriComponentsBuilder.MethodInvocationInfo controllerMethodToCheck) {
    return controllerMethodToCheck.getControllerType().isAnnotationPresent(AllowProjectContributorAccess.class)
        || controllerMethodToCheck.getControllerMethod().isAnnotationPresent(AllowProjectContributorAccess.class);
  }
}
