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

<#if entriesByType?size gt 0>
    <#list entriesByType as entryType, entries>

[#release-notes-${release.version?replace("[^a-zA-Z0-9]", "-", "r")}-${entryType?lower_case}]
=== ${entryType?capitalize}

        <#list entries as entry>
* ${entry.description.text?replace("\\s+", " ", "r")}<#if entry.issues?has_content> (<#list entry.issues as issue>${issue.link}[#${issue.id}]<#if issue?has_next>, </#if></#list>)</#if>
        </#list>
    </#list>
</#if>
