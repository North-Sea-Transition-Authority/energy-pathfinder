<#include '../layout.ftl'>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle topNavigation=true twoThirdsColumn=false>
  <ul class="govuk-list category-list">
    <#list teamTypes as teamType, url>
      <li class="govuk-list__item category-list__item">
        <#assign hintIdentifier="team-type-${teamType.identifier}-hint" />
        <@fdsAction.link linkText="${teamType.linkText}" linkClass="govuk-link govuk-link--no-visited-state category-list__link" linkUrl=springUrl(url) ariaDescribedBy=hintIdentifier/>
        <span class="govuk-hint" id="${hintIdentifier}">${teamType.linkHint}</span>
      </li>
    </#list>
  </ul>
</@defaultPage>