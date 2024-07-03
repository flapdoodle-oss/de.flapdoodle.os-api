/*
 * Copyright (C) 2020
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.os.common;

import de.flapdoodle.os.common.attributes.Attribute;
import de.flapdoodle.os.common.attributes.AttributeExtractor;
import de.flapdoodle.os.common.attributes.AttributeExtractorLookup;
import de.flapdoodle.os.common.matcher.Match;
import de.flapdoodle.os.common.matcher.Matcher;
import de.flapdoodle.os.common.matcher.MatcherLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

// TODO cache extracted attributes
public abstract class PeculiarityInspector {

  private static Logger logger = LoggerFactory.getLogger(PeculiarityInspector.class);

  public static <T extends HasPecularities> T match(
          AttributeExtractorLookup attributeExtractorLookup,
          MatcherLookup matcherLookup,
          T... items
  ) {
    return match(attributeExtractorLookup, matcherLookup, Arrays.asList(items));
  }

  public static <T extends HasPecularities> T match(
          AttributeExtractorLookup attributeExtractorLookup,
          MatcherLookup matcherLookup,
          Iterable<? extends T> items
  ) {
    List<T> matching = matching(attributeExtractorLookup, matcherLookup, items);
    if (matching.isEmpty()) {
      throw new IllegalArgumentException("no match out of " + items);
    }
    if (matching.size() > 1) {
      throw new IllegalArgumentException("more than one match: " + matching);
    }
    return matching.get(0);
  }

  public static <T extends HasPecularities> Optional<T> find(
          AttributeExtractorLookup attributeExtractorLookup,
          MatcherLookup matcherLookup,
          Iterable<? extends T> items
  ) {
    List<T> matching = matching(attributeExtractorLookup, matcherLookup, items);
    if (matching.size()>1) {
			logger.warn("more than one match: {}, using first match", matching);
    }
    return !matching.isEmpty()
            ? Optional.of(matching.get(0))
            : Optional.empty();
  }

  public static <T extends HasPecularities> List<T> matching(
          AttributeExtractorLookup attributeExtractorLookup,
          MatcherLookup matcherLookup,
          T... items
  ) {
    return matching(attributeExtractorLookup, matcherLookup, Arrays.asList(items));
  }

  public static <T extends HasPecularities> List<T> matching(
          AttributeExtractorLookup attributeExtractorLookup,
          MatcherLookup matcherLookup,
          Iterable<? extends T> items
  ) {
    return StreamSupport.stream(items.spliterator(), false)
            .filter(it -> matches(attributeExtractorLookup, matcherLookup, it.pecularities()))
            .collect(Collectors.toList());
  }


  // TODO cache extracted attributes
  public static boolean matches(
          AttributeExtractorLookup attributeExtractorLookup,
          MatcherLookup matcherLookup,
          Iterable<? extends Peculiarity> peculiarities
  ) {
    for (Peculiarity it : peculiarities) {
      return matches(attributeExtractorLookup, matcherLookup, it);
    }
    return true;
  }

  public static boolean matches(
    AttributeExtractorLookup attributeExtractorLookup,
    MatcherLookup matcherLookup,
    Peculiarity peculiarity
  ) {
    if (peculiarity instanceof DistinctPeculiarity) {
      return matches(attributeExtractorLookup, matcherLookup, (DistinctPeculiarity<?>) peculiarity);
    }
    if (peculiarity instanceof OneOf) {
      return matches(attributeExtractorLookup, matcherLookup, (OneOf) peculiarity);
    }
    if (peculiarity instanceof AllOf) {
      return matches(attributeExtractorLookup, matcherLookup, (AllOf) peculiarity);
    }
    throw new IllegalArgumentException("unknown peculiarity: "+peculiarity);
  }

  public static boolean matches(
    AttributeExtractorLookup attributeExtractorLookup,
    MatcherLookup matcherLookup,
    OneOf oneOf) {
    for (Peculiarity it : oneOf.pecularities()) {
      if (matches(attributeExtractorLookup, matcherLookup, it)) {
        return true;
      }
    }
    return false;
  }

  public static boolean matches(
    AttributeExtractorLookup attributeExtractorLookup,
    MatcherLookup matcherLookup,
    AllOf allOf) {
    for (Peculiarity it : allOf.pecularities()) {
      if (!matches(attributeExtractorLookup, matcherLookup, it)) {
        return false;
      }
    }
    return true;
  }

  // TODO cache extracted attributes
  public static <T> boolean matches(
          AttributeExtractorLookup attributeExtractorLookup,
          MatcherLookup matcherLookup,
          DistinctPeculiarity<T> peculiarity
  ) {

    Attribute<T> attribute = peculiarity.attribute();
    Optional<AttributeExtractor<T, Attribute<T>>> extractor = attributeExtractorLookup.extractor(attribute);

    Optional<T> value = extractor.flatMap(it -> it.extract(attribute));

    Match<T> match = peculiarity.match();
    Optional<Matcher<T, Match<T>>> matcher = matcherLookup.matcher(match);

    return matcher
            .map(m -> m.match(value, match))
            .orElse(false);
  }
}
