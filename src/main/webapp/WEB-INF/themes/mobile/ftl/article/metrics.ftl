<#include "../baseTemplates/articleSection.ftl" />
<#assign articleDoi = article.doi />
<#assign title = article.title />
<#assign bodyId = 'page-authors' />
<#assign cssFile = 'metrics.css' />
<#assign mainId = "pjax-container" />

<@page_header />
<#include "metricsBody.ftl"/>

<#include "../common/configJs.ftl" />
<@js src="resource/js/util/alm_config.js"/>
<@js src="resource/js/components/table_open.js"/>
<#include "../common/figshareJs.ftl" />
<@js src="resource/js/components/tooltip_hover.js"/>
<@js src="resource/js/vendor/hover-enhanced.js"/>
<@js src="resource/js/highcharts.js"/>
<#include "articleData.ftl" />
<#include "../common/article/metricsJs.ftl" />
<@page_footer />
