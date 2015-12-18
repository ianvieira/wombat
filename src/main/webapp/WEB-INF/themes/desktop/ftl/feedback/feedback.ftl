<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      lang="en" xml:lang="en"
      itemscope itemtype="http://schema.org/Article"
      class="no-js">
<#assign title = "" /> <#-- use default -->
<#assign cssFile = 'site-content.css'/>
<#include "../common/head.ftl" />

<#include "../common/journalStyle.ftl" />
<body class="${journalStyle}">
<#include "../common/header/headerContainer.ftl" />

<article class="ambra-form">
  <h1>Feedback</h1>

<#--
  Simple form input field with label. The nested element is the label.

  The key is both the form parameter name, and the name for prior input (if we are displaying validation errors) in the
  FreeMarker model. It is assumed that the same key will be used for both values.
  -->
<#macro formInput key>
  <#assign formInputId = "feedbackCreate_${key}" />
  <label for="${formInputId}">
    <#nested/>
  </label>
  <input id="${formInputId}" type="text" name="${key}" value="${key?eval!''}"/>
</#macro>

<#--
  Display the nested validation error message if the given key is present in the model.

  The errorKey must be a constant string, which refers to a FreeMarker variable name. I apologize for the sketchy use
  of eval. If the value is absent, the FreeMarker engine will throw an error when the macro is invoked, making it
  impossible to do something simpler like
  <#macro formValidation error>
    <#if error??> ...message... </#if>
  </#macro>
  -->
<#macro formValidation errorKey>
  <#if errorKey?eval??>
    <span class="error">
      <#nested/>
    </span>
  </#if>
</#macro>

  <form class="form-default" id="feedbackCreate" name="feedbackForm" method="post" title="Feedback"
        action="<@siteLink handlerName="feedbackPost" />">
    <fieldset>
    <#include "preamble.ftl" />
      <input type="text" name="userId" style="visibility: hidden" value="<#--TODO: Add userId if present-->"/>
      <ol>
        <li>
        <@formInput "name">Name:</@formInput>
        <@formValidation "nameError">This field is required.</@formValidation>
        </li>
        <li>
        <@formInput "fromEmailAddress">E-mail Address:</@formInput>
        <@formValidation "emailAddressMissingError">This field is required.</@formValidation>
        <@formValidation "emailAddressInvalidError">Invalid e-mail address</@formValidation>
        </li>
        <li>
        <@formInput "subject">Subject:</@formInput>
        <@formValidation "subjectError">This field is required.</@formValidation>
        </li>
        <li>
          <label for="feedbackCreate_note">Message:</label>
          <textarea id="feedbackCreate_note" name="note" cols="70" rows="5">${note!''}</textarea>
        <@formValidation "noteError">This field is required.</@formValidation>
        </li>
        <li>
          <label><strong>Security Check:</strong></label>

          <p>This question is to determine if you are a human visitor in order to prevent automated spam
            submissions.</p>
        ${captchaHtml}
        <@formValidation "captchaError">Verification is incorrect. Please try again.</@formValidation>
        </li>
      </ol>
      <input type="submit" value="Submit Feedback" class="btn"/>
    </fieldset>
  </form>

</article>

<#include "../common/footer/footer.ftl" />
<@renderJs />
</body>
</html>