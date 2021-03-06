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

rm -rf signature-creation
mkdir signature-creation

(
  cd signature-creation || exit 1
  ../scripts/create-animation-signatures.main.kts
  # shellcheck disable=SC2035
  if ls *-1D.csv 1>/dev/null 2>&1; then
    # shellcheck disable=SC2046
    # shellcheck disable=SC2035
    ../scripts/CSVtoPNG.py $(ls *-1D.csv)
  fi
  # shellcheck disable=SC2035
  if ls *-2D.csv 1>/dev/null 2>&1; then
    # shellcheck disable=SC2046
    # shellcheck disable=SC2035
    ../scripts/CSVtoGIF.py --width 100 --height 100 --led-locations locations.csv $(ls *-2D.csv)
  fi
)
