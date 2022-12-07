<#include '../../layout.ftl'/>

<#-- @ftlvariable name="sectionId" type="String" -->
<#-- @ftlvariable name="sectionTitle" type="String" -->
<#-- @ftlvariable name="projectContributorDiffModel" type="java.util.Map<String, Object>" -->
<#-- @ftlvariable name="showContributorsList" type="Boolean" -->
<#-- @ftlvariable name="projectTypeDisplayNameLowercase" type="String" -->

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <@fdsCheckAnswers.checkAnswers>
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Are other organisation allow to contribute to this ${projectTypeDisplayNameLowercase}?"
      diffedField=projectContributorDiffModel.ForwardWorkPlanProjectContributorsView_hasProjectContributors
    />
    <#if showContributorsList>
      <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Contributors">
        <#list projectContributorDiffModel.ForwardWorkPlanProjectContributorsView_organisationGroupNames as organisationName>
          <div>
            <@differenceChanges.renderDifference
              diffedField=organisationName
            />
          </div>
        </#list>
      </@checkAnswers.checkAnswersRowNoActionsWithNested>
    </#if>
  </@fdsCheckAnswers.checkAnswers>
</@sectionSummaryWrapper.sectionSummaryWrapper>