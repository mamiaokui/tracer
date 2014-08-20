#!/bin/bash
DIR=$(pwd)

# Build Kernel
cd ../../kernel
export PATH=../google-4.1.2_r1/prebuilts/gcc/linux-x86/arm/arm-eabi-4.6/bin:$PATH
export ARCH=arm
export SUBARCH=arm
export CROSS_COMPILE=arm-eabi-
make tuna_eventlogging_defconfig
make
cp arch/arm/boot/zImage $DIR/../../google-4.1.2_r1/device/samsung/tuna/kernel
cd $DIR

# Build Android
cd ../../google-4.1.2_r1
export PATH=$HOME/bin/jdk1.6.0_45/bin:$PATH
export JAVA_HOME=$HOME/bin/jdk1.6.0_45
source build/envsetup.sh
lunch full_maguro-userdebug 
make -j5
make -j5 otapackage 
cd $DIR 
