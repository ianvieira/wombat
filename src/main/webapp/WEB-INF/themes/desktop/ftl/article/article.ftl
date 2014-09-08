<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      lang="en" xml:lang="en"
      itemscope itemtype="http://schema.org/Article"
      class="no-js">

<#assign title = article.title />
<#assign depth = 0 />

<#include "../common/head.ftl" />

<#include "../common/journalStyle.ftl" />
  <body class="article ${journalStyle}">

  <input type="hidden" id="rawPubDate" value="${article.date}" />

  <#include "../common/header/header.ftl" />
  <div class="set-grid">
    <section class="article-body">
      <div class="classifications">
        <p class="license-short" id="licenseShort">Open Access</p>

        <#if article.articleType=="Research Article">
        <p class="peer-reviewed" id="peerReviewed">Peer-reviewed</p>
        </#if>

        <div class="article-type" id="artType">${article.articleType!""}</div>
      </div>
      <#include "articleTitle.ftl" />

      <ul class="date-doi">
        <li id="artPubDate">Published:  </li>
        <li id="artDoi">DOI: ${article.doi} </li>
      </ul>

      <#include "tabs.ftl" />

      <div class="article-text" id="artText">
         ${articleText}
      </div>


    </section>
    <aside class="article-column">

    </aside>
  </div>
  <#include "../common/footer/footer.ftl" />

  <@renderJs />
  <script src="<@siteLink path="resource/js/components/dateparse.js"/>"></script>
  <script src="<@siteLink path="resource/js/pages/article.js"/>"></script>

  </body>
</html>
