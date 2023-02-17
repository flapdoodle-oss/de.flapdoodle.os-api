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

import static de.flapdoodle.os.OsReleaseFiles.versionMatches;

public enum OSSample implements OS {
	Linux(CommonArchitecture.class, LinuxDistribution.class, osNameMatches("Linux")),
//	Windows(CommonArchitecture.class, WindowsDistribution.class, osNameMatches("Windows.*")),
	OS_X(CommonArchitecture.class, OS_X_Distribution.class, osNameMatches("Mac OS X"))
//	Solaris(CommonArchitecture.class, SolarisDistribution.class, osNameMatches(".*SunOS.*")),
//	FreeBSD(CommonArchitecture.class, FreeBSDDistribution.class, osNameMatches("FreeBSD"))
	;

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
		Ubuntu(UbuntuVersion.class, OsReleaseFiles.osReleaseFileNameMatches("Ubuntu")),
		CentOS(CentosVersion.class, CentosVersion.centosReleaseFileNameMatches("CentOS")),
//		Redhat(RedhatVersion.class, OsReleaseFiles.osReleaseFileNameMatches("Red Hat")),
//		Oracle(OracleVersion.class, OsReleaseFiles.osReleaseFileNameMatches("Oracle")),
//		OpenSUSE(OpenSUSEVersion.class, OsReleaseFiles.osReleaseFileNameMatches("openSUSE")),
//		LinuxMint(LinuxMintVersion.class, OsReleaseFiles.osReleaseFileNameMatches("Linux Mint")),
//		Debian(DebianVersion.class, OsReleaseFiles.osReleaseFileNameMatches("Debian")),
		Amazon(AmazonVersion.class, AmazonVersion.osVersionMatches(".*amzn.*"))
		;

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
		// amzn2
		// os.version=4.9.76-3.78.amzn1.x86_64
		AmazonLinux(osVersionMatches(".*amzn1.*")),
		AmazonLinux2(osVersionMatches(".*amzn2.*"));

		private final List<Peculiarity> peculiarities;

		AmazonVersion(Peculiarity... peculiarities) {
			this.peculiarities  = HasPecularities.asList(peculiarities);
		}

		@Override
		public List<Peculiarity> pecularities() {
			return peculiarities;
		}

		/**
		 * as we rely on 'os.version' only, this detection is pretty weak
		 * it should have a lower priority than others
		 */
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
		CentOS_6(OneOf.of(versionMatches(centosReleaseFile(),"6"), versionMatches(OsReleaseFiles.osReleaseFile(),"6"))),
		CentOS_7(OneOf.of(versionMatches(centosReleaseFile(),"7"), versionMatches(OsReleaseFiles.osReleaseFile(),"7"))),
		CentOS_8(OneOf.of(versionMatches(centosReleaseFile(),"8"), versionMatches(OsReleaseFiles.osReleaseFile(),"8"))),
		CentOS_9(OneOf.of(versionMatches(centosReleaseFile(),"9"), versionMatches(OsReleaseFiles.osReleaseFile(),"9"))),
		;

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
			return OneOf.of(OsReleaseFiles.nameMatches(centosReleaseFile(), name), OsReleaseFiles.nameMatches(OsReleaseFiles.osReleaseFile(), name));
		}

		private static Attribute<OsReleaseFile> centosReleaseFile() {
			return OsReleaseFiles.releaseFile(RELEASE_FILE_NAME);
		}
	}

	public enum UbuntuVersion implements Version {
		Ubuntu_16_04(OsReleaseFiles.osReleaseFileVersionMatches("16.04")),
		Ubuntu_16_10(OsReleaseFiles.osReleaseFileVersionMatches("16.10")),
		Ubuntu_18_04(OsReleaseFiles.osReleaseFileVersionMatches("18.04")),
		Ubuntu_18_10(OsReleaseFiles.osReleaseFileVersionMatches("18.10")),
		Ubuntu_19_04(OsReleaseFiles.osReleaseFileVersionMatches("19.04")),
		Ubuntu_19_10(OsReleaseFiles.osReleaseFileVersionMatches("19.10")),
		Ubuntu_20_04(OsReleaseFiles.osReleaseFileVersionMatches("20.04")),
		Ubuntu_20_10(OsReleaseFiles.osReleaseFileVersionMatches("20.10")),
		Ubuntu_21_04(OsReleaseFiles.osReleaseFileVersionMatches("21.04")),
		Ubuntu_21_10(OsReleaseFiles.osReleaseFileVersionMatches("21.10")),
		Ubuntu_22_04(OsReleaseFiles.osReleaseFileVersionMatches("22.04")),
		Ubuntu_22_10(OsReleaseFiles.osReleaseFileVersionMatches("22.10")),
		;

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
