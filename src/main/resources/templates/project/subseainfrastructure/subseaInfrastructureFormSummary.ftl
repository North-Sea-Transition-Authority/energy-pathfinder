<#include '../../layout.ftl'>
<#import '_subseaInfrastructureSummary.ftl' as subseaInfrastructureSummary>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle breadcrumbs=true errorItems=errorList>
  <#if subseaInfrastructureViews?has_content>
    <#list subseaInfrastructureViews as subseaInfrastructureView>
      <@subseaInfrastructureSummary.subseaInfrastructureSummary
        subseaInfrastructureView=subseaInfrastructureView
        showHeader=true
        showActions=true
      />
    </#list>
    <#else>
      <@setupProjectGuidance.minimumRequirementNotMetInset itemRequiredText="subsea infrastructures" linkUrl=springUrl(projectSetupUrl)/>
  </#if>
  <@fdsAction.link
    linkText="Add subsea infrastructure"
    linkUrl=springUrl(addSubseaInfrastructureUrl)
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