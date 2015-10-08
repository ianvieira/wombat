/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2013 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.wombat.service.remote;

import org.ambraproject.wombat.config.site.Site;
import org.ambraproject.wombat.config.site.SiteSet;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * Interface to the article search service for the application.
 */
public interface SearchService {

  /**
   * Type representing some restriction on the desired search results--for instance, a date range, or a sort order.
   * Implementations of SearchService should also provide appropriate implementations of this interface.
   */
  public interface SearchCriterion {

    /**
     * @return description of this criterion, suitable for exposing in the UI
     */
    public String getDescription();

    /**
     * @return implementation-dependent String value specifying this criterion
     */
    public String getValue();
  }

  /**
   * Performs a search and returns the results.
   *
   * @return deserialized JSON returned by the search server
   * @throws IOException
   */
  public Map<?, ?> search(SearchQuery searchQuery) throws IOException;


  /**
   * Attempts to retrieve information about an article based on the DOI.
   *
   * @param doi identifies the article
   * @return information about the article, if it exists; otherwise an empty result set
   * @throws IOException
   */
  public Map<?, ?> lookupArticleByDoi(String doi) throws IOException;

  /**
   * Attempts to retrieve information about an article based on the journal key and eLocationId.
   *
   * @param eLocationId identifies the article within a journal
   * @param journalKey  the journal in which to search
   * @return information about the article, if it exists; otherwise an empty result set
   * @throws IOException
   */
  public Map<?, ?> lookupArticleByELocationId(String eLocationId, String journalKey) throws IOException;

  /**
   * Adds a new property, link, to each search result passed in.  The value of this property is the correct URL to the
   * article on this environment.  Calling this method is necessary since article URLs need to be specific to the site
   * of the journal the article is published in, not the site in which the search results are being viewed.
   *
   * @param searchResults deserialized search results JSON
   * @param request       current request
   * @param site          site of the current request (for the search results)
   * @param siteSet       site set of the current request
   * @return searchResults decorated with the new property
   * @throws IOException
   */
  public Map<?, ?> addArticleLinks(Map<?, ?> searchResults, HttpServletRequest request, Site site, SiteSet
      siteSet) throws IOException;

  /**
   * Retrieves Solr stats for a given field in a given journal
   *
   * @param fieldName  specifies the name of the field
   * @param journalKey specifies the name of the journal
   * @return Solr stats for the given field
   * @throws IOException
   */
  public Map<?, ?> getStats(String fieldName, String journalKey) throws IOException;
}
