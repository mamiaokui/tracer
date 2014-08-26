#!/bin/bash

if [ ! -d result ]; then
  mkdir result;
fi;

if [ ! -d result/faked_user ]; then
  mkdir -p result/faked_user;
fi;

if [ ! -d result/faked_user/transactions ]; then
  mkdir -p result/faked_user/transactions;
fi;

rm result/faked_user/thread_name_map
rm result/faked_user/web_view_thread_map
rm result/faked_user/transactions/*

FILES=../Splited/*
for f in $FILES
do 
  cat $f | python ComputeDependencies.py
done
