<#include '../../layout.ftl'>
<#import './collaborationOpportunitySummary.ftl' as collaborationOpportunitySummary />

<@defaultPage htmlTitle="Collaboration opportunities" pageHeading="Collaboration opportunities" breadcrumbs=true>
    <#if errorSummary?has_content>
      <@fdsError.errorSummary errorItems=errorSummary />
    </#if>
    <#if opportunityViews?has_content>
      <#list opportunityViews as view>
        <div class="summary-list">
          <#assign errorId = "collaboration-opportunity-" + view.getDisplayOrder()/>
          <#assign opportunityName = "Collaboration opportunity " + view.getDisplayOrder()/>
          <h2 class="govuk-heading-l summary-list__heading" id=${errorId} >${opportunityName}</h2>
          <@collaborationOpportunitySummary.collaborationOpportunitySummary view=view opportunityName=opportunityName showValidationAndActions=true />
        </div>
      </#list>
    <#else>
      <@fdsInsetText.insetText>
        <p>
          Your project requires at least one collaboration opportunity as you advised they would be provided in the 'Set up your project' section.
        </p>
        <p>
          <@fdsAction.link linkText="Change your project set up" linkUrl=""/>
        </p>
      </@fdsInsetText.insetText>
    </#if>
    <@fdsAction.link linkText="Add collaboration opportunity" linkUrl=springUrl(addCollaborationOpportunityUrl) linkClass="govuk-button govuk-button--blue"/>
    <@fdsForm.htmlForm>
        <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryLinkText="Back to task list" linkSecondaryAction=true linkSecondaryActionUrl=springUrl(backToTaskListUrl) />
    </@fdsForm.htmlForm>
</@defaultPage>