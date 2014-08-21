#!/bin/bash
export PATH=$HOME/bin/jdk1.6.0_45/bin:$PATH
export JAVA_HOME=$HOME/bin/jdk1.6.0_45

cd jni
ndk-build
cd ..
ant release

