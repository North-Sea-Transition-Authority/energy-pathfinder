<#include '../../layout.ftl'>
<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true>
  <@setupProjectGuidance.minimumRequirementNotMetInset itemRequiredText="well" linkUrl=springUrl(projectSetupUrl)/>
  <@fdsAction.link
    linkText="Add plug abandonment schedule"
    linkUrl=springUrl(addPlugAbandonmentScheduleUrl)
    linkClass="govuk-button govuk-button--blue"
  />
</@defaultPage>