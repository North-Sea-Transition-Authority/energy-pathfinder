<#include '../layout.ftl'>

<@defaultPage htmlTitle="Contact" pageHeading="Contact" topNavigation=false>
  <#list contacts as contact>
    <h2 class="govuk-heading-m">${contact.displayName}</h2>
    <#if contact.description?has_content>
      <span class="govuk-hint">${contact.description}</span>
    </#if>
    <ul class="govuk-list">
      <li>${contact.serviceName}</li>
      <#if contact.phoneNumber?has_content>
        <li>${contact.phoneNumber}</li>
      </#if>
      <#if contact.emailAddress?has_content>
        <li>
          <@fdsAction.link
            linkText=contact.emailAddress
            linkUrl="mailto:${contact.emailAddress}"
          />
        </li>
      </#if>
    </ul>
  </#list>
</@defaultPage>
