<#--
  ~ Copyright (c) 2017 Public Library of Science
  ~
  ~ Permission is hereby granted, free of charge, to any person obtaining a
  ~ copy of this software and associated documentation files (the "Software"),
  ~ to deal in the Software without restriction, including without limitation
  ~ the rights to use, copy, modify, merge, publish, distribute, sublicense,
  ~ and/or sell copies of the Software, and to permit persons to whom the
  ~ Software is furnished to do so, subject to the following conditions:
  ~
  ~ The above copyright notice and this permission notice shall be included in
  ~ all copies or substantial portions of the Software.
  ~
  ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
  ~ THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
  ~ FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
  ~ DEALINGS IN THE SOFTWARE.
  -->


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
      <li id="security-check-row">
        <label id="security-check-label"><strong>Security Check:</strong></label>

        <p>This question is to determine if you are a human visitor in order to prevent automated spam
          submissions.</p>
      ${captchaHtml}
      <@formValidation "captchaError">Verification is incorrect. Please try again.</@formValidation>
      </li>
    </ol>
    <input id="submit-feedback-btn" type="submit" value="Submit Feedback" class="btn rounded"/>
  </fieldset>
</form>