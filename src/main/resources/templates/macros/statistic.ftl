<#macro statisticContainer>
  <div class="statistic-container">
    <#nested/>
  </div>
</#macro>

<#macro statisticContainerItem headingText>
  <div class="statistic-container__item">
    <h3 class="govuk-heading-m">${headingText}</h3>
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <#nested/>
      </div>
    </div>
  </div>
</#macro>

<#macro statistic prompt value promptClasses="">
  <div class="statistic">
    <div class="statistic__prompt ${promptClasses}">
      ${prompt}
    </div>
    <div class="statistic__value-wrapper">
      <div class="statistic__value">
        ${value}
      </div>
    </div>
    <#nested/>
  </div>
</#macro>