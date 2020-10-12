<#include '../../layout.ftl'>
<#import './_platformFpsoSummary.ftl' as platformFpsoSummary/>

<@defaultPage htmlTitle="Platforms and FPSOs" pageHeading="Platforms and FPSOs" breadcrumbs=true>
    <#if errorSummary?has_content>
        <@fdsError.errorSummary errorItems=errorSummary />
    </#if>
    <#if views?has_content>
      <#list views as view>
        <div class="summary-list">
          <#assign errorId = "platform-fpso-" + view.getDisplayOrder()/>
          <#assign platformFpsoName = "Platform FPSO " + view.getDisplayOrder()/>
          <h2 class="govuk-heading-l summary-list__heading" id=${errorId} >${platformFpsoName}</h2>
          <@platformFpsoSummary.platformFpsoSummary view=view platformFpsoName=platformFpsoName showValidationAndActions=true />
        </div>
      </#list>
    <#else>
      <@setupProjectGuidance.minimumRequirementNotMetInset itemRequiredText="platform or FPSO" linkUrl=""/>
    </#if>
  <@fdsAction.link linkText="Add platform or FPSO" linkUrl=springUrl(addPlatformFpsoUrl) linkClass="govuk-button govuk-button--blue"/>
</@defaultPage>