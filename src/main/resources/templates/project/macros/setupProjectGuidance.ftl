<#include '../../layout.ftl'>

<#macro minimumRequirementNotMetInset itemRequiredText linkUrl>
  <@fdsInsetText.insetText>
    <p>
      If you no longer need to add any ${itemRequiredText}, answer "No" to the relevant question on the ‘Set up your project’ page
    </p>
    <p>
      <@fdsAction.link linkText="Change your project set up" linkUrl=linkUrl/>
    </p>
  </@fdsInsetText.insetText>
</#macro>