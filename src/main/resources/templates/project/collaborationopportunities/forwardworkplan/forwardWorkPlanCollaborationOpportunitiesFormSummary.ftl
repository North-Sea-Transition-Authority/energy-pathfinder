<#include '../../../layout.ftl'>
<#import '_forwardWorkPlanCollaborationOpportunitySummary.ftl' as collaborationOpportunitySummary />

<#-- @ftlvariable name="pageHeading" type="String" -->
<#-- @ftlvariable name="errorSummary" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem>" -->
<#-- @ftlvariable name="collaborationOpportunityViews" type="java.util.List<uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationOpportunityView>" -->
<#-- @ftlvariable name="backToTaskListUrl" type="String" -->
<#-- @ftlvariable name="projectTypeDisplayNameLowercase" type="String" -->

<@defaultPage htmlTitle=pageHeading pageHeading=pageHeading breadcrumbs=true errorItems=errorSummary>

  <#list collaborationOpportunityViews as view>
    <@collaborationOpportunitySummary.collaborationOpportunitySummary
      view=view
      showHeader=true
      showActions=true
    />
  </#list>

  <@fdsForm.htmlForm>

    <#assign hasAddedAllCollaborationsFormBind = "form.hasOtherCollaborationsToAdd"/>

    <@fdsRadio.radioGroup
      path=hasAddedAllCollaborationsFormBind
      labelText="Do you have any other collaboration opportunities to add to this ${projectTypeDisplayNameLowercase}?"
    >
      <@fdsRadio.radioYes path=hasAddedAllCollaborationsFormBind/>
      <@fdsRadio.radioNo path=hasAddedAllCollaborationsFormBind/>
    </@fdsRadio.radioGroup>

    <@fdsAction.submitButtons
      primaryButtonText="Continue"
      secondaryLinkText="Back to task list"
      linkSecondaryAction=true
      linkSecondaryActionUrl=springUrl(backToTaskListUrl)
    />

  </@fdsForm.htmlForm>
</@defaultPage>