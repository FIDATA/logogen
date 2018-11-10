#!/usr/bin/env groovy
/*
 * org.fidata.logogen Gradle plugin
 * Copyright © 2015, 2018  Basil Peace
 *
 * This file is part of Logo Generator.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
buildscript { 
	repositories { mavenCentral() }
	dependencies { classpath 'commons-io:commons-io:+' }
}

plugins {
	id 'com.jfrog.artifactory' version '3.1.0'
	id 'com.github.jlouns.cpe' version '0.3.0'
}

apply plugin: 'distribution'
apply plugin: 'maven-publish'

import groovy.io.FileType
import org.apache.commons.io.FilenameUtils

ext.imconv = "$System.env.IMCONV"

file("$includePath/generators").eachFileMatch(FileType.FILES, { FilenameUtils.getExtension(it) == 'gradle' }) { file ->
	ext.includePath = file.parent
	apply from: file
}

distributions {
	// main { contents {
	// 	from "$buildDir/main"
	// } }
	windows_mainicon { contents {
		from "$buildDir/windows_mainicon"
	} }
	freedesktop { contents {
		from "$buildDir/freedesktop"
	} }
	android { contents {
		from "$buildDir/android"
	} }
	google_play { contents {
		from "$buildDir/google_play"
	} }
	osx { contents {
		from "$buildDir/osx"
	} }
	ios { contents {
		from "$buildDir/ios"
	} }
	ios6 { contents {
		from "$buildDir/ios6"
	} }
	apple_store { contents {
		from "$buildDir/apple_store"
	} }
	favicon { contents {
		from "$buildDir/favicon"
	} }
	webclips { contents {
		from "$buildDir/webclips"
	} }
	// src { contents {
		// from("src/main")
		// from("LICENSE.txt")
	// } }
}

// mainDistZip.dependsOn build
// windows_mainiconDistZip.dependsOn windows_mainicon
/*freedesktopDistZip.dependsOn freedesktop
androidDistZip.dependsOn android
// google_playDistZip.dependsOn google_play
osxDistZip.dependsOn osx
// iosDistZip.dependsOn ios
// ios6DistZip.dependsOn ios6
// apple_storeDistZip.dependsOn apple_store
faviconDistZip.dependsOn favicon
webclipsDistZip.dependsOn webclips*/

artifactoryPublish {
	/*dependsOn distZip, windows_mainiconDistZip, freedesktopDistZip,
		androidDistZip, google_playDistZip, osxDistZip, iosDistZip,
		ios6DistZip, apple_storeDistZip, faviconDistZip,
		webclipsDistZip*/
}

task all {
	dependsOn build
	// dependsOn artifactoryPublish
}

// Settings

publishing {
	publications {
		// artifactPublication(MavenPublication) {
			// artifact(file("$buildDir/distributions/${project.name}-${project.version}.zip"))
			// artifact(file("$buildDir/distributions/${project.name}-src-${project.version}.zip")) {
				// classifier 'src'
			// }
			// artifact(file("$buildDir/distributions/${project.name}-windows_mainicon-${project.version}.zip")) {
				// classifier 'windows_mainicon'
			// }
			// artifact(file("$buildDir/distributions/${project.name}-freedesktop-${project.version}.zip")) {
				// classifier 'freedesktop'
			// }
			// artifact(file("$buildDir/distributions/${project.name}-android-${project.version}.zip")) {
				// classifier 'android'
			// }
			// artifact(file("$buildDir/distributions/${project.name}-google_play-${project.version}.zip")) {
				// classifier 'google_play'
			// }
			// artifact(file("$buildDir/distributions/${project.name}-osx-${project.version}.zip")) {
				// classifier 'osx'
			// }
			// artifact(file("$buildDir/distributions/${project.name}-ios-${project.version}.zip")) {
				// classifier 'ios'
			// }
			// artifact(file("$buildDir/distributions/${project.name}-ios6-${project.version}.zip")) {
				// classifier 'ios6'
			// }
			// artifact(file("$buildDir/distributions/${project.name}-apple_store-${project.version}.zip")) {
				// classifier 'apple_store'
			// }
			// artifact(file("$buildDir/distributions/${project.name}-favicon-${project.version}.zip")) {
				// classifier 'favicon'
			// }
			// artifact(file("$buildDir/distributions/${project.name}-webclips-${project.version}.zip")) {
				// classifier 'webclips'
			// }
		// }
	}
}

task textToPath(type: Exec) {
	// commandLine 'inkscape', file('src/main/svg/logo.editable.svg'), "--select=$objectID", '--verb=ObjectToPath', '--export-plain-svg=' + (file('src/main/svg/logo1.svg')).toString
	commandLine 'inkscape', file('src/main/svg/logo.editable.svg'), '--export-plain-svg=' + (file('src/main/svg/logo1.svg')), '--export-text-to-path'
}

