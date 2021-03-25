<#function pluralise counter singleText pluralText>
  <#if counter == 1>
    <#return singleText />
    <#else>
      <#return pluralText />
  </#if>
</#function>