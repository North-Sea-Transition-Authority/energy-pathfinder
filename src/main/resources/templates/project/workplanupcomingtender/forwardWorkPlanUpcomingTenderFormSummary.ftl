<#include '../../layout.ftl'>
<#import '_forwardWorkPlanUpcomingTenderSummary.ftl' as tenderSummary>

<#-- @ftlvariable name="pageName" type="String" -->
<#-- @ftlvariable name="errorSummary" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem>" -->
<#-- @ftlvariable name="tenderViews" type="java.util.List<uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender.ForwardWorkPlanUpcomingTenderView>" -->
<#-- @ftlvariable name="backToTaskListUrl" type="String" -->
<#-- @ftlvariable name="projectTypeDisplayNameLowercase" type="String" -->

<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true errorItems=errorSummary>
  <#list tenderViews as view>
    <@tenderSummary.workPlanUpcomingTenderSummary view=view
      showHeader=true
      showActions=true
    />
  </#list>

  <@fdsForm.htmlForm>

    <#assign hasAddedAllTendersFormBind = "form.hasOtherTendersToAdd"/>

    <@fdsRadio.radioGroup
      path=hasAddedAllTendersFormBind
      labelText="Do you have any other upcoming tenders to add to this ${projectTypeDisplayNameLowercase}?"
    >
      <@fdsRadio.radioYes path=hasAddedAllTendersFormBind/>
      <@fdsRadio.radioNo path=hasAddedAllTendersFormBind/>
    </@fdsRadio.radioGroup>

    <@fdsAction.submitButtons
      primaryButtonText="Continue"
      secondaryLinkText="Back to task list"
      linkSecondaryAction=true
      linkSecondaryActionUrl=springUrl(backToTaskListUrl)
    />

  </@fdsForm.htmlForm>
</@defaultPage>