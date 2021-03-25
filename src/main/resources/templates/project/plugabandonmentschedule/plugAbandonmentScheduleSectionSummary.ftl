<#include '../../layout.ftl'>
<#import '_plugAbandonmentScheduleSummary.ftl' as plugAbandonmentScheduleSummary>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <#if plugAbandonmentScheduleDiffModel?has_content>
    <#list plugAbandonmentScheduleDiffModel as plugAbandonmentScheduleDiff>
      <@plugAbandonmentScheduleSummary.plugAbandonmentScheduleDiffSummary
        plugAbandonmentScheduleDiff=plugAbandonmentScheduleDiff
        showHeader=true
        showActions=false
        headingSize="h3"
        headingClass="govuk-heading-m"
      />
    </#list>
  <#else>
    <@emptySectionSummaryInset.emptySectionSummaryInset itemText="well decommissioning schedules"/>
  </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>
