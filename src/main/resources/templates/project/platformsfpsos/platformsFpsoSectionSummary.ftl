<#include '../../layout.ftl'>
<#import '_platformFpsoSummary.ftl' as platformFpsoSummary>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <#list platformFpsoViews as platformFpsoView>
    <@platformFpsoSummary.platformFpsoSummary
      view=platformFpsoView
      showActions=false
      headingSize="h3"
      headingClass="govuk-heading-m"
      showTag=true
    />
  </#list>
</@sectionSummaryWrapper.sectionSummaryWrapper>
