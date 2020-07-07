#!/bin/bash

git clone https://github.com/AnimatedLEDStrip/AnimatedLEDStrip.wiki.git wiki

VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout 2> /dev/null)

kotlinc-jvm -script create_docs.kts -cp target/animatedledstrip-core-${VERSION}.jar

cd wiki

git commit -a -m "Update animation documentation"
git push https://maxnz:$GITHUB_TOKEN@github.com/AnimatedLEDStrip/AnimatedLEDStrip.wiki.git test
