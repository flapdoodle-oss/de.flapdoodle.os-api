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

import de.flapdoodle.os.common.DistinctPeculiarity;
import de.flapdoodle.os.common.HasPecularities;
import de.flapdoodle.os.common.OneOf;
import de.flapdoodle.os.common.Peculiarity;
import de.flapdoodle.os.common.attributes.Attribute;
import de.flapdoodle.os.common.attributes.Attributes;
import de.flapdoodle.os.common.collections.Enums;
import de.flapdoodle.os.common.matcher.Matchers;
import de.flapdoodle.os.common.types.OsReleaseFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.flapdoodle.os.OsReleaseFiles.*;

public enum OSSample implements OS {
	Linux(CommonArchitecture.class, LinuxDistribution.class, osNameMatches("Linux")),
	OS_X(CommonArchitecture.class, OS_X_Distribution.class, osNameMatches("Mac OS X"));

	private final List<Peculiarity> peculiarities;
	private final List<? extends Distribution> distributions;
	private final List<? extends Architecture> architectures;

	<A extends Enum<A> & Architecture, T extends Enum<T> & Distribution> OSSample(
		Class<A> architecureClazz,
		Class<T> clazz,
		DistinctPeculiarity<?>... peculiarities
	) {
		this.peculiarities  = HasPecularities.asList(peculiarities);
		this.architectures = Enums.valuesAsList(architecureClazz);
		this.distributions = Enums.valuesAsList(clazz);
	}

	public List<? extends Distribution> distributions() {
		return distributions;
	}

	public List<? extends Architecture> architectures() {
		return  architectures;
	}

	@Override
	public List<Peculiarity> pecularities() {
		return peculiarities;
	}

	private static DistinctPeculiarity<String> osNameMatches(String pattern) {
		return DistinctPeculiarity.of(Attributes.systemProperty("os.name"), Matchers.matchPattern(pattern));
	}

	public static List<? extends OS> all() {
		return Arrays.asList(OSSample.values());
	}

	public enum OS_X_Distribution implements Distribution {
		;

		@Override
		public List<Peculiarity> pecularities() {
			return HasPecularities.empty();
		}

		@Override
		public List<Version> versions() {
			return Collections.emptyList();
		}
	}

	public enum LinuxDistribution implements Distribution {
		Amazon(AmazonVersion.class, AmazonVersion.osVersionMatches(".*amzn.*")),
		CentOS(CentosVersion.class, CentosVersion.centosReleaseFileNameMatches("CentOS")),
		Ubuntu(UbuntuVersion.class, OsReleaseFiles.osReleaseFileNameMatches("Ubuntu"));

		private final List<Peculiarity> peculiarities;
		private final List<? extends Version> versions;

		<T extends Enum<T> & Version> LinuxDistribution(Class<T> versionClazz, Peculiarity... peculiarities) {
			this.peculiarities = HasPecularities.asList(peculiarities);
			this.versions = Enums.valuesAsList(versionClazz);
		}
		@Override
		public List<Peculiarity> pecularities() {
			return peculiarities;
		}

		@Override
		public List<? extends Version> versions() {
			return this.versions;
		}

	}

	public enum AmazonVersion implements VersionWithPriority {
		AmazonLinux2(osVersionMatches(".*amzn2.*"));

		private final List<Peculiarity> peculiarities;

		AmazonVersion(Peculiarity... peculiarities) {
			this.peculiarities  = HasPecularities.asList(peculiarities);
		}

		@Override
		public List<Peculiarity> pecularities() {
			return peculiarities;
		}

		@Override
		public int priority() {
			return -1;
		}

		static DistinctPeculiarity<String> osVersionMatches(String name) {
			return DistinctPeculiarity.of(osVersion(), Matchers.matchPattern(name));
		}

		static Attribute<String> osVersion() {
			return Attributes.systemProperty("os.version");
		}
	}

	public enum CentosVersion implements Version {
		CentOS_7(OneOf.of(versionMatches(centosReleaseFile(),"7"), versionMatches(OsReleaseFiles.osReleaseFile(),"7")));

		public static final String RELEASE_FILE_NAME="/etc/centos-release";

		private final List<Peculiarity> peculiarities;

		CentosVersion(Peculiarity... peculiarities) {
			this.peculiarities  = HasPecularities.asList(peculiarities);
		}

		@Override
		public List<Peculiarity> pecularities() {
			return peculiarities;
		}

		static Peculiarity centosReleaseFileNameMatches(String name) {
			return OneOf.of(nameMatches(centosReleaseFile(), name), nameMatches(OsReleaseFiles.osReleaseFile(), name));
		}

		private static Attribute<OsReleaseFile> centosReleaseFile() {
			return OsReleaseFiles.releaseFile(RELEASE_FILE_NAME);
		}
	}

	public enum UbuntuVersion implements Version {
		Ubuntu_18_10(osReleaseFileVersionMatches("18.10"));

		private final List<Peculiarity> peculiarities;

		UbuntuVersion(DistinctPeculiarity... peculiarities) {
			this.peculiarities  = HasPecularities.asList(peculiarities);
		}

		@Override
		public List<Peculiarity> pecularities() {
			return peculiarities;
		}

	}

}
