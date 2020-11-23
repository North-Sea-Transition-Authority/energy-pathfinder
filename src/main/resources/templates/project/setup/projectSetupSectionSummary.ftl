<#include '../../layout.ftl'/>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <@fdsCheckAnswers.checkAnswers >
    <#list answers as answer>
      <@checkAnswers.checkAnswersRowNoActions prompt=answer.prompt value=answer.answerValue!"" />
    </#list>
  </@fdsCheckAnswers.checkAnswers>
</@sectionSummaryWrapper.sectionSummaryWrapper>
