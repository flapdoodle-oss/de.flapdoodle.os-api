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

import de.flapdoodle.os.VersionWithPriority;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface HasPriority {
	int priority();

	static int priority(Object object) {
		return (object instanceof VersionWithPriority)
			? ((VersionWithPriority) object).priority()
			: 0;
	}

	static <T> List<T> sortedByPriority(List<? extends T> list) {
		return list.stream()
			.sorted((Comparator<T>) (a, b) -> Integer.compare(
				priority(b),
				priority(a)))
			.collect(Collectors.toList());
	}
}
