<#include '../layout.ftl'>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem>" -->
<#-- @ftlvariable name="service" type="uk.co.ogauthority.pathfinder.config.ServiceProperties" -->
<#-- @ftlvariable name="serviceRatings" type="java.util.Map<String, String>" -->

<#assign pageTitle = "Give feedback on ${service.serviceName}"/>

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  topNavigation=false
  backLink=false
  phaseBanner=false
  errorItems=errorList
>
  <@fdsForm.htmlForm>
    <@fdsRadio.radio
      path="form.serviceRating"
      labelText="Overall, how did you feel about using this service?"
      radioItems=serviceRatings
    />
    <@fdsTextarea.textarea
      path="form.feedback"
      labelText="How could we improve this service?"
      hintText="Do not include any personal or financial information, for example your National Insurance or credit card numbers"
      optionalLabel=true
      maxCharacterLength="2000"
      characterCount=true
      rows="10"
    />
    <input type="hidden" name="form.epsciPath" id="epsciPath" />
    <@fdsAction.button buttonText="Send feedback"/>
    <script type="text/javascript">
      const urlParams = new URLSearchParams(window.location.search);
      const epsciPath = urlParams.get('epsciPath');
      if(epsciPath){
        document.querySelector('#epsciPath').value = epsciPath;
      }
    </script>
  </@fdsForm.htmlForm>
</@defaultPage>