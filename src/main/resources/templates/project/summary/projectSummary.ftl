<#include '../../layout.ftl'/>

<#macro summary projectSummaryView>
  <@noEscapeHtml.noEscapeHtml html=projectSummaryView.summaryHtml />
  <#nested>
</#macro>
