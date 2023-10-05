<#macro tag tagClasses="" ariaDescribeById="">
  <strong class="govuk-tag ${tagClasses}" <#if ariaDescribeById?has_content> id=${ariaDescribeById}</#if>>
    <#nested/>
  </strong>
</#macro>
