<#include '../layout.ftl'>

<#-- @ftlvariable name="contacts" type="java.util.List<uk.co.ogauthority.pathfinder.model.enums.contact.ServiceContactDetail>" -->
<#-- @ftlvariable name="opensInNewTab" type="Boolean" -->

<#assign pageTitle = "Contact" />

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  fullWidthColumn=true
  topNavigation=false
  backLink=(!opensInNewTab)!true
  phaseBanner=false
>
  <#list contacts as contact>
    <@serviceContact.serviceContact serviceContact=contact includeHeader=true />
  </#list>
</@defaultPage>
