<#include '../../layout.ftl'/>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <@fdsCheckAnswers.checkAnswers>
    <@checkAnswers.checkAnswersRowNoActions
      prompt="Project operator"
      value=projectOperatorView.organisationGroupName!""
    />
  </@fdsCheckAnswers.checkAnswers>
</@sectionSummaryWrapper.sectionSummaryWrapper>