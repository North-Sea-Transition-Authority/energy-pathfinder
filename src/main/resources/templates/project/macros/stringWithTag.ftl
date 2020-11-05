<#macro stringWithTag stringWithTag>
  <#if stringWithTag.value?has_content>
    <span class="string-with-tag">
      ${stringWithTag.value}
      <#if stringWithTag.tag.displayName?has_content>
        <strong class="govuk-tag string-with-tag__tag">
          ${stringWithTag.tag.displayName}
        </strong>
      </#if>
    </span>
  </#if>
</#macro>
