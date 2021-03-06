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

if ! which kotlinc-jvm >/dev/null; then
  if ! which sdk >/dev/null; then
    curl -s https://get.sdkman.io | bash
    source "$HOME/.sdkman/bin/sdkman-init.sh"
  fi
  sdk install kotlin
fi

rm -rf wiki
git clone https://github.com/AnimatedLEDStrip/animatedledstrip.github.io.git wiki

scripts/create-animation-pages.bash

(
  cd wiki || exit 1
  git add animations animations.md
  git config --local user.email "41898282+github-actions[bot]@users.noreply.github.com"
  git config --local user.name "github-actions[bot]"
  git commit --allow-empty -m "Update animation pages"
)

scripts/create-animation-signatures.bash

cp signature-creation/*.{png,gif} wiki/signatures

(
  cd wiki || exit 1
  git add signatures
  git commit --allow-empty -m "Update animation signatures"
)
