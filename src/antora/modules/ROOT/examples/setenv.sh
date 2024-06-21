#!/bin/sh
# tag::license[]
#
# Copyright (C) 2024 Piotr P. Karwasz
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# end::license[]
##
# Add the additional libraries
CLASSPATH="$CLASSPATH:$CATALINA_BASE/bin/logging/*"
##
# Add a folder for the logging configuration
CLASSPATH="$CLASSPATH:$CATALINA_BASE/conf/logging/"
