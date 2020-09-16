<#include '../../layout.ftl'>
<#import './upcomingTenderSummary.ftl' as tenderSummary>

<@defaultPage htmlTitle="Upcoming tenders" pageHeading="Upcoming tenders" breadcrumbs=true>

  <#if errorSummary?has_content>
    <@fdsError.errorSummary errorItems=errorSummary />
  </#if>
  <#if tenderViews?has_content>
    <#list tenderViews as view>
      <div class="summary-list">
          <#assign errorId = "upcoming-tender-" + view.getDisplayOrder()/>
          <#assign tenderName = "Upcoming tender " + view.getDisplayOrder()/>
        <h2 class="govuk-heading-l summary-list__heading" id=${errorId} >${tenderName}</h2>
          <@tenderSummary.upcomingTenderSummary view=view tenderName=tenderName showValidationAndActions=true />
      </div>
    </#list>
  <#else>
    <@fdsInsetText.insetText>
      <p>
        Your project requires at least one upcoming tender as you advised they would be provided in the 'Set up your project' section.
      </p>
      <p>
        <@fdsAction.link linkText="Change your project set up" linkUrl=""/>
      </p>
    </@fdsInsetText.insetText>
  </#if>

  <@fdsAction.link linkText="Add upcoming tender" linkUrl=springUrl(addTenderUrl) linkClass="govuk-button govuk-button--blue"/>

  <@fdsForm.htmlForm>
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryLinkText="Back to task list" linkSecondaryAction=true linkSecondaryActionUrl=springUrl(backToTaskListUrl) />
  </@fdsForm.htmlForm>
</@defaultPage>