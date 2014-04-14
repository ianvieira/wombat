<#--
  This is the common code between the comments list view and the corrections list view.
  Right now, comments and corrections share the same backend representation, but this
  may not always be the case.  If it changes we'll have to revise this.
-->

<section class="comments">
<#list articleComments as comment>
  <div class="comment">

    <div class="context">
      <#assign reqPath = "comment" />
      <#if mode?? && mode = "corrections">
        <#assign reqPath = "correction" />
      </#if>
      <a href="${reqPath}?id=${comment.annotationUri}" class="expand">${comment.title}</a>

      <p class="details">Posted by ${comment.creatorDisplayName}
        on <@formatJsonDate date="${comment.created}" format="dd MMM yyyy 'at' hh:mm a" />
      </p>
    </div>

    <#if comment.totalNumReplies &gt; 0>
      <div class="responses">
        <p class="response-header">
          <a>${comment.totalNumReplies}
            <#if comment.totalNumReplies == 1 >
              RESPONSE
            <#else>
              RESPONSES
            </#if>
          </a>
          | <@formatJsonDate date="${comment.lastReplyDate}" format="dd MMM yyyy 'at' hh:mm a" />
        </p>
      </div>
    </#if>
  </div>
</#list>
</section>

<#include "../common/bottomMenu/bottomMenu.ftl" />
