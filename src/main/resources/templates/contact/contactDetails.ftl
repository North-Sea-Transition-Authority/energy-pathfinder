<#include '../layout.ftl'>

<@defaultPage htmlTitle="Contact" pageHeading="Contact" topNavigation=false backLink=true>
  <#list contacts as contact>
    <@serviceContact.serviceContact serviceContact=contact includeHeader=true />
  </#list>
</@defaultPage>
