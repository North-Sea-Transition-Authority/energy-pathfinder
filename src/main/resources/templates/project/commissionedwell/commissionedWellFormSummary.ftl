<#include '../../layout.ftl'>
<#import '_commissionedWellScheduleSummary.ftl' as commissionedWellScheduleSummary>

<#-- @ftlvariable name="pageName" type="String" -->
<#-- @ftlvariable name="projectSetupUrl" type="String" -->
<#-- @ftlvariable name="addCommissionedWellUrl" type="String" -->
<#-- @ftlvariable name="commissionedWellScheduleViews" type="java.util.List<uk.co.ogauthority.pathfinder.model.view.commissionedwell.CommissionedWellScheduleView>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem>" -->
<#-- @ftlvariable name="backToTaskListUrl" type="String" -->

<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true errorItems=errorList>

  <#if commissionedWellScheduleViews?has_content>
    <#list commissionedWellScheduleViews as commissionedWellScheduleView>
      <@commissionedWellScheduleSummary.commissionedWellScheduleSummary
        commissionedWellScheduleView=commissionedWellScheduleView
        showHeader=true
        showActions=true
      />
    </#list>
  <#else>
    <@setupProjectGuidance.minimumRequirementNotMetInset
      itemRequiredText="wells to commission"
      linkUrl=springUrl(projectSetupUrl)
    />
  </#if>

  <@fdsAction.link
    linkText="Add well commissioning schedule"
    linkUrl=springUrl(addCommissionedWellUrl)
    linkClass="govuk-button govuk-button--blue"
  />
  <@fdsForm.htmlForm>
    <@fdsAction.submitButtons
      primaryButtonText="Save and complete"
      secondaryLinkText="Back to task list"
      linkSecondaryAction=true
      linkSecondaryActionUrl=springUrl(backToTaskListUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>