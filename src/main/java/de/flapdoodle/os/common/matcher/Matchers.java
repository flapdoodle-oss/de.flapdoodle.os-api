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
package de.flapdoodle.os.common.matcher;

import java.util.regex.Pattern;

public class Matchers {
  public static MatchPattern matchPattern(String pattern) {
    return ImmutableMatchPattern.of(Pattern.compile(pattern));
  }

  public static OsReleaseFileMapEntry osReleaseFileEntry(String key, String valuePattern) {
    return ImmutableOsReleaseFileMapEntry.of(key, Pattern.compile(valuePattern));
  }

  public static LsbReleaseFileMapEntry lsbReleaseFileEntry(String key, String valuePattern) {
    return ImmutableLsbReleaseFileMapEntry.of(key, Pattern.compile(valuePattern));
  }
}
