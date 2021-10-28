<#macro hiddenInput path>
  <@spring.bind path/>
  <#local name=spring.status.expression>
  <#local value=spring.stringStatusValue>
  <input id="${name}" name="${name}" type="hidden" value="${value}">
</#macro>