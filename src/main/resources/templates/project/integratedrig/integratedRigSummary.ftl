<#include '../../layout.ftl'>

<#macro integratedRigSummary integratedRigView showHeader=true showActions=true>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix="integrated-rig"
    headingPrefix="Integrated rig"
    summaryView=integratedRigView
    showHeader=showHeader
    showActions=showActions
  >
    <@checkAnswers.checkAnswersRowNoActions prompt="Structure" value=integratedRigView.structure!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Name" value=integratedRigView.name!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Integrated rig status" value=integratedRigView.status!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Intention to reactivate" value=integratedRigView.intentionToReactivate!"" />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>
