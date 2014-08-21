#!/bin/bash
export PATH=$HOME/bin/jdk1.6.0_45/bin:$PATH
export JAVA_HOME=$HOME/bin/jdk1.6.0_45

cd jni
ndk-build
cd ..
ant release

jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore my-release-key.keystore bin/EventLoggingActivity-release-unsigned.apk alias_name
zipalign -v 4 bin/EventLoggingActivity-release-unsigned.apk bin/EventLoggingActivity.apk
