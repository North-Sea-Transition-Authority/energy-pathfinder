<#include '../../layout.ftl'>
<#import '_subseaInfrastructureSummary.ftl' as subseaInfrastructureSummary>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <#if subseaInfrastructureViews?has_content>
    <#list subseaInfrastructureViews as subseaInfrastructureView>
      <@subseaInfrastructureSummary.subseaInfrastructureSummary
        subseaInfrastructureView=subseaInfrastructureView
        showHeader=true
        showActions=false
        headingSize="h3"
        headingClass="govuk-heading-m"
        showTag=true
      />
    </#list>
  <#else>
    <@emptySectionSummaryInset.emptySectionSummaryInset itemText="subsea infrastructures"/>
  </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>
