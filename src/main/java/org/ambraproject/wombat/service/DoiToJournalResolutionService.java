/*
 * Copyright (c) 2017 Public Library of Science
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package org.ambraproject.wombat.service;

import org.ambraproject.wombat.config.site.Site;
import org.ambraproject.wombat.service.remote.ArticleSearchQuery;
import org.ambraproject.wombat.service.remote.SolrSearchApi;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DoiToJournalResolutionService {

  @Autowired
  private SolrSearchApi solrSearchApi;

  public String getJournalKeyFromDoi(String doi, Site site) throws IOException {
    ArticleSearchQuery explicitDoiSearchQuery = ArticleSearchQuery.builder()
        .setSimple(false)
        .setRows(1)
        .setQuery("id:\"" + QueryParser.escape(doi) + "\"")
        .build();

    Map<String, ?> results = solrSearchApi.search(explicitDoiSearchQuery, site);
    List<SolrArticleAdapter> solrArticleAdapters = SolrArticleAdapter.unpackSolrQuery(results);
    String journalKey = null;
    if (solrArticleAdapters.size() == 1) {
      journalKey = solrArticleAdapters.get(0).getJournalKey();
    }
    return journalKey;
  }
}


