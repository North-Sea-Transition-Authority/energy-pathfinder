<#include '../../layout.ftl'>
<#import '_plugAbandonmentScheduleSummary.ftl' as plugAbandonmentScheduleSummary>

<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true errorItems=errorList>
  <#if plugAbandonmentScheduleViews?has_content>
    <#list plugAbandonmentScheduleViews as plugAbandonmentScheduleView>
      <@plugAbandonmentScheduleSummary.plugAbandonmentScheduleSummary
        plugAbandonmentScheduleView=plugAbandonmentScheduleView
        showHeader=true
        showActions=true
      />
    </#list>
    <#else>
      <@setupProjectGuidance.minimumRequirementNotMetInset itemRequiredText="wells to a well decommissioning schedule" linkUrl=springUrl(projectSetupUrl)/>
  </#if>
  <@fdsAction.link
    linkText="Add well decommissioning schedule"
    linkUrl=springUrl(addPlugAbandonmentScheduleUrl)
    linkClass="govuk-button govuk-button--blue"
  />
  <@fdsDetails.summaryDetails summaryTitle="When should I add more than one decommissioning schedule?">
    <p class="govuk-body">It is useful to add multiple well decommissioning schedules in situations where a subset of wells ceases to be operational before others or where there are both platform wells and subsea wells to be decommissioned</p>
  </@fdsDetails.summaryDetails>
  <@fdsForm.htmlForm>
    <@fdsAction.submitButtons
      primaryButtonText="Save and complete"
      secondaryLinkText="Back to task list"
      linkSecondaryAction=true
      linkSecondaryActionUrl=springUrl(backToTaskListUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>
