#! /bin/bash

#
# Copyright (c) 2018-2021 AnimatedLEDStrip
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
#

set -e

ALS_DEPENDENCY="../build/libs/animatedledstrip-core-jvm-$(./gradlew properties | grep '^version:' | sed 's/version: //g').jar"
sed -i "s|@file:DependsOn(\".*/build/libs/animatedledstrip-core-jvm-.*\.jar\")|@file:DependsOn(\"$ALS_DEPENDENCY\")|" scripts/create-animation-pages.main.kts
sed -i "s|@file:DependsOn(\".*/build/libs/animatedledstrip-core-jvm-.*\.jar\")|@file:DependsOn(\"$ALS_DEPENDENCY\")|" scripts/create-animation-signatures.main.kts

KERMIT_DEPENDENCY=$(./gradlew dependencies --configuration jvmCompileClasspath | grep kermit-jvm | sed 's/ \+/ /g' | cut -d ' ' -f 3)
sed -i "s/@file:DependsOn(\"co.touchlab:kermit-jvm:.*\")/@file:DependsOn(\"$KERMIT_DEPENDENCY\")/" scripts/create-animation-pages.main.kts
sed -i "s/@file:DependsOn(\"co.touchlab:kermit-jvm:.*\")/@file:DependsOn(\"$KERMIT_DEPENDENCY\")/" scripts/create-animation-signatures.main.kts

COROUTINES_DEPENDENCY=$(./gradlew dependencies --configuration jvmCompileClasspath | grep kotlinx-coroutines-core-jvm | sed 's/ \+/ /g' | cut -d ' ' -f 3)
sed -i "s/@file:DependsOn(\"org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:.*\")/@file:DependsOn(\"$COROUTINES_DEPENDENCY\")/" scripts/create-animation-signatures.main.kts

SERIALIZATION_DEPENDENCY=$(./gradlew dependencies --configuration jvmCompileClasspath | grep kotlinx-serialization-json-jvm | sed 's/ \+/ /g' | cut -d ' ' -f 3)
sed -i "s/@file:DependsOn(\"org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:.*\")/@file:DependsOn(\"$SERIALIZATION_DEPENDENCY\")/" scripts/create-animation-signatures.main.kts

if ! git diff-index --quiet HEAD
then
  git