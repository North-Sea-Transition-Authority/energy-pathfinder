<#include '../../layout.ftl'>
<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true>
  <@setupProjectGuidance.minimumRequirementNotMetInset itemRequiredText="well" linkUrl=springUrl(projectSetupUrl)/>
  <@fdsAction.link
    linkText="Add wells to be decommissioned"
    linkUrl=springUrl(addDecommissionedWellUrl)
    linkClass="govuk-button govuk-button--blue"
  />
</@defaultPage>