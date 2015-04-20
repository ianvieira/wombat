<#include "../common/htmlTag.ftl" />

<#assign title = '' />
<#assign depth = 0 />
<#include "../common/head.ftl" />

<body>
<div id="container-main">

<#include "../common/header/header.ftl" />

  <div id="content">

  <@fetchHtml type="siteContent" path=siteContentRepoKey/>


  </div><#-- end home-content -->

<#include "../common/footer/footer.ftl" />

</div><#-- end container-main -->

<#include "../common/siteMenu/siteMenu.ftl" />
<#include "../common/bodyJs.ftl" />

</body>
</html>