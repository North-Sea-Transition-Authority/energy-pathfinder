<#include '../../layout.ftl'>
<#import '_integratedRigSummary.ftl' as integratedRigSummary>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle breadcrumbs=true errorItems=errorList>
  <#if integratedRigViews?has_content>
    <#list integratedRigViews as integratedRigView>
      <@integratedRigSummary.integratedRigSummary
        integratedRigView=integratedRigView
        showHeader=true
        showActions=true
      />
    </#list>
    <#else>
      <@setupProjectGuidance.minimumRequirementNotMetInset itemRequiredText="integrated rig" linkUrl=springUrl(projectSetupUrl)/>
  </#if>
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