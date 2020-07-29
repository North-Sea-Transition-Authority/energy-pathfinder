<#include 'layout.ftl'>
<@defaultPage htmlTitle="Work area" pageHeading="Work area" topNavigation=true fullWidthColumn=true>
  <#if showStartProject>
      <@fdsAction.link linkText="Create project" linkUrl=springUrl(startProjectUrl) linkClass="govuk-button"/>
  </#if>
</@defaultPage>