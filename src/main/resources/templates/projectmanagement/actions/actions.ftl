<#include '../../layout.ftl'>

<#list actions as action>
  <@userAction.userAction userAction=action />
</#list>
