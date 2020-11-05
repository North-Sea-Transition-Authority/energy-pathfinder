<#include '../../layout.ftl'>
<#import '_awardedContractSummary.ftl' as awardedContractSummary>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <#if awardedContractViews?has_content>
    <#list awardedContractViews as awardedContractView>
      <@awardedContractSummary.awardedContractSummary
        awardedContractView=awardedContractView
        showHeader=true
        showActions=false
        headingSize="h3"
        headingClass="govuk-heading-m"
        showTag=true
      />
    </#list>
  <#else>
    <@emptySectionSummaryInset.emptySectionSummaryInset itemText="awarded contracts"/>
  </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>
