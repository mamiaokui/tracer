#!/bin/bash
if [ ! -d PreProcessed ]; then
  mkdir PreProcessed;
fi;

rm PreProcessed/*

#Process user events
FILES=$1/user/*

for f in $FILES
do
  ./user-event-parser < $f > ./PreProcessed/user-$(basename $f).json 
done

#Process kernel events
FILES=$1/kernel/*

for f in $FILES
do
  ./kernel-event-decompressor < $f | ./kernel-event-decoder > ./PreProcessed/kernel-$(basename $f).json 
done


