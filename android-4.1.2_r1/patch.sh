#!/bin/bash
DIR=$(pwd)

# Patch Kernel
cd ../../kernel
git checkout origin/android-omap-tuna-3.0-jb-mr2
git checkout 6a5fe817ac903697a9e996af18bc74f0d8e82ce9
git apply ../tracer/android-4.1.2_r1/patches/kernel/kernel.diff
cd $DIR

# Patch Android
cd ../../google-4.1.2_r1
#  apply the firmware patch
wget https://dl.google.com/dl/android/aosp/broadcom-maguro-jzo54k-8b0d7637.tgz
tar xzf broadcom-maguro-jzo54k-8b0d7637.tgz
./extract-broadcom-maguro.sh
rm broadcom-maguro-jzo54k-8b0d7637.tgz
rm extract-broadcom-maguro.sh

wget https://dl.google.com/dl/android/aosp/imgtec-maguro-jzo54k-0911a9b5.tgz
tar xzf imgtec-maguro-jzo54k-0911a9b5.tgz
./extract-imgtec-maguro.sh
rm imgtec-maguro-jzo54k-0911a9b5.tgz
rm extract-imgtec-maguro.sh

wget https://dl.google.com/dl/android/aosp/invensense-maguro-jzo54k-ff2586d3.tgz
tar xzf invensense-maguro-jzo54k-ff2586d3.tgz
./extract-invensense-maguro.sh
rm invensense-maguro-jzo54k-ff2586d3.tgz
rm extract-invensense-maguro.sh

wget https://dl.google.com/dl/android/aosp/nxp-maguro-jzo54k-95659b1e.tgz
tar xzf nxp-maguro-jzo54k-95659b1e.tgz
./extract-nxp-maguro.sh
rm nxp-maguro-jzo54k-95659b1e.tgz
rm extract-nxp-maguro.sh

wget https://dl.google.com/dl/android/aosp/samsung-maguro-jzo54k-b8a098be.tgz
tar xzf samsung-maguro-jzo54k-b8a098be.tgz
./extract-samsung-maguro.sh
rm samsung-maguro-jzo54k-b8a098be.tgz
rm extract-samsung-maguro.sh
#  end of firmware

#  apply the eventlog patch
patch -p1 < ../tracer/android-4.1.2_r1/patches/android/framework-base.diff
patch -p1 < ../tracer/android-4.1.2_r1/patches/android/libcore.diff
#  end 
cd $DIR
