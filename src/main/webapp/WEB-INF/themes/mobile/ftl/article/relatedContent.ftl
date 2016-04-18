<#assign cssFile = "related-content.css" />

<#include "../common/htmlTag.ftl" />

<#assign title = article.title />
<#include "../common/head.ftl" />
<body>
<#include "../common/analyticsUpdated.ftl" />

<#include "../common/configJs.ftl" />
<#include "backToArticle.ftl" />
<div id="content" class="related-content">
  <article>
    <#include "relatedContentBody.ftl" />
  </article>
</div>

</div><#-- end home-content -->

<#include "../common/footer/footer.ftl" />

</div><#-- end container-main -->

<#include "../common/siteMenu/siteMenu.ftl" />

<#include "../common/bodyJs.ftl" />

<@js src="resource/js/util/alm_config.js"/>
<@js src="resource/js/util/alm_query.js"/>
<@js src="resource/js/vendor/moment.js"/>
<@js src="resource/js/pages/related_content.js"/>
<@renderJs />

</body>
</html>