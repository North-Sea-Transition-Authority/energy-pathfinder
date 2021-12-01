<#include '../../../layout.ftl'/>

<#-- @ftlvariable name="forwardWorkPlanProjectTypeLowercaseDisplayName" type="String" -->
<#-- @ftlvariable name="infrastructureProjectTypeLowercaseDisplayName" type="String" -->
<#-- @ftlvariable name="service" type="uk.co.ogauthority.pathfinder.config.ServiceProperties" -->
<#-- @ftlvariable name="startInfrastructureProjectUrl" type="String" -->

<#macro
  introductionText
  forwardWorkPlanProjectTypeLowercaseDisplayName
  infrastructureProjectTypeLowercaseDisplayName
  startInfrastructureProjectUrl
>
  <p class="govuk-body">
    Maintenance and Operations (M&O) activity accounts for a significant annual expenditure in offshore operations
    with a multitude of contracts being offered or renewed on a regular basis. The ${forwardWorkPlanProjectTypeLowercaseDisplayName}
    facility is specifically designed to provide the supply chain with visibility of M&O contract opportunities (OPEX expenditure).
  </p>
  <p class="govuk-body">
    Capital expenditure (CAPEX) for new and decommissioning ${infrastructureProjectTypeLowercaseDisplayName}s should be provided on an
    <@fdsAction.link
      linkText="${service.serviceName} ${infrastructureProjectTypeLowercaseDisplayName}"
      linkUrl=springUrl(startInfrastructureProjectUrl)
    />
    instead of a ${forwardWorkPlanProjectTypeLowercaseDisplayName}.
  </p>
</#macro>