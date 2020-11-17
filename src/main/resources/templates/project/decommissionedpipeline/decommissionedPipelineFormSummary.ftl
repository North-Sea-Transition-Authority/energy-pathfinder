<#include '../../layout.ftl'>
<#import '_decommissionedPipelineSummary.ftl' as decommissionedPipelineSummary>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle breadcrumbs=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>
  <#if decommissionedPipelineViews?has_content>
    <#list decommissionedPipelineViews as decommissionedPipelineView>
      <@decommissionedPipelineSummary.decommissionedPipelineSummary
        decommissionedPipelineView=decommissionedPipelineView
        showHeader=true
        showActions=true
      />
    </#list>
    <#else>
      <@setupProjectGuidance.minimumRequirementNotMetInset itemRequiredText="pipeline" linkUrl=springUrl(projectSetupUrl)/>
  </#if>
  <@fdsAction.link
    linkText="Add pipeline"
    linkUrl=springUrl(addDecommissionedPipelineUrl)
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
