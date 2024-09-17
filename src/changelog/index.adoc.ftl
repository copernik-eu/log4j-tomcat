////
// tag::license[]
//
// Copyright © 2024 Piotr P. Karwasz
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
// Original version from Apache Log4j project
////

////
██     ██  █████  ██████  ███    ██ ██ ███    ██  ██████  ██
██     ██ ██   ██ ██   ██ ████   ██ ██ ████   ██ ██       ██
██  █  ██ ███████ ██████  ██ ██  ██ ██ ██ ██  ██ ██   ███ ██
██ ███ ██ ██   ██ ██   ██ ██  ██ ██ ██ ██  ██ ██ ██    ██
███ ███  ██   ██ ██   ██ ██   ████ ██ ██   ████  ██████  ██

IF THIS FILE IS CALLED `index.adoc`, IT IS AUTO-GENERATED, DO NOT EDIT IT!

Release notes `index.adoc` is generated from `src/changelog/.index.adoc.ftl`.
Auto-generation happens during `generate-sources` phase of Maven.
Hence, you must always

1. Edit `index.adoc.ftl`
2. Run `./mvnw -Pchangelog-release`
3. Commit both `index.adoc.ftl` and the generated `index.adoc`
////

// Release notes index does not look nice with a deep sectioning, override it:
:page-toclevels: 1

[#release-notes]
= Release notes
<#list releases as release><#if release.changelogEntryCount gt 0>

include::partial$release-notes/${release.version}.adoc[]
</#if></#list>
