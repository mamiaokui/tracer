#!/bin/bash
if [ ! -d Splited ]; then
  mkdir Splited;
fi;

rm Splited/*

find ./PreProcessed/ -type f | ./event-merger-splitter -v Splited

FILES=./Splited/*
for f in $FILES
do
  gunzip $f
done

rm PreProcessed -rf
