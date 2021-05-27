<#include '../../../layout.ftl'>

<@defaultPage htmlTitle=pageHeading pageHeading=pageHeading breadcrumbs=true errorItems=errorSummary>
  <@fdsAction.link
    linkText="Add collaboration opportunity"
    linkUrl=springUrl(addCollaborationOpportunityFormUrl)
    linkClass="govuk-button govuk-button--blue"
  />
</@defaultPage>