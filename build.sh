#!/bin/bash

mkdir -p build
javac -d build -encoding 'utf-8' $(find src -name '*.java')
cd build
mkdir -p META-INF; echo -e 'Manifest-Version: 1.0\r\nMain-Class: Compiler\r\n\r\n' > META-INF/MANIFEST.MF
jar cfm ../compiler.jar META-INF/MANIFEST.MF *
cd -
rm -r build
