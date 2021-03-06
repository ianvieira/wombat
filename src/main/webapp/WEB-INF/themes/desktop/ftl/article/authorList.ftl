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

<ul class="author-list clearfix"  data-js-tooltip="tooltip_container" id="author-list">
<#include "maxAuthorsToShow.ftl" />

<#include "authorItem.ftl" />

<#if authors?size gt maxAuthorsToShow + 1>
<#--
  Put all authors in the range from maxAuthorsToShow-1 to size-1 in the expander.
  I.e., before clicking the expander, the user sees the first maxAuthorsToShow-1 authors and the last author.
  If the expander would contain only one author, just show the author instead.
  -->
  <#list authors as author><#-- Before the expander -->
    <#if author_index lt (maxAuthorsToShow - 1) >
      <@authorItem author author_index author_has_next true false />
    </#if>
  </#list>
      <#list authors as author><#-- Inside the expander -->
        <#if author_index gte (maxAuthorsToShow - 1) && author_index lt (authors?size - 1) >
        <@authorItem author author_index author_has_next true true />
      </#if>
     </#list>


<li data-js-toggle="toggle_add">&nbsp;[ ... ],</li>

  <@authorItem authors[authors?size - 1] authors?size - 1 false false false /><#-- Last one after expander -->
  <li data-js-toggle="toggle_trigger" >
  <#--there was no way to not do this. -->
    <a class="more-authors active" id="authors-show">[ view all ]</a>
    </li>
  <li data-js-toggle="toggle_trigger" data-visibility="none">
    <a class="author-less" id="author-hide">[ view less ]</a>
  </li>
<#else>
<#-- List authors with no expander -->
  <#list authors as author>
    <@authorItem author author_index author_has_next />
  </#list>
</#if>

</ul><#-- end div.author-list -->
<@js src="resource/js/components/tooltip.js" />

