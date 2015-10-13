package org.ambraproject.wombat.service.remote;

import com.google.common.collect.Multimap;
import org.ambraproject.wombat.model.SearchFilter;
import org.ambraproject.wombat.model.SearchFilterFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for performing faceted search on different fields used for filtering on the search result
 */
public class SearchFilterService {

  @Autowired
  private SolrSearchService solrSearchService;

  @Autowired
  private SearchFilterFactory searchFilterFactory;

  private final String JOURNAL = "journal";

  private final String JOURNAL_FACET_FIELD = "cross_published_journal_name";

  /**
   * Retrieves a map of search filters to be added to the model. The filters displayed will change
   * depending on the query executed, but the number and type of filters is constant.
   *
   * @param query Execute query to determine the search filter results.
   *              Must be set as faceted with the setFacet() method
   * @param urlParams search URL parameters that have been rebuilt from the ArticleSearchQuery object
   * @return HashMap containing all applicable filters
   * @throws IOException
   */
  public Map<?, ?> getSearchFilters(ArticleSearchQuery query, Multimap<String, String> urlParams)
      throws IOException {
    ArticleSearchQuery.Builder queryObj = ArticleSearchQuery.builder()
        .setFacet(JOURNAL_FACET_FIELD)
        .setQuery(query.getQuery().orNull())
        .setSimple(query.isSimple())
        .setArticleTypes(query.getArticleTypes())
        .setSubjects(query.getSubjects())
        .setDateRange(query.getDateRange().orNull());

    Map<?, ?> results = solrSearchService.search(queryObj.build());

    SearchFilter journalFilter = searchFilterFactory.createSearchFilter(results, JOURNAL, urlParams);
    Map<String, SearchFilter> filters = new HashMap<>();
    filters.put(JOURNAL, journalFilter);
    // TODO: add other filters here
    return filters;
  }

  public Map<?, ?> getVolumeSearchFilters(int volume, List<String> journalKeys, List<String> articleTypes,
                                          SolrSearchService.SearchCriterion dateRange) throws IOException {
    Map<String, SearchFilter> filters = new HashMap<>();
    // TODO: add other filters here (filter by journal is not applicable here)
    return filters;
  }
}
