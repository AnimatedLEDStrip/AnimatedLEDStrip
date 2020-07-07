#!/bin/bash

curl -s https://get.sdkman.io | bash

sdk install kotlin

git clone https://github.com/AnimatedLEDStrip/AnimatedLEDStrip.wiki.git wiki

VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout 2> /dev/null)

kotlinc-jvm -script create_docs.kts -cp target/animatedledstrip-core-${VERSION}.jar
