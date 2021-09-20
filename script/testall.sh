#!/bin/bash

testfiles=`find source/ -name testfile*.txt`

echo $testfiles

for testfile in $testfiles
do
    echo $testfile
    testin=`echo $testfile | sed 's/testfile/input/g'`
    testout=`echo $testfile | sed 's/testfile/output/g'`
    make TestFile=$testfile TestIn=$testin TestOut=$testout
done
