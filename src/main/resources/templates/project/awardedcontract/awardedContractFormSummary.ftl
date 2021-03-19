<#include '../../layout.ftl'>
<#import '_awardedContractSummary.ftl' as awardedContractSummary>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle breadcrumbs=true errorItems=errorList>

  <#if awardedContractViews?has_content>
    <#list awardedContractViews as awardedContractView>
      <@awardedContractSummary.awardedContractSummary
        awardedContractView=awardedContractView
        showActions=true
        showHeader=true
      />
    </#list>
    <#else>
      <@setupProjectGuidance.minimumRequirementNotMetInset itemRequiredText="awarded contracts" linkUrl=springUrl(projectSetupUrl)/>
  </#if>
  <@fdsAction.link
    linkText="Add awarded contract"
    linkUrl=springUrl(addAwardedContractUrl)
    role=true
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