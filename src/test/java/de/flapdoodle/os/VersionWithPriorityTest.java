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
package de.flapdoodle.os;

import de.flapdoodle.os.common.collections.Immutables;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VersionWithPriorityTest {

	@Test
	void sortByPriority() {
		ImmutablePlatform amazon2 = ImmutablePlatform.builder()
			.operatingSystem(OSSample.Linux)
			.architecture(CommonArchitecture.X86_64)
			.distribution(OSSample.LinuxDistribution.Amazon)
			.version(OSSample.AmazonVersion.AmazonLinux2)
			.build();

		ImmutablePlatform centos8 = ImmutablePlatform.builder()
			.operatingSystem(OSSample.Linux)
			.architecture(CommonArchitecture.X86_64)
			.distribution(OSSample.LinuxDistribution.CentOS)
			.version(OSSample.CentosVersion.CentOS_8)
			.build();

		List<ImmutablePlatform> platforms = Immutables.asList(amazon2,centos8);

		List<Platform> sorted = VersionWithPriority.sorteByPriority(platforms);

		assertThat(sorted)
			.containsExactly(centos8,amazon2);
	}
}