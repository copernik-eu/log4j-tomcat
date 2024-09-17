////
// tag::license[]
//
// Copyright © $YEAR Piotr P. Karwasz
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// end::license[]
////

////
    ██     ██  █████  ██████  ███    ██ ██ ███    ██  ██████  ██
    ██     ██ ██   ██ ██   ██ ████   ██ ██ ████   ██ ██       ██
    ██  █  ██ ███████ ██████  ██ ██  ██ ██ ██ ██  ██ ██   ███ ██
    ██ ███ ██ ██   ██ ██   ██ ██  ██ ██ ██ ██  ██ ██ ██    ██
     ███ ███  ██   ██ ██   ██ ██   ████ ██ ██   ████  ██████  ██

    IF THIS FILE DOESN'T HAVE A `.ftl` SUFFIX, IT IS AUTO-GENERATED, DO NOT EDIT IT!

    Version-specific release notes (`7.8.0.adoc`, etc.) are generated from `src/changelog/*/.release-notes.adoc.ftl`.
    Auto-generation happens during `generate-sources` phase of Maven.
    Hence, you must always

    1. Find and edit the associated `release-notes.adoc.ftl`
    2. Run `./mvnw -Pchangelog-release`
    3. Commit both `release-notes.adoc.ftl` and the generated `7.8.0.adoc`
////

[#release-notes-${release.version?replace("[^a-zA-Z0-9]", "-", "r")}]
== ${release.version}

<#if release.date?has_content>Release date:: ${release.date}</#if>

This release constitutes a major rework of the initial release.
Since the Tomcat Log4j Core lookup and Tomcat Classloader need to be in different classloaders, the `log4j-tomcat` is split in two parts:

* the
https://oss.copernik.eu/tomcat/3.x/components/log4j-tomcat#TomcatLookup[Tomcat Lookup]
is left in the `log4j-tomcat` artifact.
* the
https://oss.copernik.eu/tomcat/3.x/components/tomcat-log4j#classloaders[Tomcat classloaders]
are moved to a new `tomcat-log4j` artifact.

=== New components

The set of tools to integrate Log4j and Tomcat is expanded to include:

* a new
https://oss.copernik.eu/tomcat/3.x/components/tomcat-juli-to-log4j[Tomcat JULI to Log4j API bridge].
* two
https://oss.copernik.eu/tomcat/3.x/components/log4j-tomcat#TomcatContextSelector[Tomcat context selectors].
* a
https://oss.copernik.eu/tomcat/3.x/components/log4j-tomcat#TomcatContextDataProvider[Tomcat context data provider].

<#include "../changelog.adoc.ftl">