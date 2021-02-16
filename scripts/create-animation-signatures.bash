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

./gradlew jvmJar

VERSION=$(./gradlew properties | grep '^version:' | sed 's/version: //g')
ALS_JAR=$(find "$(pwd)/build/libs" -name "animatedledstrip-core-jvm-${VERSION}.jar")
KERMIT_JAR="$(find ~/.gradle -name "kermit-jvm-*.jar" | tail -n 1)"
COROUTINES_JAR="$(find ~/.gradle -name "kotlinx-coroutines-core-jvm-*.jar" | tail -n 1)"

rm -rf signature-creation
mkdir signature-creation
(
  cd signature-creation || exit
  kotlinc-jvm -script ../scripts/create-animation-signatures.kts -cp "$ALS_JAR:$KERMIT_JAR:$COROUTINES_JAR"
  # shellcheck disable=SC2046
  # shellcheck disable=SC2035
  python3 ../scripts/CSVtoPNG.py $(ls *.csv)
)