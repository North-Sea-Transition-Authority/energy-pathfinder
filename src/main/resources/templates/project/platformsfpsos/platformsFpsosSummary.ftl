<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Platforms and FPSOs" pageHeading="Platforms and FPSOs" breadcrumbs=true>
  <@setupProjectGuidance.minimumRequirementNotMetInset itemRequiredText="platform or FPSO" linkUrl=""/>
  <@fdsAction.link linkText="Add platform or FPSO" linkUrl=springUrl(addPlatformFpsoUrl) linkClass="govuk-button govuk-button--blue"/>
</@defaultPage>