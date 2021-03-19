<#include '../../layout.ftl'>
<#import './_platformFpsoSummary.ftl' as platformFpsoSummary/>

<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true errorItems=errorSummary>
  <#if views?has_content>
    <#list views as view>
      <@platformFpsoSummary.platformFpsoSummary
        view=view
        platformFpsoName=platformFpsoName
        showHeader=true
        showActions=true
      />
    </#list>
  <#else>
    <@setupProjectGuidance.minimumRequirementNotMetInset
      itemRequiredText="platforms or FPSOs"
      linkUrl=springUrl(projectSetupUrl)
    />
  </#if>
  <@fdsAction.link
    linkText="Add platform or FPSO"
    linkUrl=springUrl(addPlatformFpsoUrl)
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