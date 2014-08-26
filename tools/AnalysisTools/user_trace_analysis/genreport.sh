#!/bin/bash
if [ -f transactions.txt ]; then
  rm transactions.txt;
fi;

python PrintTransactions.py ./result/faked_user/ > transactions.txt
rm ../Splited/*


