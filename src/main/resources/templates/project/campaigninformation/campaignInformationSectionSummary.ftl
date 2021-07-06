<#include '../../layout.ftl'/>

<#-- @ftlvariable name="projectTypeDisplayName" type="String" -->
<#-- @ftlvariable name="sectionId" type="String" -->
<#-- @ftlvariable name="sectionTitle" type="String" -->
<#-- @ftlvariable name="campaignInformationDiffModel" type="java.util.Map<String, Object>" -->
<#-- @ftlvariable name="isProjectIncludedInCampaign" type="Boolean" -->

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <@fdsCheckAnswers.checkAnswers>
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Campaign scope"
      diffedField=campaignInformationDiffModel.CampaignInformationView_scopeDescription
    />
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="${projectTypeDisplayName} already part of a campaign"
      diffedField=campaignInformationDiffModel.CampaignInformationView_isIncludedInCampaign
    />
    <#if isProjectIncludedInCampaign>
      <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="${projectTypeDisplayName}s included in campaign">
        <#list campaignInformationDiffModel.CampaignInformationView_campaignProjects as diffedCampaignProject>
          <div>
            <@differenceChanges.renderDifference
              diffedField=diffedCampaignProject
            />
          </div>
        </#list>
      </@checkAnswers.checkAnswersRowNoActionsWithNested>
    </#if>
  </@fdsCheckAnswers.checkAnswers>
</@sectionSummaryWrapper.sectionSummaryWrapper>
