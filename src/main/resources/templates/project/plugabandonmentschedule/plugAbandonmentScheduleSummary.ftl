<#include '../../layout.ftl'>
<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true>
  <@setupProjectGuidance.minimumRequirementNotMetInset itemRequiredText="well added to a plug and abandonment schedule" linkUrl=springUrl(projectSetupUrl)/>
  <@fdsAction.link
    linkText="Add plug and abandonment schedule"
    linkUrl=springUrl(addPlugAbandonmentScheduleUrl)
    linkClass="govuk-button govuk-button--blue"
  />
</@defaultPage>