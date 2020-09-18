<#include '../../layout.ftl'>
<#import 'awardedContractSummary.ftl' as awardedContractSummary>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle breadcrumbs=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>
  <div class="summary-list">
    <#if awardedContractViews?has_content>
      <#list awardedContractViews as awardedContractView>
        <@awardedContractSummary.awardedContractSummary awardedContractView=awardedContractView />
      </#list>
      <#else>
        <@fdsInsetText.insetText>
          <p>
            Your project requires at least one awarded contract as you advised they would be provided in the
            'Set up your project' section.
          </p>
          <p>
            <@fdsAction.link linkText="Change your project set up" linkUrl=""/>
          </p>
        </@fdsInsetText.insetText>
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