<#include '../../layout.ftl'/>

<#-- @ftlvariable name="sectionId" type="String" -->
<#-- @ftlvariable name="sectionTitle" type="String" -->
<#-- @ftlvariable name="projectContributorDiffModel" type="java.util.Map<String, Object>" -->

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <@fdsCheckAnswers.checkAnswers>
    <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Contributors">
      <#list projectContributorDiffModel.ProjectContributorsView_organisationGroupNames as organisationName>
        <div>
          <@differenceChanges.renderDifference
            diffedField=organisationName
          />
        </div>
      </#list>
    </@checkAnswers.checkAnswersRowNoActionsWithNested>
  </@fdsCheckAnswers.checkAnswers>
</@sectionSummaryWrapper.sectionSummaryWrapper>