<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.0/jquery.min.js"></script>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js" ></script>
<#if article??>
    <@themeConfig map="article" value="showFigShare" ; showFigShare>
        <#if !(showFigShare?? && showFigShare.disabled?? && showFigShare.disabled)>
            <@js src="resource/js/vendor/figshare.js" />
        </#if>
    </@themeConfig>
</#if>

<#--<script src="//code.jquery.com/jquery-2.1.4.min.js" ></script>-->

<#--<script>window.jQuery || document.write('<script src="<@siteLink path="resource/js/vendor/jquery-1.9.0.min.js" />""><\/script>')</script>-->
<@js src="resource/js/vendor/moment.js"/>
<@js src="resource/js/navigation.js" />
<@js src="resource/js/content.js" />
<@js src="resource/js/share.js" />
<@js src="resource/js/taxonomy.js" />

<@renderJs />
