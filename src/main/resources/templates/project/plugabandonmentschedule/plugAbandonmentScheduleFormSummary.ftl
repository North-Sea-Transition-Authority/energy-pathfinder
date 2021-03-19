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
      <@setupProjectGuidance.minimumRequirementNotMetInset itemRequiredText="wells to a plug and abandonment schedule" linkUrl=springUrl(projectSetupUrl)/>
  </#if>
  <@fdsAction.link
    linkText="Add plug and abandonment schedule"
    linkUrl=springUrl(addPlugAbandonmentScheduleUrl)
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
