<#include '../../layout.ftl'>
<#import './_platformFpsoSummary.ftl' as platformFpsoSummary/>
<#import './_terminology.ftl' as terminology>

<#assign platformLowerCase = terminology.terminology['platformLowerCase'] />
<#assign floatingUnitLowerCase = terminology.terminology['floatingUnitLowerCase'] />

<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true errorItems=errorSummary>
  <#if views?has_content>
    <#list views as view>
      <@platformFpsoSummary.platformFpsoSummary
        view=view
        showHeader=true
        showActions=true
      />
    </#list>
  <#else>
    <@setupProjectGuidance.minimumRequirementNotMetInset
      itemRequiredText="${platformLowerCase}s or ${floatingUnitLowerCase}s"
      linkUrl=springUrl(projectSetupUrl)
    />
  </#if>
  <@fdsAction.link
    linkText="Add ${platformLowerCase} or ${floatingUnitLowerCase}"
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