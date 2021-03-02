<#include '../../layout.ftl'>
<#import '_collaborationOpportunitySummary.ftl' as collaborationOpportunitySummary />

<@defaultPage htmlTitle="Collaboration opportunities" pageHeading="Collaboration opportunities" breadcrumbs=true errorItems=errorSummary>
  <#if opportunityViews?has_content>
    <#list opportunityViews as view>
      <@collaborationOpportunitySummary.collaborationOpportunitySummary
        view=view
        showHeader=true
        showActions=true
      />
    </#list>
  <#else>
    <@setupProjectGuidance.minimumRequirementNotMetInset itemRequiredText="collaboration opportunity" linkUrl=springUrl(projectSetupUrl)/>
  </#if>
  <@fdsAction.link linkText="Add collaboration opportunity" linkUrl=springUrl(addCollaborationOpportunityUrl) linkClass="govuk-button govuk-button--blue"/>
  <@fdsForm.htmlForm>
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryLinkText="Back to task list" linkSecondaryAction=true linkSecondaryActionUrl=springUrl(backToTaskListUrl) />
  </@fdsForm.htmlForm>
</@defaultPage>