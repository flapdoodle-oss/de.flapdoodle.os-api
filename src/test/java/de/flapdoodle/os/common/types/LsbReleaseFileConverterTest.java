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
package de.flapdoodle.os.common.types;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LsbReleaseFileConverterTest {

	@Test
	public void sampleCentosReleaseFile() {
		String sample="DISTRIB_ID=\"ManjaroLinux\"\n"
			+ "DISTRIB_RELEASE=\"24.0.8\"\n"
			+ "DISTRIB_CODENAME=\"Wynsdey\"\n"
			+ "DISTRIB_DESCRIPTION=\"Manjaro Linux\"\n";
		
		LsbReleaseFile result = LsbReleaseFileConverter.convert(sample);

		assertThat(result.attributes())
			.containsEntry("DISTRIB_ID","ManjaroLinux")
			.containsEntry("DISTRIB_RELEASE","24.0.8")
			.hasSize(4);
	}
}