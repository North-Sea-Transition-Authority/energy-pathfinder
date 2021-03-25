<#include '../../layout.ftl'>
<#import '_awardedContractSummary.ftl' as awardedContractSummary>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <#if awardedContractDiffModel?has_content>
    <#list awardedContractDiffModel as awardedContractDiff>
      <@awardedContractSummary.awardedContractDiffSummary
        awardedContractDiff=awardedContractDiff
        showHeader=true
        showActions=false
        headingSize="h3"
        headingClass="govuk-heading-m"
      />
    </#list>
  <#else>
    <@emptySectionSummaryInset.emptySectionSummaryInset itemText="awarded contracts"/>
  </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>
