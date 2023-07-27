<#include '../../../layout.ftl'/>
<#import '../_awardedContractSummaryCommon.ftl' as awardedContractSummary>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
    <#if awardedContractDiffModel?has_content>
        <#list awardedContractDiffModel as awardedContractDiff>

            <@awardedContractSummary.forwardWorkPlanAwardedContractDiffSummary
            awardedContractDiff=awardedContractDiff
            showHeader=true
            showActions=false
            headingSize="h3"
            headingClass="govuk-heading-m"
            />
        </#list>
    <#else>
        <@emptySectionSummaryInset.emptySectionSummaryInset
        itemText="awarded contracts"
        projectTypeDisplayName=projectTypeDisplayNameLowercase
        />
    </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>
