#!/bin/bash
#
#
#  Copyright 2006 OCLC, Online Computer Library Center
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#
#  Thi file was modified to be part of LA Referencia software platform LRHarvester v4.x and
#  is redistributed respecting the original licence.
#
#  For any further information please contact
#  Lautaro Matas <lmatas@gmail.com>
#
#

mvn clean package install -DskipTests -Dmaven.javadoc.skip=true

