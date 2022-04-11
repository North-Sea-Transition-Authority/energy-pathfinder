<#include '../../layout.ftl'>

<#macro minimumRequirementNotMetInset itemRequiredText linkUrl>
  <@fdsInsetText.insetText>
    <@minimumRequirementText itemRequiredText=itemRequiredText linkUrl=linkUrl/>
  </@fdsInsetText.insetText>
</#macro>

<#macro minimumRequirementText itemRequiredText linkUrl>
  <p>
    If you no longer need to add any ${itemRequiredText}, answer 'No' to the relevant question in the ‘Set up your project’ section
  </p>
  <p>
    <@fdsAction.link linkText="Change your project set up" linkUrl=linkUrl/>
  </p>
</#macro>