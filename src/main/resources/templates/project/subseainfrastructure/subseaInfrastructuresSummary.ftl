<#include '../../layout.ftl'>
<#import 'subseaInfrastructureSummary.ftl' as subseaInfrastructureSummary>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle breadcrumbs=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>
  <div class="summary-list">
    <#if subseaInfrastructureViews?has_content>
      <#list subseaInfrastructureViews as subseaInfrastructureView>
        <@subseaInfrastructureSummary.subseaInfrastructureSummary subseaInfrastructureView=subseaInfrastructureView />
      </#list>
      <#else>
        <@setupProjectGuidance.minimumRequirementNotMetInset itemRequiredText="subsea infrastructure" linkUrl=""/>
    </#if>
  </div>
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