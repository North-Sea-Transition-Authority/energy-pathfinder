<#include '../../layout.ftl'>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle breadcrumbs=true>
  <@setupProjectGuidance.minimumRequirementNotMetInset itemRequiredText="subsea infrastructure" linkUrl=""/>
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