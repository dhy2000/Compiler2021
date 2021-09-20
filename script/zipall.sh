#!/bin/bash

testfiles=`find source/ -name testfile*.txt`

filelist=""

for testfile in $testfiles
do
    # echo $testfile
    testin=`echo $testfile | sed 's/testfile/input/g'`
    testout=`echo $testfile | sed 's/testfile/output/g'`
    filelist=${filelist}" "${testfile}" "${testin}" "${testout}
done

zip -qj testfile.zip ${filelist}
