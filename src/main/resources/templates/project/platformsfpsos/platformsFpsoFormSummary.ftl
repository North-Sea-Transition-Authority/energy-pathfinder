<#include '../../layout.ftl'>
<#import './_platformFpsoSummary.ftl' as platformFpsoSummary/>

<@defaultPage htmlTitle="Platforms and FPSOs" pageHeading="Platforms and FPSOs" breadcrumbs=true>
    <#if errorSummary?has_content>
        <@fdsError.errorSummary errorItems=errorSummary />
    </#if>
    <#if views?has_content>
      <#list views as view>
        <div class="summary-list">
          <@platformFpsoSummary.platformFpsoSummary
            view=view
            platformFpsoName=platformFpsoName
            showHeader=true
            showActions=true
          />
        </div>
      </#list>
    <#else>
      <@setupProjectGuidance.minimumRequirementNotMetInset itemRequiredText="platform or FPSO" linkUrl=""/>
    </#if>
  <@fdsAction.link linkText="Add platform or FPSO" linkUrl=springUrl(addPlatformFpsoUrl) linkClass="govuk-button govuk-button--blue"/>
  <@fdsForm.htmlForm>
      <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryLinkText="Back to task list" linkSecondaryAction=true linkSecondaryActionUrl=springUrl(backToTaskListUrl) />
  </@fdsForm.htmlForm>
</@defaultPage>