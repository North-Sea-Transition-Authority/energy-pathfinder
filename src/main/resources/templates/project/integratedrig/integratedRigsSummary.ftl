<#include '../../layout.ftl'>
<#import 'integratedRigSummary.ftl' as integratedRigSummary>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle breadcrumbs=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>
  <div class="summary-list">
    <#if integratedRigViews?has_content>
      <#list integratedRigViews as integratedRigView>
        <@integratedRigSummary.integratedRigSummary integratedRigView=integratedRigView />
      </#list>
      <#else>
        <@setupProjectGuidance.minimumRequirementNotMetInset itemRequiredText="integrated rig" linkUrl=""/>
    </#if>
  </div>
  <@fdsAction.link
    linkText="Add integrated rig"
    linkUrl=springUrl(addIntegratedRigUrl)
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