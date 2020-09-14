<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Awarded contracts" pageHeading="Awarded contracts" breadcrumbs=true>
  <@fdsAction.link
    linkText="Add awarded contract"
    linkUrl=springUrl(addAwardedContractUrl)
    role=true
    linkClass="govuk-button govuk-button--blue"
  />
</@defaultPage>