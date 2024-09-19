<#macro tag tagClasses="" id="">
  <strong class="govuk-tag ${tagClasses}" <#if id?has_content> id=${id}</#if>>
    <#nested/>
  </strong>
</#macro>
