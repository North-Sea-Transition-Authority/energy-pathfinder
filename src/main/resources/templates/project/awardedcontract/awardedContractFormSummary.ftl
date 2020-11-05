<#include '../../layout.ftl'>
<#import '_awardedContractSummary.ftl' as awardedContractSummary>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle breadcrumbs=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>
  <div class="summary-list">
    <#if awardedContractViews?has_content>
      <#list awardedContractViews as awardedContractView>
        <@awardedContractSummary.awardedContractSummary awardedContractView=awardedContractView showTag=false />
      </#list>
      <#else>
        <@setupProjectGuidance.minimumRequirementNotMetInset itemRequiredText="awarded contract" linkUrl=""/>
    </#if>
  </div>
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