package uk.co.ogauthority.pathfinder.service.project.awardedcontract;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.AwardedContractViewCommon;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.util.summary.SummaryUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

public abstract class AwardedContractSummaryService {

  public static final String ERROR_FIELD_NAME = "awarded-contract-%d";
  public static final String ERROR_MESSAGE = "Awarded contract %d is incomplete";
  public static final String EMPTY_LIST_ERROR = "You must add at least one awarded contract";

  protected final ProjectSectionItemOwnershipService projectSectionItemOwnershipService;
  protected final PortalOrganisationAccessor portalOrganisationAccessor;

  @Autowired
  public AwardedContractSummaryService(ProjectSectionItemOwnershipService projectSectionItemOwnershipService,
                                       PortalOrganisationAccessor portalOrganisationAccessor) {
    this.projectSectionItemOwnershipService = projectSectionItemOwnershipService;
    this.portalOrganisationAccessor = portalOrganisationAccessor;
  }

  public abstract List<? extends AwardedContractViewCommon> getAwardedContractViews(ProjectDetail projectDetail);

  public abstract List<? extends AwardedContractViewCommon> getValidatedAwardedContractViews(ProjectDetail projectDetail);

  public List<ErrorItem> getAwardedContractViewErrors(List<? extends AwardedContractViewCommon> awardedContractViews) {
    return SummaryUtil.getErrors(new ArrayList<>(awardedContractViews), EMPTY_LIST_ERROR, ERROR_FIELD_NAME, ERROR_MESSAGE);
  }

  public ValidationResult validateViews(List<? extends AwardedContractViewCommon> views) {
    return SummaryUtil.validateViews(new ArrayList<>(views));
  }

}
