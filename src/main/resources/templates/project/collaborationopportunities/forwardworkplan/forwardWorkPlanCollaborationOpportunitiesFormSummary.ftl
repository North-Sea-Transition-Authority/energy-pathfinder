<#include '../../../layout.ftl'>
<#import '_forwardWorkPlanCollaborationOpportunitySummary.ftl' as collaborationOpportunitySummary />

<@defaultPage htmlTitle=pageHeading pageHeading=pageHeading breadcrumbs=true errorItems=errorSummary>

  <#list collaborationOpportunityViews as view>
    <@collaborationOpportunitySummary.collaborationOpportunitySummary
      view=view
      showHeader=true
      showActions=true
    />
  </#list>

  <@fdsAction.link
    linkText="Add collaboration opportunity"
    linkUrl=springUrl(addCollaborationOpportunityFormUrl)
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