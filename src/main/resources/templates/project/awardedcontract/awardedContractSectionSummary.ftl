<#include '../../layout.ftl'>
<#import '_awardedContractSummary.ftl' as awardedContractSummary>

<@sectionSummaryWrapper.sectionSummaryListWrapper sectionId=sectionId sectionTitle=sectionTitle>
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
</@sectionSummaryWrapper.sectionSummaryListWrapper>
