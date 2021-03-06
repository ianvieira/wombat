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

package org.ambraproject.wombat.util;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility methods for building article citations.
 * <p/>
 * Overlaps with some FreeMarker code. It would be good to refactor out duplication.
 */
public class Citations {
  private Citations() {
    throw new AssertionError("Not instantiable");
  }

  private static final Splitter WHITESPACE_SPLITTER = Splitter.on(CharMatcher.whitespace());
  private static final Splitter DASH_SPLITTER = Splitter.on(Pattern.compile("\\p{Pd}")).omitEmptyStrings();

  public static String abbreviateAuthorGivenNames(String givenNames) {
    Iterable<String> givenNameTokens = WHITESPACE_SPLITTER.split(givenNames);
    StringBuilder abbreviation = new StringBuilder();
    for (String givenName : givenNameTokens) {
      Iterable<String> nameParts = DASH_SPLITTER.split(givenName);
      for (String namePart : nameParts) {
        char initial = namePart.charAt(0);
        abbreviation.append(initial);
      }
    }

    return abbreviation.toString();
  }

  private static String getAbbreviatedName(Map<String, ?> author) {
    String surnames = (String) author.get("surnames");
    String givenNames = (String) author.get("givenNames");
    if (!Strings.isNullOrEmpty(surnames) && !Strings.isNullOrEmpty(givenNames)) {
      givenNames = abbreviateAuthorGivenNames(givenNames);
    }

    String suffix = (String) author.get("suffix");
    if (!Strings.isNullOrEmpty(suffix)) {
      suffix = suffix.replace(".", "");
    }

    return Stream.of(surnames, givenNames, suffix)
        .filter(s -> !Strings.isNullOrEmpty(s))
        .collect(Collectors.joining(" "));
  }


  private static final int MAX_AUTHORS = 5;

  private static final Joiner COMMA_JOINER = Joiner.on(", ");

  /*
   * TODO: Deduplicate src/main/webapp/WEB-INF/themes/desktop/ftl/article/citation.ftl
   */
  public static String buildCitation(Map<String, ?> articleMetadata, List<Map<String, ?>> authors) {
    StringBuilder citation = new StringBuilder();

    List<String> authorNames = Lists.newArrayListWithCapacity(MAX_AUTHORS);
    for (Map<String, ?> author : authors) {
      if (authorNames.size() >= MAX_AUTHORS) break;
      authorNames.add(getAbbreviatedName(author));
    }

    COMMA_JOINER.appendTo(citation, authorNames);
    if (authors.size() > MAX_AUTHORS) {
      citation.append(", et al.");
    }

    int year = LocalDate.parse((String) articleMetadata.get("publicationDate")).getYear();
    citation.append(" (").append(year).append(')');

    String title = (String) articleMetadata.get("title");
    title = TextUtil.removeMarkup(title);
    citation.append(' ').append(title).append('.');

    String journal = (String) ((Map<String, ?>) articleMetadata.get("journal")).get("title");
    String volume = (String) articleMetadata.get("volume");
    String issue = (String) articleMetadata.get("issue");
    String eLocationId = (String) articleMetadata.get("eLocationId");
    String pubInfo = String.format("%s %s(%s): %s.", journal, volume, issue, eLocationId);
    citation.append(' ').append(pubInfo);

    String doi = (String) articleMetadata.get("doi");
    citation.append(" https://doi.org/").append(doi);

    return citation.toString();
  }

}
