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
package de.flapdoodle.os.common.attributes;

import de.flapdoodle.os.common.io.IO;

import java.nio.file.Paths;
import java.util.Optional;

public class MappedTextFileResolver<T> implements AttributeExtractor<T, MappedTextFile<T>> {

  @Override
  public Optional<T> extract(MappedTextFile<T> attribute) {
    return IO.readString(Paths.get(attribute.name()), attribute.charset())
            .map(attribute.converter());
  }

  @Override public String toString() {
    return getClass().getSimpleName();
  }
}
